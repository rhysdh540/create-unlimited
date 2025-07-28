import io.github.prcraftmc.classdiff.ClassDiffer
import io.github.prcraftmc.classdiff.format.DiffWriter
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.AnnotationNode
import java.io.ByteArrayOutputStream
import java.util.jar.JarFile
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import kotlin.math.min

abstract class MergedJar : Jar() {

	@get:InputFile
	abstract val mainJar: RegularFileProperty

	@get:Internal
	abstract val platformJars: MapProperty<String, RegularFile>

	init {
		inputs.files(platformJars.map { it.values })
		inputs.property("platforms", platformJars.keySet())
	}

	@get:Inject
	abstract val archiveOps: ArchiveOperations

	@get:Inject
	abstract val fsOps: FileSystemOperations

	fun add(platform: String, jar: RegularFileProperty) {
		platformJars.put(platform, jar)
	}

	fun add(platform: String, jar: Jar) {
		platformJars.put(platform, jar.archiveFile)
	}

	fun add(platform: String, jar: TaskProvider<out Jar>) {
		platformJars.put(platform, jar.flatMap { it.archiveFile })
	}

	@TaskAction
	fun mergeJars() {
		val output = mutableMapOf<String, ByteArray>()

		JarFile(mainJar.get().asFile).use { jarFile ->
			jarFile.entries().asSequence().forEach { entry ->
				if (!entry.isDirectory) {
					output[entry.name] = jarFile.getInputStream(entry).readBytes()
				}
			}
		}

		// map of platform to (map of file name to changed file)
		val diff = mutableMapOf<String, MutableMap<String, ByteArray>>()
		platformJars.get().forEach { (platform, jarFile) ->
			JarFile(jarFile.asFile).use { jar ->
				jar.entries().asSequence().forEach a@{ entry ->
					if (entry.isDirectory) return@a

					val bytes = jar.getInputStream(entry).use { it.readBytes() }
					if (output[entry.name] == null) {
						output[entry.name] = bytes;
						return@a
					}

					if (output[entry.name]!!.contentEquals(bytes)) return@a

					if (entry.name.endsWith(".class")) {
						println("  Found changed class: ${entry.name} from platform $platform")

						val original = output[entry.name]!!
						val cleanedBytes = shrinkPatchForDirty(original, bytes)
						if (original.contentEquals(cleanedBytes)) {
							println("  cleaning fixed it")
							return@a
						}

						diff.computeIfAbsent(platform) { mutableMapOf() }[entry.name] = cleanedBytes
					} else if (entry.name == "META-INF/MANIFEST.MF") {
						String(bytes).lines().filter { it.isNotEmpty() }.forEach {
							val (key, value) = it.split(':', limit = 2).map { it.trim() }
							if (key != "Manifest-Version") {
								this.manifest.attributes[key] = value
							}
						}
					} else {
						throw IllegalArgumentException("Conflict on resource '${entry.name}' between main jar and platform '$platform'")
					}
				}
			}
		}

		output["META-INF/patches.zip"] = generatePatches(output, diff)

		val gradleBruh = temporaryDir.resolve("merged.jar")
		ZipOutputStream(gradleBruh.outputStream()).use {
			output.forEach { (name, bytes) ->
				it.putNextEntry(ZipEntry(name))
				it.write(bytes)
				it.closeEntry()
			}

			it.finish()
			it.flush()
		}

		from(archiveOps.zipTree(gradleBruh))
		copy()
	}

	private fun shrinkPatchForDirty(cleanBytes: ByteArray, dirtyBytes: ByteArray): ByteArray {
		// in an ideal world, just reorder the constant pool
		//return ClassWriter(ClassReader(cleanBytes), 0).also {
		//    ClassReader(dirtyBytes).accept(it, 0)
		//}.toByteArray()

		// as it is, annotation values may be reordered for no good reason, so we have to fix that
		val clean = readClass(cleanBytes)
		val dirty = readClass(dirtyBytes)

		if (dirty.visibleAnnotations != null) {
			dirty.visibleAnnotations.forEach { dirtyAnnotation ->
				val cleanAnnotation = clean.visibleAnnotations?.find { it.desc == dirtyAnnotation.desc }
				reorderAnnotations(cleanAnnotation, dirtyAnnotation)
			}
		}

		reorderAnnotations(clean.invisibleAnnotations, dirty.invisibleAnnotations)
		reorderAnnotations(clean.visibleAnnotations, dirty.visibleAnnotations)

		dirty.methods.forEach { dirtyMethod ->
			val cleanMethod = clean.methods.find { it.name == dirtyMethod.name && it.desc == dirtyMethod.desc }
			if (cleanMethod != null) {
				reorderAnnotations(cleanMethod.visibleAnnotations, dirtyMethod.visibleAnnotations)
				reorderAnnotations(cleanMethod.invisibleAnnotations, dirtyMethod.invisibleAnnotations)
			}
		}

		dirty.fields.forEach { dirtyField ->
			val cleanField = clean.fields.find { it.name == dirtyField.name && it.desc == dirtyField.desc }
			if (cleanField != null) {
				reorderAnnotations(cleanField.visibleAnnotations, dirtyField.visibleAnnotations)
				reorderAnnotations(cleanField.invisibleAnnotations, dirtyField.invisibleAnnotations)
			}
		}

		val writer = ClassWriter(ClassReader(cleanBytes), 0)
		dirty.accept(writer)
		return writer.toByteArray()
	}

	private fun reorderAnnotations(clean: List<AnnotationNode>?, dirty: List<AnnotationNode>?) {
		if (clean == null || dirty == null) return
		dirty.forEach { dirtyAnnotation ->
			val cleanAnnotation = clean.find { it.desc == dirtyAnnotation.desc }
			reorderAnnotations(cleanAnnotation, dirtyAnnotation)
		}
	}

	private fun reorderAnnotations(clean: AnnotationNode?, dirty: AnnotationNode) {
		if (clean == null || dirty.values == null) return
		val dirtyAsList = dirty.values.chunked(2)
			.map { (k, v) -> k as String to v }
		val cleanKeys = clean.values.nth(2).map { it as String }
		dirty.values = dirtyAsList.sortedBy { (k, _) -> cleanKeys.indexOf(k) }.flatMap { it.toList() }

		for (i in 1 until min(dirty.values.size, clean.values.size)) {
			val cleanValue = clean.values[i]
			val dirtyValue = dirty.values[i]
			if (cleanValue is AnnotationNode && dirtyValue is AnnotationNode && cleanValue.desc == dirtyValue.desc) {
				reorderAnnotations(cleanValue, dirtyValue)
			}
		}
	}

	private fun generatePatches(main: Map<String, ByteArray>, diffs: Map<String, Map<String, ByteArray>>): ByteArray {
		val bout = ByteArrayOutputStream()

		ZipOutputStream(bout).use { out ->
			out.setMethod(ZipEntry.STORED)
			diffs.forEach { (platform, changes) ->
				changes.forEach { (name, changed) ->
					val bytes = generatePatchBytes(main[name]!!, changed)
					out.putNextEntry(ZipEntry("$platform/$name.diff").also {
						it.size = bytes.size.toLong()
						it.compressedSize = it.size
						it.crc = java.util.zip.CRC32().apply { update(bytes) }.value
					})
					out.write(bytes)
					out.closeEntry()
				}
			}

			out.finish()
			out.flush()
		}

		return bout.toByteArray()
	}

	fun generatePatchBytes(clean: ByteArray, dirty: ByteArray): ByteArray {
		return DiffWriter().also {
			ClassDiffer.diff(readClass(clean), readClass(dirty), it)
		}.toByteArray()
	}
}
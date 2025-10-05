import PatchUtil.generatePatchBytes
import PatchUtil.shrinkPatchForDirty
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.*
import org.gradle.jvm.tasks.Jar
import java.io.ByteArrayOutputStream
import java.util.jar.JarFile
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject

@CacheableTask
abstract class MergedJar : Jar() {

	@get:InputFile
	@get:PathSensitive(PathSensitivity.NONE)
	abstract val mainJar: RegularFileProperty

	@get:Internal
	abstract val platformJars: MapProperty<String, RegularFile>

	init {
		inputs.files(platformJars.map { it.values })
		inputs.property("platforms", platformJars.keySet())
	}

	@get:Inject
	abstract val archiveOps: ArchiveOperations

	fun main(jar: RegularFile) {
		mainJar.set(jar)
	}

	fun main(jar: RegularFileProperty) {
		mainJar.set(jar)
	}

	fun main(jar: Jar) {
		mainJar.set(jar.archiveFile)
	}

	fun main(jar: TaskProvider<out Jar>) {
		mainJar.set(jar.flatMap { it.archiveFile })
	}

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

		println("platforms: ${platformJars.get().keys.joinToString(", ")}")

		// map of platform to (map of file name to changed file)
		val diff = mutableMapOf<String, MutableMap<String, ByteArray>>()
		platformJars.get().forEach { (platform, jarFile) ->
			JarFile(jarFile.asFile).use { jar ->
				jar.entries().asSequence().forEach a@{ entry ->
					if (entry.isDirectory) return@a

					val bytes = jar.getInputStream(entry).use { it.readBytes() }
					if (output[entry.name] == null) {
						output[entry.name] = bytes
						return@a
					}

					if (output[entry.name]!!.contentEquals(bytes)) return@a

					if (entry.name.endsWith(".class")) {
						val original = output[entry.name]!!
						val cleanedBytes = shrinkPatchForDirty(original, bytes)
						if (original.contentEquals(cleanedBytes)) {
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

		// slightly sketchy but it does work
		from(archiveOps.zipTree(gradleBruh))
		copy()
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
}
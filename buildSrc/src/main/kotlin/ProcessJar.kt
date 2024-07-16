import org.gradle.api.tasks.InputFile
import org.gradle.jvm.tasks.Jar
import java.io.File
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream
import java.util.zip.Deflater

typealias FileProcessor = (File) -> Unit

open class ProcessJar : Jar() {
	val input = project.objects.fileProperty()
		@InputFile get

	private val processors = mutableListOf<FileProcessor>()

	init {
		group = "build"
		outputs.upToDateWhen { false }
	}

	fun addFileProcessor(regex: Regex, processor: FileProcessor) {
		processors.add {
			it.walkTopDown().forEach { file ->
				if (file.extension.matches(regex))
					processor(file)
			}
		}
	}

	fun addFileProcessor(vararg extensions: String, processor: FileProcessor) {
		addFileProcessor(extensions.asIterable(), processor)
	}

	fun addFileProcessor(extensions: Iterable<String>, processor: FileProcessor) {
		processors.add {
			it.walkTopDown().forEach { file ->
				if (file.extension in extensions)
					processor(file)
			}
		}
	}

	fun addDirProcessor(processor: FileProcessor) {
		processors.add(processor)
	}

	override fun copy() {
		val inputJar = input.get().asFile

		if (!inputJar.exists())
			error("Input jar does not exist: $inputJar")

		val dir = temporaryDir.resolve("unpack")

		if (dir.exists())
			dir.deleteRecursively()
		dir.mkdirs()

		// unpack jar to temp dir
		project.copy {
			from(project.zipTree(inputJar))
			into(dir)
		}

		processors.forEach { it(dir) }

		// merge manifests
		val manifestFile = dir.resolve("META-INF/MANIFEST.MF")
		if (manifestFile.exists()) {
			manifest.from(manifestFile)
			manifestFile.delete()
			manifestFile.createNewFile()
			manifest.effectiveManifest.writeTo(manifestFile)
		}

		// repack jar
		JarOutputStream(archiveFile.get().asFile.outputStream()).use { jos ->
			jos.setLevel(Deflater.BEST_COMPRESSION)
			for (file in dir.walkTopDown()) {
				if(file.isDirectory) continue
				val entry = JarEntry(file.relativeTo(dir).path)
				jos.putNextEntry(entry)
				file.inputStream().copyTo(jos)
				jos.closeEntry()
			}

			jos.finish()
			jos.flush()
		}
	}
}
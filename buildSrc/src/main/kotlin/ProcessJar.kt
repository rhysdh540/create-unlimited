import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.jvm.tasks.Jar
import java.io.File
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream
import java.util.zip.Deflater

typealias FileProcessor = (File) -> Unit

abstract class ProcessJar : Jar() {
	@get:InputFile
	abstract val input: RegularFileProperty

	@get:Internal
	abstract val processors: ListProperty<FileProcessor>

	init {
		group = "build"
		outputs.upToDateWhen { false }

		addFileProcessor(paths = setOf("META-INF/MANIFEST.MF")) { file ->
			manifest.from(file)
			file.delete()
			file.createNewFile()
			manifest.effectiveManifest.writeTo(file)
		}
	}

	fun addFileProcessor(regex: Regex, processor: FileProcessor) {
		processors.add {
			it.walkTopDown().forEach { file ->
				if (file.path.matches(regex))
					processor(file)
			}
		}
	}

	fun addFileProcessor(extensions: Set<String> = emptySet(),
						 names: Set<String> = emptySet(),
						 paths: Set<String> = emptySet(),
						 processor: FileProcessor) {
		processors.add {
			it.walkTopDown().forEach { file ->
				if (file.extension in extensions || file.name in names || file.path in paths)
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

		processors.finalizeValue()
		processors.get().forEach { it(dir) }

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
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import java.io.File
import java.util.jar.JarEntry
import java.util.jar.JarInputStream
import java.util.jar.JarOutputStream
import java.util.zip.Deflater

object Compressors {
	// minify json
	val json: FileProcessor = {
		it.outputStream().write(JsonOutput.toJson(JsonSlurper().parse(it)).toByteArray())
	}

	// store JIJs instead of deflating them so that the outer jar compresses the entire thing (most of the time better)
	val storeJars: FileProcessor = { input ->
		val tmp = input.copyTo(File.createTempFile(input.nameWithoutExtension, ".jar"), overwrite = true)
		JarInputStream(tmp.inputStream()).use { ins ->
			JarOutputStream(input.outputStream()).use { out ->
				out.setLevel(Deflater.NO_COMPRESSION)
				while (true) {
					out.putNextEntry(JarEntry((ins.nextEntry ?: break).name))
					ins.copyTo(out)
					out.closeEntry()
					ins.closeEntry()
				}

				out.finish()
				out.flush()
			}
		}
	}
}

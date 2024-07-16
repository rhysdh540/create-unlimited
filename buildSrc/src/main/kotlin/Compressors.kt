import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import java.io.File
import java.util.jar.JarEntry
import java.util.jar.JarInputStream
import java.util.jar.JarOutputStream
import java.util.zip.Deflater

object Compressors {
	val json = { input: File ->
		input.outputStream().write(JsonOutput.toJson(JsonSlurper().parse(input)).toByteArray())
	}

	val storeJars = { input: File ->
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

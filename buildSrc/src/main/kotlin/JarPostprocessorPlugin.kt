import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import io.github.pacifistmc.forgix.plugin.ForgixMergeExtension
import io.github.pacifistmc.forgix.plugin.MergeJarsTask

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode

import java.io.File
import java.util.jar.JarFile

import org.gradle.kotlin.dsl.*
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream
import java.util.zip.Deflater

class JarPostprocessorPlugin : Plugin<Project> {

	override fun apply(project: Project) {
		project.afterEvaluate {
			if(rootProject != this) {
				throw ProjectConfigurationException("JarPostProcessorPlugin can only be applied to the root project", IllegalStateException())
			}

			val stripLVTs = findProperty("strip_lvts").toString().toBoolean()
			val forgix: ForgixMergeExtension = extensions.getByType<ForgixMergeExtension>()

			tasks.register("squishJar") {
				val mergeJarsTask = tasks.withType<MergeJarsTask>()["mergeJars"]
				dependsOn(mergeJarsTask)
				mergeJarsTask.finalizedBy(this)
				group = "forgix"
				val output = File(forgix.outputDir, forgix.mergedJarName)
				inputs.files(output)

				doLast {
					val entries = readJar(output)
					output.delete()
					entries.replaceAll { name, data ->
						if(name.endsWith(".class") && !name.startsWith("dev/rdh/createunlimited/shadow")) {
							val deAnnotationed = removeUnnecessaryAnnotationsFrom(data)
							if(stripLVTs) {
								stripLVTsFrom(deAnnotationed)
							} else {
								deAnnotationed
							}
						} else if(name.endsWith(".json")) {
							minifyJson(data)
						} else {
							data
						}
					}

					entries.keys.removeIf { it.startsWith("META-INF/services/") }

					JarOutputStream(output.outputStream()).use {
						it.setLevel(Deflater.BEST_COMPRESSION)
						entries.forEach { (name, data) ->
							it.putNextEntry(JarEntry(name))
							it.write(data)
							it.closeEntry()
						}
					}
				}
			}
		}
	}

	private fun readJar(file: File): MutableMap<String, ByteArray> {
		val entries: MutableMap<String, ByteArray> = LinkedHashMap()
		JarFile(file).use {
			it.entries().asIterator().forEach { entry ->
				if (entry.isDirectory()) return@forEach
				val data = it.getInputStream(entry).readAllBytes()
				entries[entry.name] = data
			}
		}
		return entries
	}

	private fun stripLVTsFrom(classBytes: ByteArray): ByteArray {
		val classNode = ClassNode()
		ClassReader(classBytes).accept(classNode, 0)

		classNode.methods.forEach {
			it.localVariables?.clear()
			it.parameters?.clear()
		}

		val classWriter = ClassWriter(0)
		classNode.accept(classWriter)
		return classWriter.toByteArray()
	}

	private fun removeUnnecessaryAnnotationsFrom(classBytes: ByteArray): ByteArray {
		val classNode = ClassNode()
		ClassReader(classBytes).accept(classNode, 0)

		classNode.methods?.forEach { method ->
			method.invisibleAnnotations?.clear()
		}

		val classWriter = ClassWriter(0)
		classNode.accept(classWriter)
		return classWriter.toByteArray()
	}

	private fun minifyJson(jsonBytes: ByteArray): ByteArray {
		return JsonOutput.toJson(JsonSlurper().parse(jsonBytes)).toByteArray()
	}
}
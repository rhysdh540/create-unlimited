import groovy.json.JsonOutput;
import groovy.json.JsonSlurper;

import io.github.pacifistmc.forgix.plugin.ForgixMergeExtension;
import io.github.pacifistmc.forgix.plugin.MergeJarsTask;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.Deflater;

@SuppressWarnings("unused")
public class JarPostProcessorPlugin implements Plugin<Project> {
	@Override
	public void apply(Project project) {
		if(!project.getRootProject().equals(project)) {
			throw new IllegalStateException("apply to root project please");
		}

		boolean stripLVTs = Boolean.parseBoolean(project.findProperty("strip_lvts").toString());

		ForgixMergeExtension forgix = project.getExtensions().getByType(ForgixMergeExtension.class);

		project.getTasks().register("squishJar", jar -> {
			MergeJarsTask mergeJars = project.getTasks().named("mergeJars", MergeJarsTask.class).get();
			jar.dependsOn(mergeJars);
			mergeJars.finalizedBy(jar);
			jar.setGroup("forgix");

			jar.getInputs().files(forgix.getOutputDir(), forgix.getMergedJarName());
			File output = new File(forgix.getOutputDir(), forgix.getMergedJarName());

			jar.doLast(ignore -> {
				Map<String, byte[]> entries = readJar(output);
				output.delete();

				entries.replaceAll((name, bytes) -> {
					if(name.endsWith(".class") && stripLVTs) {
						return stripLVTsFrom(name, bytes);
					}
					if(name.endsWith(".json") || name.endsWith(".mcmeta")) {
						return minifyJson(bytes);
					}
					return bytes;
				});

				entries.entrySet().removeIf(entry -> entry.getKey().startsWith("META-INF/services/"));

				try(JarOutputStream out = new JarOutputStream(Files.newOutputStream(output.toPath()))) {
					out.setLevel(Deflater.BEST_COMPRESSION);
					entries.forEach((name, bytes) -> {
						try {
							out.putNextEntry(new JarEntry(name));
							out.write(bytes);
							out.closeEntry();
						} catch (IOException e) {
							throw new RuntimeException("Failed to write jar entry: " + name, e);
						}
					});
				} catch (IOException e) {
					throw new RuntimeException("Failed to write jar file: " + output, e);
				}
			});
		});
	}

	private static Map<String, byte[]> readJar(File file) {
		Map<String, byte[]> entries = new LinkedHashMap<>();
		try(JarFile jarFile = new JarFile(file)) {
			jarFile.entries().asIterator().forEachRemaining(entry -> {
				if(entry.isDirectory()) return;
				try {
					byte[] data = jarFile.getInputStream(entry).readAllBytes();
					entries.put(entry.getName(), data);
				} catch (IOException e) {
					throw new RuntimeException("Failed to read jar entry: " + entry.getName(), e);
				}
			});
		} catch(Throwable t) {
			throw new RuntimeException("Failed to read jar file: " + file, t);
		}
		return entries;
	}

	private static byte[] stripLVTsFrom(String name, byte[] classBytes) {
		if(name.startsWith("dev/rdh/createunlimited/shadow")) return classBytes;

		try {
			ClassNode classNode = new ClassNode();
			new ClassReader(classBytes).accept(classNode, 0);

			classNode.methods.forEach(methodNode -> {
				List<LocalVariableNode> lvt = methodNode.localVariables;
				if(lvt != null) {
					lvt.clear();
				}
				List<ParameterNode> parameters = methodNode.parameters;
				if(parameters != null) {
					parameters.clear();
				}
			});

			ClassWriter classWriter = new ClassWriter(0);
			classNode.accept(classWriter);
			return classWriter.toByteArray();
		} catch(Throwable t) {
			throw new RuntimeException("Failed to process class file: " + name, t);
		}
	}

	private static byte[] minifyJson(byte[] jsonBytes) {
		return JsonOutput.toJson(new JsonSlurper().parse(jsonBytes)).getBytes();
	}
}

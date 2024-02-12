import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar;
import net.fabricmc.loom.api.LoomGradleExtensionAPI;

import net.fabricmc.loom.configuration.ide.RunConfigSettings;

import net.fabricmc.loom.task.RemapJarTask;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.component.AdhocComponentWithVariants;
import org.gradle.api.component.ConfigurationVariantDetails;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.jvm.tasks.Jar;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import dev.architectury.plugin.ArchitectPluginExtension;

import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class PlatformPlugin implements Plugin<Project> {

	@Override
	public void apply(@NotNull Project project) {
		Project rootProject = project.getRootProject();

		project.getExtensions().getByType(ArchitectPluginExtension.class).platformSetupLoomIde();

		LoomGradleExtensionAPI loom = project.getExtensions().getByType(LoomGradleExtensionAPI.class);

		loom.runs(container -> {
			container.remove(container.findByName("server"));

			Consumer<RunConfigSettings> clientConfig = settings -> {
				if(settings == null) throw new IllegalStateException("Cannot configure client run config settings because settings is null");
				settings.client();
				settings.setName("Minecraft Client");
				settings.setIdeConfigGenerated(true);
				String baseArgs = "-XX:+UnlockExperimentalVMOptions -XX:+UnlockDiagnosticVMOptions -XX:+AlwaysActAsServerClassMachine -XX:+AlwaysPreTouch -XX:+DisableExplicitGC -XX:+UseNUMA -XX:NmethodSweepActivity=1 -XX:ReservedCodeCacheSize=400M -XX:NonNMethodCodeHeapSize=12M -XX:ProfiledCodeHeapSize=194M -XX:NonProfiledCodeHeapSize=194M -XX:-DontCompileHugeMethods -XX:MaxNodeLimit=240000 -XX:NodeLimitFudgeFactor=8000 -XX:+UseVectorCmov -XX:+PerfDisableSharedMem -XX:+UseFastUnorderedTimeStamps -XX:+UseCriticalJavaThreadPriority -XX:ThreadPriorityPolicy=1 -XX:AllocatePrefetchStyle=3";
				String memoryArgs = "-Xmx4G -Xms4G";
				String gcArgs = "-XX:+UseShenandoahGC -XX:ShenandoahGCMode=iu -XX:ShenandoahGuaranteedGCInterval=1000000 -XX:AllocatePrefetchStyle=1";
				settings.vmArgs(String.join(" ", baseArgs, memoryArgs, gcArgs).split(" "));
				if("true".equals(project.findProperty("mixin.debug"))) {
					settings.vmArgs("-Dmixin.debug.export=true", "-Dmixin.debug.verbose=true");
				}
			};
			clientConfig.accept(container.findByName("client"));
		});

		ConfigurationContainer configurations = project.getConfigurations();
		Configuration common = configurations.create("common");
		Configuration shadowCommon = configurations.create("shadowCommon"); // Don't use shadow from the shadow plugin because we don't want IDEA to index this.
		configurations.getByName("compileClasspath").extendsFrom(common);
		configurations.getByName("runtimeClasspath").extendsFrom(common);

		DependencyHandler deps = project.getDependencies();
		((ModuleDependency) deps.add("common", deps.project(Map.of("path", ":common", "configuration", "namedElements"))))
			.setTransitive(false);
		((ModuleDependency) deps.add("shadowCommon", deps.project(Map.of("path", ":common", "configuration", "transformProduction" + capitalize(project.getName())))))
			.setTransitive(false);

		TaskContainer tasks = project.getTasks();

		TaskProvider<ShadowJar> shadowJarProvider = tasks.named("shadowJar", ShadowJar.class);
		shadowJarProvider.configure(task -> {
			task.exclude("architectury.common.json");
			task.exclude("**/PlatformMethods.class");
			task.exclude("LICENSE_MixinExtras");
			task.exclude("META-INF/jarjar/**");
			task.exclude("META-INF/jars/**");
			task.setConfigurations(List.of(shadowCommon, project.getConfigurations().getByName("shade")));
			task.relocate("com.llamalad7.mixinextras", "dev.rdh.createunlimited.shadow.mixinextras");
			task.getArchiveClassifier().set("shadow-" + project.getName());
		});

		ShadowJar shadowJar = shadowJarProvider.get();

		tasks.named("remapJar", RemapJarTask.class).configure(task -> {
			task.getInputFile().set(shadowJar.getArchiveFile());
			task.dependsOn(shadowJar);
			task.getArchiveClassifier().set(project.getName());
		});

		tasks.named("jar", Jar.class, task ->
			task.getArchiveClassifier().set("dev-" + project.getName()));

		tasks.named("sourcesJar", Jar.class, task -> {
			Jar commonSources = rootProject.project(":common").getTasks().named("sourcesJar", Jar.class).get();
			task.dependsOn(commonSources);
			task.from(commonSources.getArchiveFile().map(zipTree -> zipTree));
			task.getArchiveClassifier().set("sources-" + project.getName());
		});

		project.getComponents().named("java", softwareCompoment -> {
			if(softwareCompoment instanceof AdhocComponentWithVariants adhoc) {
				adhoc.withVariantsFromConfiguration(project.getConfigurations().named("shadowRuntimeElements").get(), ConfigurationVariantDetails::skip);
			} else {
				throw new RuntimeException("Unexpected component type: " + softwareCompoment.getClass().getName());
			}
		});
	}

	String capitalize(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
}

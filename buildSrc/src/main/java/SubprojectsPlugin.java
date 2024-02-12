import net.fabricmc.loom.api.LoomGradleExtensionAPI;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.AbstractCopyTask;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class SubprojectsPlugin implements Plugin<Project> {
	Project project;

	@Override
	@SuppressWarnings("UnstableApiUsage")
	public void apply(@NotNull Project project) {
		this.project = project;

		project.apply(o -> {
			o.plugin("dev.architectury.loom");
			o.plugin("architectury-plugin");
		});

		project.getExtensions().getByType(LoomGradleExtensionAPI.class).silentMojangMappingsLicense();

		project.getExtensions().getByType(JavaPluginExtension.class).withSourcesJar();

		Configuration shade = project.getConfigurations().create("shade");
		project.getConfigurations().getByName("implementation").extendsFrom(shade);

		project.getRepositories().mavenCentral();
		project.getRepositories().mavenLocal();
		maven("https://maven.parchmentmc.org");
		maven("https://maven.quiltmc.org/repository/release");
		maven("https://maven.ithundxr.dev/releases");
		maven("https://mvn.devos.one/snapshots");
		maven("https://maven.cafeteria.dev/releases");
		maven("https://maven.jamieswhiteshirt.com/libs-release");
		maven("https://maven.theillusivec4.top");
		maven("https://maven.terraformersmc.com/releases");
		maven("https://jitpack.io");
		maven("https://maven.tterrag.com",
			"com.simibubi.create", "com.jozufozu.flywheel", "com.tterrag.registrate");
		maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/");

		maven("https://api.modrinth.com/maven", "maven.modrinth");
		maven("https://cursemaven.com", "curse.maven");

		LoomGradleExtensionAPI loom = project.getExtensions().getByType(LoomGradleExtensionAPI.class);

		dependencies(deps -> {
			deps.add("minecraft", "com.mojang:minecraft:" + getProperty("minecraft_version"));
			deps.add("mappings", loom.layered(builder -> {
				builder.mappings("org.quiltmc:quilt-mappings:" + getProperty("minecraft_version") + "+build." + getProperty("quilt") + ":intermediary-v2");
				builder.officialMojangMappings();
				builder.parchment("org.parchmentmc.data:parchment-" + getProperty("minecraft_version") + ":" + getProperty("parchment") + "@zip");
			}));

			manifold(deps, "props");
			manifold(deps, "ext");
			deps.add("localRuntime", "systems.manifold:manifold-ext-rt:" + getProperty("manifold_version"));

			String me = "io.github.llamalad7:mixinextras-common:" + getProperty("mixin_extras");
			deps.add("annotationProcessor", me);
			deps.add("implementation", me);
			if(!project.getPath().equals(":common")) {
				deps.add("shade", me);
			}
		});

		project.getTasks().named("processResources", AbstractCopyTask.class, task -> {
			Map<String, String> properties = Map.of(
				"version", getProperty("modVersion"),
				"minecraft", getProperty("minecraft_version"),
				"fabric", getProperty("fabric"),
				"create", getProperty("minimum_create_version")
			);

			task.getInputs().properties(properties);

			task.exclude("**/*.aw");

			task.filesMatching("fabric.mod.json", file -> file.expand(properties));
			task.filesMatching("META-INF/mods.toml", file -> file.expand(properties));
		});
	}

	void manifold(DependencyHandler deps, String module) {
		String location = "systems.manifold:manifold-" + module + ":" + getProperty("manifold_version");
		deps.add("annotationProcessor", location);
		deps.add("compileOnly", location);
		if(!project.getPath().equals(":common")) {
			deps.add("localRuntime", location);
		}
		if(project.getPath().equals(":forge")) {
			deps.add("forgeRuntimeLibrary", location);
		}
	}

	void dependencies(Consumer<DependencyHandler> deps) {
		deps.accept(project.getDependencies());
	}

	String getProperty(String name) {
		return (String) project.getRootProject().getExtensions().getExtraProperties().get(name);
	}

	void maven(String url, String... includes) {
		project.getRepositories().maven(maven -> {
			maven.setUrl(url);
			for (String include : includes) {
				maven.content(content -> {
					content.includeGroup(include);
				});
			}
		});
	}
}

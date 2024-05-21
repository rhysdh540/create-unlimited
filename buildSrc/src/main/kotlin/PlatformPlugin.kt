import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import dev.architectury.plugin.ArchitectPluginExtension
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.task.RemapJarTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.component.AdhocComponentWithVariants
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.*

class PlatformPlugin : Plugin<Project> {
	override fun apply(project: Project) {
		project.run {
			extensions.getByType<ArchitectPluginExtension>().platformSetupLoomIde()
			val loom = extensions.getByType<LoomGradleExtensionAPI>()

			afterEvaluate {
				loom.runs {
					configureEach {
						vmArgs("-XX:+AllowEnhancedClassRedefinition", "-XX:+IgnoreUnrecognizedVMOptions")
					}

					named("client") {
						client()
						name = "Minecraft Client"
						isIdeConfigGenerated = true
						val baseArgs =
							"-XX:+UnlockExperimentalVMOptions -XX:+UnlockDiagnosticVMOptions -XX:+AlwaysActAsServerClassMachine -XX:+AlwaysPreTouch -XX:+DisableExplicitGC -XX:+UseNUMA -XX:NmethodSweepActivity=1 -XX:ReservedCodeCacheSize=400M -XX:NonNMethodCodeHeapSize=12M -XX:ProfiledCodeHeapSize=194M -XX:NonProfiledCodeHeapSize=194M -XX:-DontCompileHugeMethods -XX:MaxNodeLimit=240000 -XX:NodeLimitFudgeFactor=8000 -XX:+UseVectorCmov -XX:+PerfDisableSharedMem -XX:+UseFastUnorderedTimeStamps -XX:+UseCriticalJavaThreadPriority -XX:ThreadPriorityPolicy=1 -XX:AllocatePrefetchStyle=3"
						val memoryArgs = "-Xmx4G -Xms4G"
						val gcArgs =
							"-XX:+UseShenandoahGC -XX:ShenandoahGCMode=iu -XX:ShenandoahGuaranteedGCInterval=1000000 -XX:AllocatePrefetchStyle=1"
						vmArgs("$baseArgs $memoryArgs $gcArgs".split(" "))
						if (project.findProperty("mixin.debug")?.toString()?.toBoolean() == true) {
							vmArgs("-Dmixin.debug.export=true", "-Dmixin.debug.verbose=true")
						}


						val mixinJar = configurations["compileClasspath"].resolvedConfiguration.resolvedArtifacts
							.single {
								(it.moduleVersion.id.group == "dev.architectury" && it.moduleVersion.id.name == "mixin-patched") || // forge
									(it.moduleVersion.id.group == "net.fabricmc" && it.moduleVersion.id.name == "sponge-mixin") // fabric
							}.file

						vmArgs("-javaagent:${mixinJar.absolutePath}")
					}
				}
			}

			val common: Configuration by configurations.creating
			val shadowCommon: Configuration by configurations.creating
			configurations["compileClasspath"].extendsFrom(common)
			configurations["runtimeClasspath"].extendsFrom(common)

			dependencies {
				common(project("path" to ":common", "configuration" to "namedElements")).apply {
					(this as ModuleDependency).isTransitive = false
				}

				shadowCommon(project("path" to ":common", "configuration" to "transformProduction${project.name.capitalized()}")).apply {
					(this as ModuleDependency).isTransitive = false
				}
			}

			val shadowJar = tasks.withType<ShadowJar>()["shadowJar"]

			shadowJar.apply {
				exclude("architectury.common.json")
				exclude("**/PlatformMethods.class")
				exclude("LICENSE_MixinExtras")
				exclude("META-INF/jarjar/**")
				exclude("META-INF/jars/**")
				configurations = listOf(shadowCommon, project.configurations.getByName("shade"))
				archiveClassifier.set("shadow-${project.getName()}")
			}

			tasks.withType<RemapJarTask>()["remapJar"].apply {
				dependsOn(shadowJar)
				inputFile.set(shadowJar.archiveFile)
				archiveClassifier = project.name
			}

			tasks.withType<Jar>()["jar"]
				.archiveClassifier = "dev-${project.name}"

			tasks.withType<Jar>()["sourcesJar"].apply {
				val commonSources = rootProject.project(":common").tasks.withType<Jar>()["sourcesJar"]
				dependsOn(commonSources)
				from(commonSources.archiveFile.map { project.zipTree(it) })
				archiveClassifier = "sources-${project.name}"
			}

			components.withType<AdhocComponentWithVariants>()["java"]
				.withVariantsFromConfiguration(configurations["shadowRuntimeElements"]) {
					skip()
				}
		}
	}
}
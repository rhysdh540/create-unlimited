import org.gradle.api.Task
import org.gradle.api.internal.file.copy.DefaultCopySpec
import org.gradle.api.tasks.*
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.jvm.tasks.Jar

val JAVA_HOME = System.getProperty("java.home") ?: error("JAVA_HOME not set")

fun Jar.clearSourcePaths() {
	AbstractCopyTask::class.java.getDeclaredField("mainSpec").let {
		it.isAccessible = true
		val spec = it.get(this) as DefaultCopySpec
		spec.sourcePaths.clear()
		it.isAccessible = false
	}
}

fun AbstractArchiveTask.putInDevlibs() {
	destinationDirectory.set(project.layout.buildDirectory.dir("devlibs"))
}

inline fun <reified T: Task> TaskContainer.get(name: String): T {
	return this.withType(T::class.java).getByName(name)
}

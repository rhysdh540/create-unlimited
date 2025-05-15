import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Input

abstract class CustomTask : DefaultTask() {
	@get:Input
	abstract val toRun: ListProperty<CustomTask.() -> Unit>

	fun action(action: CustomTask.() -> Unit) {
		toRun.add(action)
	}

	@TaskAction
	fun run() {
		toRun.get().forEach { it() }
	}
}
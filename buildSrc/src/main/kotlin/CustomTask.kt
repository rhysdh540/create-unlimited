import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.TaskAction

abstract class CustomTask : DefaultTask() {
	abstract val toRun: ListProperty<CustomTask.() -> Unit>

	fun action(action: CustomTask.() -> Unit) {
		toRun.add(action)
	}

	@TaskAction
	fun run() {
		toRun.get().forEach { it() }
	}
}
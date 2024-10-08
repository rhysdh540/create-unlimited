import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import xyz.wagyourtail.commonskt.properties.FinalizeOnRead

abstract class CustomTask : DefaultTask() {
	private var toRun by FinalizeOnRead(mutableListOf<CustomTask.() -> Unit>())

	fun action(action: CustomTask.() -> Unit) {
		toRun.add(action)
	}

	@TaskAction
	fun run() {
		toRun.forEach { it() }
	}
}
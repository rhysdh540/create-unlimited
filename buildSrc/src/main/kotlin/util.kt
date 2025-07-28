import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.internal.file.copy.DefaultCopySpec
import org.gradle.api.tasks.AbstractCopyTask
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.RegisteringDomainObjectDelegateProviderWithTypeAndAction

val JAVA_HOME = System.getProperty("java.home") ?: error("JAVA_HOME not set")

fun Jar.clearSourcePaths() {
	AbstractCopyTask::class.java.getDeclaredField("mainSpec").let {
		it.isAccessible = true
		val spec = it.get(this) as DefaultCopySpec
		spec.sourcePaths.clear()
		it.isAccessible = false
	}
}

fun Project.prop(name: String): String {
	return rootProject.properties[name] as? String ?: error("Property $this not found")
}

fun AbstractArchiveTask.putInDevlibs() {
	destinationDirectory.set(project.layout.buildDirectory.dir("devlibs"))
}

fun readClass(bytes: ByteArray): ClassNode {
	val classNode = ClassNode()
	ClassReader(bytes).accept(classNode, 0)
	return classNode
}

fun <T> Iterable<T>.nth(n: Int) =
	if (n < 0) throw IndexOutOfBoundsException("Index $n is out of bounds")
	else this.windowed(1, n).map { it.single() }

inline fun <reified T : Task> TaskContainer.registering(noinline block: T.() -> Unit)
	= RegisteringDomainObjectDelegateProviderWithTypeAndAction.of(this, T::class, block)

inline fun <reified T : Task> TaskContainer.registering(vararg args: Any?, noinline block: T.() -> Unit)
	= RegisteringTaskDelegateProviderWithTypeAndActionAndArgs.of(this, T::class, *args, action = block)

// register with args is only available for TaskContainer for some reason
class RegisteringTaskDelegateProviderWithTypeAndActionAndArgs<T : Task> private constructor(
	private val delegateProvider: TaskContainer,
	private val type: kotlin.reflect.KClass<T>,
	private vararg val args: Any?,
	private val action: T.() -> Unit
) {
	companion object {
		fun <T : Task> of(delegateProvider: TaskContainer, type: kotlin.reflect.KClass<T>, vararg args: Any?, action: T.() -> Unit): RegisteringTaskDelegateProviderWithTypeAndActionAndArgs<T> {
			return RegisteringTaskDelegateProviderWithTypeAndActionAndArgs(delegateProvider, type, *args, action = action)
		}
	}

	operator fun provideDelegate(thisRef: Any?, property: kotlin.reflect.KProperty<*>): TaskProvider<T> {
		return delegateProvider.register(property.name, type.java, *args).apply {
			configure(action)
		}
	}
}
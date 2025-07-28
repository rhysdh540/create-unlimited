@file:Suppress("NOTHING_TO_INLINE", "CONTEXT_RECEIVERS_DEPRECATED")

import org.gradle.api.Project
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider

/**
 * note that this can't be used in buildscripts and must be copied:
 * `operator fun String.invoke() = prop(this)`
 * because you can't enable context receivers in buildscripts :(
 */
context(Project)
inline operator fun String.invoke() = prop(this)

// for when you're not in a Project context
fun Project.propMaybe(name: String): String? {
	var current: Project? = this
	while (current != null) {
		val value = current.properties[name] as? String
		if (value != null) return value
		current = current.parent
	}
	return null
}

fun Project.prop(name: String): String {
	return propMaybe(name) ?: error("Property $name not found for project ${project.name}")
}

context(Project)
fun String.maybe(block: (String) -> Unit) = propMaybe(this)?.let(block)

// gradle doesn't have a lazy version of this? idk why
inline fun <T> NamedDomainObjectContainer<T>.maybeRegister(name: String, noinline config: T.() -> Unit = {}): NamedDomainObjectProvider<T> {
	val provider = if (this.names.contains(name)) this.named(name) else this.register(name)
	provider.configure(config)
	return provider
}
@file:Suppress("UnstableApiUsage")

import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.InclusiveRepositoryContentDescriptor
import org.gradle.api.artifacts.repositories.RepositoryContentDescriptor
import org.gradle.internal.extensions.stdlib.capitalized
import org.gradle.kotlin.dsl.maven

fun RepositoryHandler.parchment() = maven(
	name = "ParchmentMC",
	url = "https://maven.parchmentmc.org"
) {
	includeGroupAndSubgroups("org.parchmentmc")
}

fun RepositoryHandler.modrinth() = exclusiveMaven(
	name = "Modrinth",
	url = "https://api.modrinth.com/maven"
) {
	includeGroup("maven.modrinth")
}

fun RepositoryHandler.curseMaven(beta: Boolean = false) = exclusiveMaven(
	name = "CurseMaven",
	url = if (beta) "https://beta.cursemaven.com" else "https://cursemaven.com",
) {
	includeGroup("curse.maven")
}

fun RepositoryHandler.wagYourMaven(repo: String) = maven(
	name = "WagYourTail (${repo.capitalized()})",
	url = "https://maven.wagyourtail.xyz/$repo"
)

fun RepositoryHandler.sponge() = maven(
	name = "SpongePowered",
	url = "https://repo.spongepowered.org/maven"
)

fun RepositoryHandler.createMod() = exclusiveMaven(
	name = "CreateMod",
	url = "https://maven.createmod.net"
)

fun RepositoryHandler.tterrag() = maven(
	name = "tterrag",
	url = "https://maven.tterrag.com"
)

fun RepositoryHandler.devOS(repo: String) = maven(
	name = "DevOS (${repo.capitalized()})",
	url = "https://mvn.devos.one/$repo"
)

fun RepositoryHandler.fuzs() = maven(
	name = "Fuzs",
	url = "https://raw.githubusercontent.com/Fuzss/modresources/main/maven"
)

fun RepositoryHandler.maven(
	name: String? = null,
	url: String,
	content: RepositoryContentDescriptor.() -> Unit = {}
) = maven(url) {
	if (name != null)
		this.name = name
	content(content)
}

fun RepositoryHandler.exclusiveMaven(
	name: String? = null,
	url: String,
	content: InclusiveRepositoryContentDescriptor.() -> Unit = {}
) = exclusiveContent {
	forRepository {
		maven(name, url)
	}
	filter(content)
}
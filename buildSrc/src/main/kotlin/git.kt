import org.gradle.api.Project
import org.gradle.api.provider.ProviderFactory
import org.jetbrains.annotations.ApiStatus
import java.io.File
import javax.inject.Inject

val Project.git: Git
	get() = project.extensions.let {
		it.findByType(Git::class.java) ?: it.create("git", Git::class.java, project.rootDir)
	}

@ApiStatus.NonExtendable
abstract class Git(val repository: File) {
	fun exists() = repository.resolve(".git").isDirectory()

	// does the current git repository have uncommitted changes?
	fun isDirty() = git("status", "--porcelain").isNotBlank()

	// last commit hash
	fun hash(long: Boolean = false) =
		if(long)
			git("rev-parse", "HEAD")
		else
			git("rev-parse", "--short", "HEAD")

	// current branch
	fun currentBranch() = git("rev-parse", "--abbrev-ref", "HEAD")

	// latest tag
	fun tag() = git("describe", "--tags", "--abbrev=0")

	fun getUncommitedChanges() = git("diff", "--name-only")

	@get:Inject
	@get:ApiStatus.Internal
	abstract val providers: ProviderFactory

	private fun git(vararg args: String): String {
		return providers.exec {
			commandLine("git", *args)
			workingDir(repository)
		}.standardOutput.asText.get().trim()
	}
}
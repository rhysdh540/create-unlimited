import xyz.wagyourtail.unimined.util.FinalizeOnWrite
import xyz.wagyourtail.unimined.util.MustSet
import java.nio.file.Path

object Git {
	// does the current git repository have uncommitted changes?
	fun isDirty() = git("status", "--porcelain").isNotBlank()

	// last commit hash
	fun hash() = git("rev-parse", "--short", "HEAD").trim()

	// current branch
	fun currentBranch() = git("rev-parse", "--abbrev-ref", "HEAD").trim()

	// latest tag
	fun tag() = git("describe", "--tags", "--abbrev=0").trim()

	fun getUncommitedChanges() = git("diff", "--name-only").trim()

	private fun git(vararg args: String): String {
		val process = ProcessBuilder("git", *args)
			.directory(repository.toFile())
			.start()
		return process.inputStream.bufferedReader().readText()
	}

	var repository by FinalizeOnWrite<Path>(MustSet())
}
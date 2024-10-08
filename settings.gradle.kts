include("fabric", "forge")
rootProject.name = "Create Unlimited"

val messageLen = 80
val properties by lazy { java.util.Properties().apply { load(rootProject.projectDir.resolve("gradle.properties").reader()) } }
val message = """
	- ${rootProject.name} v${properties["mod_version"]} -
	${System.getenv("GITHUB_RUN_NUMBER")?.let { "Build #$it" } ?: "Local Build"}

	Gradle ${gradle.gradleVersion}, on ${System.getProperty("java.vm.name")} v${System.getProperty("java.version")}, by ${System.getProperty("java.vendor")}
	OS: "${System.getProperty("os.name")}", arch "${System.getProperty("os.arch")}"
	-


""".trimIndent()

message.lines().forEach {
	val line = it.startsWith('-') && it.endsWith('-')
	val padding = "${if (line) '-' else ' '}".repeat((messageLen - it.length) / 2)
	println("$padding$it$padding")
}
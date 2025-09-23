plugins {
	id("java")
	id("xyz.wagyourtail.manifold")
	id("com.gradleup.shadow") version("9.1.0")
}

repositories {
	mavenLocal()
	mavenCentral()
	maven("https://repo.spongepowered.org/maven")
}

java.toolchain {
	languageVersion = JavaLanguageVersion.of(17)
}

manifold.pluginArgs.add("--no-bootstrap")

dependencies {
	val shade by configurations.creating {
		isTransitive = true
		configurations.compileOnly.get().extendsFrom(this)
	}

	shade("io.github.prcraftmc:class-diff:1.0-SNAPSHOT") {
		exclude(group = "org.ow2.asm")
	}

	compileOnly("org.spongepowered:mixin:0.8.7")
	compileOnly("org.ow2.asm:asm-tree:9.8")
	compileOnly("org.ow2.asm:asm-commons:9.8")

	compileOnly(annotationProcessor(manifold("exceptions"))!!)
}

sourceSets.main {
	java.setSrcDirs(listOf(rootProject.file("src/boot/java")))
	resources.setSrcDirs(listOf(rootProject.file("src/boot/resources")))
}

tasks.shadowJar {
	archiveClassifier.set("deps")
	configurations = listOf(project.configurations["shade"])

	exclude("**/module-info.class")
	exclude("META-INF/maven/**")

	minimize()

	for (lib in listOf("com.github.difflib", "io.github.prcraftmc.classdiff", "com.nothome.delta")) {
		relocate(lib, "dev.rdh.createunlimited.lib.${lib.substringAfterLast(".")}")
	}
}
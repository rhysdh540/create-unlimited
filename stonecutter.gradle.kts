plugins {
	id("base")
	id("dev.kikugie.stonecutter")
}
stonecutter active "1.21.1-neoforge"

rootProject.group = "dev.rdh"
rootProject.base.archivesName.set("create-unlimited")
rootProject.version = prop("mod_version")
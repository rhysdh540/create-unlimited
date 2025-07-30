plugins {
	id("base")
	id("dev.kikugie.stonecutter")
}
stonecutter active "1.20.1-forge"

rootProject.group = "dev.rdh"
rootProject.base.archivesName.set("create-unlimited")
rootProject.version = prop("mod_version")
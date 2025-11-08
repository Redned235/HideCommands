plugins {
    id("java")
    id("java-library")
    id("com.modrinth.minotaur") version "2.+"
}

group = "me.redned.geyser.extension.hidecommands"
version = "1.0.2"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        name = "opencollab-releases"
        url = uri("https://repo.opencollab.dev/maven-releases/")
        mavenContent {
            releasesOnly()
        }
    }
    maven {
        name = "opencollab-snapshots"
        url = uri("https://repo.opencollab.dev/maven-snapshots/")
        mavenContent {
            snapshotsOnly()
        }
    }
}

dependencies {
    compileOnly("org.geysermc.geyser:api:2.9.0-SNAPSHOT")
    compileOnly("org.yaml:snakeyaml:2.5")
}

modrinth {
    val snapshot = "SNAPSHOT" in rootProject.version.toString()

    token.set(System.getenv("MODRINTH_TOKEN") ?: "")
    projectId.set("hidecommands")
    versionNumber.set(rootProject.version as String + if (snapshot) "-" + System.getenv("BUILD_NUMBER") else "")
    versionType.set(if (snapshot) "beta" else "release")
    changelog.set(System.getenv("CHANGELOG") ?: "")
    uploadFile.set(tasks.jar)
    loaders.set(listOf("geyser"))
    gameVersions.set(listOf("1.21.10"))
}

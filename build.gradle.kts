plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.11.0"
}

group = "ma.leet"
version = "1.5"

repositories {
    mavenCentral()
}

intellij {
    version.value("2022.3.2")
    type.set("CL")

    plugins.set(listOf())
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    patchPluginXml {
        sinceBuild.value("222.*")
        untilBuild.value("231.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}

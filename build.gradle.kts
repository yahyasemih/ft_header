plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.15.0"
}

group = "ma.leet"
version = "1.12"

repositories {
    mavenCentral()
}

intellij {
    version.value("2023.3")
    type.set("CL")

    plugins.set(listOf())
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    patchPluginXml {
        sinceBuild.value("222")
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

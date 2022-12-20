plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.11.0"
}

group = "ma.leet"
version = "1.1"

repositories {
    mavenCentral()
}

intellij {
    version.value("2022.2.1")
    type.set("CL")

    plugins.set(listOf())
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    patchPluginXml {
        sinceBuild.value("222.3345.126")
        untilBuild.value("223.7571.171")
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

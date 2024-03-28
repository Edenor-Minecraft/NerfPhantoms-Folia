plugins {
    `java-library`
    `maven-publish`
}

group = "org.altronmaxx"
version = "2.0"
description = "nerfphantoms-folia"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenLocal()

    maven {
        url = uri("https://maven.pkg.github.com/Edenor-Minecraft/Foldenor/")
        credentials {
            username = System.getenv("USERNAME")
            password = System.getenv("TOKEN")
        }
    }
}

dependencies {
    compileOnly("dev.edenor.foldenor:foldenor-api:1.20.4-R0.1-SNAPSHOT")
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<ProcessResources> {
    outputs.upToDateWhen { false }
    filesMatching("plugin.yml") {
        expand(
                "version" to project.version
        )
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}

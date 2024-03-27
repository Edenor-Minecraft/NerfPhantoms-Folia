plugins {
    `java-library`
    `maven-publish`
}

group = "org.altronmaxx"
version = "1.0-SNAPSHOT"
description = "nerfphantoms-folia"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }

    maven {
        url = uri("https://maven.pkg.github.com/Edenor-Minecraft/Foldenor/")
        credentials {
            username = System.getenv("USERNAME")
            password = System.getenv("TOKEN")
        }
    }

    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    maven {
        url = uri("https://repo.codemc.org/repository/maven-public")
    }
}

dependencies {
    compileOnly("dev.edenor.foldenor:foldenor-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("org.bstats:bstats-bukkit:3.0.2")
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}

plugins {
    `kotlin-dsl`
    `java-library`
    `maven-publish`
    id("idea")
}

tasks.jar {
    archiveBaseName.set("Aurora")
    archiveClassifier.set("")
    archiveVersion.set("")
}
tasks.compileJava {
    options.encoding = "UTF-8"
}
tasks.processResources{
    expand("version" to version)
}

repositories {
    maven { url = uri("https://repo.voop.lv/repository/vooplv-public/") }
    maven { url = uri("https://maven.enginehub.org/repo/") }
    maven { url = uri("https://libraries.minecraft.net") }
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
    maven { url = uri("https://repo.maven.apache.org/maven2/") }
    maven { url = uri("https://repo.md-5.net/content/repositories/releases/") }
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/") {
        name = "sonatype-oss-snapshots"
    }
    maven { url = uri("https://mvn.exceptionflug.de/repository/exceptionflug-public/") }
    maven { url = uri("https://repo.dmulloy2.net/nexus/repository/public/") }
    maven { url = uri("https://maven.enginehub.org/repo/") }
    maven { url = uri("https://ci.nametagedit.com/plugin/repository/everything/") }
    maven { url = uri("https://jitpack.io/") }
    maven { url = uri("https://repo.phoenix616.dev/") }
    mavenCentral()
    gradlePluginPortal()
    mavenLocal()
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")
    compileOnly("io.papermc.paper:paper-api:1.19-R0.1-SNAPSHOT")
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
    compileOnly("com.github.BeYkeRYkt:LightAPI:5.2.0-Bukkit")
    compileOnly("com.sk89q.worldguard:worldguard-legacy:7.0.0-SNAPSHOT") {
        exclude("org.bukkit","bukkit")
        exclude("org.bstats","bstats-bukkit")
    }
    compileOnly("com.comphenix.protocol:ProtocolLib:4.8.0")
}

publishing {
    publications.create<MavenPublication>("vooplv-public") {
        artifact(tasks["jar"])
    }
    repositories {
        maven {
            name = "voopLVPrivate"
            url = uri("https://repo.voop.lv/repository/vooplv-public/")
            credentials(PasswordCredentials::class)
        }
    }
}

group = "com.zenya"
description = "aurora"
version = "4.3"
java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17
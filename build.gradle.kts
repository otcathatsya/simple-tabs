plugins {
    id("fabric-loom")
    val kotlinVersion: String by System.getProperties()
    kotlin("jvm").version(kotlinVersion)
    kotlin("plugin.serialization").version(kotlinVersion)
}
base {
    val archivesBaseName: String by project
    archivesName.set(archivesBaseName)
}
val modVersion: String by project
version = modVersion
val mavenGroup: String by project
group = mavenGroup
repositories {
    maven {
        name = "CottonMC"
        url = uri("https://server.bbkr.space/artifactory/libs-release")
    }
}
dependencies {
    val minecraftVersion: String by project
    minecraft("com.mojang", "minecraft", minecraftVersion)
    val yarnMappings: String by project
    mappings("net.fabricmc", "yarn", yarnMappings, null, "v2")
    val loaderVersion: String by project
    modImplementation("net.fabricmc", "fabric-loader", loaderVersion)
    val fabricVersion: String by project
    modImplementation("net.fabricmc.fabric-api", "fabric-api", fabricVersion)
    val fabricKotlinVersion: String by project
    modImplementation("net.fabricmc", "fabric-language-kotlin", fabricKotlinVersion)
    val libGuiVersion: String by project
    modImplementation(include("io.github.cottonmc", "LibGui", libGuiVersion))
    val okhttpVersion: String by project
    include(implementation("com.squareup.okhttp3", "okhttp", okhttpVersion))
    include(implementation("com.squareup.okhttp3", "logging-interceptor", okhttpVersion))
    val retrofitVersion: String by project
    include(implementation("com.squareup.retrofit2", "retrofit", retrofitVersion))
    val ktConverterVersion: String by project
    include(implementation("com.jakewharton.retrofit", "retrofit2-kotlinx-serialization-converter", ktConverterVersion))
}

tasks {
    val javaVersion = JavaVersion.VERSION_17
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
        options.release.set(javaVersion.toString().toInt())
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions { jvmTarget = javaVersion.toString() }
    }
    jar { from("LICENSE") { rename { "${it}_${base.archivesName}" } } }
    processResources {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") { expand(mutableMapOf("version" to project.version)) }
    }
    java {
        toolchain { languageVersion.set(JavaLanguageVersion.of(javaVersion.toString())) }
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        withSourcesJar()
    }
}

sourceSets {
    main {
        java {
            srcDir("src/main/kotlin")
            srcDir("src/main/java")
        }
    }
}

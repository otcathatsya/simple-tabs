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

    // 3rd party mods, UI
    modImplementation(include("io.github.cottonmc", "LibGui", libGuiVersion))

    // Web libs
    val okhttpVersion: String by project
    implementation(include("com.squareup.okhttp3", "okhttp", okhttpVersion))
    implementation(include("com.squareup.okhttp3", "logging-interceptor", okhttpVersion))
    val retrofitVersion: String by project
    implementation(include("com.squareup.retrofit2", "retrofit", retrofitVersion))
    val ktConverterVersion: String by project
    implementation(include("com.jakewharton.retrofit", "retrofit2-kotlinx-serialization-converter", ktConverterVersion))
    val okioVersion: String by project
    implementation(include("com.squareup.okio", "okio-jvm", okioVersion))
}

tasks {
    val javaVersion = JavaVersion.VERSION_1_8
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
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
        toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
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

plugins {
    id("fabric-loom") version ("1.8-SNAPSHOT")
    signing
    id("eu.kakde.gradle.sonatype-maven-central-publisher") version "1.0.6"
}

apply(from = "deploying/secrets.gradle.kts")

allprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "fabric-loom")

    val targetJavaVersion = 17

    java {
        withSourcesJar()
    }

    version = properties["mod_version"]!!
    group = properties["maven_group"]!!

    // floader needs to be in every sub module
    dependencies {
        modImplementation("net.fabricmc:fabric-loader:${properties["loader_version"]}")

    }

    tasks {
        withType<JavaCompile> {
            // ensure that the encoding is set to UTF-8, no matter what the system default is
            // this fixes some edge cases with special characters not displaying correctly
            // see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
            // If Javadoc is generated, this must be specified in that task too.
            this.options.encoding = "UTF-8"
            if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
                this.options.release = targetJavaVersion
            }
        }
        withType<ProcessResources> {
            inputs.property("version", version)

            filesMatching("fabric.mod.json") {
                expand(
                    mapOf(
                        "version" to version,
                        "satin_version" to project.properties["satin_version"]
                    )
                )
            }
        }
    }
}

subprojects {
    // skip our API module

    dependencies {
        // include the root project because that's where our common code is
        implementation(project(path = ":", configuration = "namedElements")) {
            // having 2 floaders from the sub project and root project causes issues so we get rid of one.
            exclude(group = "net.fabricmc", module = "fabric-loader")
        }
    }
}

version = properties["mod_version"]!!
group = properties["maven_group"]!!

repositories {
    // Add repositories to retrieve artifacts from in here.
    // You should only use this when depending on other mods because
    // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
    // See https://docs.gradle.org/current/userguide/declaring_repositories.html
    // for more information about repositories.
    maven {
        name = "Ladysnake Mods"
        url = uri("https://maven.ladysnake.org/releases")
        content {
            includeGroup("io.github.ladysnake")
            includeGroup("org.ladysnake")
            includeGroupByRegex("dev\\.onyxstudios.*")
        }
    }
}

dependencies {
    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:${properties["minecraft_version"]}")
    mappings("net.fabricmc:yarn:${properties["yarn_mappings"]}:v2")
    modImplementation("net.fabricmc:fabric-loader:${properties["loader_version"]}")

    modApi("net.fabricmc.fabric-api:fabric-api:${properties["fabric_version"]}")

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    implementation("com.github.weisj:jsvg:1.4.0")
    implementation(group = "de.javagl", name = "obj", version = "0.4.0")
    modApi("org.ladysnake:satin:${properties["satin_version"]}") {
        exclude(module = "fabric-api")
    }
}

tasks {
    withType<ProcessResources> {
        inputs.property("version", project.version)
        inputs.property("satin_version", project.properties["satin_version"])
        filteringCharset = "UTF-8"

        filesMatching("fabric.mod.json") {
            expand(
                mapOf(
                    "version" to version,
                    "satin_version" to project.properties["satin_version"]
                )
            )
        }
    }
    withType<JavaCompile> {
        this.options.encoding = "UTF-8"
    }
    withType<Jar> {
        from("LICENSE") {
            rename { "${it}_renderer" }
        }

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}

base {
    archivesName = properties["archivesBaseName"] as String
}

java {

    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()
    withJavadocJar()
}

// configure the maven publication
publishing {

    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
    repositories {
        maven {
            url = uri(layout.buildDirectory.dir("publish"))
        }
    }
}

val sonatypeUsername: String by project
val sonatypePassword: String by project

sonatypeCentralPublishExtension {
    groupId.set("io.github.0x3c50.renderer")
    artifactId.set("renderer-fabric")
    version.set(project.version as String)
    componentType = "java"
    publishingType = "USER_MANAGED"
    username.set(sonatypeUsername)
    password.set(sonatypePassword)

    pom {
        name = "renderer-fabric"
        description = "A simple rendering library for FabricMC"
        url = "https://github.com/0x3C50/Renderer"
        licenses {
            license {
                name = "GNU GPLv3"
                url = "https://www.gnu.org/licenses/gpl-3.0.en.html"
            }
        }
        developers {
            developer {
                id = "0x3C50"
                name = "0x150"
            }
        }
        scm {
            connection = "scm:git:https://github.com/0x3C50/Renderer.git"
            developerConnection = "scm:git:https://github.com/0x3C50/Renderer.git"
            url = "https://github.com/0x3C50/Renderer"
        }
    }

}
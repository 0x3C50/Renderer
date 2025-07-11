plugins {
    fabric
    signing
    id("eu.kakde.gradle.sonatype-maven-central-publisher") version "1.0.6"
}

apply(from = file("deploying/secrets.gradle.kts"))

version = rootProject.properties["mod_version"]!!
group = rootProject.properties["maven_group"]!!

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    // https://mvnrepository.com/artifact/org.lwjgl/lwjgl-harfbuzz
    include(implementation("org.lwjgl:lwjgl-harfbuzz:3.3.3")!!)
}

sourceSets {
    main {
        java {
            srcDir("src/generated/java")
        }
    }
}

java {

    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()
    withJavadocJar()
}

loom {
//    accessWidenerPath = file("src/main/resources/aw.accesswidener")
}

tasks {
    withType<ProcessResources> {
        inputs.property("version", project.version)
        val fabApi = rootProject.properties["fabric_version"]
        inputs.property("fapi_version", fabApi)
        val loader = rootProject.properties["loader_version"]
        inputs.property("loader_version", loader)
        val mc = rootProject.properties["minecraft_version"]
        inputs.property("minecraft_version", mc)
        filteringCharset = "UTF-8"

        filesMatching("fabric.mod.json") {
            expand(
                mapOf(
                    "version" to version,
                    "fapi_version" to fabApi,
                    "loader_version" to loader,
                    "minecraft_version" to mc
                )
            )
        }
    }
    withType<Jar> {
        from("LICENSE") {
            rename { "${it}_renderer" }
        }

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    register<GeneratePrimitiveEmitterTask>("generateEmitter") {
        specificationFile = rootProject.file("emitterSpec.json5").absolutePath
        packageName = "me.x150.renderer.generated"
        outputDir = file("src/generated/java")
    }

    withType<JavaCompile> {
        dependsOn("generateEmitter")
    }

    named("sourcesJar") {
        dependsOn("generateEmitter")
    }

    withType<Javadoc> {
        (options as StandardJavadocDocletOptions).addBooleanOption("Xdoclint:all,-missing", true)
    }
}

base {
    archivesName = "renderer-fabric"
}

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
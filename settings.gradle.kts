pluginManagement {
    repositories {
        maven {
            name = "Fabric"
            url = java.net.URI("https://maven.fabricmc.net/")
        }
        gradlePluginPortal()
    }
}

rootProject.name = "renderer-bom"

include ("renderer")
include ("TestMod")

plugins {
    `fabric-loom`
}

dependencies {
    minecraft("com.mojang:minecraft:${rootProject.properties["minecraft_version"]}")
    mappings("net.fabricmc:yarn:${rootProject.properties["yarn_mappings"]}:v2")
    modImplementation("net.fabricmc:fabric-loader:${rootProject.properties["loader_version"]}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${rootProject.properties["fabric_version"]}")
}
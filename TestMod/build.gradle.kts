//archivesBaseName = "TestMod"

plugins {
    fabric
}

repositories {
    maven("https://maven.wispforest.io")
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    implementation(project(path=":renderer", configuration = "namedElements"))!!

    implementation("org.lwjgl:lwjgl-harfbuzz:3.3.3:natives-linux")

    include(implementation("org.lwjgl:lwjgl-harfbuzz:3.3.3")!!)

    modImplementation("io.wispforest:owo-lib:0.12.21+1.21.6")
}
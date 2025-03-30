//archivesBaseName = "TestMod"

plugins {
    fabric
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    implementation(project(path=":renderer", configuration = "namedElements"))

    implementation("org.lwjgl:lwjgl-harfbuzz:3.3.3:natives-linux")
}
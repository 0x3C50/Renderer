# Renderer
An easy-to-use rendering library for modern fabric

# Installing
You can install this library using [Jitpack](https://jitpack.io/)

To install this library, just add the jitpack repository to your `repositories` section as described on the jitpack website, and add the ladysnake repository (for gradle to find satin):
```groovy
maven {
    name = 'Ladysnake Mods'
    url = 'https://maven.ladysnake.org/releases'
    content {
        includeGroup 'io.github.ladysnake'
        includeGroup 'org.ladysnake'
        includeGroupByRegex 'dev\\.onyxstudios.*'
    }
}
```

Then add this to your dependencies block in `build.gradle`.
```groovy
include modImplementation("com.github.0x3C50:Renderer:master-SNAPSHOT")
```
*This will use the latest commit as build target, but will cache that build target every time. Use the latest short commit hash found on [Jitpack](https://jitpack.io/#0x3C50/Renderer) (example: `d2cc995ff4`) as the version, to get that release instead.*  

## Caution
It's important to use fabric's **`modImplementation`** instead of the regular `implementation`, since this is technically a mod that needs remapping. Using anything else except `modImplementation` will not remap the library, which causes invalid names to be present.

Similarly, using fabric's **`include`** is also recommended over anything else, since it includes the whole jar as a Jar-In-Jar ("JIJ") dependency, which fabric can handle better than just copying the classes over. JIJing it this way will make it appear in the mod list, as well as making the license and credits visible, whereas copying the classes into the final jar will just keep the library and effectively strip the metadata.

# Usage
The api has an extensive javadoc, which explains almost anything. The wiki also has some more insights and examples.

# The two renderers
Renderer2d draws in 2 dimensions, on the hud. Renderer3d draws in 3 dimensions, in the world.

# Events
This library uses fabric's event system, the wiki has examples on how it works. Their wiki has more details.

# Suggestions
You can leave suggestions in the issues section

If this library helped you, please consider leaving a star, since this library took me a while to make as it is right now :)

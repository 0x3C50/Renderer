# Renderer
An easy-to-use rendering library for modern fabric.

# Installing
You can install this library using [Jitpack](https://jitpack.io/)

To install this library, add the ladysnake repository (for gradle to find satin):
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
<!-- CHANGE VERSION HERE!!!! -->
```groovy
include modImplementation("io.github.0x3c50.renderer:renderer-fabric:1.2.5")
``` 

## Caution
It's important to use fabric's **`modImplementation`** instead of the regular `implementation`, since this is technically a mod that needs remapping. Using anything else except `modImplementation` will not remap the library, which causes invalid names to be present.

Similarly, using fabric's **`include`** is also recommended, since it includes the whole jar as a Jar-In-Jar ("JIJ") dependency, which fabric can handle better than just copying the classes over. JIJing it this way will make it appear in the mod list, as well as making the license and credits visible, whereas copying the classes into the final jar will just keep the library and effectively strip the metadata.

## Verifying the download
For the interested, the public key used to sign the releases is `63FF42E13662B6D604611FF4B22112D66E3EA177`, and can be found on https://keys.openpgp.org/.

# Building on your own
Since the build contains some secrets for deploying, which are filled in by a gradle script in `deploying/secrets.gradle.kts`, you will need to create that file for the build to succeed.
It doesn't have to contain anything, the secrets will just be blank by default.

# Usage
The api has an extensive javadoc, which explains almost anything. The wiki also has some more insights and examples.

# The two renderers
Renderer2d draws in 2 dimensions, on the hud. Renderer3d draws in 3 dimensions, in the world.

# Events
This library uses fabric's event system, the wiki has examples on how it works. Their wiki has more details.

# Suggestions
You can leave suggestions in the issues section

If this library helped you, please consider leaving a star, since this library took me a while to make as it is right now :)

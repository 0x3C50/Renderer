# Renderer
An easy-to-use rendering library for modern fabric.

# Installing
This library can be found on [Maven central](https://central.sonatype.com/artifact/io.github.0x3c50.renderer/renderer-fabric).

To install it, add this snippet to the dependencies section of your `build.gradle`:
<!-- CHANGE VERSION HERE!!!! -->
```groovy
include modImplementation("io.github.0x3c50.renderer:renderer-fabric:2.1.5")
```
This will include the library as a JIJ ("Jar in Jar") dependency, such that your users won't have to worry about installing it themselves.
If you don't want to include the library in your mod, remove the `include`. (JUST THE `include`. Keep everything else intact. It should be `modImplementation(...)`)

Then, to make sure the correct HarfBuzz version (for the font renderer) is included, also add:
```groovy
include implementation("org.lwjgl:lwjgl-harfbuzz:3.3.3:natives-(platform)")
```
and replace the (platform) with a lwjgl platform. As previously mentioned, you can omit the include if you dont want to JiJ this dependency.
You can add multiple instances of this dependency with different platforms to target multiple platforms.

Note that you don't *have* to include this dependency, lwjgl can use the system installation of the library as well. It might not work as expected tho, since versions may differ. If you want to make sure, include the dependency.

The available platforms can be found here: https://repo1.maven.org/maven2/org/lwjgl/lwjgl-harfbuzz/3.3.3/ (lwjgl-harfbuzz-3.3.3-natives-(platform).jar)

## Caution
It's important to use fabric's **`modImplementation`** instead of the regular `implementation`, since this is technically a mod that needs remapping. Using anything else except `modImplementation` will not remap the library, which causes invalid names to be present.

Similarly, using fabric's **`include`** is also recommended, since it includes the whole jar as a Jar-In-Jar ("JIJ") dependency, which fabric can handle better than just copying the classes over. JIJing it this way will make it appear in the mod list, as well as making the license and credits visible, whereas copying the classes into the final jar will just keep the library and effectively strip the metadata.

## Verifying the download
For the interested, the public key used to sign the releases is `63FF42E13662B6D604611FF4B22112D66E3EA177`, and can be found on https://keys.openpgp.org/.

# Building on your own
Since the build contains some secrets for deploying, which are filled in by a gradle script in `deploying/secrets.gradle.kts`, you will need to create that file for the build to succeed.
It doesn't have to contain anything, the secrets will just be blank by default.

# Usage
Guides can be found in `guides/`.
The specific components have a javadoc, and the wiki also contains some information. Other than that, just try stuff until it works ;)

# Events
This library uses fabric's event system, the wiki has examples on how it works. Their wiki has more details.

# Suggestions
You can leave suggestions in the issues section

If this library helped you, please consider leaving a star, since this library took me a while to make as it is right now :)

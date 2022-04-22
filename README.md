
# Statement
Library mod for the Fabric and Forge mod loaders that allows mod developers to modify blockstates of existing blocks.  

# Information for Players
<details open>
<summary>Show/Hide Information for Players</summary><table width=100%><td>

## Required Mods to Run
<details open>
<summary>Show/Hide Required Mods</summary><table width=100%><td>

### Playing on Fabric

- Newest version of the [Fabric mod loader](https://fabricmc.net/use/)  
- Newest version of the [Fabric A](https://www.curseforge.com/minecraft/mc-mods/fabric-api/files/all)[PI mod](https://modrinth.com/mod/fabric-api/versions) for whichever Minecraft version you're playing on

### Playing on Forge

- Newest version of the [Forge mod loader](https://files.minecraftforge.net/net/minecraftforge/forge/) for whichever Minecraft version you're playing on
</td></table></details>

## Supported Minecraft Versions
<details>
<summary>Show/Hide Supported Minecraft Versions</summary><table width=100%><td>

### Fabric Versions
Supported Versions of `Statement-x.y.z+1.14.4-1.19`:  
`1.14.4`, `1.15.2`, `1.16.5`, `1.17.1`, `1.18.1`, `1.18.2`, `1.19`

### Forge Versions

Supported Versions of `Statement-x.y.z+1.16.5-forge`:  
`1.16.5`

Supported Versions of `Statement-x.y.z+1.17.1-forge`:  
`1.17.1`

Supported Versions of `Statement-x.y.z+1.18.1-forge`:  
`1.18.1`

Supported Versions of `Statement-x.y.z+1.18.2-forge`:  
`1.18.2`

</td></table></details>

## Mod Features
<details>
<summary>Show/Hide Mod Features</summary><table width=100%><td></br>

Statement allows mod developers to:

- Safely add and remove blockstate properties to/from existing blocks
- Make new properties which would have a mutable collection of values that can be modified later
- Perform the above points such that parts of vanilla that aren't coded with blockstate property mutability in mind don't break (e.g. certain parts of worldgen)
- Have certain blockstates sync to the client as another type of blockstate (e.g. a property that only exists serverside)
- Have certain blockstates' IDs be placed at the end of the blockstate ID list (prevents possible gaps in the ID list when combined with e.g. serverside properties)
</td></table></details>
</td></table></details>

# Information for Developers
<details>
<summary>Show/Hide Information for Developers</summary><table width=100%><td>

## Adding a Dependency
<details open>
<summary>Show/Hide Dependency Information</summary><table width=100%><td>

### Maven

<details open>
<summary>Show/Hide Maven Information</summary><table width=100%><td>

To make use of Statement in your own mod, you'll first need to go to your `repositories` block near the top of your `build.gradle` and </br>add JitPack to the bottom of the block like below:

```groovy
repositories {
	// ... your other maven repositories above ...
	maven {
		url = "https://jitpack.io"
	}
}
```
</td></table></details>

### Mod Version and Dependency Configuration

<details open>
<summary>Show/Hide Dependency Configuration Information</summary><table width=100%><td>

Now that a Maven repository is specified, add `statement_version=x.y.z-w` to your `gradle.properties`, replacing `x.y.z-w` with one </br>of the available version strings from the [list of release tags](../../tags).

Lastly, in your `build.gradle`'s `dependencies` block, add the corresponding line from below depending on your mod loader:

#### Developing for Fabric with Loom

```groovy
modApi("com.github.Virtuoel:Statement:${statement_version}", {
	exclude group: "net.fabricmc.fabric-api"
})
```

#### Developing for Forge with ForgeGradle

```groovy
api fg.deobf("com.github.Virtuoel:Statement:${statement_version}")
```

#### Developing for Forge with Architectury Loom

```groovy
modApi("com.github.Virtuoel:Statement:${statement_version}")
```
</td></table></details>

### Fixing Mixins of Dependencies If Using Older ForgeGradle (4 and below)

<details>
<summary>Show/Hide Fix for Dependency Mixins on Older ForgeGradle</summary><table width=100%><td>

If you're using Forge with ForgeGradle 4 or older, make sure refmap remapping is enabled in your `build.gradle`'s run configuration blocks.

Make sure the following lines are present in the `client {}`, `server {}`, and `data {}` run configuration blocks.

```groovy
property 'mixin.env.remapRefMap', 'true'
property 'mixin.env.refMapRemappingFile', "${buildDir}/createSrgToMcp/output.srg"
```

Then regenerate your run configurations with `genEclipseRuns`, `genIntellijRuns`, or `genVSCodeRuns` depending on your IDE.
</td></table></details>
</td></table></details>
<!--
## API Information
<details>
<summary>Show/Hide API Information</summary><table width=100%><td>

### WIP

</td></table></details>
-->
</td></table></details>

# Thing Minecraft Module 1.21.10

A Minecraft Fabric Mod (Open JDK Java 21 recommended). Getting back into modding Minecraft is fun.
I wonder how the redundancy of `yarn` will go (has gone) now that Mojang has commited to release the source
symbols.

## Features

* Some convenience documentation for general modding
* Some mod lists to maybe work as a modpack
* The `Suspicious Substance` (reduce, reuse, recycle)
* Some chat encryption and `op` marking at the server
* Just added craftable name tag data generation shaped recipe similar to
  * [`craftable-nametag`](https://modrinth.com/datapack/craftable-nametag) - Useful to name things using low iron, spare string, paper 

## Easy Development

Apart from `ctrl` + `E`, the fabulous finder of all things including code bookmarks, the following might help to test
other mods with this one. To build against them, they have to be added to the projects external libraries.

Linking the IDE launcher to your saves after the first run of `M̀inecraft Client` before you
save anything might be useful. The first launch makes `run/`.

```bash
rm -rf ~/IdeaProjects/thing/run/saves
ln -s ~/.var/app/com.mojang.Minecraft/.minecraft/saves ~/IdeaProjects/thing/run
rm -rf ~/IdeaProjects/thing/run/mods
ln -s ~/.var/app/com.mojang.Minecraft/.minecraft/mods ~/IdeaProjects/thing/run
```

This then uses the Linux Flatpack (on Linux Mint 22) and gives
access to worlds you use to test. This of course assumes you've
already installed the fabric mod loader to make `mods/` exist
in your Flatpack install.

You don't need to log in to launch from within IntelliJ IDEA,
but by default it doesn't access your saved worlds.

### Prism Launcher

I tried a new launcher, `Prism Launcher`, and the following is working for me, as links, to use instead
of the above symlinks. Be aware that deleting symlinks requires the `unlink` CLI command. Don't delete
the folders that contain your saves and mods!

```bash
rm -rf ~/IdeaProjects/thing/run/saves
ln -s ~/.var/app/org.prismlauncher.PrismLauncher/data/PrismLauncher/instances/1.21.10.Fabric/minecraft/saves ~/IdeaProjects/thing/run
rm -rf ~/IdeaProjects/thing/run/mods
ln -s ~/.var/app/org.prismlauncher.PrismLauncher/data/PrismLauncher/instances/1.21.10.Fabric/minecraft/mods ~/IdeaProjects/thing/run
```

### `Data Generation` Run Configuration

Although at first the `Data Generation` run configuration would work, it does fail as soon as mod
dependencies happen. To fix it you have to edit the run configuration and change the working directory to be the
same as the `Minecraft Client`'s run configuration working directory. Then it actually does work with **API** mods.
So yes, the correct working directory for `Data Generation` is `run/`, as Minecraft is running to generate the
`src/main/generated` resources.

## Other Utility or Dependency Mods

Here are some other mods used while developing `thing` which also helps with not duplicating features and with
some useful APIs. Some of these are marked dependencies. The **API** is locked by version numbers in
`gradle.properties`. It's where all the choice action happens.

So for **2025** there's these:

* [`appleskin`](https://modrinth.com/mod/appleskin) - Food improvement HUD
* [`BetterF3`](https://modrinth.com/mod/betterf3) - Nice `F3` key improvement
* [`chat_heads`](https://modrinth.com/mod/chat-heads) - In chat player heads
* [`cloth-config`](https://modrinth.com/mod/cloth-config) - Config menu **API** (Added as dependency)
* [`dynamic-fps`](https://modrinth.com/mod/dynamic-fps) - Resource saver (client)
  * Not really a graphics mod for me, but for listening to music instead of sleeping for example
* [`easyauth`](https://modrinth.com/mod/easyauth) - Server authentication plugin (Only servers)
* [`eiv-fabric`](https://modrinth.com/mod/eiv) - Extended ItemView for finding resource recipes **API**
  * No designs to make other craft formats, so **API** not used yet 
* [`fabric-api`](https://modrinth.com/mod/fabric-api) - Obvious **API** to use (Dependency, but uses mod folder copy) 
* [`ferritecore`](https://modrinth.com/mod/ferrite-core) - Some Java memory optimizations
* [`Jade`](https://modrinth.com/mod/jade) - Block/Entity ID on HUD
* [`lithium`](https://modrinth.com/mod/lithium) - Various CPU optimizations
* [`mclogs-fabric`](https://modrinth.com/plugin/mclogs) - A logs sharing command `/mclogs` (`/mclogsc` on the client)
* [`modmenu`](https://modrinth.com/mod/modmenu) - Mod configuration menus plus **API** (Added as dependency)
* [`placeholder-api`](https://modrinth.com/mod/placeholder-api) - Useful string macros **API** (Added as dependency)
* [`Scribble`](https://modrinth.com/mod/scribble) - Easier book editing interface
* [`StackDeobfuscatorFabric`](https://modrinth.com/mod/stackdeobf) - For better stack traces on errors

## Graphical Mods

I'm OK with Vanilla graphics, and since installing `dynamic-fps` the machine works better. Mods of this kind are
more the personal taste of people, so few if any are included here. Then there's that vibrant visuals thing, and
it will likely spill into Java Edition. I as of **2025** only have an Intel NUC, so I'm not a melted connector GFX
guy. I'd likely use such a thing for some mod AI experimentation anyway.

## Networking and Multiplayer Modes

* [`locator-heads`](https://modrinth.com/mod/locator-heads) - Nice for online play
  * Disabled in build due to "competing" `DataGenerator` run "deleting" the generated files
* [`NoChatReports`](https://modrinth.com/mod/no-chat-reports) - For controversial chat reporting (Adults assumed)

## Other Useful Links

### Project Related

* [`Javadocs`](https://jackokring.github.io/thing/) - HTML javadocs (manual gradle build)
  * Setup using a GitHub pages custom action (static build) and altering the `path:` to `./build/docs/javadoc`
* [`Jars`](build/libs) - Here are `.jar` files (manual gradle build)
  * You'll have to click tro select, and then download the raw `.jar`

### General Modding

The Mojang/Yarn choice seems to only be Yarn at the moment (2025-11-04) as the Mojang official doesn't seem to find
all the class imports. Maybe this is some work in progress for the release of obfuscation free code. Maybe Yarn then
just stabilizes API.
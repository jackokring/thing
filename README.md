# Thing Minecraft Module 1.21.10

A Minecraft Fabric Mod (Open JDK Java 21 recommended). Getting back into modding Minecraft is fun.
I wonder how the redundancy of `yarn` will go (has gone) now that Mojang has commited to release the source
symbols.

## Features

* Some convenience documentation for general modding

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

I tried a new launcher, `Prism Launcher`, and the following is working for me, as links, to use instead
of the above symlinks. Be aware that deleting the other symlinks requires the `unlink` CLI command. Don't delete
the folders that contain your saves and mods!

```bash
ln -s ~/.var/app/org.prismlauncher.PrismLauncher/data/PrismLauncher/instances/1.21.10.Fabric/minecraft/saves ~/IdeaProjects/thing/run
ln -s ~/.var/app/org.prismlauncher.PrismLauncher/data/PrismLauncher/instances/1.21.10.Fabric/minecraft/mods ~/IdeaProjects/thing/run
```

## Other Utility or Dependency Mods

Here are some other mods used while developing `Thing` which also helps with not duplicating features and with
some useful APIs. Some of these are marked dependencies.

* [`appleskin`](https://modrinth.com/mod/appleskin) - Food improvement HUD
* [`BetterF3`](https://modrinth.com/mod/betterf3) - Nice `F3` key improvement
* [`chat_heads`](https://modrinth.com/mod/chat-heads) - In chat player heads
* [`cloth-config`](https://modrinth.com/mod/cloth-config) - Config menu **API** (Added as dependency)
* [`fabric-api`](https://modrinth.com/mod/fabric-api) - Obvious **API** to use (Dependency, but uses mod folder)
* [`ferritecore`](https://modrinth.com/mod/ferrite-core) - Some Java memory optimizations
* [`Jade`](https://modrinth.com/mod/jade) - Block/Entity ID on HUD
* [`lithium`](https://modrinth.com/mod/lithium) - Various CPU optimizations
* [`modmenu`](https://modrinth.com/mod/modmenu) - Mod configuration menus plus **API** (Added as dependency)
* [`placeholder-api`](https://modrinth.com/mod/placeholder-api) - Useful string macros **API** (Added as dependency)
* [`Scribble`](https://modrinth.com/mod/scribble) - Easier book editing interface

## Other Change Game Mechanics Mods

These mods are in a separate list as they do change some game mechanic and not just the UI. It could be
said that a UI change does alter timing of events, but this is like saying pause is ruining games.

* [`craftable-nametag`](https://modrinth.com/datapack/craftable-nametag) - Useful to name things in game

## Other Links


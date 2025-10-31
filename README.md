# Thing Minecraft Module 1.21.10

A Minecraft Fabric Mod (Open JDK Java 21 recommended)

## Features

* Some convenience documentation

## Easy Development

Apart from `ctrl` + `E` the following might be nice.

Linking the launch to your saves after the first run of `M̀inecraft Client` before you
save anything or install any other mods. The first run makes `run/`.

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

You don't need to login to launch from within IntelliJ IDEA,
but by default it doesn't access your worlds.

With the `Prism Launcher` the following is working for me as the links.

```bash
ln -s ~/.var/app/org.prismlauncher.PrismLauncher/data/PrismLauncher/instances/1.21.10.Fabric/minecraft/saves ~/IdeaProjects/thing/run
ln -s ~/.var/app/org.prismlauncher.PrismLauncher/data/PrismLauncher/instances/1.21.10.Fabric/minecraft/mods ~/IdeaProjects/thing/run
```
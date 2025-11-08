package uk.co.kring.thing;

import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

class ModBlocks {
    //NEED_TOOL = BlockBehaviour.Properties.of().requiresCorrectToolForDrops();

    public static final BlockBehaviour.Properties GRASS = BlockBehaviour.Properties.of().sound(SoundType.GRASS);

    static final Block SUSPICIOUS_DIRT = register("suspicious_dirt", Block::new, GRASS);

    static void initialize() {

    }

    static Block register(String name, Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.Properties settings) {
        // Create a registry key for the block
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, Thing.identify(name));
        // Create the block instance
        Block block = blockFactory.apply(settings.setId(blockKey));

        // Sometimes, you may not want to register an item for the block.
        // Eg: if it's a technical block like `minecraft:moving_piston` or `minecraft:end_gateway`
        // Items need to be registered with a different type of registry key, but the ID
        // can be the same.
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Thing.identify(name));

        BlockItem blockItem = new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
        Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);

        return Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
    }
}

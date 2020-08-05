package mekanism.additions.common;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class AdditionsTags {

    public static class Items {

        public static final Tag<Item> BALLOONS = tag("balloons");

        public static final Tag<Item> FENCES_PLASTIC = forgeTag("fences/plastic");
        public static final Tag<Item> FENCE_GATES_PLASTIC = forgeTag("fence_gates/plastic");
        public static final Tag<Item> STAIRS_PLASTIC = forgeTag("stairs/plastic");
        public static final Tag<Item> SLABS_PLASTIC = forgeTag("slabs/plastic");
        public static final Tag<Item> STAIRS_PLASTIC_GLOW = forgeTag("stairs/plastic/glow");
        public static final Tag<Item> SLABS_PLASTIC_GLOW = forgeTag("slabs/plastic/glow");
        public static final Tag<Item> STAIRS_PLASTIC_TRANSPARENT = forgeTag("stairs/plastic/transparent");
        public static final Tag<Item> SLABS_PLASTIC_TRANSPARENT = forgeTag("slabs/plastic/transparent");

        public static final Tag<Item> GLOW_PANELS = tag("glow_panels");

        public static final Tag<Item> PLASTIC_BLOCKS = tag("plastic_blocks");
        public static final Tag<Item> PLASTIC_BLOCKS_GLOW = tag("plastic_blocks/glow");
        public static final Tag<Item> PLASTIC_BLOCKS_PLASTIC = tag("plastic_blocks/plastic");
        public static final Tag<Item> PLASTIC_BLOCKS_REINFORCED = tag("plastic_blocks/reinforced");
        public static final Tag<Item> PLASTIC_BLOCKS_ROAD = tag("plastic_blocks/road");
        public static final Tag<Item> PLASTIC_BLOCKS_SLICK = tag("plastic_blocks/slick");
        public static final Tag<Item> PLASTIC_BLOCKS_TRANSPARENT = tag("plastic_blocks/transparent");

        private static Tag<Item> forgeTag(String name) {
            return new ItemTags.Wrapper(new ResourceLocation("forge", name));
        }

        private static Tag<Item> tag(String name) {
            return new ItemTags.Wrapper(MekanismAdditions.rl(name));
        }
    }

    public static class Blocks {

        public static final Tag<Block> FENCES_PLASTIC = forgeTag("fences/plastic");
        public static final Tag<Block> FENCE_GATES_PLASTIC = forgeTag("fence_gates/plastic");
        public static final Tag<Block> STAIRS_PLASTIC = forgeTag("stairs/plastic");
        public static final Tag<Block> SLABS_PLASTIC = forgeTag("slabs/plastic");
        public static final Tag<Block> STAIRS_PLASTIC_GLOW = forgeTag("stairs/plastic/glow");
        public static final Tag<Block> SLABS_PLASTIC_GLOW = forgeTag("slabs/plastic/glow");
        public static final Tag<Block> STAIRS_PLASTIC_TRANSPARENT = forgeTag("stairs/plastic/transparent");
        public static final Tag<Block> SLABS_PLASTIC_TRANSPARENT = forgeTag("slabs/plastic/transparent");

        public static final Tag<Block> GLOW_PANELS = tag("glow_panels");

        public static final Tag<Block> PLASTIC_BLOCKS = tag("plastic_blocks");
        public static final Tag<Block> PLASTIC_BLOCKS_GLOW = tag("plastic_blocks/glow");
        public static final Tag<Block> PLASTIC_BLOCKS_PLASTIC = tag("plastic_blocks/plastic");
        public static final Tag<Block> PLASTIC_BLOCKS_REINFORCED = tag("plastic_blocks/reinforced");
        public static final Tag<Block> PLASTIC_BLOCKS_ROAD = tag("plastic_blocks/road");
        public static final Tag<Block> PLASTIC_BLOCKS_SLICK = tag("plastic_blocks/slick");
        public static final Tag<Block> PLASTIC_BLOCKS_TRANSPARENT = tag("plastic_blocks/transparent");

        private static Tag<Block> forgeTag(String name) {
            return new BlockTags.Wrapper(new ResourceLocation("forge", name));
        }

        private static Tag<Block> tag(String name) {
            return new BlockTags.Wrapper(MekanismAdditions.rl(name));
        }
    }

    public static class Entities {

        public static final Tag<EntityType<?>> CREEPERS = forgeTag("creepers");
        public static final Tag<EntityType<?>> ENDERMEN = forgeTag("endermen");

        private static Tag<EntityType<?>> forgeTag(String name) {
            return new EntityTypeTags.Wrapper(new ResourceLocation("forge", name));
        }
    }
}
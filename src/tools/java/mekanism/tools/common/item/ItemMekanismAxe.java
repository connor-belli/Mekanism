package mekanism.tools.common.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.tools.common.IHasRepairType;
import mekanism.tools.common.ToolsLang;
import mekanism.tools.common.material.MaterialCreator;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

public class ItemMekanismAxe extends AxeItem implements IHasRepairType {

    private final MaterialCreator material;

    public ItemMekanismAxe(MaterialCreator material, Item.Properties properties) {
        super(material, material.getAxeDamage(), material.getAxeAtkSpeed(), properties);
        this.material = material;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag) {
        tooltip.add(ToolsLang.HP.translate(stack.getMaxDamage() - stack.getDamage()));
    }

    public float getAttackDamage() {
        return material.getAxeDamage() + getTier().getAttackDamage();
    }

    public int getHarvestLevel() {
        return getTier().getHarvestLevel();
    }

    /**
     * {@inheritDoc}
     *
     * @implNote Wrap {@link AxeItem#getDestroySpeed(ItemStack, BlockState)} and  {@link net.minecraft.item.ToolItem#getDestroySpeed(ItemStack, BlockState)} to return our
     * efficiency level
     */
    @Override
    public float getDestroySpeed(@Nonnull ItemStack stack, BlockState state) {
        Material material = state.getMaterial();
        if (material == Material.WOOD || material == Material.PLANTS || material == Material.TALL_PLANTS || material == Material.BAMBOO ||
            getToolTypes(stack).stream().anyMatch(state::isToolEffective) || effectiveBlocks.contains(state.getBlock())) {
            return getTier().getEfficiency();
        }
        return 1;
    }

    /**
     * {@inheritDoc}
     *
     * @implNote Patches {@link AxeItem} to return true when the block's harvest tool is an axe
     */
    @Override
    public boolean canHarvestBlock(BlockState state) {
        return state.getHarvestTool() == ToolType.AXE ? getHarvestLevel() >= state.getHarvestLevel() : super.canHarvestBlock(state);
    }

    @Nonnull
    @Override
    public Ingredient getRepairMaterial() {
        return getTier().getRepairMaterial();
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return getTier().getMaxUses();
    }

    @Override
    public boolean isDamageable() {
        return getTier().getMaxUses() > 0;
    }

    @Override
    public int getHarvestLevel(@Nonnull ItemStack stack, @Nonnull ToolType tool, @Nullable PlayerEntity player, @Nullable BlockState blockState) {
        return tool == ToolType.AXE ? getHarvestLevel() : super.getHarvestLevel(stack, tool, player, blockState);
    }

    /**
     * {@inheritDoc}
     *
     * @implNote We bypass calling super to ensure we get added instead of not being able to add the proper values that {@link net.minecraft.item.ToolItem} tries to set
     */

    @Nonnull
    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(@Nonnull EquipmentSlotType slot) {
        Multimap<String, AttributeModifier> attributes = HashMultimap.create();
        if (slot == EquipmentSlotType.MAINHAND) {
            attributes.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", getAttackDamage(), Operation.ADDITION));
            attributes.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", material.getAxeAtkSpeed(), Operation.ADDITION));
        }
        return attributes;
    }
}
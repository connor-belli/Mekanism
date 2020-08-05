package mekanism.additions.common.entity.baby;

import java.util.Random;
import javax.annotation.Nonnull;
import mekanism.additions.common.MekanismAdditions;
import mekanism.additions.common.registries.AdditionsItems;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.monster.StrayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class EntityBabyStray extends StrayEntity {

    private static final DataParameter<Boolean> IS_CHILD = EntityDataManager.createKey(EntityBabyStray.class, DataSerializers.BOOLEAN);

    //Copy of stray spawn restrictions
    public static boolean spawnRestrictions(EntityType<EntityBabyStray> type, IWorld world, SpawnReason reason, BlockPos pos, Random random) {
        return canMonsterSpawnInLight(type, world, reason, pos, random) && (reason == SpawnReason.SPAWNER || world.canSeeSky(pos));
    }

    public EntityBabyStray(EntityType<EntityBabyStray> type, World world) {
        super(type, world);
        setChild(true);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.getDataManager().register(IS_CHILD, false);
    }

    @Override
    public boolean isChild() {
        return getDataManager().get(IS_CHILD);
    }

    public void setChild(boolean child) {
        getDataManager().set(IS_CHILD, child);
        if (world != null && !world.isRemote) {
            IAttributeInstance attributeInstance = getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
            attributeInstance.removeModifier(MekanismAdditions.babySpeedBoostModifier);
            if (child) {
                attributeInstance.applyModifier(MekanismAdditions.babySpeedBoostModifier);
            }
        }
    }

    @Override
    public void notifyDataManagerChange(@Nonnull DataParameter<?> key) {
        if (IS_CHILD.equals(key)) {
            recalculateSize();
        }
        super.notifyDataManagerChange(key);
    }

    @Override
    protected int getExperiencePoints(@Nonnull PlayerEntity player) {
        if (isChild()) {
            experienceValue = (int) (experienceValue * 2.5F);
        }
        return super.getExperiencePoints(player);
    }

    @Override
    public double getYOffset() {
        return isChild() ? 0.0D : super.getYOffset();
    }

    @Override
    protected float getStandingEyeHeight(@Nonnull Pose pose, @Nonnull EntitySize size) {
        return this.isChild() ? 0.93F : super.getStandingEyeHeight(pose, size);
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        return AdditionsItems.BABY_STRAY_SPAWN_EGG.getItemStack();
    }
}
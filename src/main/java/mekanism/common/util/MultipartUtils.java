package mekanism.common.util;

import mekanism.api.backport.Vector3d;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.VoxelShape;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;

public final class MultipartUtils {

    /* taken from MCMP */
    public static Pair<Vector3d, Vector3d> getRayTraceVectors(Entity entity) {
        float pitch = entity.rotationPitch;
        float yaw = entity.rotationYaw;
        Vector3d start = new Vector3d(entity.getPosX(), entity.getPosY() + entity.getEyeHeight(), entity.getPosZ());
        float f1 = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f2 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f3 = -MathHelper.cos(-pitch * 0.017453292F);
        float f4 = MathHelper.sin(-pitch * 0.017453292F);
        float f5 = f2 * f3;
        float f6 = f1 * f3;
        double d3 = 5.0D;
        if (entity instanceof ServerPlayerEntity) {
            d3 = ((ServerPlayerEntity) entity).getAttribute(PlayerEntity.REACH_DISTANCE).getValue();
        }
        Vector3d end = start.add(f5 * d3, f4 * d3, f6 * d3);
        return Pair.of(start, end);
    }

    public static AdvancedRayTraceResult collisionRayTrace(BlockPos pos, Vector3d start, Vector3d end, Collection<VoxelShape> boxes) {
        double minDistance = Double.POSITIVE_INFINITY;
        AdvancedRayTraceResult hit = null;
        int i = -1;
        for (VoxelShape shape : boxes) {
            if (shape != null) {
                BlockRayTraceResult result = shape.rayTrace(start.toVec(), end.toVec(), pos);
                if (result != null) {
                    result.subHit = i;
                    result.hitInfo = null;
                    AdvancedRayTraceResult advancedResult = new AdvancedRayTraceResult(result, shape);
                    double d = advancedResult.squareDistanceTo(start);
                    if (d < minDistance) {
                        minDistance = d;
                        hit = advancedResult;
                    }
                }
            }
            i++;
        }
        return hit;
    }

    public static class AdvancedRayTraceResult {

        public final VoxelShape bounds;
        public final RayTraceResult hit;

        public AdvancedRayTraceResult(RayTraceResult mop, VoxelShape shape) {
            hit = mop;
            bounds = shape;
        }

        public boolean valid() {
            return hit != null && bounds != null;
        }

        public double squareDistanceTo(Vector3d vec) {
            return hit.getHitVec().squareDistanceTo(new Vec3d(vec.x, vec.y, vec.z));
        }
    }
}
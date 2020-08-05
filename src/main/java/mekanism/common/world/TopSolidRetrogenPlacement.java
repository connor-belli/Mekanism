package mekanism.common.world;

import com.mojang.datafixers.Dynamic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.placement.FrequencyConfig;
import net.minecraft.world.gen.placement.TopSolid;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TopSolidRetrogenPlacement extends TopSolid {


    public TopSolidRetrogenPlacement(Function<Dynamic<?>, ? extends FrequencyConfig> configFactory) {
        super(configFactory);
    }

    @Nonnull
    @Override
    public Stream<BlockPos> getPositions(IWorld world, ChunkGenerator<? extends GenerationSettings> generator, Random random, FrequencyConfig config, BlockPos pos) {
        return IntStream.range(0, config.count).mapToObj(num -> {
            int i = random.nextInt(16) + pos.getX();
            int j = random.nextInt(16) + pos.getZ();
            //Use OCEAN_FLOOR instead of OCEAN_FLOOR_WG as the chunks are already generated
            int k = world.getHeight(Type.OCEAN_FLOOR, i, j);
            return new BlockPos(i, k, j);
        });
    }
}
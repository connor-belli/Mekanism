package mekanism.common;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import mekanism.common.config.MekanismConfig;
import mekanism.common.lib.chunkloading.ChunkManager;
import mekanism.common.lib.frequency.FrequencyManager;
import mekanism.common.world.GenHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

public class CommonWorldTickHandler {

    private static final long maximumDeltaTimeNanoSecs = 16_000_000; // 16 milliseconds

    private Map<ResourceLocation, Queue<ChunkPos>> chunkRegenMap;
    public static boolean flushTagAndRecipeCaches;

    public void addRegenChunk(DimensionType dimension, ChunkPos chunkCoord) {
        if (chunkRegenMap == null) {
            chunkRegenMap = new Object2ObjectArrayMap<>();
        }
        ResourceLocation dimensionName = dimension.getRegistryName();
        if (!chunkRegenMap.containsKey(dimensionName)) {
            LinkedList<ChunkPos> list = new LinkedList<>();
            list.add(chunkCoord);
            chunkRegenMap.put(dimensionName, list);
        } else if (!chunkRegenMap.get(dimensionName).contains(chunkCoord)) {
            chunkRegenMap.get(dimensionName).add(chunkCoord);
        }
    }

    public void resetRegenChunks() {
        if (chunkRegenMap != null) {
            chunkRegenMap.clear();
        }
    }

    @SubscribeEvent
    public void worldLoadEvent(WorldEvent.Load event) {
        if (!event.getWorld().isRemote()) {
            FrequencyManager.load();
            Mekanism.radiationManager.createOrLoad();
            if (event.getWorld() instanceof ServerWorld) {
                ChunkManager.worldLoad((ServerWorld) event.getWorld());
            }
        }
    }

    @SubscribeEvent
    public void onTick(ServerTickEvent event) {
        if (event.side.isServer() && event.phase == Phase.END) {
            serverTick();
        }
    }

    @SubscribeEvent
    public void onTick(WorldTickEvent event) {
        if (event.side.isServer() && event.phase == Phase.END) {
            tickEnd((ServerWorld) event.world);
        }
    }

    private void serverTick() {
        FrequencyManager.tick();
        Mekanism.radiationManager.tickServer();
    }

    private void tickEnd(ServerWorld world) {
        if (!world.isRemote) {
            Mekanism.radiationManager.tickServerWorld(world);
            ChunkManager.tick(world);
            flushTagAndRecipeCaches = false;

            if (chunkRegenMap == null || !MekanismConfig.world.enableRegeneration.get()) {
                return;
            }
            ResourceLocation dimensionName = world.getDimension().getType().getRegistryName();
            //Credit to E. Beef
            if (chunkRegenMap.containsKey(dimensionName)) {
                Queue<ChunkPos> chunksToGen = chunkRegenMap.get(dimensionName);
                long startTime = System.nanoTime();
                while (System.nanoTime() - startTime < maximumDeltaTimeNanoSecs && !chunksToGen.isEmpty()) {
                    ChunkPos nextChunk = chunksToGen.poll();
                    if (nextChunk == null) {
                        break;
                    }

                    Random fmlRandom = new Random(world.getSeed());
                    long xSeed = fmlRandom.nextLong() >> 2 + 1L;
                    long zSeed = fmlRandom.nextLong() >> 2 + 1L;
                    fmlRandom.setSeed((xSeed * nextChunk.x + zSeed * nextChunk.z) ^ world.getSeed());
                    if (GenHandler.generate(world, fmlRandom, nextChunk.x, nextChunk.z)) {
                        Mekanism.logger.info("Regenerating ores at chunk {}", nextChunk);
                    }
                }
                if (chunksToGen.isEmpty()) {
                    chunkRegenMap.remove(dimensionName);
                }
            }
        }
    }
}
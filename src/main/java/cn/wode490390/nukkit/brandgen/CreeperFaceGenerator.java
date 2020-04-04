package cn.wode490390.nukkit.brandgen;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import cn.wode490390.nukkit.brandgen.noise.PerlinSimplexNoise;
import cn.wode490390.nukkit.brandgen.util.BlockEntry;
import cn.wode490390.nukkit.brandgen.util.ColoredBlocks;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class CreeperFaceGenerator extends Generator {

    protected static final boolean[] FACE = {
            false, false, false, false, false, false, false, false,
            false, true , true , false, false, true , true , false,
            false, true , true , false, false, true , true , false,
            false, false, false, true , true , false, false, false,
            false, false, false, true , true , false, false, false,
            false, false, true , true , true , true , false, false,
            false, false, true , false, false, true , false, false,
            false, false, false, false, false, false, false, false
    };

    protected ChunkManager level;
    protected NukkitRandom nukkitRandom;
    protected long localSeed1;
    protected long localSeed2;

    protected PerlinSimplexNoise face;
    protected PerlinSimplexNoise background;

    public CreeperFaceGenerator() {

    }

    public CreeperFaceGenerator(Map<String, Object> options) {

    }

    @Override
    public int getId() {
        return TYPE_INFINITE;
    }

    @Override
    public String getName() {
        return "normal";
    }

    @Override
    public ChunkManager getChunkManager() {
        return this.level;
    }

    @Override
    public Map<String, Object> getSettings() {
        return Collections.emptyMap();
    }

    @Override
    public void init(ChunkManager level, NukkitRandom random) {
        this.level = level;
        this.nukkitRandom = random;
        this.nukkitRandom.setSeed(this.level.getSeed());
        this.localSeed1 = ThreadLocalRandom.current().nextLong();
        this.localSeed2 = ThreadLocalRandom.current().nextLong();

        this.face = new PerlinSimplexNoise(this.nukkitRandom, 6);
        this.background = new PerlinSimplexNoise(this.nukkitRandom, 6);
    }

    @Override
    public void generateChunk(int chunkX, int chunkZ) {
        this.nukkitRandom.setSeed(chunkX * this.localSeed1 ^ chunkZ * this.localSeed2 ^ this.level.getSeed());
        BaseFullChunk chunk = this.level.getChunk(chunkX, chunkZ);
        this.placeFace(chunk, 0, 0);
        this.placeFace(chunk, 0, 8);
        this.placeFace(chunk, 8, 0);
        this.placeFace(chunk, 8, 8);
    }

    protected void placeFace(BaseFullChunk chunk, int baseX, int baseZ) {
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        double faceHeight = (1 + this.face.getValue(chunkX, chunkZ)) * 16;
        double backgroundHeight = (1 + this.background.getValue(chunkX, chunkZ)) * 16;

        this.nukkitRandom.setSeed(((chunkX << 1) + baseX) * 0x4f9939f508L + ((chunkZ << 1) + baseZ) * 0x1ef1565bd5L);
        BlockEntry[] entries = ColoredBlocks.COLORED_BLOCKS[this.nukkitRandom.nextBoundedInt(ColoredBlocks.COLORED_BLOCKS.length)];
        BlockEntry faceBlock = entries[this.nukkitRandom.nextBoundedInt(entries.length)];
        BlockEntry backgroundBlock = entries[this.nukkitRandom.nextBoundedInt(entries.length)];

        for (int x = 0; x < 8; ++x) {
            int cx = x + baseX;
            for (int z = 0; z < 8; ++z) {
                int cz = z + baseZ;

                BlockEntry block;
                double height;
                if (FACE[(x << 3) + z]) {
                    block = faceBlock;
                    height = faceHeight;
                } else {
                    block = backgroundBlock;
                    height = backgroundHeight;
                }

                for (int y = 1; y < height && y < 256; ++y) {
                    chunk.setBlock(cx, y, cz, block.getId(), block.getMeta());
                }
                chunk.setBiome(cx, cz, EnumBiome.JUNGLE.biome);
            }
        }
    }

    @Override
    public void populateChunk(int chunkX, int chunkZ) {

    }

    @Override
    public Vector3 getSpawn() {
        return new Vector3(0.5, 256, 0.5);
    }
}

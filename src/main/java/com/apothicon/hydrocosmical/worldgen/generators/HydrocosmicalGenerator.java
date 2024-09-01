package com.apothicon.hydrocosmical.worldgen.generators;

import com.apothicon.hydrocosmical.HydrolMath;
import com.apothicon.hydrocosmical.worldgen.noise.ConfiguredSimplexNoise;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.savelib.blockdata.IBlockData;
import finalforeach.cosmicreach.savelib.blockdata.SingleBlockData;
import finalforeach.cosmicreach.savelib.blocks.IBlockDataFactory;
import finalforeach.cosmicreach.world.Chunk;
import finalforeach.cosmicreach.world.Zone;
import finalforeach.cosmicreach.worldgen.ChunkColumn;
import finalforeach.cosmicreach.worldgen.ZoneGenerator;
import finalforeach.cosmicreach.worldgen.noise.SimplexNoise;

import java.util.Random;

public class HydrocosmicalGenerator extends ZoneGenerator {
    protected ConfiguredSimplexNoise baseNoise;
    protected ConfiguredSimplexNoise surfaceVariationNoise;
    protected ConfiguredSimplexNoise temperatureNoise;
    protected ConfiguredSimplexNoise continentalnessNoise;

    BlockState airBlock = this.getBlockStateInstance("base:air[default]");
    BlockState stoneBlock = this.getBlockStateInstance("base:stone_basalt[default]");
    BlockState grassBlock = this.getBlockStateInstance("base:grass[default]");
    BlockState dirtBlock = this.getBlockStateInstance("base:dirt[default]");
    BlockState magmaBlock = this.getBlockStateInstance("base:magma[default]");
    BlockState waterBlock = this.getBlockStateInstance("base:water[default]");

    private float maxGameElevation = 384.0F;
    public long seed = (new Random()).nextLong();

    IBlockDataFactory<BlockState> chunkDataFactory = new IBlockDataFactory<BlockState>() {
        public IBlockData<BlockState> createChunkData() {
            SingleBlockData<BlockState> chunkData = new SingleBlockData(airBlock);
            return chunkData;
        }
    };

    @Override
    public String getSaveKey() {
        return "hydrocosmical:hydrocosmical";
    }

    @Override
    protected String getName() {
        return "Hydrocosmical";
    }

    @Override
    public int getDefaultRespawnYLevel() {
        return Integer.MIN_VALUE;
    }

    @Override
    public void create() {
        this.baseNoise = new ConfiguredSimplexNoise(new SimplexNoise(this.seed), 0.66f, 0f);
        this.surfaceVariationNoise = new ConfiguredSimplexNoise(new SimplexNoise(this.seed+100L),  4, 0);
        this.temperatureNoise = new ConfiguredSimplexNoise(new SimplexNoise(this.seed+200L), 0.125f, 0);
        this.continentalnessNoise = new ConfiguredSimplexNoise(new SimplexNoise(this.seed+300L), 0.125f, 0);
    }

    @Override
    public void generateForChunkColumn(Zone zone, ChunkColumn col) {
        int maxCy = col.chunkY+15;

        for (int cy = 0; cy <= maxCy; ++cy) {
            Chunk chunk = zone.getChunkAtChunkCoords(col.chunkX, cy, col.chunkZ);
            if (chunk == null) {
                chunk = new Chunk(col.chunkX, cy, col.chunkZ);
                chunk.initChunkData(this.chunkDataFactory);
                zone.addChunk(chunk);
                col.addChunk(chunk);
            }

            for (int y = 0; y < 16; ++y) {
                int altitude = chunk.blockY + y;
                for (int x = 0; x < 16; ++x) {
                    int longitude = chunk.blockX + x;
                    for (int z = 0; z < 16; ++z) {
                        int latitude = chunk.blockZ + z;
                        if (altitude <= 16) {
                            chunk.setBlockState(magmaBlock, x, y, z);
                        } else if (altitude <= 256) {
                            double finalTerrain = getFinalTerrain(longitude, altitude, latitude);
                            if (finalTerrain > 0) {
                                chunk.setBlockState(stoneBlock, x, y, z);
                            } else if (altitude <= 63) {
                                chunk.setBlockState(waterBlock, x, y, z);
                            }
                            //if (baseNoise.getValue(longitude, latitude) + HydrolMath.gradient(altitude, 128, 0, 1, -1) > 0) {
                            //    chunk.setBlockState(stoneBlock, x, y, z);
                            //} else if (altitude <= 63) {
                            //    chunk.setBlockState(waterBlock, x, y, z);
                            //}
                        }
                    }
                }
            }
        }
    }

    private double getFinalTerrain(int longitude, int altitude, int latitude) {
        float surfaceVariation = surfaceVariationNoise.getValue(longitude, latitude);
        //float randomVariation = 3*surfaceVariation;
        float temperature = temperatureNoise.getValue(longitude, latitude);
        //double badlands = -1000*(-0.5+Math.max(0.5, temperature));
        //if (HydrolMath.gradient(altitude, (int) (randomVariation+Math.max(110, 256+badlands)), (int) (randomVariation+Math.max(109, 255+badlands)), -1, 1) >= 0) {
        //    return 0;
        //} else {
            float continentalness = continentalnessNoise.getValue(longitude, latitude);
            double horizontalShift = getHorizontalShift(altitude, continentalness, surfaceVariation);

            altitude += HydrolMath.gradient(altitude, 58, 68, 0, 200)*(-0.9+Math.max(0.9, Math.min(0.95, temperature)));

            altitude += -20*(-0.3+Math.max(0.3, Math.min(0.4, continentalness+(0.1*surfaceVariation))));

            double rivers = (Math.abs(-10*(-0.6+Math.min(0.6, Math.max(0.5, temperature))))+(10*(-0.75+Math.min(0.85, Math.max(0.75, temperature)))))*Math.min(0, getRivers(longitude, altitude, latitude, surfaceVariation));

            continentalness = continentalnessNoise.getValue(longitude, latitude);
            altitude += getOceans(longitude, altitude, latitude, continentalness);
            longitude += horizontalShift;
            latitude += horizontalShift;

            surfaceVariation = surfaceVariationNoise.getValue(longitude, latitude);
            temperature = temperatureNoise.getValue(longitude, latitude);
            continentalness = continentalnessNoise.getValue(longitude, latitude);
            float baseValue = baseNoise.getValue(longitude, latitude);
            horizontalShift = getHorizontalShift(altitude, continentalness, surfaceVariation);
            altitude += Math.min(0, surfaceVariation)+((-500*Math.min(0.1, -0.5+Math.max(0.5, temperature)))+(HydrolMath.gradient(altitude, 55, 62, 0, (float) (-130.66*Math.max(-0.3, Math.min(0.3, continentalness))))+(((HydrolMath.gradient(altitude, 69, 88, 0, 3)+(HydrolMath.gradient(altitude, 69, 88, 0, 1)*(Math.max(0, HydrolMath.gradient(altitude, 88, 152, 0, 96*baseValue))+Math.min(0, HydrolMath.gradient(altitude, 152, 256, 0, 152*baseValue)))))*surfaceVariation)+((HydrolMath.gradient(altitude, 22, 59, 5, 0)+(HydrolMath.gradient(altitude, 22, 59, 5, 0)+(HydrolMath.gradient(altitude, 69, 96, 0, 3)+(HydrolMath.gradient(altitude, 96, 148, 0, 96)+HydrolMath.gradient(altitude, 112, 196, 0, 96)))))*(0.01*horizontalShift)))));
            longitude += horizontalShift;
            latitude += horizontalShift;

            baseValue = baseNoise.getValue(longitude, latitude);
            return rivers+getBaseTerrain(altitude, baseValue);
            //return getBaseTerrain(altitude, baseValue);
        //}
    }

    private double getRivers(int longitude, int altitude, int latitude, double surfaceVariation) {
        if (altitude < 66) {
            return 0;
        } else {
            int cliffShift = (int) (HydrolMath.gradient(altitude, 65, 74, 0, 9)*surfaceVariation);
            longitude += cliffShift;
            latitude += cliffShift;
            return 10*((100*getBaseTerrain(62, baseNoise.getValue(longitude, latitude)))+(HydrolMath.gradient(altitude, 66, 72, 0, -5)+(HydrolMath.gradient(altitude, 72, 124, 0, -20)+HydrolMath.gradient(altitude, 124, 256, 0, -20))));
        }
    }

    private double getOceans(int longitude, int altitude, int latitude, float continentalness) {
        if (continentalness > 0.05) {
            return 0;
        } else {
            altitude += HydrolMath.gradient(altitude, 22, 36, 0, -50)*(5*(0.05+Math.min(-0.05, continentalness)));
            return Math.max(0, HydrolMath.gradient(altitude, 60, 72, -48, -308)*Math.max(-1, 15*continentalnessNoise.getValue(longitude, latitude)));
        }
    }

    private double getHorizontalShift(int altitude, float continentalness, float surfaceVariation) {
        return Math.min(1, Math.max(0, continentalness*4))*(HydrolMath.gradient(altitude, 69, 290, 0, 172)+(HydrolMath.gradient(altitude, 78, 116, 0, 16)+HydrolMath.gradient(altitude, 104, 142, 0, -32)))+(HydrolMath.gradient(altitude, 65, 68, 0, 3)*surfaceVariation);
    }

    private double getBaseTerrain(int altitude, float baseValue) {
        if (altitude < 56) {
            return 1;
        } else if (altitude > 196) {
            return -1;
        } else {
            return HydrolMath.gradient(altitude, 62, 196, 0, -1)+(HydrolMath.gradient(altitude, 56, 64, 0, -0.0025f)+Math.abs((HydrolMath.gradient(altitude, 56, 96, 1, 2)*baseValue)*baseValue));
        }
    }
}
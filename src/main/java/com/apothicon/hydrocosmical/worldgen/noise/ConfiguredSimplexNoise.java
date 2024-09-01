package com.apothicon.hydrocosmical.worldgen.noise;

import finalforeach.cosmicreach.worldgen.noise.SimplexNoise;

public class ConfiguredSimplexNoise {
    private final SimplexNoise noise;
    private final float xzScale;
    private final float yScale;

    public ConfiguredSimplexNoise(SimplexNoise noise, float xzScale, float yScale) {
        this.noise = noise;
        this.xzScale = xzScale*0.003f;
        this.yScale = yScale*0.003f;
    }

    public ConfiguredSimplexNoise(SimplexNoise noise, float xzScale) {
        this.noise = noise;
        this.xzScale = xzScale;
        this.yScale = 0;
    }

    public float getValue(int x, int z) {
        return this.noise.noise2(x*xzScale, z*xzScale);
    }

    public float getValue(int x, int y, int z) {
        return this.noise.noise3_XZBeforeY(x*xzScale, y*yScale, z*xzScale);
    }
}
package com.apothicon.cosmicillumination;

import dev.crmodders.cosmicquilt.api.entrypoint.ModInitializer;
import org.quiltmc.loader.api.ModContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CosmicIllumination implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Cosmic Illumination");

	@Override
	public void onInitialize(ModContainer mod) {
		LOGGER.info("Cosmic Illumination Initialized!");
	}
}
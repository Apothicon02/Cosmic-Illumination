package com.apothicon.terraflat;

import dev.crmodders.cosmicquilt.api.entrypoint.ModInitializer;
import org.quiltmc.loader.api.ModContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Terraflat implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Terraflat");

	@Override
	public void onInitialize(ModContainer mod) {
		LOGGER.info("Terraflat Initialized!");
	}
}
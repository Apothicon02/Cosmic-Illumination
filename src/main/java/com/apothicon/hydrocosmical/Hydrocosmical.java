package com.apothicon.hydrocosmical;

import dev.crmodders.cosmicquilt.api.entrypoint.ModInitializer;
import org.quiltmc.loader.api.ModContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hydrocosmical implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Hydrocosmical");
	public static boolean isHydrocosmicalRegistered = false;

	@Override
	public void onInitialize(ModContainer mod) {
		LOGGER.info("Hydrocosmical Initialized!");
	}
}
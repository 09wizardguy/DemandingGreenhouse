package com.o9wizardguy;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemandingGreenhouses implements ModInitializer {
	public static final String MOD_ID = "demandingdreenhouses";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Demanding Greenhouses Mixin initialized!");
	}
}
package diacritics.owo;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import diacritics.owo.config.McaExpandedConfig;

public class McaExpanded implements ModInitializer {
	public static final String MOD_ID = "mca-expanded";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final McaExpandedConfig CONFIG = McaExpandedConfig.createAndLoad();

	@Override
	public void onInitialize() {}
}

package diacritics.owo;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import diacritics.owo.config.Config;

public class MCAUtilities implements ModInitializer {
	public static final String MOD_ID = "mca-utilities";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Config CONFIG = new Config("mca-utilities");

	@Override
	public void onInitialize() {
		// create it if it doesn't exist
		CONFIG.read();
	}
}

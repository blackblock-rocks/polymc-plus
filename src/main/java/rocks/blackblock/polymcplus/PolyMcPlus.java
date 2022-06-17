package rocks.blackblock.polymcplus;

import io.github.theepicblock.polymc.api.PolyMap;
import io.github.theepicblock.polymc.api.PolyMcEntrypoint;
import io.github.theepicblock.polymc.api.PolyRegistry;
import io.github.theepicblock.polymc.api.misc.PolyMapProvider;
import io.github.theepicblock.polymc.impl.generator.Generator;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.blackblock.polymcplus.generator.PolyPlusGenerator;
import rocks.blackblock.polymcplus.polymc.PolyPlusMap;
import rocks.blackblock.polymcplus.polymc.PolyPlusRegistry;
import rocks.blackblock.polymcplus.server.PolyPlusCommands;

import java.util.List;

/**
 * The main class of the mod
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.1.0
 */
public class PolyMcPlus implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("polymc-plus");

	@Override
	public void onInitialize() {

		// Let PolyMC use our custom maps
		PolyMapProvider.EVENT.register(player -> {
			return getRegistry().build();
		});

		PolyPlusCommands.registerCommands();
	}

	private static PolyPlusRegistry main_registry = null;

	public static PolyMap getMainMap() {
		return getRegistry().build();
	}

	public static PolyPlusRegistry getRegistry() {

		if (main_registry != null) {
			return main_registry;
		}

		main_registry = new PolyPlusRegistry();

		// Register default global ItemPolys
		PolyPlusGenerator.addDefaultGlobalItemPolys(main_registry);

		// Let mods register polys via the api
		List<PolyMcEntrypoint> entrypoints = FabricLoader.getInstance().getEntrypoints("polymc", PolyMcEntrypoint.class);
		for (PolyMcEntrypoint entrypointEntry : entrypoints) {
			entrypointEntry.registerPolys(main_registry);
		}

		PolyPlusGenerator.generateMissing(main_registry);

		return main_registry;
	}
}

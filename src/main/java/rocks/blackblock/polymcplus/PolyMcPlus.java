package rocks.blackblock.polymcplus;

import io.github.theepicblock.polymc.api.PolyMap;
import io.github.theepicblock.polymc.api.PolyMcEntrypoint;
import io.github.theepicblock.polymc.api.misc.PolyMapProvider;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.ApiStatus;
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
	private static PolyPlusRegistry mainPolyPlusRegistry = null;
	private static PolyPlusMap mainPolyPlusMap = null;

	/**
	 * Initialize PolyMcPlus
	 *
	 * @author   Jelle De Loecker   <jelle@elevenways.be>
	 * @since    0.1.0
	 */
	@Override
	public void onInitialize() {

		// Let PolyMC use our custom maps
		PolyMapProvider.EVENT.register(player -> getGeneratedMap());

		PolyPlusCommands.registerCommands();
	}

	/**
	 * Builds the poly map, this should only be run when all blocks/items have been registered.
	 * This will be called by PolyMc when the worlds are generated.
	 * @deprecated this is an internal method you shouldn't call.
	 */
	/**
	 * Generate the PolyMap.
	 * This should only be run when all blocks/items have been registered.
	 * This will be called by PolyMcPlus when the worlds are generated.
	 *
	 * @author   Jelle De Loecker   <jelle@elevenways.be>
	 * @since    0.1.0
	 */
	@SuppressWarnings("DeprecatedIsStillUsed")
	@Deprecated
	public static void generatePolyMap() {
		mainPolyPlusMap = getRegistry().build();
	}

	/**
	 * Get the previously generated PolyPlusMap
	 *
	 * @author   Jelle De Loecker   <jelle@elevenways.be>
	 * @since    0.1.0
	 */
	@Deprecated
	@ApiStatus.Internal
	public static PolyPlusMap getGeneratedMap() {
		if (mainPolyPlusMap == null) {
			throw new NullPointerException("Tried to access the PolyPlusMap before it was initialized");
		}
		return mainPolyPlusMap;
	}

	/**
	 * Return the main PolyPlusMap
	 *
	 * @author   Jelle De Loecker   <jelle@elevenways.be>
	 * @since    0.1.0
	 */
	public static PolyMap getMainMap() {
		return getRegistry().build();
	}

	/**
	 * Get/create the PolyPlusRegistry instance
	 *
	 * @author   Jelle De Loecker   <jelle@elevenways.be>
	 * @since    0.1.0
	 */
	public static PolyPlusRegistry getRegistry() {

		if (mainPolyPlusRegistry != null) {
			return mainPolyPlusRegistry;
		}

		mainPolyPlusRegistry = new PolyPlusRegistry();

		// Register default global ItemPolys
		PolyPlusGenerator.addDefaultGlobalItemPolys(mainPolyPlusRegistry);

		// Let mods register polys via the api
		List<PolyMcEntrypoint> entrypoints = FabricLoader.getInstance().getEntrypoints("polymc", PolyMcEntrypoint.class);
		for (PolyMcEntrypoint entrypointEntry : entrypoints) {
			entrypointEntry.registerPolys(mainPolyPlusRegistry);
		}

		PolyPlusGenerator.generateMissing(mainPolyPlusRegistry);

		return mainPolyPlusRegistry;
	}
}

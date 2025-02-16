package rocks.blackblock.polymcplus;

import io.github.theepicblock.polymc.api.PolyMap;
import io.github.theepicblock.polymc.api.PolyMcEntrypoint;
import io.github.theepicblock.polymc.api.misc.PolyMapProvider;
import io.github.theepicblock.polymc.api.resource.ModdedResources;
import io.github.theepicblock.polymc.api.resource.PolyMcResourcePack;
import io.github.theepicblock.polymc.impl.misc.WatchListener;
import io.github.theepicblock.polymc.impl.misc.logging.SimpleLogger;
import io.github.theepicblock.polymc.impl.poly.wizard.PacketCountManager;
import io.github.theepicblock.polymc.impl.resource.ModdedResourceContainerImpl;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.blackblock.bib.util.BibLog;
import rocks.blackblock.polymcplus.generator.PolyPlusGenerator;
import rocks.blackblock.polymcplus.polymc.PolyPlusMap;
import rocks.blackblock.polymcplus.polymc.PolyPlusRegistry;
import rocks.blackblock.polymcplus.server.PolyPlusCommands;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * The main class of the mod
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.1.0
 */
public class PolyMcPlus implements ModInitializer {

	// A reusable ModdedResources container
	private static ModdedResources modded_resources = null;
	public static final BibLog.Categorised LOGGER = BibLog.getCategorised("polymcplus");

	public static final Logger SIMPLE_LOGGER = LoggerFactory.getLogger("polymc-plus");
	private static PolyPlusRegistry mainPolyPlusRegistry = null;
	private static PolyPlusMap mainPolyPlusMap = null;

	/**
	 * Get a ModdedResources container.
	 *
	 * @author   Jelle De Loecker   <jelle@elevenways.be>
	 * @since    0.5.1
	 */
	public static ModdedResources getModdedResources() {

		if (modded_resources == null) {
			modded_resources = new ModdedResourceContainerImpl();
		}

		return modded_resources;
	}

	/**
	 * Release the ModdedResources container.
	 *
	 * @author   Jelle De Loecker   <jelle@elevenways.be>
	 * @since    0.5.1
	 */
	public static void releaseModdedResources() {

		if (modded_resources == null) {
			return;
		}

		try {
			modded_resources.close();
		} catch (Exception e) {
			SIMPLE_LOGGER.error("Failed to close modded resources", e);
		} finally {
			modded_resources = null;
		}
	}

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

		// Make sure the shared modded resources instance
		// (with loaded client jar, probably) is closed
		ServerLifecycleEvents.SERVER_STARTED.register(server1 -> {
			if (modded_resources != null) {
				try {
					modded_resources.close();
				} catch (Exception e) {
					// Ignore
				}
				modded_resources = null;
			}

			// Used to do this at the head of the `createWorlds` call,
			// is it still in-time here?
			PolyMcPlus.generatePolyMap();
		});

		// Blackblock-perf & VMP rewrite a lot of chunk handlers,
		// causing PolyMC to never see the chunk unload event
		ServerChunkEvents.CHUNK_UNLOAD.register((world, chunk) -> ((WatchListener)chunk).polymc$removeAllPlayers());

		// PolyMC starts at a high wizard-restriction level,
		// causing the packets to not be sent the first minute.
		// This works around that by overriding the starting restriction level to 0
		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			if (entity instanceof ServerPlayerEntity player) {
				var manager = PacketCountManager.INSTANCE.getTrackerInfoForPlayer(player);
				if (manager != null) {
					manager.setRestrictionLevel((byte) 0);
				}
			}
		});
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

	/**
	 * Copy an entire folder from the resources into the resource pack
	 *
	 * @author   Jelle De Loecker   <jelle@elevenways.be>
	 * @since    0.4.0
	 */
	@SuppressWarnings("unused")
	public static void copyAssetDirectoryIntoResourcePack(String namespace, String path, Class clazz, PolyMcResourcePack target_pack, SimpleLogger logger) {

		String full_path = "assets/" + namespace + "/" + path + "/";

		final File jar_file = new File(clazz.getProtectionDomain().getCodeSource().getLocation().getPath());

		if (!jar_file.isFile()) {
			logger.warn("Could not find jar file " + jar_file + " in resources");
			return;
		}

		String prefix = "assets/" + namespace + "/";

		try {

			final JarFile jar = new JarFile(jar_file);

			// Get ALL the entires in the jar
			final Enumeration<JarEntry> entries = jar.entries();

			while (entries.hasMoreElements()) {
				final String name = entries.nextElement().getName();

				if (name.startsWith(full_path)) {

					// If the name ends with a slash, it's a directory. Skip it
					if (name.endsWith("/")) {
						continue;
					}

					// Remove the prefix
					String asset_path = name.substring(prefix.length());

					// Get the file url
					URL file_url = clazz.getResource("/" + name);

					target_pack.setAsset(namespace, asset_path, (outputStream, gson) -> {
						try (InputStream inputStream = clazz.getResourceAsStream("/" + name)) {

							if (inputStream == null) {
								logger.warn("Could not find resource " + name + " in jar file " + jar_file);
								return;
							}

							inputStream.transferTo(outputStream);
						}
					});

				}
			}
			jar.close();


		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}

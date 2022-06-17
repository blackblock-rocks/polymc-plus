package rocks.blackblock.polymcplus.server;


import com.mojang.brigadier.Command;
import io.github.theepicblock.polymc.PolyMc;
import io.github.theepicblock.polymc.impl.misc.PolyDumper;
import io.github.theepicblock.polymc.impl.misc.logging.CommandSourceLogger;
import io.github.theepicblock.polymc.impl.misc.logging.ErrorTrackerWrapper;
import io.github.theepicblock.polymc.impl.misc.logging.SimpleLogger;
import io.github.theepicblock.polymc.impl.resource.ResourcePackGenerator;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import rocks.blackblock.polymcplus.PolyMcPlus;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static net.minecraft.server.command.CommandManager.literal;

/**
 * Registers the polyplus commands.
 */
public class PolyPlusCommands {

    public static void registerCommands() {

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("polyplus").requires(source -> source.hasPermissionLevel(2))
                    .then(literal("generate")
                            .then(literal("resources")
                                    .executes((context -> {
                                        SimpleLogger commandSource = new CommandSourceLogger(context.getSource(), true);
                                        ErrorTrackerWrapper logger = new ErrorTrackerWrapper(PolyMc.LOGGER);
                                        try {
                                            commandSource.info("Generating Polyplus resource pack...");
                                            ResourcePackGenerator.generate(PolyMcPlus.getMainMap(), "resource_polyplus", logger);

                                            commandSource.info("Generated Polyplus resource pack. Zipping...");
                                            compressGameMap("resource_polyplus", "resource_polyplus.zip");
                                            commandSource.info("Zipped Polyvalent resource pack.");

                                        } catch (Exception e) {
                                            commandSource.error("An error occurred whilst trying to generate the resource pack! Please check the console.");
                                            e.printStackTrace();
                                            return 0;
                                        }
                                        if (logger.errors != 0) {
                                            commandSource.error("There have been errors whilst generating the resource pack. These are usually completely normal. It only means that PolyMc couldn't find some of the textures or models. See the console for more info.");
                                        }
                                        commandSource.info("Finished generating resource pack");
                                        commandSource.warn("Before hosting this resource pack, please make sure you have the legal right to redistribute the assets inside.");
                                        return Command.SINGLE_SUCCESS;
                                    })))
                            .then(literal("polyDump")
                                    .executes((context) -> {
                                        SimpleLogger logger = new CommandSourceLogger(context.getSource(), true);
                                        try {
                                            PolyDumper.dumpPolyMap(PolyMcPlus.getMainMap(), "PolyplusDump.txt", logger);
                                        } catch (IOException e) {
                                            logger.error(e.getMessage());
                                            return 0;
                                        } catch (Exception e) {
                                            logger.info("An error occurred whilst trying to generate the poly dump! Please check the console.");
                                            e.printStackTrace();
                                            return 0;
                                        }
                                        logger.info("Finished generating poly dump");
                                        return Command.SINGLE_SUCCESS;
                                    }))
                    ));
        });
    }

    public static void compressGameMap(String mapName, String outputName) {
        Path gameDir = FabricLoader.getInstance().getGameDir();
        Path resourcePath = gameDir.resolve(mapName).toAbsolutePath();

        outputName = gameDir.resolve(outputName).toAbsolutePath().toString();

        compress(resourcePath, outputName);
    }

    public static void compress(String sourcePath, String targetPath) {
        final Path sourceDir = Paths.get(sourcePath);
        compress(sourceDir, targetPath);
    }

    public static void compress(Path sourceDir, String targetPath) {
        try {
            final ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(targetPath));

            // Use a TreeSet to ensure the entries are sorted
            TreeSet<Path> paths = new TreeSet<>();

            Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
                    paths.add(file);
                    return FileVisitResult.CONTINUE;
                }
            });

            for (Path file : paths) {
                try {
                    Path targetFile = sourceDir.relativize(file);
                    ZipEntry zip_entry = new ZipEntry(targetFile.toString());

                    // Always set the time to 0 in order to produce identical zip files
                    zip_entry.setTime(0);

                    outputStream.putNextEntry(zip_entry);
                    byte[] bytes = Files.readAllBytes(file);
                    outputStream.write(bytes, 0, bytes.length);
                    outputStream.closeEntry();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

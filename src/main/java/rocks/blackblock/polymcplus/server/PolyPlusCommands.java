package rocks.blackblock.polymcplus.server;


import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.theepicblock.polymc.PolyMc;
import io.github.theepicblock.polymc.api.wizard.VItemFrame;
import io.github.theepicblock.polymc.impl.misc.PolyDumper;
import io.github.theepicblock.polymc.impl.misc.logging.CommandSourceLogger;
import io.github.theepicblock.polymc.impl.misc.logging.ErrorTrackerWrapper;
import io.github.theepicblock.polymc.impl.misc.logging.SimpleLogger;
import io.github.theepicblock.polymc.impl.poly.wizard.AbstractVirtualEntity;
import io.github.theepicblock.polymc.impl.poly.wizard.SinglePlayerView;
import io.github.theepicblock.polymc.impl.resource.ResourcePackGenerator;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import rocks.blackblock.polymcplus.PolyMcPlus;
import rocks.blackblock.polymcplus.wizard.TestVirtualEntity;
import rocks.blackblock.polymcplus.wizard.VArmorStand;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
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
                    )
                    .then(literal("spawn-fps-test")
                            .then(CommandManager.argument("type", StringArgumentType.string())
                                    .executes(context -> {

                                        ServerCommandSource source = context.getSource();
                                        ServerPlayerEntity player = source.getPlayer();

                                        if (player == null) {
                                            source.sendError(Text.literal("You must be a player to execute this command."));
                                            return 0;
                                        }

                                        SinglePlayerView consumer = new SinglePlayerView(player);

                                        String scope_slug = StringArgumentType.getString(context, "type");

                                        for (int i = 0; i < 500; i++) {
                                            AbstractVirtualEntity entity;

                                            if (scope_slug.equals("armorstand")) {
                                                entity = new VArmorStand();
                                            } else if (scope_slug.equals("itemframe")) {
                                                entity = new VItemFrame();
                                            } else {
                                                source.sendError(Text.literal("Type must be 'armorstand' or 'itemframe'."));
                                                return 0;
                                            }


                                            entity.spawn(consumer, player.getPos());
                                            entity.setNoGravity(consumer, true);

                                            if (entity instanceof VArmorStand vArmorStand) {
                                                vArmorStand.makeInvisible(consumer);
                                                vArmorStand.sendArmorStandFlags(consumer, false, false, false, true);
                                                vArmorStand.sendSingleSlot(consumer, EquipmentSlot.HEAD, Items.STONE.getDefaultStack());
                                                vArmorStand.move(consumer, player.getPos().add(0.1f * i, 0.1f * i, 0.1f*i), 0, 0, false);
                                            } else if (entity instanceof VItemFrame vItemFrame) {
                                                //vItemFrame.makeInvisible(consumer);
                                                vItemFrame.sendItemStack(consumer, Items.STONE.getDefaultStack());
                                                vItemFrame.move(consumer, player.getPos().add(0.1f * i, 0.1f * i, 0.1f*i), 0, 0, false);
                                            }
                                        }

                                        return 1;
                                    })
                            )

                    )
                    .then(literal("spawn-virtual-entity")
                            .then(CommandManager.argument("entity_id", StringArgumentType.string())
                                    .executes(context -> {

                                        ServerCommandSource source = context.getSource();
                                        ServerPlayerEntity player = source.getPlayer();

                                        if (player == null) {
                                            source.sendError(Text.literal("You must be a player to execute this command."));
                                            return 0;
                                        }

                                        SinglePlayerView consumer = new SinglePlayerView(player);

                                        String entity_type_name = StringArgumentType.getString(context, "entity_id");

                                        EntityType<?> entity_type = Registry.ENTITY_TYPE.get(Identifier.tryParse(entity_type_name));

                                        if (entity_type == null) {
                                            source.sendError(Text.literal("Could not find an entity with the id '" + entity_type_name + "'"));
                                            return 0;
                                        }

                                        TestVirtualEntity entity = new TestVirtualEntity(entity_type);
                                        entity.spawn(consumer, player.getPos());
                                        entity.setNoGravity(consumer, true);

                                        return 1;
                                    })
                            )
                    )
                    .then(literal("generate-all-blockstates-and-destroy-landscape").executes(context -> {

                        ServerCommandSource source = context.getSource();
                        ServerPlayerEntity player = source.getPlayer();
                        ServerWorld world = player.getWorld();

                        if (!player.isCreative()) {
                            source.sendError(Text.literal("You have to do this in Creative mode. This will ruin these chunks! Don't do it on live environment."));
                            return 0;
                        }

                        List<BlockState> all_states = new ArrayList<>();

                        for (Block block : Registry.BLOCK.stream().toList()) {
                            for (BlockState state : block.getStateManager().getStates()) {
                                all_states.add(state);
                            }
                        }

                        int state_count = all_states.size();
                        int count = -1;
                        BlockPos player_pos = player.getBlockPos();
                        BlockPos pos;

                        int air_blocks = 0;

                        // Make everything air first
                        for (int x = -800; x < 1600; x++) {
                            for (int y = 0; y <= 90; y++) {

                                int new_y = player_pos.getY() + y;

                                if (new_y > 320) {
                                    continue;
                                }

                                for (int z = -34; z < 68; z++) {
                                    pos = player_pos.add(x, y, z);

                                    if (y == 0) {
                                        world.setBlockState(pos, Blocks.STONE.getDefaultState());
                                    } else {
                                        air_blocks++;
                                        world.setBlockState(pos, Blocks.AIR.getDefaultState());
                                    }
                                }
                            }
                        }

                        source.sendFeedback(Text.literal("Set " + air_blocks + " blocks to air. Doing BlockStates next"), false);

                        // And now paste all the blockstates
                        for (int x = -799; x < 1590; x += 3) {
                            for (int z = -32; z < 64; z += 3) {
                                pos = player_pos.add(x, 1, z);

                                BlockState state = all_states.get(count + 1);

                                if (state == null) {
                                    break;
                                }

                                count++;

                                try {

                                    if (state.contains(Properties.WATERLOGGED)) {
                                        boolean is_waterlogged = state.get(Properties.WATERLOGGED);

                                        if (is_waterlogged) {
                                            BlockState barrier = Blocks.BARRIER.getDefaultState();
                                            world.setBlockState(pos.add(-1, 0, 0), barrier,Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
                                            world.setBlockState(pos.add(1, 0, 0), barrier,Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
                                            world.setBlockState(pos.add(0, 0, -1), barrier,Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
                                            world.setBlockState(pos.add(0, 0, 1), barrier,Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
                                        }
                                    }

                                    world.setBlockState(pos, state,Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
                                } catch (Exception e) {
                                    System.out.println("Failed to set state " + state + " at " + pos);
                                }
                            }
                        }

                        source.sendFeedback(Text.literal("Generated " + count + " blockstates into the world of a total of " + state_count), false);

                        return 1;
                    }))
            );
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

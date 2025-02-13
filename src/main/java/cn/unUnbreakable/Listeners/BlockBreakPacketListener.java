package cn.unUnbreakable.Listeners;

import cn.unUnbreakable.Main;
import cn.unUnbreakable.Managers.PlayerDigging;
import cn.unUnbreakable.Utils.EventUtils;
import cn.unUnbreakable.block.Degree;
import cn.unUnbreakable.block.EndPortalFrame;
import cn.unUnbreakable.block.UnbreakableBlock;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class BlockBreakPacketListener extends PacketListenerAbstract {
    private final Main plugin;
    private final List<BlockFace> blockFaces;

    public BlockBreakPacketListener(Main main) {
        super(PacketListenerPriority.NORMAL);
        this.plugin = main;
        this.blockFaces = Arrays.asList(BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            Player player = event.getPlayer();
            if (player.getGameMode() != GameMode.SURVIVAL) return;
            WrapperPlayClientPlayerDigging playerDiggingPacket = new WrapperPlayClientPlayerDigging(event);
            Vector3i blockPosition = playerDiggingPacket.getBlockPosition();
            World world = player.getWorld();
            Block block = world.getBlockAt(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
            Location location = block.getLocation();
            if (playerDiggingPacket.getAction() == DiggingAction.START_DIGGING) {
                if (PlayerDigging.isBreaking(location)) return;
                UnbreakableBlock unbreakableBlock = plugin.getBlockManager().getBlock(block.getType());
                if (unbreakableBlock == null) return;
                ItemStack tools = player.getInventory().getItemInMainHand();
                int delay = unbreakableBlock.getBreakTime(tools.getType());
                if (delay < 0) return;

                BukkitTask task = new BukkitRunnable() {
                    byte stage = 0;

                    @Override
                    public void run() {
                        if (!PlayerDigging.isBreaking(location)) {
                            cancel();
                            return;
                        }
                        if (stage > 9) {
                            PlayerDigging.cancel(location);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                                if (EventUtils.callEvent(new BlockBreakEvent(block, player))) {
                                    block.breakNaturally(tools, true);
                                    Map<Material, Degree> drops = unbreakableBlock.getDrops();
                                    if (!drops.isEmpty()) {
                                        drops.forEach((k, v) -> {
                                            int amount = v.getRandom();
                                            if (amount > 0) {
                                                world.dropItemNaturally(location, new ItemStack(k, amount));
                                            }
                                        });
                                    }
                                    if (unbreakableBlock instanceof EndPortalFrame endPortalFrame) {
                                        if (endPortalFrame.isBreakFrame()) {
                                            frameBreak(block);
                                        }
                                    }
                                }
                                for (Player p : world.getNearbyPlayers(location, 16, 16, 16)) {
                                    p.sendBlockDamage(location, 0, location.hashCode());
                                }
                                PlayerDigging.remove(location);
                            });
                            return;
                        }
                        stage++;
                        for (Player p : world.getNearbyPlayers(location, 16, 16, 16)) {
                            p.sendBlockDamage(location, (float) stage / 10, location.hashCode());
                        }
                    }
                }.runTaskTimer(plugin, 0, delay);

                PlayerDigging.add(location, task);
            } else {
                if (!PlayerDigging.isBreaking(location)) return;
                PlayerDigging.cancel(location);
                PlayerDigging.remove(location);
                Bukkit.getScheduler().runTask(plugin, () -> {
                    for (Player p : world.getNearbyPlayers(location, 16, 16, 16)) {
                        p.sendBlockDamage(location, 0, location.hashCode());
                    }
                });
            }
        }
    }


    private void frameBreak(Block startBlock) {
        List<Block> blockList = new ArrayList<>();
        Set<Location> visited = new HashSet<>();
        visited.add(startBlock.getLocation());

        for (BlockFace face : blockFaces) {
            Block adjacentBlock = startBlock.getRelative(face);
            visited.add(adjacentBlock.getLocation());
            if (adjacentBlock.getType() == Material.END_PORTAL) {
                blockList.add(adjacentBlock);
            }
        }

        if (blockList.isEmpty()) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                Block currentBlock = blockList.removeFirst();
                currentBlock.breakNaturally(true);
                for (BlockFace face : blockFaces) {
                    Block adjacentBlock = currentBlock.getRelative(face);
                    if (!visited.contains(adjacentBlock.getLocation())) {
                        visited.add(adjacentBlock.getLocation());
                        if (adjacentBlock.getType() == Material.END_PORTAL) {
                            blockList.add(adjacentBlock);
                        }
                    }
                }
                if (blockList.isEmpty()) {
                    cancel();
                    visited.clear();
                    currentBlock.getWorld().playSound(currentBlock.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 3F, 0.5F);
                }
            }
        }.runTaskTimer(plugin, 1, 1);
    }
}


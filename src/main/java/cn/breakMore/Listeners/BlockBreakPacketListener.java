package cn.breakMore.Listeners;

import cn.breakMore.Main;
import cn.breakMore.Managers.PlayerDigging;
import cn.breakMore.Utils.EventUtils;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class BlockBreakPacketListener extends PacketListenerAbstract {
    private final Main plugin;


    public BlockBreakPacketListener(Main main) {
        super(PacketListenerPriority.NORMAL);
        this.plugin = main;
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {

        if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            Player player = event.getPlayer();
            if (player.getGameMode() == GameMode.CREATIVE) return;
            WrapperPlayClientPlayerDigging playerDigging = new WrapperPlayClientPlayerDigging(event);
            Vector3i blockPosition = playerDigging.getBlockPosition();
            Block block = player.getWorld().getBlockAt(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
            Integer delay = plugin.getBlockManager().get(block.getType());
            if (delay < 0) return;

            Location location = block.getLocation();
            if (playerDigging.getAction() == DiggingAction.START_DIGGING) {
                if (PlayerDigging.isBreaking(location)) return;
                BukkitTask task = new BukkitRunnable() {
                    byte stage = 0;

                    @Override
                    public void run() {
                        if (!PlayerDigging.isBreaking(location)) {
                            cancel();
                            return;
                        }
                        if (stage >= 10) {
                            PlayerDigging.cancel(location);
                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                if (EventUtils.callEvent(new BlockBreakEvent(block, player))) {
                                    block.breakNaturally(player.getInventory().getItemInMainHand(), true);
                                }
                                PlayerDigging.remove(location);
                            }, 1);
                            return;
                        }
                        for (Entity e : player.getWorld().getNearbyEntities(location, 16, 16, 16)) {
                            if (e instanceof Player p) {
                                PlayerDigging.sendBreakAnimation(p, blockPosition, stage);
                            }
                        }
                        stage++;
                    }
                }.runTaskTimer(plugin, 0, delay);
                PlayerDigging.add(location, task);
            } else {
                PlayerDigging.cancel(location);
                PlayerDigging.remove(location);
                Bukkit.getScheduler().runTask(plugin, () -> {
                    for (Entity e : player.getWorld().getNearbyEntities(location, 16, 16, 16)) {
                        if (e instanceof Player p) {
                            PlayerDigging.sendBreakAnimation(p, blockPosition, (byte) 10);
                        }
                    }
                });
            }


        }


    }
}


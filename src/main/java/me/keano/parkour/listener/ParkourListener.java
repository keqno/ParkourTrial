package me.keano.parkour.listener;

import me.keano.parkour.manager.ParkourManager;
import me.keano.parkour.manager.object.ParkourCountObject;
import me.keano.parkour.manager.object.ParkourStoreObject;
import me.keano.parkour.utils.CC;
import me.keano.parkour.utils.Formatter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ParkourListener implements Listener {

    private final Map<UUID, ParkourCountObject> map;
    private final ParkourManager manager;

    public ParkourListener(ParkourManager manager) {
        this.map = new HashMap<>();
        this.manager = manager;
    }

    @EventHandler
    public void onWater(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Location to = e.getTo();

        if (to == null) return;
        if (!map.containsKey(player.getUniqueId())) return;
        if (to.getBlock().getType() != Material.WATER) return; // if they are in water take back to checkpoint

        ParkourCountObject object = map.get(player.getUniqueId());
        player.sendMessage(CC.t("&aTeleported back to checkpoint!"));

        float pitch = player.getLocation().getPitch();
        float yaw = player.getLocation().getYaw();

        // if they haven't reached a checkpoint yet
        if (object.getCheckpoint() == null) {
            player.teleport(manager.getStart());

        } else {
            player.teleport(object.getCheckpoint());
        }

        // make sure they look the same way
        player.setFallDistance(0.0F); // so they don't take fall damage
        player.getLocation().setYaw(yaw);
        player.getLocation().setPitch(pitch);
    }

    @EventHandler // fix memory leaks
    public void onQuit(PlayerQuitEvent e) {
        map.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler // fix memory leaks
    public void onDie(PlayerDeathEvent e) {
        map.remove(e.getEntity().getUniqueId());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Location to = e.getTo();
        boolean newRecord = false;

        if (to == null) return;

        if (!map.containsKey(player.getUniqueId())) {
            if (isSimilar(to, manager.getStart())) {
                map.put(player.getUniqueId(), new ParkourCountObject(player.getUniqueId(), player.getName()));
                player.sendMessage(CC.t("&aStarted parkour!"));
            }
            return; // return anyways
        }

        for (Location checkpoint : manager.getCheckpoints()) {
            if (isSimilar(to, checkpoint)) {
                ParkourCountObject object = map.get(player.getUniqueId());
                object.setCheckpoint(checkpoint); // update checkpoint
            }
        }

        if (isSimilar(to, manager.getEnd())) {
            // get ready to store this
            ParkourStoreObject storeObject = new ParkourStoreObject(
                    player.getUniqueId(),
                    player.getName(),
                    map.get(player.getUniqueId()).getTime()
            );

            map.remove(player.getUniqueId()); // remove from the temp map

            if (manager.getFinishedParkours().containsKey(player.getUniqueId())) {
                ParkourStoreObject old = manager.getFinishedParkours().get(player.getUniqueId());

                // Don't override the old object if the time took was less
                if (old.getTime() < storeObject.getTime()) {
                    player.sendMessage(CC.t("&eCongratulations! You finished in &a" + Formatter
                            .getRemaining(storeObject.getTime(), true)
                            + " &ebut it didn't count due to you having a lower time before."));
                    return;

                } else {
                    newRecord = true;
                }
            }

            manager.getFinishedParkours().put(player.getUniqueId(), storeObject);
            manager.getInstance().getMongoManager().save(storeObject);

            player.sendMessage(CC.t("&eCongratulations! You finished in &a" + Formatter.getRemaining(
                    storeObject.getTime(), true)
                    + "&e. " + (newRecord ? "(New record)" : ""))
            );
        }
    }

    // Location#equals checks yaw and pitch aswell.
    private boolean isSimilar(Location location, Location location1) {
        return location.getWorld() == location1.getWorld() &&
                // Uses Math#abs for consistency
                Math.abs(location.getBlockX()) == Math.abs(location1.getBlockX()) &&
                Math.abs(location.getBlockY()) == Math.abs(location1.getBlockY()) &&
                Math.abs(location.getBlockZ()) == Math.abs(location1.getBlockZ());
    }
}
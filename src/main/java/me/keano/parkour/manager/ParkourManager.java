package me.keano.parkour.manager;

import lombok.Getter;
import lombok.Setter;
import me.keano.parkour.Parkour;
import me.keano.parkour.listener.ParkourListener;
import me.keano.parkour.manager.command.ParkourCommand;
import me.keano.parkour.manager.object.ParkourStoreObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class ParkourManager {

    private final Parkour instance;
    private final Map<UUID, ParkourStoreObject> finishedParkours;
    private final List<Location> checkpoints;

    private Location start;
    private Location end;
    private List<ParkourStoreObject> sorted;

    public ParkourManager(Parkour instance) {
        this.instance = instance;

        this.finishedParkours = new HashMap<>();
        this.checkpoints = new ArrayList<>();
        this.sorted = new ArrayList<>();

        this.load();
        this.sort();
        this.registerCommand();

        // Remove returns the object
        this.start = checkpoints.remove(0);
        this.end = checkpoints.remove(checkpoints.size() - 1);

        instance.getServer().getPluginManager().registerEvents(new ParkourListener(this), instance);
    }

    // Command map isn't accessible on 1.17 for some reason - keano
    private void registerCommand() {
        try {

            Server bukkit = Bukkit.getServer();
            Method method = bukkit.getClass().getMethod("getCommandMap");
            ((CommandMap) method.invoke(bukkit)).register("trial", new ParkourCommand(this));

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void sort() {
        new BukkitRunnable() {
            @Override
            public void run() {
                setSorted(new ArrayList<>(finishedParkours.values())
                        .stream()
                        .sorted(Comparator.comparingLong(ParkourStoreObject::getTime))
                        .collect(Collectors.toList()));
            }
        }.runTaskTimer(getInstance(), 0L, 20 * 30); // every 30s we sort
    }
    
    // I couldn't find a better way - keano
    @SuppressWarnings("unchecked")
    private void load() {
        // It's a list of maps?
        List<Map<String, Object>> maps = (List<Map<String, Object>>) instance.getJsonFile().getValues().get("checkpointsData");

        for (Map<String, Object> map : maps) {
            checkpoints.add(new Location(
                    Bukkit.getWorld((String) map.get("worldName")),
                    (double) map.get("x"),
                    (double) map.get("y"),
                    (double) map.get("z")
            ));
        }
    }
}
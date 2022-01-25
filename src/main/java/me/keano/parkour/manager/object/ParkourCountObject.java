package me.keano.parkour.manager.object;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.UUID;

@Getter
@Setter
public class ParkourCountObject {

    private UUID player;
    private String playerName;
    private Location checkpoint;
    private long start;

    public ParkourCountObject(UUID player, String playerName) {
        this.player = player;
        this.playerName = playerName;
        this.checkpoint = null;
        this.start = System.currentTimeMillis();
    }

    public long getTime() {
        return System.currentTimeMillis() - start;
    }
}
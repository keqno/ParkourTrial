package me.keano.parkour.manager.object;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
/*
Used to store the time it took for a player to finish
 */
public class ParkourStoreObject {

    private UUID player;
    private String playerName;
    private long time;

    public ParkourStoreObject(UUID player, String playerName, long time) {
        this.player = player;
        this.playerName = playerName;
        this.time = time;
    }
}
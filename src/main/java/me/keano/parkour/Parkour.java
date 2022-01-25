package me.keano.parkour;

import lombok.Getter;
import me.keano.parkour.board.BoardManager;
import me.keano.parkour.manager.ParkourManager;
import me.keano.parkour.mongo.MongoManager;
import me.keano.parkour.utils.JsonFile;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Parkour extends JavaPlugin {

    @Getter
    private static Parkour instance;

    private JsonFile jsonFile;
    private BoardManager boardManager;
    private ParkourManager parkourManager;
    private MongoManager mongoManager;

    @Override
    public void onEnable() {
        instance = this;
        this.jsonFile = new JsonFile(this, getDataFolder(), "locations.json");
        this.boardManager = new BoardManager(this);
        this.parkourManager = new ParkourManager(this);
        this.mongoManager = new MongoManager(this);
    }

    @Override
    public void onDisable() {
        parkourManager.getFinishedParkours().forEach((uuid, object) -> mongoManager.save(object));
    }
}
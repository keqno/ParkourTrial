package me.keano.parkour.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import me.keano.parkour.Parkour;
import me.keano.parkour.manager.object.ParkourStoreObject;
import org.bson.Document;

import java.util.UUID;

public class MongoManager {

    private final Parkour instance;
    private final MongoCollection<Document> collection;

    public MongoManager(final Parkour instance) {
        this.instance = instance;
        this.collection = this.connect();
        this.load();
    }

    public MongoCollection<Document> connect() {
        try {

            MongoClient client = MongoClients.create("mongodb://127.0.0.1:27017");
            MongoDatabase db = client.getDatabase(instance.getName());

            return db.getCollection("ParkourData");

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void load() {
        for (Document document : collection.find()) {
            ParkourStoreObject object = new ParkourStoreObject(
                    UUID.fromString(document.getString("_id")),
                    document.getString("name"),
                    document.getLong("time")
            );

            instance.getParkourManager().getFinishedParkours().put(object.getPlayer(), object);
        }
    }

    public void save(ParkourStoreObject object) {
        Document document = new Document("_id", object.getPlayer().toString());

        document.put("name", object.getPlayerName());
        document.put("time", object.getTime());

        // Will replace if it exists otherwise just save it.
        collection.replaceOne(
                Filters.eq("_id", object.getPlayer().toString()),
                document,
                new ReplaceOptions().upsert(true)
        );
    }
}
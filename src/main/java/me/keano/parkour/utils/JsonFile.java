package me.keano.parkour.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import me.keano.parkour.Parkour;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

@Getter
public class JsonFile {

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private final File config;
    private Map<String, Object> values;

    public JsonFile(Parkour instance, File dataFolder, String name) {
        this.config = new File(dataFolder, name);
        this.values = new HashMap<>();
        instance.saveResource(name, false); // load the config
        this.load();
    }

    public void load() {
        try {

            this.values = gson.fromJson(new FileReader(config), Map.class);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {

            String json = gson.toJson(values);
            config.delete();
            Files.write(config.toPath(), json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
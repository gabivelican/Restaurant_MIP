package ro.unitbv.restaurant;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;

public class MenuExporter {

    public static void export(Menu menu, String path) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(menu);

            FileWriter writer = new FileWriter(path);
            writer.write(json);
            writer.close();

            System.out.println("Meniu exportat cu succes în: " + path);

        } catch (IOException e) {
            System.out.println("Eroare la salvarea meniului în JSON!");
        }
    }
}

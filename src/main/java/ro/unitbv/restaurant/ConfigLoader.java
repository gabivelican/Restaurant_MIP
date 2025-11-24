package ro.unitbv.restaurant;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class ConfigLoader {

    public static AppConfig load(String path) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(new FileReader(path), AppConfig.class);

        } catch (FileNotFoundException e) {
            System.out.println("Eroare: Fișierul de configurare nu a fost găsit!");
            return null;

        } catch (JsonSyntaxException e) {
            System.out.println("Eroare: Format JSON invalid în fișierul de configurare!");
            return null;
        }
    }
}

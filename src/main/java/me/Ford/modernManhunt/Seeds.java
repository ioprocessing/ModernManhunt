package me.Ford.modernManhunt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class Seeds {
    public static final Set<Long> seedSet = new HashSet<>();
    static {
        InputStream stream = Seeds.class.getResourceAsStream("/seeds.txt");

        if (stream == null) {
            System.err.println("Failed to load seeds.txt from resources.");
        } else {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                while ((line = reader.readLine()) != null) {
                    try {
                        seedSet.add(Long.parseLong(line.trim()));
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid seed in file: " + line);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading seeds.txt: " + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                    stream.close();
                } catch (IOException e) {
                    System.err.println("Error closing stream: " + e.getMessage());
                }
            }
        }
    }
}

package org.ioprocessing.modernManhunt.worlds;

import org.ioprocessing.modernManhunt.Core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class SeedLoader {
    public static final Set<Long> seedSet = new HashSet<>();
    static {
        InputStream stream = SeedLoader.class.getResourceAsStream("/seeds.txt");

        if (stream == null) {
            Core.logger().error("Failed to load seeds.txt from resources");
        } else {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                while ((line = reader.readLine()) != null) {
                    try {
                        seedSet.add(Long.parseLong(line.trim()));
                    } catch (NumberFormatException e) {
                        Core.logger().error("Invalid seed in file: {}", line);
                    }
                }
            } catch (IOException e) {
                Core.logger().error("Error reading seeds.txt: {}", e.getMessage());
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                    stream.close();
                } catch (IOException e) {
                    Core.logger().error("Error closing stream: {}", e.getMessage());
                }
            }
        }
    }
}
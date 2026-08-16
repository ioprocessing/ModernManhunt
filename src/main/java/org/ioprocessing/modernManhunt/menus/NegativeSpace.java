package org.ioprocessing.modernManhunt.menus;

import net.kyori.adventure.text.Component;

public class NegativeSpace {
    private NegativeSpace() { /* prevent instantiation */ }

    public static Component intToSpace(int space) {
        if (space < -8192 || space > 8192) {
            throw new IllegalArgumentException("Space value must be between -8192 and 8192 inclusive.");
        }

        // Calculate the character code
        int codePoint = 0xD0000 + space;

        String unicode = new String(Character.toChars(codePoint));

        return Component.text(unicode);
    }
}

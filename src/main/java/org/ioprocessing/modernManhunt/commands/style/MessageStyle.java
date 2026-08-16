package org.ioprocessing.modernManhunt.commands.style;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public enum MessageStyle {

    ERROR("✘", NamedTextColor.RED, TextDecoration.BOLD),
    WARN("⚠", NamedTextColor.YELLOW, null),
    SUCCESS("✔", NamedTextColor.GREEN, TextDecoration.BOLD),
    INFO("ℹ", NamedTextColor.AQUA, TextDecoration.BOLD),
    HARM("☠", NamedTextColor.RED, null),
    HEAL("❤", NamedTextColor.GREEN, null),
    OPERATOR("[OP]", NamedTextColor.YELLOW, TextDecoration.BOLD);

    private final String icon;
    private final NamedTextColor highlightColor;
    private final TextDecoration decoration;

    MessageStyle(String icon, NamedTextColor highlightColor, TextDecoration decoration) {
        this.icon = icon;
        this.highlightColor = highlightColor;
        this.decoration = decoration;
    }

    public String icon() {
        return icon;
    }

    public NamedTextColor highlightColor() {
        return highlightColor;
    }

    public TextDecoration decoration() { return decoration; }
}

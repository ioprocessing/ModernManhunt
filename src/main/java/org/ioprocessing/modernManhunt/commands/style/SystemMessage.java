package org.ioprocessing.modernManhunt.commands.style;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import javax.annotation.Nullable;

/**
 * Utility class for constructing consistently formatted system messages.
 *
 * <p>Messages follow a common pattern:
 * <ul>
 *     <li>A leading icon in a highlight color</li>
 *     <li>Default gray body text</li>
 *     <li>Highlighted keywords marked using {@code {braces}}</li>
 * </ul>
 *
 * <p>This class is intentionally lightweight and does not depend on MiniMessage.
 */
public final class SystemMessage {

    private static final NamedTextColor DEFAULT_TEXT_COLOR = NamedTextColor.GRAY;

    private SystemMessage() {}

    public static Component warn(String pattern) {
        return build(MessageStyle.WARN, pattern);
    }

    public static Component error(String pattern) {
        return build(MessageStyle.ERROR, pattern);
    }

    public static Component success(String pattern) {
        return build(MessageStyle.SUCCESS, pattern);
    }

    public static Component info(String pattern) {
        return build(MessageStyle.INFO, pattern);
    }

    public static Component harm(String pattern) {
        return build(MessageStyle.HARM, pattern);
    }

    public static Component heal(String pattern) {
        return build(MessageStyle.HEAL, pattern);
    }

    public static Component op(String pattern) {return build(MessageStyle.OPERATOR, pattern);}

    /**
     * Builds a system message using a custom emoji and highlight color.
     *
     * <p>This method is intended for rare or module-specific cases where none of
     * the predefined {@link MessageStyle} values apply.
     *
     * @param emoji          the emoji or symbol prefix
     * @param highlightColor the color used for the emoji and highlighted text
     * @param pattern        the message pattern, using {@code {}} to mark highlights
     * @param decoration     the text decoration to format the emoji with
     * @return the constructed message component
     */
    public static Component custom(
            String emoji,
            NamedTextColor highlightColor,
            String pattern,
            TextDecoration decoration
    ) {
        return buildCustom(emoji, highlightColor, pattern, decoration);
    }

    ///  Core Builders ///

    private static Component build(MessageStyle style, String pattern) {
        return buildCustom(style.icon(), style.highlightColor(), pattern, style.decoration());
    }

    private static Component buildCustom(
            String emoji,
            NamedTextColor highlightColor,
            String pattern,
            @Nullable TextDecoration decoration
    ) {
        TextComponent.Builder out = Component.text();

        if(decoration != null)
            out.append(Component.text(emoji + " ", highlightColor, decoration));
        else
            out.append(Component.text(emoji + " ", highlightColor));
        appendPattern(out, pattern, highlightColor);

        return out.build();
    }

    ///  Pattern Parsing ///

    /**
     * Appends a formatted pattern to a component builder.
     *
     * <p>Text enclosed in {@code {}} is rendered using the highlight color.
     * All other text is rendered using the default gray color.
     *
     * @param out            the component builder to append to
     * @param pattern        the raw message pattern
     * @param highlightColor the color used for highlighted segments
     */
    private static void appendPattern(
            TextComponent.Builder out,
            String pattern,
            NamedTextColor highlightColor
    ) {
        int index = 0;

        while (index < pattern.length()) {
            int open = pattern.indexOf('{', index);
            if (open == -1) {
                out.append(Component.text(
                        pattern.substring(index),
                        DEFAULT_TEXT_COLOR
                ));
                return;
            }

            int close = pattern.indexOf('}', open);
            if (close == -1) {
                out.append(Component.text(
                        pattern.substring(index),
                        DEFAULT_TEXT_COLOR
                ));
                return;
            }

            if (open > index) {
                out.append(Component.text(
                        pattern.substring(index, open),
                        DEFAULT_TEXT_COLOR
                ));
            }

            out.append(Component.text(
                    pattern.substring(open + 1, close),
                    highlightColor
            ));

            index = close + 1;
        }
    }
}


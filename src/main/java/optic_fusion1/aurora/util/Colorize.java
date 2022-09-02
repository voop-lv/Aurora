package optic_fusion1.aurora.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;

public final class Colorize {

    private static final Pattern COLOR_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");

    private Colorize() {
    }

    public static String colorize(String string) {
        for (Matcher matcher = COLOR_PATTERN.matcher(string); matcher.find(); matcher = COLOR_PATTERN.matcher(string)) {
            String color = string.substring(matcher.start(), matcher.end());
            string = string.replace(color, net.md_5.bungee.api.ChatColor.of(color) + "");
        }
        return ChatColor.translateAlternateColorCodes('&', string);
    }

}

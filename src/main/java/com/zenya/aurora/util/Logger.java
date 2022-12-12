package com.zenya.aurora.util;

import com.zenya.aurora.Aurora;
import org.bukkit.Bukkit;
import java.util.logging.Level;

public final class Logger {

    private static final Aurora AURORA = Aurora.getPlugin(Aurora.class);
    
    private Logger() {
    }

    public static void logInfo(String message, Object... args) {
        log(Level.INFO, message, args);
    }

    public static void logError(String message, Object... args) {
        log(Level.SEVERE, message, args);
    }

    private static void log(Level level, String message, Object... args) {
        if (args != null && args.length > 0) {
            message = String.format(message, args);
        }

        if (AURORA.getLogger().isLoggable(level)) {
            Bukkit.getConsoleSender().sendMessage(String.format("§7[§b%s§7] %s", "Aurora",
                    adjustResetFormat("§r" + message, level == Level.SEVERE ? "§c" : "§f")
            ));
        }
    }

    private static String adjustResetFormat(String message, String append) {
        return message.replaceAll("§r", "§r" + append);
    }
}

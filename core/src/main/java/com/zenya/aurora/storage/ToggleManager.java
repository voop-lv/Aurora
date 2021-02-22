package com.zenya.aurora.storage;

import java.util.HashMap;

public class ToggleManager {
    public static final ToggleManager INSTANCE = new ToggleManager();
    private HashMap<String, Boolean> toggleMap = new HashMap<>();

    public Boolean isToggled(String playerName) {
        return toggleMap.getOrDefault(playerName, true);
    }

    public void registerToggle(String playerName, Boolean enabled) {
        toggleMap.put(playerName, enabled);
    }

    public void unregisterToggle(String playerName) {
        toggleMap.remove(playerName);
    }
}



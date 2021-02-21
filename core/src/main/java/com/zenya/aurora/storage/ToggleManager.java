package com.zenya.aurora.storage;

import java.util.HashMap;

public class ToggleManager {
    private static ToggleManager toggleManager;
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

    public static ToggleManager getInstance() {
        if(toggleManager == null) {
            toggleManager = new ToggleManager();
        }
        return toggleManager;
    }
}



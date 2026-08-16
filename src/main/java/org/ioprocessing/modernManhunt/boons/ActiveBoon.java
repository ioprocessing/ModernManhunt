package org.ioprocessing.modernManhunt.boons;

import java.util.HashMap;
import java.util.Map;

public class ActiveBoon {

    private final BoonType type;
    private final Map<String, Object> savedState;

    public ActiveBoon(BoonType type) {
        this.type = type;
        this.savedState = new HashMap<>();
    }

    public BoonType getType() { return type; }

    public Map<String, Object> getSavedState() { return savedState; }
}

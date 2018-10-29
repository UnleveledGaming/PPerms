package com.github.gizmo0320.PowerfulPermsAPI;

public class Event {
    protected String name;

    public String getEventName() {
        if (name == null) {
            name = getClass().getSimpleName();
        }
        return name;
    }
}

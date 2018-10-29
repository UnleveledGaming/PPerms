package com.github.gizmo0320.PowerfulPermsAPI;

public interface EventHandler {

    public void registerListener(PowerfulPermsListener listener);

    public void unregisterListener(PowerfulPermsListener listener);

    public void fireEvent(Event event);

}

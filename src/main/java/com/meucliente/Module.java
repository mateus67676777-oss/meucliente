package com.meucliente.module;

public class Module {

    public enum Category { COMBAT, VISUALS, MOVEMENT, PLAYER, HUD }

    private final String name;
    private final Category category;
    private boolean enabled;

    public Module(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    public String getName() { return name; }
    public Category getCategory() { return category; }
    public boolean isEnabled() { return enabled; }

    public void toggle() { setEnabled(!enabled); }

    public void setEnabled(boolean value) {
        if (value == enabled) return;
        enabled = value;
        if (value) onEnable(); else onDisable();
    }

    /** A seta verde so aparece quando isto retorna true (bloco 2). */
    public boolean hasSettings() { return false; }

    protected void onEnable() { }
    protected void onDisable() { }
}

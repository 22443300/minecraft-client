package dev.phantom.client.core.module;

public enum Category {
    COMBAT("Combat", 0xFFE03535),
    MOVEMENT("Movement", 0xFF35C75A),
    VISUAL("Visual", 0xFF3580E0),
    UTILITY("Utility", 0xFFD4B84A),
    QOL("QoL", 0xFF9B59B6),
    WORLD("World", 0xFFE07835),
    TWEAKEROO("Tweakeroo", 0xFF4AB8B8);

    private final String displayName;
    private final int color;

    Category(String displayName, int color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getColor() {
        return color;
    }
}

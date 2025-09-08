package de.hxntuch.AWL.util;

import de.hxntuch.AWL.AdvancedWhitelist;

public class ConfigInput {

    private String path;
    private Object value;

    public ConfigInput(String path, Object value) {
        this.path = path;
        this.value = value;
        AdvancedWhitelist.getInstance().getWhitelistConfig().getSortedList().add(this);
    }

    public Object getValue() {
        return value;
    }

    public String getPath() {
        return path;
    }

}

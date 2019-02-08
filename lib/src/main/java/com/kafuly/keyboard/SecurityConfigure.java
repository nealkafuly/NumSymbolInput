package com.kafuly.keyboard;

import android.graphics.Color;


public class SecurityConfigure {
    private int selectedColor = 0xff66aeff;
    //    private int selectedColor = Color.RED;
    private int unselectedColor = Color.WHITE;
    private boolean isNumberEnabled = true;
    private boolean isLetterEnabled = true;
    private boolean isSymbolEnabled = true;
    private KeyboardType defaultKeyboardType = KeyboardType.LETTER;

    public SecurityConfigure() {
    }

    public int getSelectedColor() {
        return selectedColor;
    }

    public SecurityConfigure setSelectedColor(int selectedColor) {
        this.selectedColor = selectedColor;
        return this;
    }

    public int getUnselectedColor() {
        return unselectedColor;
    }

    public SecurityConfigure setUnselectedColor(int unselectedColor) {
        this.unselectedColor = unselectedColor;
        return this;
    }

    public boolean isNumberEnabled() {
        return isNumberEnabled;
    }

    public SecurityConfigure setNumberEnabled(boolean numberEnabled) {
        this.isNumberEnabled = numberEnabled;
        return this;
    }

    public boolean isLetterEnabled() {
        return isLetterEnabled;
    }

    public SecurityConfigure setLetterEnabled(boolean letterEnabled) {
        this.isLetterEnabled = letterEnabled;
        return this;
    }

    public boolean isSymbolEnabled() {
        return isSymbolEnabled;
    }

    public SecurityConfigure setSymbolEnabled(boolean symbolEnabled) {
        this.isSymbolEnabled = symbolEnabled;
        return this;
    }

    public KeyboardType getDefaultKeyboardType() {
        return defaultKeyboardType;
    }

    public SecurityConfigure setDefaultKeyboardType(KeyboardType defaultKeyboardType) {
        this.defaultKeyboardType = defaultKeyboardType;
        return this;
    }
}

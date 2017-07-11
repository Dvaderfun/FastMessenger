package ru.lischenko_dev.fastmessenger.common;

import android.content.Context;
import android.view.View;

import ru.lischenko_dev.fastmessenger.R;
import ru.lischenko_dev.fastmessenger.util.Utils;

public class ThemeManager {
    private Context c;
    private boolean dark_theme = false;

    public ThemeManager(Context c) {
        this.c = c;
        dark_theme = Utils.getPrefs(c).getBoolean("dark_theme", false);
    }

    public static ThemeManager get(Context c) {
        return new ThemeManager(c);
    }

    public int getCurrentTheme() {
        return dark_theme ? R.style.AppTheme_Dark : R.style.AppTheme_Light;
    }

    public int getPanelColor() {
        return isDarkTheme() ? 0xff252525 : 0xffffffff;
    }

    public int getCurrentActionBarTheme() {
        return dark_theme ? R.style.AppTheme_Dark_ActionBar : R.style.AppTheme_Light_ActionBar;
    }

    public int getPrimaryColor() {
        return isDarkTheme() ? 0xff13509C : 0xff1565c0;
    }

    public int getInBubbleColor() {
        return isDarkTheme() ? 0xff333333 : 0xffe6e5ea;
    }

    public int getBubbleInTextColor() {
        return isDarkTheme() ? 0xfff0f0f0 : 0xff212121;
    }

    public int getPrimaryDarkColor() {
        return isDarkTheme() ? 0xff14417f : 0xff11519a;
    }

    public int getAccentColor() {
        return getPrimaryColor();
    }

    public int getSecondaryBackgroundColor() {
        return isDarkTheme() ? 0xff212121 : 0xffffffff;
    }

    public int getButtonColor() {
        return isDarkTheme() ? 0xff343434 : 0xffe5e5e5;
    }

    public boolean isDarkTheme() {
        return dark_theme;
    }

    public int getUnreadColor() {
        return isDarkTheme() ? 0xff454545 : 0xfff8f8f8;
    }

    public void putTheme(boolean newValue) {
        Utils.getPrefs(c).edit().putBoolean("dark_theme", newValue).apply();
    }

    public int getPrimaryTextColor() {
        return isDarkTheme() ? 0xffffffff : 0xff404040;
    }

    public int getSecondaryTextColor() {
        return isDarkTheme() ? 0xfff0f0f0 : 0xff78797a;
    }

    public int getEditTextColor() {
        return isDarkTheme() ? 0xfff0f0f0 : 0xff212121;
    }

    public void setHrBackgroundColor(View hr) {
        hr.setBackgroundColor(isDarkTheme() ? 0xff404040 : 0xfff0f0f0);
    }

}

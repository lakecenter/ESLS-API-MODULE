package com.wdy.module.utils;

import java.awt.*;

public class ColorUtil {

    public static byte getColorByte(int backgroundColor, int fontColor) {
        // 黑（0），白（1），红（2）
        // 白黑
        // 白红
        // 红黑(全0)
        if (backgroundColor == 0 && fontColor == 0) {
            return 0;
        } else if (backgroundColor == 0 && fontColor == 1) {
            return 1;
        } else if (backgroundColor == 0 && fontColor == 2) {
            return 3;
        } else if (backgroundColor == 1 && fontColor == 0) {
            return 16;
        } else if (backgroundColor == 1 && fontColor == 1) {
            return 17;
        } else if (backgroundColor == 1 && fontColor == 2) {
            return 19;
        } else if (backgroundColor == 2 && fontColor == 0) {
            return 48;
        } else if (backgroundColor == 2 && fontColor == 1) {
            return 49;
        } else if (backgroundColor == 2 && fontColor == 2) {
            return 51;
        }
        return 0;
    }

    public static Color getColorByInt(int type) {
        // 黑（0），白（1），红（2）
        switch (type) {
            case 0:
                return Color.BLACK;
            case 1:
                return Color.WHITE;
            case 2:
                return Color.RED;
            default:
                return Color.BLACK;
        }
    }

    public static Boolean isRedAndBlack(int backgroundColor, int fontColor) {
        Color background = getColorByInt(backgroundColor);
        Color font = getColorByInt(fontColor);
        if (background.equals(Color.RED) && font.equals(Color.BLACK))
            return true;
        else if (background.equals(Color.BLACK) && font.equals(Color.RED))
            return true;
        return false;
    }

    public static Integer getFontType(String fontType) {
        switch (fontType) {
            case "bold":
                return Font.BOLD;
            case "plain":
                return Font.PLAIN;
            case "italic":
                return Font.ITALIC;
            default:
                return Font.PLAIN;
        }
    }
}

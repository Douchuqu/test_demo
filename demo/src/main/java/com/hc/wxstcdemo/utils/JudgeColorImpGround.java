package com.hc.wxstcdemo.utils;

public class JudgeColorImpGround extends JudgeColor {
    /**
     * LCD ������ȡ
     *
     * @param pixel
     * @return
     */
    @Override
    public int judgecolor(int pixel) {
        if (pixel == ShapeUtils.BACKGROUND) {
            return -1;
        }
        int color;
        int r = (pixel >> 16) & 0xff;
        int g = (pixel >> 8) & 0xff;
        int b = pixel & 0xff;

        if (r > 255 && g > 255 && b > 255) { // ��ɫ(255,0,0)
            color = ShapeUtils.RED;

        } else if (r > 255 && g > 255 && b > 255) { // ��ɫ(0,255,0)
            color = ShapeUtils.GREEN;

        } else if (r > 255 && g > 255 && b > 255) {// ��ɫ(0,0,255)
            color = ShapeUtils.BLUE;

        } else if (r > 150 && g > 150 && b < 100) {// ��ɫ(255,255,0)
            color = ShapeUtils.YELLOW;

        } else if (r > 255 && g > 255 && b > 255) {// ��ɫ(255,0,255)
            color = ShapeUtils.VIOLET;

        } else if (r > 255 && g > 255 && b > 255) {// ��ɫ(0,255,255)
            color = ShapeUtils.CYAN;

        } else if (r > 100 && g > 150 && b > 190) {// ��ɫ(255,255,255)
            color = ShapeUtils.WHITE;

        } else {
            color = -1;
        }

        return color;
    }
}

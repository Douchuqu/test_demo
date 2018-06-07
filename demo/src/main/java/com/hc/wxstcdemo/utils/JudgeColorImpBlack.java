package com.hc.wxstcdemo.utils;

import com.hc.wxstcdemo.SysParameter;

public class JudgeColorImpBlack extends JudgeColor {
    /**
     * 图形颜色取值
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
        if(SysParameter.Read==ShapeUtils.LCD) {
            ////////////////////////////LCD的图形取值范围//////////////////////////////////
            if (r > 80 && g < 70 && b < 70) { // 红色(255,0,0)
                color = ShapeUtils.RED == ShapeUtils.GROUNDING ? -1 : ShapeUtils.RED;
            } else if (r < 150 && g > 150 && b < 100) { // 绿色(0,255,0)
                color = ShapeUtils.GREEN == ShapeUtils.GROUNDING ? -1 : ShapeUtils.GREEN;

            } else if (r < 50 && g < 120 && b > 150) {// 蓝色(0,0,255)
                color = ShapeUtils.BLUE == ShapeUtils.GROUNDING ? -1 : ShapeUtils.BLUE;

            } else if (r > 130 && g > 180 && b < 150) {// 黄色(255,255,0)
                color = ShapeUtils.YELLOW == ShapeUtils.GROUNDING ? -1 : ShapeUtils.YELLOW;

            } else if (r > 150 && g < 100 && b > 150) {// 紫色(255,0,255)
                color = ShapeUtils.VIOLET == ShapeUtils.GROUNDING ? -1 : ShapeUtils.VIOLET;

            } else if (r < 100 && g > 200 && b > 200) {// 青色(0,255,255)
                color = ShapeUtils.CYAN == ShapeUtils.GROUNDING ? -1 : ShapeUtils.CYAN;

            } else if (r < 60 && g < 60 && b < 40) {// 黑色(0,0,0)
                color = ShapeUtils.BLACK == ShapeUtils.GROUNDING ? -1 : ShapeUtils.BLACK;

            } else if (r > 150 && g > 150 && b > 150) {// 白色(255,255,255)
                color = ShapeUtils.WHITE == ShapeUtils.GROUNDING ? -1 : ShapeUtils.WHITE;

            } else {
                color = -1;
            }

            return color;
        }else{
            //////////////////////////TFT的图形取值范围//////////////////////////////
            if (r > 80 && g < 100 && b < 100) { // 红色(255,0,0)
                color = ShapeUtils.RED == ShapeUtils.GROUNDING ? -1 : ShapeUtils.RED;
            } else if (r < 130 && g > 150 && b < 100) { // 绿色(0,255,0)
                color = ShapeUtils.GREEN == ShapeUtils.GROUNDING ? -1 : ShapeUtils.GREEN;

            } else if (r < 50 && g < 120 && b > 150) {// 蓝色(0,0,255)
                color = ShapeUtils.BLUE == ShapeUtils.GROUNDING ? -1 : ShapeUtils.BLUE;

            } else if (r > 130 && g > 180 && b < 150) {// 黄色(255,255,0)
                color = ShapeUtils.YELLOW == ShapeUtils.GROUNDING ? -1 : ShapeUtils.YELLOW;

            } else if (r > 80 && g < 50 && b > 130) {// 紫色(255,0,255)
                color = ShapeUtils.VIOLET == ShapeUtils.GROUNDING ? -1 : ShapeUtils.VIOLET;

            } else if (r < 80 && g > 150 && b > 150) {// 青色(0,255,255)
                color = ShapeUtils.CYAN == ShapeUtils.GROUNDING ? -1 : ShapeUtils.CYAN;

            } else if (r < 60 && g < 60 && b < 40) {// 黑色(0,0,0)
                color = ShapeUtils.BLACK == ShapeUtils.GROUNDING ? -1 : ShapeUtils.BLACK;

            } else if (r > 255 && g > 255 && b > 255) {// 白色(255,255,255)
                color = ShapeUtils.WHITE == ShapeUtils.GROUNDING ? -1 : ShapeUtils.WHITE;

            } else {
                color = -1;
            }

            return color;
        }
    }
}

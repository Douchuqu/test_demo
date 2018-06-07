package com.hc.wxstcdemo;

import com.hc.wxstcdemo.utils.ShapeUtils;

/**
 * Created by Administrator on 2017/5/15.
 * <p>
 * 全局变量
 */

public class SysParameter {
    public static int Read = ShapeUtils.LCD;
    //////////////亮度///////////////
    public static float hls_S = 0.7f;
    public static float hls_L = 0.7f;
    public static float hls_H = 0.7f;
    /////////////LCD车牌////////////////
    public static float LCD_car_top = 0.4f;
    public static float LCD_car_bottom = 0.1f;
    ///////
    public static float LCD_car_start = 0.65f;
    public static float LCD_car_end = 1.2f;
    ////////////TFT车牌////////////////
    public static float TFT_car_top = 0.2f;
    public static float TFT_car_bottom = 0.25f;
    //////
    public static float TFT_car_start = 0.7f;
    public static float TFT_car_end = 1.3f;
    ////////////LCD图形识别///////////////
    public static float LCD_shape_top = 0.2f;
    public static float LCD_shape_bootom = 0.1f;
    ////////////TFT图形识别///////////////
    public static float TFT_shape_top = 0.25f;
    public static float TFT_shape_bootom = 0.1f;

}

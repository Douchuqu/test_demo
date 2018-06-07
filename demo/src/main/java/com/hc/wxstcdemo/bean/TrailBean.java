package com.hc.wxstcdemo.bean;

public class TrailBean {
    public final static int GO = 0;
    public final static int LINE = 1;// ѭ��
    public final static int GOLINE = 21;// ѭ��
    public final static int BACK = 2;
    public final static int LEFT = 3;
    public final static int GOLEFT = 4;
    public final static int HALFLEFT = 5;
    public final static int RIGHT = 6;
    public final static int GORIGHT = 7;
    public final static int HALFRIGHT = 8;
    public final static int STOP = 9;
    public final static int GOANDLINE = 10;
    public final static int NINELEFT = 11;
    public final static int NINERIGHT = 12;
    public int order;// ָ��
    public int sp_n;// �ٶ�
    public int en_n;// ����
    public byte[] bytes = new byte[6];
    public int left;
    public int right;
    public int buzzer;// ������
    public String strvoice;// ��������
    public String strthreedisplay;// ������ʾ
    public int[] digs = new int[4];// ��һ��Ϊ�ڼ�����ʾ

    private TrailBean(int order, int s1, int s2) {
        this.order = order;
        sp_n = s1;
        en_n = s2;
    }

    private TrailBean(int order, int s1) {
        this.order = order;
        this.sp_n = s1;
    }

    private TrailBean(int order) {
        this.order = order;
    }

    public TrailBean(int order, byte[] bytes) {
        this.order = order;
        this.bytes = bytes;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static TrailBean go(int sp_n, int en_n) {
        return new TrailBean(GO, sp_n, en_n);
    }

    public static TrailBean goandline(int sp_n, int en_n) {
        return new TrailBean(GOANDLINE, sp_n, en_n);
    }

    public static TrailBean line(int sp_n) {
        return new TrailBean(LINE, sp_n);
    }

    public static TrailBean goline(int sp_n) {
        return new TrailBean(GOLINE, sp_n);
    }

    public static TrailBean back(int sp_n, int en_n) {
        return new TrailBean(BACK, sp_n, en_n);
    }

    public static TrailBean left(int sp_n) {
        return new TrailBean(LEFT, sp_n);
    }

    public static TrailBean goleft(int sp_n) {
        return new TrailBean(GOLEFT, sp_n);
    }

    public static TrailBean halfleft(int sp_n) {
        return new TrailBean(HALFLEFT, sp_n);
    }

    public static TrailBean right(int sp_n) {
        return new TrailBean(RIGHT, sp_n);
    }

    public static TrailBean goright(int sp_n) {
        return new TrailBean(GORIGHT, sp_n);
    }

    public static TrailBean halfright(int sp_n) {
        return new TrailBean(HALFRIGHT, sp_n);
    }

    public static TrailBean nineright(int sp_n) {
        return new TrailBean(NINERIGHT, sp_n);
    }

    public static TrailBean nineleft(int sp_n) {
        return new TrailBean(NINELEFT, sp_n);
    }

    public static TrailBean stop() {
        return new TrailBean(STOP);
    }

}

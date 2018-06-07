package com.hc.wxstcdemo.utils;

import android.graphics.Bitmap;
import android.util.Log;

import com.hc.wxstcdemo.SysParameter;
import com.hc.wxstcdemo.bean.Coordinates;

import java.util.ArrayList;
import java.util.List;

public class ShapeUtils {
    public static final int ALLCOLOR = 0;//
    // ��ɫ(255,0,0)����ɫ(0,255,0)����ɫ(0,0,255)����ɫ(255,255,0)����ɫ(255,0,255)����ɫ(0,255,255)?����ɫ(0,0,0)����ɫ(255,255,255)
    public static final int RED = 1;// ��ɫ
    public static final int GREEN = 2;// ��ɫ
    public static final int BLUE = 3;// ��ɫ
    public static final int YELLOW = 4;// ��ɫ
    public static final int VIOLET = 5;// ��ɫ
    public static final int CYAN = 6;// ��ɫ
    public static final int BLACK = 7;// ��ɫ
    public static final int WHITE = 8;// ��ɫ
    public static final int TAGERCOLOR = 9;
    public static final int BACKGROUND = 0xff000000;

    public static int GROUNDING = 10;//ͼƬ��ɫ

    public ArrayList<Integer> allcolorlists = new ArrayList<Integer>();

    public ShapeUtils() {
        allcolorlists.add(RED);
        allcolorlists.add(GREEN);
        allcolorlists.add(BLUE);
        allcolorlists.add(YELLOW);
        allcolorlists.add(VIOLET);
        allcolorlists.add(CYAN);
        allcolorlists.add(BLACK);
        allcolorlists.add(WHITE);
    }

    public int judgecolor(int pixel) {
        int color;
        int r = (pixel >> 16) & 0xff;
        int g = (pixel >> 8) & 0xff;
        int b = pixel & 0xff;
        int abs = Math.abs(r - b);
        if (r > 80 && g < 5 && b < 5) { // ��ɫ
            color = RED;
        } else if (r < 85 && g > 130 && b < 10) { // ��ɫ
            color = GREEN;
        } else if (r > 90 && g > 90 && b < 8) {// ��ɫ
            color = YELLOW;
        } else {
            color = WHITE;// ��ɫ
        }
        return color;
    }

    private String colortoChinese(int color) {
        switch (color) {
            case RED:
                return "��";
            case GREEN:
                return "��";
            case YELLOW:
                return "��";
        }
        return "��";
    }

    // // �洢ͼƬ�Ϸ�����ֵ
    ArrayList<Coordinates> list = new ArrayList<Coordinates>();
    // �м��жϵ���
    ArrayList<Coordinates> list2 = new ArrayList<Coordinates>();


    public String shapepager(Bitmap bp) {
        return shapeDivisionPager(bp);
    }

    public ArrayList<Integer> shapeDivision(Bitmap bp) {
        int firstcolor = 0;
        ArrayList<Integer> colors = new ArrayList<Integer>();
        list.clear();
        list2.clear();
        int width = bp.getWidth();
        int height = bp.getHeight();
        int[] pixels = new int[width * height];
        bp.getPixels(pixels, 0, width, 0, 0, width, height);
        int offest1 = 0;
        int offest2 = 0;
        boolean ischange = false;
        for (int x = 0; x < width; x++) {
            if (!ischange) {
                offest1 = 0;
                offest2 = 0;
                ischange = false;
            }
            for (int y = 0; y < height; y++) {
                int pixel = pixels[y * width + x];
                int color = judgecolor(pixel);
                if (firstcolor == 0) {
                    if (color != WHITE) { // ��Ϊ��ɫ
                        offest1++;
                        ischange = true;
                        if (offest1 > 3) {
                            list.add(new Coordinates(x, y));
                            firstcolor = color;
                            break;
                        }
                        break;
                    }

                } else {
                    if (color != firstcolor && color != WHITE) {
                        offest2++;
                        ischange = true;
                        if (offest2 > 3 && (x - list.get(0).getX()) > 40) {
                            list.add(new Coordinates(x, y));
                            x = width;
                            break;
                        }
                        break;
                    } else {
                        if (color == firstcolor) {
                            break;
                        }
                    }
                }
            }
        }
        if (list.size() == 2) {
            int width2 = (list.get(0).getX() + list.get(1).getX() - 6) / 2;
            for (int y = 0; y < height; y++) {
                list2.add(new Coordinates(width2, y));
            }
            int lastcolor = 4;
            for (int y = 0; y < list2.size(); y++) {
                int pixel = pixels[y * width + width2];
                int color = judgecolor(pixel);
                if (color != WHITE && color != lastcolor) {
                    colors.add(color);
                    lastcolor = color;
                    Log.e("color", lastcolor + "");
                }
            }
        }
        return colors;
    }

    public boolean judgeblack(int pixel) {
        int color;
        int r = (pixel >> 16) & 0xff;
        int g = (pixel >> 8) & 0xff;
        int b = pixel & 0xff;
        int abs = Math.abs(r - b);
        if (r < 50 && g < 50 && b < 50) { // ��ɫ
            return true;
        }
        return false;
    }

    /************ ������ά����г����޷�ʶ�� ***********************/
    // // �洢ͼƬx������ֵ
    ArrayList<Coordinates> listx = new ArrayList<Coordinates>();
    // // �洢ͼƬy������ֵ
    ArrayList<Coordinates> listy = new ArrayList<Coordinates>();

    public ArrayList<Bitmap> shapeqrcode(Bitmap bp) {
        int cutx = 0;
        listx.clear();
        listy.clear();
        int width = bp.getWidth();
        int height = bp.getHeight();
        int[] pixels = new int[width * height];
        bp.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = pixels[y * width + x];
                if (judgeblack(pixel)) {
                    Log.e("TAG", x + "");
                    listx.add(new Coordinates(x, y));
                    break;
                }
            }
        }
        for (int i = 0; i < listx.size() - 1; i++) {
            if ((listx.get(i + 1).getX() - listx.get(i).getX()) > 10) {
                cutx = i;
                break;
            }
        }
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = pixels[y * width + x];
                if (judgeblack(pixel)) {
                    Log.e("TAG", x + "");
                    listy.add(new Coordinates(x, y));
                    break;
                }
            }
        }
        int wid = listx.get(cutx).getX() - listx.get(0).getX();
        int hei = listy.get(listy.size() - 1).getY() - listy.get(0).getY();

        int wid2 = listx.get(listx.size() - 1).getX()
                - listx.get(cutx + 1).getX();
        Bitmap bitmap1 = Bitmap.createBitmap(bp, listx.get(0).getX(), listy
                .get(0).getY(), wid, hei);
        Bitmap bitmap2 = Bitmap.createBitmap(bp, listx.get(cutx + 1).getX(),
                listy.get(0).getY(), wid2, hei);
        ArrayList<Bitmap> bimaps = new ArrayList<Bitmap>();
        bimaps.add(bitmap1);
        bimaps.add(bitmap2);
        return bimaps;
    }

    // // �洢ͼƬ�Ϸ�����ֵ
    ArrayList<Coordinates> list3 = new ArrayList<Coordinates>();
    ArrayList<Coordinates> list4 = new ArrayList<Coordinates>();

    public int judgecolor2(int pixel) {
        int color;
        int r = (pixel >> 16) & 0xff;
        int g = (pixel >> 8) & 0xff;
        int b = pixel & 0xff;
        int abs = Math.abs(r - b);
        if (r > 120 && g < 60 && b < 60) { // ��ɫ
            color = RED;
        } else if (r < 80 && g > 80 && b < 80) { // ��ɫ
            color = GREEN;
        } else if (r > 120 && g > 120 && b < 80) {// ��ɫ
            color = YELLOW;
        } else if (r > 120 && g > 120 && b < 120) {
            color = TAGERCOLOR;// Ŀ���ά����Χ
        } else {
            color = WHITE;// ��ɫ
        }
        return color;
    }

    public String shapeDivisionPager(Bitmap bp) {
        String result = "";
        list3.clear();
        list4.clear();
        bp = zuoyouOp(bp, null, new ArrayList<Integer>());
        if (bp != null) {

            int width2 = bp.getWidth();
            int height2 = bp.getHeight();
            int[] pixels2 = new int[width2 * height2];
            bp.getPixels(pixels2, 0, width2, 0, 0, width2, height2);

            for (int y = 0; y < height2 - 1; y++) {
                for (int x = 0; x < width2 - 1; x++) {
                    int pixel = pixels2[y * width2 + x];
                    int color = judgecolor2(pixel);
                    if (color == RED || color == GREEN || color == YELLOW) {
                        list4.add(new Coordinates(x, y));
                        break;
                    }
                }
            }
            int mid = 0;
            for (int i = 0; i < list4.size() - 1; i++) {
                if ((list4.get(i + 1).getY() - list4.get(i).getY()) > 4) {
                    mid = i;
                    break;
                }
            }
            if (mid != 0) {
                Bitmap bitmap1 = Bitmap.createBitmap(bp, 0,
                        list4.get(0).getY(), width2, list4.get(mid).getY()
                                - list4.get(0).getY());
                ArrayList<Integer> colors = new ArrayList<Integer>();
                colors.add(RED);
                colors.add(GREEN);
                colors.add(YELLOW);
                Bitmap btmap1 = zuoyouOp(bitmap1, null, colors);
                String s1 = discernShap(btmap1);

                Bitmap bitmap2 = Bitmap.createBitmap(bp, 0, list4.get(mid + 1)
                        .getY(), width2, list4.get(list4.size() - 1).getY()
                        - list4.get(mid + 1).getY());
                Bitmap btmap2 = zuoyouOp(bitmap2, null, colors);

                String s2 = discernShap(btmap2);
                result = s1 + "," + s2;
            }
        }
        return result;
    }

    /******************����ʶ��ͼ�η�****************************/////////
    public String Shape(Bitmap bitmap, int color, int Read) {
        String result = "";
        ArrayList<Bitmap> bitmaplists = juzhenshape(initBitmap(bitmap, new JudgeColorImpGround(), Read), color);
        if (bitmaplists.size() == 0) {
            return result;
        }
        for (int i = 0; i < bitmaplists.size(); i++) {

            result += discernShap(bitmaplists.get(i));
        }
        return result;
    }

    //////��ͼ ���°�����////////////
    public Bitmap screenshot(Bitmap bimap, float f, float f1, float f2, float f3) {
        if (bimap == null) {
            return null;
        }
        int width = bimap.getWidth();
        int height = bimap.getHeight();

        return Bitmap.createBitmap(bimap, (int) (f2 * width), (int) (height * f), (int) ((1 - f2 - f3) * width), (int) (height * (1 - f - f1)));
    }

    /**
     * @param jc
     * @param Read ʶ��LCD����TFT
     * @return
     */
    public static final int LCD = 0;
    public static final int TFT = 1;

    public Bitmap initBitmap(Bitmap bmap, JudgeColor jc, int Read) {
        float top = 0;
        float bottom = 0;
        if (Read == LCD) {
            top = SysParameter.LCD_shape_top;
            bottom = SysParameter.LCD_shape_bootom;
        } else if (Read == TFT) {
            top = SysParameter.TFT_shape_top;
            bottom = SysParameter.TFT_shape_bootom;
        }
        Bitmap bitmap = VeHoOp(screenshot(bmap, top, bottom, 0.15f, 0.15f), jc, null);
//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//        int[] pixels = new int[width * height];
//        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        //GROUNDING=jc.judgecolor(pixels[5*width+width/2]);
        //ground_tx.setText("����Ϊ:"+GROUNDING);
        return bitmap;
    }

    ///////////////�س���ָ����ɫ��ȫ����ɫͼ��//////////////////
    public ArrayList<Bitmap> juzhenshape(Bitmap bitmap, int targetcolor) {
        ArrayList<Bitmap> allbitmaplists = new ArrayList<Bitmap>();
        if (targetcolor == ALLCOLOR) {
            for (int i = 0; i < allcolorlists.size(); i++) {
                ArrayList<Bitmap> bitmaps = juzhenshapeOp(bitmap, allcolorlists.get(i));
                if (bitmaps != null) {
                    allbitmaplists.addAll(bitmaps);
                }
            }
            return allbitmaplists;
        }
        allbitmaplists = juzhenshapeOp(bitmap, targetcolor);
        return allbitmaplists;
    }

    //////////////////////�س���ָ����ɫͼ��////////////////////////
    public ArrayList<Bitmap> juzhenshapeOp(Bitmap bitmap, int targetcolor) {
        ArrayList<Bitmap> bitmaplists = new ArrayList<Bitmap>();
        Bitmap bitmap1 = convertToBlack(bitmap, null, targetcolor);
        if (bitmap1 == null) {
            return null;
        }
        int width = bitmap1.getWidth();
        int height = bitmap1.getHeight();
        int[] pixels = new int[width * height];
        bitmap1.getPixels(pixels, 0, width, 0, 0, width, height);
        int[][] pl = new int[height][width];
        int num = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = pixels[y * width + x];
                if (pixel != BACKGROUND) {
                    pl[y][x] = 1;
                    num++;
                }
            }
        }
        if (num < 200) {
            return bitmaplists;
        }
        int k = qiedantu(pl);
        int[] baoliu = new int[width * height];
        for (int i = 1; i <= k; i++) {
            int baoliunumber = baoliu(pl, pixels, baoliu, i);
            ///////////////////////�Ľس���ͼ�δ�С����/////////////////////////
            if (baoliunumber > 50 && baoliunumber < 8000) {
                Bitmap result = Bitmap.createBitmap(width, height,
                        Bitmap.Config.ARGB_8888);
                result.setPixels(baoliu, 0, width, 0, 0, width, height);
                Bitmap bitmap2 = VeHoOp(result, null, null);
                if (bitmap2 != null && (bitmap1.getByteCount() - bitmap2.getByteCount()) > bitmap2.getByteCount() / 5) {
                    bitmaplists.add(bitmap2);
                }
            }
        }

        return bitmaplists;
    }

    private int baoliu(int[][] pl, int[] pixels, int[] baoliu, int tagert) {
        int num = 0;
        int width = pl[0].length;
        int height = pl.length;
        for (int l = 0; l < height; l++) {
            for (int n = 0; n < width; n++) {
                if (pl[l][n] != tagert) {
                    baoliu[l * width + n] = BACKGROUND;
                } else {
                    baoliu[l * width + n] = pixels[l * width + n];
                    num++;
                }
            }
        }
        return num;
    }


    public Bitmap VeHoOp(Bitmap bimap, JudgeColor jc, List<Integer> tagetcolor) {
        return shangxiaOp(zuoyouOp(bimap, jc, tagetcolor), jc, tagetcolor);
    }


    private int qiedantu(int[][] pl)            //����k��ͼ�κţ��м���ܻ��б��ϲ�����ͼ�κţ�
    {
        int width = pl[0].length;
        int height = pl.length;
        //24=pl.length
        //32=pl[0].length
        boolean flag = false;
        int m, k = 0;        //Ŀǰ����k��ͼ�κ��루��û��ͼ�����ұߣ�
        int x, y, z, t, i, j;
        for (i = 1; i < height; i++) {
            t = 0;
            j = 0;
            flag = false;         //ɨ�赽�߶�flag=1������flag=0
            while (j < width) {
                while (pl[i][j] == 0) {
                    j++;         //��ɫ
                    if (j > width - 1)
                        break;
                }
                t = j;                   //��ס�߶���ʼ��
                if (j > width - 1)
                    break;
                while (pl[i][j] != 0)        //��ɫ�߶�
                {
                    flag = true;                     //ɨ�赽�߶�
                    j++;
                    if (j > width - 1) break;
                }
                if (flag) {
                    flag = false;
                    for (m = 2; m <= k + 1; m++)         //Ѱ���Ƿ�����һ�е��߶�����
                    {
                        for (x = t; x < j; x++) {
                            if (pl[i - 1][x] == m) {
                                if (!flag)                    //��һ������
                                {
                                    flag = true;
                                    for (y = t; y < j; y++)
                                        pl[i][y] = m;
                                    break;
                                } else if (flag)            //�ǵ�һ������
                                {
                                    for (y = 0; y < i; y++)
                                        for (z = 0; z < width; z++) {
                                            if (pl[y][z] == m)
                                                pl[y][z] = pl[i][t];
                                        }
                                    break;
                                }
                            }
                        }
                    }
                    if (!flag)                      //�Ҳ�������һ���߶����ڵ�
                    {
                        k++;
                        for (x = t; x < j; x++)
                            pl[i][x] = k;
                    }
                }
            }
        }
        return k;
    }


    /*
     * ���ҽ�ͼ
     */
    public Bitmap zuoyouOp(Bitmap bimap, JudgeColor jc, List<Integer> tagetcolor) {
        if (bimap == null) {
            return null;
        }
        if (jc == null) {
            jc = new JudgeColorImpBlack();
        }
        if (tagetcolor == null) {
            tagetcolor = allcolorlists;

        }
        list3.clear();
        int num = 0;
        int width = bimap.getWidth();
        int height = bimap.getHeight();
        int[] pixels = new int[width * height];
        bimap.getPixels(pixels, 0, width, 0, 0, width, height);
        boolean ischange = false;
        for (int x = 0; x < width; x++) {
            if (ischange) {
                ischange = false;
            } else {
                num = 0;
            }
            for (int y = 0; y < height; y++) {
                int pixel = pixels[y * width + x];
                int color = jc.judgecolor(pixel);
                if (tagetcolor.contains(color)) {
                    num++;
                    ischange = true;
                    if (num > 10) {
                        list3.add(new Coordinates(x - 9, y));
                        x = width;
                        break;
                    }
                    break;
                }
            }
        }
        num = 0;
        for (int x = width - 1; x > 0; x--) {
            if (ischange) {
                ischange = false;
            } else {
                num = 0;
            }
            for (int y = 0; y < height; y++) {
                int pixel = pixels[y * width + x];
                int color = jc.judgecolor(pixel);
                if (tagetcolor.contains(color)) {
                    num++;
                    ischange = true;
                    if (num > 10) {
                        list3.add(new Coordinates(x + 11, y));
                        x = 0;
                        break;
                    }
                    break;
                }
            }
        }
        if (list3.size() == 2 && list3.get(1).getX() > list3.get(0).getX()) {
            Bitmap bitmap1 = Bitmap.createBitmap(bimap, list3.get(0).getX(), 0,
                    list3.get(1).getX() - list3.get(0).getX(), height);
            return bitmap1;
        }
        return null;
    }

    // ���½�ͼ
    public Bitmap shangxiaOp(Bitmap bimap, JudgeColor jc,
                             List<Integer> tagetcolor) {
        if (bimap == null) {
            return null;
        }
        if (jc == null) {
            jc = new JudgeColorImpBlack();
        }
        if (tagetcolor == null) {
            tagetcolor = allcolorlists;

        }
        list3.clear();
        int num = 0;
        int width = bimap.getWidth();
        int height = bimap.getHeight();
        int[] pixels = new int[width * height];
        bimap.getPixels(pixels, 0, width, 0, 0, width, height);
        boolean ischange = false;
        for (int y = 0; y < height; y++) {
            if (ischange) {
                ischange = false;
            } else {
                num = 0;
            }
            for (int x = 0; x < width; x++) {
                int pixel = pixels[y * width + x];
                int color = jc.judgecolor(pixel);
                if (tagetcolor.contains(color)) {
                    num++;
                    ischange = true;
                    if (num > 10) {
                        list3.add(new Coordinates(x, y - 9));
                        y = height;
                        break;
                    }
                    break;
                }
            }
        }
        num = 0;
        for (int y = height - 1; y > 0; y--) {
            if (ischange) {
                ischange = false;
            } else {
                num = 0;
            }
            for (int x = 0; x < width; x++) {
                int pixel = pixels[y * width + x];
                int color = jc.judgecolor(pixel);
                if (tagetcolor.contains(color)) {
                    num++;
                    ischange = true;
                    if (num > 10) {
                        list3.add(new Coordinates(x, y + 11));
                        y = 0;
                        break;
                    }
                    break;
                }
            }
        }
        if (list3.size() == 2 && list3.get(1).getY() > list3.get(0).getY()) {
            Bitmap bitmap1 = Bitmap.createBitmap(bimap, 0, list3.get(0).getY(),
                    width, list3.get(1).getY() - list3.get(0).getY());
            return bitmap1;
        }
        return null;
    }

    // ���϶�ά��
    public Bitmap newtopmidOp(Bitmap bimap, double prop) {
        int width = bimap.getWidth();
        int height = bimap.getHeight();
        Bitmap bitmap1 = Bitmap.createBitmap(bimap, 0, 0, width, (int) (height * prop));
        return bitmap1;
    }

    // ���¶�ά��
    public Bitmap newbottommidOp(Bitmap bimap, double prop) {
        int width = bimap.getWidth();
        int height = bimap.getHeight();
        Bitmap bitmap2 = Bitmap
                .createBitmap(bimap, 0, (int) ((prop) * height), width, height - (int) ((prop) * height));
        return bitmap2;
    }

    // ���Ҷ�ά��
    public Bitmap newrightmidOp(Bitmap bimap, double prop) {
        int width = bimap.getWidth();
        int height = bimap.getHeight();
        Bitmap bitmap2 = Bitmap
                .createBitmap(bimap, (int) ((prop) * width), 0, width - (int) ((prop) * width), height);
        return bitmap2;
    }

    // �����ά��
    public Bitmap newleftmidOp(Bitmap bimap, double prop) {
        int width = bimap.getWidth();
        int height = bimap.getHeight();
        Bitmap bitmap2 = Bitmap
                .createBitmap(bimap, 0, 0, (int) ((prop) * width), height);
        return bitmap2;
    }

    // ���½ض�ά��
    public ArrayList<Bitmap> shangxiamidOp(Bitmap bimap, JudgeColor jc) {
        list3.clear();
        ArrayList<Integer> arrayList2 = new ArrayList<Integer>();
        arrayList2.add(WHITE);
        bimap = zuoyouOp(bimap, new JudgeColor() {
            @Override
            public int judgecolor(int pixel) {
                int r = (pixel >> 16) & 0xff;
                int g = (pixel >> 8) & 0xff;
                int b = pixel & 0xff;
                if (r > 150 && g > 150 && b > 150) {
                    return WHITE;
                }
                return 0;
            }
        }, arrayList2);

        list3.clear();
        int num = 0;
        int width = bimap.getWidth();
        int height = bimap.getHeight();
        int[] pixels = new int[width * height];
        bimap.getPixels(pixels, 0, width, 0, 0, width, height);
        boolean ischange = false;
        int x1 = 0;
        for (int y = 0; y < height - 1; y++) {
            if (ischange) {
                num++;
                ischange = false;
            } else {
                num = 0;
            }
            for (int x = 0; x < width - 1; x++) {
                int pixel = pixels[y * width + x];
                int color = jc.judgecolor(pixel);
                if (color == TAGERCOLOR) {
                    ischange = true;
                    x1 = x;
                    break;
                }
            }
            if (num > 20) {
                list3.add(new Coordinates(x1, y - 20));
                break;
            }
        }
        num = 0;
        for (int y = height - 1; y > 0; y--) {
            if (ischange) {
                num++;
                ischange = false;
            } else {
                num = 0;
            }
            for (int x = 0; x < width - 1; x++) {
                int pixel = pixels[y * width + x];
                int color = jc.judgecolor(pixel);
                if (color == TAGERCOLOR) {
                    ischange = true;
                    x1 = x;
                    break;
                }
            }
            if (num > 20) {
                list3.add(new Coordinates(x1, y + 20));
                break;
            }
        }
        if (list3.size() == 2 && list3.get(1).getY() > list3.get(0).getY()) {
            ArrayList<Bitmap> arrayList = new ArrayList<Bitmap>();
            int mid = (list3.get(0).getY() + list3.get(1).getY()) / 2;
            Bitmap bitmap1 = Bitmap.createBitmap(bimap, 0, 0, width, mid);
            Bitmap bitmap2 = Bitmap.createBitmap(bimap, 0, mid, width, height
                    - mid);
            arrayList.add(bitmap1);
            arrayList.add(bitmap2);
            return arrayList;
        }
        return null;
    }

    /*
     * ���ҽض�ά��
     */
    public ArrayList<Bitmap> zuoyoumidOp(Bitmap bimap, JudgeColor jc) {
        list3.clear();
        ArrayList<Integer> arrayList2 = new ArrayList<Integer>();
        arrayList2.add(WHITE);
        bimap = shangxiaOp(bimap, new JudgeColor() {
            @Override
            public int judgecolor(int pixel) {
                int r = (pixel >> 16) & 0xff;
                int g = (pixel >> 8) & 0xff;
                int b = pixel & 0xff;
                if (r > 150 && g > 150 && b > 150) {
                    return WHITE;
                }
                return 0;
            }
        }, arrayList2);
        list3.clear();
        int num = 0;
        int width = bimap.getWidth();
        int height = bimap.getHeight();
        int[] pixels = new int[width * height];
        bimap.getPixels(pixels, 0, width, 0, 0, width, height);
        boolean ischange = false;
        int y1 = 0;
        for (int x = 0; x < width - 1; x++) {
            if (ischange) {
                num++;
                ischange = false;
            } else {
                num = 0;
            }
            for (int y = 0; y < height - 1; y++) {
                int pixel = pixels[y * width + x];
                int color = jc.judgecolor(pixel);
                if (color == TAGERCOLOR) {
                    ischange = true;
                    y1 = y;
                    break;
                }
            }
            if (num > 20) {
                list3.add(new Coordinates(x - 20, y1));
                break;
            }
        }
        num = 0;
        for (int x = width - 1; x > 0; x--) {
            if (ischange) {
                num++;
                ischange = false;
            } else {
                num = 0;
            }
            for (int y = 0; y < height - 1; y++) {
                int pixel = pixels[y * width + x];
                int color = jc.judgecolor(pixel);
                if (color == TAGERCOLOR) {
                    ischange = true;
                    y1 = y;
                    break;
                }
            }
            if (num > 20) {
                list3.add(new Coordinates(x + 20, y1));
                break;
            }
        }
        if (list3.size() == 2 && list3.get(1).getX() > list3.get(0).getX()) {
            ArrayList<Bitmap> arrayList = new ArrayList<Bitmap>();
            int mid = (list3.get(0).getX() + list3.get(1).getX()) / 2;
            Bitmap bitmap1 = Bitmap.createBitmap(bimap, 0, 0, mid, height);
            Bitmap bitmap2 = Bitmap.createBitmap(bimap, mid, 0, width - mid,
                    height);
            arrayList.add(bitmap1);
            arrayList.add(bitmap2);
            return arrayList;
        }
        return null;
    }

    // ���е�
    ArrayList<Coordinates> list5r = new ArrayList<Coordinates>();
    ArrayList<Coordinates> list5g = new ArrayList<Coordinates>();
    ArrayList<Coordinates> list5y = new ArrayList<Coordinates>();

    //�ж��������
    public int Distance(Coordinates c1, Coordinates c2) {
        int distance;
        int x1 = c1.getX();
        int x2 = c2.getX();
        int y1 = c1.getY();
        int y2 = c2.getY();
        int x = Math.abs(x1 - x2);
        int y = Math.abs(y1 - y2);
        if (x == 0) {
            distance = y;
        }
        if (y == 0) {
            distance = x;
        }
        distance = (int) Math.sqrt(x * x + y * y);
        return distance;
    }

    //�ж��Ƿ������
    public boolean isFiveStar(Bitmap bitmap) {

        int offest1 = 0;
        int offest2 = 0;
        boolean islastcolorBLACK = true;
        int xianduan = 0;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        int max = width / 8;
        int b = 0;
        for (int i = 0; i < pixels.length; i++) {
            if (pixels[i] != BACKGROUND) {
                b++;
            }
        }
        double zhanbi = ((double) b / (double) pixels.length);
        for (int x = 0; x < width; x++) {
            islastcolorBLACK = true;
            xianduan = 0;
            for (int y = 0; y < height; y++) {
                int pixel = pixels[y * width + x];
                if (pixel != BACKGROUND) {
                    if (islastcolorBLACK) {
                        offest2++;
                        offest1 = 0;
                    }
                } else {
                    offest1++;  //offest1Ϊ��ɫ
                    offest2 = 0;
                }

                if (offest2 >= max) {
                    xianduan++;
                    offest2 = 0;
                    islastcolorBLACK = false;
                }
                if (offest1 >= max) {
                    offest1 = 0;
                    islastcolorBLACK = true;
                }
                if (xianduan == 2 && zhanbi < 0.5) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
        �ж�Բ��
     */
    public boolean isCircle(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Coordinates c1 = null;
        Coordinates c2 = null;
        Coordinates c3 = null;
        Coordinates c4 = null;
        Coordinates cz = null;
        int b = 0;
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        double max = Math.sqrt(width * width + height * height) / 15;
        for (int x = 0; x < width; x++) {
            int pixel = pixels[height / 3 * width + x];
            if (pixel != BACKGROUND) {
                c1 = new Coordinates(x, height / 3);
                break;
            }
        }
        for (int x = width - 1; x > 0; x--) {
            int pixel = pixels[height / 3 * width + x];
            if (pixel != BACKGROUND) {
                c2 = new Coordinates(x, height / 3);
                break;
            }
        }
        for (int x = 0; x < width; x++) {
            int pixel = pixels[height / 4 * 3 * width + x];
            if (pixel != BACKGROUND) {
                c3 = new Coordinates(x, height / 4 * 3);
                break;
            }
        }
        for (int x = width - 1; x > 0; x--) {
            int pixel = pixels[height / 4 * 3 * width + x];
            if (pixel != BACKGROUND) {
                c4 = new Coordinates(x, height / 4 * 3);
                break;
            }
        }
        for (int i = 0; i < pixels.length; i++) {
            if (pixels[i] != BACKGROUND) {
                b++;
            }
        }
        double zhanbi = ((double) b / (double) pixels.length);
        cz = new Coordinates(width / 2, height / 2);
        if (c1 != null && c2 != null && c3 != null && c4 != null) {
            int d1 = Distance(c1, cz);
            int d2 = Distance(c2, cz);
            int d3 = Distance(c3, cz);
            int d4 = Distance(c4, cz);
            if (Math.abs(height / 2 - d1) <= max
                    && Math.abs(height / 2 - d2) <= max
                    && Math.abs(height / 2 - d3) <= max
                    && Math.abs(height / 2 - d4) <= max
                    && zhanbi > 0.75 && zhanbi < 0.90) {
                return true;
            }
        }
        return false;
    }

    /*
        �ж��Ƿ�Ϊ������
     */
    public boolean istriangle(Bitmap bitmap) {
        boolean rT = false;
        boolean rB = false;
        boolean lT = false;
        boolean lB = false;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        int length = pixels.length;
        if (pixels[0] != BACKGROUND || pixels[1] != BACKGROUND || pixels[width] != BACKGROUND
                || pixels[width - 1] != BACKGROUND || pixels[width - 2] != BACKGROUND || pixels[2 * width - 1] != BACKGROUND
                || pixels[length - width] != BACKGROUND || pixels[length - width + 1] != BACKGROUND || pixels[length - 2 * width] != BACKGROUND
                || pixels[length - 1] != BACKGROUND || pixels[length - 2] != BACKGROUND || pixels[length - 1 - width] != BACKGROUND) {
            if (dectAt(bitmap) < 0.65) {
                return true;
            }
        }
        return false;
    }

    //���ϵĵ� (˳ʱ��)
    public ArrayList<Coordinates> biandian(Bitmap bitmap) {
        ArrayList<Coordinates> lists = new ArrayList<Coordinates>();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        int pixel;
        for (int x = 0; x < width; x++) {
            pixel = pixels[x];
            if (pixel != BACKGROUND) {
                lists.add(new Coordinates(x, 0));
                break;
            }
        }
        for (int x = width - 1; x > 0; x--) {
            pixel = pixels[(height - 1) * width + x];
            if (pixel != BACKGROUND) {
                lists.add(new Coordinates(x, height - 1));
                break;
            }
        }
        for (int y = height - 1; y > 0; y--) {
            pixel = pixels[y * width];
            if (pixel != BACKGROUND) {
                lists.add(new Coordinates(0, y));
                break;
            }
        }
        for (int y = 0; y < height; y++) {
            pixel = pixels[y * width + width - 1];
            if (pixel != BACKGROUND) {
                lists.add(new Coordinates(width - 1, y));
                break;
            }
        }
        return lists;
    }

    /*
        �жϾ���
     */
    public boolean isRect(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        double max = Math.sqrt(width * width + height * height) / 5;
        int b = 0;
        if (dectAt(bitmap) > 0.92) {
            return true;
        }
        ArrayList<Coordinates> lists = biandian(bitmap);
        if (lists.size() == 4) {
            double angle = Angle(lists.get(0), lists.get(2), lists.get(3));
            if (Math.abs(angle - 90) < 18) {
                return true;
            }
        }
        return false;
    }

    /*
     * �ж���״
     */
    public String discernShap(Bitmap bitmap) {
        if (bitmap == null) return "";
        JudgeColor jc = new JudgeColorImpBlack();
        int red = 0, green = 0, blue = 0, yellow = 0, violet = 0, cyan = 0, black = 0, white = 0;
        String result = "";
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = pixels[y * width + x];
                int color = jc.judgecolor(pixel);
                if (color == RED) {
                    red++;
                } else if (color == GREEN) {
                    green++;
                } else if (color == BLUE) {
                    blue++;
                } else if (color == YELLOW) {
                    yellow++;
                } else if (color == VIOLET) {
                    violet++;
                } else if (color == CYAN) {
                    cyan++;
                } else if (color == BLACK) {
                    black++;
                } else if (color == WHITE) {
                    white++;
                }
            }
        }
        if (red > 10) {
            result += "һ����ɫ" + chect(bitmap);
        }
        if (green > 10) {
            result += "һ����ɫ" + chect(bitmap);
        }
        if (blue > 10) {
            result += "һ����ɫ" + chect(bitmap);
        }
        if (yellow > 10) {
            result += "һ����ɫ" + chect(bitmap);
        }
        if (violet > 10) {
            result += "һ����ɫ" + chect(bitmap);
        }
        if (cyan > 10) {
            result += "һ����ɫ" + chect(bitmap);
        }
        if (black > 10) {
            result += "һ����ɫ" + chect(bitmap);
        }
        if (white > 10) {
            result += "һ����ɫ" + chect(bitmap);
        }
        return result;
    }

    public String chect(Bitmap bitmap) {
        String result = "";
        if (isFiveStar(bitmap)) {
            result = "�����";
        } else if (istriangle(bitmap)) {
            result = "������";
        } else if (isCircle(bitmap)) {
            result = "Բ��";
        } else if (isRect(bitmap)) {
            result = "����";
        } else {
            result = "����";
        }
        return result;
    }

    /*
       ռ��
     */
    public double dectAt(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        int b = 0;
        for (int i = 0; i < pixels.length; i++) {
            if (pixels[i] != BACKGROUND) {
                b++;
            }
        }
        return ((double) b / (double) pixels.length);

    }

    /*
        ����Ƕ�
     */
    double Angle(Coordinates o, Coordinates s, Coordinates e) {
        double cosfi = 0, fi = 0, norm = 0;
        double dsx = s.getX() - o.getX();
        double dsy = s.getY() - o.getY();
        double dex = e.getX() - o.getX();
        double dey = e.getY() - o.getY();
        cosfi = dsx * dex + dsy * dey;
        norm = (dsx * dsx + dsy * dsy) * (dex * dex + dey * dey);
        cosfi /= Math.sqrt(norm);
        if (cosfi >= 1.0) return 0;
        if (cosfi <= -1.0) return Math.PI;
        fi = Math.acos(cosfi);
        if (180 * fi / Math.PI < 180) {
            return 180 * fi / Math.PI;
        } else {
            return 360 - 180 * fi / Math.PI;
        }
    }

    public Bitmap convertToBlack(Bitmap bip, JudgeColor jc, int color) {// ���ش�������Ϊ��ɫ��
        if (bip == null) return null;
        if (jc == null) {
            jc = new JudgeColorImpBlack();
        }
        int width = bip.getWidth();
        int height = bip.getHeight();
        int[] pixels = new int[width * height];
        bip.getPixels(pixels, 0, width, 0, 0, width, height);
        int[] pl = new int[bip.getWidth() * bip.getHeight()];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                int pixel = pixels[offset + x];
                int judgecolor = jc.judgecolor(pixel);
                if (judgecolor == color) {
                    pl[offset + x] = pixel;
                    ////////�Ѻ�ɫ�ĺ�///////////
                    if (judgecolor == BLACK) {
                        pl[offset + x] = 0xffff0000;
                    }
                } else
                    pl[offset + x] = BACKGROUND;// ��ɫ
            }
        }
        Bitmap result = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        result.setPixels(pl, 0, width, 0, 0, width, height);
        return result;
    }

    String shapeResult = null;


    /************************************************ͼ����������ʱ�������***************************************/
    //���ָ����ɫ������ͼ��
    public ArrayList<Bitmap> RESshapeOp(Bitmap bitmap, int targetcolor) {
        ArrayList<Bitmap> bitmaplists = new ArrayList<Bitmap>();
        Bitmap bimp;
        if (targetcolor == ALLCOLOR) {
            bimp = convertToBlack(bitmap, null, RED);
            bitmaplists.addAll(vhOp(bimp));
            bimp = convertToBlack(bitmap, null, GREEN);
            bitmaplists.addAll(vhOp(bimp));
            bimp = convertToBlack(bitmap, null, YELLOW);
            bitmaplists.addAll(vhOp(bimp));
        } else {
            bimp = convertToBlack(bitmap, null, targetcolor);
            bitmaplists.addAll(vhOp(bimp));
        }
        for (int i = 0; i < bitmaplists.size(); i++) {
            Bitmap bitmap2 = bitmaplists.get(i);

        }
        return bitmaplists;
    }

    //���м�����
    public ArrayList<Bitmap> vhOp(Bitmap bimap) {
        ArrayList<Bitmap> allbtmaps = new ArrayList<Bitmap>();
        ArrayList<Bitmap> verticalOp = verticalOp(bimap);
        if (verticalOp == null)
            return null;
        for (int i = 0; i < verticalOp.size(); i++) {
            ArrayList<Bitmap> horizontalOp = horizontalOp(verticalOp.get(i));
            if (horizontalOp == null)
                continue;
            for (int j = 0; j < horizontalOp.size(); j++) {
                allbtmaps.add(zuoyouOp(horizontalOp.get(j), null, null));
            }
        }
        return allbtmaps;
    }

    // ������
    public ArrayList<Bitmap> verticalOp(Bitmap bimap) {
        list3.clear();
        int width = bimap.getWidth();
        int height = bimap.getHeight();
        int[] pixels = new int[width * height];
        bimap.getPixels(pixels, 0, width, 0, 0, width, height);
        int offest1 = 0;
        int offest2 = 0;
        int y1 = 0;
        boolean isblack = true;
        int lastcolor = BACKGROUND;
        for (int x = 0; x < width; x++) {
            if (isblack) {
                if (lastcolor != BACKGROUND) {
                    offest2++;
                    offest1 = 0;
                }
            } else {
                if (lastcolor == BACKGROUND) {
                    offest1++;
                    offest2 = 0;
                }
            }

            for (int y = 0; y < height; y++) {
                int pixel = pixels[y * width + x];
                isblack = true;
                if (pixel != BACKGROUND) {
                    y1 = y;
                    isblack = false;
                    break;
                }
            }
            if (offest1 > 3) {
                list3.add(new Coordinates(x - 4, y1));
                lastcolor = 0;
                offest1 = 0;
            }
            if (offest2 > 3) {
                list3.add(new Coordinates(x - 4, y1));
                lastcolor = BACKGROUND;
                offest2 = 0;
            }
        }
        if (list3.size() > 1) {
            ArrayList<Bitmap> btmaplists = new ArrayList<Bitmap>();
            for (int i = 0; i < list3.size() / 2; i++) {
                btmaplists.add(Bitmap.createBitmap(bimap, list3.get(2 * i)
                                .getX(), 0,
                        list3.get(2 * i + 1).getX() - list3.get(2 * i).getX(),
                        height));
            }
            return btmaplists;
        }
        return null;
    }

    // ������
    public ArrayList<Bitmap> horizontalOp(Bitmap bimap) {
        list3.clear();
        int width = bimap.getWidth();
        int height = bimap.getHeight();
        int[] pixels = new int[width * height];
        bimap.getPixels(pixels, 0, width, 0, 0, width, height);
        int offest1 = 0;
        int offest2 = 0;
        int x1 = 0;
        boolean isblack = true;
        int lastcolor = BACKGROUND;
        for (int y = 0; y < height; y++) {
            if (isblack) {
                if (lastcolor != BACKGROUND) {
                    offest2++;
                    offest1 = 0;
                }
            } else {
                if (lastcolor == BACKGROUND) {
                    offest1++;
                    offest2 = 0;
                }
            }
            for (int x = 0; x < width; x++) {
                int pixel = pixels[y * width + x];
                isblack = true;
                if (pixel != BACKGROUND) {
                    x1 = x;
                    isblack = false;
                    break;
                }
            }
            if (offest1 > 1) {
                list3.add(new Coordinates(x1, y - 2));
                lastcolor = 0;
                offest1 = 0;
            }
            if (offest2 > 1) {
                list3.add(new Coordinates(x1, y - 2));
                lastcolor = BACKGROUND;
                offest2 = 0;
            }
        }
        if (list3.size() > 1) {
            ArrayList<Bitmap> btmaplists = new ArrayList<Bitmap>();
            for (int i = 0; i < list3.size() / 2; i++) {
                btmaplists.add(Bitmap.createBitmap(bimap, 0, list3.get(2 * i)
                        .getY(), width, list3.get(2 * i + 1).getY()
                        - list3.get(2 * i).getY()));
            }
            return btmaplists;
        }
        return null;
    }
}

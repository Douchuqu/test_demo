package com.hc.wxstcdemo.client;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.bkrcl.control_car_video.camerautil.CameraCommandUtil;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.hc.wxstcdemo.BebugActivity;
import com.hc.wxstcdemo.MainActivity;
import com.hc.wxstcdemo.SysParameter;
import com.hc.wxstcdemo.bean.TrailBean;
import com.hc.wxstcdemo.utils.RGBLuminanceSource;
import com.hc.wxstcdemo.utils.ScaleUtils;
import com.hc.wxstcdemo.utils.ShapeUtils;
import com.hc.wxstcdemo.utils.ToastUtils;
import com.hc.wxstcdemo.utils.TrafficUtils;

import org.w3c.dom.ls.LSInput;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import match.AssetsResource;
import match.ColorKMeans;
import match.Oritenation;
import match.RecEachCharInMinDis;
import match.SegInEachChar;

public class Client {


    public Client(Context context, boolean ismain) {
        if (ismain) {
            main = (MainActivity) context;
        } else {

        }
        shapeutils = new ShapeUtils();
        trafficUtils = new TrafficUtils();
    }

    private short[] data = {0x00, 0x00, 0x00, 0x00, 0x00};
    public ShapeUtils shapeutils;
    private TrafficUtils trafficUtils;
    private String textResult;


    private long lasttime;
    private boolean istiming = false;
    private boolean isETCOpen = false;

    // 上下文
    public boolean iszhuflag = true;
    public short TYPE = 0xAA;
    public short MAJOR = 0x00;
    public short FIRST = 0x00;
    public short SECOND = 0x00;
    public short THRID = 0x00;
    // WiFi端口号
    private int port = 60000;
    // 端口
    private Socket socket = null;
    // 输入流
    private DataInputStream dataInputStream = null;
    // 输出流
    private DataOutputStream dataOutputStream = null;
    // 接受数据字节数组
    private byte[] mbyte = new byte[40];


    private Timer timer;
    public int mark = -50;
    private String first_car = "2#车库";
    private String two_car = "3#车库";
    private boolean two_flag = true;
    ArrayList<TrailBean> lists = new ArrayList<TrailBean>();
    // 接受数据线程
    Thread thread = new Thread(new Runnable() {
        public void run() {
            // 端口打开且有数据
            while (socket != null && !socket.isClosed()) {
                try {
                    // 读出数据到字节数组中
                    dataInputStream.read(mbyte);
                    if ((mbyte[2] & 0xFF) == 0x06) isETCOpen = true;
                } catch (IOException e) {// 数据读取异常处理
                    e.printStackTrace();
                }
            }
        }
    });

    public Thread quanThread = new Thread(new Runnable() {
        @Override
        public void run() {
            quan();
//            String shape = shapeutils.Shape(main.bitmap, shapeutils.ALLCOLOR);
//            main.addTextView(shape);
        }
    });
    public MainActivity main;

    // 连接WiFi
    public void connect(final Handler car_handler, String IP) {
        try {
            // 客户与服务连接
            socket = new Socket(IP, port);
            // 数据传输
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            // 数据接受
            dataInputStream = new DataInputStream(socket.getInputStream());
            // 开启线程时时接受数据
            thread.start();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.obj = mbyte;
                    message.what = 1;
                    car_handler.sendMessage(message);
                }
            }, 0, 500);
        } catch (UnknownHostException e) {
            // IP不正确处理
            e.printStackTrace();
        } catch (IOException e) {
            // 端口不正确处理
            e.printStackTrace();
        }
    }

    // 步骤命令
    public void stepsend(byte b) {
        try {
            // 发送数据字节数组
            byte[] sbyte = {0x55, (byte) TYPE, 0x17, b, 0x17};
            // 数据传输
            dataOutputStream.write(sbyte);
            dataOutputStream.flush();
        } catch (UnknownHostException e) {
            // IP不正确处理
            e.printStackTrace();
        } catch (IOException e) {
            // 端口不正确处理
            e.printStackTrace();
        }
    }

    // 坐标命令
    public void coordinatesend(byte x, byte y, byte d) {
        try {
            // 发送数据字节数组
            byte[] sbyte = {0x55, (byte) TYPE, 0x66, x, y, d, 0x66};
            // 数据传输
            dataOutputStream.write(sbyte);
            dataOutputStream.flush();
        } catch (UnknownHostException e) {
            // IP不正确处理
            e.printStackTrace();
        } catch (IOException e) {
            // 端口不正确处理
            e.printStackTrace();
        }
    }

    // RFID坐标命令
    public void coordinatesend_rfid(byte x, byte y, byte d) {
        try {
            // 发送数据字节数组
            byte[] sbyte = {0x55, (byte) TYPE, 0x65, x, y, d, 0x65};
            // 数据传输
            dataOutputStream.write(sbyte);
            dataOutputStream.flush();
        } catch (UnknownHostException e) {
            // IP不正确处理
            e.printStackTrace();
        } catch (IOException e) {
            // 端口不正确处理
            e.printStackTrace();
        }

    }

    // 发送命令
    public void send() {
        synchronized (this) {
            try {
                short CHECKSUM = (short) ((MAJOR + FIRST + SECOND + THRID) % 256);
                // 发送数据字节数组
                byte[] sbyte = {0x55, (byte) TYPE, (byte) MAJOR, (byte) FIRST,
                        (byte) SECOND, (byte) THRID, (byte) CHECKSUM,
                        (byte) 0xBB};
                // 数据传输
                dataOutputStream.write(sbyte);
                dataOutputStream.flush();
            } catch (UnknownHostException e) {
                // IP不正确处理
                e.printStackTrace();
            } catch (IOException e) {
                // 端口不正确处理
                e.printStackTrace();
            }
        }

    }


    public void task1() {
        vice(1);
        stepgo(5);
        police(null);
        stepgo(6);

        vice(2);

    }

    public void task2() {
        yanchi(500);
        digital_clear();
        yanchi(1000);
        digital_open();

    }


    public void task3() {


    }

    public void task4() {
        kong();
        int v = voice_rec();
        Log.e("vioce", "" + v);
        switch (v) {
            case -1:
                main.addTextView("未识别语言");
                break;
            case 1:
                main.addTextView("1");
                break;
            case 2:
                main.addTextView("2");
                break;
            case 3:
                main.addTextView("3");
                break;
            case 4:
                main.addTextView("4");
                break;
            case 5:
                main.addTextView("5");
                break;
        }

    }

    int num = 0;

    public void task5() {

        for (; ; ) {
            lists.add(TrailBean.go(60, 50));

            lists.add(TrailBean.back(60, 50));

            startGo(lists);
            num++;
            Log.e("Mystate", num + "");


        }
    }

    public void task6() {
        final ArrayList<String> codelists = CodeTwo(main.bitmap, Code_sx);
        if (codelists != null) {
            main.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.toast(main, codelists.get(0) + '\n' + codelists.get(1));
                }
            });

        }
    }

    public void task7() {
        int i = getandsetGear(2);
        main.addTextView("初始档位:" + i);
    }

    public void task8() {
        vice(1);
    }

    public void task9() {
        vice(2);
    }

    public void task10() {
        //lists.add(TrailBean.line(60));
        lists.add(TrailBean.goleft(60));
        lists.add(TrailBean.line(60));
        lists.add(TrailBean.goright(60));
        lists.add(TrailBean.line(60));
        lists.add(TrailBean.goline(60));
        lists.add(TrailBean.goleft(60));
        lists.add(TrailBean.line(60));
        lists.add(TrailBean.goleft(60));
        lists.add(TrailBean.goandline(60, 80));
        lists.add(TrailBean.nineleft(60));


        lists.add(TrailBean.line(60));
        lists.add(TrailBean.goleft(60));
        startGo(lists);
        initcoordinate(1, 1, 1);
        coordinatego(3, 0, 0);

    }

    public void task11() {
        initcoordinate(3, 0, 0);
        lists.add(TrailBean.goandline(60, 80));
        startGo(lists);

        coordinatego(2, 1, 1);
    }

    public void task12() {
        lists.add(TrailBean.goandline(60, 60));
        startGo(lists);

    }

    public void task13() {

        byte b = (byte) (0x01 & 0xFF);
        stepsend(b);
        while ((mbyte[2] & 0xFF) != 0x88)
            ;
        main.addTextView("已获得");
    }

    public void task14() {
//        digital_clear();
//        yanchi(800);
//        digital_open();
//        yanchi(500);
//        lists.add(TrailBean.line(60));
//        lists.add(TrailBean.go(60, 20));
//        startGo(lists);
//        yanchi(500);
//        initcoordinate(3, 0, 0);
//        yanchi(500);
//        coordinatego(3, 1, 1);
//        yanchi(3000);
//        String carcode = carCode(main.bitmap, ShapeUtils.LCD);
//        main.addTextView(carcode);
//        yanchi(1000);
//
//        TFT_Show(TFT_carcode, carcode, null);
//        yanchi(500);
//
//        coordinatego(3, 2, 1);
//        TFT_Show(TFT_picture, "下", null);
//        yanchi(800);
//        TFT_Show(TFT_picture, "指定", "6");
//        yanchi(800);
//        TFT_Show(TFT_picture, "指定", "6");
//        yanchi(4000);
//        main.addImgeView(main.bitmap);
//        String shape = shape(ShapeUtils.TFT);
//        main.addTextView(shape);
//        coordinatego(2, 2, 2);
//        coordinatego(2, 1, 2);
//        yanchi(500);
//        lists.add(TrailBean.go(60, 5));
//        lists.add(TrailBean.halfright(60));
//        startGo(lists);
//        police(null);
//        yanchi(1000);
//        lists.add(TrailBean.halfleft(60));
//        startGo(lists);
//        initcoordinate(2, 1, 2);
//        coordinatego(2, 0, 2);
//        lists.add(TrailBean.go(60, 18));
//        startGo(lists);
//        yanchi(3000);
//        String traffic = traffic();
//        main.addImgeView(main.bitmap);
//        lists.add(TrailBean.back(60, 18));
//        startGo(lists);
//        if (traffic != null && !traffic.equals("")) {
//            main.addTextView(traffic);
//            if (traffic.contains("红色向右")) {
//                coordinatego(3, 0, 1);
//                coordinatego(3, 1, 3);
//                coordinatego(1, 0, 3);
//            } else if (traffic.contains("红色向左")) {
//                coordinatego(1, 0, 3);
//            } else if (traffic.contains("绿色向右")) {
//                coordinatego(1, 0, 3);
//            } else if (traffic.contains("绿色向左")) {
//                coordinatego(3, 0, 1);
//                coordinatego(3, 1, 3);
//                coordinatego(1, 0, 3);
//            } else if (traffic.contains("掉头")) {
//                coordinatego(2, 1, 3);
//                coordinatego(1, 0, 3);
//            }
//        } else {
//            coordinatego(1, 0, 3);
//        }
//        yanchi(3000);
//        String code = Code(main.bitmap);
//        if (code == null) {
//            lists.add(TrailBean.back(60, 3));
//            startGo(lists);
//            code = Code(main.bitmap);
//        }
//        main.addTextView("二维码:" + code);
//        yanchi(1000);
//        gate(1);
//        yanchi(500);
//        coordinatego(3, 0, 1);
//        lists.add(TrailBean.go(60, 30));
//        startGo(lists);
//
//        int dangWei = getandsetGear(0);
//        main.addTextView("当前档位:" + dangWei);
//        lists.add(TrailBean.back(60, 30));
//        lists.add(TrailBean.left(80));
//        startGo(lists);
        initcoordinate(3, 0, 0);
        yanchi(500);
        byte[] RFIDbyte = RFID_coordinatego(3, 2, 0);
        if (RFIDbyte != null) {
            main.addTextView(ScaleUtils.byte2HexStr(RFIDbyte));
            main.addTextView(new String(RFIDbyte, Charset.forName("gbk")));
        } else {
            RFIDbyte = RFID_coordinatego(3, 0, 0);
            if (RFIDbyte != null) {
                main.addTextView(ScaleUtils.byte2HexStr(RFIDbyte));
                main.addTextView(new String(RFIDbyte, Charset.forName("gbk")));
            }
        }

        coordinatego(2, 2, 3);
//        lists.add(TrailBean.line(60));
//        startGo(lists);
//        yanchi(3000);
//        int ultraSonic = getUltraSonic();
//        main.addTextView("测得距离" + ultraSonic + "mm");
//
//        lists.add(TrailBean.go(60, 25));
//        lists.add(TrailBean.left(80));
//        startGo(lists);
//        initcoordinate(1, 2, 2);
//        coordinatego(2, 1, 1);
//        lists.add(TrailBean.go(60, 5));
//        lists.add(TrailBean.halfright(60));
//        startGo(lists);
//
//        threedisplay(STE_diatance,ultraSonic+"");
//
//        yanchi(1000);
//        lists.add(TrailBean.halfleft(60));
//        startGo(lists);
//        initcoordinate(2,1,1);
//        coordinatego(2, 2, 0);
//        int voice = voice_rec();
//        main.addTextView(voice + "");
//        switch (voice) {
//            case -1:
//                main.addTextView("未识别语言");
//                break;
//            case 0:
//                main.addTextView("0");
//                break;
//            case 1:
//                main.addTextView("1");
//                break;
//            case 2:
//                main.addTextView("2");
//                break;
//            case 3:
//                main.addTextView("3");
//                break;
//            case 4:
//                main.addTextView("4");
//                break;
//        }

//        coordinatego(2, 2, 2);
//        coordinatego(0, 1, 3);
//        buzzer(1);
//        yanchi(3000);
//
//        buzzer(0);
//        yanchi(1000);
//        digital_close();
//        yanchi(1000);
//        send_voice("比赛完成");


    }

    public void task15() {
        initcoordinate(3, 0, 3);
        byte[] bytes = RFID_coordinatego(1, 0, 3);
        if (bytes == null) {
            bytes = RFID_coordinatego(3, 0, 1);
        }
        if (bytes != null) {
            main.addTextView(ScaleUtils.byteArrayToHexStr(bytes));
            main.addTextView(new String(bytes, Charset.forName("gbk")));
        }

    }

    public void task16() {
        String code = null;
        String ultraSonic = "10";
        vice(1);
        initcoordinate(0, 1, 1);
        coordinatego(3, 0, 1);

        lists.add(TrailBean.goandline(60, 25));
        startGo(lists);
        int dangwei;
        if (code != null) {
            dangwei = getandsetGear(Integer.valueOf(code));
        } else {
            dangwei = getandsetGear(2);
        }
        main.addTextView("初始档位:" + dangwei);

        lists.add(TrailBean.back(60, 25));
        lists.add(TrailBean.left(60));
        lists.add(TrailBean.halfleft(60));
        startGo(lists);

        threedisplay(STE_diatance, ultraSonic + "");
        yanchi(1500);

        lists.add(TrailBean.right(60));
        lists.add(TrailBean.goandline(60, 45));
        lists.add(TrailBean.nineright(60));
        lists.add(TrailBean.right(60));
        startGo(lists);

        initcoordinate(3, 0, 3);

        coordinatego(1, 1, 1);

        lists.add(TrailBean.back(60, 90));
        startGo(lists);
        vice(2);

        lists.add(TrailBean.right(60));
        startGo(lists);

        byte[] bytes = RFID_coordinatego(3, 0, 0);
        if (bytes != null) {
            main.addTextView(ScaleUtils.byte2HexStr(bytes));
            main.addTextView(new String(bytes, Charset.forName("gbk")));
        } else {
            bytes = RFID_coordinatego(2, 2, 3);
            if (bytes != null) {
                main.addTextView(ScaleUtils.byte2HexStr(bytes));
                main.addTextView(new String(bytes, Charset.forName("gbk")));
            }
        }
        coordinatego(1, 0, 3);
        lists.add(TrailBean.line(60));
        startGo(lists);
        light(1, 1);
        yanchi(500);
        lists.add(TrailBean.go(60, 15));
        startGo(lists);
        buzzer(1);
        yanchi(1800);

        buzzer(0);
        digital_close();
    }

    public boolean timeout(int sec) {
        if (lasttime == 0) {
            lasttime = System.currentTimeMillis();
        }
        if (System.currentTimeMillis() - lasttime > sec * 1000) {
            lasttime = 0;
            return true;
        }
        return false;
    }

    //////////立体显示车牌加位置
    /////////图形黑色改回来
    public void quan() {
        // turncamera(2);
        yanchi(500);
        digital_clear();
        yanchi(1000);
        digital_open();
        yanchi(500);
        lists.add(TrailBean.go(60, 50));
        lists.add(TrailBean.halfleft(60));
        startGo(lists);
//        TFT_Show(TFT_picture, "指定", "1");
//        yanchi(500);
//        TFT_Show(TFT_picture, "指定", "1");
        //yanchi(3000);
        main.addImgeView(main.bitmap);
        /////////////////////////////////////////////////////
        String carcode = "";
        //carCode(main.bitmap, ShapeUtils.TFT);


        //TFT_Show(TFT_picture, "下", null);
        yanchi(4000);
        main.addImgeView(main.bitmap);
        String shape = shape(ShapeUtils.TFT);
        if (shapeNum(shape, "一个") < 5 || shapeNum(shape, "三角形") > 4) {
            TFT_Show(TFT_picture, "下", null);
            yanchi(4000);
            shape = shape(ShapeUtils.TFT);
        }
        if (shapeNum(shape, "一个") > 3) {
            digital(2, Integer.valueOf("A" + shapeNum(shape, "矩形"), 16),
                    Integer.valueOf("B" + shapeNum(shape, "圆形"), 16),
                    Integer.valueOf("C" + shapeNum(shape, "三角形"), 16));
        }
        main.addTextView("图形:" + shape);
        yanchi(500);
        lists.add(TrailBean.left(80));
        startGo(lists);
        initcoordinate(3, 2, 1);
        turncamera(3);
        lists.add(TrailBean.right(80));
        lists.add(TrailBean.goandline(60, 60));
        startGo(lists);
        yanchi(3500);
        String code = Code(main.bitmap);
        int i = 0;
        while (code == null && i < 3) {
            lists.add(TrailBean.go(60, 3));
            startGo(lists);
            i++;
            yanchi(2500);
            code = Code(main.bitmap);
        }
        main.addTextView("二维码:" + code);
        turncamera(2);
        String[] code_canlist = null;
        String[] code_milist = null;
        if (code != null) {
            String[] split = code.split("/");
            String code_chan = split[0].substring(split[0].indexOf("<") + 1, split[0].indexOf(">"));
            String code_mi = split[1].substring(split[1].indexOf("<") + 1, split[1].indexOf(">"));
            code_canlist = code_chan.split(",");
            code_milist = code_chan.split(",");
            main.addTextView("参数：" + code_chan + "密文" + code_mi);
        }

        String strRFID = "";
        byte[] bytes = RFID_coordinatego(3, 0, 3);
        if (bytes != null) {
            strRFID = new String(bytes);
            main.addTextView(strRFID);
        }
        String AVG_get;
        try {
            AVG_get = strRFID.substring(strRFID.indexOf("\"") + 1, strRFID.indexOf("\"") + 5);
            main.addTextView(AVG_get);

        } catch (Exception e) {
            AVG_get = "";
        }
        coordinatego(2, 0, 2);
        int voice = voice_rec();
        shangchuan(2);
        yanchi(1000);
        coordinatego(2, 0, 0);

        lists.add(TrailBean.goandline(60, 80));
        startGo(lists);

        vice(1);
        try {
            if (main.index == 1) {

            } else if (main.index == 2) {
                lists.add(TrailBean.left(60));
                startGo(lists);
            } else if (main.index == 3) {
                stepgo(9);
            } else if (main.index == 4) {
                lists.add(TrailBean.right(60));
            } else {

            }
        } catch (Exception e) {
            lists.add(TrailBean.left(60));
            startGo(lists);
        }
        stepgo(5);
        //police(null);
        yanchi(500);
        gate(1);
        yanchi(1000);
        stepgo(8);
        vice(2);

        coordinatego(2, 1, 0);
        yanchi(2000);
        coordinatego(1, 2, 0);

        if (shape != null && shapeNum(shape, "五角星") != 0) {
            getandsetGear(shapeNum(shape, "五角星"));
        } else {
            getandsetGear(2);
        }

        coordinatego(1, 1, 2);

        lists.add(TrailBean.halfleft(60));
        startGo(lists);
        if (carcode != null && !carcode.equals("")) {
            threedisplay(STE_carcode, carcode + "D2");
        } else {
            threedisplay(STE_carcode, "R896S3" + "F2");
        }
        yanchi(500);
        lists.add(TrailBean.left(70));
        startGo(lists);
        coordinatego(1, 0, 3);

        lists.add(TrailBean.goandline(60, 70));
        startGo(lists);
        yanchi(500);
        magnetic(1);
        yanchi(1000);
        digital_close();
        yanchi(500);
        buzzer(1);
        yanchi(1500);
        buzzer(0);
        yanchi(500);
        light(1, 1);


    }

    ///////////////////识别字符串中的指定字符有多少个//////////////////////////////
    public static int shapeNum(String str, String key) {
        int count = 0, index = 0;
        while (str.indexOf(key) != -1) {
            count++;
            index = str.indexOf(key);
            str = str.substring(index + key.length());
        }
        return count;
    }

    //获得超声波距离
    public int getUltraSonic() {
        yanchi(1000);
        long UltraSonic;
        UltraSonic = mbyte[5] & 0xff;
        UltraSonic = UltraSonic << 8;
        UltraSonic += mbyte[4] & 0xff;

        return (int) UltraSonic;
    }
//    public int f1(int M04) {
//        Calendar c = Calendar.getInstance();
//        int year = c.get(Calendar.YEAR);
//        int month = c.get(Calendar.MONTH);
//        int day = c.get(Calendar.DAY_OF_MONTH);
//        int result = (int) (((float) month - (float) INT(M04 - year * 10) / 50.0) % 4 + 1);
//        return result;
//    }
//
//    public int f2(int M01, int M02, int M03, int M04) {
//        int result = (12 + (M02 % 2) * 2 + M03 % 2 * 4 + OCT(M01 + M02 * 10) + f1(DEC(
//                M04, 16))) % 4 + 1;
//
//        return result;
//    }
//
//    public int f3(int M04) {
//        int result = (DEC(M04, 16) + 1) % 4 + 1;
//        return result;
//    }
//
//    private int DEC(int f, int radix) {
//        return Integer.valueOf(f + "", radix);
//    }
//
//    private int OCT(int f) {
//        String octalString = Integer.toOctalString(f);
//        return Integer.valueOf(octalString);
//    }
//
//    private int INT(float f) {
//        return (int) ((f * 10 + 5) / 10);
//    }
//
//    private int BIN(int f) {
//        String binaryString = Integer.toBinaryString(f);
//        return Integer.valueOf(binaryString);
//    }
//
//    private int HEX(int f) {
//        String hexString = Integer.toHexString(f);
//        return f;
//    }

    public void startGo(ArrayList<TrailBean> list2) {
        ArrayList<TrailBean> lists = new ArrayList<TrailBean>(list2);

        list2.clear();
        for (TrailBean list : lists) {
            switch (list.order) {
                case TrailBean.GO:
                    go(list.sp_n, list.en_n);
                    break;
                case TrailBean.LINE:
                    line(list.sp_n);
                    break;
                case TrailBean.GOLINE:
                    goline(list.sp_n);
                    break;
                case TrailBean.LEFT:
                    left(list.sp_n);
                    kong();
                    break;
                case TrailBean.GOLEFT:
                    goleft(list.sp_n);
                    break;
                case TrailBean.HALFLEFT:
                    halfleft(list.sp_n);
                    break;
                case TrailBean.RIGHT:
                    right(list.sp_n);
                    break;
                case TrailBean.GORIGHT:
                    goright(list.sp_n);
                    break;
                case TrailBean.HALFRIGHT:
                    halfright(list.sp_n);
                    break;
                case TrailBean.BACK:
                    back(list.sp_n, list.en_n);
                    break;
                case TrailBean.STOP:
                    stop();
                    break;
                case TrailBean.GOANDLINE:
                    goandline(list.sp_n, list.en_n);
                    break;
                case TrailBean.NINERIGHT:
                    nineright(list.sp_n);
                    break;
                case TrailBean.NINELEFT:
                    nineleft(list.sp_n);
                    break;
            }
            if (iszhuflag) {
                yanchi(100);
            }
        }

    }

    private CameraCommandUtil cameraCommandUtil = new CameraCommandUtil();

    // 转动摄像头 要先设置预设位
    public void turncamera(int i) {
        String IP = main.IP;
        switch (i) {
            case 1:
                cameraCommandUtil.postHttp(IP, 31, 0);
                break;
            case 2:
                cameraCommandUtil.postHttp(IP, 33, 0);
                break;
            case 3:
                cameraCommandUtil.postHttp(IP, 35, 0);
                break;
        }
    }

    public final static int Code_sx = 0;
    public final static int Code_zy = 1;

    //识别上下二维码或左右二维码
    public ArrayList<String> CodeTwo(Bitmap bitmap, int code_type) {
        ArrayList<String> codelists = new ArrayList<String>();
        String result1 = null;
        String result2 = null;
        double zhejia = 0.04;
        double prop = 0.1;
        while (prop < 0.8) {
            prop += zhejia;
            Bitmap bitmap1 = null;
            if (code_type == Code_zy) {
                bitmap1 = shapeutils.newleftmidOp(bitmap, prop);
            } else if (Code_sx == code_type) {
                bitmap1 = shapeutils.newtopmidOp(bitmap, prop);
            }
            result1 = Code(bitmap1);
            if (result1 != null && !result1.equals("null")) {
                codelists.add(result1);
                prop = 1;
            }
        }
        if (result1 != null && result1.equals("null")) {
            return null;
        }
        zhejia = 0.04;
        prop = 0.1;
        while (prop < 0.8) {
            prop += zhejia;
            Bitmap bitmap2 = null;
            if (code_type == Code_zy) {

                bitmap2 = shapeutils.newrightmidOp(bitmap, prop);
            } else if (Code_sx == code_type) {

                bitmap2 = shapeutils.newbottommidOp(bitmap, prop);
            }

            result2 = Code(bitmap2);
            if (result2 != null && !result2.equals("null")) {
                codelists.add(result2);
                prop = 1;
            }
        }
        if (result2 != null && result2.equals("null")) {
            return null;
        }
        if (codelists.size() == 2 && !codelists.get(0).equals(codelists.get(1))) {
            return codelists;
        }
        return null;
    }


    // 识别二维码
    public String Code(Bitmap bp) {
        Result result = null;
        if (bp == null) return null;
        Bitmap bitmap = bp;
        Map<DecodeHintType, String> hints = new HashMap<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        RGBLuminanceSource source = new RGBLuminanceSource(bitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            result = reader.decode(bitmap1, hints);
        } catch (NotFoundException e) {
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        if (result == null) {
            return null;
        }
        return result.toString();
    }

    //遍历识别车牌
    public String carCode(Bitmap bimp, int Read) {
        float start = 0.45f;
        float end = 1.2f;
        if (Read == ShapeUtils.LCD) {
            start = SysParameter.LCD_car_start;
            end = SysParameter.LCD_car_end;
        } else if (Read == ShapeUtils.TFT) {
            start = SysParameter.TFT_car_start;
            end = SysParameter.TFT_car_end;
        }
        String result = "";
        for (double x = start; x < end; x = x + 0.05) {
            SysParameter.hls_S = (float) x;
            SysParameter.hls_L = (float) x;
            SysParameter.hls_H = (float) x;
            result = ocr_car(bimp, Read);
            if (result != null && !result.equals("")) {
                return result;
            }
        }
        return result;
    }

    // 识别车牌
    public String ocr_car(Bitmap bimp, int Read) {
        String result = "";
        Bitmap curbitmap;
        Bitmap[] bitmaps;
        try {
            if (Read == shapeutils.LCD) {
                bimp = shapeutils.screenshot(bimp, SysParameter.LCD_car_top, SysParameter.LCD_car_bottom, 0, 0);
            } else if (Read == shapeutils.TFT) {
                bimp = shapeutils.screenshot(bimp, SysParameter.TFT_car_top, SysParameter.TFT_car_bottom, 0, 0);
            }
            curbitmap = ColorKMeans.Math(bimp);
            curbitmap = Oritenation.Math(curbitmap, bimp);
            bitmaps = SegInEachChar.Math(curbitmap);
            AssetsResource.context = main;
            result = RecEachCharInMinDis.Math(bitmaps);
        } catch (Exception e) {
            result = "";
        }
        if (!result.equals("")) {
            result = result.substring(1, 7);
        }
        return result;
    }

    // 识别图形
    public String shape(int read) {
        if (read == ShapeUtils.LCD || read == ShapeUtils.TFT) {
            SysParameter.Read = read;
        }
        Bitmap bitmap = main.bitmap;
        if (bitmap == null) {
            return "";
        }
        String colors = shapeutils.Shape(bitmap, ShapeUtils.ALLCOLOR, SysParameter.Read);

        return colors;
    }

    // 识别纸质图形
    public String shapePaper() {
        Bitmap bitmap = main.bitmap;
        if (bitmap == null) {
            return "";
        }
        String result = shapeutils.shapepager(bitmap);
        if (result.equals("")) {
            return null;
        }
        return result;
    }

    // 识别交通灯
    public String traffic() {
        String result;
        Bitmap bitmap = main.bitmap;
        if (bitmap == null) {
            return null;
        }
        result = trafficUtils.shapeIdentification(trafficUtils.qiege(bitmap), null);
        return result;
    }

    // 开报警器 红外
    public void police(byte[] b) {
        if (b == null || b.length != 6) {
            infrared((byte) 0x03, (byte) 0x05,
                    (byte) 0x14, (byte) 0x45, (byte) 0xDE,
                    (byte) 0x92);
        } else {
            infrared(b[0], b[1], b[2], b[3],
                    b[4], b[5]);
        }
        yanchi(1000);
        if (!iszhuflag) {
            yanchi(500);
        }
    }

    public static final int STE_color = 0;
    public static final int STE_carcode = 1;
    public static final int STE_shape = 2;
    public static final int STE_diatance = 3;

    // 立体显示 红外
    public void threedisplay(int style, String str) {
        if (str == null)
            return;
        switch (style) {
            case STE_color:
                data[0] = 0x13;
                try {
                    data[1] = (short) (Integer.valueOf(str) + 0x01);
                } catch (Exception e) {
                    return;
                }
                infrared_stereo(data);
                break;
            case STE_carcode:
                short[] li = StringToBytes(str);
                data[0] = 0x20;
                data[1] = (short) (li[0]);
                data[2] = (short) (li[1]);
                data[3] = (short) (li[2]);
                data[4] = (short) (li[3]);
                infrared_stereo(data);
                data[0] = 0x10;
                data[1] = (short) (li[4]);
                data[2] = (short) (li[5]);
                data[3] = (short) (li[6]);
                data[4] = (short) (li[7]);
                infrared_stereo(data);
                break;
            case STE_shape:

                break;
            case STE_diatance:
                char[] c = str.substring(0, 2).toCharArray();
                data[0] = 0x11;
                data[1] = (short) (c[0]);
                data[2] = (short) (c[1]);
                data[3] = 0;
                data[4] = 0;
                infrared_stereo(data);
                break;
        }

    }

    //RFID
    public byte[] RFID() {
        byte[] RFIDbyte = null;
        stepsend((byte) 0x00);
        while ((mbyte[2] & 0xFF) != 0x88) ;
        while ((mbyte[2] & 0xFF) != 0x88 && (mbyte[2] & 0xFF) != 0x66) ;
        if ((mbyte[2] & 0xFF) == 0x88) {
            if (RFIDbyte == null) {
                RFIDbyte = new byte[]{};
                RFIDbyte = Arrays.copyOfRange(mbyte, 4, mbyte[3] + 3);
                return RFIDbyte;
            }
        }
        return null;
    }

    //    //RFID坐标
    public byte[] RFID_coordinatego(int X, int Y, int direction) {

        byte[] RFIDbyte = null;
        byte x = (byte) (X & 0xFF);
        byte y = (byte) (Y & 0xFF);
        byte d = (byte) (direction & 0xFF);
        coordinatesend_rfid(x, y, d);
        Log.e("Mystate", "RFID坐标命令已发送");
        while ((mbyte[2] & 0xFF) != 0x88 && (mbyte[2] & 0xFF) != 0x66) ;
        Log.e("Mystate", "RFID坐标命令已返回");
        if ((mbyte[2] & 0xFF) == 0x88) {
            RFIDbyte = new byte[]{};
            RFIDbyte = Arrays.copyOfRange(mbyte, 4, mbyte[3] + 3);
            Log.e("Mystate", "RFID已成功识别");
            kong();
            return RFIDbyte;
        }
        kong();
        return null;
    }

    // 按步骤走
    public void stepgo(int step) {
        byte b = (byte) (step & 0xFF);
        stepsend(b);
        Log.e("Mystate", "步骤" + step + "已发");
        while ((mbyte[2] & 0xFF) != b + 0xf0)
            ;
        Log.e("Mystate", "步骤" + step + "已返回");
    }

    // 坐标位命令发送

    /**
     * @param X         X坐标
     * @param Y         Y坐标
     * @param direction 车头方向 y正方向为0 顺时针依次 0 1 2 3
     */
    public void coordinatego(int X, int Y, int direction) {
        byte x = (byte) (X & 0xFF);
        byte y = (byte) (Y & 0xFF);
        byte d = (byte) (direction & 0xFF);

        coordinatesend(x, y, d);
        Log.e("Mystate", "坐标命令:" + "(" + x + "," + y + "," + direction + "," + ")" + "已发送");
        while ((mbyte[2] & 0xFF) != 0x66)
            ;
        Log.e("Mystate", "坐标命令已返回");
        kong();
    }

    // 空命令
    public void kong() {
        coordinatesend((byte) 0x05, (byte) 0x05, (byte) 0x05);
        Log.e("Mystate", "开始等待空命令返回");
        while ((mbyte[2] & 0xFF) != 0x67)
            ;
        Log.e("Mystate", "空命令已返回");
    }

    // 坐标减命令
    public void jiancoordinate() {
        coordinatesend((byte) 0x00, (byte) 0x00, (byte) 0x06);
        while ((mbyte[2] & 0xFF) != 0x68)
            ;
    }

    // 初始化坐标
    public void initcoordinate(int X, int Y, int direction) {
        X = direction * 10 + X;
        String x = X + "";
        int integer = Integer.valueOf(x, 16);
        coordinatesend((byte) integer, (byte) Y, (byte) 0x07);
        Log.e("Mystate", "开始等待初始化坐标返回");
        while ((mbyte[2] & 0xFF) != 0x69)
            ;
        Log.e("Mystate", "初始化坐标已返回");
        yanchi(500);
    }


    // 前进
    public void go(int sp_n, int en_n) {

        MAJOR = 0x02;
        FIRST = (byte) (sp_n & 0xFF);
        SECOND = (byte) (en_n & 0xff);
        THRID = (byte) (en_n >> 8);
        send();
        if (iszhuflag) {
            while (mbyte[2] != 3)
                ;
        } else {
            while (mbyte[2] != 3)
                ;
            yanchi(500);
        }
    }

    // 后退
    public void back(int sp_n, int en_n) {

        MAJOR = 0x03;
        FIRST = (byte) (sp_n & 0xFF);
        SECOND = (byte) (en_n & 0xff);
        THRID = (byte) (en_n >> 8);
        send();
        if (iszhuflag) {
            while (mbyte[2] != 7)
                ;
        } else {
            while (mbyte[2] != 7)
                ;
            yanchi(500);
        }
    }

    // 左转
    public void left(int sp_n) {

        MAJOR = 0x04;
        FIRST = (byte) (sp_n & 0xFF);
        SECOND = 0x00;
        THRID = 0x00;
        send();
        if (iszhuflag) {
            while (mbyte[2] != 2)
                ;
        } else {
            while (mbyte[2] != 2)
                ;
            yanchi(500);
        }
    }

    // 右转
    public void right(int sp_n) {

        MAJOR = 0x05;
        FIRST = (byte) (sp_n & 0xFF);
        SECOND = 0x00;
        THRID = 0x00;
        send();
        if (iszhuflag) {
            while (mbyte[2] != 8)
                ;
        } else {
            while (mbyte[2] != 8)
                ;
            yanchi(500);
        }
    }

    // 循迹
    public void line(int sp_n) {

        MAJOR = 0x06;
        FIRST = (byte) (sp_n & 0xFF);
        SECOND = 0x00;
        THRID = 0x00;
        send();
        if (iszhuflag) {
            while (mbyte[2] != 1)
                ;
        } else {
            while (mbyte[2] != 1)
                ;
        }
    }

    // 循迹固定距离
    public void goandline(int sp_n, int en_n) {
        MAJOR = 0x98;
        FIRST = (byte) (sp_n & 0xFF);
        SECOND = (byte) (en_n & 0xff);
        THRID = (byte) (en_n >> 8);
        send();
        while (mbyte[2] != 0x15)
            ;
    }

    // 前进加循迹
    public void goline(int sp_n) {
        MAJOR = 0x94;
        FIRST = (byte) (sp_n & 0xFF);
        SECOND = 0x00;
        THRID = 0x00;
        send();
        while (mbyte[2] != 0x12)
            ;
    }

    // 前进加左转
    public void goleft(int sp_n) {
        MAJOR = 0x99;
        FIRST = (byte) (sp_n & 0xFF);
        SECOND = 0x00;
        THRID = 0x00;
        send();
        while (mbyte[2] != 0x16)
            ;
    }

    // 前进加右转
    public void goright(int sp_n) {
        MAJOR = 0x9A;
        FIRST = (byte) (sp_n & 0xFF);
        SECOND = 0x00;
        THRID = 0x00;
        send();
        while (mbyte[2] != 0x17)
            ;
    }

    // 90左转
    public void nineleft(int sp_n) {
        MAJOR = 0x95;
        FIRST = (byte) (sp_n & 0xFF);
        SECOND = 0x00;
        THRID = 0x00;
        send();
        while (mbyte[2] != 0x13)
            ;
    }

    // 90右转
    public void nineright(int sp_n) {
        MAJOR = 0x96;
        FIRST = (byte) (sp_n & 0xFF);
        SECOND = 0x00;
        THRID = 0x00;
        send();
        while (mbyte[2] != 0x14)
            ;
    }

    // 45左转
    public void halfleft(int sp_n) {
        MAJOR = 0x92;
        FIRST = (byte) (sp_n & 0xFF);
        SECOND = 0x00;
        THRID = 0x00;
        send();
        while (mbyte[2] != 0x10)
            ;
    }

    // 45右转
    public void halfright(int sp_n) {
        MAJOR = 0x93;
        FIRST = (byte) (sp_n & 0xFF);
        SECOND = 0x00;
        THRID = 0x00;
        send();
        while (mbyte[2] != 0x11)
            ;
    }


    // 停车
    public void stop() {
        MAJOR = 0x01;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    public void vice(int i) {// 主从车状态转换
        if (i == 1) {// 从车状态
            TYPE = 0x02;
            MAJOR = 0x80;
            FIRST = 0x01;
            SECOND = 0x00;
            THRID = 0x00;
            send();
            Log.e("Mystate", "从车开始上传数据已发送");
            yanchi(1000);

            TYPE = (byte) 0xAA;
            MAJOR = 0x80;
            FIRST = 0x01;
            SECOND = 0x00;
            THRID = 0x00;
            send();
            TYPE = 0x02;
            yanchi(500);
            Log.e("Mystate", "主关闭数据上传已发送");
            while (mbyte[1] != 0x02)
                ;
            iszhuflag = false;
        } else if (i == 2) {// 主车状态
            TYPE = 0x02;
            MAJOR = 0x80;
            FIRST = 0x00;
            SECOND = 0x00;
            THRID = 0x00;
            send();
            yanchi(1000);

            TYPE = (byte) 0xAA;
            MAJOR = 0x80;
            FIRST = 0x00;
            SECOND = 0x00;
            THRID = 0x00;
            send();
            TYPE = 0xAA;
            yanchi(500);
            while ((mbyte[1] & 0xFF) != 0xAA)
                ;
            iszhuflag = true;
        }

    }


    // 红外
    public void infrared(byte one, byte two, byte thrid, byte four, byte five,
                         byte six) {
        MAJOR = 0x10;
        FIRST = one;
        SECOND = two;
        THRID = thrid;
        send();
        yanchi(1000);
        MAJOR = 0x11;
        FIRST = four;
        SECOND = five;
        THRID = six;
        send();
        yanchi(1000);
        MAJOR = 0x12;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
        yanchi(1000);
    }

    // 双色LED灯
    public void lamp(byte command) {
        MAJOR = 0x40;
        FIRST = command;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    // 指示灯
    public void light(int left, int right) {
        if (left == 1 && right == 1) {
            MAJOR = 0x20;
            FIRST = 0x01;
            SECOND = 0x01;
            THRID = 0x00;
            send();
        } else if (left == 1 && right == 0) {
            MAJOR = 0x20;
            FIRST = 0x01;
            SECOND = 0x00;
            THRID = 0x00;
            send();
        } else if (left == 0 && right == 1) {
            MAJOR = 0x20;
            FIRST = 0x00;
            SECOND = 0x01;
            THRID = 0x00;
            send();
        } else if (left == 0 && right == 0) {
            MAJOR = 0x20;
            FIRST = 0x00;
            SECOND = 0x00;
            THRID = 0x00;
            send();
        }
        yanchi(500);
    }

    // 蜂鸣器
    public void buzzer(int i) {
        if (i == 1) {
            MAJOR = 0x30;
            FIRST = 0x01;
            SECOND = 0x00;
            THRID = 0x00;
            send();
        }
        if (i == 0) {
            MAJOR = 0x30;
            FIRST = 0x00;
            SECOND = 0x00;
            THRID = 0x00;
            send();
        }
    }

    // 清零码盘
    public void clear() {
        MAJOR = 0x07;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    // 语言播报
    public void send_voice(String src) {
        byte[] bytesend;
        try {
            // 发送数据字节数组
            if (socket != null && !socket.isClosed()) {
                if (src == null) {
                    bytesend = bytesend("未识别".getBytes("GBK"));
                } else {
                    bytesend = bytesend(src.getBytes("GBK"));
                }
                dataOutputStream.write(bytesend, 0, bytesend.length);
                dataOutputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        if (iszhuflag) {
//            while (mbyte[2] != (0x10 & 0xff))
//                ;
//        }
        yanchi(500);
    }

    private byte[] bytesend(byte[] sbyte) {
        byte[] textbyte = new byte[sbyte.length + 5];
        textbyte[0] = (byte) 0xFD;
        textbyte[1] = (byte) (((sbyte.length + 2) >> 8) & 0xff);
        textbyte[2] = (byte) ((sbyte.length + 2) & 0xff);
        textbyte[3] = 0x01;// 合成语音命令
        textbyte[4] = (byte) 0x01;// 编码格式
        for (int i = 0; i < sbyte.length; i++) {
            textbyte[i + 5] = sbyte[i];
        }
        return textbyte;
    }

    /**
     * 打开道闸
     *
     * @param i
     */
    public void gate(int i) {// 闸门
        byte type = (byte) TYPE;
        if (i == 1) {
            TYPE = 0x03;
            MAJOR = 0x01;
            FIRST = 0x01;
            SECOND = 0x00;
            THRID = 0x00;
            send();
        } else if (i == 2) {
            TYPE = 0x03;
            MAJOR = 0x01;
            FIRST = 0x02;
            SECOND = 0x00;
            THRID = 0x00;
            send();
        }
        TYPE = type;
        yanchi(1000);
        // while (mbyte[2] != 0x05)
        // ;
    }

    public void picture(int i) {// 图片上翻和下翻
        if (i == 1)
            MAJOR = 0x50;
        else
            MAJOR = 0x51;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
        yanchi(200);
    }


    public void gear(int i) {// 光照档位加
        if (i == 0)
            MAJOR = 0x00;
        else if (i == 1)
            MAJOR = 0x61;
        else if (i == 2)
            MAJOR = 0x62;
        else if (i == 3)
            MAJOR = 0x63;

        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
        yanchi(200);
    }

    public int getandsetGear(int i) {// 获得并到达哪个档位

        int result = 0;
        stepsend((byte) 0x03);
        while ((mbyte[2] & 0xFF) != 0xF3)
            ;
        result = mbyte[3];
        int jiadan;
        if (result == 1) {
            jiadan = i % 4;
        } else {
            jiadan = (i + 5 - result) % 4;
        }
        gear(jiadan);
        Log.e("Light", "开始档位:" + result + " 加的几挡:" + jiadan + "要求档位:" + i);
        yanchi(500);
        if (!iszhuflag) {
            yanchi(2500);
        }
        return result;
    }

//    public int getandsetGear(int i) {// 获得并到达哪个档位
//        int result = 0;
//        long[] array = new long[4];
//        long nowNum;
//        long firstNum;
//        array[0] = light();
//        for (int m = 1; m < array.length; m++) {
//            gear(1);
//            yanchi(4000);
//            array[m] = light();
//        }
//        Log.e("Light", "1:" + array[0] + "," + array[1] + "," + array[2] + "," + array[3]);
//        nowNum = array[3];
//        firstNum = array[0];
//        Arrays.sort(array);
//        Log.e("Light", "2:" + array[0] + "," + array[1] + "," + array[2] + "," + array[3]);
//        for (int n = 0; n < array.length; n++) {
//            if (firstNum == array[n]) {
//                result = n + 1;
//                int jiadan;
//                if (n == 0) {
//                    jiadan = i % 4;
//                } else {
//                    jiadan = (i + 4 - n) % 4;
//                }
//                gear(jiadan);
//                Log.e("Light", "开始档位:" + n + " 加的几挡:" + jiadan + "要求档位:" + i);
//                yanchi(500);
//            }
//        }
//        return result;
//    }


//    public int getDangWei() {// 当前档位
//        long[] array = new long[4];
//        long nowNum;
//        array[0] = light();
//        nowNum = array[0];
//        for (int m = 1; m < array.length; m++) {
//            gear(1);
//            yanchi(3000);
//            array[m] = light();
//        }
//        Arrays.sort(array);
//        for (int n = 0; n < array.length; n++) {
//            if (nowNum == array[n]) {
//                return n + 1;
//            }
//        }
//        return 0;
//    }

    private long light() {
        long Light = 0;
        Light = mbyte[7] & 0xff;
        Light = Light << 8;
        Light += mbyte[6] & 0xff;
        Log.e("Light", "光照度" + Light);
        return Light;
    }

    public void fan() {// 风扇

    }

    // 立体显示
    public void infrared_stereo(short[] data) {
        MAJOR = 0x10;
        FIRST = 0xff;
        SECOND = data[0];
        THRID = data[1];
        send();
        yanchi(1000);
        MAJOR = 0x11;
        FIRST = data[2];
        SECOND = data[3];
        THRID = data[4];
        send();
        yanchi(1000);
        MAJOR = 0x12;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
        yanchi(500);
    }

    // LCD显示标志物进入计时模式
    public void digital_close() {// 数码管关闭
        byte type = (byte) TYPE;
        TYPE = 0x04;
        MAJOR = 0x03;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
        TYPE = type;
        yanchi(200);
    }

    public void digital_open() {// 数码管打开
        byte type = (byte) TYPE;
        TYPE = 0x04;
        MAJOR = 0x03;
        FIRST = 0x01;
        SECOND = 0x00;
        THRID = 0x00;
        send();
        TYPE = type;
        yanchi(200);
    }

    public void digital_clear() {// 数码管清零
        byte type = (byte) TYPE;
        TYPE = 0x04;
        MAJOR = 0x03;
        FIRST = 0x02;
        SECOND = 0x00;
        THRID = 0x00;
        send();
        TYPE = type;
        yanchi(200);
    }

    public void digital_dic(int dis) {// LCD显示标志物第二排显示距离
        byte type = (byte) TYPE;
        TYPE = 0x04;
        MAJOR = 0x04;
        FIRST = 0x00;
        SECOND = (short) (dis / 100);
        THRID = (short) (dis % 100);
        send();
        TYPE = type;
        yanchi(200);
    }

    public void shangchuan(int value) {// 上传

        try {
            // 发送数据字节数组
            byte[] sbyte = {(byte) 0xAF, (byte) 0x06, (byte) value, (byte) 0x02,
                    (byte) 0x00, (byte) 0x00, (byte) 0x01,
                    (byte) 0xBF};
            // 数据传输
            dataOutputStream.write(sbyte);
            dataOutputStream.flush();
        } catch (UnknownHostException e) {
            // IP不正确处理
            e.printStackTrace();
        } catch (IOException e) {
            // 端口不正确处理
            e.printStackTrace();
        }
        yanchi(200);
    }

    public void digital(int i, int one, int two, int three) {// 数码管
        byte type = (byte) TYPE;
        TYPE = 0x04;
        if (i == 1) {// 数据写入第一排数码管
            MAJOR = 0x01;
            FIRST = (byte) one;
            SECOND = (byte) two;
            THRID = (byte) three;
        } else if (i == 2) {// 数据写入第二排数码管
            MAJOR = 0x02;
            FIRST = (byte) one;
            SECOND = (byte) two;
            THRID = (byte) three;
        }
        send();
        TYPE = type;
        yanchi(500);
    }


    //语音识别
    public int voice_rec() {
        stepsend((byte) 0x02);
        Log.e("vioce", "等待语音返回");
//        while ((mbyte[2] & 0xFF) != 0x90 && (mbyte[2] & 0xFF) != 0x91)
//            ;
//        Log.e("vioce", "语言已经返回");
//        if ((mbyte[2] & 0xFF) == 0x90) {
//            return mbyte[3];
//        } else {
//            return -1;
//        }
        yanchi(3000);
        return 1;
    }

    //语言控制命令
    public void send_voiceTraffic(int i) {
        byte type = (byte) TYPE;
        TYPE = (short) 0x06;
        MAJOR = (short) 0x10;//0x20 为随机命令
        if (i == 1) {
            FIRST = 0x01;
        } else if (i == 2) {
            FIRST = 0x02;
        } else if (i == 3) {
            FIRST = 0x03;
        } else if (i == 4) {
            FIRST = 0x04;
        } else if (i == 5) {
            FIRST = 0x05;
        } else if (i == 6) {
            FIRST = 0x06;
        } else {
            FIRST = 0x00;
        }
        SECOND = (byte) 0x00;
        THRID = (byte) 0x00;
        send();
        yanchi(500);
        TYPE = type;
    }

    //TFT
    public void TFT_LCD(int MAIN, int KIND, int COMMAD, int DEPUTY) {
        byte type = (byte) TYPE;
        TYPE = (short) 0x0B;
        MAJOR = (short) MAIN;
        FIRST = (byte) KIND;
        SECOND = (byte) COMMAD;
        THRID = (byte) DEPUTY;
        send();
        TYPE = type;
    }

    public static final int TFT_picture = 0;
    public static final int TFT_carcode = 1;
    public static final int TFT_time = 2;
    public static final int TFT_diatance = 3;
    public static final int TFT_hex = 4;

    //TFT实现

    /**
     * @param type TFT进行什么操作
     * @param str1 命令有 指定 上 下 自动 开始 关闭 停止 以及显示距离
     * @param str2 一般为null 指定
     */
    public void TFT_Show(int type, String str1, String str2) {
        switch (type) {
            case TFT_picture:
                if (str1 == null || str1.equals("")) {
                    return;
                }
                int index;
                if (str1.contains("指定")) {
                    try {
                        index = Integer.valueOf(str2);
                    } catch (Exception e) {
                        return;
                    }
                    TFT_LCD(0x10, 0x00, index, 0x00);
                } else if (str1.contains("上")) {
                    TFT_LCD(0x10, 0x01, 0x00, 0x00);
                } else if (str1.contains("下")) {
                    TFT_LCD(0x10, 0x02, 0x00, 0x00);
                } else if (str1.contains("自动")) {
                    TFT_LCD(0x10, 0x03, 0x00, 0x00);
                }
                break;
            case TFT_carcode:
                if (str1.length() == 6) {
                    char[] chars = str1.toCharArray();
                    TFT_LCD(0x20, chars[0], chars[1], chars[2]);
                    yanchi(500);
                    TFT_LCD(0x21, chars[3], chars[4], chars[5]);
                    yanchi(800);
                    TFT_LCD(0x20, chars[0], chars[1], chars[2]);
                    yanchi(500);
                    TFT_LCD(0x21, chars[3], chars[4], chars[5]);


                }
                break;
            case TFT_time:
                if (str1.contains("开始")) {
                    TFT_LCD(0x30, 0x01, 0x00, 0x00);
                } else if (str1.contains("关闭")) {
                    TFT_LCD(0x30, 0x02, 0x00, 0x00);
                } else if (str1.contains("停止")) {
                    TFT_LCD(0x30, 0x00, 0x00, 0x00);
                }
                break;
            case TFT_diatance:
                if (str1.length() == 3) {
                    char[] chars = str1.toCharArray();
                    int value = (chars[1] - '0') * 10 + chars[2] - '0';
                    TFT_LCD(0x50, 0x00, chars[0] - '0', Integer.valueOf("" + value, 16));
                } else if (str1.length() == 2 || str1.length() == 1) {
                    TFT_LCD(0x50, 0x00, 0x00, Integer.valueOf(str1, 16));
                }
                break;
            case TFT_hex:

                break;
        }
        yanchi(800);

    }

    //ETC
    public void ETC() {
        while (isETCOpen || !timeout(5)) ;
        lasttime = 0;
        isETCOpen = false;
        yanchi(1000);
    }

    //磁悬浮
    public void magnetic_suspension(int MAIN, int KIND, int COMMAD, int DEPUTY) {
        byte type = (byte) TYPE;
        TYPE = (short) 0x0A;
        MAJOR = (short) MAIN;
        FIRST = (byte) KIND;
        SECOND = (byte) COMMAD;
        THRID = (byte) DEPUTY;
        send();
        yanchi(500);
        TYPE = type;
    }

    public void magnetic(int i) {
        if (i == 1) {///////开
            magnetic_suspension(0x01, 0x01, 0x00, 0x00);
        } else if (i == 0) {///////关
            magnetic_suspension(0x01, 0x02, 0x00, 0x00);
        }

    }


    // 沉睡
    public void yanchi(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 获取sd卡的路径
     *
     * @return 路径的字符串
     */
    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取外存目录
        }
        return sdDir.toString();
    }

    /**
     * 进行图片识别
     *
     * @param bitmap   待识别图片
     * @param language 识别语言
     * @return 识别结果字符串
     */
    public String doOcr(Bitmap bitmap, String language) {
        TessBaseAPI baseApi = new TessBaseAPI();

        baseApi.init(getSDPath(), language);

        System.gc();
        // 必须加此行，tess-two要求BMP必须为此配置
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        baseApi.setImage(bitmap);

        String text = baseApi.getUTF8Text();

        baseApi.clear();
        baseApi.end();

        return text;
    }

    // 从string中得到short数据数组
    private short[] StringToBytes(String licString) {
        if (licString == null || licString.equals("")) {
            return null;
        }
        licString = licString.toUpperCase();
        int length = licString.length();
        char[] hexChars = licString.toCharArray();
        short[] d = new short[length];
        for (int i = 0; i < length; i++) {
            d[i] = (short) hexChars[i];
        }
        return d;
    }

}

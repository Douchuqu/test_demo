package com.hc.wxstcdemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.format.Formatter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bkrcl.control_car_video.camerautil.CameraCommandUtil;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.hc.wxstcdemo.client.Client;
import com.hc.wxstcdemo.service.SearchService;
import com.hc.wxstcdemo.utils.BitmapUtils;
import com.hc.wxstcdemo.utils.FileService;
import com.hc.wxstcdemo.utils.JudgeColorImpGround;
import com.hc.wxstcdemo.utils.RGBLuminanceSource;
import com.hc.wxstcdemo.utils.ShapeUtils;
import com.hc.wxstcdemo.utils.ToastUtils;
import com.hc.wxstcdemo.utils.TrafficUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import match.AssetsResource;
import match.ColorKMeans;
import match.Oritenation;
import match.RecEachCharInMinDis;
import match.SegInEachChar;

public class BebugActivity extends Activity {

    private static final int PHOTO_PICK = 0x11;//
    // WiFi������
    private WifiManager wifiManager;
    // ��������Ϣ
    private DhcpInfo dhcpInfo;
    private TextView resultView;
    private String cameraIP;
    private boolean flag;
    private CameraCommandUtil cameraCommandUtil;
    private ShapeUtils shapeutils;
    TrafficUtils trafficUtils;
    private EditText car_eidt;
    private Bitmap bitmap = null;

    // �˿ڷ�����
    private Client client;
    // �㲥����
    public static final String A_S = "com.a_s";
    // �㲥������
    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context arg0, Intent arg1) {
            cameraIP = arg1.getStringExtra("IP");
            progressDialog.dismiss();
            phThread.start();
        }

    };
    // �����߳̽�������ͷ��ǰͼƬ
    private Thread phThread = new Thread(new Runnable() {
        public void run() {
            // TODO Auto-generated method stub
            while (true) {
                if (flag) {
                    bitmap = cameraCommandUtil.httpForImage(cameraIP);
                    phHandler.sendEmptyMessage(10);
                }
            }
        }
    });
    // ��ʾͼƬ
    public Handler phHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 10) {
                if (bitmap != null)
                    img.setImageBitmap(bitmap);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bebug);
        // ע��㲥
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(A_S);

        registerReceiver(myBroadcastReceiver, intentFilter);
        init();
        cameraCommandUtil = new CameraCommandUtil();

        shapeutils = new ShapeUtils();
        trafficUtils = new TrafficUtils();

        // 4.0������� ���̷߳�������
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build());

        registerReceiver(myBroadcastReceiver, intentFilter);
        // �õ�WiFi��Ϣ
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // ȡ�÷�������Ϣ
        dhcpInfo = wifiManager.getDhcpInfo();
        // ȡ�÷�������IP��ַ
        String car_IP = intToIp(dhcpInfo.gateway);
        // ���ó�ʼ���Ŀؼ�
        client = new Client(this, false);
        client.connect(new Handler(), car_IP);
    }

    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
        System.exit(0);
    }

    // ��������
    private ProgressDialog progressDialog = null;

    // ��������cameraIP������
    private void search() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("����");
        progressDialog.show();
        Intent intent = new Intent();
        intent.setClass(BebugActivity.this, SearchService.class);
        startService(intent);
    }

    // ʶ���ά��
    public String Code(Bitmap bp) {
        Result result = null;
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

    private String[] items = {"��ɫ", "��ɫ", "��ɫ", "��ɫ", "��ɫ", "��ɫ", "��ɫ", "��ɫ"};
    private String[] items2 = {"LCD", "TFT"};
    private Button gearbtn;
    private Spinner typeView;
    private Spinner gearView;
    private int index;
    private LinearLayout line_layout;
    private ImageView imageView;
    Button btncar;
    Button btnsav;
    Button btntraffic;
    TextView left_txt;
    TextView right_txt;

    private void init() {

        img1 = (ImageView) findViewById(R.id.img1);
        img2 = (ImageView) findViewById(R.id.img2);
        img3 = (ImageView) findViewById(R.id.img3);
        img4 = (ImageView) findViewById(R.id.img4);
        img5 = (ImageView) findViewById(R.id.img5);
        img6 = (ImageView) findViewById(R.id.img6);

        left_txt = (TextView) findViewById(R.id.left_txt);
        right_txt = (TextView) findViewById(R.id.right_txt);

        btntraffic = (Button) findViewById(R.id.traffic_btn);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                BebugActivity.this, android.R.layout.simple_spinner_item, items);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
                BebugActivity.this, android.R.layout.simple_spinner_item, items2);
        gearbtn = (Button) findViewById(R.id.shapebtn4);
        shapebtn = (Button) findViewById(R.id.shapebtn5);
        line_layout = (LinearLayout) findViewById(R.id.lin_layout);
        gearView = (Spinner) findViewById(R.id.gearView);
        typeView = (Spinner) findViewById(R.id.type);
        btncar = (Button) findViewById(R.id.shapebtncar);
        car_eidt = (EditText) findViewById(R.id.car_edit);

        findViewById(R.id.LCD_turn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.picture(1);
            }
        });
        findViewById(R.id.TFT_turn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.TFT_Show(Client.TFT_picture, "�·�", null);
                Log.e("FATAL", "�·�");
            }
        });


        ////////////////////��ͨ��ʶ�𰴼�/////////////////////
        btntraffic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap qiege = trafficUtils.qiege(bitmap);
                img2.setImageBitmap(qiege);
                trafficUtils.shapeIdentification(qiege, new TrafficUtils.trafficListener() {
                    @Override
                    public void traffic(int left, int right, String result) {
                        left_txt.setText(left + "");
                        right_txt.setText(right + "");
                        ToastUtils.toast(BebugActivity.this, result);
                    }
                });
            }
        });

        /////////////////////���ձ��水��////////////////
        btnsav = (Button) findViewById(R.id.takepic);
        btnsav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileService.savePhoto(bitmap, System.currentTimeMillis() + ".jpg");

            }
        });
        /////////////////////����ʶ�𰴼�//////////////
        btncar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String s = car_eidt.getText().toString();
//                if (!s.equals("")) {
//                    SysParameter.hls_H = Float.valueOf(s);
//                    SysParameter.hls_L = Float.valueOf(s);
//                    SysParameter.hls_S = Float.valueOf(s);
//                }
                String carcode = carCode(bitmap, SysParameter.Read);
                if (carcode == null && carcode == "") {
                    ToastUtils.toast(getApplicationContext(), "δʶ��");
                } else {
                    ToastUtils.toast(getApplicationContext(), carcode);
                }
            }
        });
        gearView.setAdapter(adapter);
        typeView.setAdapter(adapter2);
        typeView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    SysParameter.Read = ShapeUtils.LCD;
                } else {
                    SysParameter.Read = ShapeUtils.TFT;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        gearView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                index = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        /////////////////������ɫ����/////////////////////////////
        gearbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Bitmap bimp = shapeutils.convertToBlack(shapeutils.initBitmap(bitmap, new JudgeColorImpGround(), SysParameter.Read), null, index);
                img1.setImageBitmap(bimp);
                Bitmap bimp2 = shapeutils.initBitmap(bitmap, new JudgeColorImpGround(), 3);
                img2.setImageBitmap(bimp2);
            }
        });

        ///////////////////ͼ��ʶ��////////////////////
        shapebtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ArrayList<Bitmap> bitmaplists = shapeutils.juzhenshape(shapeutils.initBitmap(bitmap, new JudgeColorImpGround(), SysParameter.Read), ShapeUtils.ALLCOLOR);
                if (bitmaplists.size() == 0) {
                    ToastUtils.toast(getApplicationContext(), "δʶ��");
                }
                line_layout.removeAllViews();
                String result = "";
                for (int i = 0; i < bitmaplists.size(); i++) {
                    imageView = new ImageView(getApplicationContext());
                    imageView.setPadding(5, 5, 5, 5);
                    imageView.setImageBitmap(bitmaplists.get(i));
                    line_layout.addView(imageView);
                    result += shapeutils.discernShap(bitmaplists.get(i));
                }
                resultView.setText(result);
            }
        });
        resultView = (TextView) findViewById(R.id.result);
        ;
        Button shapebutton = (Button) findViewById(R.id.shapebtn);
        img = (ImageView) findViewById(R.id.img);
        // bitmap = BitmapFactory.decodeResource(getResources(),
        // R.drawable.picture);
        // img.setImageBitmap(bitmap);
        findViewById(R.id.shapebtn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ArrayList<String> strings = CodeTwo(bitmap, Code_zy);
                if (strings != null) {
                    ToastUtils.toast(getApplicationContext(), "��ά��1" + strings.get(0) + "��ά��2:" + strings.get(1));
                } else {
                    ToastUtils.toast(getApplicationContext(), "δʶ��");
                }
            }
        });
        findViewById(R.id.shapebtnsx).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ArrayList<String> strings = CodeTwo(bitmap, Code_sx);
                if (strings != null) {
                    ToastUtils.toast(getApplicationContext(), "��ά��1" + strings.get(0) + "��ά��2:" + strings.get(1));
                } else {
                    ToastUtils.toast(getApplicationContext(), "δʶ��");
                }
            }
        });
        img.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                int x = (int) event.getX();
                int y = (int) event.getY();
                int pixel = bitmap.getPixel(x, y);
                int r = (pixel & 0xff0000) >> 16;
                int g = (pixel & 0xff00) >> 8;
                int b = (pixel & 0xff);
                ToastUtils.toast(BebugActivity.this, "�죺" + r + "��:" + g + "����"
                        + b);
                return false;
            }
        });
        img5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");                                        //ɾ����intent.putExtra��������Ƿ�ʵ�ֵ���ͼƬ�и�
                startActivityForResult(intent, PHOTO_PICK);
            }
        });
        img6.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                int x = (int) event.getX();
                int y = (int) event.getY();
                int pixel = bitmap.getPixel(x, y);
                int r = (pixel & 0xff0000) >> 16;
                int g = (pixel & 0xff00) >> 8;
                int b = (pixel & 0xff);
                ToastUtils.toast(BebugActivity.this, "�죺" + r + "��:" + g + "����"
                        + b);
                return false;
            }
        });
        findViewById(R.id.startbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                flag = true;
                if (cameraIP == null || cameraIP.equals(""))
                    search();
            }
        });
        // img.setOnClickListener(new selectButtonListener());


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED)
            return;
        // ������
        if (requestCode == 17) {
            bitmap = BitmapUtils.decodeUriAsBitmap(data.getData(), this);
            img6.setImageBitmap(bitmap);
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    String shapeResult = null;

    private ImageView img1;
    private ImageView img2;
    private ImageView img3;
    private ImageView img4;
    private ImageView img5;
    private ImageView img6;
    private ImageView img;
    private Button shapebtn;

    public final static int Code_sx = 0;
    public final static int Code_zy = 1;

    //ʶ�����¶�ά������Ҷ�ά��
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
                img1.setImageBitmap(bitmap1);
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
                img2.setImageBitmap(bitmap2);
                codelists.add(result2);
                prop = 1;
            }
        }
        if (result2 != null && result2.equals("null")) {
            return null;
        }
        if (codelists.size() == 2&&!codelists.get(0).equals(codelists.get(1))) {
            return codelists;
        }
        return null;
    }

//    // ʶ����
//    public String carCode(Bitmap bimp, int Read) {
//        String result = "";
//        Bitmap curbitmap;
//        Bitmap[] bitmaps;
//        try {
//            if (Read == shapeutils.LCD) {
//                bimp = shapeutils.screenshot(bimp, SysParameter.LCD_car_top, SysParameter.LCD_car_bottom, 0, 0);
//            } else if (Read == shapeutils.TFT) {
//                bimp = shapeutils.screenshot(bimp, SysParameter.TFT_car_top, SysParameter.TFT_car_bottom, 0, 0);
//            }
//            curbitmap = ColorKMeans.Math(bimp);
//            img1.setImageBitmap(curbitmap);
//            curbitmap = Oritenation.Math(curbitmap, bimp);
//            img2.setImageBitmap(curbitmap);
//            bitmaps = SegInEachChar.Math(curbitmap);
//            line_layout.removeAllViews();
//            for (int i = 0; i < bitmaps.length; i++) {
//                imageView = new ImageView(getApplicationContext());
//                imageView.setPadding(5, 5, 5, 5);
//                imageView.setImageBitmap(bitmaps[i]);
//                line_layout.addView(imageView);
//            }
//            AssetsResource.context = BebugActivity.this;
//            result = RecEachCharInMinDis.Math(bitmaps);
//        } catch (Exception e) {
//            result = "";
//        }
//        return result;
//    }

    /**
     * ��ȡsd����·��
     *
     * @return ·�����ַ���
     */
    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED); // �ж�sd���Ƿ����
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// ��ȡ���Ŀ¼
        }
        return sdDir.toString();
    }

    private String intToIp(int i) {

        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
                + "." + (i >> 24 & 0xFF);
    }

    //����ʶ����
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
                car_eidt.setText(x+"");
                return result;
            }
        }
        return result;
    }

    // ʶ����
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
            img1.setImageBitmap(curbitmap);
            curbitmap = Oritenation.Math(curbitmap, bimp);
            img2.setImageBitmap(curbitmap);
            bitmaps = SegInEachChar.Math(curbitmap);
            line_layout.removeAllViews();
            for (int i = 0; i < bitmaps.length; i++) {
                imageView = new ImageView(getApplicationContext());
                imageView.setPadding(5, 5, 5, 5);
                imageView.setImageBitmap(bitmaps[i]);
                line_layout.addView(imageView);
            }
            AssetsResource.context = BebugActivity.this;
            result = RecEachCharInMinDis.Math(bitmaps);
        } catch (Exception e) {
            result = "";
        }
        return result;
    }

}


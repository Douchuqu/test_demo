package com.hc.wxstcdemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bkrcl.control_car_video.camerautil.CameraCommandUtil;
import com.hc.wxstcdemo.client.Client;
import com.hc.wxstcdemo.service.SearchService;
import com.hc.wxstcdemo.utils.ToastUtils;

public class MainActivity extends Activity {
    private String[] items = {"1", "2", "3", "4", "5", "6", "7", "8", "9",
            "10", "11", "12", "13", "14", "15", "16"};
    // UI布局中摄像头的同类控件声明
    private ImageView image = null;
    public boolean flag = false;
    // 广播名称
    public static final String A_S = "com.a_s";
    // 摄像头IP端口
    public String IP = null;
    // 摄像头旋转方法类
    private CameraCommandUtil cameraCommandUtil = null;
    // 广播接收器
    // 搜索进度
    private ProgressDialog progressDialog = null;
    // WiFi管理器
    private WifiManager wifiManager;
    // 服务器信息
    private DhcpInfo dhcpInfo;
    // WiFi地址
    private String car_IP = null;
    // 端口方法类
    private Client client;
    private byte[] mByte = new byte[10];
    // 接受传感器
    long psStatus = 0;
    public long UltraSonic = 0;
    long Light = 0;
    long CodedDisk = 0;

    private Button gearbtn;
    private Spinner gearView;
    public int index;

    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            IP = arg1.getStringExtra("IP");
            Log.e("IP地址", IP);
            progressDialog.dismiss();
            phThread.start();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        // 4.0以上添加 主线程访问网络
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build());
        // 注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(A_S);
        registerReceiver(myBroadcastReceiver, intentFilter);

        // 得到WiFi信息
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // 取得服务器信息
        dhcpInfo = wifiManager.getDhcpInfo();
        // 取得服务器的IP地址
        car_IP = intToIp(dhcpInfo.gateway);

        Toast.makeText(getBaseContext(), "网关地址" + car_IP, Toast.LENGTH_SHORT)
                .show();
        // 获取本机IP
        car_IP = Formatter.formatIpAddress(dhcpInfo.gateway);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        Toast.makeText(getBaseContext(), "本机地址" + intToIp(ipAddress),
                Toast.LENGTH_SHORT).show();
        cameraCommandUtil = new CameraCommandUtil();
        init();
        search();
        // 调用初始化的控件
        client = new Client(this,true);
        client.connect(car_myHandler, car_IP);
    }

    private void init() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                MainActivity.this, android.R.layout.simple_spinner_item, items);
        gearbtn = (Button) findViewById(R.id.btn1);
        gearView = (Spinner) findViewById(R.id.gearView);
        gearView.setAdapter(adapter);

        image = (ImageView) findViewById(R.id.imageView);
        show = (TextView) findViewById(R.id.show);
        line_layout = (LinearLayout) findViewById(R.id.lin_layout);
        findViewById(R.id.start).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!flag) {
                    client.quanThread.start();
                    flag = true;
                }
            }
        });
        gearView.setOnItemSelectedListener(new OnItemSelectedListener() {
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
        gearbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        switch (index) {
                            case 1:
                                client.task1();
                                break;
                            case 2:
                                client.task2();
                                break;
                            case 3:
                                client.task3();
                                break;
                            case 4:
                                client.task4();
                                break;
                            case 5:
                                client.task5();
                                break;
                            case 6:
                                client.task6();
                                break;
                            case 7:
                                client.task7();
                                break;
                            case 8:
                                client.task8();
                                break;
                            case 9:
                                client.task9();
                                break;
                            case 10:
                                client.task10();
                                break;
                            case 11:
                                client.task11();
                                break;
                            case 12:
                                client.task12();
                                break;
                            case 13:
                                client.task13();
                                break;
                            case 14:
                                client.task14();
                                break;
                            case 15:
                                client.task15();
                                break;
                            case 16:
                                client.task16();
                                break;
                        }
                    }
                }.start();
            }
        });

    }

    ImageView imageView;

    public void addImgeView(final Bitmap bitmap) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView = new ImageView(getApplicationContext());
                imageView.setImageBitmap(bitmap);
                line_layout.addView(imageView);
            }
        });
    }

    public void addTextView(final String str) {
        if(str.equals("")) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final TextView textView = new TextView(getApplicationContext());
                textView.setTextColor(Color.BLACK);
                textView.setText(str);
                line_layout.addView(textView);
            }
        });
    }

    public static Bitmap bitmap = null;
    // 开启线程接受摄像头当前图片
    private Thread phThread = new Thread(new Runnable() {
        Bitmap bp;

        public void run() {
            Looper.prepare();
            while (true) {
                bp = cameraCommandUtil.httpForImage(IP);
                if (bp == null)
                    continue;
                else {
                    bitmap = bp;
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        image.setImageBitmap(bitmap);
                    }
                });
            }
        }
    });
    // 接受传感器
    public Handler car_myHandler = new Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                mByte = (byte[]) msg.obj;
                if (mByte[0] == 0x55 && mByte[1] == (byte) 0xaa) {
                    psStatus = mByte[3] & 0xff;
                    UltraSonic = mByte[5] & 0xff;
                    UltraSonic = UltraSonic << 8;
                    UltraSonic += mByte[4] & 0xff;

                    Light = mByte[7] & 0xff;
                    Light = Light << 8;
                    Light += mByte[6] & 0xff;

                    CodedDisk = mByte[9] & 0xff;
                    CodedDisk = CodedDisk << 8;
                    CodedDisk += mByte[8] & 0xff;

                    show.setText("主车信息   超声波：" + UltraSonic + "mm  光照：" + Light
                            + "lx" + "\n" + "  码盘：" + CodedDisk + " 光敏状态："
                            + psStatus + "  状态：" + (mByte[2] & 0xFF) + " mark值："
                            + client.mark);
                }
                if (mByte[0] == 0x55 && mByte[1] == (byte) 0x02) {
                    psStatus = mByte[3] & 0xff;
                    UltraSonic = mByte[5] & 0xff;
                    UltraSonic = UltraSonic << 8;
                    UltraSonic += mByte[4] & 0xff;

                    Light = mByte[7] & 0xff;
                    Light = Light << 8;
                    Light += mByte[6] & 0xff;

                    CodedDisk = mByte[9] & 0xff;
                    CodedDisk = CodedDisk << 8;
                    CodedDisk += mByte[8] & 0xff;

                    show.setText("从车信息   超声波：" + UltraSonic + "mm  光照：" + Light
                            + "lx" + "\n" + "  码盘：" + CodedDisk + " 光敏状态："
                            + psStatus + "  状态：" + (mByte[2] & 0xFF)
                            + " mark值：" + client.mark);
                }
            }
        }
    };
    private TextView show;
    private LinearLayout line_layout;

    private String intToIp(int i) {

        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
                + "." + (i >> 24 & 0xFF);
    }

    // 搜索摄像IP进度条
    private void search() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("搜索");
        progressDialog.show();
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SearchService.class);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        client.quanThread.stop();
        unregisterReceiver(myBroadcastReceiver);
    }

}

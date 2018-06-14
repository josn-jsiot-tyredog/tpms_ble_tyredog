/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sheng.tpms;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.media.AudioManager.STREAM_ALARM;
import static com.sheng.tpms.R.id.BtnDemo;
import static com.sheng.tpms.R.id.tViewVP4;
import static com.sheng.tpms.R.layout.port_activiyt_blescreenmain;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class blescreenMain extends Activity {
    private final static String TAG = blescreenMain.class.getSimpleName();

    private BluetoothGatt mBluetoothGatt;
//    static String strValue;
//    static{
//        strValue = "ken";
//    }

    int oldOrientation = -1;


    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

//    private TextView mConnectionState;
    private TextView mDataField;
    private boolean mGetDevice;
    private String mDeviceName;
    private String mDeviceAddress;
    private String Cmd_data;
    private ExpandableListView mGattServicesList;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
//    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private BluetoothGattCharacteristic mWriteCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    /////////////////////////////////////////////////////////////////////////

    int SETFLAG = 21;
    int CLOSE = 22;
    int W0 = 23;
    int W1 = 24;
    int W2 = 25;
    int W3 = 26;
    private String saveKey[]=new String[]{"FIRST","PUNIT","TUNIT","HPLIMIT","LPLIMIT","HTLIMIT","HPMAX","LPMAX","HTMAX","P1","P2","P3","P4","PP1","PP2","PP3","PP4","T1","T2","T3","T4","SETFLAG","CLOSE","W0","W1","W2","W3"};
    private int saveValue[]=new int[]{0,0,0,45,26,70,100,100,125,32,33,32,33,0,0,0,0,25,26,25,24,0,0,0,1,2,3};
    private int InitValue[]=new int[]{1,0,0,45,26,70,100,100,125,32,33,32,33,0,0,0,0,25,26,25,24,0,0,0,1,2,3};
    private int PressPoint[]=new int[]{1,0,2,2};
    private int TempPoint[]=new int[]{0,0};

    int AlarmSysV = 1;
    int NotiySysV = 2;
    int AlarmAppV = 3;
    int NotiyAppV = 4;
    int AlarmMax = 5;
    int NotiyMax = 6;
    int SETVFLAG = 7;
    int SAFLAG = 8;
    int AlarmFlag = 9;
    int NotiyFlag = 10;
    int CLOSEV = 11;
    String saveKeyV[]=new String[]{"Voice","AlarmSysV","NotiySysV","AlarmAppV","NotiyAppV","AlarmMax","NotiyMax","SETVFLAG","SAFLAG","AlarmFlag","NotiyFlag","CLOSEV"};
    int saveValueV[]=new int[]{0,0,0,0,0,0,0,0,1,1,1,0};
    int initValueV[]=new int[]{1,0,0,5,5,10,10,0,1,1,1,0};


    int Fac = 1;
    int Learn = 2;
    int Check = 3;
    int Cal = 4;
    int Test = 5;
    int BleTest = 6;
    int Emode = 7;
    int QA = 8;
    String saveKeyF[]=new String[]{"Mode","Fac","Learn","Check","Cal","TEST","BleTEST","Emode","QA"};
    int saveValueF[]=new int[]{0,0,0,0,0,0,0,0,0};
    int initValueF[]=new int[]{1,0,0,0,0,0,0,0,0};
    int getid_cnt = 0;
    int timer_cnt = 0;
    int Faccnt = 0;
    int CMD_CHK = 0;

    private int demo_cnt = 0;
    private int demo_cnt_k = 20;
    private int demoWheel[]=new int[]{0,0,1,1,2,2,3,3,2,2,1,1,0,0,0,0,0,0,0,0};
    private double demoPvalue[]=new double[]{24,32,50,33,32,32,33,32,35,33,32,34,36,38,42,48,55,46,39,32};
    private double demoTvalue[]=new double[]{25,25,26,24,75,28,26,25,25,26,25,26,25,25,25,25,25,25,25,25};
    private boolean demoVvalue[]=new boolean[]{false,false,false,false,false,false,true,false,false,false,false,false,false,false,false,false,false,false,false,false};


// 0  1  2  3  4  5  6  7  8  9  A  B  C  D  E  F  $  #
//48 49 50 51 52 53 54 55 56 57 65 66 67 68 69 70 36 35

    private String TM10 = "[36, 65, 48, 49, 48";           //$YY10
    private String TM11 = "[36, 65, 48, 49, 49";           //$YY11
    private String TM120 = "[36, 65, 48, 49, 50, 48, 48";  //$YY1200
    private String TM121 = "[36, 65, 48, 49, 50, 48, 49";  //$YY1201

    private String TC20  = "[36, 65, 48, 65, 48";          //$YY20
    private String TC210 = "[36, 65, 48, 65, 49, 48, 48";  //$YY2100
    private String TC211 = "[36, 65, 48, 65, 49, 48, 49";  //$YY2101
    private String TC22  = "[36, 65, 48, 65, 50, 48, 48";  //$YY2200
    private String TC23  = "[36, 65, 48, 65, 51, 48, 48";  //$YY2300
    private String TC240 = "[36, 65, 48, 65, 52, 48, 48";  //$YY2400
    private String TC241 = "[36, 65, 48, 65, 52, 48, 49";  //$YY2401
    private String TC25  = "[36, 65, 48, 65, 53, 48, 48";  //$YY2500

    private String TS30  = "[36, 65, 48, 51, 48";          //$YY30

    private String TC40  = "[36, 65, 48, 67, 48, 48, 48";  //$YY4000
    private String TC41  = "[36, 65, 48, 67, 49, 48, 48";  //$YY4100
    private String TC42  = "[36, 65, 48, 67, 50";          //$YY42
    private String TC43  = "[36, 65, 48, 67, 51, 48, 48";  //$YY4300
    private String TC44  = "[36, 65, 48, 67, 52";          //$YY44
    private String TC45  = "[36, 65, 48, 67, 53, 48, 49";  //$YY4501

    private String TC50  = "[36, 65, 48, 68, 48, 48, 48";  //$YY5000
    private String TC510 = "[36, 65, 48, 68, 49, 48, 48";  //$YY5100
    private String TC511 = "[36, 65, 48, 68, 49, 48, 49";  //$YY5101
    private String TC512 = "[36, 65, 48, 68, 49, 48, 50";  //$YY5102
    private String TC513 = "[36, 65, 48, 68, 49, 48, 51";  //$YY5103
    private String TC52  = "[36, 65, 48, 68, 50, 48, 48";  //$YY5200
    private String TC53  = "[36, 65, 48, 68, 51, 48, 48";  //$YY53000
    private String TC540 = "[36, 65, 48, 68, 52, 48, 48";  //$YY5400
    private String TC541 = "[36, 65, 48, 53, 52, 48, 49";  //$YY5401
    private String TC542 = "[36, 65, 48, 53, 52, 48, 50";  //$YY5402
    private String TC543 = "[36, 65, 48, 53, 52, 48, 51";  //$YY5403


    private String TESTRF = "[36, 84, 69, 83, 84, 82, 70, 35]";
    private String LEARN = "[36, 76, 69, 65, 82, 78, 35]";
    private String LEND = "[36, 76, 69, 78, 68, 35]";
    private String TEST = "[36, 84, 69, 83, 84, 35]";
    private int RFRXcnt = 0;





    final static int[] wheelon = {R.drawable.p_no_1_1,R.drawable.p_no_2_2,R.drawable.p_no_3_2,R.drawable.p_no_4_2};
    final static int[] wheeloff = {R.drawable.p_no_1_0,R.drawable.p_no_2_0,R.drawable.p_no_3_0,R.drawable.p_no_4_0};

    private int wheelN_k = 4;

    private double PressData[]=new double[wheelN_k];
    private double TempData[]=new double[wheelN_k];
    private double PressDataS[]=new double[wheelN_k];
    private int TempDataS[]=new int[wheelN_k];


    private double PressDataBuf[]=new double[wheelN_k];
    private double TempDataBuf[]=new double[wheelN_k];
    private boolean BtFlagBuf[]=new boolean[wheelN_k];

    private double PressHigh[]=new double[wheelN_k];
    private double PressLow[]=new double[wheelN_k];
    private double TempHigh[]=new double[wheelN_k];

    private double PressRHigh[]=new double[wheelN_k];
    private double PressRLow[]=new double[wheelN_k];
    private double TempRHigh[]=new double[wheelN_k];

//    private boolean GetFlag[]=new boolean[wheelN_k];
    private boolean RxFlag[]=new boolean[wheelN_k];
    private boolean BtFlag[]=new boolean[wheelN_k];

    private boolean PressHFlag[]=new boolean[wheelN_k];
    private boolean PressLFlag[]=new boolean[wheelN_k];
    private boolean TempHFlag[]=new boolean[wheelN_k];
    private boolean BtLowFlag[]=new boolean[wheelN_k];


    private boolean PressHRFlag[]=new boolean[wheelN_k];
    private boolean PressLRFlag[]=new boolean[wheelN_k];
    private boolean TempHRFlag[]=new boolean[wheelN_k];

    private boolean FLASH_WHEEL[]=new boolean[wheelN_k];
    private int CHANGE_WHEEL[]=new int[]{0,1,2,3};


    private int HIGHPRESS = 0;
    private int LOWPRESS = 1;
    private int HIGHTEMP = 2;
    private int LOWBT = 3;

    private double DeltaPress_k = 1;
    private double DeltaTemp_k = 1;

    private int alarmcnt = 0;
    private int alarmcnt_k = 0;
    private int alarmcnt_k1 = 10;
    private int alarmcnt_k2 = 15;

    private int RFTEST_CNT = 0;



    private double HPvalue,LPvalue,HTvalue;
    private double Punit_k[]=new double[]{1,6.895,0.06895,0.07031};
    private double Tunit_k0 = 1.8;
    private int Tunit_k1 = 32;

    public String prefile = "setdata";



    /////////////////////////////////////////////////////////////////////////
    private boolean notiyID_same_FLAG = false;

    private boolean SHOW_LOG = false;
    private boolean DEBUG_MODE = false;
    private boolean SHOW_PROTOCOL = false;
    private boolean screenPL = true;
    private boolean RXflash_enable = false;
    private boolean BLEint_FLAG = false;
    private boolean WriteFLAG = false;
    private boolean BleFstCmd = true;




    private int rxant_cnt = 0;
    private int sound_cnt = alarmcnt_k2;

    private int RxreflashTimer_k = 50;


    private int Iconjptime;
    private int Iconjptime_k = 50; //ms
    double icomjp0_k = 0.2;
    double icomjp1_k = 0.3;
    double icomjpcnt_k0 = 0.050;
    double icomjpcnt_k1 = 0.025;

    private int l_Iconjptime_k = 120; //ms
    double l_icomjp0_k = 0.025;
    double l_icomjp1_k = 0.3;
    double l_icomjpcnt_k0 = 0.00625;
    double l_icomjpcnt_k1 = 0.003125;


    double icomjpcnt = 0;
    private int Jpno_cnt = 0;
    private boolean iconjp_updn = true;



    private int voice_max;
    private int voice_value;
    private double voice_level;
    private static final int TONE_VOICE_LEVEL = 80; // 音量
    private static final int TONE_LENGTH_MS = 500; // 延遲時間
    private static final int TONE_LENGTH_MS_RF = 20; // 延遲時間
    long[] vibrate_noti = {0,100,200,300};
    private int ID_alarm = 1;
    private int ID_note = 2;

    private double screenWsize;
    private double screenHsize;
    /////////////////////////////////////////////

    private int textsizeP_k;
    private int textsizeP_k0 = 16;
    private int textsizeP_k1 = 15;
    private int textsizeP_k2 = 13;
    private int textsizeP_k3 = 11;
    private int textsizeP_k4 = 10;

    private int textsizeP_k5 = 48;
    private int textsizeP_k6 = 47;
    private int textsizeP_k7 = 45;
    private int textsizeP_k8 = 43;
    private int textsizeP_k9 = 42;


    private int textsizeT_k;
    private int textsizeT_k0 = 16;
    private int textsizeT_k1 = 15;
    private int textsizeT_k2 = 13;
    private int textsizeT_k3 = 11;
    private int textsizeT_k4 = 10;

    private int textsizeT_k5 = 40;
    private int textsizeT_k6 = 39;
    private int textsizeT_k7 = 37;
    private int textsizeT_k8 = 35;
    private int textsizeT_k9 = 34;

    private int textsizeN_k;
    private int textsizeN_k0 = 16;
    private int textsizeN_k1 = 15;
    private int textsizeN_k2 = 13;
    private int textsizeN_k3 = 11;
    private int textsizeN_k4 = 10;

    private int textsizeN_k5 = 16;
    private int textsizeN_k6 = 15;
    private int textsizeN_k7 = 13;
    private int textsizeN_k8 = 11;
    private int textsizeN_k9 = 10;
    /////////////////////////////////////////////
    private double MainSizeW_k = 0.5;
    private double MainTop_k = 0.2031;//0.2343;

    private double LogoSizeH_k = 0.078;
    private double LogoTop_k = 0.0244;

    private double ModeSizeW_k = 0.25;
    private double ModeBottom_k = 0;

    private double ValueSizeW_k = 0.2302;
    private double ValueSizeH_k = 0.1632;
    private double ValueRight_k = 0;
    private double ValueLeft_k = 0;
    private double ValueTop1_k = 0.2285;
    private double ValueTop2_k = 0.1632;

    private double AntSizeW_k = 0.1438;
    private double AntTop_k = 0.020;
    private double AntLeft_k = 0.070;

    private double TunitSizeH_k = 0.106;
    private double TunitTop_k = 0.000;
    private double TunitRight_k = 0.107;

    private double PunitSizeW_k = 0.211;
    private double PunitSizeH_k = 0.053;
    private double PunitTop_k = -0.030;
    private double PunitRight_k = 0.036;

    private double PressSizeW_k = 0.086;
    private double PressSizeH_k = 0.048;
    private double PressTop_k = 0.017;
    private double PressLeft_k = 0.013;

    private double TempSizeW_k = 0.086;
    private double TempSizeH_k = 0.048;
    private double TempBottom_k = 0.017;
    private double TempLeft_k = 0.013;

    private double NoSizeW_k = 0.1;
    private double NoSizeH_k = 0.056;
    private double NoTop_k = -0.070;
    private double NoLeft_k = 0.027;

    private double BtSizeW_k = 0.1;
    private double BtSizeH_k = 0.056;
    private double BtTop_k = -0.070;
    private double BtRight_k = -0.013;

    private double VPSizeW_k = 0.125;
    private double VPSizeH_k = 0.031;
    private double VPTop_k = 0.026;
    private double VPRight_k = 0;

    private double VTSizeW_k = 0.125;
    private double VTSizeH_k = 0.031;
    private double VTBottom_k = 0.026;
    private double VTRight_k = 0;
    /////////////////////////////////////////////

    private double l_TitleBar_k = 0.070;

    private double l_LogoSizeW_k = 0.2040;
    private double l_LogoSizeH_k = 0.117;
    private double l_LogoTop_k = 0.000;

    private double l_AntSizeH_k = 0.093;
    private double l_AntTop_k = 0.0156;
    private double l_AntLeft_k = 0.0327;

    private double l_PunitSizeW_k = 0.2049;
    private double l_PunitSizeH_k = 0.1318;
    private double l_PunitTop_k = 0.03;
    private double l_PunitLeft_k = 0.06;

    private double l_TunitSizeW_k = 0.1046;
    private double l_TunitSizeH_k = 0.1648;
    private double l_TunitTop_k = 0.0156;
    private double l_TunitLeft_k = 0.18;;

    private double l_SetSizeH_k = 0.15;//0.1116;
    private double l_SetTop_k = 0.0156;
    private double l_SetRight_k = 0.0327;

    private double l_VoiceSizeH_k = 0.15;//0.1116;
    private double l_VoiceTop_k = 0.0156;
    private double l_VoiceRight_k = 0.0327;

    private double l_ValueSizeW_k = 0.4262;
    private double l_ValueSizeH_k = 0.3200;
    private double l_ValueRight_k = 0.0429;
    private double l_ValueLeft_k = l_ValueRight_k;
    private double l_ValueTop1_k = LogoSizeH_k/8;
    private double l_ValueTop2_k = LogoSizeH_k/4;


    private double l_NoSizeW_k = 0.0696;
    private double l_NoSizeH_k = 0.1231;
    private double l_NoTop_k = 0;
    private double l_NoRight_k = 0;
    private double l_NoTop_k_1 = -0.2;


    private double l_BtSizeW_k = 0.0782;
    private double l_BtSizeH_k = 0.1;
    private double l_BtBottom_k = 0.0289;
    private double l_BtRight_k = 0.0061;

    private double l_PressSizeW_k = 0.0696;
    private double l_PressSizeH_k = 0.1231;
    private double l_PressTop_k = 0.0144;
    private double l_PressLeft_k = 0.0122;

    private double l_TempSizeW_k = 0.0696;
    private double l_TempSizeH_k = 0.1231;
    private double l_TempBottom_k = 0.0289;
    private double l_TempLeft_k = 0.0122;

    private double l_VPSizeW_k = l_ValueSizeW_k/2;//0.1352;
    private double l_VPSizeH_k = l_ValueSizeH_k/2;//0.1304;
    private double l_VPTop_k = -0.015;//-0.025;

    private double l_VTSizeW_k = l_ValueSizeW_k/2;//0.1229;
    private double l_VTSizeH_k = l_ValueSizeH_k/2;//0.1086;
    private double l_VTBottom_k = 0.0189;




    private EditText etRead;
    private ImageButton miBtnMain,miBtnLogo,miBtnMode1,miBtnMode2,miBtnMode3,miBtnMode4,miBtnAnt,miBtnTunit,miBtnPunit;
    private ImageButton miBtnPress1,miBtnPress2,miBtnPress3,miBtnPress4,miBtnTemp1,miBtnTemp2,miBtnTemp3,miBtnTemp4;
    private ImageButton miBtnNo1,miBtnNo2,miBtnNo3,miBtnNo4,miBtnBt1,miBtnBt2,miBtnBt3,miBtnBt4;
    private ImageButton miBtnSet,miBtnVoice;
    private Button mBtnDemo,mBtnLearn,mBtnLearx,mBtnLend,mBtnFac,mBtnCal,mBtnTest,mBtnVer,mBtnGetid,mBtnTimeron,mBtnTimeroff,mBtnTimer;
    private ScrollView miScVSet;
    private Spinner miSpinGetid,miSpinTimer;



    private TextView mtViewVP1,mtViewVP2,mtViewVP3,mtViewVP4,mtViewVT1,mtViewVT2,mtViewVT3,mtViewVT4;
    private RelativeLayout mrelayValue1,mrelayValue2,mrelayValue3,mrelayValue4;

    private int w1press,w2press,w3press,w4press;
    private int w1temp,w2temp,w3temp,w4temp;
    private boolean w1rx,w2rx,w3rx,w4rx;
    private boolean w1bt,w2bt,w3bt,w4bt;
    private boolean w1p,w2p,w3p,w4p;
    private boolean w1reflash,w2reflash,w3reflash,w4reflash;

    private int LimitPLow,LimitPHigh,LimitTHigh;
    private boolean w1pLow,w2pLow,w3pLow,w4pLow;
    private boolean w1pHigh,w2pHigh,w3pHigh,w4pHigh;
    private boolean w1tHigh,w2tHigh,w3tHigh,w4tHigh;
    private boolean alarmflag,alarmflag2;



    private Handler handler1 = new Handler(); //T1Enable
    private Handler handler2 = new Handler(); //FacEnable
    private Handler handler3 = new Handler(); //RxCharEnable
    private Handler handler4 = new Handler(); //IconjpTimer
    private Handler handler5 = new Handler(); //RxreflashTimer
    private Handler handler6 = new Handler(); //RxantTimer
    private Handler handler7 = new Handler(); //RxantTimerOff
    private Handler handler8 = new Handler(); //soundTimer
    private Handler handler9 = new Handler(); //ChksetTimer
    private Handler handler10 = new Handler(); //DemoTimer

















    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");

                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };          //須利用來取得service物件

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {         //廣播-確認
        @Override
        public void onReceive(Context context, Intent intent) {                 // rx data
//            Log.d(TAG, "onReceive");
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
//                mConnected = true;
//                updateConnectionState(R.string.connected);
//                invalidateOptionsMenu();
                RXflash_enable = true;
                Toast.makeText(blescreenMain.this, R.string.module_connected, Toast.LENGTH_LONG).show();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
//                mConnected = false;
//                updateConnectionState(R.string.disconnected);
//                invalidateOptionsMenu();
                RXflash_enable = false;
                Toast.makeText(blescreenMain.this, R.string.module_disconnected, Toast.LENGTH_LONG).show();
                BleInt();
//                clearUI();
//                Toast.makeText(blescreenMain.this, "Test2", Toast.LENGTH_SHORT).show();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
//                Toast.makeText(blescreenMain.this, "Test3", Toast.LENGTH_SHORT).show();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
//                Toast.makeText(blescreenMain.this, "Test4", Toast.LENGTH_SHORT).show();
            } else if (BluetoothLeService.ACTION_DATA_WriteERROR.equals(action)) {
                Toast.makeText(blescreenMain.this, "ACTION_DATA_WriteERROR", Toast.LENGTH_SHORT).show();
//                WriteFLAG = false;
            } else if (BluetoothLeService.ACTION_DATA_WriteSUCCESS.equals(action)) {
                Toast.makeText(blescreenMain.this, "ACTION_DATA_WriteSUCCESS", Toast.LENGTH_SHORT).show();
//                WriteFLAG = false;
            }
            else {
//                Toast.makeText(blescreenMain.this, "Test4", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    };


/*


    // If a given GATT characteristic is selected, check for supported features.  This sample
    // demonstrates 'Read' and 'Notify' features.  See
    // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
    // list of supported characteristic features.
    private final ExpandableListView.OnChildClickListener servicesListClickListner =             //選擇UUID
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                    if (mGattCharacteristics != null) {
                        final BluetoothGattCharacteristic characteristic =
                                mGattCharacteristics.get(groupPosition).get(childPosition);       //點選後由Arraylist取得"特徵"
                        final int charaProp = characteristic.getProperties();
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            Log.d(TAG, "PROPERTY_READ");
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.
                            if (mNotifyCharacteristic != null) {                                  //若已點選過需清掉
                                mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
//                                Toast.makeText(blescreenMain.this, "Test8", Toast.LENGTH_SHORT).show();
                            }
                            mBluetoothLeService.readCharacteristic(characteristic);               // remote device 讀資料
//                            Toast.makeText(blescreenMain.this, "Test7", Toast.LENGTH_SHORT).show();
                        }
//                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
//                            Log.d(TAG, "PROPERTY_WRITE");
//                            if (mNotifyCharacteristic != null) {
//                                mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, false);
//                                mNotifyCharacteristic = null;
//                            }

//                            mBluetoothLeService.writeCharacteristic(characteristic);
                            //characteristic.setValue(bytes);
                            //characteristic.setValue("testing");
                            //characteristic.setWriteType(BluetoothGattCharacteristic.PERMISSION_WRITE);
//                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            Log.d(TAG, "PROPERTY_NOTIFY");
                            mNotifyCharacteristic = characteristic;
                            mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, true);//開啟Notification
//                            Toast.makeText(blescreenMain.this, "Test6", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
//                    Toast.makeText(blescreenMain.this, "Test5", Toast.LENGTH_SHORT).show();
                    return false;
                }
            };

*/





    private void BleInt() {
        etRead.setText(R.string.no_data);
        timerdisable();
        CheckReScan();
    }


    private void clearUI() {
//        mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        etRead.setText(R.string.no_data);
        timerdisable();
//        Intent i = new Intent(blescreenMain.this,blescan.class);
//        startActivity(i);//開始跳往要去的Activity
        finish();
    }               //顯示更新-清空列表&"DATA"

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        loadpara();
        getVoicevalue();

        loadparaV();

        loadparaF();


//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 横屏
//        setRequestedOrientation(ActivityInfo .SCREEN_ORIENTATION_PORTRAIT);//竖屏

        if (saveValueF[Fac] ==1){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
            DEBUG_MODE = true;
            SHOW_PROTOCOL = true;
        }

        setContentView(port_activiyt_blescreenmain);
        getwindowPL();


        final Intent intent = getIntent();                                      //由DeviceScanActivity的Intent來
        mGetDevice = intent.getBooleanExtra("GetDevice",false);            //取得所選device的name
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);            //取得所選device的name
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);      //取得所選device的mac address



/////////////////////////////////////////////////////////////////////

//        soundDisable();

        getwindowsize();

        init_para();


        init_display();
        unit_update();

//        soundEnable();



        timerdisable();
        if (mGetDevice == true){

            handler1.removeCallbacks(T1enable);
//        handler1.postDelayed(ClrFaccnt, 1000);
            handler2.removeCallbacks(ClrFaccnt);
//        handler2.postDelayed(ClrFaccnt, 3000);

            handler3.removeCallbacks(RxCharTimer);
//        handler3.postDelayed(RxCharTimer, 500);

            handler4.removeCallbacks(IconjpTimer);
//        handler4.postDelayed(IconjpTimer, Iconjptime);
            handler5.removeCallbacks(RxreflashTimer);
//        handler5.postDelayed(RxreflashTimer, 500);
            handler6.removeCallbacks(RxanticonTimer);
            handler6.postDelayed(RxanticonTimer, 250);
            handler7.removeCallbacks(RxanticonTimerOff);
//        handler7.postDelayed(RxanticonTimerOff, 2000);
            handler8.removeCallbacks(soundTimer);
            handler8.postDelayed(soundTimer, 5000);
        }else {
            if (saveValueF[Fac] != 1){
                handler10.removeCallbacks(DemoTimer);
                handler10.postDelayed(DemoTimer, 10000);
            }
        }
        handler9.removeCallbacks(ChksetTimer);
        handler9.postDelayed(ChksetTimer, 1000);

/////////////////////////////////////////////////////////////////////


        // Sets up UI references.
//        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
//        mGattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
//        mGattServicesList.setOnChildClickListener(servicesListClickListner);
//        mConnectionState = (TextView) findViewById(R.id.connection_state);
//        mDataField = (TextView) findViewById(R.id.data_value);

//        getActionBar().setTitle(mDeviceName);                                   //顯示device的name
//        getActionBar().setDisplayHomeAsUpEnabled(true);                         //開啟返回箭頭顯示

        if (mGetDevice == true){
            Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);  //Server綁定
            bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);//Server連結
            registerbleReceiver();


        }

        Log.d(TAG, "onCreate");
    }//onCreate




    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }



    @Override
    protected void onStart() {
        super.onStart();
            Log.d(TAG, "onStart");
    }


    //由別的activity回來
    @Override
    protected void onResume() {                 //註冊 mGattUpdateReceiver　可收Service的廣播
        super.onResume();
//        registerbleReceiver();
        Log.d(TAG, "onResume");
    }


    //往別的activity
    //返回
    @Override
    protected void onPause() {                  //撤銷 mGattUpdateReceiver
        super.onPause();
//        unregisterbleReceiver();
        Log.d(TAG, "onPause");
    }

    //返回
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }
    //返回
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterbleReceiver();                //撤銷mGattUpdateReceiver & 停止service
//        unbindService(mServiceConnection);      //停止service
//        mBluetoothLeService = null;
        MainClose();
        Log.d(TAG, "onDestroy");
    }



    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res.getConfiguration().fontScale != 1) {//非默认值
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //檢測修改字型大小設定
        if (newConfig.fontScale != 1) getResources();

        // 检测屏幕的方向：纵向或横向
        if(oldOrientation != newConfig.orientation)
        {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
            {
                if (saveValueF[Fac] != 1){
                    setContentView(R.layout.land_activiyt_blescreenmain);
                    getwindowsize();
                    init_display();
                    l_Reflash();
                    display_unit();
                }else{
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
//                    setContentView(R.layout.port_activiyt_blescreenmain);
//                    getwindowsize();
//                    init_display();
//                    Reflash();
//                    display_unit();
                }
//                Log.d(TAG, "ORIENTATION_LANDSCAPE");
            }else {
                setContentView(R.layout.port_activiyt_blescreenmain);
                getwindowsize();
                init_display();
                Reflash();
                display_unit();
//                Log.d(TAG, "ORIENTATION_PORTRAIT");
            }
            oldOrientation = newConfig.orientation;
        }
        icomjpcnt = 0;
        iconjp_updn = true;
    }



    protected void registerbleReceiver(){
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
//            Log.d(TAG, "Connect request result=" + result);
        }
    }
    protected void unregisterbleReceiver(){
        if (mGetDevice == true){
            unregisterReceiver(mGattUpdateReceiver);
            unbindService(mServiceConnection);      //停止service
            mBluetoothLeService = null;
        }
//        Log.d(TAG, "UnregisterReceiver");
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.gatt_services, menu);
//        if (mConnected) {
//            menu.findItem(R.id.menu_connect).setVisible(false);
//            menu.findItem(R.id.menu_disconnect).setVisible(true);
//        } else {
//            menu.findItem(R.id.menu_connect).setVisible(true);
//            menu.findItem(R.id.menu_disconnect).setVisible(false);
//        }
//        return true;
//    }                 //更新標題

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch(item.getItemId()) {
//            case R.id.menu_connect:
//                mBluetoothLeService.connect(mDeviceAddress);            //連結device
//                return true;
//            case R.id.menu_disconnect:
//                mBluetoothLeService.disconnect();                       //斷開device
//                return true;
//            case android.R.id.home:
//                onBackPressed();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }           //標提列觸發案

//    private void updateConnectionState(final int resourceId) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mConnectionState.setText(resourceId);
//            }
//        });
//    }      //更新"狀態"



    private void loadpara() {
        for (int i = 0; i < saveKey.length; i++) {
            saveValue[i] = GetPara(saveKey[i]);
//            Log.d(TAG,"loadpara"+":"+String.valueOf(saveValue[i]));
        }
        if (saveValue[0] == 999 || saveValue[0] == 0) {
            for (int i = 0; i < saveKey.length; i++) {
                saveValue[i] = InitValue[i];
            }
            savepara();
        }
        loadwheel();
    }
    private void loadparaV(){
        for (int i=0; i<saveKeyV.length; i++) {
            saveValueV[i] = GetPara(saveKeyV[i]);
            Log.d(TAG,"loadparaV"+":"+String.valueOf(saveValueV[i]));
        }
        if (saveValueV[0] == 999 || saveValueV[0] == 0){
            for (int i=0; i<saveKeyV.length; i++) {
                saveValueV[i] = initValueV[i];
            }
            getVoicevalue();
            saveparaV();
        }
//        getVoicevalue();

        setVoice();

    }

    private void loadparaF(){
        for (int i=0; i<saveKeyF.length; i++) {
            saveValueF[i] = GetPara(saveKeyF[i]);
//            Log.d(TAG,"loadparaF"+":"+String.valueOf(saveValueF[i]));
        }
        if (saveValueF[0] == 999 || saveValueF[0] == 0){
            for (int i=0; i<saveKeyF.length; i++) {
                saveValueF[i] = initValueF[i];
            }
            saveparaF();
        }
    }



    private void savepara(){
        for (int i=0; i<saveKey.length; i++) {
            SetPara(saveKey[i],saveValue[i]);
        }
    }
    private void saveparaV(){
        for (int i=0; i<saveKeyV.length; i++) {
            SetPara(saveKeyV[i],saveValueV[i]);
        }
    }
    private void saveparaF(){
        for (int i=0; i<saveKeyF.length; i++) {
            SetPara(saveKeyF[i],saveValueF[i]);
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////
    private void SetPara(String key,int value){
        PutPara(prefile,key,value);
    }
    private int GetPara(String key){
        return OutPara(prefile,key);
    }
    private void ClrPara(String key){
        DelPara(prefile,key);
    }
    private void ClrAllPara(){
        DelAllPara(prefile);
    }

    private void PutPara(String profile,String key,int value){
        SharedPreferences perf = getSharedPreferences(profile,MODE_PRIVATE);
        perf.edit()
                .putInt(key, value)
                .commit();
    }
    private int OutPara(String prefile,String key){
        SharedPreferences perf = getSharedPreferences(prefile,MODE_PRIVATE);
        int data = perf.getInt(key,999);
        return data;
    }
    private void DelPara(String prefile,String key){
        SharedPreferences perf = getSharedPreferences(prefile,MODE_PRIVATE);
        perf.edit()
                .remove(key)
                .commit();
    }
    private void DelAllPara(String prefile){
        SharedPreferences perf = getSharedPreferences(prefile,MODE_PRIVATE);
        perf.edit()
                .clear()
                .commit();
    }
/////////////////////////////////////////////////////////////////////////////////////

    private  void unit_vupdate(){
        loadparaV();
   }



    private  void unit_update(){
        loadpara();
        //PRESS
        display_unitP();
        for (int i=0; i<wheelN_k; i++){
            PressDataS[i] = PutPointN(PressData[i]*Punit_k[saveValue[1]],PressPoint[saveValue[1]]);
        }
        mtViewVP1.setText(String.valueOf(PressDataS[0]));
        mtViewVP2.setText(String.valueOf(PressDataS[1]));
        mtViewVP3.setText(String.valueOf(PressDataS[2]));
        mtViewVP4.setText(String.valueOf(PressDataS[3]));
        mtViewVP1.setTextColor(this.getResources().getColor(R.color.white));
        mtViewVP2.setTextColor(this.getResources().getColor(R.color.white));
        mtViewVP3.setTextColor(this.getResources().getColor(R.color.white));
        mtViewVP4.setTextColor(this.getResources().getColor(R.color.white));
        //TEMP
        display_unitT();
        mtViewVT1.setText(String.valueOf(TempDataS[0]));
        mtViewVT2.setText(String.valueOf(TempDataS[1]));
        mtViewVT3.setText(String.valueOf(TempDataS[2]));
        mtViewVT4.setText(String.valueOf(TempDataS[3]));
        mtViewVT1.setTextColor(this.getResources().getColor(R.color.white));
        mtViewVT2.setTextColor(this.getResources().getColor(R.color.white));
        mtViewVT3.setTextColor(this.getResources().getColor(R.color.white));
        mtViewVT4.setTextColor(this.getResources().getColor(R.color.white));
    }

    private void display_unit(){
        display_unitP();
        display_unitT();
    }

    private void display_unitP(){
        if (saveValue[1] == 0){
            if (screenPL==true){
                miBtnPunit.setImageResource(R.drawable.p_unitp_psi_0);
            }else {
                miBtnPunit.setImageResource(R.drawable.l_unitp_psi_0);
            }
        }else if (saveValue[1] == 1){
            if (screenPL==true){
                miBtnPunit.setImageResource(R.drawable.p_unitp_kpa_0);
            }else {
                miBtnPunit.setImageResource(R.drawable.l_unitp_kpa_0);
            }
        }else if (saveValue[1] == 2){
            if (screenPL==true){
                miBtnPunit.setImageResource(R.drawable.p_unitp_bar_0);
            }else {
                miBtnPunit.setImageResource(R.drawable.l_unitp_bar_0);
            }
        }else if (saveValue[1] == 3){
            if (screenPL==true){
                miBtnPunit.setImageResource(R.drawable.p_unitp_kgcm_0);
            }else {
                miBtnPunit.setImageResource(R.drawable.l_unitp_kgcm_0);
            }
        }else{
        }
    }
    private void display_unitT(){
        if (saveValue[2] == 0){
            if (screenPL==true){
                miBtnTunit.setImageResource(R.drawable.p_unitt_c_0);
            }else {
                miBtnTunit.setImageResource(R.drawable.l_unitt_c_0);
            }
            for (int i=0; i<wheelN_k; i++){
                TempDataS[i] = (int)PutPointN(TempData[i],TempPoint[saveValue[2]]);
            }
        }else if (saveValue[2] == 1){
            if (screenPL==true){
                miBtnTunit.setImageResource(R.drawable.p_unitt_f_0);
            }else {
                miBtnTunit.setImageResource(R.drawable.l_unitt_f_0);
            }
            for (int i=0; i<wheelN_k; i++){
                TempDataS[i] = (int)PutPointN(((TempData[i]*Tunit_k0)+Tunit_k1),TempPoint[saveValue[2]]);
            }
        }else{
        }
    }

    private void displayData(String data) {
        if (data != null) {
            etRead.setText(data);
//            String[] databuf2 = data.toString().split("");
//            String[] databuf2 = data.split(",");
            int[] databuf= {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
            for(int i=0;i<data.length();++i){
                databuf[i] = (int) (data.charAt(i));
            }
//            int rxstart = (int) (databuf2[1].charAt(0));
//            etRead.setText((databuf2[1])+(databuf2[12])+Integer.toString(rxstart)+Integer.toString(databuf[11]));

            int rxlen = 0;


            boolean rx_start = false;
            boolean rx_end = false;
            for (int i=0; i<databuf.length; i++){
                if (rx_end == false){
                    if (rx_start == false){
                        if (databuf[i] == 0x24){
                            rx_start = true;
//                            CallLOG("rx_start");
                            rxlen = 1;
                        }
                    }else{
                        if (databuf[i] == 0x23){
                            rx_end = true;
//                            CallLOG("rx_end");
                            rxlen = rxlen+1;
                        }else if(databuf[i] != 0x24){
                            rxlen = rxlen+1;
                        }
                    }
                }else{

                }
            }

//            CallLOG(rxlen);
//            CallLOG(databuf.length);

            int rxlength = 0;
            int[] rxdata = new int[rxlen];  // 動態配置長度
            if (rx_end == false){
                return;
            }else{
                rx_start = false;
                for (int i=0; i<databuf.length; i++){
                    if (rxlength < rxlen) {
                        if (rx_start==false){
                            if (databuf[i] == 0x24) {
                                rxlength = 1;
                                rx_start = true;
                                rxdata[rxlength-1] = databuf[i];
//                                CallLOG("rx_start");
                            }
                        }else{
                            rxlength = rxlength + 1;
                            rxdata[rxlength-1] = databuf[i];
//                            CallLOG(rxlength);
                        }
                    }else{
//                        CallLOG("over");
                    }
                }
            }

            String rxstr = Arrays.toString(rxdata);
            CallLOG(rxstr);
//            String aaaa = String.valueOf(rxdata);
//            CallLOG(rxstr);




///////////////////////////////////////////////
//New protocol
            if (rxstr.indexOf(TC20) > -1) {
                CallLOG("Read TireStatus");
                displaydemo("Read OK");
                return;
            }else if (rxstr.indexOf(TC210) > -1) {
                CallLOG("Enter To Normal Mode");
                displaydemo("LEND");
                init_RXFLAG();
                mBtnDemo.setText("NORMAL");
                return;
            }else if (rxstr.indexOf(TC211) > -1){
                CallLOG("Enter To Learning Mode");
                displaydemo("LEARN");
                init_RXFLAG();
                return;
            }else if (rxstr.indexOf(TC22) > -1){
                CallLOG("Set Tire Alarm Threshood");
                displaydemo("Set OK");
                return;
            }else if (rxstr.indexOf(TC23) > -1){
                CallLOG("Clear Received Flag");
                displaydemo("Clear OK");
                return;
            }else if (rxstr.indexOf(TC240) > -1){
                CallLOG("Set Auto Tx TireStatus Stop");
                displaydemo("Set OK");
                return;
            }else if (rxstr.indexOf(TC241) > -1){
                CallLOG("Set Auto Tx TireStatus Start");
                displaydemo("Set OK");
                return;
            }else if (rxstr.indexOf(TC25) > -1){
                CallLOG("Read Alarm Set");
                displaydemo("Read OK");
                return;
            }else if (rxstr.indexOf(TS30) > -1){
                CallLOG("Test Rfcode has Be Received");
                RFTEST_CNT = RFTEST_CNT + 1;
                if (RFTEST_CNT == 256) RFTEST_CNT = 0;
                displaydemo("RF TEST " + String.valueOf(RFTEST_CNT));
                return;
            }else if (rxstr.indexOf(TC40) > -1){
                CallLOG("Read MD Information");
                displaydemo("Read OK");
                return;
            }else if (rxstr.indexOf(TC41) > -1){
                CallLOG("Read MD ID");
                displaydemo("Read OK");
                return;
            }else if (rxstr.indexOf(TC42) > -1){
                CallLOG("Read Sensor ID");
                displaydemo("Read OK");
                return;
            }else if (rxstr.indexOf(TC43) > -1){
                CallLOG("Read RFRX Freq");
                displaydemo("Read OK");
                return;
            }else if (rxstr.indexOf(TC44) > -1){
                CallLOG("Set Tire Data");
                displaydemo("Set OK");
                return;
            }else if (rxstr.indexOf(TC45) > -1){
                CallLOG("nter To Learnning start code Mode");
                displaydemo("LEARX");
                init_RXFLAG();
                return;
            }else if (rxstr.indexOf(TC50) > -1){
                CallLOG("Enter To Factory Mode");
                displaydemo("Set OK");
                mBtnDemo.setText("FAC");
                return;
            }else if (rxstr.indexOf(TC510) > -1){
                CallLOG("Set Auto Tx TireStatus Period Time");
                displaydemo("Set OK");
                return;
            }else if (rxstr.indexOf(TC511) > -1){
                CallLOG("Set No TireStaus Received Time");
                displaydemo("Set OK");
                return;
            }else if (rxstr.indexOf(TC512) > -1){
                CallLOG("Set Tire Amount");
                displaydemo("Set OK");
                return;
            }else if (rxstr.indexOf(TC513) > -1){
                CallLOG("Set Module MDFlag");
                displaydemo("Set OK");
                return;
            }else if (rxstr.indexOf(TC52) > -1){
                CallLOG("Set Module ID");
                displaydemo("Set OK");
                return;
            }else if (rxstr.indexOf(TC53) > -1){
                CallLOG("Set Module Rf Freq");
                displaydemo("Set OK");
                return;
            }else if (rxstr.indexOf(TC540) > -1){
                CallLOG("Module Calibration Start");
                displaydemo("CAL START");
                mBtnDemo.setText("開始校正");
                return;
            }else if (rxstr.indexOf(TC541) > -1){
                CallLOG("RF CAL...");
                displaydemo("CAL..");
                mBtnDemo.setText("校正中..");
                return;
            }else if (rxstr.indexOf(TC542) > -1){
                CallLOG("RF CAL DONE");
                displaydemo("CAL OK");
                mBtnDemo.setText("校正完成");
                return;
            }else if (rxstr.indexOf(TC543) > -1){
                CallLOG("RF CAL ERROR");
                displaydemo("CAL ERROR");
                mBtnDemo.setText("校正失敗");
                return;
            }else if (rxstr.indexOf(TM120) > -1){
                CallLOG("Enter To Normal Mode");
                displaydemo("LEND");
                init_RXFLAG();
                return;
            }else if (rxstr.indexOf(TM121) > -1){
                CallLOG("Enter To Learning Mode");
                displaydemo("LEARN");
                init_RXFLAG();
                return;
            }else if ((rxstr.indexOf(TM10) > -1) || (rxstr.indexOf(TM11) > -1)){
                CallLOG("DATA");
                int chksumbuf = 0;
                int len = 16;
                int rxstart = rxdata[0] & 0xff;
                int rxend = rxdata[15] & 0xff;

                if (rxstart == 0x24 & rxend == 0x23) {
//                etRead.setText(data);
                    for (int j = 1; j < len - 3; j++) {
                        chksumbuf = (rxdata[j] & 0xff) + (chksumbuf & 0xff);
                    }

                    int rxchk0 = 0;
                    int rxchk1 = 0;
                    if ((rxdata[13] >= 65) & (rxdata[13] <= 70)) {
                        rxchk1 = rxdata[13] - 65 + 10;
                    } else if ((rxdata[13] >= 48) & (rxdata[13] <= 57)) {
                        rxchk1 = rxdata[13] - 48;
                    }
                    if ((rxdata[14] >= 65) & (rxdata[14] <= 70)) {
                        rxchk0 = rxdata[14] - 65 + 10;
                    } else if ((rxdata[14] >= 48) & (rxdata[14] <= 57)) {
                        rxchk0 = rxdata[14] - 48;
                    }
                    int chksum = rxchk1 * 16 + rxchk0;

                    if (chksum == chksumbuf) {
//                    Log.d(TAG, "Chksum match !");
//                    Log.d(TAG, "TPMS Rxdata : " + rxdata.toString());

//                            len_b.setText(rxdata.toString());

                        int[] rx = rxdata;
                        int wheel = rx[6];


                        boolean wbt = false;
                        if (Byte2toFlag(rx[7],rx[8],1) == true) {
                            wbt = true;
                        }
                        boolean wrx = false;
                        if (Byte2toFlag(rx[7],rx[8],7) == true) {
                            wrx = true;
                        }
                        boolean wpt = false;
                        if (Byte2toFlag(rx[7],rx[8],0) == true) {
                            wpt = true;
                        }
//                            int press = (((rx[5] - 48) * 16) + (rx[6] - 48));
//                            int temp = ((((rx[7] - 48) * 16) + (rx[8] - 48)) - 40);

                        int press0 = 0;
                        int press1 = 0;
                        if ((rxdata[9] >= 65) & (rxdata[9] <= 70)) {
                            press1 = rxdata[9] - 65 + 10;
                        } else if ((rxdata[9] >= 48) & (rxdata[9] <= 57)) {
                            press1 = rxdata[9] - 48;
                        }
                        if ((rxdata[10] >= 65) & (rxdata[10] <= 70)) {
                            press0 = rxdata[10] - 65 + 10;
                        } else if ((rxdata[10] >= 48) & (rxdata[10] <= 57)) {
                            press0 = rxdata[10] - 48;
                        }
                        int press = ((press1 * 16) + press0);

                        int temp0 = 0;
                        int temp1 = 0;
                        if ((rxdata[11] >= 65) & (rxdata[11] <= 70)) {
                            temp1 = rxdata[11] - 65 + 10;
                        } else if ((rxdata[11] >= 48) & (rxdata[11] <= 57)) {
                            temp1 = rxdata[11] - 48;
                        }
                        if ((rxdata[12] >= 65) & (rxdata[12] <= 70)) {
                            temp0 = rxdata[12] - 65 + 10;
                        } else if ((rxdata[12] >= 48) & (rxdata[12] <= 57)) {
                            temp0 = rxdata[12] - 48;
                        }
                        int temp = (((temp1 * 16) + temp0) -40);

//                            Toast.makeText(serialscreen.this, String.valueOf(rx[7]), Toast.LENGTH_SHORT).show();
//                            Toast.makeText(serialscreen.this, String.valueOf(temp), Toast.LENGTH_SHORT).show();

                        int wheel_N = 0;
                        switch (wheel) {
                            case 0x30:
                                wheel_N=0 ;
                                break;
                            case 0x31:
                                wheel_N=1 ;
                                break;
                            case 0x32:
                                wheel_N=2 ;
                                break;
                            case 0x33:
                                wheel_N=3 ;
                                break;
                            case 0x34:
                                wheel_N=4 ;
                                break;
                            case 0x35:
                                wheel_N=5 ;
                                break;
                        }

                        double pressure = 0;
                        if (wpt == true){
                            pressure = press + 0.5;
                        } else {
                            pressure = press + 0.0;
                        }
                        PressData[wheel_N] = pressure;
                        TempData[wheel_N] = temp;
                        RxFlag[wheel_N] = wrx;
                        BtFlag[wheel_N] = wbt;
//                    GetFlag[wheel_N] = true;

                        checkdata(wheel_N);
                        handler5.postDelayed(RxreflashTimer, RxreflashTimer_k);

                    }
                }
            }else{

            }
//New protocol
///////////////////////////////////////////////


///////////////////////////////////////////////
//Old protocol
            if (rxstr.indexOf(LEARN) > -1) {
                CallLOG("LEARN");
                displaydemo("LEARN");
                init_RXFLAG();
                return;
            }

            if (rxlength == 8) {
                if (rxstr.indexOf(TESTRF) > -1) {
                    CallLOG("TESTRF");
                    displaydemo("TESTRF");
//                    setsound_RF();
                    displayRF();
                    return;
                }
            }else if(rxlength == 6){
                if (rxstr.indexOf(TEST) > -1) {
                    CallLOG("TEST");
                    displaydemo("TEST");
                    init_RXFLAG();
                    return;
                }else if (rxstr.indexOf(LEND) > -1){
                    CallLOG("LEND");
                    displaydemo("LEND");
                    return;
                }
            }else if(rxlength == 7){
                if (rxstr.indexOf(LEARN) > -1) {
                    CallLOG("LEARN");
                    displaydemo("LEARN");
                    init_RXFLAG();
                    return;
                }
            }else if(rxlength == 10){
                if (rxstr.indexOf(LEARN) > -1) {
                    CallLOG("LEARN");
                    displaydemo("LEARN");
                    init_RXFLAG();
                    return;
                }
            }else if(rxlength == 12){
                CallLOG("DATA");
                int chksumbuf = 0;
                int len = 12;
                int rxstart = rxdata[0] & 0xff;
                int rxend = rxdata[11] & 0xff;

                if (rxstart == 0x24 & rxend == 0x23) {
//                etRead.setText(data);
                    for (int j = 1; j < len - 3; j++) {
                        chksumbuf = (rxdata[j] & 0xff) + (chksumbuf & 0xff);
                    }

                    int rxchk0 = 0;
                    int rxchk1 = 0;
                    if ((rxdata[9] >= 65) & (rxdata[9] <= 70)) {
                        rxchk1 = rxdata[9] - 65 + 10;
                    } else if ((rxdata[9] >= 48) & (rxdata[9] <= 57)) {
                        rxchk1 = rxdata[9] - 48;
                    }
                    if ((rxdata[10] >= 65) & (rxdata[10] <= 70)) {
                        rxchk0 = rxdata[10] - 65 + 10;
                    } else if ((rxdata[10] >= 48) & (rxdata[10] <= 57)) {
                        rxchk0 = rxdata[10] - 48;
                    }
                    int chksum = rxchk1 * 16 + rxchk0;

                    if (chksum == chksumbuf) {
//                    Log.d(TAG, "Chksum match !");
//                    Log.d(TAG, "TPMS Rxdata : " + rxdata.toString());

//                            len_b.setText(rxdata.toString());

                        int[] rx = rxdata;
                        int wheel = rx[1];
                        boolean wbt = false;
                        if (rx[2] == 0x31) {
                            wbt = true;
                        }
                        boolean wrx = false;
                        if (rx[3] == 0x31) {
                            wrx = true;
                        }
                        boolean wpt = false;
                        if (rx[4] == 0x35) {
                            wpt = true;
                        }
//                            int press = (((rx[5] - 48) * 16) + (rx[6] - 48));
//                            int temp = ((((rx[7] - 48) * 16) + (rx[8] - 48)) - 40);

                        int press0 = 0;
                        int press1 = 0;
                        if ((rxdata[5] >= 65) & (rxdata[5] <= 70)) {
                            press1 = rxdata[5] - 65 + 10;
                        } else if ((rxdata[5] >= 48) & (rxdata[5] <= 57)) {
                            press1 = rxdata[5] - 48;
                        }
                        if ((rxdata[6] >= 65) & (rxdata[6] <= 70)) {
                            press0 = rxdata[6] - 65 + 10;
                        } else if ((rxdata[6] >= 48) & (rxdata[6] <= 57)) {
                            press0 = rxdata[6] - 48;
                        }
                        int press = ((press1 * 16) + press0);

                        int temp0 = 0;
                        int temp1 = 0;
                        if ((rxdata[7] >= 65) & (rxdata[7] <= 70)) {
                            temp1 = rxdata[7] - 65 + 10;
                        } else if ((rxdata[7] >= 48) & (rxdata[7] <= 57)) {
                            temp1 = rxdata[7] - 48;
                        }
                        if ((rxdata[8] >= 65) & (rxdata[8] <= 70)) {
                            temp0 = rxdata[8] - 65 + 10;
                        } else if ((rxdata[8] >= 48) & (rxdata[8] <= 57)) {
                            temp0 = rxdata[8] - 48;
                        }
                        int temp = (((temp1 * 16) + temp0) -40);

//                            Toast.makeText(serialscreen.this, String.valueOf(rx[7]), Toast.LENGTH_SHORT).show();
//                            Toast.makeText(serialscreen.this, String.valueOf(temp), Toast.LENGTH_SHORT).show();

                        int wheel_N = 0;
                        switch (wheel) {
                            case 0x30:
                                wheel_N=0 ;
                                break;
                            case 0x31:
                                wheel_N=1 ;
                                break;
                            case 0x32:
                                wheel_N=2 ;
                                break;
                            case 0x33:
                                wheel_N=3 ;
                                break;
                        }
                        double pressure = 0;
                        if (wpt == true){
                            pressure = press + 0.5;
                        } else {
                            pressure = press + 0.0;
                        }
                        PressData[wheel_N] = pressure;
                        TempData[wheel_N] = temp;
                        RxFlag[wheel_N] = wrx;
                        BtFlag[wheel_N] = wbt;
//                    GetFlag[wheel_N] = true;

                        checkdata(wheel_N);
                        handler5.postDelayed(RxreflashTimer, RxreflashTimer_k);
                    }
                }
            }else{
                return;
            }
//Old protocol
///////////////////////////////////////////////


        }
    } //更新"DATA"



    private boolean Byte2toFlag(int Byte1,int Byte0,int Bit){
        if(Byte1>=65){
            Byte1 = Byte1-55;
        }else{
            Byte1 = Byte1-48;
        }
        if(Byte0>=65){
            Byte0 = Byte0-55;
        }else{
            Byte0 = Byte0-48;
        }

        Byte0 = (Byte1*16)+Byte0;

        for (int i=0; i<(Bit+1); i++){
            Byte1 = Byte0%2;
            Byte0 = Byte0/2;
        }
        if (Byte1 == 1) {
            return true;
        }else{
            return false;
        }
    }



    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {      //顯示所有服務&特徵
        if (gattServices == null) return;
        String uuid = null;
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {                    //service顯示
            List<BluetoothGattCharacteristic> gattCharacteristics =                //得到該服務內所有特徵
                    gattService.getCharacteristics();
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                uuid = gattCharacteristic.getUuid().toString();

                //////////////////////////////此段為可自行鎖定某特徵接收
                if ( SampleGattAttributes.lookup(uuid, unknownCharaString) == "TYREDOG") {
                    final BluetoothGattCharacteristic characteristic = gattCharacteristic;
                    final int charaProp = characteristic.getProperties();
                    if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                        Log.d(TAG, "PROPERTY_READ");
                        // If there is an active notification on a characteristic, clear
                        // it first so it doesn't update the data field on the user interface.
                        if (mNotifyCharacteristic != null) {                                  //若已點選過需清掉
                            mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, false);
                            mNotifyCharacteristic = null;
//                            Toast.makeText(blescreenMain.this, "Test8", Toast.LENGTH_SHORT).show();
                        }
                        mBluetoothLeService.readCharacteristic(characteristic);               // remote device 讀資料
//                        Toast.makeText(blescreenMain.this, "Test7", Toast.LENGTH_SHORT).show();
                    }
                    if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
                        Log.d(TAG, "PROPERTY_WRITE");
                        if (mWriteCharacteristic != null) {
                            mWriteCharacteristic = null;
//                            Toast.makeText(blescreenMain.this, "Test9", Toast.LENGTH_SHORT).show();
                        }
//                        mWriteCharacteristic = mBluetoothGatt.getService(UUID_TYREDOG_SERVICE).getCharacteristic(UUID_TYREDOG_TPMS);
                        mWriteCharacteristic = characteristic;
//                        Toast.makeText(blescreenMain.this, "Test10", Toast.LENGTH_SHORT).show();
                        if ((saveValueF[Fac] ==1) && (saveValueF[Check] !=1)){
                            if (BleFstCmd == true){
//                                Cmd_T1();
                                Cmd_TC241();
                            }
                        }else{
                            if (BleFstCmd == true){
//                                Cmd_T1();
                                Cmd_TC241();
                            }
                        }

//                        mBluetoothLeService.writeCharacteristic(characteristic);
                        //characteristic.setValue(bytes);
                        //characteristic.setValue("testing");
                        //characteristic.setWriteType(BluetoothGattCharacteristic.PERMISSION_WRITE);
//                        Toast.makeText(blescreenMain.this, "Test10", Toast.LENGTH_SHORT).show();
                    }
                    if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                        Log.d(TAG, "PROPERTY_NOTIFY");
                        mNotifyCharacteristic = characteristic;
                        mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, true);//開啟Notification
//                        Toast.makeText(blescreenMain.this, "Test6", Toast.LENGTH_SHORT).show();
                    }
                }
                //////////////////////////////此段為可自行鎖定某特徵接收
            }
        }
    }


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }           //註冊廣播用


    private Runnable RxreflashTimer = new Runnable() {
        public void run() {

            if (FLASH_WHEEL[0] == true){
                if (screenPL == true){
                    ReflashW0(CHANGE_WHEEL[0]);
                }else {
                    l_ReflashW0(CHANGE_WHEEL[0]);
                }
                FLASH_WHEEL[0] = false;
            }
            if (FLASH_WHEEL[1] == true){
                if (screenPL == true){
                    ReflashW1(CHANGE_WHEEL[1]);
                }else {
                    l_ReflashW1(CHANGE_WHEEL[1]);
                }
                FLASH_WHEEL[1] = false;
            }
            if (FLASH_WHEEL[2] == true){
                if (screenPL == true){
                    ReflashW2(CHANGE_WHEEL[2]);
                }else {
                    l_ReflashW2(CHANGE_WHEEL[2]);
                }
                FLASH_WHEEL[2] = false;
            }
            if (FLASH_WHEEL[3] == true){
                if (screenPL == true){
                    ReflashW3(CHANGE_WHEEL[3]);
                }else {
                    l_ReflashW3(CHANGE_WHEEL[3]);
                }
                FLASH_WHEEL[3] = false;
            }

            checkalarm();

            handler5.removeCallbacks(RxreflashTimer);

        }
    };



    private void checkdata(int i) {
        int Waddress = CHECK_WHEEL(i);
        if (RxFlag[i] == true && ((PressData[i] != PressDataBuf[i]) || (TempData[i] != TempDataBuf[i]) || (BtFlag[i] != BtFlagBuf[i]))){
//                Log.d(TAG, "R=1 Data Difference");
            //PRESSURE
            if (PressHFlag[i] == true){
                if (PressData[i] > PressHigh[i]){
                    PressHigh[i] = PressData[i];
                    PressRHigh[i] = PressData[i];
//                        alarmcnt = alarmcnt_k;
                    sound_cnt = alarmcnt_k;
                    EnNoti_alarm(Waddress,HIGHPRESS);
                    PressHRFlag[i] = false;
                }else {
                    if (PressData[i] < PressHigh[i] && PressData[i] >= saveValue[3]){
                        if (PressData[i] < PressRHigh[i]){
                            PressHRFlag[i] = true;
                        }else if (PressData[i] > PressRHigh[i]){
                            PressHRFlag[i] = false;
                        }else {
                            ;// (PressData[i] = PressRHigh[i])
                        }
                        PressRHigh[i] = PressData[i];
                    }else if (PressData[i] < (saveValue[3]-DeltaPress_k)){
                        PressHRFlag[i] = false;
                        PressHFlag[i] = false;
                    }else {
                        ;//( (saveValue[3]-DeltaPress_k <= PressData[i] < PressHigh[i])
                    }
                }
            }else if(PressLFlag[i] == true){
                if (PressData[i] < PressLow[i]){
                    PressLow[i] = PressData[i];
                    PressRLow[i] = PressData[i];
//                        alarmcnt = alarmcnt_k;
                    sound_cnt = alarmcnt_k;
                    EnNoti_alarm(Waddress,LOWPRESS);
                    PressLRFlag[i] = false;
                }else {
                    if (PressData[i] > PressLow[i] && PressData[i] < saveValue[4]){
                        if (PressData[i] > PressRLow[i]){
                            PressLRFlag[i] = true;
                        }else if (PressData[i] < PressRLow[i]){
                            PressLRFlag[i] = false;
                        }else {
                            ;// (PressData[i] = PressRLow[i])
                        }
                        PressRLow[i] = PressData[i];
                    }else if (PressData[i] > (saveValue[4]+DeltaPress_k)){
                        PressLRFlag[i] = false;
                        PressLFlag[i] = false;
                    }else {
                        ;//( ( PressLow[i] < PressData[i] < saveValue[4]+DeltaPress_k)
                    }
                }
            }else{
                if (PressData[i] >= saveValue[3]){
                    PressHFlag[i] = true;
                    PressHigh[i] = PressData[i];
//                        alarmcnt = alarmcnt_k;
                    sound_cnt = alarmcnt_k;
                    EnNoti_alarm(Waddress,HIGHPRESS);
                }else if (PressData[i] < saveValue[4]){
                    PressLFlag[i] = true;
                    PressLow[i] = PressData[i];
//                        alarmcnt = alarmcnt_k;
                    sound_cnt = alarmcnt_k;
                    EnNoti_alarm(Waddress,LOWPRESS);
                }else{
                    PressHFlag[i] = false;
                    PressLFlag[i] = false;
                }
            }
            //TEMPERTURE
            if (TempHFlag[i] == true){
                if (TempData[i] > TempHigh[i]){
                    TempHigh[i] = TempData[i];
                    TempRHigh[i] = TempData[i];
//                        alarmcnt = alarmcnt_k;
                    sound_cnt = alarmcnt_k;
                    EnNoti_alarm(Waddress,HIGHTEMP);
                    TempHRFlag[i] = false;
                }else {
                    if (TempData[i] < TempHigh[i] && TempData[i] >= saveValue[5]){
                        if (TempData[i] < TempRHigh[i]){
                            TempHRFlag[i] = true;
                        }else if (TempData[i] > TempRHigh[i]){
                            TempHRFlag[i] = false;
                        }else {
                            ;// (TempData[i] = TempRHigh[i])
                        }
                        TempRHigh[i] = TempData[i];
                    }else if (TempData[i] < (saveValue[5]-DeltaTemp_k)){
                        TempHRFlag[i] = false;
                        TempHFlag[i] = false;
                    }else {
                        ;//( (saveValue[5]-DeltaTemp_k <= TempData[i] < TempHigh[i])
                    }
                }
            }else{
                if (TempData[i] >= saveValue[5]){
                    TempHFlag[i] = true;
                    TempHigh[i] = TempData[i];
//                        alarmcnt = alarmcnt_k;
                    sound_cnt = alarmcnt_k;
                    EnNoti_alarm(Waddress,HIGHTEMP);
                }else{
                    TempHFlag[i] = false;
                }
            }
            //BT
            if (BtLowFlag[i] == true){
                if (BtFlag[i] == true){
                    BtLowFlag[i] = true;
                }else{
                    BtLowFlag[i] = false;
                }
            }else{
                if (BtFlag[i] == true){
                    BtLowFlag[i] = true;
                    sound_cnt = alarmcnt_k;
                    EnNoti_alarm(Waddress,LOWBT);
                }else{
                    BtLowFlag[i] = false;
                }
            }

        }else if (RxFlag[i] == false){
//                Log.d(TAG, "R=0");
            PressHFlag[i] = false;
            PressLFlag[i] = false;
            TempHFlag[i] = false;
        }else {
//                Log.d(TAG, "R=1 Data same");
            //(RxFlag[i] == true && !((PressData[i] != PressDataBuf[i]) || (TempData[i] != TempDataBuf[i])))
        }
        PressDataBuf[i] = PressData[i];
        TempDataBuf[i] = TempData[i];
        BtFlagBuf[i] = BtFlag[i];

        int changei = CHECK_WHEEL(i);
        FLASH_WHEEL[changei] = true;
    }


    private int CHECK_WHEEL(int no){
        int wheel = 0;
//        loadwheel();
        for (int i=0; i<wheelN_k; i++){
            if (no == CHANGE_WHEEL[i]){
                wheel = i;
            }
        }
//        Log.d(TAG,String.valueOf(no)+String.valueOf(wheel));
        return wheel;
    }
    private void savewheel(){
        for (int i=0; i<wheelN_k; i++){
            saveValue[23+i] = CHANGE_WHEEL[i];
            SetPara(saveKey[23+i],saveValue[23+i]);
        }
    }
    private void loadwheel(){
        for (int i=0; i<wheelN_k; i++){
            if (saveValue[23+i] == 999){
                CHANGE_WHEEL[i] = i;
            }else{
                CHANGE_WHEEL[i] = saveValue[23+i];
            }
        }
    }


    private void display_PTvalue(int wheel_N){
        PressDataS[wheel_N] = PutPointN(PressData[wheel_N]*Punit_k[saveValue[1]],PressPoint[saveValue[1]]);
        if (saveValue[2] == 1) {
            TempDataS[wheel_N] = (int)PutPointN(((TempData[wheel_N]*Tunit_k0)+Tunit_k1),TempPoint[saveValue[2]]);
        }else{
            TempDataS[wheel_N] = (int)PutPointN(TempData[wheel_N],TempPoint[saveValue[2]]);
        }
//        etRead.setText(String.valueOf((TempData[wheel_N]))+"&"+String.valueOf((TempDataS[wheel_N])));
//        Log.d(TAG, String.valueOf(saveValue[1])+" "+String.valueOf(saveValue[2]));
    }




    private void checkalarm() {
        alarmflag = false;
        for (int i=0; i<wheelN_k; i++){
            if (PressHFlag[i]==true || PressLFlag[i]==true || TempHFlag[i]==true || BtLowFlag[i]==true){
                alarmflag = true;
            }
        }


        if (alarmflag2 == true){
            if (alarmflag == true){

            }else{
                soundDisable();
                alarmflag2 = false;
            }
        }else{
            if (alarmflag == true){
                soundEnable();
                alarmflag2 = true;
            }else{
                soundDisable();
            }
        }

//        if (alarmflag == true){
//            soundEnable();
//        }else {
//            soundDisable();
//        }


//        if ((w1pLow==false)&(w2pLow==false)&(w3pLow==false)&(w4pLow==false)&(w1pHigh==false)&(w2pHigh==false)&(w3pHigh==false)&(w4pHigh==false)&(w1tHigh==false)&(w2tHigh==false)&(w3tHigh==false)&(w4tHigh==false)){
//            alarmflag = false;
//        }else{
//            alarmflag = true;
//        }

//        if (alarmflag2 == false) {
//            if (alarmflag == true) {
//                alarmflag2 = true;
//                soundEnable();
//            } else{
//                soundDisable();
//            }
//        } else {
//            if (alarmflag == true) {
//                    ;//soundEnable();
//            } else{
//                alarmflag2 = false;
//                soundDisable();
//            }
//        }

    }

    private void Reflash(){
        ReflashW0(CHANGE_WHEEL[0]);
        ReflashW1(CHANGE_WHEEL[1]);
        ReflashW2(CHANGE_WHEEL[2]);
        ReflashW3(CHANGE_WHEEL[3]);
    }
    private void l_Reflash(){
        l_ReflashW0(CHANGE_WHEEL[0]);
        l_ReflashW1(CHANGE_WHEEL[1]);
        l_ReflashW2(CHANGE_WHEEL[2]);
        l_ReflashW3(CHANGE_WHEEL[3]);
    }




    private void ReflashW0(int i_wheel) {
        if (RxFlag[i_wheel]==true) {
//            miBtnNo1.setImageResource(R.drawable.p_no_1_1);
            miBtnNo1.setImageResource(wheelon[i_wheel]);
        }else {
            miBtnNo1.setImageResource(wheeloff[i_wheel]);
//            miBtnNo1.setImageResource(R.drawable.p_no_1_0);
        }
        if (BtFlag[i_wheel]==true) {
            miBtnBt1.setImageResource(R.drawable.p_sign_bt_1);
        }else {
            miBtnBt1.setImageResource(R.drawable.p_sign_bt_0);
        }
        if (PressLFlag[i_wheel]==true){
            mtViewVP1.setTextColor(this.getResources().getColor(R.color.red));
            miBtnPress1.setImageResource(R.drawable.p_sign_press_2);
        }else if (PressHFlag[i_wheel]==true){
            mtViewVP1.setTextColor(this.getResources().getColor(R.color.red));
            miBtnPress1.setImageResource(R.drawable.p_sign_press_2);
        }else {
            mtViewVP1.setTextColor(this.getResources().getColor(R.color.white));
            miBtnPress1.setImageResource(R.drawable.p_sign_press_1);
        }
        if (TempHFlag[i_wheel]==true){
            mtViewVT1.setTextColor(this.getResources().getColor(R.color.red));
            miBtnTemp1.setImageResource(R.drawable.p_sign_temp_2);
        }else {
            mtViewVT1.setTextColor(this.getResources().getColor(R.color.white));
            miBtnTemp1.setImageResource(R.drawable.p_sign_temp_1);
        }
//        etRead.setText(String.valueOf((TempData[i_wheel]))+"&"+String.valueOf((TempDataS[i_wheel])));
        display_PTvalue(i_wheel);
        mtViewVP1.setText(String.valueOf(PressDataS[i_wheel]));
        mtViewVT1.setText(String.valueOf(TempDataS[i_wheel]));
    }
    private void ReflashW1(int i_wheel) {
        if (RxFlag[i_wheel]==true) {
            miBtnNo2.setImageResource(wheelon[i_wheel]);
        }else {
            miBtnNo2.setImageResource(wheeloff[i_wheel]);
        }
        if (BtFlag[i_wheel]==true) {
            miBtnBt2.setImageResource(R.drawable.p_sign_bt_1);
        }else {
            miBtnBt2.setImageResource(R.drawable.p_sign_bt_0);
        }
        if (PressLFlag[i_wheel]==true){
            mtViewVP2.setTextColor(this.getResources().getColor(R.color.red));
            miBtnPress2.setImageResource(R.drawable.p_sign_press_2);
        }else if (PressHFlag[i_wheel]==true){
            mtViewVP2.setTextColor(this.getResources().getColor(R.color.red));
            miBtnPress2.setImageResource(R.drawable.p_sign_press_2);
        }else {
            mtViewVP2.setTextColor(this.getResources().getColor(R.color.white));
            miBtnPress2.setImageResource(R.drawable.p_sign_press_1);
        }
        if (TempHFlag[i_wheel]==true){
            mtViewVT2.setTextColor(this.getResources().getColor(R.color.red));
            miBtnTemp2.setImageResource(R.drawable.p_sign_temp_2);
        }else {
            mtViewVT2.setTextColor(this.getResources().getColor(R.color.white));
            miBtnTemp2.setImageResource(R.drawable.p_sign_temp_1);
        }
        display_PTvalue(i_wheel);
        mtViewVP2.setText(String.valueOf(PressDataS[i_wheel]));
        mtViewVT2.setText(String.valueOf(TempDataS[i_wheel]));
    }
    private void ReflashW2(int i_wheel) {
        if (RxFlag[i_wheel]==true) {
            miBtnNo3.setImageResource(wheelon[i_wheel]);
        }else {
            miBtnNo3.setImageResource(wheeloff[i_wheel]);
        }
        if (BtFlag[i_wheel]==true) {
            miBtnBt3.setImageResource(R.drawable.p_sign_bt_1);
        }else {
            miBtnBt3.setImageResource(R.drawable.p_sign_bt_0);
        }
        if (PressLFlag[i_wheel]==true){
            mtViewVP3.setTextColor(this.getResources().getColor(R.color.red));
            miBtnPress3.setImageResource(R.drawable.p_sign_press_2);
        }else if (PressHFlag[i_wheel]==true){
            mtViewVP3.setTextColor(this.getResources().getColor(R.color.red));
            miBtnPress3.setImageResource(R.drawable.p_sign_press_2);
        }else {
            mtViewVP3.setTextColor(this.getResources().getColor(R.color.white));
            miBtnPress3.setImageResource(R.drawable.p_sign_press_1);
        }
        if (TempHFlag[i_wheel]==true){
            mtViewVT3.setTextColor(this.getResources().getColor(R.color.red));
            miBtnTemp3.setImageResource(R.drawable.p_sign_temp_2);
        }else {
            mtViewVT3.setTextColor(this.getResources().getColor(R.color.white));
            miBtnTemp3.setImageResource(R.drawable.p_sign_temp_1);
        }
        display_PTvalue(i_wheel);
        mtViewVP3.setText(String.valueOf(PressDataS[i_wheel]));
        mtViewVT3.setText(String.valueOf(TempDataS[i_wheel]));
    }
    private void ReflashW3(int i_wheel) {
        if (RxFlag[i_wheel]==true) {
            miBtnNo4.setImageResource(wheelon[i_wheel]);
        }else {
            miBtnNo4.setImageResource(wheeloff[i_wheel]);
        }
        if (BtFlag[i_wheel]==true) {
            miBtnBt4.setImageResource(R.drawable.p_sign_bt_1);
        }else {
            miBtnBt4.setImageResource(R.drawable.p_sign_bt_0);
        }
        if (PressLFlag[i_wheel]==true){
            mtViewVP4.setTextColor(this.getResources().getColor(R.color.red));
            miBtnPress4.setImageResource(R.drawable.p_sign_press_2);
        }else if (PressHFlag[i_wheel]==true){
            mtViewVP4.setTextColor(this.getResources().getColor(R.color.red));
            miBtnPress4.setImageResource(R.drawable.p_sign_press_2);
        }else {
            mtViewVP4.setTextColor(this.getResources().getColor(R.color.white));
            miBtnPress4.setImageResource(R.drawable.p_sign_press_1);
        }
        if (TempHFlag[i_wheel]==true){
            mtViewVT4.setTextColor(this.getResources().getColor(R.color.red));
            miBtnTemp4.setImageResource(R.drawable.p_sign_temp_2);
        }else {
            mtViewVT4.setTextColor(this.getResources().getColor(R.color.white));
            miBtnTemp4.setImageResource(R.drawable.p_sign_temp_1);
        }
        display_PTvalue(i_wheel);
        mtViewVP4.setText(String.valueOf(PressDataS[i_wheel]));
        mtViewVT4.setText(String.valueOf(TempDataS[i_wheel]));
    }


    private void l_ReflashW0(int i_wheel) {
        if (RxFlag[i_wheel]==true) {
            miBtnNo1.setImageResource(wheelon[i_wheel]);
        }else {
            miBtnNo1.setImageResource(wheeloff[i_wheel]);
        }
        if (BtFlag[i_wheel]==true) {
            miBtnBt1.setImageResource(R.drawable.l_sign_bt_1);
        }else {
            miBtnBt1.setImageResource(R.drawable.l_sign_bt_0);
        }
        if (PressLFlag[i_wheel]==true){
            mtViewVP1.setTextColor(this.getResources().getColor(R.color.red));
            miBtnPress1.setImageResource(R.drawable.l_sign_press_2);
        }else if (PressHFlag[i_wheel]==true){
            mtViewVP1.setTextColor(this.getResources().getColor(R.color.red));
            miBtnPress1.setImageResource(R.drawable.l_sign_press_2);
        }else {
            mtViewVP1.setTextColor(this.getResources().getColor(R.color.white));
            miBtnPress1.setImageResource(R.drawable.l_sign_press_1);
        }
        if (TempHFlag[i_wheel]==true){
            mtViewVT1.setTextColor(this.getResources().getColor(R.color.red));
            miBtnTemp1.setImageResource(R.drawable.l_sign_temp_2);
        }else {
            mtViewVT1.setTextColor(this.getResources().getColor(R.color.white));
            miBtnTemp1.setImageResource(R.drawable.l_sign_temp_1);
        }
        display_PTvalue(i_wheel);
        mtViewVP1.setText(String.valueOf(PressDataS[i_wheel]));
        mtViewVT1.setText(String.valueOf(TempDataS[i_wheel]));
    }
    private void l_ReflashW1(int i_wheel) {
        if (RxFlag[i_wheel]==true) {
            miBtnNo2.setImageResource(wheelon[i_wheel]);
        }else {
            miBtnNo2.setImageResource(wheeloff[i_wheel]);
        }
        if (BtFlag[i_wheel]==true) {
            miBtnBt2.setImageResource(R.drawable.l_sign_bt_1);
        }else {
            miBtnBt2.setImageResource(R.drawable.l_sign_bt_0);
        }
        if (PressLFlag[i_wheel]==true){
            mtViewVP2.setTextColor(this.getResources().getColor(R.color.red));
            miBtnPress2.setImageResource(R.drawable.l_sign_press_2);
        }else if (PressHFlag[i_wheel]==true){
            mtViewVP2.setTextColor(this.getResources().getColor(R.color.red));
            miBtnPress2.setImageResource(R.drawable.l_sign_press_2);
        }else {
            mtViewVP2.setTextColor(this.getResources().getColor(R.color.white));
            miBtnPress2.setImageResource(R.drawable.l_sign_press_1);
        }
        if (TempHFlag[i_wheel]==true){
            mtViewVT2.setTextColor(this.getResources().getColor(R.color.red));
            miBtnTemp2.setImageResource(R.drawable.l_sign_temp_2);
        }else {
            mtViewVT2.setTextColor(this.getResources().getColor(R.color.white));
            miBtnTemp2.setImageResource(R.drawable.l_sign_temp_1);
        }
        display_PTvalue(i_wheel);
        mtViewVP2.setText(String.valueOf(PressDataS[i_wheel]));
        mtViewVT2.setText(String.valueOf(TempDataS[i_wheel]));
    }
    private void l_ReflashW2(int i_wheel) {
        if (RxFlag[i_wheel]==true) {
            miBtnNo3.setImageResource(wheelon[i_wheel]);
        }else {
            miBtnNo3.setImageResource(wheeloff[i_wheel]);
        }
        if (BtFlag[i_wheel]==true) {
            miBtnBt3.setImageResource(R.drawable.l_sign_bt_1);
        }else {
            miBtnBt3.setImageResource(R.drawable.l_sign_bt_0);
        }
        if (PressLFlag[i_wheel]==true){
            mtViewVP3.setTextColor(this.getResources().getColor(R.color.red));
            miBtnPress3.setImageResource(R.drawable.l_sign_press_2);
        }else if (PressHFlag[i_wheel]==true){
            mtViewVP3.setTextColor(this.getResources().getColor(R.color.red));
            miBtnPress3.setImageResource(R.drawable.l_sign_press_2);
        }else {
            mtViewVP3.setTextColor(this.getResources().getColor(R.color.white));
            miBtnPress3.setImageResource(R.drawable.l_sign_press_1);
        }
        if (TempHFlag[i_wheel]==true){
            mtViewVT3.setTextColor(this.getResources().getColor(R.color.red));
            miBtnTemp3.setImageResource(R.drawable.l_sign_temp_2);
        }else {
            mtViewVT3.setTextColor(this.getResources().getColor(R.color.white));
            miBtnTemp3.setImageResource(R.drawable.l_sign_temp_1);
        }
        display_PTvalue(i_wheel);
        mtViewVP3.setText(String.valueOf(PressDataS[i_wheel]));
        mtViewVT3.setText(String.valueOf(TempDataS[i_wheel]));
    }
    private void l_ReflashW3(int i_wheel) {
        if (RxFlag[i_wheel]==true) {
            miBtnNo4.setImageResource(wheelon[i_wheel]);
        }else {
            miBtnNo4.setImageResource(wheeloff[i_wheel]);
        }
        if (BtFlag[i_wheel]==true) {
            miBtnBt4.setImageResource(R.drawable.l_sign_bt_1);
        }else {
            miBtnBt4.setImageResource(R.drawable.l_sign_bt_0);
        }
        if (PressLFlag[i_wheel]==true){
            mtViewVP4.setTextColor(this.getResources().getColor(R.color.red));
            miBtnPress4.setImageResource(R.drawable.l_sign_press_2);
        }else if (PressHFlag[i_wheel]==true){
            mtViewVP4.setTextColor(this.getResources().getColor(R.color.red));
            miBtnPress4.setImageResource(R.drawable.l_sign_press_2);
        }else {
            mtViewVP4.setTextColor(this.getResources().getColor(R.color.white));
            miBtnPress4.setImageResource(R.drawable.l_sign_press_1);
        }
        if (TempHFlag[i_wheel]==true){
            mtViewVT4.setTextColor(this.getResources().getColor(R.color.red));
            miBtnTemp4.setImageResource(R.drawable.l_sign_temp_2);
        }else {
            mtViewVT4.setTextColor(this.getResources().getColor(R.color.white));
            miBtnTemp4.setImageResource(R.drawable.l_sign_temp_1);
        }
        display_PTvalue(i_wheel);
        mtViewVP4.setText(String.valueOf(PressDataS[i_wheel]));
        mtViewVT4.setText(String.valueOf(TempDataS[i_wheel]));
    }




    private void displayerant(int icnt) {
        if (icnt == 0) {
            miBtnAnt.setImageResource(R.drawable.p_sign_rx_0);
        }
        else if (icnt == 1) {
            miBtnAnt.setImageResource(R.drawable.p_sign_rx_1);
        }
        else if (icnt == 2) {
            miBtnAnt.setImageResource(R.drawable.p_sign_rx_2);
        }
        else {
            miBtnAnt.setImageResource(R.drawable.p_sign_rx_3);
        }
    }

    private void getwindowPL() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeigth = dm.heightPixels;


        if (saveValueF[Fac] == 1){
            screenPL = true;               //portrait
            setContentView(R.layout.port_activiyt_blescreenmain);
            return;
        }

        if (screenWidth >= screenHeigth){
            screenPL = false;              //landscape
            setContentView(R.layout.land_activiyt_blescreenmain);
        } else {
            screenPL = true;               //portrait
            setContentView(R.layout.port_activiyt_blescreenmain);
        }
    }

    private void getwindowsize() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int screenWidth = dm.widthPixels;
        int screenHeigth = dm.heightPixels;
        float screenDensity = dm.density; //
        int screenDpi = dm.densityDpi;//

        float horiDpi = dm.xdpi;
        float vertDpi = dm.ydpi;

        screenWsize = screenWidth;
        screenHsize = screenHeigth;

        if (screenWidth >= screenHeigth){
            screenPL = false;              //landscape
        } else {
            screenPL = true;               //portrait
        }
        if (saveValueF[Fac] == 1){
            screenPL = true;               //portrait
        }


        init_findview(screenPL);

//        mTextView1.setText("手機銀幕大小為 " + screenWidth + " X " + screenHeigth + " Density" + screenDensity + " horiDpi" + horiDpi + " vertDpi" + vertDpi);



        if (screenPL == true) {
//Main
            int MainSizeW = (int)(screenWidth*(MainSizeW_k));
            myimageviewsize(miBtnMain,(MainSizeW),0);
            int MainTop = (int)(screenHeigth*(MainTop_k));
            imageviewaddress(miBtnMain,0, MainTop, 0, 0);
//Logo
            int LogoSizeH = (int)(screenHeigth*(LogoSizeH_k));
            myimageviewsize(miBtnLogo,0,(LogoSizeH));
            int LogoTop = (int)(screenHeigth*(LogoTop_k));
            imageviewaddress(miBtnLogo,0, LogoTop, 0, 0);
//Mode
            int ModeSizeW = (int)(screenWidth*(ModeSizeW_k));
            myimageviewsize(miBtnMode1,(ModeSizeW),0);
            myimageviewsize(miBtnMode2,(ModeSizeW),0);
            myimageviewsize(miBtnMode3,(ModeSizeW),0);
            myimageviewsize(miBtnMode4,(ModeSizeW),0);
            int ModeBottom = (int)(screenWidth*(ModeBottom_k));
            imageviewaddress(miBtnMode1,0, 0, 0, ModeBottom);
            imageviewaddress(miBtnMode2,0, 0, 0, ModeBottom);
            imageviewaddress(miBtnMode3,0, 0, 0, ModeBottom);
            imageviewaddress(miBtnMode4,0, 0, 0, ModeBottom);
//Value
            int ValueSizeW = (int)(screenWidth*(ValueSizeW_k));
            int ValueSizeH = (int)(screenHeigth*(ValueSizeH_k));
            relayoutsize(mrelayValue1,ValueSizeW,ValueSizeH);
            relayoutsize(mrelayValue2,ValueSizeW,ValueSizeH);
            relayoutsize(mrelayValue3,ValueSizeW,ValueSizeH);
            relayoutsize(mrelayValue4,ValueSizeW,ValueSizeH);
            int ValueRight = (int)(screenWidth*(ValueRight_k));
            int ValueLeft = (int)(screenWidth*(ValueLeft_k));
            int ValueTop1 = (int)(screenHeigth*(ValueTop1_k));
            int ValueTop2 = (int)(screenHeigth*(ValueTop2_k));
            relayoutaddress(mrelayValue1,0,ValueTop1,ValueRight,0);
            relayoutaddress(mrelayValue3,0,ValueTop2,ValueRight,0);
            relayoutaddress(mrelayValue2,ValueLeft,ValueTop1,0,0);
            relayoutaddress(mrelayValue4,ValueLeft,ValueTop2,0,0);

//Ant
            int AntSizeW = (int)(screenWidth*(AntSizeW_k));
            myimageviewsize(miBtnAnt,(AntSizeW),0);
            int AntTop = (int)(screenHeigth*(AntTop_k));
            int AntLeft = (int)(screenWidth*(AntLeft_k));
            imageviewaddress(miBtnAnt,AntLeft, AntTop, 0, 0);
//Tunit
            int TunitSizeH = (int)(screenHeigth*(TunitSizeH_k));
            myimageviewsize(miBtnTunit,0,(TunitSizeH));
            int TunitTop = (int)(screenHeigth*(TunitTop_k));
            int TunitRight = (int)(screenWidth*(TunitRight_k));
            imageviewaddress(miBtnTunit,0, TunitTop, TunitRight, 0);
//Punit
            int PunitSizeW = (int)(screenWidth*(PunitSizeW_k));
            int PunitSizeH = (int)(screenHeigth*(PunitSizeH_k));
            myimageviewsize(miBtnPunit,(PunitSizeW),(PunitSizeH));
            int PunitTop = (int)(screenHeigth*(PunitTop_k));
            int PunitRight = (int)(screenWidth*(PunitRight_k));
            imageviewaddress(miBtnPunit,0, PunitTop, PunitRight, 0);
//Press
            int PressSizeW = (int)(screenWidth*(PressSizeW_k));
            int PressSizeH = (int)(screenHeigth*(PressSizeH_k));
            myimageviewsize(miBtnPress1,(PressSizeW),(PressSizeH));
            myimageviewsize(miBtnPress2,(PressSizeW),(PressSizeH));
            myimageviewsize(miBtnPress3,(PressSizeW),(PressSizeH));
            myimageviewsize(miBtnPress4,(PressSizeW),(PressSizeH));
            int PressTop = (int)(screenHeigth*(PressTop_k));
            int PressLeft = (int)(screenWidth*(PressLeft_k));
            imageviewaddress(miBtnPress1,PressLeft, PressTop, 0, 0);
            imageviewaddress(miBtnPress2,PressLeft, PressTop, 0, 0);
            imageviewaddress(miBtnPress3,PressLeft, PressTop, 0, 0);
            imageviewaddress(miBtnPress4,PressLeft, PressTop, 0, 0);
//Temp
            int TempSizeW = (int)(screenWidth*(TempSizeW_k));
            int TempSizeH = (int)(screenHeigth*(TempSizeH_k));
            myimageviewsize(miBtnTemp1,(TempSizeW),(TempSizeH));
            myimageviewsize(miBtnTemp2,(TempSizeW),(TempSizeH));
            myimageviewsize(miBtnTemp3,(TempSizeW),(TempSizeH));
            myimageviewsize(miBtnTemp4,(TempSizeW),(TempSizeH));
            int TempBottom = (int)(screenHeigth*(TempBottom_k));
            int TempLeft = (int)(screenWidth*(TempLeft_k));
            imageviewaddress(miBtnTemp1,TempLeft, 0, 0, TempBottom);
            imageviewaddress(miBtnTemp2,TempLeft, 0, 0, TempBottom);
            imageviewaddress(miBtnTemp3,TempLeft, 0, 0, TempBottom);
            imageviewaddress(miBtnTemp4,TempLeft, 0, 0, TempBottom);
//No
            int NoSizeW = (int)(screenWidth*(NoSizeW_k));
            int NoSizeH = (int)(screenHeigth*(NoSizeH_k));
            myimageviewsize(miBtnNo1,(NoSizeW),(NoSizeH));
            myimageviewsize(miBtnNo2,(NoSizeW),(NoSizeH));
            myimageviewsize(miBtnNo3,(NoSizeW),(NoSizeH));
            myimageviewsize(miBtnNo4,(NoSizeW),(NoSizeH));
            int NoTop = (int)(screenHeigth*(NoTop_k));
            int NoLeft = (int)(screenWidth*(NoLeft_k));
            imageviewaddress(miBtnNo1,NoLeft, NoTop, 0, 0);
            imageviewaddress(miBtnNo2,NoLeft, NoTop, 0, 0);
            imageviewaddress(miBtnNo3,NoLeft, NoTop, 0, 0);
            imageviewaddress(miBtnNo4,NoLeft, NoTop, 0, 0);
//Bt
            int BtSizeW = (int)(screenWidth*(BtSizeW_k));
            int BtSizeH = (int)(screenHeigth*(BtSizeH_k));
            myimageviewsize(miBtnBt1,(BtSizeW),(BtSizeH));
            myimageviewsize(miBtnBt2,(BtSizeW),(BtSizeH));
            myimageviewsize(miBtnBt3,(BtSizeW),(BtSizeH));
            myimageviewsize(miBtnBt4,(BtSizeW),(BtSizeH));
            int BtTop = (int)(screenHeigth*(BtTop_k));
            int BtRight = (int)(screenWidth*(BtRight_k));
            imageviewaddress(miBtnBt1,0, BtTop, BtRight, 0);
            imageviewaddress(miBtnBt1,0, BtTop, BtRight, 0);
            imageviewaddress(miBtnBt1,0, BtTop, BtRight, 0);
            imageviewaddress(miBtnBt1,0, BtTop, BtRight, 0);
//ValueP
            int VPSizeW = (int)(screenWidth*(VPSizeW_k));
            int VPSizeH = (int)(screenHeigth*(VPSizeH_k));
            textviewsize(mtViewVP1,(VPSizeW),(VPSizeH));
            textviewsize(mtViewVP2,(VPSizeW),(VPSizeH));
            textviewsize(mtViewVP3,(VPSizeW),(VPSizeH));
            textviewsize(mtViewVP4,(VPSizeW),(VPSizeH));
            int VPTop = (int)(screenHeigth*(VPTop_k));
            int VPRight = (int)(screenWidth*(VPRight_k));
            textviewaddress(mtViewVP1,0, VPTop, VPRight, 0);
            textviewaddress(mtViewVP2,0, VPTop, VPRight, 0);
            textviewaddress(mtViewVP3,0, VPTop, VPRight, 0);
            textviewaddress(mtViewVP4,0, VPTop, VPRight, 0);
//ValueT
            int VTSizeW = (int)(screenWidth*(VTSizeW_k));
            int VTSizeH = (int)(screenHeigth*(VTSizeH_k));
            textviewsize(mtViewVT1,(VTSizeW),(VTSizeH));
            textviewsize(mtViewVT2,(VTSizeW),(VTSizeH));
            textviewsize(mtViewVT3,(VTSizeW),(VTSizeH));
            textviewsize(mtViewVT4,(VTSizeW),(VTSizeH));
            int VTBottom = (int)(screenHeigth*(VTBottom_k));
            int VTRight = (int)(screenWidth*(VTRight_k));
            textviewaddress(mtViewVT1,0, 0, VTRight, VTBottom);
            textviewaddress(mtViewVT2,0, 0, VTRight, VTBottom);
            textviewaddress(mtViewVT3,0, 0, VTRight, VTBottom);
            textviewaddress(mtViewVT4,0, 0, VTRight, VTBottom);

        } else {

//Logo
            int LogoSizeW = (int)(screenWidth*(l_LogoSizeW_k));
            int LogoSizeH = (int)(screenWidth*(l_LogoSizeH_k));
            myimageviewsize(miBtnLogo,LogoSizeW,LogoSizeH);
            int LogoTop = (int)(screenHeigth*(l_LogoTop_k));
            imageviewaddress(miBtnLogo,0, LogoTop, 0, 0);



//Ant
            int AntSizeH = (int)(screenWidth*(l_AntSizeH_k));
            myimageviewsize(miBtnAnt,0,AntSizeH);
            int AntTop = (int)(screenHeigth*(l_AntTop_k));
            int AntLeft = (int)(screenWidth*(l_AntLeft_k));
            imageviewaddress(miBtnAnt,AntLeft, AntTop, 0, 0);
//Tunit
            int TunitSizeW = (int)(screenHeigth*(l_TunitSizeW_k));
            int TunitSizeH = (int)(screenHeigth*(l_TunitSizeH_k));
            myimageviewsize(miBtnTunit,(TunitSizeW),(TunitSizeH));
            int TunitTop = (int)(screenHeigth*(l_TunitTop_k));
            int TunitLeft = (int)(screenWidth*(l_TunitLeft_k));
            imageviewaddress(miBtnTunit,TunitLeft, TunitTop, 0, 0);
//Punit
            int PunitSizeW = (int)(screenWidth*(l_PunitSizeW_k));
            int PunitSizeH = (int)(screenHeigth*(l_PunitSizeH_k));
            myimageviewsize(miBtnPunit,(PunitSizeW),(PunitSizeH));
            int PunitTop = (int)(screenHeigth*(l_PunitTop_k));
            int PunitLeft = (int)(screenWidth*(l_PunitLeft_k));
            imageviewaddress(miBtnPunit,PunitLeft, PunitTop, 0, 0);

//Set
            int SetSizeH = (int)(screenWidth*(l_SetSizeH_k));
            myimageviewsize(miBtnSet,0,AntSizeH);
            int SetTop = (int)(screenHeigth*(l_SetTop_k));
            int SetRight = (int)(screenWidth*(l_SetRight_k));
            imageviewaddress(miBtnSet,0, SetTop, SetRight, 0);

//Voice
            int VoiceSizeH = (int)(screenWidth*(l_VoiceSizeH_k));
            myimageviewsize(miBtnVoice,0,AntSizeH);
            int VoicetTop = (int)(screenHeigth*(l_VoiceTop_k));
            int VoiceRight = (int)(screenWidth*(l_VoiceRight_k));
            imageviewaddress(miBtnVoice,0, VoicetTop, VoiceRight, 0);

//Value
            int ValueSizeW = (int)(screenWidth*(l_ValueSizeW_k));
            int ValueSizeH = (int)(screenHeigth*(l_ValueSizeH_k));
            relayoutsize(mrelayValue1,ValueSizeW,ValueSizeH);
            relayoutsize(mrelayValue2,ValueSizeW,ValueSizeH);
            relayoutsize(mrelayValue3,ValueSizeW,ValueSizeH);
            relayoutsize(mrelayValue4,ValueSizeW,ValueSizeH);
            int ValueRight = (int)(screenWidth*(l_ValueRight_k));
            int ValueLeft = (int)(screenWidth*(l_ValueLeft_k));
            int ValueTop1 = (int)(screenHeigth*(l_ValueTop1_k));
            int ValueTop2 = (int)(screenHeigth*(l_ValueTop2_k));
            relayoutaddress(mrelayValue1,ValueLeft,ValueTop1,0,0);
            relayoutaddress(mrelayValue3,ValueLeft,ValueTop2,0,0);
            relayoutaddress(mrelayValue2,0,ValueTop1,ValueRight,0);
            relayoutaddress(mrelayValue4,0,ValueTop2,ValueRight,0);

//No
            int NoSizeW = (int)(screenWidth*(l_NoSizeW_k));
            int NoSizeH = (int)(screenHeigth*(l_NoSizeH_k));
            myimageviewsize(miBtnNo1,(NoSizeW),(NoSizeH));
            myimageviewsize(miBtnNo2,(NoSizeW),(NoSizeH));
            myimageviewsize(miBtnNo3,(NoSizeW),(NoSizeH));
            myimageviewsize(miBtnNo4,(NoSizeW),(NoSizeH));
            int NoTop = (int)(screenHeigth*(l_NoTop_k));
            int NoRight = (int)(screenWidth*(l_NoRight_k));
            imageviewaddress(miBtnNo1, 0, NoTop, NoRight, 0);
            imageviewaddress(miBtnNo2, 0, NoTop, NoRight, 0);
            imageviewaddress(miBtnNo3, 0, NoTop, NoRight, 0);
            imageviewaddress(miBtnNo4, 0, NoTop, NoRight, 0);
//Bt
            int BtSizeW = (int)(screenWidth*(l_BtSizeW_k));
            int BtSizeH = (int)(screenHeigth*(l_BtSizeH_k));
            myimageviewsize(miBtnBt1,(BtSizeW),(BtSizeH));
            myimageviewsize(miBtnBt2,(BtSizeW),(BtSizeH));
            myimageviewsize(miBtnBt3,(BtSizeW),(BtSizeH));
            myimageviewsize(miBtnBt4,(BtSizeW),(BtSizeH));
            int BtBottom = (int)(screenHeigth*(l_BtBottom_k));
            int BtRight = (int)(screenWidth*(l_BtRight_k));
            imageviewaddress(miBtnBt1,0, 0, BtRight, BtBottom);
            imageviewaddress(miBtnBt2,0, 0, BtRight, BtBottom);
            imageviewaddress(miBtnBt3,0, 0, BtRight, BtBottom);
            imageviewaddress(miBtnBt4,0, 0, BtRight, BtBottom);
//Press
            int PressSizeW = (int)(screenWidth*(l_PressSizeW_k));
            int PressSizeH = (int)(screenHeigth*(l_PressSizeH_k));
            myimageviewsize(miBtnPress1,(PressSizeW),(PressSizeH));
            myimageviewsize(miBtnPress2,(PressSizeW),(PressSizeH));
            myimageviewsize(miBtnPress3,(PressSizeW),(PressSizeH));
            myimageviewsize(miBtnPress4,(PressSizeW),(PressSizeH));
            int PressTop = (int)(screenHeigth*(l_PressTop_k));
            int PressLeft = (int)(screenWidth*(l_PressLeft_k));
            imageviewaddress(miBtnPress1,PressLeft, PressTop, 0, 0);
            imageviewaddress(miBtnPress2,PressLeft, PressTop, 0, 0);
            imageviewaddress(miBtnPress3,PressLeft, PressTop, 0, 0);
            imageviewaddress(miBtnPress4,PressLeft, PressTop, 0, 0);
//Temp
            int TempSizeW = (int)(screenWidth*(l_TempSizeW_k));
            int TempSizeH = (int)(screenHeigth*(l_TempSizeH_k));
            myimageviewsize(miBtnTemp1,(TempSizeW),(TempSizeH));
            myimageviewsize(miBtnTemp2,(TempSizeW),(TempSizeH));
            myimageviewsize(miBtnTemp3,(TempSizeW),(TempSizeH));
            myimageviewsize(miBtnTemp4,(TempSizeW),(TempSizeH));
            int TempBottom = (int)(screenHeigth*(l_TempBottom_k));
            int TempLeft = (int)(screenWidth*(l_TempLeft_k));
            imageviewaddress(miBtnTemp1,TempLeft, 0, 0, TempBottom);
            imageviewaddress(miBtnTemp2,TempLeft, 0, 0, TempBottom);
            imageviewaddress(miBtnTemp3,TempLeft, 0, 0, TempBottom);
            imageviewaddress(miBtnTemp4,TempLeft, 0, 0, TempBottom);
//ValueP
            int VPSizeW = (int)(screenWidth*(l_VPSizeW_k));
            int VPSizeH = (int)(screenHeigth*(l_VPSizeH_k));
            textviewsize(mtViewVP1,(VPSizeW),(VPSizeH));
            textviewsize(mtViewVP2,(VPSizeW),(VPSizeH));
            textviewsize(mtViewVP3,(VPSizeW),(VPSizeH));
            textviewsize(mtViewVP4,(VPSizeW),(VPSizeH));
            int VPTop = (int)(screenHeigth*(l_VPTop_k));
            textviewaddress(mtViewVP1,0, VPTop, 0, 0);
            textviewaddress(mtViewVP2,0, VPTop, 0, 0);
            textviewaddress(mtViewVP3,0, VPTop, 0, 0);
            textviewaddress(mtViewVP4,0, VPTop, 0, 0);
//ValueT
            int VTSizeW = (int)(screenWidth*(l_VTSizeW_k));
            int VTSizeH = (int)(screenHeigth*(l_VTSizeH_k));
            textviewsize(mtViewVT1,(VTSizeW),(VTSizeH));
            textviewsize(mtViewVT2,(VTSizeW),(VTSizeH));
            textviewsize(mtViewVT3,(VTSizeW),(VTSizeH));
            textviewsize(mtViewVT4,(VTSizeW),(VTSizeH));
            int VTBottom = (int)(screenHeigth*(l_VTBottom_k));
            textviewaddress(mtViewVT1,0, 0, 0, VTBottom);
            textviewaddress(mtViewVT2,0, 0, 0, VTBottom);
            textviewaddress(mtViewVT3,0, 0, 0, VTBottom);
            textviewaddress(mtViewVT4,0, 0, 0, VTBottom);
        }
        init_textsize(screenPL,screenDpi);

    }//getwindowsize


    private void init_textsize(boolean screenPL,int Dpi){
        if (screenPL == true) {
            if (Dpi >= 480){
                textsizeP_k = textsizeP_k0;
                textsizeT_k = textsizeT_k0;
                textsizeN_k = textsizeN_k0;
            }else if(Dpi >= 320){
                textsizeP_k = textsizeP_k1;
                textsizeT_k = textsizeT_k1;
                textsizeN_k = textsizeN_k1;
                miBtnLogo.setVisibility(View.INVISIBLE);
            }else if(Dpi >= 240){
                textsizeP_k = textsizeP_k2;
                textsizeT_k = textsizeT_k2;
                textsizeN_k = textsizeN_k2;
                miBtnLogo.setVisibility(View.INVISIBLE);
            }else if(Dpi >= 160){
                textsizeP_k = textsizeP_k3;
                textsizeT_k = textsizeT_k3;
                textsizeN_k = textsizeN_k3;
                miBtnLogo.setVisibility(View.INVISIBLE);
            }else{
                textsizeP_k = textsizeP_k4;
                textsizeT_k = textsizeT_k4;
                textsizeN_k = textsizeN_k4;
                miBtnLogo.setVisibility(View.INVISIBLE);
            }
        }else{
            if (Dpi >= 480){
                textsizeP_k = textsizeP_k5;
                textsizeT_k = textsizeT_k5;
                textsizeN_k = textsizeN_k5;
            }else if(Dpi >= 320){
                textsizeP_k = textsizeP_k6;
                textsizeT_k = textsizeT_k6;
                textsizeN_k = textsizeN_k6;
            }else if(Dpi >= 240){
                textsizeP_k = textsizeP_k7;
                textsizeT_k = textsizeT_k7;
                textsizeN_k = textsizeN_k7;
            }else if(Dpi >= 160){
                textsizeP_k = textsizeP_k8;
                textsizeT_k = textsizeT_k8;
                textsizeN_k = textsizeN_k8;
            }else{
                textsizeP_k = textsizeP_k9;
                textsizeT_k = textsizeT_k9;
                textsizeN_k = textsizeN_k9;
            }
        }
        mtViewVP1.setTextSize(textsizeP_k);
        mtViewVP2.setTextSize(textsizeP_k);
        mtViewVP3.setTextSize(textsizeP_k);
        mtViewVP4.setTextSize(textsizeP_k);
        mtViewVT1.setTextSize(textsizeT_k);
        mtViewVT2.setTextSize(textsizeT_k);
        mtViewVT3.setTextSize(textsizeT_k);
        mtViewVT4.setTextSize(textsizeT_k);
        mBtnDemo.setTextSize(textsizeN_k);
    }

    public void init_findview(boolean screenPL) {
        if (screenPL == true) {
            mBtnLearn = (Button) findViewById(R.id.BtnLearn);
            mBtnLearn.setOnClickListener(mBtnLearnOnclick);
            mBtnLearx = (Button) findViewById(R.id.BtnLearx);
            mBtnLearx.setOnClickListener(mBtnLearxOnclick);
            mBtnLend = (Button) findViewById(R.id.BtnLend);
            mBtnLend.setOnClickListener(mBtnLendOnclick);
            mBtnFac = (Button) findViewById(R.id.BtnFac);
            mBtnFac.setOnClickListener(mBtnFacOnclick);
            mBtnCal = (Button) findViewById(R.id.BtnCal);
            mBtnCal.setOnClickListener(mBtnCalOnclick);
            mBtnTest = (Button) findViewById(R.id.BtnTest);
            mBtnTest.setOnClickListener(mBtnTestOnclick);
            mBtnVer = (Button) findViewById(R.id.BtnVer);
            mBtnVer.setOnClickListener(mBtnVerOnclick);
            mBtnGetid = (Button) findViewById(R.id.BtnGetid);
            mBtnGetid.setOnClickListener(mBtnGetidOnclick);
            mBtnTimeron = (Button) findViewById(R.id.BtnTimeron);
            mBtnTimeron.setOnClickListener(mBtnTimeronOnclick);
            mBtnTimeroff = (Button) findViewById(R.id.BtnTimeroff);
            mBtnTimeroff.setOnClickListener(mBtnTimeroffOnclick);
            mBtnTimer = (Button) findViewById(R.id.BtnTimer);
            mBtnTimer.setOnClickListener(mBtnTimerOnclick);
            miSpinGetid = (Spinner) findViewById(R.id.iSpinGetid);
            miSpinGetid.setOnItemSelectedListener(spinGetidSelected);
            miSpinTimer = (Spinner) findViewById(R.id.iSpinTimer);
            miSpinTimer.setOnItemSelectedListener(spinTimerSelected);



            miBtnMode1 = (ImageButton)findViewById(R.id.iBtnMode1);
            miBtnMode2 = (ImageButton)findViewById(R.id.iBtnMode2);
            miBtnMode3 = (ImageButton)findViewById(R.id.iBtnMode3);
            miBtnMode4 = (ImageButton)findViewById(R.id.iBtnMode4);
            miBtnMode1.setOnClickListener(iBtnMode1Onclick);
            miBtnMode2.setOnClickListener(iBtnMode2Onclick);
            miBtnMode3.setOnClickListener(iBtnMode3Onclick);
            miBtnMode4.setOnClickListener(iBtnMode4Onclick);
        }else {
            miBtnSet = (ImageButton)findViewById(R.id.iBtnSet);
            miBtnSet.setOnClickListener(iBtnSetOnclick);
            miBtnVoice = (ImageButton)findViewById(R.id.iBtnVoice);
            miBtnVoice.setOnClickListener(iBtnVoiceOnclick);
        }

        etRead = (EditText) findViewById(R.id.editText2);
        mBtnDemo = (Button) findViewById(BtnDemo);
        mBtnDemo.setOnClickListener(mBtnDemoOnclick);

        miBtnMain = (ImageButton)findViewById(R.id.iBtnMain);
        miBtnMain.setOnClickListener(iBtnMainOnclick);
        miBtnLogo = (ImageButton)findViewById(R.id.iBtnLogo);

        mrelayValue1 = (RelativeLayout) findViewById(R.id.relativeLayout1);
        mrelayValue2 = (RelativeLayout) findViewById(R.id.relativeLayout2);
        mrelayValue3 = (RelativeLayout) findViewById(R.id.relativeLayout3);
        mrelayValue4 = (RelativeLayout) findViewById(R.id.relativeLayout4);

        miBtnAnt = (ImageButton)findViewById(R.id.iBtnAnt);
        miBtnAnt.setOnClickListener(iBtnAntOnclick);
        miBtnTunit = (ImageButton)findViewById(R.id.iBtnTunit);
        miBtnPunit = (ImageButton)findViewById(R.id.iBtnPunit);

        miBtnPress1 = (ImageButton)findViewById(R.id.iBtnPress1);
        miBtnPress2 = (ImageButton)findViewById(R.id.iBtnPress2);
        miBtnPress3 = (ImageButton)findViewById(R.id.iBtnPress3);
        miBtnPress4 = (ImageButton)findViewById(R.id.iBtnPress4);

        miBtnTemp1 = (ImageButton)findViewById(R.id.iBtnTemp1);
        miBtnTemp2 = (ImageButton)findViewById(R.id.iBtnTemp2);
        miBtnTemp3 = (ImageButton)findViewById(R.id.iBtnTemp3);
        miBtnTemp4 = (ImageButton)findViewById(R.id.iBtnTemp4);

        miBtnNo1 = (ImageButton)findViewById(R.id.iBtnNo1);
        miBtnNo1.setOnClickListener(iBtnNo1Onclick);
        miBtnNo1.setOnLongClickListener(iBtnNo1OnLongclick);
        miBtnNo2 = (ImageButton)findViewById(R.id.iBtnNo2);
        miBtnNo2.setOnClickListener(iBtnNo2Onclick);
        miBtnNo2.setOnLongClickListener(iBtnNo2OnLongclick);
        miBtnNo3 = (ImageButton)findViewById(R.id.iBtnNo3);
        miBtnNo3.setOnClickListener(iBtnNo3Onclick);
        miBtnNo3.setOnLongClickListener(iBtnNo3OnLongclick);
        miBtnNo4 = (ImageButton)findViewById(R.id.iBtnNo4);
        miBtnNo4.setOnClickListener(iBtnNo4Onclick);
        miBtnNo4.setOnLongClickListener(iBtnNo4OnLongclick);

        miBtnBt1 = (ImageButton)findViewById(R.id.iBtnBt1);
        miBtnBt2 = (ImageButton)findViewById(R.id.iBtnBt2);
        miBtnBt3 = (ImageButton)findViewById(R.id.iBtnBt3);
        miBtnBt4 = (ImageButton)findViewById(R.id.iBtnBt4);

        mtViewVP1 = (TextView)findViewById(R.id.tViewVP1);
        mtViewVP2 = (TextView)findViewById(R.id.tViewVP2);
        mtViewVP3 = (TextView)findViewById(R.id.tViewVP3);
        mtViewVP4 = (TextView)findViewById(tViewVP4);

        mtViewVT1 = (TextView)findViewById(R.id.tViewVT1);
        mtViewVT2 = (TextView)findViewById(R.id.tViewVT2);
        mtViewVT3 = (TextView)findViewById(R.id.tViewVT3);
        mtViewVT4 = (TextView)findViewById(R.id.tViewVT4);

    }


    public  void  init_RXFLAG() {
        for (int i=0; i<wheelN_k; i++){
            RxFlag[i] = false;
            PressHFlag[i] = false;
            PressLFlag[i] = false;
            TempHFlag[i] = false;
            PressHRFlag[i] = false;
            PressLRFlag[i] = false;
            TempHRFlag[i] = false;

            FLASH_WHEEL[i] = true;
        }
    }



    public void init_para() {

        for (int i=0; i<wheelN_k; i++){
//            GetFlag[i] = false;
            RxFlag[i] = false;
            PressHFlag[i] = false;
            PressLFlag[i] = false;
            TempHFlag[i] = false;
            PressHRFlag[i] = false;
            PressLRFlag[i] = false;
            TempHRFlag[i] = false;
            FLASH_WHEEL[i] = false;
        }
        PressData[0]= 32.5;
        PressData[1]= 33.0;
        PressData[2]= 32.0;
        PressData[3]= 31.5;
        PressDataBuf[0]= 32.5;
        PressDataBuf[1]= 33.0;
        PressDataBuf[2]= 32.0;
        PressDataBuf[3]= 31.5;
        TempData[0]= 25;
        TempData[1]= 24;
        TempData[2]= 26;
        TempData[3]= 25;
        TempDataBuf[0]= 25;
        TempDataBuf[1]= 24;
        TempDataBuf[2]= 26;
        TempDataBuf[3]= 25;


        w1press = 32;
        w2press = 32;
        w3press = 33;
        w4press = 32;
        w1temp = 25;
        w2temp = 25;
        w3temp = 25;
        w4temp = 25;
        w1rx = false;
        w2rx = false;
        w3rx = false;
        w4rx = false;
        w1bt = false;
        w2bt = true;
        w3bt = false;
        w4bt = false;
        w1p = false;
        w2p = false;
        w3p = false;
        w4p = false;
        w1reflash = false;
        w2reflash = false;
        w3reflash = false;
        w4reflash = false;


        init_alarmflag();

        LimitPLow = 26;
        LimitPHigh = 45;
        LimitTHigh = 70;
    }//init_para
    public void init_alarmflag() {
        w1pLow = false;
        w2pLow = false;
        w3pLow = false;
        w4pLow = false;
        w1pHigh = false;
        w2pHigh = false;
        w3pHigh = false;
        w4pHigh = false;
        w1tHigh = false;
        w2tHigh = false;
        w3tHigh = false;
        w4tHigh = false;
        alarmflag = false;
        alarmflag2 = false;
    }

    public void init_display() {
        if (DEBUG_MODE == true) {
            if (SHOW_PROTOCOL == true) {
                etRead.setVisibility(View.VISIBLE);
            } else {
                etRead.setVisibility(View.INVISIBLE);
            }
            SHOW_LOG = true;
        }else{
            etRead.setVisibility(View.INVISIBLE);
            SHOW_LOG = false;
        }

        if (BLEint_FLAG == true){
            mBtnDemo.setText(R.string.moduleint);
            mBtnDemo.setHint(R.string.PushScan);
            mBtnDemo.setVisibility(View.VISIBLE);
        }else{
            if (mGetDevice == false){
                mBtnDemo.setText(R.string.Demo);
                mBtnDemo.setHint(R.string.PushScan);
                mBtnDemo.setVisibility(View.VISIBLE);
            }else{
                if (saveValueF[Fac] == 1){
                    mBtnDemo.setText(R.string.modefac);
                    mBtnDemo.setHint("");
                    mBtnDemo.setVisibility(View.VISIBLE);
                }else{
                    mBtnDemo.setText("");
                    mBtnDemo.setHint("");
                    mBtnDemo.setVisibility(View.INVISIBLE);
                }
            }
        }
        if (saveValueF[Fac] == 1){
            mBtnDemo.setText(R.string.Demo);
            mBtnDemo.setHint(R.string.PushScan);
            mBtnDemo.setVisibility(View.VISIBLE);
        }





        if (screenPL==true){
            init_icon_Port();
            init_fac();
        } else {
            init_icon_Land();
        }

//        if (saveValue[0] == 999 || saveValue[0] == 0){
//            mtViewVT1.setText(String.valueOf(w1temp));
//            mtViewVT2.setText(String.valueOf(w2temp));
//            mtViewVT3.setText(String.valueOf(w3temp));
//            mtViewVT4.setText(String.valueOf(w4temp));
//            mtViewVP1.setText(String.valueOf(w1press+".0"));
//            mtViewVP2.setText(String.valueOf(w2press+".0"));
//            mtViewVP3.setText(String.valueOf(w3press+".0"));
//            mtViewVP4.setText(String.valueOf(w4press+".0"));
//        }else {
//        }

    }//init_display

    private void init_fac(){
        fac_INVISIBLE();
        if (saveValueF[Learn] == 1){
            mBtnLearx.setVisibility(View.VISIBLE);
            mBtnLend.setVisibility(View.VISIBLE);
        }else if(saveValueF[Check] == 1){
            fac_INVISIBLE();
        }else if(saveValueF[Cal] == 1){
            mBtnLend.setVisibility(View.VISIBLE);
            mBtnFac.setVisibility(View.VISIBLE);
            mBtnCal.setVisibility(View.VISIBLE);
        }else if(saveValueF[Test] == 1){
            mBtnLend.setVisibility(View.VISIBLE);
            mBtnTest.setVisibility(View.VISIBLE);
        }else if(saveValueF[Emode] == 1){
            fac_VISIBLE();
        }else if(saveValueF[QA] == 1){
            mBtnLend.setVisibility(View.VISIBLE);
            mBtnTest.setVisibility(View.VISIBLE);
            mBtnVer.setVisibility(View.VISIBLE);
            mBtnGetid.setVisibility(View.VISIBLE);
            miSpinGetid.setVisibility(View.VISIBLE);
            mBtnTimeron.setVisibility(View.VISIBLE);
            mBtnTimeroff.setVisibility(View.VISIBLE);
        }

    }
    private void fac_INVISIBLE(){
        mBtnLearn.setVisibility(View.INVISIBLE);
        mBtnLearx.setVisibility(View.INVISIBLE);
        mBtnLend.setVisibility(View.INVISIBLE);
        mBtnFac.setVisibility(View.INVISIBLE);
        mBtnCal.setVisibility(View.INVISIBLE);
        mBtnTest.setVisibility(View.INVISIBLE);
        mBtnVer.setVisibility(View.INVISIBLE);
        mBtnGetid.setVisibility(View.INVISIBLE);
        miSpinGetid.setVisibility(View.INVISIBLE);
        mBtnTimeron.setVisibility(View.INVISIBLE);
        mBtnTimeroff.setVisibility(View.INVISIBLE);
        mBtnTimer.setVisibility(View.INVISIBLE);
        miSpinTimer.setVisibility(View.INVISIBLE);
    }
    private void fac_VISIBLE(){
        mBtnLearn.setVisibility(View.VISIBLE);
        mBtnLearx.setVisibility(View.VISIBLE);
        mBtnLend.setVisibility(View.VISIBLE);
        mBtnFac.setVisibility(View.VISIBLE);
        mBtnCal.setVisibility(View.VISIBLE);
        mBtnTest.setVisibility(View.VISIBLE);
        mBtnVer.setVisibility(View.VISIBLE);
        mBtnGetid.setVisibility(View.VISIBLE);
        miSpinGetid.setVisibility(View.VISIBLE);
        mBtnTimeron.setVisibility(View.VISIBLE);
        mBtnTimeroff.setVisibility(View.VISIBLE);
        mBtnTimer.setVisibility(View.VISIBLE);
        miSpinTimer.setVisibility(View.VISIBLE);
    }
    public void init_icon_Port() {
        miBtnMain.setImageResource(R.drawable.p_main_car_0);
        miBtnLogo.setImageResource(R.drawable.p_logo_tyredog_0);

        miBtnAnt.setImageResource(R.drawable.p_sign_rx_3);

        miBtnMode1.setImageResource(R.drawable.p_mode_status_1);
        miBtnMode2.setImageResource(R.drawable.p_mode_voice_0);
        miBtnMode3.setImageResource(R.drawable.p_mode_set_0);
        miBtnMode4.setImageResource(R.drawable.p_mode_about_0);


        miBtnTunit.setImageResource(R.drawable.p_unitt_c_0);
        miBtnPunit.setImageResource(R.drawable.p_unitp_psi_0);

        miBtnPress1.setImageResource(R.drawable.p_sign_press_0);
        miBtnPress2.setImageResource(R.drawable.p_sign_press_0);
        miBtnPress3.setImageResource(R.drawable.p_sign_press_0);
        miBtnPress4.setImageResource(R.drawable.p_sign_press_0);
        miBtnTemp1.setImageResource(R.drawable.p_sign_temp_0);
        miBtnTemp2.setImageResource(R.drawable.p_sign_temp_0);
        miBtnTemp3.setImageResource(R.drawable.p_sign_temp_0);
        miBtnTemp4.setImageResource(R.drawable.p_sign_temp_0);

        miBtnNo1.setImageResource(R.drawable.p_no_1_0);
        miBtnNo2.setImageResource(R.drawable.p_no_2_0);
        miBtnNo3.setImageResource(R.drawable.p_no_3_0);
        miBtnNo4.setImageResource(R.drawable.p_no_4_0);

        miBtnBt1.setImageResource(R.drawable.p_sign_bt_0);
        miBtnBt2.setImageResource(R.drawable.p_sign_bt_0);
        miBtnBt3.setImageResource(R.drawable.p_sign_bt_0);
        miBtnBt4.setImageResource(R.drawable.p_sign_bt_0);
    }//init_icon_Port
    public void init_icon_Land() {

        miBtnMain.setImageResource(R.drawable.p_main_car_0);
        miBtnLogo.setImageResource(R.drawable.l_logo_tyredog_0);

        miBtnAnt.setImageResource(R.drawable.l_sign_rx_3);

        miBtnSet.setImageResource(R.drawable.l_mode_set_0);
        miBtnVoice.setImageResource(R.drawable.l_mode_voice_0);

        miBtnTunit.setImageResource(R.drawable.l_unitt_c_0);
        miBtnPunit.setImageResource(R.drawable.l_unitp_psi_0);

        miBtnPress1.setImageResource(R.drawable.l_sign_press_0);
        miBtnPress2.setImageResource(R.drawable.l_sign_press_0);
        miBtnPress3.setImageResource(R.drawable.l_sign_press_0);
        miBtnPress4.setImageResource(R.drawable.l_sign_press_0);
        miBtnTemp1.setImageResource(R.drawable.l_sign_temp_0);
        miBtnTemp2.setImageResource(R.drawable.l_sign_temp_0);
        miBtnTemp3.setImageResource(R.drawable.l_sign_temp_0);
        miBtnTemp4.setImageResource(R.drawable.l_sign_temp_0);

        miBtnNo1.setImageResource(R.drawable.l_no_1_0);
        miBtnNo2.setImageResource(R.drawable.l_no_2_0);
        miBtnNo3.setImageResource(R.drawable.l_no_3_0);
        miBtnNo4.setImageResource(R.drawable.l_no_4_0);

        miBtnBt1.setImageResource(R.drawable.l_sign_bt_0);
        miBtnBt2.setImageResource(R.drawable.l_sign_bt_0);
        miBtnBt3.setImageResource(R.drawable.l_sign_bt_0);
        miBtnBt4.setImageResource(R.drawable.l_sign_bt_0);
    }//init_icon_Land

    private void myimageviewsize(ImageButton imgid1, int evenWidth, int evenHight) {
// TODO 自動產生的方法 Stub
        ViewGroup.LayoutParams params1 = imgid1.getLayoutParams();  //需import android.view.ViewGroup.LayoutParams;
        if (!(evenWidth == 0)) {
            params1.width = evenWidth;
        }
        if (!(evenHight == 0)) {
            params1.height = evenHight;
        }
    }//myimageviewsize
    private void relayoutsize(RelativeLayout imgid2, int evenWidth, int evenHight) {
// TODO 自動產生的方法 Stub
        ViewGroup.LayoutParams params2 = imgid2.getLayoutParams();
        if (!(evenWidth == 0)) {
            params2.width = evenWidth;
        }
        if (!(evenHight == 0)) {
            params2.height = evenHight;
        }
    }//relayoutsize
    private void textviewsize(TextView imgid1, int evenWidth, int evenHight) {
// TODO 自動產生的方法 Stub
        ViewGroup.LayoutParams params1 = imgid1.getLayoutParams();
        if (!(evenWidth == 0)) {
            params1.width = evenWidth;
        }
        if (!(evenHight == 0)) {
            params1.height = evenHight;
        }
    }//myimageviewsize
    private void imageviewaddress(ImageButton imgid3, int left, int top, int right, int bottom) {
// TODO 自動產生的方法 Stub
        RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams) imgid3.getLayoutParams();
        params3.setMargins(left, top, right, bottom);
        imgid3.setLayoutParams(params3);
    }//imageviewaddress
    private void relayoutaddress(RelativeLayout imgid4, int left, int top, int right, int bottom) {
// TODO 自動產生的方法 Stub
        RelativeLayout.LayoutParams params4 = (RelativeLayout.LayoutParams) imgid4.getLayoutParams();
        params4.setMargins(left, top, right, bottom);
        imgid4.setLayoutParams(params4);
    }//relayoutaddress
    private void textviewaddress(TextView imgid3, int left, int top, int right, int bottom) {
// TODO 自動產生的方法 Stub
        RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams) imgid3.getLayoutParams();
        params3.setMargins(left, top, right, bottom);
        imgid3.setLayoutParams(params3);
    }//imageviewaddress



    private void soundEnable(){
//        sound_cnt = alarmcnt_k2;
        handler8.postDelayed(soundTimer, 1000);
    }//soundEnable
    private void soundDisable(){
        sound_cnt = alarmcnt_k2;
        handler8.removeCallbacks(soundTimer);
    }//soundDisable


    private Runnable RxanticonTimerOff = new Runnable() {
        public void run() {
            RXflash_enable = false;
            handler7.postDelayed(this, 3000);
        }
    };

    private Runnable RxanticonTimer = new Runnable() {
        public void run() {
            rxant_cnt = rxant_cnt + 1;
            if (rxant_cnt >=4) {
                rxant_cnt = 0;
            }
            if (RXflash_enable ==true){
                displayerant(rxant_cnt);
            } else {
                rxant_cnt = 0;
                displayerant(rxant_cnt);
            }
            handler6.postDelayed(this, 250);
        }
    };



    private Runnable ChksetTimer = new Runnable() {
        public void run() {
//            etRead.setText(String.valueOf(GetPara(saveKey[SETFLAG])));
            if (GetPara(saveKey[SETFLAG]) == 1){
                unit_update();
                SetPara(saveKey[SETFLAG],0);
                if (GetPara(saveKey[CLOSE]) == 1){
                    SetPara(saveKey[CLOSE],0);
                    askcloseapp();
                }
            }
            if (GetPara(saveKeyV[SETVFLAG]) == 1){
                unit_vupdate();
                SetPara(saveKeyV[SETVFLAG],0);
                if (GetPara(saveKeyV[CLOSEV]) == 1){
                    SetPara(saveKeyV[CLOSEV],0);
                    askcloseapp();
                }
            }
            handler9.postDelayed(this, 1000);
        }
    };




    private Runnable DemoTimer = new Runnable() {
        public void run() {
            handler10.removeCallbacks(DemoTimer);
            if (demo_cnt == demo_cnt_k){
                demo_cnt = 0;
            }
            RxFlag[demoWheel[demo_cnt]] = true;
            PressData[demoWheel[demo_cnt]] = demoPvalue[demo_cnt];
            TempData[demoWheel[demo_cnt]] = demoTvalue[demo_cnt];
            BtFlag[demoWheel[demo_cnt]] = demoVvalue[demo_cnt];

            checkdata(demoWheel[demo_cnt]);
            handler5.postDelayed(RxreflashTimer, RxreflashTimer_k);

            demo_cnt = demo_cnt + 1;
            handler10.postDelayed(this, 10000);
        }
    };


    private Runnable soundTimer = new Runnable() {
        public void run() {
            sound_cnt = sound_cnt + 1;

            handler8.removeCallbacks(soundTimer);
            if (sound_cnt >=alarmcnt_k2){
                sound_cnt = alarmcnt_k2;
//                Log.d(TAG, "Sound to arrved 15 times");
//                soundDisable();

                handler8.postDelayed(soundTimer, 1000);
            }else if (sound_cnt >=alarmcnt_k1) {
                setsound();
//                Log.d(TAG, "Sound to arrved 10 times");
                handler8.postDelayed(soundTimer, 10000);
            }else {
                setsound();
//                Log.d(TAG, "Sound to < 10 times");
                handler8.postDelayed(soundTimer, 5000);
            }
        }
    };


    private void setsound(){
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        voice_max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
//        voice_value = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
//        voice_level = ((double)voice_value/(double)voice_max)*100;
        if (saveValueV[AlarmFlag] == 1){
            if (saveValueV[SAFLAG] == 0){
                voice_level = ((double)saveValueV[AlarmAppV]/(double)saveValueV[AlarmMax])*100;
            }else {
                voice_level = ((double)saveValueV[AlarmSysV]/(double)saveValueV[AlarmMax])*100;
            }
        }else {
            voice_level = 0;
        }
        int ringerMode = mAudioManager.getRingerMode();
        if ((ringerMode == AudioManager.RINGER_MODE_SILENT) || (ringerMode == AudioManager.RINGER_MODE_VIBRATE)) {
            return;
        }
        ToneGenerator toneG = new ToneGenerator(STREAM_ALARM,((int)voice_level));
        toneG.stopTone();
        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, TONE_LENGTH_MS);
    }//setsound


    private void getVoicevalue_init(){
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        saveValueV[AlarmMax] = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        saveValueV[AlarmSysV] = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);

        saveValueV[NotiyMax] = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
        saveValueV[NotiySysV] = mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);

//        Log.d(TAG,String.valueOf(saveValueV[AlarmMax])+" "+String.valueOf(saveValueV[AlarmSysV])+" "+String.valueOf(saveValueV[NotiyMax])+" "+String.valueOf(saveValueV[NotiySysV]));
        SetPara(saveKeyV[AlarmMax],saveValueV[AlarmMax]);
        SetPara(saveKeyV[AlarmSysV],saveValueV[AlarmSysV]);
        SetPara(saveKeyV[NotiyMax],saveValueV[NotiyMax]);
        SetPara(saveKeyV[NotiySysV],saveValueV[NotiySysV]);
    }
    private void getVoicevalue(){
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        saveValueV[AlarmMax] = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        saveValueV[AlarmSysV] = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);

        saveValueV[NotiyMax] = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
        saveValueV[NotiySysV] = mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);

//        Log.d(TAG,String.valueOf(saveValueV[AlarmMax])+" "+String.valueOf(saveValueV[AlarmSysV])+" "+String.valueOf(saveValueV[NotiyMax])+" "+String.valueOf(saveValueV[NotiySysV]));
        SetPara(saveKeyV[AlarmMax],saveValueV[AlarmMax]);
        SetPara(saveKeyV[AlarmSysV],saveValueV[AlarmSysV]);
        SetPara(saveKeyV[NotiyMax],saveValueV[NotiyMax]);
        SetPara(saveKeyV[NotiySysV],saveValueV[NotiySysV]);
    }
    private void setVoice(){
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int ringerMode = mAudioManager.getRingerMode();
        if ((ringerMode == AudioManager.RINGER_MODE_SILENT) || (ringerMode == AudioManager.RINGER_MODE_VIBRATE)) {
            return;
        }
        if (saveValueV[SAFLAG] == 0){
                mAudioManager.setStreamVolume(AudioManager.STREAM_RING,saveValueV[AlarmAppV],AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,saveValueV[NotiyAppV],AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        }else {
                mAudioManager.setStreamVolume(AudioManager.STREAM_RING,saveValueV[AlarmSysV],AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,saveValueV[NotiySysV],AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        }
    }

    private void setsound_RF(){
        ToneGenerator toneGRF = new ToneGenerator(STREAM_ALARM,100);
//        toneGRF.stopTone();
        toneGRF.startTone(ToneGenerator.TONE_CDMA_ONE_MIN_BEEP, TONE_LENGTH_MS_RF);
    }//setsound_RF




    public void setVibrate(int time){
        Vibrator myVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        myVibrator.vibrate(time);
    }//setVibrate





    private void timerdisable(){
        handler1.removeCallbacks(T1enable);
//        handler1.postDelayed(T1enable, 1000);
        handler2.removeCallbacks(ClrFaccnt);
//        handler2.postDelayed(ClrFaccnt, 3000);
        handler3.removeCallbacks(RxCharTimer);
//        handler3.postDelayed(RxreflashTimer, 500);
        handler4.removeCallbacks(IconjpTimer);
//        handler4.postDelayed(IconjpTimer, Iconjptime);
        handler5.removeCallbacks(RxreflashTimer);
//        handler5.postDelayed(RxreflashTimer, 500);
        handler6.removeCallbacks(RxanticonTimer);
//        handler6.postDelayed(RxanticonTimer, 250);
        handler7.removeCallbacks(RxanticonTimerOff);
//        handler7.postDelayed(RxanticonTimerOff, 2000);
        handler8.removeCallbacks(soundTimer);
//        handler8.postDelayed(soundTimer, 5000);
        handler9.removeCallbacks(ChksetTimer);
//        handler9.postDelayed(ChksetTimer, 5000);
        handler10.removeCallbacks(DemoTimer);
    }






    private void SetNotification(int ID, String title, StringBuffer text,int res,int largeres,int sound){
//        NotificationChannel channelAlarm = new NotificationChannel(
//                idAlarm,
//                "Channel Alarm",
//                NotificationManager.IMPORTANCE_HIGH);
//        channelAlarm.setDescription("TPMS Alarm");
//        channelAlarm.enableLights(true);
//        channelAlarm.enableVibration(true);
//        notificationManager.createNotificationChannel(channelAlarm);
//

        final int requestCode = ID; // PendingIntent的Request Code
        final Intent intent = getIntent(); // 目前Activity的Intent
        final int flags = PendingIntent.FLAG_CANCEL_CURRENT; // ONE_SHOT：PendingIntent只使用一次；CANCEL_CURRENT：PendingIntent執行前會先結束掉之前的；NO_CREATE：沿用先前的PendingIntent，不建立新的PendingIntent；UPDATE_CURRENT：更新先前PendingIntent所帶的額外資料，並繼續沿用
        final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), requestCode, intent, flags); // 取得PendingIntent

        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); // 取得系統的通知服務
        final Notification notification = new Notification.Builder(getApplicationContext())
                    .setSmallIcon(res)
                    .setLargeIcon(BitmapFactory. decodeResource(this.getResources(),largeres))
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(title)
                    .setContentText(text)
                    .setAutoCancel(true)
                    .setVibrate(vibrate_noti)
                    .setFullScreenIntent(pendingIntent, false)
//                    .setChannelId(idAlarm)
                    .setContentIntent(pendingIntent).build(); // 建立通知
//        notification.defaults |= Notification.DEFAULT_SOUND;
        if (saveValueV[NotiyFlag] == 1){
            notification.sound = Uri.parse("android.resource://" + getPackageName() + "/" +sound);
        }
        notificationManager.notify(ID, notification); // 發送通知
    }


    final static int[] alermicon = {R.mipmap.ic_tyredog,R.mipmap.ic_tyredog,R.mipmap.ic_tyredog,R.mipmap.ic_tyredog};
    final static int[] alermlargeicon = {R.drawable.alarmw1,R.drawable.alarmw2,R.drawable.alarmw3,R.drawable.alarmw4};
    final static int[] alermsound_p = {R.raw.pfl,R.raw.pfr,R.raw.prl,R.raw.prr};
    final static int[] alermsound_t = {R.raw.tfl,R.raw.tfr,R.raw.trl,R.raw.trr};
    final static int[] alermsound_s = {R.raw.sfl,R.raw.sfr,R.raw.srl,R.raw.srr};


    final static int[] alermtitle = {R.string.pressalarm,R.string.pressalarm,R.string.tempalarm,R.string.sensoralarm};
    final static int[] str_wheel = {R.string.wheel0,R.string.wheel1,R.string.wheel2,R.string.wheel3};
    final static int[] str_type0 = {R.string.press,R.string.press,R.string.temp,R.string.sensor};
    final static int[] str_type1 = {R.string.pressH,R.string.pressL,R.string.tempH,R.string.btL};

    private void EnNoti_alarm(int wheel,int type){
//        setVibrate(1000);
        int icon = alermicon[wheel];
        int largeicon = alermlargeicon[wheel];

        int soundfile = alermsound_p[wheel];
        if (type == 0){
            soundfile = alermsound_p[wheel];
        }else if (type == 1){
            soundfile = alermsound_p[wheel];
        }else if (type == 2){
            soundfile = alermsound_t[wheel];
        }else{
            soundfile = alermsound_s[wheel];
        }

        String title =  getString(alermtitle[type]);

        StringBuffer content = new StringBuffer();
        content.delete(0,content.length());
        content.append(getString(str_wheel[wheel]));
        content.append(" "+getString(str_type0[type]));
        content.append(" "+getString(str_type1[type]));
        int notiyID = 999;
        if (notiyID_same_FLAG == true){
            notiyID = 999;
        } else {
            notiyID = wheel;
        }
        SetNotification(notiyID,title,content,icon,largeicon,soundfile);
    }

    private void About(){
        //產生視窗物件
        miBtnMode1.setImageResource(R.drawable.p_mode_status_0);
        miBtnMode4.setImageResource(R.drawable.p_mode_about_1);

        AlertDialog.Builder AboutDialog = new AlertDialog.Builder(blescreenMain.this);
        AboutDialog.setTitle(R.string.about);//設定視窗標題
        AboutDialog.setIcon(R.mipmap.ic_tyredog);//設定對話視窗圖示
        AboutDialog.setCancelable(false);
//        AboutDialog.setMessage("\nJOSN Electronic Co., Ltd.\n\n☆Tel: (02)2299-6900\n\n☆Mail: sales@josn.com.tw\n\n☆Web: www.josn.com.tw\n\n☆Address: 3F, No.12, Wugong 6th Rd., Xinzhuang Dist., New Taipei City, 242, Taiwan\n\n\nVersion:"+getVersionInfo());//設定顯示的文字
        AboutDialog.setMessage(companyinfo());//設定顯示的文字


        AboutDialog.setPositiveButton(R.string.OK,new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        if (which == KeyEvent.KEYCODE_SEARCH)
//                        {
//                            return;
//                        }
                        dialog.cancel();
                        miBtnMode1.setImageResource(R.drawable.p_mode_status_1);
                        miBtnMode4.setImageResource(R.drawable.p_mode_about_0);
                    }
                });//設定結束的子視窗
        AboutDialog.show();//呈現對話視窗
    }


    private String companyinfo(){
        String info = ("\n"+ (RStrToStr(R.string.company)) +"\n\n"+ (RStrToStr(R.string.phone)) +"\n\n"+ (RStrToStr(R.string.mail)) +"\n\n"+ (RStrToStr(R.string.web)) +"\n\n"+ (RStrToStr(R.string.address)) +"\n\n\n"+ "Version: " +getVersionInfo());
        return info;
    }
    private String RStrToStr(int resid){
        String Str = getResources().getString(resid);
        return Str;
    }


    private String getVersionInfo() {
        String versionName = "";
        int versionCode = -1;
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getVersionInfo: "+String.format("Version name = %s \nVersion code = %d", versionName, versionCode));
        return versionName;
    }



    private void CheckReScan(){
        //產生視窗物件
        AlertDialog.Builder ReScanDialog = new AlertDialog.Builder(blescreenMain.this);
        ReScanDialog.setTitle(R.string.scanerror);//設定視窗標題
        ReScanDialog.setIcon(R.mipmap.ic_tyredog);//設定對話視窗圖示
        ReScanDialog.setCancelable(false);
        ReScanDialog.setMessage(R.string.checkscan);//設定顯示的文字
        ReScanDialog.setPositiveButton(R.string.rescan,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Toast.makeText(blescreenMain.this, R.string.module_power, Toast.LENGTH_SHORT).show();
                final Intent intent = new Intent(blescreenMain.this, blescan.class);    //跳轉並傳遞資料(name&address)
                startActivity(intent);
                finish();
            }
        });//設定結束的子視窗
        ReScanDialog.setNeutralButton(R.string.close,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });//設定結束的子視窗
        ReScanDialog.setNegativeButton(R.string.Cancel,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                BLEint_FLAG = true;
                mBtnDemo.setText(R.string.moduleint);
                mBtnDemo.setHint(R.string.PushScan);
                mBtnDemo.setVisibility(View.VISIBLE);
            }
        });//設定結束的子視窗
        ReScanDialog.show();//呈現對話視窗
    }


    private void askcloseapp(){
        new AlertDialog.Builder(blescreenMain.this)
                .setTitle(R.string.LOGO)
                .setMessage(R.string.closeapp)
                .setIcon(R.mipmap.ic_tyredog)
                .setPositiveButton(R.string.OK,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
//                                    unregisterbleReceiver();
                                finish();
                            }
                        })
                .setNegativeButton(R.string.Cancel,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub

                            }
                        }).show();
    }


    public double PutPointN(double number,int bit){
        double rnumber =  (double)(Math.round(number*(Math.pow(10,bit))))/(Math.pow(10,bit));
        return rnumber;
    }





    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            askcloseapp();
        }else if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            int ringerMode = mAudioManager.getRingerMode();
            if ((ringerMode == AudioManager.RINGER_MODE_SILENT) || (ringerMode == AudioManager.RINGER_MODE_VIBRATE)) {
                return true;
            }
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_RING,AudioManager.ADJUST_LOWER,AudioManager.FLAG_SHOW_UI);
            voice_value = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
            saveValueV[AlarmSysV] = voice_value;
            SetPara(saveKeyV[AlarmSysV],saveValueV[AlarmSysV]);
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION,AudioManager.ADJUST_LOWER,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            voice_value = mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
            saveValueV[NotiySysV] = voice_value;
            SetPara(saveKeyV[NotiySysV],saveValueV[NotiySysV]);
        }else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            int ringerMode = mAudioManager.getRingerMode();
            if ((ringerMode == AudioManager.RINGER_MODE_SILENT) || (ringerMode == AudioManager.RINGER_MODE_VIBRATE)) {
                return true;
            }
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_RING,AudioManager.ADJUST_RAISE,AudioManager.FLAG_SHOW_UI);
            voice_value = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
            saveValueV[AlarmSysV] = voice_value;
            SetPara(saveKeyV[AlarmSysV],saveValueV[AlarmSysV]);
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION,AudioManager.ADJUST_RAISE,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            voice_value = mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
            saveValueV[NotiySysV] = voice_value;
            SetPara(saveKeyV[NotiySysV],saveValueV[NotiySysV]);
        }else {
        }

        return true;
    }


    private void init_FacBtn_BG(){
//        mBtnLearn.setBackground(getResources().getDrawable(R.drawable.rectangle_drawable_gary));
//        mBtnLearx.setBackground(getResources().getDrawable(R.drawable.rectangle_drawable_gary));
//        mBtnLend.setBackground(getResources().getDrawable(R.drawable.rectangle_drawable_gary));
//        mBtnFac.setBackground(getResources().getDrawable(R.drawable.rectangle_drawable_gary));
//        mBtnCal.setBackground(getResources().getDrawable(R.drawable.rectangle_drawable_gary));
//        mBtnTest.setBackground(getResources().getDrawable(R.drawable.rectangle_drawable_gary));
//        mBtnVer.setBackground(getResources().getDrawable(R.drawable.rectangle_drawable_gary));
//        mBtnGetid.setBackground(getResources().getDrawable(R.drawable.rectangle_drawable_gary));
//        mBtnTimeron.setBackground(getResources().getDrawable(R.drawable.rectangle_drawable_gary));
//        mBtnTimeroff.setBackground(getResources().getDrawable(R.drawable.rectangle_drawable_gary));
//        mBtnTimer.setBackground(getResources().getDrawable(R.drawable.rectangle_drawable_gary));
//
        mBtnLearn.setBackgroundColor(getResources().getColor(R.color.divider_color));
        mBtnLearx.setBackgroundColor(getResources().getColor(R.color.divider_color));
        mBtnLend.setBackgroundColor(getResources().getColor(R.color.divider_color));
        mBtnFac.setBackgroundColor(getResources().getColor(R.color.divider_color));
        mBtnCal.setBackgroundColor(getResources().getColor(R.color.divider_color));
        mBtnTest.setBackgroundColor(getResources().getColor(R.color.divider_color));
        mBtnVer.setBackgroundColor(getResources().getColor(R.color.divider_color));
        mBtnGetid.setBackgroundColor(getResources().getColor(R.color.divider_color));
        mBtnTimeron.setBackgroundColor(getResources().getColor(R.color.divider_color));
        mBtnTimeroff.setBackgroundColor(getResources().getColor(R.color.divider_color));
        mBtnTimer.setBackgroundColor(getResources().getColor(R.color.divider_color));
    }

    private View.OnClickListener mBtnLearnOnclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            init_FacBtn_BG();
            mBtnLearn.setBackgroundColor(getResources().getColor(R.color.divider_color2));
            Cmd_LEARN();
            Cmd_TC211();
        }
    };
    private View.OnClickListener mBtnLearxOnclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            init_FacBtn_BG();
            mBtnLearx.setBackgroundColor(getResources().getColor(R.color.divider_color2));
            Cmd_LEARX();
            Cmd_TC45();
        }
    };
    private View.OnClickListener mBtnLendOnclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            init_FacBtn_BG();
            mBtnLend.setBackgroundColor(getResources().getColor(R.color.divider_color2));
            Cmd_LEND();
            Cmd_TC210();
        }
    };
    private View.OnClickListener mBtnFacOnclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            init_FacBtn_BG();
            mBtnFac.setBackgroundColor(getResources().getColor(R.color.divider_color2));
            Cmd_FAC();
            Cmd_TC50();
        }
    };
    private View.OnClickListener mBtnCalOnclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            init_FacBtn_BG();
            mBtnCal.setBackgroundColor(getResources().getColor(R.color.divider_color2));
            Cmd_CAL();
            Cmd_TC540();
        }
    };
    private View.OnClickListener mBtnTestOnclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            init_FacBtn_BG();
            mBtnTest.setBackgroundColor(getResources().getColor(R.color.divider_color2));
            Cmd_TEST();
        }
    };
    private View.OnClickListener mBtnVerOnclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            init_FacBtn_BG();
            mBtnVer.setBackgroundColor(getResources().getColor(R.color.divider_color2));
            Cmd_VER();
            Cmd_TC40();
        }
    };
    private View.OnClickListener mBtnGetidOnclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            init_FacBtn_BG();
            mBtnGetid.setBackgroundColor(getResources().getColor(R.color.divider_color2));
            Cmd_Getid();
        }
    };
    private View.OnClickListener mBtnTimeronOnclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            init_FacBtn_BG();
            mBtnTimeron.setBackgroundColor(getResources().getColor(R.color.divider_color2));
            Cmd_T0();
            Cmd_TC241();
        }
    };
    private View.OnClickListener mBtnTimeroffOnclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            init_FacBtn_BG();
            mBtnTimeroff.setBackgroundColor(getResources().getColor(R.color.divider_color2));
            Cmd_T1();
            Cmd_TC240();
        }
    };
    private View.OnClickListener mBtnTimerOnclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            init_FacBtn_BG();
            mBtnTimer.setBackgroundColor(getResources().getColor(R.color.divider_color2));
            Cmd_Timer();
        }
    };



    private AdapterView.OnItemSelectedListener spinGetidSelected
            = new AdapterView.OnItemSelectedListener()
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        {
            // TODO Auto-generated method stub
            getid_cnt=position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0)
        {
            // TODO Auto-generated method stub
        }
    };

    private AdapterView.OnItemSelectedListener spinTimerSelected
            = new AdapterView.OnItemSelectedListener()
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        {
            // TODO Auto-generated method stub
            timer_cnt=position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0)
        {
            // TODO Auto-generated method stub
        }
    };








    private View.OnClickListener mBtnDemoOnclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mBtnDemo.getVisibility() == View.VISIBLE){

                new AlertDialog.Builder(blescreenMain.this)
                        .setTitle(R.string.rescan)
                        .setMessage(R.string.resetscan)
                        .setIcon(R.mipmap.ic_tyredog)
                        .setPositiveButton(R.string.OK,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        Intent i = new Intent(blescreenMain.this,blescan.class);
                                        startActivity(i);//開始跳往要去的Activity
                                        mBtnDemo.setVisibility(View.INVISIBLE);
                                        finish();
                                    }
                                })
                        .setNegativeButton(R.string.Cancel,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub

                                    }
                                }).show();
            }
        }
    };

    private View.OnClickListener iBtnMainOnclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (DEBUG_MODE == true){
                if (SHOW_PROTOCOL == true) {
                    SHOW_PROTOCOL = false;
                    etRead.setVisibility(View.INVISIBLE);
                } else {
                    SHOW_PROTOCOL = true;
                    etRead.setVisibility(View.VISIBLE);
                }
            }
        }
    };
    private View.OnClickListener iBtnAntOnclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            handler2.removeCallbacks(ClrFaccnt);
            handler2.postDelayed(ClrFaccnt, 2000);
            if (Faccnt == 0){
                Faccnt = 1;
            }else if((Faccnt>=1) && (Faccnt<5)){
                Faccnt = Faccnt+1;
            }else {

            }
            Log.d(TAG,"Faccnt" + String.valueOf(Faccnt));
        }
    };



    private View.OnLongClickListener iBtnNo1OnLongclick= new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {

            if (Jpno_cnt == 0){
                Jpno_cnt = 1;
                enable_iconjp();
            }else{
            }
            return true;
        }
    };
    private View.OnLongClickListener iBtnNo2OnLongclick= new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {

            if (Jpno_cnt == 0){
                Jpno_cnt = 2;
                enable_iconjp();
            }else{
            }
            return true;
        }
    };    private View.OnLongClickListener iBtnNo3OnLongclick= new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {

            if (Jpno_cnt == 0){
                Jpno_cnt = 3;
                enable_iconjp();
            }else{
            }
            return true;
        }
    };    private View.OnLongClickListener iBtnNo4OnLongclick= new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {

            if (Jpno_cnt == 0){
                Jpno_cnt = 4;
                enable_iconjp();
            }else{
            }
            return true;
        }
    };

    private View.OnClickListener iBtnNo1Onclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Jpno_cnt != 0){
                chkchwheel(1);
            }else{
            }
        }
    };

    private void enable_iconjp(){
//        setVibrate(250);
        icomjpcnt = 0;
        iconjp_updn = true;
        if (screenPL == true){
            Iconjptime = Iconjptime_k;
        }else{
            Iconjptime = l_Iconjptime_k;
        }
        handler4.postDelayed(IconjpTimer, Iconjptime);
    }
    private void chkchwheel(int wheel){
        if (wheel == Jpno_cnt){
        }else{
            setVibrate(100);
            int wheelbuf = CHANGE_WHEEL[wheel-1];
            CHANGE_WHEEL[wheel-1] = CHANGE_WHEEL[Jpno_cnt-1];
            CHANGE_WHEEL[Jpno_cnt-1] = wheelbuf;
            savewheel();
            if (screenPL == true){
                Reflash();
            }else{
                l_Reflash();
            }
//            Toast.makeText(blescreenMain.this, "Test8", Toast.LENGTH_SHORT).show();
            ToastShort(R.string.chwok);
            setVibrate(1000);
        }
        handler4.removeCallbacks(IconjpTimer);
        jpno_origan();
        Jpno_cnt = 0;
        Log.d(TAG, Arrays.toString(CHANGE_WHEEL));
    }


    private View.OnClickListener iBtnNo2Onclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Jpno_cnt != 0){
                chkchwheel(2);
            }else{
            }
        }
    };
    private View.OnClickListener iBtnNo3Onclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Jpno_cnt != 0){
                chkchwheel(3);
            }else{
            }
        }
    };
    private View.OnClickListener iBtnNo4Onclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Jpno_cnt != 0){
                chkchwheel(4);
            }else{
            }
        }
    };

    private View.OnClickListener iBtnMode1Onclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            handler2.removeCallbacks(ClrFaccnt);
            handler2.postDelayed(ClrFaccnt, 2000);
            if ((Faccnt >= 5) && (Faccnt < 10)){
                Faccnt = Faccnt+1;
            }else if(Faccnt >= 10){
                Faccnt = 0;
                handler2.removeCallbacks(ClrFaccnt);
                new AlertDialog.Builder(blescreenMain.this)
                        .setTitle(R.string.epeople)
                        .setMessage(R.string.openfac)
                        .setIcon(R.mipmap.ic_tyredog)
                        .setPositiveButton(R.string.OK,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        saveValueF[Fac] = 1;
                                        saveparaF();
                                        finish();
                                    }
                                })
                        .setNegativeButton(R.string.Cancel,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub

                                    }
                                }).show();
            }
            Log.d(TAG,"Faccnt" + String.valueOf(Faccnt));
//            setsound_RF();
//            Cmd_T1();
//            Cmd_LEARX();
        }
    };
    private View.OnClickListener iBtnMode2Onclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(blescreenMain.this,voicesetting.class);
            startActivity(i);//開始跳往要去的Activity
//            Cmd_T0();
//            Cmd_TEST();
//            Cmd_LEND();
//            EnNoti_alarm(2,1);
        }
    };
    private View.OnClickListener iBtnMode3Onclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(blescreenMain.this,blesetting.class);
            startActivity(i);//開始跳往要去的Activity
//            Cmd_LEND();
        }
    };
    private View.OnClickListener iBtnMode4Onclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            Intent i = new Intent(blescreenMain.this,otgabout.class);
//            startActivity(i);//開始跳往要去的Activity
//            Cmd_LEARX();
//              Cmd_LEND();
//            loadpara();
//            loadparaV();
            About();
        }
    };

    private View.OnClickListener iBtnSetOnclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(blescreenMain.this,blesetting.class);
            startActivity(i);//開始跳往要去的Activity

        }
    };
    private View.OnClickListener iBtnVoiceOnclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(blescreenMain.this,voicesetting.class);
            startActivity(i);//開始跳往要去的Activity
        }
    };




    private  void MainClose(){
        soundDisable();
        timerdisable();

    }




    public static void registerPush() {
        final String token = FirebaseInstanceId.getInstance().getToken();
//        if (token != null) {
//            new AsyncTask<Void, Void, Void>() {
//                protected Void doInBackground(Void... params) {
//                    mClient.getPush().register(token);
//                    return null;
//                }
//            }.execute();
//        }
        Log.d(TAG, "Token:"+token);
    }



    private Runnable RxCharTimer = new Runnable() {
        public void run() {
            handler3.removeCallbacks(RxCharTimer);
            mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, true);//開啟Notification
        }
    };



    private Runnable IconjpTimer = new Runnable() {
        public void run() {
            handler4.removeCallbacks(IconjpTimer);
            if (screenPL == true){
                if (iconjp_updn == false){
                    icomjpcnt = icomjpcnt - icomjpcnt_k1;
                    if (icomjpcnt <= 0){
                        iconjp_updn = true;
                        icomjpcnt = 0;
                    }
                }else{
                    icomjpcnt = icomjpcnt + icomjpcnt_k0;
                    if (icomjpcnt >= icomjp0_k){
                        iconjp_updn = false;
                        icomjpcnt = icomjp0_k;
                    }
                }
            }else{
                if (iconjp_updn == false){
                    icomjpcnt = icomjpcnt - l_icomjpcnt_k1;
                    if (icomjpcnt >= 0){
                        iconjp_updn = true;
                        icomjpcnt = 0;
                    }
                }else{
                    icomjpcnt = icomjpcnt + l_icomjpcnt_k0;
                    if (icomjpcnt >= l_icomjp0_k){
                        iconjp_updn = false;
                        icomjpcnt = l_icomjp0_k;
                    }
                }
            }
            displayjpno(icomjpcnt);

            if (screenPL == true){
                Iconjptime = Iconjptime_k;
            }else{
                Iconjptime = l_Iconjptime_k;
            }
            handler4.postDelayed(IconjpTimer, Iconjptime);
        }
    };


    private void jpno_origan(){
        icomjpcnt = 0;
        displayjpno(icomjpcnt);
    }
    private void displayjpno(double jpcnt){
        if (screenPL == true){
            if (Jpno_cnt == 1){
                iconTop(miBtnNo1,NoLeft_k,NoTop_k,0,0,1,jpcnt);
            }else if(Jpno_cnt == 2){
                iconTop(miBtnNo2,NoLeft_k,NoTop_k,0,0,1,jpcnt);
            }else if(Jpno_cnt == 3){
                iconTop(miBtnNo3,NoLeft_k,NoTop_k,0,0,1,jpcnt);
            }else if(Jpno_cnt == 4) {
                iconTop(miBtnNo4,NoLeft_k,NoTop_k,0,0,1,jpcnt);
            }else{
            }
        }else{
            if (Jpno_cnt == 1){
                iconTop(miBtnNo1,0,l_NoTop_k,l_NoRight_k,0,2,jpcnt);
            }else if(Jpno_cnt == 2){
                iconTop(miBtnNo2,0,l_NoTop_k,l_NoRight_k,0,2,jpcnt);
            }else if(Jpno_cnt == 3){
                iconTop(miBtnNo3,0,l_NoTop_k,l_NoRight_k,0,2,jpcnt);
            }else if(Jpno_cnt == 4) {
                iconTop(miBtnNo4,0,l_NoTop_k,l_NoRight_k,0,2,jpcnt);
            }else{
            }
        }

    }

    private void iconTop(ImageButton imgid,double leftk0,double topk0,double rightk0,double bottomk0,int angle,double jprate){
        if (angle == 1){
            double shiftrate = jprate + 1;
            topk0 = topk0*shiftrate;
//            bottomk0 = bottomk0*shiftrate;
//            leftk0 = leftk0*shiftrate;
//            rightk0 = rightk0*shiftrate;
        }else if(angle == 2){
            topk0 = topk0+jprate;
        }else{

        }

        int NoLeftSfit = (int)(screenWsize*(leftk0));
        int NoTopSfit = (int)(screenHsize*(topk0));
        int NoRightSfit = (int)(screenWsize*(rightk0));
        int NoBottomSfit = (int)(screenHsize*(bottomk0));
        imageviewaddress(imgid,NoLeftSfit, NoTopSfit, NoRightSfit, NoBottomSfit);
    }


    private Runnable ClrFaccnt = new Runnable() {
        public void run() {
            handler2.removeCallbacks(ClrFaccnt);
            Faccnt = 0;
            handler2.postDelayed(ClrFaccnt, 3000);
        }
    };

    private Runnable T1enable = new Runnable() {
        public void run() {
            handler1.removeCallbacks(T1enable);
            Cmd_T1();
        }
    };




    private void sentdata(String data){
//        boolean sentsuccsee = mBluetoothLeService.writeCharacteristic(mNotifyCharacteristic,data);               // remote device 讀資料
//        mBluetoothLeService.writeDATA(mNotifyCharacteristic,data);
        if (mGetDevice == true){
            mBluetoothLeService.writeCharacteristic(mWriteCharacteristic,data);
        }
//        Log.d(TAG, "sentdata:"+String.valueOf(sentsuccsee));

//        mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, false);//開啟Notification
//        handler3.postDelayed(RxCharTimer,300);
    }



    private void displaydemo(String Text){
        if (BLEint_FLAG == true){
            mBtnDemo.setText(Text);
            mBtnDemo.setHint("");
            mBtnDemo.setVisibility(View.VISIBLE);
        }
    }


    private void displayRF(){
        RFRXcnt = RFRXcnt+1;
        if (RFRXcnt == 256){
            RFRXcnt = 0;
        }
        etRead.setText(String.valueOf(RFRXcnt));
    }





    private void Cmd_TC20(){
        Cmd_data = "$TC20#"+"";
        sentdata(Cmd_data);
        Log.d(TAG, "Read TireStatus");
    }
    private void Cmd_TC210(){
        Cmd_data = "$A0210034#";
        sentdata(Cmd_data);
        Log.d(TAG, "Enter To Normal Mode");
    }
    private void Cmd_TC211(){
        Cmd_data = "$A0210135#";
        sentdata(Cmd_data);
        Log.d(TAG, "Enter To Learnning Mode");
    }
    private void Cmd_TC22(){
        Cmd_data = "$YY2200"+"";
        sentdata(Cmd_data);
        Log.d(TAG, "Set Tire Alarm Threshood");
    }
    private void Cmd_TC23(){
        Cmd_data = "$A0230036#";
        sentdata(Cmd_data);
        Log.d(TAG, "Clear Received Flag");
    }
    private void Cmd_TC240(){
        Cmd_data = "$A0240037#";
        sentdata(Cmd_data);
        Log.d(TAG, "Set Auto Tx TireStatus Stop");
    }
    private void Cmd_TC241(){
        Cmd_data = "$A0240138#";
        sentdata(Cmd_data);
        Log.d(TAG, "Set Auto Tx TireStatus Start");
    }
    private void Cmd_TC25(){
        Cmd_data = "$A0250038#";
        sentdata(Cmd_data);
        Log.d(TAG, "Read Alarm Set");
    }
    private void Cmd_TC40(){
        Cmd_data = "$A0400035#";
        sentdata(Cmd_data);
        Log.d(TAG, "Read MD Information");
    }
    private void Cmd_TC41(){
        Cmd_data = "$A0410036#";
        sentdata(Cmd_data);
        Log.d(TAG, "Read MD ID");
    }
    private void Cmd_TC42(){

        Cmd_data = "$A042" + String.valueOf(getid_cnt) + String.valueOf(CMD_CHK) +"#";
        sentdata(Cmd_data);
        Log.d(TAG, "Read Sensor ID");
    }
    private void Cmd_TC43(){
        Cmd_data = "$A0430038#";
        sentdata(Cmd_data);
        Log.d(TAG, "Read RFRX Freq");
    }
    private void Cmd_TC44(){
        Cmd_data = "$YY44"+"";
        sentdata(Cmd_data);
        Log.d(TAG, "Set Tire Data");
    }
    private void Cmd_TC45(){
        Cmd_data = "$A045013B#";
        sentdata(Cmd_data);
        Log.d(TAG, "Enter To Learnning start code Mode");
    }
    private void Cmd_TC50(){
        Cmd_data = "$A0500036#";
        sentdata(Cmd_data);
        Log.d(TAG, "Enter To Factory Mode");
    }
    private void Cmd_TC510(){
        Cmd_data = "$A05100"+"";
        sentdata(Cmd_data);
        Log.d(TAG, "Set Auto Tx TireStatus Period Time");
    }
    private void Cmd_TC511(){
        Cmd_data = "$YY5101"+"";
        sentdata(Cmd_data);
        Log.d(TAG, "Set No TireStaus Received Time");
    }
    private void Cmd_TC512(){
        Cmd_data = "$YY5102"+"";
        sentdata(Cmd_data);
        Log.d(TAG, "Set Tire Amount");
    }
    private void Cmd_TC513(){
        Cmd_data = "$YY5103"+"";
        sentdata(Cmd_data);
        Log.d(TAG, "Set Module MDFlag");
    }
    private void Cmd_TC52(){
        Cmd_data = "$YY5200"+"";
        sentdata(Cmd_data);
        Log.d(TAG, "Set Module ID");
    }
    private void Cmd_TC53(){
        Cmd_data = "$YY5300"+"";
        sentdata(Cmd_data);
        Log.d(TAG, "Set Module Rf Freq");
    }
    private void Cmd_TC540(){
        Cmd_data = "$A054003A#";
        sentdata(Cmd_data);
        Log.d(TAG, "Module Calibration Start");
    }






    private void Cmd_T1(){
        Cmd_data = "$T1#";
        sentdata(Cmd_data);
        Log.d(TAG, "$T1# TX");
    }
    private void Cmd_T0(){
        Cmd_data = "$T0#";
        sentdata(Cmd_data);
        Log.d(TAG, "$T0# TX");
    }

    private void Cmd_LEARX(){
        Cmd_data = "$LEARX#";
        sentdata(Cmd_data);
        Log.d(TAG, "$LEARX# TX");
    }
    private void Cmd_LEARN(){
        Cmd_data = "$LEARN#";
        sentdata(Cmd_data);
        Log.d(TAG, "$LEARN# TX");
    }
    private void Cmd_LEND(){
        Cmd_data = "$LEND#";
        sentdata(Cmd_data);
        Log.d(TAG, "$LEND# TX");
    }

    private void Cmd_TEST(){
        Cmd_data = "$TEST#";
        sentdata(Cmd_data);
        Log.d(TAG, "$TEST# TX");
    }
    private void Cmd_FAC(){
        Cmd_data = "$FAC#";
        sentdata(Cmd_data);
        Log.d(TAG, "$FAC# TX");
    }
    private void Cmd_CAL(){
        Cmd_data = "$CAL#";
        sentdata(Cmd_data);
        Log.d(TAG, "$CAL# TX");
    }
    private void Cmd_SLEEP(){
        Cmd_data = "$SLEEP#";
        sentdata(Cmd_data);
        Log.d(TAG, "$SLEEP# TX");
    }

    private void Cmd_VER(){
        Cmd_data = "$VER#";
        sentdata(Cmd_data);
        Log.d(TAG, "$VER# TX");
    }
    private void Cmd_Getid(){
        Cmd_data = "$GETW" + String.valueOf(getid_cnt) + "#";
        sentdata(Cmd_data);
        Log.d(TAG, "$GETW" + String.valueOf(getid_cnt) + "#");
    }
    private void Cmd_Timer(){
        Cmd_data = "$IST1" + String.valueOf(timer_cnt) + "AB#";
        sentdata(Cmd_data);
        Log.d(TAG, "$IST1" + String.valueOf(timer_cnt) + "AB#");
    }
    private void Cmd_Timerx(){
        Cmd_data = "$IST0" + String.valueOf(timer_cnt) + "AB#";
        sentdata(Cmd_data);
        Log.d(TAG, "$IST0" + String.valueOf(timer_cnt) + "AB#");
    }

    private void CallLOG(String str){
        if (SHOW_LOG == true){
            Log.d(TAG, str);
        }
    }
    private void CallLOG(int str){
        if (SHOW_LOG == true){
            Log.d(TAG, String.valueOf(str));
        }
    }
    private void CallLOG(boolean str){
        if (SHOW_LOG == true){
            Log.d(TAG, String.valueOf(str));
        }
    }

    private void ToastShort(String Str){
        Toast.makeText(blescreenMain.this, Str, Toast.LENGTH_SHORT).show();
    }
    private void ToastShort(int Str){
        Toast.makeText(blescreenMain.this, Str, Toast.LENGTH_SHORT).show();
    }
    private void ToastLong(String Str){
        Toast.makeText(blescreenMain.this, Str, Toast.LENGTH_LONG).show();
    }
    private void ToastLong(int Str){
        Toast.makeText(blescreenMain.this, Str, Toast.LENGTH_LONG).show();
    }

/*

    public class BootBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
                //Intent.ACTION_BOOT_COMPLETED == android.intent.action.BOOT_COMPLETED

                Intent intent1 = new Intent(context , MainActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);
                //執行一個Activity

                Intent intent2 = new Intent(context , MyService.class);
                context.startService(intent2);
                //執行一個Service
            }
        }
    }
*/





}






//  当一个程序执行的时候，启动的方法有
//          onCreate，onStart，onResume
//          当点击返回键时的全部顺序为：
//          onCreate，onStart，onResume，点击返回键，onPause，onStop，onDestroy
//          再次启动程序时为点击返回键顺序为：
//          onCreate，onStart，onResume，点击返回键，onPause，onStop，onDestroy
//          当点击HOME键时的全部顺序为：
//          onCreate，onStart，onResume，点击HOME键，onPause，onStop
//          再次启动程序时为点击HOME键顺序为：
//          onRestart，onStart，onResume，点击HOME键，onPause，onStop
//          可以看出来，点击HOME键只是讲程序腿到后台，点击返回键是退出程序
//          点击自己实现菜单键运行顺序为同点击返回键效果一样




package com.sheng.tpms;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;

public class blesetting extends AppCompatActivity {



    Config_tpms mConfig = new Config_tpms();

    private boolean setwheel_FLAG = true;
    private boolean SHOW_loadpara = false;
    private boolean josn_config = false;


    private boolean screenPL = true;
    private int push_set_cnt = 0;

//    private double MainSizeW_k = 0.5;
//    private double MainTop_k = 0.2031;//0.2343;
//
//    private double LogoSizeH_k = 0.078;
//    private double LogoTop_k = 0.0244;
//
//    private double ModeSizeW_k = 0.25;
//    private double ModeBottom_k = 0;
//
//    private double ValueSizeW_k = 0.2302;
//    private double ValueSizeH_k = 0.1632;
//    private double ValueRight_k = 0;
//    private double ValueLeft_k = 0;
//    private double ValueTop1_k = 0.2285;
//    private double ValueTop2_k = 0.1632;

    int SETFLAG = 21;
    int CLOSE = 22;
    int W0 = 23;
    int W1 = 24;
    int W2 = 25;
    int W3 = 26;
    int SETWHEEL = 29;
    int WHEELN = 30;
    private String saveKey[]=new String[]{"FIRST","PUNIT","TUNIT","HPLIMIT","LPLIMIT","HTLIMIT","HPMAX","LPMAX","HTMAX","P1","P2","P3","P4","PP1","PP2","PP3","PP4","T1","T2","T3","T4","SETFLAG","CLOSE","W0","W1","W2","W3","W4","W5","SETWHEEL","WHEELN"};
    private int saveValue[]=new int[]{0,0,0,45,26,70,100,100,125,32,33,32,33,0,0,0,0,25,26,25,24,0,0,0,1,2,3,4,5,0,4};
    private int InitValue[]=new int[]{1,0,0,45,26,70,100,100,125,32,33,32,33,0,0,0,0,25,26,25,24,0,0,0,1,2,3,4,5,0,4};
    int PressPoint[]=new int[]{0,1,2,2};
    int TempPoint[]=new int[]{0,0};


    int HPmax =100; //PSI
    int LPmax =100; //PSI
    int HTmax =125; //*C
    private double HPvalue,LPvalue,HTvalue;
    private double Punit_k[]=new double[]{1,6.895,0.06895,0.07031};
    private double Tunit_k0 = 1.8;
    private int Tunit_k1 = 32;

    public String prefile = "setdata";
    public String fileopen = "filetest.txt";
    public String filename = "setdata.txt";
    public String string = "Hello world!";


    private Spinner miSpinPunit,miSpinTunit;
    private SeekBar miSebHP,miSebLP,miSebHT;

    private TextView miTxtSet,miTxtPara,miTxtHPlimit,miTxtLPlimit,miTxtHTlimit;
    private ImageButton miBtnMode1,miBtnMode2,miBtnMode3,miBtnMode4;
    private Button miBtnInitial,miBtnClose,miBtnReturn;
    private Button miBtn2Wheel,miBtn4Wheel,miBtn6Wheel;
    private ScrollView miScVSet;

    private RelativeLayout mrelativeLayoutsetting;



    private double ModeSizeW_k = 0.25;
    private double ModeBottom_k = 0;
    private double ScVSetSizeH_k = 1-0.04;


    private Handler handler1 = new Handler(); //btndetector


    String TAG = "blesetting";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Enter onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otgsetting);

        initConfig();


        getwindowsize();


        loadpara();
        init_display();

        timerdisable();
        handler1.postDelayed(btndetector, 10000);

        Log.d(TAG, "Leave onCreate");
    }//onCreate

    @Override
    protected void onStart() {
        Log.d(TAG, "Enter onStart");
        super.onStart();
        Log.d(TAG, "Leave onStart");
    }
    @Override
    protected void onResume() {
        Log.d(TAG, "Enter onResume");
        super.onResume();
        String action =  getIntent().getAction();
        Log.d(TAG, "onResume:"+action);
    }
    @Override
    protected void onPause() {
        Log.d(TAG, "Enter onPause");
        super.onPause();
        Log.d(TAG, "Leave onPause");
    }
    @Override
    protected void onStop() {
        Log.d(TAG, "Enter onStop");
        super.onStop();
        Log.d(TAG, "Leave onStop");
    }
    @Override
    protected void onDestroy() {
        Log.d(TAG, "Enter onDestroy");
        super.onDestroy();
        loadpara();
        timerdisable();
        Log.d(TAG, "Leave onDestroy");
    }


    private void timerdisable() {
        handler1.removeCallbacks(btndetector);
//        handler1.postDelayed(btndetector, 1000);
    }

    private Runnable btndetector = new Runnable() {
        public void run() {
            handler1.removeCallbacks(btndetector);
            push_set_cnt = 0;
            handler1.postDelayed(btndetector, 10000);
        }
    };


    private  void  initConfig() {

        setwheel_FLAG = mConfig.setwheel_FLAG;
        SHOW_loadpara = mConfig.SHOW_loadpara;
        josn_config = mConfig.josn_config;
    }

    private void getwindowsize() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int screenWidth = dm.widthPixels;
        int screenHeigth = dm.heightPixels;
        float screenDensity = dm.density;

        float horiDpi = dm.xdpi;
        float vertDpi = dm.ydpi;



        if (screenWidth >= screenHeigth){
            screenPL = false;              //landscape
        } else {
            screenPL = true;               //portrait
        }


        init_findview(screenPL);

//        mTextView1.setText("手機銀幕大小為 " + screenWidth + " X " + screenHeigth + " Density" + screenDensity + " horiDpi" + horiDpi + " vertDpi" + vertDpi);

        if (screenPL == true) {
            //ScroView
            int ScVSetSizeH = (int)(screenHeigth*(ScVSetSizeH_k-(screenWidth*(ModeSizeW_k))/screenHeigth));
            myscrollviewsize(miScVSet,0,(ScVSetSizeH));
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


        } else {

        }
    }//getwindowsize



    public void init_findview(boolean screenPL) {
        if (screenPL == true) {

            miScVSet =  (ScrollView) findViewById(R.id.iScVSet);

            miBtnMode1 = (ImageButton)findViewById(R.id.iBtnMode1);
            miBtnMode2 = (ImageButton)findViewById(R.id.iBtnMode2);
            miBtnMode3 = (ImageButton)findViewById(R.id.iBtnMode3);
            miBtnMode4 = (ImageButton)findViewById(R.id.iBtnMode4);
            miBtnMode1.setOnClickListener(iBtnMode1Onclick);
            miBtnMode2.setOnClickListener(iBtnMode2Onclick);
            miBtnMode3.setOnClickListener(iBtnMode3Onclick);
            miBtnMode4.setOnClickListener(iBtnMode4Onclick);
        }else {
            miBtnReturn = (Button)findViewById(R.id.iBtnReturn);
            miBtnReturn.setOnClickListener(iBtnReturnOnclick);
        }

        miTxtSet = (TextView) findViewById(R.id.iTxtSet);
        miTxtSet.setOnClickListener(iTxtSetOnclick);
        miTxtSet.setMovementMethod(ScrollingMovementMethod.getInstance());

        miTxtPara = (TextView) findViewById(R.id.iTxtPara);
        miTxtHPlimit = (TextView) findViewById(R.id.iTxtHPlimit);
        miTxtLPlimit = (TextView) findViewById(R.id.iTxtLPlimit);
        miTxtHTlimit = (TextView) findViewById(R.id.iTxtHTlimit);

        miBtnInitial = (Button)findViewById(R.id.iBtnInitial);
        miBtnClose = (Button)findViewById(R.id.iBtnClose);
        miBtnInitial.setOnClickListener(iBtnInitialOnclick);
        miBtnClose.setOnClickListener(iBtnCloseOnclick);

        mrelativeLayoutsetting = (RelativeLayout) findViewById(R.id.relativeLayoutsetting);

        miBtn2Wheel = (Button) findViewById(R.id.iBtn2Wheel);
        miBtn4Wheel = (Button) findViewById(R.id.iBtn4Wheel);
        miBtn6Wheel = (Button)findViewById(R.id.iBtn6Wheel);
        miBtn2Wheel.setOnClickListener(iBtn2WheelOnclick);
        miBtn4Wheel.setOnClickListener(iBtn4WheelOnclick);
        miBtn6Wheel.setOnClickListener(iBtn6WheelOnclick);


        miSpinPunit = (Spinner) findViewById(R.id.iSpinPunit);
        miSpinPunit.setOnItemSelectedListener(spinPunitSelected);
        miSpinTunit = (Spinner) findViewById(R.id.iSpinTunit);
        miSpinTunit.setOnItemSelectedListener(spinTunitSelected);

        miSebHP=(SeekBar) findViewById(R.id.iSebHP);
        miSebHP.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int position_buf = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < saveValue[4]){
                    position_buf = saveValue[4];
                }else {
                    position_buf = progress;
                }
                saveValue[3]=position_buf;
                display_SebHP();
                display_HP();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                saveValue[3]=position_buf;
                display_HP();
                SetPara(saveKey[3],saveValue[3]);
                SetPara(saveKey[SETFLAG],1);
            }
        });

        miSebLP=(SeekBar) findViewById(R.id.iSebLP);
        miSebLP.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int position_buf = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress > saveValue[3]){
                    position_buf = saveValue[3];
                }else {
                    position_buf = progress;
                }
                saveValue[4]=position_buf;
                display_SebLP();
                display_LP();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                saveValue[4]=position_buf;
                display_LP();
                SetPara(saveKey[4],saveValue[4]);
                SetPara(saveKey[SETFLAG],1);
            }
        });

        miSebHT=(SeekBar) findViewById(R.id.iSebHT);
        miSebHT.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int position_buf = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                position_buf = progress;
                saveValue[5]=position_buf;
                display_HT();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                saveValue[5]=position_buf;
                display_HT();
                SetPara(saveKey[5],saveValue[5]);
                SetPara(saveKey[SETFLAG],1);
            }
        });
    }

    private void savepara(){
        for (int i=0; i<saveKey.length; i++) {
            SetPara(saveKey[i],saveValue[i]);
        }
    }
    private void loadpara(){
        for (int i=0; i<saveKey.length; i++) {
            saveValue[i] = GetPara(saveKey[i]);
            Log.d(TAG,String.valueOf(saveValue[i]));
        }
    }




    private void init_display(){
        if (SHOW_loadpara == true) {
            miTxtPara.setVisibility(View.VISIBLE);
        } else {
            miTxtPara.setVisibility(View.INVISIBLE);
        }


        if (screenPL == true) {
            if (setwheel_FLAG == true) {
                mrelativeLayoutsetting.setVisibility(View.VISIBLE);
            } else {
//                mrelativeLayoutsetting.setVisibility(View.INVISIBLE);
                mrelativeLayoutsetting.setVisibility(View.GONE);
            }
        }



        if (screenPL==true){
            init_icon_Port();
        } else {
            init_icon_Land();
        }
        dispaly_para();
    }
    public void init_icon_Port() {
        if (josn_config == true) {
            if (saveValue[WHEELN] == 2){
                miBtnMode1.setImageResource(R.drawable.p_mode_status_0_j_moto);
            }else if (saveValue[WHEELN] >= 4){
                miBtnMode1.setImageResource(R.drawable.p_mode_status_0_j_car);
            }
            miBtnMode3.setImageResource(R.drawable.p_mode_set_1_j);
        } else {
            miBtnMode1.setImageResource(R.drawable.p_mode_status_0);
            miBtnMode3.setImageResource(R.drawable.p_mode_set_1);
        }


    }//init_icon_Port
    public void init_icon_Land() {



    }//init_icon_Land


//        String saveKey[]=new String[]{"FIRST","PUNIT","TUNIT","HPLIMIT","LPLIMIT","HTLIMIT","HPMAX","LPMAX","HTMAX","P1","P2","P3","P4","PP1","PP2","PP3","PP4","T1","T2","T3","T4"};
//        int saveValue[]=new int[]{0,0,0,45,26,70,100,100,125,32,33,32,33,0,0,0,0,25,26,25,24};
//        int InitValue[]=new int[]{1,0,0,45,26,70,100,100,125,32,33,32,33,0,0,0,0,25,26,25,24};
    private void dispaly_para(){
        display_SpinPunit();
        display_SpinTunit();
        display_SebHP();
        display_SebLP();
        display_SebHT();
        display_HP();
        display_LP();
        display_HT();
    }
    private void display_SpinPunit(){
        miSpinPunit.setSelection(saveValue[1],true);
    }
    private void display_SpinTunit(){
        miSpinTunit.setSelection(saveValue[2],true);
    }
    private void display_SebHP(){
        miSebHP.setMax(saveValue[6]);
        miSebHP.setProgress(saveValue[3]);
    }
    private void display_SebLP(){
        miSebLP.setMax(saveValue[7]);
        miSebLP.setProgress(saveValue[4]);
    }
    private void display_SebHT(){
        miSebHT.setMax(saveValue[8]);
        miSebHT.setProgress(saveValue[5]);
    }

    private void display_HP(){
        miSebHP.setMax(saveValue[6]);
        HPvalue = PutPointN(saveValue[3]*Punit_k[saveValue[1]],PressPoint[saveValue[1]]);
        miTxtHPlimit.setText(String.valueOf(HPvalue));
    }
    private void display_LP(){
        miSebLP.setMax(saveValue[7]);
        LPvalue = PutPointN(saveValue[4]*Punit_k[saveValue[1]],PressPoint[saveValue[1]]);
        miTxtLPlimit.setText(String.valueOf(LPvalue));
    }
    private void display_HT(){
        miSebHT.setMax(saveValue[8]);
        if (saveValue[2] == 1) {
            HTvalue =PutPointN(((saveValue[5]*Tunit_k0)+Tunit_k1),TempPoint[saveValue[2]]);
        }else{
            HTvalue = PutPointN(saveValue[5],TempPoint[saveValue[2]]);
        }
        miTxtHTlimit.setText(String.valueOf(HTvalue));
    }





    private AdapterView.OnItemSelectedListener spinPunitSelected
            = new AdapterView.OnItemSelectedListener()
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        {
            // TODO Auto-generated method stub
            saveValue[1]=position;
            SetPara(saveKey[1],saveValue[1]);
            SetPara(saveKey[SETFLAG],1);
            display_HP();
            display_LP();
//            savepara();

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0)
        {
            // TODO Auto-generated method stub
        }
    };

    private AdapterView.OnItemSelectedListener spinTunitSelected
            = new AdapterView.OnItemSelectedListener()
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        {
            // TODO Auto-generated method stub
            saveValue[2]=position;
            SetPara(saveKey[2],saveValue[2]);
            SetPara(saveKey[SETFLAG],1);
            display_HT();
//            savepara();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0)
        {
            // TODO Auto-generated method stub
        }
    };




    void createExternalStoragePrivateFile() {

//        getExtermalStoragePrivateDir("setdata");
        File f = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), filename);
        try {
            FileOutputStream outputStream = new FileOutputStream(f);
            String data = "222\n3333\n1111";
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }   //開一txt檔案  可用

    private File getExtermalStoragePrivateDir(String OpenName) {
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), OpenName);
        if (!file.mkdirs()) {
            Log.e("", "Directory not created or exist");
        }
        return file;
    }   //開一資料夾

    void deleteExternalStoragePrivateFile() {
        // Get path for the file on external storage.  If external
        // storage is not currently mounted this will fail.
        File file = new File(getExternalFilesDir(null), "files");
        if (file != null) {
            file.delete();
        }
    }   //開一資料夾

//    private void WriteToFile(String writeData){
////        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
////        File file = new File(path,filename);
//        try {
//            FileOutputStream outputStream = openFileOutput("abc", Context.MODE_PRIVATE);
//            outputStream.write(writeData.getBytes());
//            outputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

//    private void ReadToFile(String filename){
////        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//        File file = new File(getApplicationContext().getFilesDir(), filename);
//
//        FileInputStream inputStream = null;
////        StringBuilder rdstr = new StringBuilder();
//        try {
//            FileReader isr = new FileReader(file);
//            BufferedReader bufFile = new BufferedReader(isr);
//            String readData = "";
//            String temp = bufFile.readLine(); //readLine()讀取一整行
//            while (temp!=null){
//                readData+=temp + "\n";
//                temp=bufFile.readLine();
//            }
//            miTxtSet.setText(readData);
//            bufFile.close();
//            isr.close();
//            inputStream.close();
//        } catch (IOException e){
//            Log.e("ReadToFile",e.toString());
//        }
//    }

    public File getAlbumStorageDir(Context context, String filename) {
        // Get the directory for the app's private pictures directory.
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_DOCUMENTS), filename);
        if (!file.mkdirs()) {
            Log.e(TAG, "Directory not created");
        }
        return file;
    }   //建立資料夾







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


/////////////////////////////////////////////////////////////////////////////////////
    public double PutPointN(double number,int bit){
        double rnumber =  (double)(Math.round(number*(Math.pow(10,bit))))/(Math.pow(10,bit));
        return rnumber;
    }
/////////////////////////////////////////////////////////////////////////////////////



    private void About(){
        //產生視窗物件
        miBtnMode3.setImageResource(R.drawable.p_mode_set_0);
        miBtnMode4.setImageResource(R.drawable.p_mode_about_1);

        AlertDialog.Builder AboutDialog = new AlertDialog.Builder(blesetting.this);
        AboutDialog.setTitle(R.string.about);//設定視窗標題
        AboutDialog.setIcon(R.mipmap.ic_tyredog);//設定對話視窗圖示
        AboutDialog.setCancelable(false);
//        AboutDialog.setMessage("\n       JOSN Electronic Co., Ltd.\n\n☆Tel: (02)2299-6900\n\n☆Mail: sales@josn.com.tw\n\n☆Web: www.josn.com.tw\n\n☆Address: 3F, No.12, Wugong 6th Rd., Xinzhuang Dist., New Taipei City, 242, Taiwan");//設定顯示的文字
        AboutDialog.setMessage(companyinfo());//設定顯示的文字
        AboutDialog.setPositiveButton(R.string.OK,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                        if (which == KeyEvent.KEYCODE_SEARCH)
//                        {
//                            return;
//                        }
                dialog.cancel();
                miBtnMode3.setImageResource(R.drawable.p_mode_set_1);
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
    private void imageviewaddress(ImageButton imgid3, int left, int top, int right, int bottom) {
// TODO 自動產生的方法 Stub
        RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams) imgid3.getLayoutParams();
        params3.setMargins(left, top, right, bottom);
        imgid3.setLayoutParams(params3);
    }//imageviewaddress
    private void myscrollviewsize(ScrollView imgid1, int evenWidth, int evenHight) {
// TODO 自動產生的方法 Stub
        ViewGroup.LayoutParams params1 = imgid1.getLayoutParams();  //需import android.view.ViewGroup.LayoutParams;
        if (!(evenWidth == 0)) {
            params1.width = evenWidth;
        }
        if (!(evenHight == 0)) {
            params1.height = evenHight;
        }
    }//myscrollviewsize


    /////////////////////////////////////////////////////////////////////////////////////
    private View.OnClickListener iTxtSetOnclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (SHOW_loadpara == true) {
                SHOW_loadpara = false;
                miTxtPara.setVisibility(View.INVISIBLE);
            } else {
                SHOW_loadpara = true;
                miTxtPara.setVisibility(View.VISIBLE);
            }
        }
    };
/////////////////////////////////////////////////////////////////////////////////////
    private View.OnClickListener iBtnMode1Onclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
//            Intent i = new Intent(blesetting.this,otgscreen.class);
//            startActivity(i);//開始跳往要去的Activity
        }
    };
    private View.OnClickListener iBtnMode2Onclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(blesetting.this,voicesetting.class);
            startActivity(i);//開始跳往要去的Activity
            finish();
//            SetPara("FIRST",1);
        }
    };
    private View.OnClickListener iBtnMode3Onclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            WriteToFile(string);
//            ClrPara("FIRST");


            push_set_cnt = push_set_cnt + 1;
            if (push_set_cnt >= 30 ) {
                mrelativeLayoutsetting.setVisibility(View.VISIBLE);
            }

        }
    };
    private View.OnClickListener iBtnMode4Onclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            miTxtPara.setText(String.valueOf(GetPara("FIRST")));
//            Intent i = new Intent(blesetting.this,otgabout.class);
//            startActivity(i);//開始跳往要去的Activity
            About();
        }
    };
/////////////////////////////////////////////////////////////////////////////////////

    private View.OnClickListener iBtnInitialOnclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for (int i=0; i<saveKey.length; i++) {
                saveValue[i] = InitValue[i];
            }
            savepara();
            dispaly_para();
        }
    };
    private View.OnClickListener iBtnCloseOnclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            savepara();
//            System.exit(0);
            SetPara(saveKey[CLOSE],1);
            SetPara(saveKey[SETFLAG],1);
            finish();
        }
    };

    private View.OnClickListener iBtnReturnOnclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };


    private View.OnClickListener iBtn2WheelOnclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SetPara(saveKey[SETWHEEL],2);
            SetPara(saveKey[SETFLAG],1);
            finish();
        }
    };
    private View.OnClickListener iBtn4WheelOnclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SetPara(saveKey[SETWHEEL],4);
            SetPara(saveKey[SETFLAG],1);
            finish();
        }
    };
    private View.OnClickListener iBtn6WheelOnclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SetPara(saveKey[SETWHEEL],6);
            SetPara(saveKey[SETFLAG],1);
            finish();
        }
    };

}

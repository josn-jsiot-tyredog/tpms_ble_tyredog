package com.sheng.tpms;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Calendar;

import static com.sheng.tpms.R.layout.wellcome_tyredog;

/**
 * Created by sheng on 2017/11/30.
 */




public class wellcomeMain extends AppCompatActivity{

    private final static String TAG = wellcomeMain.class.getSimpleName();

    private boolean FLASH_FLAG = true;


    public String prefile = "setdata";
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
    int resetValueF[]=new int[]{1,1,0,0,0,0,0,0,0};

    private int year,month,day,hour,minute,second;



    private AlertDialog newVersionDialog; //新版本對話框

    final static int[] mainicon = {R.drawable.td_1,R.drawable.td_2,R.drawable.td_3,R.drawable.td_4,R.drawable.td_5,R.drawable.td_6,R.drawable.td_7,R.drawable.td_8,R.drawable.td_9,R.drawable.td_10};

    int flash_cnt = 0;
    int flashwait_k = 90;
    int flashwaitToast_k = 75;
    int FlashTimer_k = 100;


    private ImageButton miBtnMainlogo;
    private Button miBtnLearn,miBtnCheck,miBtnCal,miBtnTest,miBtnBletest,miBtnEmode,miBtnExit,miBtnQA,miBtnVer;


    private Handler handler1 = new Handler(); //FlashTimer


    private double MainSizeW_k = 1;
    private double MainSizeH_k = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        newVersionDialog = alertDialog("有新版本", "是否更新");
//        intoNextPage();
        setContentView(wellcome_tyredog);

        loadparaF();


        getwindowsize();
        if (saveValueF[Fac] != 1){
            if (FLASH_FLAG == true){
                handler1.removeCallbacks(FlashTimer);
                handler1.postDelayed(FlashTimer, 500);
            }else{
                go2blescan();
            }
        }
    }


    private  void init_findview(){

        if (saveValueF[Fac] != 1){
            miBtnMainlogo = (ImageButton)findViewById(R.id.iBtnMainlogo);
        }else {

            miBtnLearn = (Button)findViewById(R.id.iBtnLearn);
            miBtnLearn.setOnClickListener(miBtnLearnOnclick);
            miBtnCheck = (Button)findViewById(R.id.iBtnCheck);
            miBtnCheck.setOnClickListener(miBtnCheckOnclick);
            miBtnCal = (Button)findViewById(R.id.iBtnCal);
            miBtnCal.setOnClickListener(miBtnCalOnclick);
            miBtnTest = (Button)findViewById(R.id.iBtnTest);
            miBtnTest.setOnClickListener(miBtnTestOnclick);
            miBtnBletest = (Button)findViewById(R.id.iBtnBletest);
            miBtnBletest.setOnClickListener(miBtnBletestOnclick);
            miBtnEmode = (Button)findViewById(R.id.iBtnEmode);
            miBtnEmode.setOnClickListener(miBtnEmodeOnclick);
            miBtnExit = (Button)findViewById(R.id.iBtnExit);
            miBtnExit.setOnClickListener(miBtnExitOnclick);
            miBtnQA = (Button)findViewById(R.id.iBtnQA);
            miBtnQA.setOnClickListener(miBtnQAOnclick);
            miBtnVer = (Button)findViewById(R.id.iBtnVer);
            miBtnVer.setOnClickListener(miBtnVerOnclick);
        }
    }


    private void getwindowsize() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //screen size
        int screenWidth = dm.widthPixels;
        int screenHeigth = dm.heightPixels;
        float screenDensity = dm.density;
        //screen density
        float horiDpi = dm.xdpi;
        float vertDpi = dm.ydpi;


//        if (screenWidth >= screenHeigth) {
//            screenPL = false;              //landscape
//        } else {
//            screenPL = true;               //portrait
//        }

        if (saveValueF[Fac] != 1){
            setContentView(R.layout.wellcome_tyredog);
        }else {
            setContentView(R.layout.fac_tyredog);


        }

        init_findview();

        if (saveValueF[Fac] != 1){
            //Main
            int MainSizeW = (int)(screenWidth*(MainSizeW_k));
            int MainSizeH = (int)(screenHeigth*(MainSizeH_k));
            myimageviewsize(miBtnMainlogo,(MainSizeW),(MainSizeH));
        }

    }


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

        //對話框按下back不能取消的監聽
//        newVersionDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//
//                if ((keyCode == KeyEvent.KEYCODE_BACK)) {
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        });
//
//        對話框碰到外邊不會被取消
//        newVersionDialog.setCanceledOnTouchOutside(false);
//


        Log.d(TAG, "onResume");
    }


    //往別的activity
    //返回
    @Override
    protected void onPause() {                  //撤銷 mGattUpdateReceiver
        super.onPause();
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
        Log.d(TAG, "onDestroy");
    }


    private Runnable FlashTimer = new Runnable() {
        public void run() {
            if (flash_cnt >= (mainicon.length + flashwait_k)){
                handler1.removeCallbacks(FlashTimer);
                go2blescan();
            } else if (flash_cnt == (mainicon.length)){
                Toast.makeText(wellcomeMain.this, R.string.readysscan, Toast.LENGTH_LONG).show();
                flash_cnt = flash_cnt + 1;
                handler1.postDelayed(FlashTimer, FlashTimer_k);
            } else if (flash_cnt > (mainicon.length)){
                flash_cnt = flash_cnt + 1;
                handler1.postDelayed(FlashTimer, FlashTimer_k);
            } else {
                miBtnMainlogo.setImageResource(mainicon[flash_cnt]);
                flash_cnt = flash_cnt + 1;
                handler1.postDelayed(FlashTimer, FlashTimer_k);
            }
        }
    };



    private void go2blescan(){
        Intent i = new Intent(wellcomeMain.this,blescan.class);
        startActivity(i);//開始跳往要去的Activity
        finish();
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




//    StringRequest appVersion = BaseApi.appVersion(new ResponseListener() {
//        @Override
//        public void onError(VolleyError error) {
//            super.onError(error);
//            intoNextPage(); //有可能網路出錯但仍需讓使用者可以進入下一個頁面
//        }
//
//        @Override
//        public void onResponse(String str) {
//            super.onResponse(str);
//
//            Pattern pattern = Pattern.compile("\"softwareVersion\"\\W*([\\d\\.]+)");
//            Matcher matcher = pattern.matcher(str);
//            if (matcher.find()) {
//
//                if (!ValueUtility.getCurrentVersionName().equals(matcher.group(1))) {
//                    newVersionDialog.show();
//                } else {
//                    intoNextPage();
//                }
//
//            } else { //有可能Google Play的網頁內容變動，但仍需讓使用者可以進到下一頁面
//                intoNextPage();
//            }
//
//        }
//    });
//
    private AlertDialog alertDialog(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                go2GooglePlay();
                finish();
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        return builder.create();

    }

    private void go2GooglePlay() {
        final String appPackageName = this.getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }



    private void intoNextPage(){
        setContentView(wellcome_tyredog);
        getwindowsize();
        handler1.removeCallbacks(FlashTimer);
        handler1.postDelayed(FlashTimer, 500);
    }


//    public static void registerPush() {
//        final String token = FirebaseInstanceId.getInstance().getToken();
//        if (token != null) {
//            new AsyncTask<Void, Void, Void>() {
//                protected Void doInBackground(Void... params) {
//                    mClient.getPush().register(token);
//                    return null;
//                }
//            }.execute();
//        }
//        Log.d(TAG, "Token:"+token);
//    }



    private void saveparaF(){
        for (int i=0; i<saveKeyF.length; i++) {
            SetPara(saveKeyF[i],saveValueF[i]);
        }
    }
    private void loadparaF(){
        for (int i=0; i<saveKeyF.length; i++) {
            saveValueF[i] = GetPara(saveKeyF[i]);
            Log.d(TAG,"loadparaF"+":"+String.valueOf(saveValueF[i]));
        }
        if (saveValueF[0] == 999 || saveValueF[0] == 0){
            for (int i=0; i<saveKeyF.length; i++) {
                saveValueF[i] = initValueF[i];
            }
            saveparaF();
        }else if(saveValueF[Fac] == 1){
            for (int i=0; i<saveKeyF.length; i++) {
                saveValueF[i] = resetValueF[i];
            }
            saveparaF();
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


    private void getSysTime(){
        Calendar systime = Calendar.getInstance();
        year = systime.get(Calendar.YEAR);
        month = systime.get(Calendar.MONTH);
        day = systime.get(Calendar.DAY_OF_MONTH);
        hour = systime.get(Calendar.HOUR_OF_DAY);
        minute = systime.get(Calendar.MINUTE);
        second = systime.get(Calendar.SECOND);
    }




    private String companyinfo(){
        String info = ("\n"+ (RStrToStr(R.string.company)) +"\n\n"+ (RStrToStr(R.string.phone)) +"\n\n"+ (RStrToStr(R.string.mail)) +"\n\n"+ (RStrToStr(R.string.web)) +"\n\n"+ (RStrToStr(R.string.address)) +"\n\n\n"+ "Version: " +getVersionInfo());
        return info;
    }
    private String RStrToStr(int resid){
        String Str = getResources().getString(resid);
        return Str;
    }

    private void ToastShort(String Str){
        Toast.makeText(wellcomeMain.this, Str, Toast.LENGTH_SHORT).show();
    }
    private void ToastShort(int Str){
        Toast.makeText(wellcomeMain.this, Str, Toast.LENGTH_SHORT).show();
    }
    private void ToastLong(String Str){
        Toast.makeText(wellcomeMain.this, Str, Toast.LENGTH_LONG).show();
    }
    private void ToastLong(int Str){
        Toast.makeText(wellcomeMain.this, Str, Toast.LENGTH_LONG).show();
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



    private View.OnClickListener miBtnLearnOnclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            saveValueF[Learn] = 1;
            saveparaF();
            go2blescan();
        }
    };
    private View.OnClickListener miBtnCheckOnclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            saveValueF[Check] = 1;
            saveparaF();
            go2blescan();
        }
    };
    private View.OnClickListener miBtnCalOnclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            saveValueF[Cal] = 1;
            saveparaF();
            go2blescan();
        }
    };
    private View.OnClickListener miBtnTestOnclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            saveValueF[Test] = 1;
            saveparaF();
            go2blescan();
        }
    };
    private View.OnClickListener miBtnBletestOnclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            saveValueF[BleTest] = 1;
//            saveparaF();
//            go2blescan();
        }
    };
    private View.OnClickListener miBtnEmodeOnclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            saveValueF[Emode] = 1;
            saveparaF();
            go2blescan();
        }
    };
    private View.OnClickListener miBtnQAOnclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            saveValueF[QA] = 1;
            saveparaF();
            go2blescan();
        }
    };
    private View.OnClickListener miBtnExitOnclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            saveValueF[Fac] = 0;
            saveparaF();
            go2blescan();
        }
    };


    private View.OnClickListener miBtnVerOnclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        ToastShort("Ver: " + getVersionInfo());
        }
    };



}


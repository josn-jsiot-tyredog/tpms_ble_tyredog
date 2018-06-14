package com.sheng.tpms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import static android.media.AudioManager.STREAM_ALARM;
import static com.sheng.tpms.R.id.iTxtPara;
import static com.sheng.tpms.R.id.iTxtValarmV;
import static com.sheng.tpms.R.id.iTxtValarmV1;
import static com.sheng.tpms.R.id.iTxtVnotiy;
import static com.sheng.tpms.R.id.iTxtVnotiyV;
import static com.sheng.tpms.R.id.iTxtVnotiyV1;

/**
 * Created by sheng on 2017/11/30.
 */



public class voicesetting extends AppCompatActivity {



    public boolean SHOW_loadpara = false;
    private boolean screenPL = true;
    private int voice_value;

    public String prefile = "setdata";
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
    String saveKey[]=new String[]{"Voice","AlarmSysV","NotiySysV","AlarmAppV","NotiyAppV","AlarmMax","NotiyMax","SETVFLAG","SAFLAG","AlarmFlag","NotiyFlag","CLOSEV"};
    int saveValue[]=new int[]{0,0,0,0,0,0,0,0,1,1,1,0};
    int initValue[]=new int[]{0,0,0,0,0,0,0,0,1,1,1,0};

    boolean chkcancel_Flag = false;
    boolean chkcancel_Flag2 = false;

    private TextView miTxtSet,miTxtCloseitem,miTxtValarm,miTxtValarmV,miTxtVnotiy,miTxtVnotiyV,miTxtValarm1,miTxtValarmV1,miTxtVnotiy1,miTxtVnotiyV1,miTxtPara;
    private SeekBar miSebValarmV,miSebVnotiyV,miSebValarmV1,miSebVnotiyV1;
    private ImageButton miBtnMode1,miBtnMode2,miBtnMode3,miBtnMode4;
    private Button miBtnClose,miBtnReturn;
    private CheckBox michkBoxSystem,michkBoxApp,michkBoxAlarm,michkBoxNotiy;
    private ScrollView miScVSet;


    private double ModeSizeW_k = 0.25;
    private double ModeBottom_k = 0;
    private double ScVSetSizeH_k = 1-0.04;

    String TAG = "voicesetting";

    private static final int TONE_LENGTH_MS = 100; // 延遲時間


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Enter onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voicesetting);


        getwindowsize();

        loadpara();
        init_display();



        Log.d(TAG, "Leave onCreate");
    }

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


        Log.d(TAG, "Leave onDestroy");
    }



    private void getwindowsize() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //screen size
        int screenWidth = dm.widthPixels;
        int screenHeigth = dm.heightPixels;


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
            miBtnClose = (Button)findViewById(R.id.iBtnClose);
            miBtnClose.setOnClickListener(iBtnCloseOnclick);
            miBtnReturn = (Button)findViewById(R.id.iBtnReturn);
            miBtnReturn.setOnClickListener(iBtnReturnOnclick);
        }




        miTxtSet = (TextView) findViewById(R.id.iTxtSet);
//        miTxtSet.setOnClickListener(iTxtSetOnclick);
//        miTxtSet.setMovementMethod(ScrollingMovementMethod.getInstance());

        miTxtSet = (TextView) findViewById(R.id.iTxtSet);
        miTxtCloseitem = (TextView) findViewById(R.id.iTxtCloseitem);
        miTxtValarm = (TextView) findViewById(R.id.iTxtValarm);
        miTxtValarmV = (TextView) findViewById(iTxtValarmV);
        miTxtVnotiy = (TextView) findViewById(iTxtVnotiy);
        miTxtVnotiyV = (TextView) findViewById(iTxtVnotiyV);
        miTxtValarm1 = (TextView) findViewById(R.id.iTxtValarm1);
        miTxtValarmV1 = (TextView) findViewById(iTxtValarmV1);
        miTxtVnotiy1 = (TextView) findViewById(R.id.iTxtVnotiy1);
        miTxtVnotiyV1 = (TextView) findViewById(iTxtVnotiyV1);
        miTxtPara = (TextView) findViewById(iTxtPara);


        michkBoxSystem = (CheckBox) findViewById(R.id.ichkBoxSystem);
        michkBoxApp = (CheckBox) findViewById(R.id.ichkBoxApp);
        michkBoxAlarm = (CheckBox) findViewById(R.id.ichkBoxAlarm);
        michkBoxNotiy = (CheckBox) findViewById(R.id.ichkBoxNotiy);

        michkBoxSystem.setOnCheckedChangeListener(chklistener);
        michkBoxApp.setOnCheckedChangeListener(chklistener);
        michkBoxAlarm.setOnCheckedChangeListener(chklistener);
        michkBoxNotiy.setOnCheckedChangeListener(chklistener);






        miSebValarmV=(SeekBar) findViewById(R.id.iSebValarmV);
        miSebValarmV.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int position_buf = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "Progress"+String.valueOf(progress));
//                if (chkcancel_Flag2 == false){
                    position_buf = progress;
                    saveValue[AlarmSysV]=position_buf;
                    display_SebAS();
                    display_AS();
//                }else{
//                    chkcancel_Flag2 = false;
//                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "strat");
                position_buf = 999;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "end");
                if (position_buf != 999){
                    saveValue[AlarmSysV]=position_buf;
                    display_AS();
                    SetPara(saveKey[AlarmSysV],saveValue[AlarmSysV]);
                    SetPara(saveKey[SETVFLAG],1);
                    setVoice("AlarmSys");
//                setsound(AlarmSysV,AlarmMax);

                }
            }
        });

        miSebVnotiyV=(SeekBar) findViewById(R.id.iSebVnotiyV);
        miSebVnotiyV.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int position_buf = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (chkcancel_Flag2 == false){
                    position_buf = progress;
                    saveValue[NotiySysV]=position_buf;
                    display_SebNS();
                    display_NS();
//                }else{
//                    chkcancel_Flag2 = false;
//                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                position_buf = 999;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (position_buf != 999) {
                    saveValue[NotiySysV] = position_buf;
                    display_NS();
                    SetPara(saveKey[NotiySysV], saveValue[NotiySysV]);
                    SetPara(saveKey[SETVFLAG], 1);
                    setVoice("NotiySys");
//                setsound(NotiySysV,NotiyMax);
                }
            }
        });

        miSebValarmV1=(SeekBar) findViewById(R.id.iSebValarmV1);
        miSebValarmV1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int position_buf = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (chkcancel_Flag2 == false){
                    position_buf = progress;
                    saveValue[AlarmAppV]=position_buf;
                    display_SebAA();
                    display_AA();
//                }else{
//                    chkcancel_Flag2 = false;
//                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                position_buf = 999;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (position_buf != 999) {
                    saveValue[AlarmAppV] = position_buf;
                    display_AA();
                    SetPara(saveKey[AlarmAppV], saveValue[AlarmAppV]);
                    SetPara(saveKey[SETVFLAG], 1);
                    setVoice("AlarmApp");
//                setsound(AlarmAppV,AlarmMax);
                }
            }
        });

        miSebVnotiyV1=(SeekBar) findViewById(R.id.iSebVnotiyV1);
        miSebVnotiyV1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int position_buf = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (chkcancel_Flag2 == false){
                    position_buf = progress;
                    saveValue[NotiyAppV]=position_buf;
                    display_SebNA();
                    display_NA();
//                }else{
//                    chkcancel_Flag2 = false;
//                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                position_buf = 999;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (position_buf != 999) {
                    saveValue[NotiyAppV] = position_buf;
                    display_NA();
                    SetPara(saveKey[NotiyAppV], saveValue[NotiyAppV]);
                    SetPara(saveKey[SETVFLAG], 1);
                    setVoice("NotiyApp");
//                setsound(NotiyAppV,NotiyMax);
                }
            }
        });

    }


    private void init_display(){
        if (SHOW_loadpara == true) {
            miTxtPara.setVisibility(View.VISIBLE);
        } else {
            miTxtPara.setVisibility(View.INVISIBLE);
        }

        if (screenPL==true){
            init_icon_Port();
        } else {
            init_icon_Land();
        }

        dispaly_para();
    }
    public void init_icon_Port() {
        miBtnMode1.setImageResource(R.drawable.p_mode_status_0);
        miBtnMode2.setImageResource(R.drawable.p_mode_voice_1);
        miBtnMode3.setImageResource(R.drawable.p_mode_set_0);
        miBtnMode4.setImageResource(R.drawable.p_mode_about_0);
    }//init_icon_Port
    public void init_icon_Land() {

    }//init_icon_Land

    private void dispaly_para(){
        display_Chkbox();

        display_SebAS();
        display_SebNS();
        display_SebAA();
        display_SebNA();

        display_AS();
        display_NS();
        display_AA();
        display_NA();
    }



//    String saveKey[]=new String[]{"Voice","AlarmSysV","NotiySysV","AlarmAppV","NotiyAppV","AlarmMax","NotiyMax","SETVFLAG","SAFLAG","AlarmFlag","NotiyFlag"};
private void display_Chkbox(){
    if (saveValue[SAFLAG] == 0){
        michkBoxSystem.setChecked(false);
        michkBoxApp.setChecked(true);
        miSebValarmV.setBackgroundResource(R.color.grey);
        miSebVnotiyV.setBackgroundResource(R.color.grey);
        miSebValarmV1.setBackgroundResource(R.color.light_grey);
        miSebVnotiyV1.setBackgroundResource(R.color.light_grey);
    }else{
        michkBoxSystem.setChecked(true);
        michkBoxApp.setChecked(false);
        miSebValarmV.setBackgroundResource(R.color.light_grey);
        miSebVnotiyV.setBackgroundResource(R.color.light_grey);
        miSebValarmV1.setBackgroundResource(R.color.grey);
        miSebVnotiyV1.setBackgroundResource(R.color.grey);
    }
    if (saveValue[AlarmFlag] == 0){
        michkBoxAlarm.setChecked(true); //close alarm
    }else{
        michkBoxAlarm.setChecked(false);
    }
    if (saveValue[NotiyFlag] == 0){
        michkBoxNotiy.setChecked(true); //close Notiy
    }else{
        michkBoxNotiy.setChecked(false);
    }
}





    private void display_SebAS(){
//        chkcancel_Flag2 = true;
        Log.d(TAG,"a");
        miSebValarmV.setMax(saveValue[AlarmMax]);
        miSebValarmV.setProgress(saveValue[AlarmSysV]);
//        miTxtPara.setText(String.valueOf(saveValue[AlarmMax])+" "+String.valueOf(saveValue[AlarmSysV]));
    }
    private void display_SebNS(){
//        chkcancel_Flag2 = true;
        Log.d(TAG,"b");
        miSebVnotiyV.setMax(saveValue[NotiyMax]);
        miSebVnotiyV.setProgress(saveValue[NotiySysV]);
    }
    private void display_SebAA(){
//        chkcancel_Flag2 = true;
        Log.d(TAG,"c");
        miSebValarmV1.setMax(saveValue[AlarmMax]);
        miSebValarmV1.setProgress(saveValue[AlarmAppV]);
    }
    private void display_SebNA(){
//        chkcancel_Flag2 = true;
        Log.d(TAG,"d");
        miSebVnotiyV1.setMax(saveValue[NotiyMax]);
        miSebVnotiyV1.setProgress(saveValue[NotiyAppV]);
    }

    private void display_AS(){
//        miSebValarmV.setMax(saveValue[AlarmMax]);
//        miTxtValarmV.setText(String.valueOf((int)(((double)saveValue[AlarmSysV]/(double)saveValue[AlarmMax])*100)));
        Log.d(TAG,String.valueOf(saveValue[AlarmSysV]));
        miTxtValarmV.setText(String.valueOf(saveValue[AlarmSysV])+"/"+String.valueOf(saveValue[AlarmMax]));
    }
    private void display_NS(){
//        miSebVnotiyV.setMax(saveValue[NotiyMax]);
//        miTxtVnotiyV.setText(String.valueOf((int)(((double)saveValue[NotiySysV]/(double)saveValue[NotiyMax])*100)));
        miTxtVnotiyV.setText(String.valueOf(saveValue[NotiySysV])+"/"+String.valueOf(saveValue[AlarmMax]));
    }
    private void display_AA(){
//        miSebValarmV1.setMax(saveValue[AlarmMax]);
//        miTxtValarmV1.setText(String.valueOf((int)(((double)saveValue[AlarmAppV]/(double)saveValue[AlarmMax])*100)));
        miTxtValarmV1.setText(String.valueOf(saveValue[AlarmAppV])+"/"+String.valueOf(saveValue[AlarmMax]));
    }
    private void display_NA(){
//        miSebVnotiyV1.setMax(saveValue[NotiyMax]);
//        miTxtVnotiyV1.setText(String.valueOf((int)(((double)saveValue[NotiyAppV]/(double)saveValue[NotiyMax])*100)));
        miTxtVnotiyV1.setText(String.valueOf(saveValue[NotiyAppV])+"/"+String.valueOf(saveValue[NotiyMax]));
    }


    private void setVoice(String type){
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (saveValue[SAFLAG] == 0){
            if (type == "AlarmApp"){
                mAudioManager.setStreamVolume(AudioManager.STREAM_RING,saveValue[AlarmAppV],AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            }else if (type == "NotiyApp") {
                mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,saveValue[NotiyAppV],AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            } else{
            }
        }else {
            if (type == "AlarmSys"){
                mAudioManager.setStreamVolume(AudioManager.STREAM_RING,saveValue[AlarmSysV],AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            }else if (type == "NotiySys") {
                mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,saveValue[NotiySysV],AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            } else{
            }
        }
    }



    private void setsound(int value,int Max){
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        double voice_level = ((double)value/(double)Max)*100;
        int ringerMode = mAudioManager.getRingerMode();
        if ((ringerMode == AudioManager.RINGER_MODE_SILENT) || (ringerMode == AudioManager.RINGER_MODE_VIBRATE)) {
            return;
        }
        ToneGenerator toneV = new ToneGenerator(STREAM_ALARM,((int)voice_level));
        toneV.stopTone();
        toneV.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, TONE_LENGTH_MS);
    }//setsound



    private void savepara(){
        for (int i=0; i<saveKey.length; i++) {
            SetPara(saveKey[i],saveValue[i]);
        }
    }
    private void loadpara(){
        for (int i=0; i<saveKey.length; i++) {
            saveValue[i] = GetPara(saveKey[i]);
//            Log.d(TAG,"loadpara"+":"+String.valueOf(saveValue[i]));
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

    /////////////////////////////////////////////////////////////////////////////////////
    public double PutPointN(double number,int bit){
        double rnumber =  (double)(Math.round(number*(Math.pow(10,bit))))/(Math.pow(10,bit));
        return rnumber;
    }
    /////////////////////////////////////////////////////////////////////////////////////


/////////////////////////////////////////////////////////////////////////////////////
    private void About(){
        //產生視窗物件
        miBtnMode2.setImageResource(R.drawable.p_mode_voice_0);
        miBtnMode4.setImageResource(R.drawable.p_mode_about_1);

        AlertDialog.Builder AboutDialog = new AlertDialog.Builder(voicesetting.this);
        AboutDialog.setTitle(R.string.about);//設定視窗標題
        AboutDialog.setIcon(R.mipmap.ic_tyredog);//設定對話視窗圖示
        AboutDialog.setCancelable(false);
//        AboutDialog.setMessage("\n       JOSN Electronic Co., Ltd.\n\n☆Tel: (02)2299-6900\n\n☆Mail: sales@josn.com.tw\n\n☆Web: www.josn.com.tw\n\n☆Address: 3F, No.12, Wugong 6th Rd., Xinzhuang Dist., New Taipei City, 242, Taiwan");//設定顯示的文字
        AboutDialog.setMessage(companyinfo());//設定顯示的文字        AboutDialog.setMessage("\n       JOSN Electronic Co., Ltd.\n\n☆Tel: (02)2299-6900\n\n☆Mail: sales@josn.com.tw\n\n☆Web: www.josn.com.tw\n\n☆Address: 3F, No.12, Wugong 6th Rd., Xinzhuang Dist., New Taipei City, 242, Taiwan");//設定顯示的文字
        AboutDialog.setPositiveButton(R.string.OK,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                        if (which == KeyEvent.KEYCODE_SEARCH)
//                        {
//                            return;
//                        }
                dialog.cancel();
                miBtnMode2.setImageResource(R.drawable.p_mode_voice_1);
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



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_RING,AudioManager.ADJUST_LOWER,AudioManager.FLAG_SHOW_UI);
            voice_value = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
            saveValue[AlarmSysV] = voice_value;
            SetPara(saveKey[AlarmSysV],saveValue[AlarmSysV]);
            display_SebAS();
            display_AS();
        }else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_RING,AudioManager.ADJUST_RAISE,AudioManager.FLAG_SHOW_UI);
            voice_value = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
            saveValue[AlarmSysV] = voice_value;
            SetPara(saveKey[AlarmSysV],saveValue[AlarmSysV]);
            display_SebAS();
            display_AS();
        }else {
        }

        return true;
    }



    private CheckBox.OnCheckedChangeListener chklistener = new CheckBox.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
        // TODO Auto-generated method stub
            if (buttonView.getText() == getString(R.string.voiceSystem)){
                if (chkcancel_Flag == false){
                    if (isChecked == true){
                        chkcancel_Flag = true;
                        michkBoxApp.setChecked(false);
                        miSebValarmV.setBackgroundResource(R.color.light_grey);
                        miSebVnotiyV.setBackgroundResource(R.color.light_grey);
                        miSebValarmV1.setBackgroundResource(R.color.grey);
                        miSebVnotiyV1.setBackgroundResource(R.color.grey);
                        saveValue[SAFLAG] = 1;
                        SetPara(saveKey[SAFLAG],saveValue[SAFLAG]);
                        SetPara(saveKey[SETVFLAG],1);
    //                        Log.d(TAG, "A-ON");
                    }else{
                        michkBoxSystem.setChecked(true);
//                        Log.d(TAG, "A-off");
                    }
                }else {
                    chkcancel_Flag = false;
//                    Log.d(TAG, "A-----");
                }
            }else if (buttonView.getText() == getString(R.string.voiceApp)){
                if (chkcancel_Flag == false){
                    if (isChecked == true){
                        chkcancel_Flag = true;
                        michkBoxSystem.setChecked(false);
                        miSebValarmV.setBackgroundResource(R.color.grey);
                        miSebVnotiyV.setBackgroundResource(R.color.grey);
                        miSebValarmV1.setBackgroundResource(R.color.light_grey);
                        miSebVnotiyV1.setBackgroundResource(R.color.light_grey);
                        saveValue[SAFLAG] = 0;
                        SetPara(saveKey[SAFLAG],saveValue[SAFLAG]);
                        SetPara(saveKey[SETVFLAG],1);
//                        Log.d(TAG, "B-ON");
                    }else{
                        michkBoxApp.setChecked(true);
//                        Log.d(TAG, "B-Off");
                    }
                }else{
                    chkcancel_Flag = false;
//                    Log.d(TAG, "B-----");
                }
            }else if (buttonView.getText() == getString(R.string.closealarm)){
                if (chkcancel_Flag == false){
                    if (isChecked == true){
                        saveValue[AlarmFlag] = 0;
                    }else{
                        saveValue[AlarmFlag] = 1;
                    }
                    SetPara(saveKey[AlarmFlag],saveValue[AlarmFlag]);
                    SetPara(saveKey[SETVFLAG],1);
                }else {
                }
            }else if (buttonView.getText() == getString(R.string.closenotiy)){
                if (chkcancel_Flag == false){
                    if (isChecked == true){
                        saveValue[NotiyFlag] = 0;
                    }else{
                        saveValue[NotiyFlag] = 1;
                    }
                    SetPara(saveKey[NotiyFlag],saveValue[NotiyFlag]);
                    SetPara(saveKey[SETVFLAG],1);
                }else{

                }

            }else{
            }
            miTxtPara.setText(buttonView.getText()+":"+String.valueOf(isChecked));
        }
    };

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
    private View.OnClickListener iBtnMode1Onclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };
    private View.OnClickListener iBtnMode2Onclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };
    private View.OnClickListener iBtnMode3Onclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(voicesetting.this,blesetting.class);
            startActivity(i);//開始跳往要去的Activity
            finish();
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



    private View.OnClickListener iBtnCloseOnclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            savepara();
//            System.exit(0);
            SetPara(saveKey[CLOSEV],1);
            SetPara(saveKey[SETVFLAG],1);
            finish();
        }
    };

    private View.OnClickListener iBtnReturnOnclick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };


}

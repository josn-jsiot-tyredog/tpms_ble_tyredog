package com.sheng.tpms;

/**
 * Created by sheng on 2018/7/18.
 */


public class Config_tpms {

    public boolean josn_config = false;

//wellcomeMain
    public boolean SHOW_WELLCOME = true;       //show wellcome icon


//blescreenMain
    public boolean SHOW_LOG = false;
    public boolean DEBUG_MODE = false;
    public boolean SHOW_PROTOCOL = false;

    public boolean listview_only = false;       //always show listview
    public boolean wheel4screen = false;        //Always first show 4car icon when repower on
    public boolean chk_module_FLAG = true;      //with setwheel_FLAG
    public int wheelN_k = 4;                    //set init wheel_N

    public boolean load_saveWHEELN = true;      //load old wheel_N para when repower on


//bleseeting
    public boolean setwheel_FLAG = false;      //with chk_module_FLAG
    public boolean SHOW_loadpara = false;
}

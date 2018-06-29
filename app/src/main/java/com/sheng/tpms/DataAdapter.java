package com.sheng.tpms;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by sheng on 2018/6/15.
 */


public class DataAdapter extends BaseAdapter {


//    private LayoutInflater myInflater;


    private int press_iconl = R.drawable.btire_0d;
    private int temp_iconl = R.drawable.l_sign_temp_1r;

    private int rx_ok_icon = R.drawable.l_sign_rx_3_g;
    private int rx_no_icon = R.drawable.l_sign_rx_n;
    private int bt_ok_icon = R.drawable.l_sign_bt_n;
    private int bt_low_icon = R.drawable.l_sign_bt_1;
    private int status_ok_icon = R.drawable.l_open_sign1_0;
    private int status_ng_icon = R.drawable.l_open_sign1_2;



    public int mWheelN;
    public int[] mWheel;
//    public String[] mSubWords;

    public double[] mPress;
    public int[] mTemp;
    public int[] mIcons;
    public boolean[] mRxflag;
    public boolean[] mBtflag;
    public boolean[] mPressflag;
    public boolean[] mTempflag;
    public boolean[] mStatusflag;


    public DataAdapter(int[] wheel, boolean[] rxflag, double[] pvalue, int[] tvalue, boolean[] btflag, int[] icons) {
//        myInflater = LayoutInflater.from(context);
        mWheel = wheel;
//        mSubWords = subwords;
        mIcons = icons;
        mPress = pvalue;
        mTemp = tvalue;
        mRxflag = rxflag;
        mBtflag = btflag;
    }
    public DataAdapter(int wheelN, int[] wheel, boolean[] rxflag, double[] pvalue, int[] tvalue, boolean[] btflag, boolean[] statusflag, boolean[] pressflag, boolean[] tempflag) {
//        myInflater = LayoutInflater.from(context);
        mWheelN = wheelN;
        mWheel = wheel;
//        mSubWords = subwords;
        mStatusflag = statusflag;
        mPress = pvalue;
        mTemp = tvalue;
        mRxflag = rxflag;
        mPressflag = pressflag;
        mTempflag = tempflag;
        mBtflag = btflag;
    }



    @Override
    public int getCount() {
//        return mWheel.length;
        return mWheelN;
    }

    @Override
    public Object getItem(int position) {
        return mWheel[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemdata, null);
//            convertView = myInflater.inflate(R.layout.itemdata, null);
        }

        // 找到TextView
        TextView txtwheel = (TextView) convertView.findViewById(R.id.Txt_Wheel);
//        TextView subTitle = (TextView) convertView.findViewById(R.id.Txt_Subwords);
        ImageView imgRx = (ImageView) convertView.findViewById(R.id.Img_Rx);
        ImageView imgStatus = (ImageView) convertView.findViewById(R.id.Img_ststus);
        ImageView imgpress = (ImageView) convertView.findViewById(R.id.Img_Press);
        ImageView imgtemp = (ImageView) convertView.findViewById(R.id.Img_Temp);
        ImageView imgbt = (ImageView) convertView.findViewById(R.id.Img_Bt);
        TextView txtpress = (TextView) convertView.findViewById(R.id.Txt_Press);
        TextView txttemp = (TextView) convertView.findViewById(R.id.Txt_Temp);

        txtwheel.setText("W " + String.valueOf(mWheel[position]));
//        subTitle.setText(String.valueOf(mSubWords[position]));
        txtpress.setText(String.valueOf(mPress[position]));
        txttemp.setText(String.valueOf(mTemp[position]));

        // 依照位置算出對應的圖片
//        int resId = mIcons[position % mIcons.length];
        int resId;

        resId = press_iconl;
        imgpress.setImageResource(resId);
        resId = temp_iconl;
        imgtemp.setImageResource(resId);


        if (mRxflag[position] == true) {
            resId = rx_ok_icon;
        } else {
            resId = rx_no_icon;
        }
        imgRx.setImageResource(resId);

        if (mStatusflag[position] == true) {
            resId = status_ng_icon;
        } else {
            resId = status_ok_icon;
        }
        imgStatus.setImageResource(resId);

//        resId = mBt[position % mIcons.length];
        if (mBtflag[position] == true) {
            resId = bt_low_icon;
        } else {
            resId = bt_ok_icon;
        }
        imgbt.setImageResource(resId);


        if (mPressflag[position] == true) {
            txtpress.setTextColor(Color.parseColor("#ff0005"));
        } else {
            txtpress.setTextColor(Color.parseColor("#ffffff"));
        }
        if (mTempflag[position] == true) {
            txttemp.setTextColor(Color.parseColor("#ff0005"));
        } else {
            txttemp.setTextColor(Color.parseColor("#ffffff"));
        }



        return convertView;
    }
}
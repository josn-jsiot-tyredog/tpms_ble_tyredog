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

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class blescan extends ListActivity {

    int oldOrientation = -1;


    private final static String TAG = DeviceScanActivity.class.getSimpleName();
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;

    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;


    private String BLE_name[]=new String[]{"TYREDOG","JDY-08","Realtek","RB8762"};
    private String BLE_ID = "JDY-08";
//    private String BLE_ID = "TYREDOG";
    private int BLE_SCAN_CNT = 0;
    private int BLE_SCAN_CNT_K = 3;
    private int BLE_SCAN_TIMER_k = 6000;


    private Handler handler_BLE_SCAN = new Handler(); //BLE_SCAN_TIMER





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getActionBar().setTitle(R.string.title_devices);
        mHandler = new Handler();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }



        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        Toast.makeText(blescan.this, R.string.wellcome, Toast.LENGTH_LONG).show();
        handler_BLE_SCAN.removeCallbacks(BLE_SCAN_TIMER);
        handler_BLE_SCAN.postDelayed(BLE_SCAN_TIMER, BLE_SCAN_TIMER_k);

    }             //開頭


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // TODO request success
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {                     //顯示更新標題列
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }               //標題列

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                mLeDeviceListAdapter.clear();
                scanLeDevice(true);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
        }
        return true;
    }



    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
    }


    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        setListAdapter(mLeDeviceListAdapter);
        scanLeDevice(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {                 //list 選擇觸發
//        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);                //讀列表內的
//        if (device == null) return;
//        final Intent intent = new Intent(this, DeviceControlActivity.class);                    //跳轉並傳遞資料(name&address)
//        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());        //傳遞device name
//        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());  //傳遞device address
//        if (mScanning) {                                                                        //此時可停止搜尋
//            mBluetoothAdapter.stopLeScan(mLeScanCallback);
//            mScanning = false;
//        }
//        startActivity(intent);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {               //放一個DELAY，時間到後關閉搜尋
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;                                   //開啟搜尋參數
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;                                  //關閉搜尋參數
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();                                //搜尋始能
    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = blescan.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            viewHolder.deviceAddress.setText(device.getAddress());

            return view;
        }            //ListView產生
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =                       //查詢更新device
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {                                              //可由此修改綁定device
                            mLeDeviceListAdapter.addDevice(device);                         //更新device name
                            mLeDeviceListAdapter.notifyDataSetChanged();                    //刷新listview
//                    Toast.makeText(DeviceScanActivity.this, "Test2", Toast.LENGTH_SHORT).show();


                            final String deviceNamec = device.getName();
                            if (deviceNamec != null && deviceNamec.length() > 0){
//                        Log.d(TAG, deviceNamec);
                                if (checkdevicename(deviceNamec)){
//                                if (deviceNamec.indexOf(BLE_ID)>-1){
                                    Log.d(TAG, "Connect:"+deviceNamec);
                                    final Intent intent = new Intent(blescan.this, blescreenMain.class);   //跳轉並傳遞資料(name&address)
                                    intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());        //傳遞device name
                                    intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());  //傳遞device address
                                    intent.putExtra("GetDevice",true);                                       //
                                    if (mScanning) {                                                                        //此時可停止搜尋
                                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                                        mScanning = false;
                                    }
                                    handler_BLE_SCAN.removeCallbacks(BLE_SCAN_TIMER);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    Log.d(TAG, deviceNamec);
                                }
                            }

                        }
                    });
                }
            };

    static class ViewHolder {                   //新增變數
        TextView deviceName;
        TextView deviceAddress;
    }


    private boolean checkdevicename(String deviceNamec){
        boolean name_FLAG = false;
        for (int i=0; i<BLE_name.length; i++){
            if (deviceNamec.indexOf(BLE_name[i]) > -1){
                name_FLAG = true;
            }
        }
        return name_FLAG;
    }




    private Runnable BLE_SCAN_TIMER = new Runnable() {
        public void run() {

            if (BLE_SCAN_CNT >= BLE_SCAN_CNT_K){
                BLE_SCAN_CNT = 0;
                handler_BLE_SCAN.removeCallbacks(BLE_SCAN_TIMER);
                CheckScan();
            }else {
                BLE_SCAN_CNT = BLE_SCAN_CNT + 1;
                Toast.makeText(blescan.this, R.string.module_power, Toast.LENGTH_SHORT).show();
                handler_BLE_SCAN.postDelayed(BLE_SCAN_TIMER, BLE_SCAN_TIMER_k);
                scanLeDevice(true);
            }

        }
    };




    private void CheckScan(){
        //產生視窗物件
        AlertDialog.Builder ScanDialog = new AlertDialog.Builder(blescan.this);
        ScanDialog.setTitle(R.string.scanerror);//設定視窗標題
        ScanDialog.setIcon(R.mipmap.ic_tyredog);//設定對話視窗圖示
        ScanDialog.setCancelable(false);
        ScanDialog.setMessage(R.string.checkscan);//設定顯示的文字
        ScanDialog.setPositiveButton(R.string.contiune,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(blescan.this, R.string.module_power, Toast.LENGTH_SHORT).show();
                handler_BLE_SCAN.postDelayed(BLE_SCAN_TIMER, BLE_SCAN_TIMER_k);
                scanLeDevice(true);
                dialog.cancel();
            }
        });//設定結束的子視窗
        ScanDialog.setNeutralButton(R.string.close,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });//設定結束的子視窗
        ScanDialog.setNegativeButton(R.string.Cancel,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                handler_BLE_SCAN.removeCallbacks(BLE_SCAN_TIMER);
                if (mScanning) {                                                                        //此時可停止搜尋
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    mScanning = false;
                }
                final Intent intent = new Intent(blescan.this, blescreenMain.class);    //跳轉並傳遞資料(name&address)
                intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME,"ERROR");        //傳遞device name
                intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS,"ERROR");  //傳遞device address
                intent.putExtra("GetDevice",false);                                     //

                startActivity(intent);
                finish();
            }
        });//設定結束的子視窗
        ScanDialog.show();//呈現對話視窗
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵

            handler_BLE_SCAN.removeCallbacks(BLE_SCAN_TIMER);
            new AlertDialog.Builder(blescan.this)
                    .setTitle(R.string.LOGO)
                    .setMessage(R.string.closeapp)
                    .setIcon(R.mipmap.ic_tyredog)
                    .setPositiveButton(R.string.OK,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    handler_BLE_SCAN.removeCallbacks(BLE_SCAN_TIMER);
                                    finish();
                                }
                            })
                    .setNegativeButton(R.string.Cancel,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub
                                    handler_BLE_SCAN.postDelayed(BLE_SCAN_TIMER, BLE_SCAN_TIMER_k);
                                }
                            }).show();
        }else {
        }
        return true;
    }


//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//
//        //檢測修改字型大小設定
//        if (newConfig.fontScale != 1) getResources();
//
//        // 检测屏幕的方向：纵向或横向
//        if(oldOrientation != newConfig.orientation)
//        {
//            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
//            {
////                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
////                Log.d(TAG, "ORIENTATION_LANDSCAPE");
//            }else {
////                setContentView(R.layout.port_activiyt_blescreenmain);
////                Log.d(TAG, "ORIENTATION_PORTRAIT");
//            }
//            oldOrientation = newConfig.orientation;
//        }
//    }


}
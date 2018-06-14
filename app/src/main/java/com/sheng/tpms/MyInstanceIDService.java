package com.sheng.tpms;

/**
 * Created by sheng on 2017/12/4.
 */


import com.google.firebase.iid.FirebaseInstanceIdService;


public class MyInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
//        String token = FirebaseInstanceId.getInstance().getToken();
        blescreenMain.registerPush();
//        Log.d("FCM", "Token:"+token);
    }

}



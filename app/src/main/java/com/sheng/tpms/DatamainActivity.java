package com.sheng.tpms;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class DatamainActivity extends AppCompatActivity {

    public int wheelN = 4;
    public int[] wheel = {1,2,3,4,5,6,7,8,9,10,11,12,13} ;
    public String[] subwords = {"A","B","C","D","E","A","B","C","D","E","C","D","E"} ;
    public double[] pvalue = {32.0,31.5,32.0,33.0,32.0,32.0,31.5,32.0,33.0,32.0,32.0,33.0,32.0} ;
    public int[] tvalue = {25,25,26,27,24,25,25,26,27,24,26,27,24} ;
    public boolean[] rxflag = {false,true,true,false,true,true,false,true,true,false,true,true,false} ;
    public boolean[] btflag = {true,true,true,false,true,true,false,false,true,true,true,true,false} ;
    public int[] icons = {R.drawable.l_open_sign1_1,R.drawable.l_open_sign1_0,R.drawable.l_open_sign1_1,R.drawable.l_open_sign1_1,R.drawable.l_open_sign1_0,R.drawable.l_open_sign1_1,R.drawable.l_open_sign1_0,R.drawable.l_open_sign1_1,R.drawable.l_open_sign1_1,R.drawable.l_open_sign1_0,R.drawable.l_open_sign1_1,R.drawable.l_open_sign1_1,R.drawable.l_open_sign1_0} ;
    public boolean[] pressflag = {true,true,true,false,true,true,false,false,true,true,true,true,false} ;
    public boolean[] tempflag = {true,true,true,false,true,true,false,false,true,true,true,true,false} ;
    public boolean[] statusflag = {true,true,true,false,true,true,false,false,true,true,true,true,false} ;


    Context context_view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datamain);


//        ListView dataview = (ListView) findViewById(R.id.listview);
//        ListView_Customer(dataview);
    }


    // 客製化ListView
    public void ListView_Customer(ListView dataview, Context context) {
        this.context_view = context;
        DataAdapter adapter = new DataAdapter(wheelN, wheel, rxflag, pvalue, tvalue, btflag, statusflag, pressflag, tempflag);
//        ListView dataview = (ListView) findViewById(R.id.listview);
        dataview.setAdapter(adapter);

        dataview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
//                TextView txv = (TextView) view.findViewById(R.id.Wheel);
//                Toast.makeText(Main2Activity.this, "你點擊了:" + txv.getText(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(DatamainActivity.this,"點選第 "+(position +1) +" 個 \n內容："+wheel[position], Toast.LENGTH_SHORT).show();
//                Toast.makeText(context_view,"點選第 "+(position +1) +" 個 \n內容："+wheel[position], Toast.LENGTH_SHORT).show();
            }
        });
    }



}

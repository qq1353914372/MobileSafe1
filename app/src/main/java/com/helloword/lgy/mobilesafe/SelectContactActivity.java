package com.helloword.lgy.mobilesafe;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectContactActivity extends AppCompatActivity {
    private ListView lv_select_contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);
        lv_select_contact= (ListView) findViewById(R.id.lv_select_contact);
        //初始化数据
        final List<Map<String,String>> data=getContactInfo();
        //显示数据
        lv_select_contact.setAdapter(new SimpleAdapter(this,data,R.layout.contect_item,
                new String[]{"name","phone"},new int[]{R.id.tv_name,R.id.tv_phone}));
        //点击事件
        lv_select_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               String phone= data.get(position).get("phone").replace("-","");
                Intent intent=new Intent();
                intent.putExtra("phone",phone);
                setResult(0,intent);
                finish();
            }
        });
    }
    public List<Map<String, String>> getContactInfo() {
        List<Map<String,String>> list=new ArrayList<Map<String, String>>();
        ContentResolver resolver= getContentResolver();
        Uri uri=Uri.parse("content://com.android.contacts/raw_contacts");
        Uri dataUri=Uri.parse("content://com.android.contacts/data");
        Cursor cursor= resolver.query(uri,new String[]{"contact_id"},null,null,null);
        while (cursor.moveToNext()){
            String contact_id=cursor.getString(0);
            if (contact_id!=null){
                //具体某一个人
                Map<String,String> map=new HashMap<>();
                Cursor  datacursor=resolver.query(dataUri,new String[]{"data1","mimetype"},
                        "contact_id=?",new String[]{contact_id},null);
                while (datacursor.moveToNext()){
                    String data1=datacursor.getString(0);
                    String mimetype=datacursor.getString(1);
//                    System.out.println("data1="+data1+"---------mimetype="+mimetype);
                    if ("vnd.android.cursor.item/phone_v2".equals(mimetype)){//电话
                        String phone=datacursor.getString(0);
                        System.out.println("phone="+phone);
                        map.put("phone",data1);

                    }else if ("vnd.android.cursor.item/name".equals(mimetype)){//姓名
                        String name=datacursor.getString(0);
                        System.out.println("name="+name);
                        map.put("name",data1);
                    }

                }
                datacursor.close();
                list.add(map);

            }
        }
        cursor.close();

        return list;
    }
}

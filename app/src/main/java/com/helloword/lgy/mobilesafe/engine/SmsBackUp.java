package com.helloword.lgy.mobilesafe.engine;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by hasee on 2017/10/31.
 */

public class SmsBackUp {//备份短信

    public static void backUp(Context context, String path, CallBack callBack){
        FileOutputStream fos=null;
        Cursor cursor=null;
        int index=0;
        File file=new File(path);
        try {
        //拿到内容解析者
            cursor= context.getContentResolver().query( Uri.parse("content://sms/"),
                new String[]{"address","date","type","body"},null,null,null);
        //文件输出流
            fos=new FileOutputStream(file);
            //序列化数据，设置xml参数
         XmlSerializer serializer= Xml.newSerializer();
            serializer.setOutput(fos,"utf-8");
            serializer.startDocument("utf-8",true);
            serializer.startTag(null,"smss");
            //设置进度条最大值
            callBack.setMax(cursor.getCount());
            while (cursor.moveToNext()){
                serializer.startTag(null,"sms");

                serializer.startTag(null,"address");
                serializer.text(cursor.getString(0));
                serializer.endTag(null,"address");

                serializer.startTag(null,"date");
                serializer.text(cursor.getString(1));
                serializer.endTag(null,"date");

                serializer.startTag(null,"type");
                serializer.text(cursor.getString(2));
                serializer.endTag(null,"type");

                serializer.startTag(null,"body");
                serializer.text(cursor.getString(3));
                serializer.endTag(null,"body");

                serializer.endTag(null,"sms");
                index++;
                if (index<100){

                    Thread.sleep(100);
                }
                //更新进度条
                callBack.setProgress(index);


            }
            serializer.endTag(null,"smss");
            serializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //关闭流，游标
            if (fos!=null&&cursor!=null){
                try {
                    fos.close();
                    cursor.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }
    public interface CallBack{
         void setMax(int max);
         void setProgress(int index);

    }
}

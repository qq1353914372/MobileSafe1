package com.helloword.lgy.mobilesafe.com.helloword.lgy.mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by hasee on 2017/10/1.
 */

public  class SteamToos {
    public static String readStram(InputStream is) throws IOException {
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        byte[] buffer=new byte[1024];
        int len=0;
        while ((len=is.read(buffer))!=-1){
            baos.write(buffer,0,len);
        }
        is.close();
        String result=baos.toString();
        baos.close();
        return  result;

    }
}

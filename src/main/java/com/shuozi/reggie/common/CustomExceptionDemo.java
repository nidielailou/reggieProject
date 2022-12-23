package com.shuozi.reggie.common;

import java.io.IOException;

public class CustomExceptionDemo extends IOException
{

    @Override
    public void printStackTrace() {
        super.printStackTrace();
        System.out.println("这个文件找不到了");
    }
}

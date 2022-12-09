package com.shuozi.reggie.common;

/**
 * 由于filter编写和controller和公共字段填充是一个线程  所以可以使用 threadlocal 把http的员工id填写到公共填充字段中
 */
public class BaseContext {

    private static  ThreadLocal<Long> thread = new ThreadLocal<>();

    public static void setCurrentLocal(Long l)
    {
        thread.set(l);
    }

    public static Long getCurrentLocal()
    {
        return thread.get();
    }
}

package com.sharemeng.laputa.monitor.alert;

import java.util.concurrent.TimeUnit;

/**
 * Created by Tony on 2017/10/10.
 */
public class Demo {
    public static void main(String arsg[]) {
        Info<String> obj = new InfoImp("");
        System.out.println("Length Of String: " + obj.getVar().length());
        System.out.println("String value: " + obj.getVar());

        Info<Integer> obj2 = new InfoImp<Integer>(23);
        System.out.println(obj2.getVar());

        System.out.println(TimeUnit.HOURS.name());

    }
}

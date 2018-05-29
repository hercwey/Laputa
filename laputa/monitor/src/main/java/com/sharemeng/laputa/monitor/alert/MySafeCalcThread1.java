package com.sharemeng.laputa.monitor.alert;

/**
 * Created by Tony on 2017/10/10.
 */

import java.util.concurrent.atomic.AtomicInteger;


public class MySafeCalcThread1 implements Runnable {

    private static AtomicInteger count = new AtomicInteger(0);

    public synchronized static void calc() {
        if ((count.get()) < 100) {
            int c = count.incrementAndGet();// 自增1,返回更新值
            System.out.println("正在运行是线程" + Thread.currentThread().getName() + ":" + c);
        }
    }

    public void run() {
        while (true) {
            MySafeCalcThread1.calc();
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        for (int i = 0; i < 4; i++) {
            MySafeCalcThread1 thread = new MySafeCalcThread1();
            Thread t = new Thread(thread);
            t.start();
        }
    }
}


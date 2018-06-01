package com.sharemeng.lapuda.concurrent.futuretask;

import java.util.concurrent.Callable;

/**
 * @author Paul 2018/5/30
 */
public class MyCallable implements Callable<String> {

    private long waitTime;

    public MyCallable(int timeInMillis) {
        this.waitTime = timeInMillis;
    }

    @Override
    public String call() throws Exception {
        Thread.sleep(waitTime);
        //返回执行该任务的线程名
        return Thread.currentThread().getName();
    }
}

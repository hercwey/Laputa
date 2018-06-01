package com.sharemeng.lapuda.concurrent.futuretask;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * FutureTask实例：FutureTask 是Future接口的基本的具体实现，并提供异步处理。它包含start和cancel一个任务的方法，也能
 * 返回FutureTask的状态（不管是完成还是取消）。我们需要一个callable对象来创建一个future task，然后我们可以使用Thread
 * Pool Executor来异步地处理。
 *
 * Tutorial: https://www.journaldev.com/1650/java-futuretask-example-program
 * @author Paul 2018/5/30
 */
public class FutureTaskExample {
    public static void main(String[] args) {

        MyCallable callable1 = new MyCallable(1000);
        MyCallable callable2 = new MyCallable(3000);

        FutureTask<String> futureTask1 = new FutureTask<String>(callable1);
        FutureTask<String> futureTask2 = new FutureTask<String>(callable2);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(futureTask1);
        executor.execute(futureTask2);

        while (true) {
            try {
                if (futureTask1.isDone() && futureTask2.isDone()) {
                    System.out.println("完成了！");
                    //  关闭executor服务
                    executor.shutdown();
                    return;
                }

                if (!futureTask1.isDone()) {
                    // 无限期等待future task 完成
                    System.out.println("FutureTask1 output=" + futureTask1.get());
                }

                System.out.println("等待FutureTask2完成");
                String s = futureTask2.get(200L, TimeUnit.MILLISECONDS);
                if (s != null) {
                    System.out.println("FutureTask2 output=" + s);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                //do nothing
            }
        }

    }

}

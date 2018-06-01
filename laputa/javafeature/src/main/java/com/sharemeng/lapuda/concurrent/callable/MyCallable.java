package com.sharemeng.lapuda.concurrent.callable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Callable结构使用泛型来定义对象的返回类型。Executors类型提供了有用的方法在线程池中执行Java Callable。因为callable
 * 任务并行运行，我们必须等待该返回对象(java.util.concurrent.Future)。
 * 通过Java Future对象，我们可以得到Callable任务的状态和返回的对象。它提供了get()方法等待Callable完成然后返回结果。
 *
 * Future 提供了cancel()方法来取消关联的Callable 任务。重载的get()方法可以指定时间来等待结果，它避免当前线程长时间阻塞。
 * isDone()和isCancelled()方法用于查询关联callable任务的当前状态。
 *
 * 下面是个Callable任务实例，它一秒后返回执行任务的线程名。我使用Executor框架来并行地执行50个任务，并使用Java Future
 * 来获取提交的任务的结果。
 *
 * Tutorial: https://www.journaldev.com/1090/java-callable-future-example
 * @author Paul 2018/5/29
 */
public class MyCallable implements Callable<String> {

    @Override
    public String call() throws Exception {
        Thread.sleep(5000);
        //返回执行该Callalbe任务的线程名
        return Thread.currentThread().getName();
    }

    public static void main(String args[]) {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        // 创建一个list来容纳关联Callable的Future对象
        List<Future<String>> list = new ArrayList<>();
        // 创建MyCallable实例
        Callable<String> callable = new MyCallable();
        for (int i = 0; i < 50; i++) {
            // 提交Callable任务到线程池取执行
            Future<String> future = executor.submit(callable);
            // 添加Future到list，我们可以用Future得到返回值
            list.add(future);
        }
        for (Future<String> fut : list) {
            try {
                // 打印Future的返回值，注意控制台的输出delay
                // 因为Future.get() 等待task 执行完成
                System.out.println(new Date() + "::" + fut.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        // 关闭executor服务
        executor.shutdown();
    }
}

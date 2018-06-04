package com.sharemeng.laputa.redis.mq2;

/**
 * <p>Title: TaskShedulerSystem</p>
 * <p>Description: </p>
 * <p>Company: </p>
 * @author Paul
 */
public class TaskShedulerSystem {
    public static void main(String[] args) throws Exception {

        // 启动一个生产者线程，模拟任务的产生
        new Thread(new TaskProducer()).start();

        Thread.sleep(15000);

        //启动一个线程者线程，模拟任务的处理
        new Thread(new TaskConsumer()).start();

        //主线程休眠
        Thread.sleep(Long.MAX_VALUE);
    }
}

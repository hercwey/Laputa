package com.sharemeng.laputa.redis.mq2;

/**
 * Tutorial： https://blog.csdn.net/snakemoving/article/details/78194544
 * <p>Title: TaskShedulerSystem</p>
 * <p>Description: </p>
 * <p>Company: </p>
 * @author Paul
 */
public class TaskShedulerSystem {
    public static void main(String[] args) throws Exception {

        // 启动一个生产者线程，模拟任务的产生
        new Thread(new TaskProducer()).start();

        Thread.sleep(150000);

        //启动5个消费者线程，模拟任务的并发处理
        for (int i=0; i<5; i++) {
            new Thread(new TaskConsumer()).start();
        }

        //主线程休眠
        Thread.sleep(Long.MAX_VALUE);
    }
}

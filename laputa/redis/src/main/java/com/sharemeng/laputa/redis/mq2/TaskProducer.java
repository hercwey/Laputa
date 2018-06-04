package com.sharemeng.laputa.redis.mq2;

import java.util.Random;
import java.util.UUID;

import redis.clients.jedis.Jedis;

/**
 * Tutorial： https://blog.csdn.net/snakemoving/article/details/78194544
 * 模拟一个生产者
 * <p>Title: TaskProducer</p>
 * <p>Description: </p>
 * <p>Company: </p>
 * @author Paul
 */
public class TaskProducer implements Runnable {
    Jedis jedis = new Jedis("192.168.226.134", 6379);

    public void run() {
        Random random = new Random();
        int count = 0;
        while (true) {
            try {
                Thread.sleep(random.nextInt(600) + 100);
                // 模拟生成一个任务
                UUID taskid = UUID.randomUUID();
                //将任务插入任务队列：task-queue
                //jedis.lpush("task-queue", taskid.toString());
                jedis.lpush("task-queue", taskid.toString());
                System.out.println("插入了一个新的任务： " + taskid);
                count++;
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (count == 200) { break; } //只生产20个任务
        }
    }

}

package com.sharemeng.laputa.redis.queue;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Redis队列实例: 问题场景是我们有一个队列数据结构，应用的生产者模块将添加一些任务到队列来执行。另一方面，一个消费者
 * 模块将从队列中获取任务来执行。
 * 消费者模块可以自治地决定什么时候来消费当前队列中的任务。为了简便，我们假设，有6个应用运行实例，有6个生产者模块发送数据
 * 到队列中，并且有6个消费者模块争夺队列中的数据。
 *
 * 这里很重要的一点是事务隔离。这意味着2个隔离的消费者不能从队列中消费同一个任务。Redis的一个特性是一个任务不能离开
 * Redis的控制，除非它已经完成了。所以这种情况，Redis 传送该任务到另一个队列，让我们把它叫着“work”或“processing”队列。
 * 每个任务在交给应用之前将被推迟到这个工作队列。在任务的工作期间，消费者模块的冻结或崩溃事件中，Redis将知道一个“hanging”
 * 任务在“work”队列中存在了较长一段时间，意味着任务将不会再一个冻结的应用内部丢失，而是返回到一个等待队列来被其他消费者
 * 实例再次消费。
 *
 * 所以在这个情况下，我们有2个队列，q1和q2. 生产者把任务放进q1，然后消费者自动从q1弹出一个元素并放入q2. 当消费者完成处理后，
 * 该消息将从q2中删除。q2用于从失败中恢复（网络问题或者消费者崩溃）。如果消息包含了一个时间戳，那么它们的寿命可以被测量，
 * 并且假如它们在q2中呆的太久，它们可以被传回q1，被一个消费者重新再次处理。
 *
 * 首先，我们将建一个构建通道（channel）适配器类，作为应用实例和Redis服务的沟通点。该类将暴露包含如下这些功能的方法：
 * > 发送任务到等待队列
 * > 检查队列中可得到的任务
 * > 从work队列返回任务到wait队列
 * > 从work 队列中移除任务
 * > 获得work队列中的任务列表
 *
 *
 * 发送任务到等待队列：当生产者模块有一个任务准备去工作，它将被委派给一个等待队列，这里我们使用Redis的LPUSH（left push）命令。
 *
 * Redis的LPOPRPUSH 将试着从等待队列取得一个元素，并传送到工作队列，它们在单个事务中完成，所以在处理过程中不会有消息丢失，
 * 它要么留在等对队列中（任务问题出现时）要么成功完成事务将任务对象传送到工作队列，并返回到一个消费者应用模块。
 *
 * 获取进行中的任务：这里使用Redis的LRANGE方法，它返回指定通道的N个元素。我们需要传一个开始元素。开始元素明显是0（list的第一个元素）
 * ，而结束元素设置为-1，我们设置为-1，Redis将知道我们想要队列中的所有元素。该方法的其余部分是实例化一个ArrayList对象（
 * 其初始空间设置为来自Redis驱动程序的列表大小）以及一个for循环，将转换后的消息填充到批处理对象中。
 *
 * 总结：使用Redis可以方便地在应用实例之间实现数据交换，以便所有实例共享同一内存空间。
 *
 * Tutorial: https://blog.rapid7.com/2016/05/04/queuing-tasks-with-redis/
 * https://segmentfault.com/a/1190000012244418
 * https://medium.com/@weyoss/building-a-simple-message-queue-using-redis-server-and-node-js-964eda240a2a
 * @author Paul 2018/5/31
 */
public class ChannelAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelAdapter.class);

    private final String waitQueue;
    private final String workQueue;

    private final JedisPool jedisPool;

    /**
     * instantiates ChannelAdapter, in charge of communication with messaging
     * service.
     *
     * @param jedisPool
     *            - provider of Jedis connections, if null means let
     *            ChannelAdapter create one by itself using data from AppConfig
     */
    public ChannelAdapter(JedisPool jedisPool) {

        this.jedisPool = jedisPool;

        this.waitQueue = "TestSample:waitQueue";
        this.workQueue = "TestSample:workQueue";
    }

    /* implementation of conversion Batch object into json presentation */
    private String batchToJson(Batch job) {
        return job.toString();
    }

    /* implementation of conversion json into Batch presentation */
    private Batch jsonToBatch(String batchJson) {
        return null;// new Batch(batchJson);
    }

    /**
     * sends batch job to a queue for further processing.
     *
     * @param job
     *            task that will be serialized and sent to queue
     * @return true if job has been successfully queued
     */
    public boolean sendJobToWaitQueue(Batch job) {

        LOGGER.debug("Trying to push job to queue: " + job.toString());

        String jobJson = batchToJson(job);

        Jedis instance = null;

        try {
            instance = this.jedisPool.getResource();

            // left push to a wait queue
            instance.lpush(waitQueue, jobJson);

            LOGGER.debug("Job successfully published to channel {} {}", waitQueue, jobJson);

            return true;
        } catch (Exception e) {
            LOGGER.error("Problem while publishing message to a channel", e);
            return false;
        } finally {
            instance.close();
        }
    }

    /**
     * checks if there is job available waiting in a 'wait' queue. If there is
     * job waiting in a queue, it will be transfered into 'work' queue and
     * returned back.
     *
     * @return Batch if available for work, otherwise null
     */
    public Batch checkIfJobAvailable() {

        String jobJson = null;
        Jedis instance = null;
        Batch job = null;

        try {
            instance = this.jedisPool.getResource();

            // trying to pick up new job from 'wait' queue and transfer it to
            // 'work' queue in single transaction
            String message = instance.rpoplpush(waitQueue, workQueue);

            if (message == null) {
                return null;
            }

            job = jsonToBatch(message);

            if (job == null) {
                return job;
            }

            LOGGER.debug("Job successfully transferred to 'work' queue:{} json:{}", workQueue, jobJson);

            return job;
        } catch (Exception e) {
            LOGGER.error("Problem while checking new job message", e);
            return null;
        } finally {
            instance.close();
        }
    }

    /**
     * makes sure ChannelAdapter will stop its activities in a secure manner,
     * closing all connections.
     */
    public void stopActivities() {
        this.jedisPool.close();
    }

    /**
     * assures all needed for a job to be returned successfully to a 'wait'
     * queue. removes job from 'work' queue,
     *
     * @return information if transaction succeeded or not.
     */
    public boolean returnJobBackToWaitQueue(Batch job) {
        boolean res = false;
        String jobId = job.getId();

        LOGGER.info("Returning job {} back to 'wait' queue", jobId);

        // 1. remove it from working queue
        res = removeJobFromWorkQueue(job);

        if (!res) {
            LOGGER.error("Failed to take job off 'work' queue; id: {}", jobId);
            return res;
        }

        // 2. back to wait queue
        res = sendJobToWaitQueue(job);

        return res;
    }

    /*
     * searches for dedicated job on queue and removes it off queue, send back
     * true if removal successful
     */
    private boolean removeJobFromWorkQueue(Batch job) {

        if (job.getId() == null) {
            LOGGER.warn("Got null ID of batch to remove off queue?!? Buckets: {}", job.getBuckets());
            return false;
        }

        String batchJson = batchToJson(job);

        Jedis instance = null;
        Long res = -1L;

        try {
            instance = this.jedisPool.getResource();
            res = instance.lrem(workQueue, 1, batchJson);

        } catch (Exception e) {
            LOGGER.warn("Problem while removing job {} off queue {} Ex:{}", job.getId(), workQueue, e);

        } finally {
            instance.close();
        }
        return res == 1;
    }

    /**
     * lists all jobs that are currently in progress.
     */
    public List<Batch> getJobsInProgress() {

        Jedis instance = null;

        List<String> res;

        try {
            instance = this.jedisPool.getResource();

            LOGGER.debug("Trying to read all elements in {} queue", workQueue);

            res = instance.lrange(workQueue, 0, -1);
            List<Batch> jobs = new ArrayList<>(res.size());
            for (String json : res) {
                Batch job = jsonToBatch(json);
                jobs.add(job);
            }

            return jobs;
        } catch (Exception e) {
            LOGGER.warn("Problem while listing job list of all elements, queue:{}", workQueue, e);
            return null;
        } finally {
            if (instance != null) {
                instance.close();
            }
        }
    }

}

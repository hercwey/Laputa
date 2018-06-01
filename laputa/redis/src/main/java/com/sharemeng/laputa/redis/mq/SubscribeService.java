package com.sharemeng.laputa.redis.mq;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author Paul 2018/5/31
 */
@Service
public class SubscribeService {

    @Resource
    private RedisService redisService;
    @Resource
    private SynListener synListener;//订阅者

    @PostConstruct
    public void subscribe() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                LogCvt.info("服务已订阅频道：{}", channel);
                redisService.subscribe(synListener, channel);
            }
        }).start();

    }
}

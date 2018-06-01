package com.sharemeng.laputa.redis.mq;

import javax.annotation.Resource;

import redis.clients.jedis.JedisPubSub;

/**
 * @author Paul 2018/5/31
 */
@Service
public class SynListener extends JedisPubSub {

    @Resource
    private DispatchMessageHandler dispatchMessageHandler;

    @Override
    public void onMessage(String channel, String message) {
        LogCvt.info("channel:{},receives message:{}",channel,message);
        try {
            //处理业务（同步文件）
            dispatchMessageHandler.synFile();
        } catch (Exception e) {
            LogCvt.error(e.getMessage(),e);
        }
    }
}

package com.sharemeng.laputa.redis.mq;

import javax.annotation.Resource;

/**
 * @author Paul 2018/5/31
 */
@Service
public class DispatchMessageHandler {

    @Resource
    private RedisService redisService;
    @Resource
    private MessageHandler messageHandler;

    public void synFile(){
        while(true){
            try {
                String message = redisService.lpop(RedisKeyUtil.syn_file_queue_key());
                if (null == message){
                    break;
                }
                Thread.currentThread().setName(Tools.uuid());
                // 队列数据处理
                messageHandler.synfile(message);
            } catch (Exception e) {
                LogCvt.error(e.getMessage(),e);
            }
        }
    }

}

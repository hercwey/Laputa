package com.sharemeng.laputa.monitor.alert;

/**
 * 消息发送接口
 * Created by Tony on 2017/10/9.
 */
public interface Sender<T extends MessageContext> {
    public void sendMessage(T context);
}

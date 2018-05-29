package com.sharemeng.laputa.monitor.alert;

/**
 * Created by Tony on 2017/10/9.
 */
public class MessageSenderFactory {
    public static Sender createSender(SenderChannelEnum channel) {
        switch (channel) {
            case EMAIL:
                return new EmailSender();
            case SMS:
                return new SMSSender();
            case WEIXIN:
                return new IMSender();
            default:
                return new EmailSender();
        }
    }
}

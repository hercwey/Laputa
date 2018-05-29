package com.sharemeng.laputa.monitor.alert;

import javax.activation.MailcapCommandMap;

/**
 * Created by Tony on 2017/10/9.
 */
public enum SenderChannelEnum {

    EMAIL(0),
    SMS(1),
    WEIXIN(2);

    private int value = 0;

    SenderChannelEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

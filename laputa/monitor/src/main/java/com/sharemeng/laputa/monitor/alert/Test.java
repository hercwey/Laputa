package com.sharemeng.laputa.monitor.alert;

import com.sun.org.apache.xpath.internal.operations.String;

/**
 * Created by Tony on 2017/10/10.
 */
public class Test {
    public static void main(String[] args) {
        MessageSenderFactory.createSender(SenderChannelEnum.SMS, new TestBean());
    }
}

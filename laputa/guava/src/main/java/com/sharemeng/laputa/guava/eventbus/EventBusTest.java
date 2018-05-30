package com.sharemeng.laputa.guava.eventbus;

import java.util.Date;
import java.util.concurrent.Executors;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;

/**
 * @author Paul 2018/5/29
 */
public class EventBusTest {

    public static void siginalThreadConsumer(){

        EventBus bus = new EventBus("iamzhongyong");
        SFEventListener sf = new SFEventListener();
        YTOEventListener yto = new YTOEventListener();
        bus.register(sf);
        bus.register(yto);
        SignEvent sign1 = new SignEvent("SF","比熊啊",new Date());
        bus.post(sign1);
        SignEvent sign2 = new SignEvent("YTO","你妹的",new Date());
        bus.post(sign2);
    }

    public static void multiThread(){
        EventBus bus = new AsyncEventBus(Executors.newFixedThreadPool(3));
        SFEventListener sf = new SFEventListener();
        YTOEventListener yto = new YTOEventListener();
        bus.register(sf);
        bus.register(yto);
        SignEvent sign1 = new SignEvent("SF","比熊啊",new Date());
        bus.post(sign1);
        SignEvent sign2 = new SignEvent("YTO","你妹的",new Date());
        bus.post(sign2);
    }

    public static void main(String[] args) {
        EventBusTest.siginalThreadConsumer();
        EventBusTest.multiThread();
    }
}
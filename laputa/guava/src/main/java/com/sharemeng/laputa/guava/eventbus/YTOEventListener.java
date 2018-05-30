package com.sharemeng.laputa.guava.eventbus;

import com.google.common.eventbus.Subscribe;

/**
 * @author Paul 2018/5/29
 */
public class YTOEventListener {
    @Subscribe
    public void consign(SignEvent signEvent){
        if(signEvent.getCompanyName().equalsIgnoreCase("YTO")){
            System.out.println("YTO。。。开始发货");
            System.out.println(signEvent.getMessage());
        }
    }

    @Subscribe
    public void delivery(SignEvent signEvent){
        if(signEvent.getCompanyName().equalsIgnoreCase("YTO")){
            System.out.println("YTO。。。开始投递");
        }
    }
}

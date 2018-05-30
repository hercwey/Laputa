package com.sharemeng.laputa.guava.eventbus;

import java.util.Date;

/**
 * 这个EventBus是guava中比较给力的一个类，从字面上看是事件总线，解决了传统的观察这模式的问题，使用比较灵活方便。最近
 * 打算搞一个轻量级的事件驱动引擎，重点参照了这个类EventBus，如果是内存级别的事件分发或者MQ，推荐直接用EventBus。
 关于EventBus中的几个问题？
 1、  事件定义：任意的对象即可；
 2、  事件处理器的注册：事件处理的方法，添加注解即可，然后事件处理器的对象注册到总线中，总线维护一个事件和事件处理器的
 关联关系，在内存中；
 3、  事件的处理过程：同步处理和异步处理，事件提交之后，事件队列维护在本地缓存，同步的方式直接当前线程去执行，异步的
 处理策略是在初始化事件总线的时候就搞了一个线程池出来，由线程池去异步执行；
 4、  EventBus就开放了三个方法，register/post/unregister
 5、  为什么会有unregister？
 在99.99%的使用场景中，是不会在runtime的时候去register/unregister某个observer的，在spring的环境，也是在init的时候
 做register/unregister。不过做framework就必须要考虑这0.01%的使用场景。
 *
 * @author Paul 2018/5/29
 */

public class SignEvent {
    private String companyName;
    private String signName;
    private Date signDate;

    public SignEvent(String name, String signName, Date signDate) {
        super();
        this.companyName = name;
        this.signName = signName;
        this.signDate = signDate;
    }

    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("物流公司：").append(this.companyName);
        sb.append("签收人：").append(signName).append(",签收日期：").append(signDate);
        return sb.toString();
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}

package com.sharemeng.lapuda.concurrent.callable;

/**
 * Callable结构使用泛型来定义对象的返回类型。Executors类型提供了有用的方法在线程池中执行Java Callable。因为callable
 * 任务并行运行，我们必须等待该返回对象(java.util.concurrent.Future)。
 * 通过Java Future对象，我们可以得到Callable任务的状态和返回的对象。它提供了get()方法等待Callable完成然后返回结果。
 *
 * Tutorial: https://www.journaldev.com/1090/java-callable-future-example
 * @author Paul 2018/5/29
 */
public class MyCallable {

}

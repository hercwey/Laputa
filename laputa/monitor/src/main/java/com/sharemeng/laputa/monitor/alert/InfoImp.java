package com.sharemeng.laputa.monitor.alert;

/**
 * Created by Tony on 2017/10/10.
 */
class InfoImp<T> implements Info<T> {
    private T var;
    // 定义泛型构造方法
    public InfoImp(T var) {
        this.setVar(var);
    }
    public void setVar(T var) {
        this.var = var;
    }
    public T getVar() {
        return this.var;
    }
}

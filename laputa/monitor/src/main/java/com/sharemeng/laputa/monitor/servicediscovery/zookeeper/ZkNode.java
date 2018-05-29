package com.sharemeng.laputa.monitor.servicediscovery.zookeeper;

/**
 * Created by Tony on 2017/9/5.
 */

public class ZkNode {
    public String path;
    public String data;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ZkNode [path=" + path + ", data=" + data + "]";
    }
}
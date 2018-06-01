package com.sharemeng.laputa.redis.queue;

import java.util.List;

/**
 * @author Paul 2018/5/31
 */
public class Batch {
    private String id;

    private List<?> buckets;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<?> getBuckets() {
        return buckets;
    }

    public void setBuckets(List<?> buckets) {
        this.buckets = buckets;
    }
}

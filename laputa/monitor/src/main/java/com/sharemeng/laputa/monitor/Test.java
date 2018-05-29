package com.sharemeng.laputa.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.InvalidParameterException;

/**
 * Created by Tony on 2017/9/20.
 */
public class Test {
    private static final Logger LOGGER = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) {
        LOGGER.error("Invalid param {},{}", "test","sss");
    }
}

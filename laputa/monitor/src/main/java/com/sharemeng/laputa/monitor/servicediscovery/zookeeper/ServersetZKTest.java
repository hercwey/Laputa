package com.sharemeng.laputa.monitor.servicediscovery.zookeeper;

import com.twitter.common.zookeeper.ServerSetImpl;
import com.twitter.finagle.zookeeper.ZkAnnouncer;
import com.twitter.util.Await;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * Created by Tony on 2017/9/6.
 */
public class ServersetZKTest {
    //10.20.219.126
    public static void main(String[] args) {

        InetAddress inetAddress = null;
        InetSocketAddress socketAddress = null;
        try {
            inetAddress = InetAddress.getByName("10.20.219.126");
            socketAddress = new InetSocketAddress(InetAddress.getByAddress("10.20.219.126", inetAddress.getAddress()), 8081);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        //new ServerSetImpl()
        ZkAnnouncer announcer = new ZkAnnouncer();

        announcer.announce(socketAddress, "zk!192.168.226.131:2181!/zksd!0");
        System.out.println("Successfully registered!");

        //Await.result();

    }
}

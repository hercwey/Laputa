package com.sharemeng.laputa.concurrent.lockobjects.reentrantlock;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 可重入锁实例
 * tutorial: https://www.journaldev.com/2377/java-lock-example-reentrantlock
 * @author Paul 2018/5/28
 */
public class ReentrantLockExample {
    private static ReentrantLock lock = new ReentrantLock();
    private static AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        Thread firstThread = new Thread(new AcquireLockRunnable(), "FirstThread");
        firstThread.start();
        Thread.sleep(1000);
        for (int i = 2; i < 9; i++) {
            Thread t = new Thread(new AcquireLockRunnable(), "OtherThreads(" + (i - 1) + ")");
            t.start();
        }
    }

    private static void print(String tag, String p) {
        System.out.println(Thread.currentThread().getName() + ": " + tag + ": " + p);
    }

    private static class AcquireLockRunnable implements Runnable {
        public void run() {
            counter.incrementAndGet();
            print("AcquireLockRunnable", "try lock");
            lock.lock();
            print("AcquireLockRunnable", "got lock");
            try {
                if (counter.get() == 1) {
                    Thread.sleep(5000);
                } else {
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
                print("AcquireLockRunnable", "unlocked");
            }
        }
    }
}
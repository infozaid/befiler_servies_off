package com.arittek.befiler_services.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Arittek on 11/6/2017.
 */
public class ExecutorTestingExample {
    public static void main(String[] agr) {
        ExecutorService es = Executors.newFixedThreadPool(4);

        for (int i = 1; i <= 2; i++) {
            final int cnt = i;
            es.execute(new Runnable() {
                @Override
                public void run() {
                        System.out.println("1st execute: "+Thread.currentThread().getName() + " is running Runnable - " + cnt);

                }
            });
        }
        try {
            Thread.sleep(5000);
            es.shutdown();
        }catch (Exception e){e.printStackTrace();}
    }
}

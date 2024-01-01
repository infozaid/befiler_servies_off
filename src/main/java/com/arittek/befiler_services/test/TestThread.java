package com.arittek.befiler_services.test;

/**
 * Created by Arittek on 11/9/2017.
 */
public class TestThread {
    public static void main(String arg[] ){

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0 ; i<=10 ; i++){
                    try {
                        Thread.sleep(500);
                        System.out.println("1 Thread: " + i);
                    }catch (Exception e){e.printStackTrace();}
                    }
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0 ; i<=10 ; i++){
                    try {
                        Thread.sleep(1000);
                        System.out.println("2 Thread: " + i);
                    }catch (Exception e){e.printStackTrace();}
                }
            }
        });

        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0 ; i<=10 ; i++){
                    try {
                        Thread.sleep(1500);
                        System.out.println("3 Thread: " + i);
                    }catch (Exception e){e.printStackTrace();}
                }
            }
        });
        t3.start();
        t2.start();
        t1.start();
    }
}

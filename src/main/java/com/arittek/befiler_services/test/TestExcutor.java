package com.arittek.befiler_services.test;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


// UserController.java


class SendEmailUtility {
    public static String sendMail(String sendto)throws IOException {
       // System.out.println("Email send successful: "+sendto);
        return "";
    }
}

class Demo {
    int i,j = 0;
    ExecutorService executor = Executors.newFixedThreadPool(1);

    public void sentMail(String sendTo) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                   System.out.println("1min thread: "+i++);
                    SendEmailUtility.sendMail(sendTo);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(500);
                    System.out.println("half min thread: "+j++);
                }catch (Exception e){e.printStackTrace();}
            }
        });
    }
}


public class TestExcutor {
    public static void main(String[] args) {
        Demo demo = new Demo();
        for(int i=1;i<=10 ;i++)
        demo.sentMail("nadeem2012ali@gmail.com");
        demo.executor.shutdown();
    }
}
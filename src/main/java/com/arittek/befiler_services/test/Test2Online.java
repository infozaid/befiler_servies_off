package com.arittek.befiler_services.test;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.util.List;

/**
 * Created by Arittek on 11/16/2017.
 */
public class Test2Online {
    public static void main(String arg[]){
        WebClient webClient = new WebClient();

        try {
            HtmlPage currentPage1 = webClient.getPage("https://www.google.com.pk/?gws_rd=cr,ssl&dcr=0&ei=IGMNWp2aF4qg8QXS3a3QBw");
            List<HtmlForm> form = currentPage1.getForms();
            for(int i = 0 ; i <= form.size() -1;i++){
                System.out.println("::::::::::::::::forms::::::::::::"+form.get(i).getId());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

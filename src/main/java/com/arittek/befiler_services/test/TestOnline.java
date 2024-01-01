package com.arittek.befiler_services.test;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.util.List;
import java.util.Scanner;

/**
 * Created by Arittek on 11/15/2017.
 */
public class TestOnline {

    public static void main(String arg[]) throws Exception {

        Scanner ob = new Scanner(System.in);

        WebClient webClient = new WebClient();

//        HtmlPage currentPage1 = webClient.getPage("https://iris.fbr.gov.pk/infosys/public/txplogin.xhtml");
        HtmlPage currentPage1 = webClient.getPage("https://e.fbr.gov.pk/Registration/onlinesearchTaxpayer.aspx");

        /*   List<HtmlAnchor> anchors = currentPage.getAnchors();
        for (int i = 0; i <= anchors.size() - 1; i++) {
            System.out.println("::anchors::" + anchors.get(i).getId());
        }
        System.out.println("::anchors size::" + anchors.size());
        */

        HtmlAnchor htmlAnchor = (HtmlAnchor) currentPage1.getHtmlElementById("loginForm:addNewUserID");

        HtmlPage page1 = htmlAnchor.click();

        Thread.sleep(6000);

        List<HtmlForm> forms = page1.getForms();
        for(int i = 0 ; i<= forms.size() - 1; i++){
            System.out.println(":::::::::::::::::::::::"+forms.get(i));
        }

        HtmlForm form = page1.getFormByName("publicRegistrationForm");

        HtmlButton  btn1 = (HtmlButton) form.getElementsByTagName("button").get(0);
        HtmlButton  btn2 = (HtmlButton) form.getElementsByTagName("button").get(1);
        System.out.println(btn1.getId()+"::::::::::::::  btn :::::::::::::::::"+btn2.getId());

        HtmlPage page2 = btn2.click();
        if(page2.isHtmlPage()){
            List<HtmlForm> list = page2.getForms();
            for(int i = 0; i<= list.size() -1;i++)
            System.out.println("::::::::::::::true::::::::::::forms: ::"+list.get(i).getId());
        }
        else
            System.out.println("::::::::::::::false::::::::::::::");
        /*
        HtmlForm form = page1.getFormByName("publicRegistrationForm");

        System.out.println(":::::::::::" + form.getId());

        HtmlImage imageReg = (HtmlImage) page1.getHtmlElementById("publicRegistrationForm:cpatchaImage").getByXPath("//img").get(4);
        String url = "https://iris.fbr.gov.pk" + imageReg.getSrcAttribute();

        HtmlSelect prefix = form.getSelectByName("publicRegistrationForm:j_idt61_input");
        HtmlOption prefixOption = prefix.getOption(1);
        prefixOption.setSelected(true);


        HtmlInput cnic = form.getInputByName("publicRegistrationForm:txtcnic");
        cnic.setValueAttribute("2222222222222");

        HtmlInput fname = form.getInputByName("publicRegistrationForm:txtNewFirstName");
        fname.setValueAttribute("father name");

        HtmlInput lname = form.getInputByName("publicRegistrationForm:txtLastName");
        lname.setValueAttribute("lastname");

        HtmlInput cellNo = form.getInputByName("publicRegistrationForm:txtCellNo");
        cellNo.setValueAttribute("00923133997366");

        HtmlInput cellNoConfirm = form.getInputByName("publicRegistrationForm:txtCnfrmCell");
        cellNoConfirm.setValueAttribute("00923133997366");

        HtmlInput emailConfirm = form.getInputByName("publicRegistrationForm:txtCnfrmEmailCmp");
        emailConfirm.setValueAttribute("ali@gmail.com");

        HtmlInput email = form.getInputByName("publicRegistrationForm:txtEmailCom");
        email.setValueAttribute("ali@gmail.com");

        System.out.println("Url:::::::1:::::::::" + url);

        HtmlInput captchaValuee = form.getInputByName("publicRegistrationForm:captchaValue");
        captchaValuee.setValueAttribute(ob.nextLine());

        HtmlButton button = form.getButtonByName("publicRegistrationForm:btnCaptchaSubmit");
        HtmlPage page2 = button.click(); // btn clicked

        if(page2.isHtmlPage()){

        HtmlForm form1 = page2.getFormByName("publicRegistrationForm");

       // HtmlInput smsPin  =  form1.getInputByName("publicRegistrationForm:smsPin");
       // System.out.println("Url:::::::::2:::::::" + smsPin);




        System.out.println("" + prefixOption.getValueAttribute());
        System.out.println("" + cnic.getValueAttribute());
        System.out.println("" + fname.getValueAttribute());
        System.out.println("" + lname.getValueAttribute());
        System.out.println("" + cellNo.getValueAttribute());

        System.out.println("" + cellNoConfirm.getValueAttribute());
        System.out.println("" + emailConfirm.getValueAttribute());
        System.out.println("" + email.getValueAttribute());
        System.out.println("captchaValuee: " + captchaValuee.getValueAttribute());
        }else
            System.out.println("not a page >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        *//*  HtmlInput smsConfig = form1.getInputByName("publicRegistrationForm:smsPin");
        smsConfig.setValueAttribute("123");
        HtmlInput emailConfig = form1.getInputByName("publicRegistrationForm:emailPin");
        emailConfig.setValueAttribute("123");
*/

        /* List<HtmlForm> forms = page.getForms();
        for(int i =0 ;i<=forms.size() -1;i++){
            System.out.println(forms.get(i).getId());
        }
        publicRegistrationForm:EE_submitCode1
*/

/*

        Thread.sleep(6000);
        List<HtmlForm> anchor = page.getForms();
        for(int i =0 ; i<= anchor.size() -1;i++){
            System.out.println("::forms::"+anchor.get(i).getId());
        }
        System.out.println("::forms size::"+anchor.size());
*/


        /*List<HtmlForm> forms = currentPage1.getForms();
        for(int i = 0 ;i<= forms.size()-1;i++){
        System.out.println("::forms::"+forms.get(i).getId());
        }
*/

        /*List<HtmlForm> forms = currentPage.getForms();
        for(int i = 0; i<= forms.size() -1;i++){
            System.out.println("::::::::::::"+forms.get(i).getId());
        }
*/
        //currentPage.getElementById("loginForm:addNewUserID");

        /*HtmlAnchor searchButton =  (HtmlAnchor) currentPage.getHtmlElementById("loginForm:addNewUserID");
        currentPage2 = searchButton.click();

        List<HtmlForm> forms  = currentPage2.getForms();
        for(int i = 0 ; i<= forms.size()-1;i++){

            System.out.println("::::::::"+forms.get(i).getId());
        }

        HtmlForm form = currentPage2.getFormByName("loginForm");
        *///HtmlInput cnic = form.getInputByName("publicRegistrationForm:txtcnic");


               /* List<HtmlForm> forms = currentPage.getForms();
                for(int i = 0 ;i <=forms.size()-1 ; i++){
                    System.out.println("Size :::::::::::::::::::::::"+forms.get(i).getId());
                }
                System.out.println("Size :::::::::::::::::::::::"+forms.size());
*/

    }
}

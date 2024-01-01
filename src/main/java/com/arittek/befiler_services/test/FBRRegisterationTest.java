package com.arittek.befiler_services.test;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arittek on 7/20/2017.
 */
public class FBRRegisterationTest {
    public static void main(String[] arg){
        HtmlButton htmlButton;
        System.getProperties().put("org.apache.commons.logging.simplelog.defaultlog", "debug");
        final WebClient webClient = new WebClient(BrowserVersion.CHROME);
        try{
            //Through Submit Live Form With Button Click

            HtmlPage page = (HtmlPage) webClient.getPage("https://iris.fbr.gov.pk/infosys/public/txplogin.xhtml");

            HtmlAnchor searchButton =  (HtmlAnchor) page.getHtmlElementById("loginForm:addNewUserID");
            HtmlPage htmlPage1 = searchButton.click();
            Thread.sleep(2000);
            HtmlForm form = page.getFormByName("publicRegistrationForm");



           /* HtmlInput prefix  = form.getInputByName("publicRegistrationForm:j_idt59");
            prefix.setValueAttribute("1");*/


/*            System.out.println("From Value--- "+form.getInputByName("publicRegistrationForm:txtcnic")); html tag*/

            //Getting Image Captcha


/*
            HtmlSelect mobile = form.getSelectByName("publicRegistrationForm:j_idt81_input");
            HtmlOption mobileOption = mobile.getOption(501);
            mobileOption.setSelected(true);*/

        HtmlSelect prefix = form.getSelectByName("publicRegistrationForm:j_idt59_input");
            HtmlOption prefixOption = prefix.getOption(3);
            prefixOption.setSelected(true);



/*

            System.out.println("Mobile:---- "+mobileOption.getValueAttribute());*/


            HtmlInput cnic = form.getInputByName("publicRegistrationForm:txtcnic");
            cnic.setValueAttribute("343534534534");

            HtmlInput fname   = form.getInputByName("publicRegistrationForm:txtNewFirstName");
            fname.setValueAttribute("Samiullah");

            HtmlInput lname = form.getInputByName("publicRegistrationForm:txtLastName");
            lname.setValueAttribute("Danish");

           /* HtmlInput mobile  = form.getInputByName("publicRegistrationForm:j_idt81_input");
            mobile.setValueAttribute("503");*/

            HtmlInput cellNo  = form.getInputByName("publicRegistrationForm:txtCellNo");
            cellNo.setValueAttribute("00923051245213");

            HtmlInput cellNoConfirm  = form.getInputByName("publicRegistrationForm:txtCnfrmCell");
            cellNoConfirm.setValueAttribute("00923051245213");

            HtmlInput emailConfirm  = form.getInputByName("publicRegistrationForm:txtCnfrmEmailCmp");
            emailConfirm.setValueAttribute("sarfaraz@arittek.com");

            HtmlInput email =   form.getInputByName("publicRegistrationForm:txtEmailCom");
            email.setValueAttribute("sarfaraz@arittek.com");

            //Sms Code
            //publicRegistrationForm:smsPin

            //Email Code
            //publicRegistrationForm:emailPin

            //Alert Message From Class
            //alertLabelLong


            System.out.println("CNIC --- "+cnic.getValueAttribute());
            System.out.println("Prefix-- "+prefixOption.getValueAttribute());
            System.out.println("First Name --- "+fname.getValueAttribute());
            System.out.println("Last Name --- "+lname.getValueAttribute());
            /*System.out.println("Mobile --- "+mobileOption.getValueAttribute());*/
            System.out.println("Cell No --- "+cellNo.getValueAttribute());
            System.out.println("Email --- "+emailConfirm.getValueAttribute());
            HtmlImage image = (HtmlImage) page.getHtmlElementById("publicRegistrationForm:cpatchaImage").getByXPath("//img").get(4);
            System.out.println("Captacha Image Url---- "+image.getSrcAttribute());
//            htmlButton = (HtmlButton) htmlPage1.getHtmlElementById("publicRegistrationForm:btnCaptchaSubmit");
//            System.out.println("Button----- "+htmlButton);
//            htmlPage1 = (HtmlPage)htmlButton.click();
/*

            System.out.println("Web Page Response Path : --- "+htmlPage1);
            System.out.println("Web Page Response: --- "+htmlPage1.getWebResponse());*/



//Extra Code

        //Through Post method.....
        //        webClient.setTimeout(5000);

/*
            HtmlPage page1 = (HtmlPage) webClient.getPage("https://iris.fbr.gov.pk/infosys/public/txplogin.xhtml");

            HtmlAnchor searchButton =  (HtmlAnchor) page1.getHtmlElementById("loginForm:addNewUserID");
            HtmlPage htmlPage1 = searchButton.click();
            Thread.sleep(1000);
            HtmlForm form = page1.getFormByName("publicRegistrationForm");

            form.getInputByName("publicRegistrationForm:txtcnic").setValueAttribute("4140241546751");  // works OK
            form.getInputByName("publicRegistrationForm:j_idt59_input").setValueAttribute("1"); // works OK
            form.getInputByName("publicRegistrationForm:txtNewFirstName").setValueAttribute("Samiullah"); // works OK
            form.getInputByName("publicRegistrationForm:txtLastName").setValueAttribute("Danish"); // works OK
            form.getInputByName("publicRegistrationForm:j_idt81_input").setValueAttribute("503"); // works OK
            form.getInputByName("publicRegistrationForm:txtCellNo").setValueAttribute("00923051245213"); // works OK
            form.getInputByName("publicRegistrationForm:txtCnfrmCell").setValueAttribute("00923051245213"); // works OK
            form.getInputByName("publicRegistrationForm:txtEmailCom").setValueAttribute("sarfaraz@arittek.com"); // works OK
            form.getInputByName("publicRegistrationForm:txtCnfrmEmailCmp").setValueAttribute("sarfaraz@arittek.com"); // works OK


            System.out.println("Form Values "+form);
*//*
            HtmlButton signInButton = (HtmlButton) form.getButtonsByName("Submit");  // doesn't work
            HtmlPage nextPage = (HtmlPage) signInButton.click();*//*





            System.out.print("Print Url Form-------- "+form);
//            System.out.print("Print Url Button-------- "+nextPage);

            Thread.sleep(2000);
            webClient.close();*/


            // Create web request
       /*   WebRequest requestSettings = new WebRequest(new URL("https://iris.fbr.gov.pk/infosys/public/txplogin.xhtml"), HttpMethod.POST);*/


/*
            form.getInputByName("publicRegistrationForm:txtcnic").setValueAttribute("4140241546751");  // works OK
            form.getInputByName("publicRegistrationForm:j_idt59_input").setValueAttribute("1"); // works OK
            form.getInputByName("publicRegistrationForm:txtNewFirstName").setValueAttribute("Samiullah"); // works OK
            form.getInputByName("publicRegistrationForm:txtLastName").setValueAttribute("Danish"); // works OK
            form.getInputByName("publicRegistrationForm:j_idt81_input").setValueAttribute("503"); // works OK
            form.getInputByName("publicRegistrationForm:txtCellNo").setValueAttribute("00923051245213"); // works OK
            form.getInputByName("publicRegistrationForm:txtCnfrmCell").setValueAttribute("00923051245213"); // works OK
            form.getInputByName("publicRegistrationForm:txtEmailCom").setValueAttribute("sarfaraz@arittek.com"); // works OK
            form.getInputByName("publicRegistrationForm:txtCnfrmEmailCmp").setValueAttribute("sarfaraz@arittek.com"); // works OK*/

            // Set the request parameters
          /* requestSettings.setRequestParameters(new ArrayList());
           requestSettings.getRequestParameters().add(new NameValuePair("publicRegistrationForm:txtcnic", "4140241546751"));
            requestSettings.getRequestParameters().add(new NameValuePair("publicRegistrationForm:j_idt59_input", "1"));
            requestSettings.getRequestParameters().add(new NameValuePair("publicRegistrationForm:txtNewFirstName", "Samiullah"));
            requestSettings.getRequestParameters().add(new NameValuePair("publicRegistrationForm:txtLastName", "Danish"));
            requestSettings.getRequestParameters().add(new NameValuePair("publicRegistrationForm:j_idt81_input", "503"));
            requestSettings.getRequestParameters().add(new NameValuePair("publicRegistrationForm:txtCellNo", "00923012526211"));
            requestSettings.getRequestParameters().add(new NameValuePair("publicRegistrationForm:txtCnfrmCell", "00923012526211"));
            requestSettings.getRequestParameters().add(new NameValuePair("publicRegistrationForm:txtEmailCom", "sarfaraz@arittek.com"));
            requestSettings.getRequestParameters().add(new NameValuePair("publicRegistrationForm:txtCnfrmEmailCmp", "sarfaraz@arittek.com"));

            Page page = webClient.getPage(requestSettings);
            System.out.println("URL Pages Testing-------------"+page.getWebResponse().getWebRequest());
            System.out.println(page.getWebResponse());*/

        }catch(Exception exe){

        }
   }
}
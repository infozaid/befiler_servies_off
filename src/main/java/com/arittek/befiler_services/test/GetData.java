package com.arittek.befiler_services.test;

import com.arittek.befiler_services.beans.FbrCaputerCodeBean;
import com.arittek.befiler_services.util.MyPrint;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Base64;

/**
 * Created by Arittek on 11/1/2017.
 */
public class GetData {

    public static void main(String[] arg){
        HtmlPage pageRegistration,htmlPage1;
        FbrCaputerCodeBean fbr=new FbrCaputerCodeBean();
        try {
            WebClient webClient = new WebClient();
            pageRegistration= (HtmlPage) webClient.getPage("https://iris.fbr.gov.pk/infosys/public/txplogin.xhtml");
            MyPrint.println("HtmlPage: ");
            HtmlAnchor searchButton =  (HtmlAnchor) pageRegistration.getHtmlElementById("loginForm:addNewUserID");
            htmlPage1 = searchButton.click();
            MyPrint.println("HtmlAnchor :");
            boolean hint=true;
            long start=System.currentTimeMillis();
//            HtmlForm form=null;
            HtmlImage imageReg=null;
            while(hint) {
                try {
                    imageReg=(HtmlImage) pageRegistration.getHtmlElementById("publicRegistrationForm:cpatchaImage").getByXPath("//img").get(4);
//                    form = pageRegistration.getFormByName("publicRegistrationForm");
                    hint=false;
                } catch (Exception e) {
//                    e.printStackTrace();
                }
            }

            long end=System.currentTimeMillis()-start;
            MyPrint.println("loaded in seconds:"+end/1000+" , in millis:"+end);
            HtmlForm form = pageRegistration.getFormByName("publicRegistrationForm");

            HtmlSelect prefix = form.getSelectByName("publicRegistrationForm:j_idt61_input");//"publicRegistrationForm:j_idt59_input"
            HtmlOption prefixOption = prefix.getOption(3);
            prefixOption.setSelected(true);

            HtmlSelect select = (HtmlSelect) pageRegistration.getElementById("publicRegistrationForm:j_idt83_input");//81
            HtmlOption option = select.getOptionByValue("501");
            select.setSelectedAttribute(option, true);
            System.out.print("Get Selected Options:------- "+select.getSelectedOptions());

            HtmlInput cnic = form.getInputByName("publicRegistrationForm:txtcnic");
            cnic.setValueAttribute("343534534534");

            HtmlInput fname   = form.getInputByName("publicRegistrationForm:txtNewFirstName");
            fname.setValueAttribute("Samiullah");

            HtmlInput lname = form.getInputByName("publicRegistrationForm:txtLastName");
            lname.setValueAttribute("Danish");

            HtmlInput cellNo  = form.getInputByName("publicRegistrationForm:txtCellNo");
            cellNo.setValueAttribute("00923051245213");

            HtmlInput cellNoConfirm  = form.getInputByName("publicRegistrationForm:txtCnfrmCell");
            cellNoConfirm.setValueAttribute("00923051245213");

            HtmlInput emailConfirm  = form.getInputByName("publicRegistrationForm:txtCnfrmEmailCmp");
            emailConfirm.setValueAttribute("sarfaraz@arittek.com");

            HtmlInput email =   form.getInputByName("publicRegistrationForm:txtEmailCom");
            email.setValueAttribute("sarfaraz@arittek.com");

            MyPrint.println("Size of form: "+form);

            MyPrint.println("Captacha Image Url---- "+imageReg.getSrcAttribute());
            String url="https://iris.fbr.gov.pk"+imageReg.getSrcAttribute();
            byte[] imageBytes = IOUtils.toByteArray(new URL(url));
            String base64 = Base64.getEncoder().encodeToString(imageBytes);

            fbr.setCode(1);
            fbr.setCaputreUrl(base64);
            fbr.setMessage("Success");

           MyPrint.println("fbr::::::::::::::::::::::"+fbr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

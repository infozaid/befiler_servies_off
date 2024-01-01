package com.arittek.befiler_services.services;

import com.arittek.befiler_services.beans.*;
import com.arittek.befiler_services.fbr.FbrActiveTaxpayer;
import com.arittek.befiler_services.fbr.FbrActiveTaxpayerRepository;
import com.arittek.befiler_services.fbr.FbrActiveTaxpayerServices;
import com.arittek.befiler_services.util.CommonUtil;
import com.arittek.befiler_services.util.Logger4j;
import com.arittek.befiler_services.util.MacAddress;
import com.arittek.befiler_services.util.MyPrint;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLSpanElement;
import org.apache.commons.io.IOUtils;
import org.aspectj.util.FileUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLBodyElement;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLTableCellElement;

import javax.imageio.ImageIO;
import javax.lang.model.util.Elements;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.List;

public class FbrVarificationsServices {

    private static Properties fbrScriptingData = new Properties();
    /*private HtmlPage pageRegistration;*/
    HtmlImage imageReg=null;
    Map<String ,HtmlPage> map = new HashMap();

    public NtnCaptureCodeBean getNtnCaptureCodeFromFbrWebSite(Integer userId,String macAddress) throws Exception {
        MyPrint.println("user id------------------------------------" + userId);
        NtnCaptureCodeBean fbr = new NtnCaptureCodeBean();

        HtmlPage pageRegistration;
        System.getProperties().put("org.apache.commons.logging.simplelog.defaultlog", "debug");
        final WebClient webClient = new WebClient(BrowserVersion.CHROME);
        try {

            try {
                pageRegistration= (HtmlPage) webClient.getPage("https://e.fbr.gov.pk/Registration/onlinesearchTaxpayer.aspx");
                map.put(macAddress,pageRegistration);

                System.out.println("get Service1:::::::::::"+pageRegistration.getUrl());
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : ", e);
                String[] emails = {"kiratkuma47@gmail.com", "amaqsood68@gmail.com"};
                CommonUtil.sendMail(emails, "Ntn Error", "Main URL :: https://iris.fbr.gov.pk/infosys/public/txplogin.xhtml has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                fbr.setCode(0);
                fbr.setMessage("Error");
                return fbr;
            }

            boolean hint = true;
            long start = System.currentTimeMillis();
//            HtmlImage imageReg=null;
            while(hint) {
                try {
                    imageReg = (HtmlImage) pageRegistration.getElementById("ctl00_ContentPlaceHolder1_capchaImage");
                    hint = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            long end = System.currentTimeMillis() - start;
            MyPrint.println("loaded in seconds:" + end / 1000 + " , in millis:" + end);

            HtmlImage image = null;
            try {
                image = pageRegistration.<HtmlImage>getFirstByXPath("//img[@id='ctl00_ContentPlaceHolder1_capchaImage']");
            }catch (Exception e){
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : ", e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error", CommonUtil.getRootCause(e).getMessage());
                fbr.setCode(0);
                fbr.setMessage("Error");
                return fbr;
            }

            File imageFile = new File("C:/base64/"+macAddress+".jpeg");
            image.saveAs(imageFile);
            /*byte[] imageBytes = IOUtils.toByteArray(imageFile.getIn);*/
            byte[] imageBytes = FileUtil.readAsByteArray(imageFile);
            String base64 = Base64.getEncoder().encodeToString(imageBytes);

            fbr.setCode(1);
            fbr.setCaputreUrl(base64);
            fbr.setCaptchaUrl("https://b9d8fa66.ngrok.io/befiler_services_dev/captcha/" + macAddress + ".jpeg");
            fbr.setMessage("Success");

            return fbr;

        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : ", e);
            String[] emails = {"kiratkumar47@gmail.com", "amaqsood68@gmail.com"};
            CommonUtil.sendMail(emails, "FBR Registrations Error", CommonUtil.getRootCause(e).getMessage());
            fbr.setCode(0);
            fbr.setMessage("Error3");
            return fbr;
        }
    }


    public CaptchaBean getFbrByNntOrFtn(String searchBy, String registrationNo, String captchaCode,String macAddress, FbrActiveTaxpayerRepository taxpayerRepository) throws Exception {

        CaptchaBean captchaBean = new CaptchaBean();

        HtmlPage pageRegistration =  map.get(macAddress);

        System.getProperties().put("org.apache.commons.logging.simplelog.defaultlog", "debug");
        final WebClient webClient = new WebClient(BrowserVersion.CHROME);

        try {
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setPrintContentOnFailingStatusCode(false);

            HtmlSelect searchParameter = null;
            try {

                searchParameter = (HtmlSelect) pageRegistration.getHtmlElementById("ctl00_ContentPlaceHolder1_DDLS0004001"); //fbrScriptingData.getProperty("ddlSearchType")

            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : ", e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error", CommonUtil.getRootCause(e).getMessage());
                captchaBean.setCode(0);
                captchaBean.setMessage("Error");
                return captchaBean;
            }
            HtmlOption option = searchParameter.getOptionByValue(searchBy);
            searchParameter.setSelectedAttribute(option, true);

            HtmlInput ntnNum1 = null;
            try {
                ntnNum1 = (HtmlInput) pageRegistration.getHtmlElementById("ctl00_ContentPlaceHolder1_TXTS1003002");//fbrScriptingData.getProperty("ntnNum1")
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : ", e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error", CommonUtil.getRootCause(e).getMessage());
                captchaBean.setCode(0);
                captchaBean.setMessage("Error");
                return captchaBean;
            }

            ntnNum1.setValueAttribute(registrationNo);

            HtmlInput ntnCaptcha = null;
            try {
                ntnCaptcha = (HtmlInput) pageRegistration.getHtmlElementById("ctl00_ContentPlaceHolder1_txtCapcha");
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : ", e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error", CommonUtil.getRootCause(e).getMessage());
                captchaBean.setCode(0);
                captchaBean.setMessage("Error");
                return captchaBean;
            }

            ntnCaptcha.setValueAttribute(captchaCode);

            final HtmlForm form = pageRegistration.getForms().get(0);

            HtmlSubmitInput searchButton = null;
            try {
                searchButton = form.getInputByName("ctl00$ContentPlaceHolder1$btnVerify");
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : ", e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error", CommonUtil.getRootCause(e).getMessage());
                captchaBean.setCode(0);
                captchaBean.setMessage("Error");
                return captchaBean;
            }
            HtmlPage htmlPage1 = searchButton.click();

            HtmlSpan elements = null;
            try {
                elements = (HtmlSpan) htmlPage1.getElementById("ctl00_ContentPlaceHolder1_lblResults");
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : ", e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error", CommonUtil.getRootCause(e).getMessage());
                captchaBean.setCode(0);
                captchaBean.setMessage("Error");
                return captchaBean;
            }

            if (elements != null) {


                DomNodeList<HtmlElement> htmlElements = null;
                try {
                    htmlElements = elements.getElementsByTagName("table");

                } catch (Exception e) {
                    Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                    Logger4j.getLogger().error("Exception : ", e);
                    String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                    CommonUtil.sendMail(emails, "FBR Registrations Error", CommonUtil.getRootCause(e).getMessage());
                    captchaBean.setCode(0);
                    captchaBean.setMessage("Error");
                    return captchaBean;
                }

                DomNodeList<HtmlElement> htmlStrong = elements.getElementsByTagName("strong");
                if(htmlStrong.size() > 0){
                    captchaBean.setCode(0);
                    captchaBean.setMessage("No record exists");
                    return captchaBean;
                }

                ArrayList<CaptchaBean2> bean2ArrayList = new ArrayList<>();
                System.out.println("Total Tables: "+htmlElements.size());
                for (HtmlElement element : htmlElements) {
                    System.out.println("Total Rows: "+element.getElementsByTagName("tr").size());


                    for (HtmlElement elementTr : element.getElementsByTagName("tr")) {

                        CaptchaBean2  captchaBean2 = new CaptchaBean2();
                        if(elementTr.getFirstElementChild().getTagName().equals("td")){

                            int countTd = 0;
                            for (HtmlElement elementTd : elementTr.getElementsByTagName("td")) {
                                System.out.print(elementTd.asText());
                                if(countTd==0){
                                    captchaBean2.setSr(elementTd.asText());
                                }
                                if(countTd==1){
                                    captchaBean2.setBusinessBranchName(elementTd.asText());
                                }
                                if(countTd==2){
                                    captchaBean2.setBusinessBranchAddress(elementTd.asText());
                                }
                                if(countTd==3){
                                    captchaBean2.setPrincipalActivity(elementTd.asText());
                                }
                                countTd++;
                            }
                            bean2ArrayList.add(captchaBean2);
                            System.out.println();
                        }

//                            for (HtmlElement elementTh : elementTr.getElementsByTagName("th")) {
//                                MyPrint.println("Head:: ::::"+elementTh.asText()+" : ");
//                                for (HtmlElement elementTd : elementTr.getElementsByTagName("td")) {
//
//                                    if(elementTh.asText().equals("Sr.")){
//                                        System.out.print("Data: :::"+elementTd.asText()+" : ");
//                                    }
//                                }
//                            System.out.println();
//                        }

                        int index = 0;
                        for (HtmlElement elementTh : elementTr.getElementsByTagName("th")) {

                            if (elementTh.asText().equals("Category")) {
                                captchaBean.setCategory(elementTr.getElementsByTagName("td").get(index).asText());
                            }
                            if (elementTh.asText().equals("Registration No")) {
                                captchaBean.setRegistrationNo(elementTr.getElementsByTagName("td").get(index).asText());

                            }
                            if (elementTh.asText().equals("Reference No")) {
                                captchaBean.setReferenceNo(elementTr.getElementsByTagName("td").get(index).asText());
                            }
                            if (elementTh.asText().equals("Name")) {
                                captchaBean.setName(elementTr.getElementsByTagName("td").get(index).asText());
                            }

                            if (elementTh.asText().equals("STRN")) {
                                captchaBean.setStrn(elementTr.getElementsByTagName("td").get(index).asText());
                            }
                            if (elementTh.asText().equals("Address")) {
                                captchaBean.setAddress(elementTr.getElementsByTagName("td").get(index).asText());
                            }
                            if (elementTh.asText().equals("Registered On")) {
                                captchaBean.setRegisteredOn(elementTr.getElementsByTagName("td").get(index).asText());
                            }

                            if (elementTh.asText().equals("Tax Office")) {
                                captchaBean.setTaxOffice(elementTr.getElementsByTagName("td").get(index).asText());
                            }
                            if (elementTh.asText().equals("Registration Status")) {
                                captchaBean.setRegistrationStatus(elementTr.getElementsByTagName("td").get(index).asText());
                            }

                            index++;
                        }
                    }


                    if(bean2ArrayList != null){
                        captchaBean.setCaptchaBean2(bean2ArrayList);
                    }

                    //---------------------------------------------------------------------------

                    try {
                        if(registrationNo != null) {
                            FbrActiveTaxpayer taxpayer = taxpayerRepository.findOneByRegistrationNo(registrationNo);
                            if (taxpayer != null)
                                captchaBean.setFilerStatus("Filer");
                            else
                                captchaBean.setFilerStatus("non - Filer");
                        }

                    }catch (Exception e){e.printStackTrace();}
                    captchaBean.setCode(1);
                    captchaBean.setMessage("Success");
                    return captchaBean;
                }

                HtmlSpan status = (HtmlSpan) htmlPage1.getElementById("ctl00_ContentPlaceHolder1_lblStatus");
                if(status != null){
                    captchaBean.setCode(2);
                    captchaBean.setMessage(status.asText());
                    return captchaBean;
                }
                System.out.println("-----------------------------------------------");
            }

        } catch (Exception e) {
            Logger4j.getLogger().error("Exception:", e);
            e.printStackTrace();
        }
        return captchaBean;
    }

    public FbrBean getFbrByCNIC(String searchBy, String parm1, String captchaCode,String macAddress) throws Exception {

        MyPrint.println("getFbrNIC method is called from FbrAllervices Class........");
        FbrBean fbrBean = new FbrBean();

        HtmlPage pageRegistration =  map.get(macAddress);

        System.getProperties().put("org.apache.commons.logging.simplelog.defaultlog", "debug");
        final WebClient webClient = new WebClient(BrowserVersion.CHROME);
        try {
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setPrintContentOnFailingStatusCode(false);
            /*pageRegistration = webClient.getPage("https://e.fbr.gov.pk/Registration/onlinesearchTaxpayer.aspx?rand=2");*/

            /*MyPrint.println("Page Content ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::" + page.asText());*/

            HtmlSelect searchParameter = (HtmlSelect) pageRegistration.getElementById(fbrScriptingData.getProperty("ddlSearchType"));
            /*MyPrint.println("Search Parameter ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::" + searchParameter);*/

            HtmlOption option = searchParameter.getOptionByValue(searchBy);
            searchParameter.setSelectedAttribute(option, true);

            HtmlInput ntnNum1 = (HtmlInput) pageRegistration.getHtmlElementById(fbrScriptingData.getProperty("ntntxtsearch2"));
            ntnNum1.setValueAttribute(parm1);


//            HtmlInput ntnCaptcha = (HtmlInput) pageRegistration.getHtmlElementById(fbrScriptingData.getProperty("ntntxtsearch2"));
            HtmlInput ntnCaptcha = (HtmlInput) pageRegistration.getHtmlElementById("ctl00_ContentPlaceHolder1_txtCapcha");

            ntnCaptcha.setValueAttribute(captchaCode);


            MyPrint.println("Search Parameter ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::" + searchParameter.getSelectedOptions());
            MyPrint.println("NTN Number 1 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::" + ntnNum1);
            MyPrint.println("NTN captchaCode 2 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::" + captchaCode);

            HtmlAnchor searchButton = (HtmlAnchor) pageRegistration.getHtmlElementById(fbrScriptingData.getProperty("searchbtn"));
            HtmlPage htmlPage1 = searchButton.click();


            HtmlTable table = (HtmlTable) htmlPage1.getElementsByTagName("table").get(0);
            List<HtmlTableRow> tableRowList = table.getRows();

            int r = 0;

            for (HtmlTableRow row : tableRowList) {
                int c = 0;
                r++;
                for (HtmlTableCell cell : row.getCells()) {
                    c++;
                    /*if (cell.asText().trim() != null && !cell.asText().trim().equalsIgnoreCase(""))*/
                    MyPrint.println(r + ":" + c + "::::::" + cell.asText().trim() + "      ");
                }
                MyPrint.println();
            }

            HtmlSpan elements = pageRegistration.getHtmlElementById("ctl00_ContentPlaceHolder1_lblResults");//fbrScriptingData.getProperty("elementslblmsg")
            MyPrint.println("Error Span ::::::::::" + elements.asText());

            if (elements.asText().equals("Requested Information not found, please visit the Local Registration Office")) {
                fbrBean.setCode(2);
                fbrBean.setMessage(elements.asText());
                return fbrBean;
            } else {
//                fbrBean.setCode(1);
//                fbrBean.setNtnOrFtn(table.getRows().get(3).getCells().get(2).asText() + "");
//                fbrBean.setCategory(table.getRows().get(3).getCells().get(5).asText());
//                fbrBean.setName(table.getRows().get(4).getCells().get(2).asText());
//                fbrBean.setCnicOrPpOrRegOrIncNo(table.getRows().get(5).getCells().get(2).asText());
//                fbrBean.setHouseOrFlatOrPlotNo(table.getRows().get(6).getCells().get(2).asText());
//                fbrBean.setStreetOrLane(table.getRows().get(6).getCells().get(5).asText());
//                fbrBean.setBlockOrSectorOrRoad(table.getRows().get(7).getCells().get(2).asText());
//                fbrBean.setCity(table.getRows().get(7).getCells().get(5).asText());
//
//                fbrBean.setPrincipalBusinessActivity(table.getRows().get(11).getCells().get(2).asText());
//                fbrBean.setBusinessNature(table.getRows().get(12).getCells().get(2).asText());
//                fbrBean.setRegisteredFor(table.getRows().get(13).getCells().get(2).asText());
//                fbrBean.setIncomeTaxOffice(table.getRows().get(14).getCells().get(2).asText());
//                fbrBean.setMessage("succeed...");

                String branchListString = table.getRows().get(9).getCells().get(2).asText();
                StringTokenizer tokens = new StringTokenizer(branchListString, "\n");  //token for branch list

                tokens.nextElement();   //first token will be destroy bcz of heading name as like " Business/Branches"

                List<FbrBusinessOrBranchesBean> fbrBusinessOrBranchesBeanList = new ArrayList<>();  //all branches will add in this list

            /* HERE FETCH EVERY BRANCH TOKEN AND THEN SET IN BEAN   */
                MyPrint.println("===========================tokens Size =" + tokens.countTokens());
                while (tokens.hasMoreTokens()) {
                    String branch = tokens.nextElement() + "";  // COMPLETE DATA OF EVERY BRANCH

                    StringTokenizer branchTokens = new StringTokenizer(branch, "\t");/* "\t" FOR SEPERATE NESTED DATA*/

                    FbrBusinessOrBranchesBean bean = new FbrBusinessOrBranchesBean();

                    bean.setSerialNo(branchTokens.nextElement() + "");
                    bean.setBusinessOrBranchName(branchTokens.nextElement() + "");
                    bean.setBusinessOrBranchAddress(branchTokens.nextElement() + "");
                    fbrBusinessOrBranchesBeanList.add(bean);
                }

                // fbrBean.setFbrBusinessOrBranchesBeanList(fbrBusinessOrBranchesBeanList);
                return fbrBean;
            }

        } catch (Exception e) {
            Logger4j.getLogger().error("Exception:", e);
            e.printStackTrace();
        } finally {
            webClient.close();
        }
        return fbrBean;

    }

}

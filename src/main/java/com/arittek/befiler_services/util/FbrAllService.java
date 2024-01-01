package com.arittek.befiler_services.util;

import com.arittek.befiler_services.beans.*;
import com.arittek.befiler_services.repository.FbrRegisteredUserRepository;
import com.arittek.befiler_services.services.FbrRegisterdUserServices;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class FbrAllService {
    private  FbrRegisterdUserServices fbrRegisterdUserServices;
    private FbrRegisteredUserRepository fbrRegisterdUserRepository;

    //FbrUserRegistrationPageBean
    public FbrAllService(){

    }
    @Autowired
    public FbrAllService(FbrRegisteredUserRepository fbrRegisterdUserRepository, FbrRegisterdUserServices fbrRegisterdUserServices){
        this.fbrRegisterdUserRepository=fbrRegisterdUserRepository;
        this.fbrRegisterdUserServices=fbrRegisterdUserServices;
    }

    private static Properties fbrScriptingData = new Properties();

    HashMap<Integer,FbrUserRegistrationPageBean> usersRegistraionPagesMap=new HashMap<Integer,FbrUserRegistrationPageBean>();

    Map<String, HtmlPage> pageRegistrationList;
    HtmlPage pageRegistration ;
    private  HtmlPage htmlPage1;

    public  FbrBean getFbrByNntOrFtn(String searchBy,String parm1,String parm2)throws Exception{
        FbrBean fbrBean = new FbrBean();

        System.getProperties().put("org.apache.commons.logging.simplelog.defaultlog", "debug");
        final WebClient webClient = new WebClient(BrowserVersion.CHROME);

        try {
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setPrintContentOnFailingStatusCode(false);

            //                                           https://e.fbr.gov.pk/Registration/onlinesearchTaxpayer.aspx?PralAJAXControls_CallBack=true
            final HtmlPage page = webClient.getPage("https://e.fbr.gov.pk/Registration/onlinesearchTaxpayer.aspx?rand=2");


            /*MyPrint.println("Page Content ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::" + page.asText());*/

            HtmlSelect searchParameter = (HtmlSelect) page.getElementById(fbrScriptingData.getProperty("ddlSearchType"));
            /*MyPrint.println("Search Parameter ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::" + searchParameter);*/

            HtmlOption option = searchParameter.getOptionByValue(searchBy);
            searchParameter.setSelectedAttribute(option, true);

            HtmlInput ntnNum1 = (HtmlInput)page.getHtmlElementById(fbrScriptingData.getProperty("ntnNum1"));
            ntnNum1.setValueAttribute(parm1);

            HtmlInput ntnNum2 = (HtmlInput)page.getHtmlElementById(fbrScriptingData.getProperty("ntnNum2"));
            ntnNum2.setValueAttribute(parm2);

            MyPrint.println("Search Parameter ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::" + searchParameter.getSelectedOptions());
            MyPrint.println("NTN Number 1 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::" + ntnNum1);
            MyPrint.println("NTN Number 2 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::" + ntnNum2);

            HtmlAnchor searchButton =  (HtmlAnchor) page.getHtmlElementById(fbrScriptingData.getProperty("searchbtn"));
            HtmlPage htmlPage1 = searchButton.click();



            HtmlTable table = (HtmlTable) htmlPage1.getElementsByTagName("table").get(0);
            List<HtmlTableRow> tableRowList = table.getRows();


            int r=0;

            for (HtmlTableRow row : tableRowList) {
                int c=0;
                r++;
                for (HtmlTableCell cell : row.getCells()){
                    c++;
                    /*if (cell.asText().trim() != null && !cell.asText().trim().equalsIgnoreCase(""))*/
                    MyPrint.println(r+":"+c+"::::::" + cell.asText().trim() + "      ");
                }
                MyPrint.println();
            }

            HtmlSpan elements = page.getHtmlElementById(fbrScriptingData.getProperty("elementslblmsg"));
            MyPrint.println("Error Span ::::::::::" + elements.asText());

            if(elements.asText().equals("Requested Information not found, please visit the Local Registration Office")){
                fbrBean.setCode(0);
                fbrBean.setMessage(elements.asText());
                return fbrBean;
            }
            else {
                fbrBean.setCode(1);
                fbrBean.setNtnOrFtn(table.getRows().get(3).getCells().get(2).asText() + "");
                fbrBean.setCategory(table.getRows().get(3).getCells().get(5).asText());
                fbrBean.setName(table.getRows().get(4).getCells().get(2).asText());
                fbrBean.setCnicOrPpOrRegOrIncNo(table.getRows().get(5).getCells().get(2).asText());
                fbrBean.setHouseOrFlatOrPlotNo(table.getRows().get(6).getCells().get(2).asText());
                fbrBean.setStreetOrLane(table.getRows().get(6).getCells().get(5).asText());
                fbrBean.setBlockOrSectorOrRoad(table.getRows().get(7).getCells().get(2).asText());
                fbrBean.setCity(table.getRows().get(7).getCells().get(5).asText());

                fbrBean.setPrincipalBusinessActivity(table.getRows().get(11).getCells().get(2).asText());
                fbrBean.setBusinessNature(table.getRows().get(12).getCells().get(2).asText());
                fbrBean.setRegisteredFor(table.getRows().get(13).getCells().get(2).asText());
                fbrBean.setIncomeTaxOffice(table.getRows().get(14).getCells().get(2).asText());
                fbrBean.setMessage("succeed...");

                String branchListString = table.getRows().get(9).getCells().get(2).asText();
                StringTokenizer tokens = new StringTokenizer(branchListString, "\n");  //token for branch list

                tokens.nextElement();   //first token will be destroy bcz of heading name as like " Business/Branches"

                List<FbrBusinessOrBranchesBean> fbrBusinessOrBranchesBeanList = new ArrayList<>();  //all branches will add in this list

            /* HERE FETCH EVERY BRANCH TOKEN AND THEN SET IN BEAN   */
                for (int i = 1; i <= tokens.countTokens(); i++) {

                    String branch = tokens.nextElement() + "";  //COMPLETE DATA OF EVERY BRANCH

                    StringTokenizer branchTokens = new StringTokenizer(branch, "\t"); //"\t" FOR SEPERATE NESTED DATA

                    FbrBusinessOrBranchesBean bean = new FbrBusinessOrBranchesBean();

                    bean.setSerialNo(branchTokens.nextElement() + "");
                    bean.setBusinessOrBranchName(branchTokens.nextElement() + "");
                    bean.setBusinessOrBranchAddress(branchTokens.nextElement() + "");
                    fbrBusinessOrBranchesBeanList.add(bean);
                }

                fbrBean.setFbrBusinessOrBranchesBeanList(fbrBusinessOrBranchesBeanList);
                return  fbrBean;
            }

        } catch (Exception e) {
            Logger4j.getLogger().error("Exception:",e);
            e.printStackTrace();
        } finally {
            webClient.close();
        }
        return  fbrBean;
    }  //end of getFbrNtnOrFtn...

    public  FbrBean getFbrByCNIC(String searchBy,String parm1,String parm2)throws Exception{

        MyPrint.println("getFbrNIC method is called from FbrAllervices Class........");
        FbrBean fbrBean = new FbrBean();

        System.getProperties().put("org.apache.commons.logging.simplelog.defaultlog", "debug");
        final WebClient webClient = new WebClient(BrowserVersion.CHROME);
        try {
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setPrintContentOnFailingStatusCode(false);
            final HtmlPage page = webClient.getPage("https://e.fbr.gov.pk/Registration/onlinesearchTaxpayer.aspx?rand=2");

            /*MyPrint.println("Page Content ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::" + page.asText());*/

            HtmlSelect searchParameter = (HtmlSelect) page.getElementById(fbrScriptingData.getProperty("ddlSearchType"));
            /*MyPrint.println("Search Parameter ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::" + searchParameter);*/

            HtmlOption option = searchParameter.getOptionByValue(searchBy);
            searchParameter.setSelectedAttribute(option, true);

            HtmlInput ntnNum1 = (HtmlInput)page.getHtmlElementById(fbrScriptingData.getProperty("ntntxtsearch2"));
            ntnNum1.setValueAttribute(parm1);

/*
            HtmlInput ntnNum2 = (HtmlInput)page.getHtmlElementById("ctl00_ContentPlaceHolder1_ctrlOnlineSearch_txtChkDigit");
            ntnNum2.setValueAttribute(parm2);
*/

            MyPrint.println("Search Parameter ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::" + searchParameter.getSelectedOptions());
            MyPrint.println("NTN Number 1 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::" + ntnNum1);
/*
            MyPrint.println("NTN Number 2 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::" + ntnNum2);
*/

            HtmlAnchor searchButton =  (HtmlAnchor) page.getHtmlElementById(fbrScriptingData.getProperty("searchbtn"));
            HtmlPage htmlPage1 = searchButton.click();



            HtmlTable table = (HtmlTable) htmlPage1.getElementsByTagName("table").get(0);
            List<HtmlTableRow> tableRowList = table.getRows();

            int r=0;

            for (HtmlTableRow row : tableRowList) {
                int c=0;
                r++;
                for (HtmlTableCell cell : row.getCells()){
                    c++;
                    /*if (cell.asText().trim() != null && !cell.asText().trim().equalsIgnoreCase(""))*/
                    MyPrint.println(r+":"+c+"::::::" + cell.asText().trim() + "      ");
                }
                MyPrint.println();
            }

            HtmlSpan elements = page.getHtmlElementById(fbrScriptingData.getProperty("elementslblmsg"));
            MyPrint.println("Error Span ::::::::::" + elements.asText());

            if(elements.asText().equals("Requested Information not found, please visit the Local Registration Office")){
                fbrBean.setCode(0);
                fbrBean.setMessage(elements.asText());
                return fbrBean;
            }
            else {
                fbrBean.setCode(1);
                fbrBean.setNtnOrFtn(table.getRows().get(3).getCells().get(2).asText() + "");
                fbrBean.setCategory(table.getRows().get(3).getCells().get(5).asText());
                fbrBean.setName(table.getRows().get(4).getCells().get(2).asText());
                fbrBean.setCnicOrPpOrRegOrIncNo(table.getRows().get(5).getCells().get(2).asText());
                fbrBean.setHouseOrFlatOrPlotNo(table.getRows().get(6).getCells().get(2).asText());
                fbrBean.setStreetOrLane(table.getRows().get(6).getCells().get(5).asText());
                fbrBean.setBlockOrSectorOrRoad(table.getRows().get(7).getCells().get(2).asText());
                fbrBean.setCity(table.getRows().get(7).getCells().get(5).asText());

                fbrBean.setPrincipalBusinessActivity(table.getRows().get(11).getCells().get(2).asText());
                fbrBean.setBusinessNature(table.getRows().get(12).getCells().get(2).asText());
                fbrBean.setRegisteredFor(table.getRows().get(13).getCells().get(2).asText());
                fbrBean.setIncomeTaxOffice(table.getRows().get(14).getCells().get(2).asText());
                fbrBean.setMessage("succeed...");

                String branchListString = table.getRows().get(9).getCells().get(2).asText();
                StringTokenizer tokens = new StringTokenizer(branchListString, "\n");  //token for branch list

                tokens.nextElement();   //first token will be destroy bcz of heading name as like " Business/Branches"

                List<FbrBusinessOrBranchesBean> fbrBusinessOrBranchesBeanList = new ArrayList<>();  //all branches will add in this list

            /* HERE FETCH EVERY BRANCH TOKEN AND THEN SET IN BEAN   */
                MyPrint.println("===========================tokens Size ="+tokens.countTokens());
                while (tokens.hasMoreTokens()){
                    String branch = tokens.nextElement() + "";  // COMPLETE DATA OF EVERY BRANCH

                    StringTokenizer branchTokens = new StringTokenizer(branch, "\t");/* "\t" FOR SEPERATE NESTED DATA*/

                    FbrBusinessOrBranchesBean bean = new FbrBusinessOrBranchesBean();

                    bean.setSerialNo(branchTokens.nextElement() + "");
                    bean.setBusinessOrBranchName(branchTokens.nextElement() + "");
                    bean.setBusinessOrBranchAddress(branchTokens.nextElement() + "");
                    fbrBusinessOrBranchesBeanList.add(bean);
                }

                fbrBean.setFbrBusinessOrBranchesBeanList(fbrBusinessOrBranchesBeanList);
                return  fbrBean;
            }

        } catch (Exception e) {
            Logger4j.getLogger().error("Exception:",e);
            e.printStackTrace();
        } finally {
            webClient.close();
        }
        return  fbrBean;

    }


    public  FbrCaputerCodeBean refreshCaptcha()throws  Exception{
        FbrCaputerCodeBean responseBean=new FbrCaputerCodeBean();
        HtmlButton htmlButton;
        System.getProperties().put("org.apache.commons.logging.simplelog.defaultlog", "debug");
        final WebClient webClient = new WebClient(BrowserVersion.CHROME);
        try{
            HtmlForm form = pageRegistration.getFormByName(fbrScriptingData.getProperty("form"));


            HtmlImage imageReg = (HtmlImage) pageRegistration.getHtmlElementById(fbrScriptingData.getProperty("captchimage")).getByXPath("//img").get(4);
            MyPrint.println("Captacha Image Url---- "+imageReg.getSrcAttribute());

            ///publicRegistrationForm:mainErrorMessages

            htmlButton = (HtmlButton) htmlPage1.getHtmlElementById(fbrScriptingData.getProperty("btnrefreshcaptcha"));
            MyPrint.println("Button----- "+htmlButton);

            MyPrint.println("_______________________________________________________1");

            htmlPage1 = (HtmlPage)htmlButton.click();
            // htmlPage1 = searchButton.click();
//            Thread.sleep(2000);
//            MyPrint.println("public reg form: "+pageRegistration.getFormByName("publicRegistrationForm"));
//            MyPrint.println("startingggggg");
            boolean hint=true;
            long start=System.currentTimeMillis();
//            HtmlForm form=null;
            HtmlImage imageReg2=null;
            while(hint) {
                try {
                    imageReg2=(HtmlImage) pageRegistration.getHtmlElementById(fbrScriptingData.getProperty("captchimage")).getByXPath("//img").get(4);
//                    form = pageRegistration.getFormByName("publicRegistrationForm");
                    hint=false;
                } catch (Exception e) {
//                    e.printStackTrace();
                }
            }
            long end=System.currentTimeMillis()-start;
            MyPrint.println("loaded in seconds:"+end/1000+" , in millis:"+end);
            MyPrint.println("Captacha Image Url---- "+imageReg2.getSrcAttribute());
            String url="https://iris.fbr.gov.pk"+imageReg2.getSrcAttribute();

            byte[] imageBytes = IOUtils.toByteArray(new URL(url));
            String base64 = Base64.getEncoder().encodeToString(imageBytes);

            responseBean.setCode(1);
            responseBean.setCaputreUrl(base64);
            responseBean.setMessage("Success");
            return responseBean;
        }catch(Exception e){
            e.printStackTrace();
            Logger4j.getLogger().error("Exception:",e);
        }
        return responseBean;
    }

    public  FbrCaputerCodeBean getFbrCaptureCodeFromFbrWebSite(String userId)throws Exception {
        MyPrint.println("user id------------------------------------"+userId);

        if(pageRegistrationList==null){
            pageRegistrationList=new HashMap<String,HtmlPage>();
        }
        FbrCaputerCodeBean fbr=new FbrCaputerCodeBean();

        System.getProperties().put("org.apache.commons.logging.simplelog.defaultlog", "debug");
        final WebClient webClient = new WebClient(BrowserVersion.CHROME);
        try {

            try {
                pageRegistration= (HtmlPage) webClient.getPage("https://iris.fbr.gov.pk/infosys/public/txplogin.xhtml");
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "Main URL :: https://iris.fbr.gov.pk/infosys/public/txplogin.xhtml has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                fbr.setCode(0);
                fbr.setMessage("Error");
                return fbr;
            }


            HtmlAnchor searchButton = null;

            try {
                searchButton =  (HtmlAnchor) pageRegistration.getHtmlElementById(fbrScriptingData.getProperty("addnewuserid"));
                htmlPage1 = searchButton.click();
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "Registrations Form ID :: " + fbrScriptingData.getProperty("addnewuserid")+ " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                fbr.setCode(0);
                fbr.setMessage("Error");
                return fbr;
            }

            boolean hint=true;
            long start=System.currentTimeMillis();
            HtmlImage imageReg=null;
            while(hint) {
                try {
                    imageReg=(HtmlImage) pageRegistration.getHtmlElementById(fbrScriptingData.getProperty("captchimage")).getByXPath("//img").get(4);
//                    form = pageRegistration.getFormByName("publicRegistrationForm");
                    hint=false;
                } catch (Exception e) {
//                    e.printStackTrace();
                }
            }
            long end=System.currentTimeMillis()-start;
            MyPrint.println("loaded in seconds:"+end/1000+" , in millis:"+end);

            HtmlForm form = null;
            try {
                form = pageRegistration.getFormByName(fbrScriptingData.getProperty("form"));
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "Registrations Form ID ::" + fbrScriptingData.getProperty("form") + "has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                fbr.setCode(0);
                fbr.setMessage("Error");
                return fbr;
            }

            try {
                HtmlSelect prefix = form.getSelectByName(fbrScriptingData.getProperty("prefix"));
                HtmlOption prefixOption = prefix.getOption(3);
                prefixOption.setSelected(true);
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "Prefix :: " + fbrScriptingData.getProperty("prefix") + " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                fbr.setCode(0);
                fbr.setMessage("Error");
                return fbr;
            }

            try {
                HtmlSelect select = (HtmlSelect) pageRegistration.getElementById(fbrScriptingData.getProperty("selectserviceprovider"));
                HtmlOption option = select.getOptionByValue("501");
                select.setSelectedAttribute(option, true);
                System.out.print("Get Selected Options:------- "+select.getSelectedOptions());
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " ,e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "Service Profider ::" + fbrScriptingData.getProperty("selectserviceprovider") + " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                fbr.setCode(0);
                fbr.setMessage("Error");
                return fbr;
            }

            try {
                HtmlInput cnic = form.getInputByName(fbrScriptingData.getProperty("cnic"));
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "CNIC :: " + fbrScriptingData.getProperty("cnic") + "has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                fbr.setCode(0);
                fbr.setMessage("Error");
                return fbr;
            }

            try {
                HtmlInput fname   = form.getInputByName(fbrScriptingData.getProperty("firstname"));
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "First Name :: " + fbrScriptingData.getProperty("firstname") + " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                fbr.setCode(0);
                fbr.setMessage("Error");
                return fbr;
            }

            try {
                HtmlInput lname = form.getInputByName(fbrScriptingData.getProperty("lastname"));
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "Last Name :: " + fbrScriptingData.getProperty("lastname") + " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                fbr.setCode(0);
                fbr.setMessage("Error");
                return fbr;
            }

            try {
                HtmlInput cellNo  = form.getInputByName(fbrScriptingData.getProperty("cellno"));
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "Cell No :: " + fbrScriptingData.getProperty("cellno") + " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                fbr.setCode(0);
                fbr.setMessage("Error");
                return fbr;
            }

            try {
                HtmlInput cellNoConfirm  = form.getInputByName(fbrScriptingData.getProperty("cellnoconfirm"));
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "Cell No Confirm :: " + fbrScriptingData.getProperty("cellnoconfirm") + " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                fbr.setCode(0);
                fbr.setMessage("Error");
                return fbr;
            }

            try {
                HtmlInput emailConfirm  = form.getInputByName(fbrScriptingData.getProperty("emailconfirm"));
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "Email Address Confirm :: " + fbrScriptingData.getProperty("emailconfirm")  + " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                fbr.setCode(0);
                fbr.setMessage("Error");
                return fbr;
            }

            try {
                HtmlInput email =   form.getInputByName(fbrScriptingData.getProperty("email"));
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "Email Address :: " +  fbrScriptingData.getProperty("email") + " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                fbr.setCode(0);
                fbr.setMessage("Error");
                return fbr;
            }

//            HtmlImage imageReg=(HtmlImage) pageRegistration.getHtmlElementById(fbrScriptingData.getProperty("captchimage")).getByXPath("//img").get(4);
            MyPrint.println("Captacha Image Url---- "+imageReg.getSrcAttribute());
            String url="https://iris.fbr.gov.pk"+imageReg.getSrcAttribute();
            byte[] imageBytes = IOUtils.toByteArray(new URL(url));
            String base64 = Base64.getEncoder().encodeToString(imageBytes);

            fbr.setCode(1);
            fbr.setCaputreUrl(base64);
            fbr.setMessage("Success");
            fbr.setToken(userId);
            pageRegistrationList.put(userId,pageRegistration);
            return fbr;

        } catch(Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
            String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
            CommonUtil.sendMail(emails, "FBR Registrations Error" ,  CommonUtil.getRootCause(e).getMessage());
            fbr.setCode(0);
            fbr.setMessage("Error");
            return fbr;
        }
    }

    public  FbrCaputerCodeBean submitFbrRegistration(FbrRegistrationBean bean)throws Exception{
        FbrCaputerCodeBean responseBean=new FbrCaputerCodeBean();
        responseBean.setCode(0);
        responseBean.setMessage("Error");
        String response="Error";
        MyPrint.println("......................submitFbrRegistration service is called.........................");
        HtmlButton htmlButton;
        System.getProperties().put("org.apache.commons.logging.simplelog.defaultlog", "debug");
        final WebClient webClient = new WebClient(BrowserVersion.CHROME);
        try {
            Thread.sleep(10000);

            HtmlForm form = null;
            try {
                System.out.println("after bfr registratinnn");
                form = pageRegistration.getFormByName(fbrScriptingData.getProperty("form"));
                System.out.println("form valuse is ");
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "Registration Form ::" + fbrScriptingData.getProperty("form") + " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                responseBean.setCode(0);
                responseBean.setMessage("Error");
                return responseBean;
            }


            HtmlOption prefixOption = null;
            try {
                HtmlSelect prefix = form.getSelectByName(fbrScriptingData.getProperty("prefix"));
                prefixOption = prefix.getOption(bean.getPrefix());
                prefixOption.setSelected(true);
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "Prefix :: " + fbrScriptingData.getProperty("prefix") + " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                responseBean.setCode(0);
                responseBean.setMessage("Error");
                return responseBean;
            }

            HtmlInput cnic = null;
            try {
                cnic = form.getInputByName(fbrScriptingData.getProperty("cnic"));
                cnic.setValueAttribute(bean.getCnic());
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "Cnic No :: " + fbrScriptingData.getProperty("cnic") + " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                responseBean.setCode(0);
                responseBean.setMessage("Error");
                return responseBean;
            }

            HtmlInput fname = null;
            try {
                fname = form.getInputByName(fbrScriptingData.getProperty("firstname"));
                fname.setValueAttribute(bean.getFirstName());
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "FirstName ::" + fbrScriptingData.getProperty("firstname") + " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                responseBean.setCode(0);
                responseBean.setMessage("Error");
                return responseBean;
            }


            HtmlInput lname = null;
            try {
                lname = form.getInputByName(fbrScriptingData.getProperty("lastname"));
                lname.setValueAttribute(bean.getLastName());
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "LastName ::" + fbrScriptingData.getProperty("lastname") + " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                responseBean.setCode(0);
                responseBean.setMessage("Error");
                return responseBean;
            }

            HtmlInput cellNo = null;
            try {
                cellNo = form.getInputByName(fbrScriptingData.getProperty("cellno"));
                cellNo.setValueAttribute(bean.getCellNo());
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "CellNo :: " + fbrScriptingData.getProperty("cellno") + "  has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                responseBean.setCode(0);
                responseBean.setMessage("Error");
                return responseBean;
            }

            HtmlInput cellNoConfirm = null;
            try {
                cellNoConfirm = form.getInputByName(fbrScriptingData.getProperty("cellnoconfirm"));
                cellNoConfirm.setValueAttribute(bean.getCellNo());
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "CellNoConfirm ::" + fbrScriptingData.getProperty("cellnoconfirm") + " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                responseBean.setCode(0);
                responseBean.setMessage("Error");
                return responseBean;
            }

            HtmlInput emailConfirm = null;
            try {
                emailConfirm  = form.getInputByName(fbrScriptingData.getProperty("emailconfirm"));
                emailConfirm.setValueAttribute(bean.getEmail());
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "EmailConfirm :: " + fbrScriptingData.getProperty("emailconfirm") + " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                responseBean.setCode(0);
                responseBean.setMessage("Error");
                return responseBean;
            }

            HtmlInput email = null;
            try {
                email =   form.getInputByName(fbrScriptingData.getProperty("email"));
                email.setValueAttribute(bean.getEmail());
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "Email :: " + fbrScriptingData.getProperty("email") + " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                responseBean.setCode(0);
                responseBean.setMessage("Error");
                return responseBean;
            }

            HtmlInput captchaValuee = null;
            try {
                captchaValuee =   form.getInputByName(fbrScriptingData.getProperty("captchavalue"));
                MyPrint.println("captcha value is:"+bean.getCaptcha());
                captchaValuee.setValueAttribute(bean.getCaptcha());
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "Set CaptchaValue ::" + fbrScriptingData.getProperty("captchavalue") + " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                responseBean.setCode(0);
                responseBean.setMessage("Error");
                return responseBean;
            }

            MyPrint.println("_______________________________________________________0");
            MyPrint.println("CNIC "+cnic.getValueAttribute());
            MyPrint.println("Prefix-- "+prefixOption.getValueAttribute());
            MyPrint.println("First Name --- "+fname.getValueAttribute());
            MyPrint.println("Last Name --- "+lname.getValueAttribute());
            /*MyPrint.println("Mobile --- "+mobileOption.getValueAttribute());*/
            MyPrint.println("Cell No --- "+cellNo.getValueAttribute());
            MyPrint.println("Email --- "+emailConfirm.getValueAttribute());

            HtmlImage imageReg = null;
            try {
                imageReg = (HtmlImage) pageRegistration.getHtmlElementById(fbrScriptingData.getProperty("captchimage")).getByXPath("//img").get(4);
                MyPrint.println("Captacha Image Url---- "+imageReg.getSrcAttribute());
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "Get CaptchaValue for SMS & EMAIL Code :: " + fbrScriptingData.getProperty("captchimage") + " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                responseBean.setCode(0);
                responseBean.setMessage("Error");
                return responseBean;
            }
            ///publicRegistrationForm:mainErrorMessages

            try {
                htmlButton = (HtmlButton) htmlPage1.getHtmlElementById(fbrScriptingData.getProperty("captchsubmit"));
                htmlPage1 = (HtmlPage)htmlButton.click();
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "Submit Registrations for without SMS & EMAIL Code :: " + fbrScriptingData.getProperty("captchsubmit") + " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                responseBean.setCode(0);
                responseBean.setMessage("Error");
                return responseBean;
            }

            try {
                long startTime = System.currentTimeMillis(); //fetch starting time
                while(false||(System.currentTimeMillis()-startTime)<10000) {
                    boolean loopBreak=false;
                    Thread.sleep(600);
                    List<DomElement> divs =pageRegistration.getElementsById(fbrScriptingData.getProperty("mesagecontainer"));
                    for(DomElement element :divs){
                        MyPrint.println("--------------------child element count"+element.getChildElementCount());
                        if(element.getChildElementCount()>0){
                            response=element.asText();
                            loopBreak=true;
                        }
                        MyPrint.println("----------::::::::::"+element.asText());
                    }
                    if(loopBreak==true) {
                        if(response.equals("Enter codes sent on SMS and email in the given fields.")){
                            responseBean.setCode(1);
                            responseBean.setMessage(response);
                            return responseBean;
                        }
                        else{
                            responseBean.setCode(0);
                            responseBean.setMessage(response);
                            return responseBean;
                        }
                    }  //Enter codes sent on SMS and email in the given fields.
                }
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "Main Error Message Box ::" + fbrScriptingData.getProperty("mesagecontainer") + " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                responseBean.setCode(0);
                responseBean.setMessage("Error");
                return responseBean;
            }


            /*MyPrint.println("_______________________________________________________2");
            HtmlTable table = (HtmlTable) htmlPage1.getElementsByTagName("table").get(1);
            List<HtmlTableRow> tableRowList = table.getRows();
            int r=0;

            for (HtmlTableRow row : tableRowList){
                int c=0;
                r++;
                for (HtmlTableCell cell : row.getCells()){
                    c++;
                    *//*if (cell.asText().trim() != null && !cell.asText().trim().equalsIgnoreCase(""))*//*
                    MyPrint.println(r+":"+c+":::::::----CellText " + cell.asText().trim() + "      ");
                }
                MyPrint.println();
            }*/


        }catch(Exception exe){
            exe.printStackTrace();
            Logger4j.getLogger().error("Exception:",exe);
            return responseBean;
        }
        return responseBean;
    }//end of method


    /////////////////////////////////////  FbrAndUserRegistration////////////////////////////////////

    public  Status submitFbrRegistration1(FbrAndUserRegistrationBean fbrAndUserRegistrationBean)throws Exception{

        Status responseBean=new Status();
        responseBean.setCode(0);
        responseBean.setMessage("Error");
        String response="Error";
        MyPrint.println("......................submitFbrRegistration service is called.........................");
        HtmlButton htmlButton;
        System.getProperties().put("org.apache.commons.logging.simplelog.defaultlog", "debug");
        final WebClient webClient = new WebClient(BrowserVersion.CHROME);
        try {
            Thread.sleep(10000);

            HtmlPage pageRegistration = pageRegistrationList.get(fbrAndUserRegistrationBean.getToken());


            HtmlForm form = null;
            try {
                form = pageRegistration.getFormByName(fbrScriptingData.getProperty("form"));
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "Registration Form :: " + fbrScriptingData.getProperty("form") + " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                responseBean.setCode(0);
                responseBean.setMessage("Error");
                return responseBean;
            }


            HtmlOption prefixOption = null;
            try {

                HtmlSelect prefix = form.getSelectByName(fbrScriptingData.getProperty("prefix"));
                prefixOption = prefix.getOption(fbrAndUserRegistrationBean.getUserDetail().getPrefix());
                prefixOption.setSelected(true);
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "Prefix ::" + fbrScriptingData.getProperty("prefix") + " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                responseBean.setCode(0);
                responseBean.setMessage("Error");
                return responseBean;
            }

            HtmlInput cnic = null;
            try {
                cnic = form.getInputByName(fbrScriptingData.getProperty("cnic"));
                System.out.println("++++++++++++++++++++++++++++++++++++++++++++ nic nic ++++++++++++++++"+cnic);
                cnic.setValueAttribute(fbrAndUserRegistrationBean.getUserDetail().getCnic());
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "Cnic No ::" + fbrScriptingData.getProperty("cnic") + " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                responseBean.setCode(0);
                responseBean.setMessage("Error");
                return responseBean;
            }

            HtmlInput fname = null;
            try {
                fname = form.getInputByName(fbrScriptingData.getProperty("firstname"));
                fname.setValueAttribute(fbrAndUserRegistrationBean.getUserDetail().getFirstName());
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "FirstName :: " + fbrScriptingData.getProperty("firstname") + " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                responseBean.setCode(0);
                responseBean.setMessage("Error");
                return responseBean;
            }


            HtmlInput lname = null;
            try {

                lname = form.getInputByName(fbrScriptingData.getProperty("lastname"));
                lname.setValueAttribute(fbrAndUserRegistrationBean.getUserDetail().getLastName());
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " ,e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "LastName ::" + fbrScriptingData.getProperty("lastname") + " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                responseBean.setCode(0);
                responseBean.setMessage("Error");
                return responseBean;
            }

            HtmlInput cellNo = null;
            try {
                cellNo = form.getInputByName(fbrScriptingData.getProperty("cellno"));
                cellNo.setValueAttribute(fbrAndUserRegistrationBean.getUserDetail().getMobileNo());
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "CellNo :: " + fbrScriptingData.getProperty("cellno") + " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                responseBean.setCode(0);
                responseBean.setMessage("Error");
                return responseBean;
            }

            HtmlInput cellNoConfirm = null;
            try {
                cellNoConfirm = form.getInputByName(fbrScriptingData.getProperty("cellnoconfirm"));
                cellNoConfirm.setValueAttribute(fbrAndUserRegistrationBean.getUserDetail().getMobileNo());
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "CellNoConfirm :: " + fbrScriptingData.getProperty("cellnoconfirm") + " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                responseBean.setCode(0);
                responseBean.setMessage("Error");
                return responseBean;
            }

            HtmlInput emailConfirm = null;
            try {
                emailConfirm  = form.getInputByName(fbrScriptingData.getProperty("emailconfirm"));
                emailConfirm.setValueAttribute(fbrAndUserRegistrationBean.getUserDetail().getEmail());
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "EmailConfirm :: " + fbrScriptingData.getProperty("emailconfirm") + " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                responseBean.setCode(0);
                responseBean.setMessage("Error");
                return responseBean;
            }

            HtmlInput email = null;
            try {
                email =   form.getInputByName(fbrScriptingData.getProperty("email"));
                email.setValueAttribute(fbrAndUserRegistrationBean.getUserDetail().getEmail());
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "Email :: " + fbrScriptingData.getProperty("email") + " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                responseBean.setCode(0);
                responseBean.setMessage("Error");
                return responseBean;


            }

            HtmlInput captchaValuee = null;
            try {
                captchaValuee =   form.getInputByName(fbrScriptingData.getProperty("captchavalue"));
                MyPrint.println("captcha value is:"+fbrAndUserRegistrationBean.getUserDetail().getCaptcha());
                captchaValuee.setValueAttribute(fbrAndUserRegistrationBean.getUserDetail().getCaptcha());
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "Set CaptchaValue :: " + fbrScriptingData.getProperty("captchavalue") + "has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                responseBean.setCode(0);
                responseBean.setMessage("Error");
                return responseBean;
            }
               /* FbrRegisteredUser fbrRegisteredUser = new FbrRegisteredUser();
                //fbrRegisteredUser
                fbrRegisteredUser.setFirstName(fbrAndUserRegistrationBean.getUserDetail().getFirstName());
                fbrRegisteredUser.setLastName(fbrAndUserRegistrationBean.getUserDetail().getLastName());
                fbrRegisteredUser.setCnic(fbrAndUserRegistrationBean.getUserDetail().getCnic());
                fbrRegisteredUser.setCellNo(fbrAndUserRegistrationBean.getUserDetail().getMobileNo());
                fbrRegisteredUser.setCaptcha(fbrAndUserRegistrationBean.getUserDetail().getCaptcha());
                fbrRegisteredUser.setEmail(fbrAndUserRegistrationBean.getUserDetail().getEmail());
                fbrRegisteredUser.setPrefix(fbrAndUserRegistrationBean.getUserDetail().getPrefix());
                fbrRegisteredUser.setServiceProvider("dfd");
                fbrRegisteredUser.setCurrentDae(CommonUtil.getCurrentTimestamp());

                //fbrRegisteredUser.setUser();
                //      fbrRegisteredUser.getPrefix();
                System.out.print("hello hre " + fbrRegisteredUser.getFirstName());

                fbrRegisterdUserServices.createFbrRegistredUser(fbrRegisteredUser);*/

            MyPrint.println("_______________________________________________________0");
            MyPrint.println("CNIC "+cnic.getValueAttribute());
            MyPrint.println("Prefix-- "+prefixOption.getValueAttribute());
            MyPrint.println("First Name --- "+fname.getValueAttribute());
            MyPrint.println("Last Name --- "+lname.getValueAttribute());
            /*MyPrint.println("Mobile --- "+mobileOption.getValueAttribute());*/
            MyPrint.println("Cell No --- "+cellNo.getValueAttribute());
            MyPrint.println("Email --- "+emailConfirm.getValueAttribute());

            HtmlImage imageReg = null;
            try {
                imageReg = (HtmlImage) pageRegistration.getHtmlElementById(fbrScriptingData.getProperty("captchimage")).getByXPath("//img").get(4);
                MyPrint.println("Captacha Image Url---- "+imageReg.getSrcAttribute());
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "Get CaptchaValue for SMS & EMAIL Code ::  " + fbrScriptingData.getProperty("captchimage") + " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                responseBean.setCode(0);
                responseBean.setMessage("Error");
                return responseBean;
            }
            ///publicRegistrationForm:mainErrorMessages

            try {
                htmlButton = (HtmlButton) htmlPage1.getHtmlElementById(fbrScriptingData.getProperty("captchsubmit"));
                htmlPage1 = (HtmlPage)htmlButton.click();
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "Submit Registrations for without SMS & EMAIL Code ::" + fbrScriptingData.getProperty("captchsubmit") + " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                responseBean.setCode(0);
                responseBean.setMessage("Error");
                return responseBean;
            }

            try {
                long startTime = System.currentTimeMillis(); //fetch starting time
                while(false||(System.currentTimeMillis()-startTime)<10000) {
                    boolean loopBreak=false;
                    Thread.sleep(600);
                    List<DomElement> divs =pageRegistration.getElementsById(fbrScriptingData.getProperty("mesagecontainer"));
                    for(DomElement element :divs){
                        MyPrint.println("--------------------child element count"+element.getChildElementCount());
                        if(element.getChildElementCount()>0){
                            response=element.asText();
                            loopBreak=true;
                        }
                        MyPrint.println("----------::::::::::"+element.asText());
                    }
                    if(loopBreak==true) {
                        if(response.equals("Enter codes sent on SMS and email in the given fields.")){
                            responseBean.setCode(1);
                            responseBean.setMessage(response);
                            return responseBean;
                        }
                        else{
                            responseBean.setCode(0);
                            responseBean.setMessage(response);
                            return responseBean;
                        }
                    }  //Enter codes sent on SMS and email in the given fields.
                }
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "FBR Registrations Error" , "Main Error Message Box ::" + fbrScriptingData.getProperty("mesagecontainer") + " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                responseBean.setCode(0);
                responseBean.setMessage("Error");
                return responseBean;
            }


            /*MyPrint.println("_______________________________________________________2");
            HtmlTable table = (HtmlTable) htmlPage1.getElementsByTagName("table").get(1);
            List<HtmlTableRow> tableRowList = table.getRows();
            int r=0;

            for (HtmlTableRow row : tableRowList){
                int c=0;
                r++;
                for (HtmlTableCell cell : row.getCells()){
                    c++;
                    *//*if (cell.asText().trim() != null && !cell.asText().trim().equalsIgnoreCase(""))*//*
                    MyPrint.println(r+":"+c+":::::::----CellText " + cell.asText().trim() + "      ");
                }
                MyPrint.println();
            }*/


        }catch(Exception exe){
            exe.printStackTrace();
            Logger4j.getLogger().error("Exception:",exe);
            return responseBean;
        }
        return responseBean;
    }//end of method

    private static void fetchConfig() {
        //This file contains the javax.mail config properties mentioned above.
        try  {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            fbrScriptingData.load(loader.getResourceAsStream("fbrScriptingData.properties"));
        }
        catch (IOException ex){
            ex.printStackTrace();
            Logger4j.getLogger().error("Exception:",ex);
            MyPrint.errln("response ");
        }
    }
    static {fetchConfig();}


    ///////////////////////////////////////End of FbrUserAndRegistration///////////////////////////////

    public  FbrCaputerCodeBean submitFbrRegistration2(FbrRegistrationBean bean)throws Exception{
        FbrCaputerCodeBean responseBean=new FbrCaputerCodeBean();
        responseBean.setCode(0);
        responseBean.setMessage("Error");

        String response="Error";
        MyPrint.println("......................submitFbrRegistration service is called.........................");
        HtmlButton htmlButton;
        System.getProperties().put("org.apache.commons.logging.simplelog.defaultlog", "debug");
        final WebClient webClient = new WebClient(BrowserVersion.CHROME);
        try{
            Thread.sleep(2000);

            HtmlForm form = null;
            try {
                form = pageRegistration.getFormByName(fbrScriptingData.getProperty("form"));
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "2.FBR Registrations Error" , "Registrations Page ::" + fbrScriptingData.getProperty("form") + "has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                responseBean.setCode(0);
                responseBean.setMessage("Error");
                return responseBean;
            }

            HtmlOption prefixOption = null;
            try {
                HtmlSelect prefix = form.getSelectByName(fbrScriptingData.getProperty("prefix"));
                prefixOption = prefix.getOption(bean.getPrefix());
                prefixOption.setSelected(true);
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "2.FBR Registrations Error" , "Prefix ::" + fbrScriptingData.getProperty("prefix") + " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                responseBean.setCode(0);
                responseBean.setMessage("Error");
                return responseBean;
            }

            HtmlInput cnic = null;
            try {
                cnic = form.getInputByName(fbrScriptingData.getProperty("cnic"));
                cnic.setValueAttribute(bean.getCnic());
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "2.FBR Registrations Error" , "CnicNo :: " + fbrScriptingData.getProperty("cnic") + " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                responseBean.setCode(0);
                responseBean.setMessage("Error");
                return responseBean;
            }


            HtmlInput testRegistrdAddress = null;
            try {
                testRegistrdAddress = form.getInputByName(fbrScriptingData.getProperty("registerdaddress"));
                testRegistrdAddress.setValueAttribute("");
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " ,e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "2.FBR Registrations Error" , "Registered Address :: " + fbrScriptingData.getProperty("registerdaddress")+ " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                responseBean.setCode(0);
                responseBean.setMessage("Error");
                return responseBean;
            }

            HtmlInput fname = null;
            try {
                fname = form.getInputByName(fbrScriptingData.getProperty("firstname"));
                fname.setValueAttribute(bean.getFirstName());
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                String[] emails = {"kiratkumar47@gmail.com", "sarfarazchohan311@gmail.com"};
                CommonUtil.sendMail(emails, "2.FBR Registrations Error" , "FirstName ::" + fbrScriptingData.getProperty("firstname") + " has been replaced :::: " + CommonUtil.getRootCause(e).getMessage());
                responseBean.setCode(0);
                responseBean.setMessage("Error");
                return responseBean;
            }

            HtmlInput lname = form.getInputByName(fbrScriptingData.getProperty("lastname"));
            lname.setValueAttribute(bean.getLastName());


            HtmlInput cellNo  = form.getInputByName(fbrScriptingData.getProperty("cellno"));
            cellNo.setValueAttribute(bean.getCellNo());

            HtmlInput cellNoConfirm  = form.getInputByName(fbrScriptingData.getProperty("cellnoconfirm"));
            cellNoConfirm.setValueAttribute(bean.getCellNo());

            HtmlInput emailConfirm  = form.getInputByName(fbrScriptingData.getProperty("emailconfirm"));
            emailConfirm.setValueAttribute(bean.getEmail());

            HtmlInput email =   form.getInputByName(fbrScriptingData.getProperty("email"));
            System.out.print("hi jetmal " + email.getValueAttribute());
            email.setValueAttribute(bean.getEmail());



            HtmlInput smsPin =   form.getInputByName(fbrScriptingData.getProperty("smspincode"));
            MyPrint.println("captcha value is:"+bean.getSmsPin());
            smsPin.setValueAttribute(bean.getSmsPin());

            HtmlInput emailPin =   form.getInputByName(fbrScriptingData.getProperty("emailpin"));
            MyPrint.println("captcha value is:"+bean.getEmailPin());
            emailPin.setValueAttribute(bean.getEmailPin());





            MyPrint.println("_______________________________________________________0");
            MyPrint.println("CNIC "+cnic.getValueAttribute());
            MyPrint.println("Prefix-- "+prefixOption.getValueAttribute());
            MyPrint.println("First Name --- "+fname.getValueAttribute());
            MyPrint.println("Last Name --- "+lname.getValueAttribute());
            /*MyPrint.println("Mobile --- "+mobileOption.getValueAttribute());*/
            MyPrint.println("Cell No --- "+cellNo.getValueAttribute());
            MyPrint.println("Email --- "+emailConfirm.getValueAttribute());


            htmlButton = (HtmlButton) htmlPage1.getHtmlElementById(fbrScriptingData.getProperty("submitcode1"));
            MyPrint.println("Button----- "+htmlButton);

            MyPrint.println("_______________________________________________________1");

            htmlPage1 = (HtmlPage)htmlButton.click();

            for(int i=1;i<=100;i++){
                Thread.sleep(600);
                boolean loopBreak=false;
                List<DomElement> divs =pageRegistration.getElementsById(fbrScriptingData.getProperty("mesagecontainer"));
//                List<DomElement> divs =pageRegistration.getElementsById("loginForm:cnfrmtionDlg");
                for(DomElement element :divs){
                    //
                    MyPrint.println("-------------------------::::::::::::::-------------"+element.asText());
                    MyPrint.println("--------------------child element count"+element.getChildElementCount());
                    if(element.getChildElementCount()>0){
                        responseBean.setCode(0);
                        responseBean.setMessage(element.asText());
                        response=element.asText();
                        loopBreak=true;
                    }
                    MyPrint.println("----------::::::::::"+element.asText());
                    MyPrint.println("-------test:"+element.getChildElements()+"   ------Div Id--------------------------------"+element.getId()+"--------------------------Elements:=---- "+element);
                }
                if(loopBreak==true){
                    return responseBean;
                    // break;
                }
            }//

            for(int i=1;i<=1;i++){
                Thread.sleep(600);
                boolean loopBreak=false;
                List<DomElement> divs =pageRegistration.getElementsById("loginForm:cnfrmtionDlg");
                for(DomElement element :divs){
                    //

                    MyPrint.println("confrmtionDialog is -------------------------::::::::::::::-------------"+element.asText());
                    MyPrint.println("--------------------child element count"+element.getChildElementCount());
                    if(element.getChildElementCount()>0){
                        responseBean.setCode(1);
                        responseBean.setMessage(element.asText());
                        response=element.asText();
                        loopBreak=true;
                    }
                    MyPrint.println("----------::::::::::"+element.asText());
                    MyPrint.println("-------test:"+element.getChildElements()+"   ------Div Id--------------------------------"+element.getId()+"--------------------------Elements:=---- "+element);
                }
                if(loopBreak==true)break;
            }


            MyPrint.println("_______________________________________________________2");
            HtmlTable table = (HtmlTable) htmlPage1.getElementsByTagName("table").get(1);
            List<HtmlTableRow> tableRowList = table.getRows();
            int r=0;

            for (HtmlTableRow row : tableRowList) {
                int c=0;
                r++;
                for (HtmlTableCell cell : row.getCells()){
                    c++;
                    /*if (cell.asText().trim() != null && !cell.asText().trim().equalsIgnoreCase(""))*/
                    MyPrint.println(r+":"+c+"::::::----CellText " + cell.asText().trim() + "      ");
                }
                MyPrint.println();
            }


        }catch(Exception exe){
            exe.printStackTrace();
            Logger4j.getLogger().error("Exception:",exe);
        }
        return responseBean;
    }//end of method
}//end of class

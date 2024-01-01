package com.arittek.befiler_services.test;

import com.arittek.befiler_services.beans.FbrBean;
import com.arittek.befiler_services.beans.FbrBusinessOrBranchesBean;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class FbrTest {
    public static void main(String args[]){
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


            /*System.out.println("Page Content ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::" + page.asText());*/

            HtmlSelect searchParameter = (HtmlSelect) page.getElementById("ctl00_ContentPlaceHolder1_ctrlOnlineSearch_ddlSearchType");
            /*System.out.println("Search Parameter ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::" + searchParameter);*/

            HtmlOption option = searchParameter.getOptionByValue("1");
            searchParameter.setSelectedAttribute(option, true);

            HtmlInput ntnNum1 = (HtmlInput) page.getHtmlElementById("ctl00_ContentPlaceHolder1_ctrlOnlineSearch_txtSearchText");
            ntnNum1.setValueAttribute("0003456");

            HtmlInput ntnNum2 = (HtmlInput) page.getHtmlElementById("ctl00_ContentPlaceHolder1_ctrlOnlineSearch_txtChkDigit");
            ntnNum2.setValueAttribute("2");

            System.out.println("Search Parameter ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::" + searchParameter.getSelectedOptions());
            System.out.println("NTN Number 1 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::" + ntnNum1);
            System.out.println("NTN Number 2 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::" + ntnNum2);

            HtmlAnchor searchButton = (HtmlAnchor) page.getHtmlElementById("ctl00_ContentPlaceHolder1_ctrlOnlineSearch_btnSearch");
            HtmlPage htmlPage1 = searchButton.click();


            HtmlSpan elements = page.getHtmlElementById("ctl00_ContentPlaceHolder1_ctrlOnlineSearch_lblMsg");
            System.out.println("Error Span ::::::::::" + elements.asText());



            HtmlTable table = (HtmlTable) htmlPage1.getElementsByTagName("table").get(0);
            List<HtmlTableRow> tableRowList = table.getRows();


            int r=0;

            for (HtmlTableRow row : tableRowList) {
                int c=0;
                r++;
                for (HtmlTableCell cell : row.getCells()){
                    c++;
                    /*if (cell.asText().trim() != null && !cell.asText().trim().equalsIgnoreCase(""))*/
                        System.out.println(r+":"+c+"::::::" + cell.asText().trim() + "      ");
                }
                System.out.println();
            }

            FbrBean fbrBean = new FbrBean();
            fbrBean.setNtnOrFtn(table.getRows().get(3).getCells().get(2).asText()+"");
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

            String branchListString =table.getRows().get(9).getCells().get(2).asText();
            StringTokenizer tokens=new StringTokenizer(branchListString,"\n");  //token for branch list

            tokens.nextElement();   //first token will be destroy bcz of heading name as like " Business/Branches"

            List<FbrBusinessOrBranchesBean>  fbrBusinessOrBranchesBeanList=new ArrayList<>();  //all branches will add in this list

            /* HERE FETCH EVERY BRANCH TOKEN AND THEN SET IN BEAN   */
            for (int i=1;i<=tokens.countTokens();i++){

                String branch=tokens.nextElement()+"";  //COMPLETE DATA OF EVERY BRANCH

                StringTokenizer branchTokens=new StringTokenizer(branch,"\t"); //"\t" FOR SEPERATE NESTED DATA

                FbrBusinessOrBranchesBean bean=new FbrBusinessOrBranchesBean();

                bean.setSerialNo(branchTokens.nextElement()+"");
                bean.setBusinessOrBranchName(branchTokens.nextElement()+"");
                bean.setBusinessOrBranchAddress(branchTokens.nextElement()+"");
                fbrBusinessOrBranchesBeanList.add(bean);
            }

            fbrBean.setFbrBusinessOrBranchesBeanList(fbrBusinessOrBranchesBeanList);

            System.out.println("TEST :::::::::::::::" +fbrBean);
            /*fbrBean.setCategory(table.getRow(4).getCell(4) + "");*/

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            webClient.close();
        }

    }
}

package com.arittek.befiler_services.services.active_taxpayer;


import com.arittek.befiler_services.beans.active_taxpayer.ActiveTaxpayerInquiryBean;
import com.arittek.befiler_services.model.active_taxpayer.ActiveTaxpayerInquiry;
import com.arittek.befiler_services.repository.active_taxpayer.ActiveTaxpayerInquiryRepository;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ActiveTaxpayerInquiryServiceImpl implements ActiveTaxpayerInquiryService {


    private ActiveTaxpayerInquiryRepository activeTaxpayerInquiryRepository;

    @Autowired
    ActiveTaxpayerInquiryServiceImpl(ActiveTaxpayerInquiryRepository activeTaxpayerInquiryRepository) {
        this.activeTaxpayerInquiryRepository = activeTaxpayerInquiryRepository;
    }

    Map<String, Object> headersVar = new HashMap<>();

    private String __VIEWSTATE = "";
    private String __EVENTVALIDATION = "";

    private String cookie;

    final String defaultUrl = "https://e.fbr.gov.pk/esbn/Service.aspx?PID=ghvHPOys6XuO4aRJWKLE+Q==";
    private URL url;

    public Map<String, Object> getCaptcha() throws Exception {

        Map<String, Object> captchaMap = new HashMap<>();

//      GET ATL Service...
        url = new URL(defaultUrl);
        try (final WebClient webClient = new WebClient()) {
            // Get the first page
            HtmlPage getPage1 = webClient.getPage(url);

//          getting cookie...
            cookie = "";
            String urlCookie = webClient.getCookies(url).toString();
            for (int a = 1; a <= urlCookie.length(); a++) {
                if (a >= 1 && a <= 42) {
                    cookie += urlCookie.charAt(a);
                } else {
                    break;
                }
            }
            captchaMap.put("webCookie", cookie);

            // Get the form that we are dealing with and within that form,
            final HtmlForm form = (HtmlForm) getPage1.getElementById("aspnetForm");
            __VIEWSTATE = form.getInputByName("__VIEWSTATE").getAttribute("value");
            __EVENTVALIDATION = form.getInputByName("__EVENTVALIDATION").getAttribute("value");

            headersVar.put("viewstate", __VIEWSTATE);
            headersVar.put("eventvalidation", __EVENTVALIDATION);

            String captchaSource = getPage1.getElementById("ctl00_ContentPlaceHolder1_capchaImage").getAttribute("src");

            captchaMap.put("captchaUrl", "https://e.fbr.gov.pk/esbn/" + captchaSource);

        } catch (Exception ex) {
            System.out.println("Exception Occur in get ATL URL" + ex);
        }

        return captchaMap;
    }

    public ActiveTaxpayerInquiryBean getATL(Map<String, Object> inputMap) throws Exception {
        if(__VIEWSTATE.equals("")==true || __EVENTVALIDATION.equals("")==true){
            return null;
        }

        ActiveTaxpayerInquiryBean activeTaxpayerInquiryBean = new ActiveTaxpayerInquiryBean();
        String registrationNo = (String) inputMap.get("registrationNo");
        String printCaptcha = (String) inputMap.get("printCaptcha");
        String webCookie = (String) inputMap.get("webCookie");
        try {
            Document doc2 = Jsoup.connect(url.toString())
                    .header("Cookie", webCookie)
                    .data("__EVENTTARGET", "")
                    .data("__EVENTARGUMENT", "")
                    .data("__VIEWSTATE", __VIEWSTATE)
                    .data("__VIEWSTATEGENERATOR", "CE521D22")
                    .data("__EVENTVALIDATION", __EVENTVALIDATION)
                    .data("ctl00$ContentPlaceHolder1$TXTS1004001", registrationNo)
                    .data("ctl00$ContentPlaceHolder1$DTPS1004002", "")
                    .data("ctl00$ContentPlaceHolder1$txtCapcha", printCaptcha)
                    .data("ctl00$ContentPlaceHolder1$btnVerify", "Verify")
                    .post();

            /*Document doc2 = Jsoup.connect(url.toString())
                    .header("Cookie", webCookie)
                    .data("__EVENTTARGET", "")
                    .data("__EVENTARGUMENT", "")
                    .data("__VIEWSTATE", headersVar.get("viewstate").toString())
                    .data("__VIEWSTATEGENERATOR", "CE521D22")
                    .data("__EVENTVALIDATION", headersVar.get("eventvalidation").toString())
                    .data("ctl00$ContentPlaceHolder1$TXTS1004001", registrationNo)
                    .data("ctl00$ContentPlaceHolder1$DTPS1004002", "")
                    .data("ctl00$ContentPlaceHolder1$txtCapcha", printCaptcha)
                    .data("ctl00$ContentPlaceHolder1$btnVerify", "Verify")
                    .post();*/

            Element docBody = doc2.body();

            System.out.println("BODY TEXT"+ docBody);

            String invalidCaptcha = docBody.getElementById("ctl00_ContentPlaceHolder1_lblStatus").text();
            if (invalidCaptcha.equals("") || invalidCaptcha == "") {

                String resultData = docBody.getElementById("ctl00_ContentPlaceHolder1_lblResults").text();
                if (resultData.equals("No record exists") || resultData == "No record exists") {
                    activeTaxpayerInquiryBean.setNoRecord(resultData);
                    return activeTaxpayerInquiryBean;

                } else {
                    int n = 0;
                    Elements tdContents = docBody.getElementById("ctl00_ContentPlaceHolder1_lblResults").getElementsByTag("td");
                    for (Element td : tdContents) {
                        if (n == 0) {
                            activeTaxpayerInquiryBean.setRegistrationNo(td.text());
                        } else if (n == 1) {
                            activeTaxpayerInquiryBean.setName(td.text());
                        } else if (n == 2) {
                            activeTaxpayerInquiryBean.setBusinessName(td.text());
                        } else if (n == 3) {
                            activeTaxpayerInquiryBean.setFilingStatus(td.text());
                        }
                        n++;
                    }
                    return activeTaxpayerInquiryBean;
                }
            } else {
                activeTaxpayerInquiryBean.setInvalidCaptcha(invalidCaptcha);
                return activeTaxpayerInquiryBean;
            }

        } catch (Exception e) {
            System.out.println("Exception occur in Image captcha:::::::" + e);
        }

        return null;
    }


    @Override
    public ActiveTaxpayerInquiry create(ActiveTaxpayerInquiry activeTaxpayerInquiry) throws Exception {
        if (activeTaxpayerInquiry != null) {
            return activeTaxpayerInquiryRepository.save(activeTaxpayerInquiry);
        }
        return null;
    }

    @Override
    public Boolean registrationNoIsExist(String registrationNo) throws Exception {
        if (registrationNo != null) {
            ActiveTaxpayerInquiry ActiveTaxpayerInquiry = activeTaxpayerInquiryRepository.findOneByRegistrationNo(registrationNo);
            if(ActiveTaxpayerInquiry == null)
                return false;
            else
                return true;
        }
        return null;
    }

    @Override
    public ActiveTaxpayerInquiry update(ActiveTaxpayerInquiry activeTaxpayerInquiry) throws Exception {
        return null;
    }

    @Override
    public List<ActiveTaxpayerInquiry> findAll() throws Exception {
        return null;
    }
}

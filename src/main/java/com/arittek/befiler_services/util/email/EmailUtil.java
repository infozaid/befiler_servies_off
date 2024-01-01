package com.arittek.befiler_services.util.email;

import com.arittek.befiler_services.model.taxform.Taxform;
import com.arittek.befiler_services.model.user.User;

public class EmailUtil {

    public static String urlLogo = "<image src= \\\"https://befiler.com/img/logo-befiler.png\\\" height=\\\"80\\\" width=\\\"200\\\"/>";

    public static String emailTemaplate(String url, String firstName) throws Exception {

        String str = "\n" +
                "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>\n" +
                "<html xmlns='http://www.w3.org/1999/xhtml'>\n" +
                "<head>\n" +
                " \n" +
                "<meta name='viewport' content='width=device-width'/>\n" +
                "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>\n" +
                "<style>\n" +
                "\n" +
                "* { \n" +
                "\tmargin:0;\n" +
                "\tpadding:0;\n" +
                "}\n" +
                "* { font-family: 'Helvetica Neue', 'Helvetica', Helvetica, Arial, sans-serif; }\n" +
                "\n" +
                "img { \n" +
                "\tmax-width: 12%; \n" +
                "}\n" +
                ".collapse {\n" +
                "\tmargin:0;\n" +
                "\tpadding:0;\n" +
                "}\n" +
                "body {\n" +
                "\t-webkit-font-smoothing:antialiased; \n" +
                "\t-webkit-text-size-adjust:none; \n" +
                "\twidth: 100%!important; \n" +
                "\theight: 100%;\n" +
                "}\n" +
                "\n" +
                "\n" +
                "a { color: #2BA6CB;}\n" +
                "\n" +
                ".btn {\n" +
                "\ttext-decoration:none;\n" +
                "\tcolor: #FFF;\n" +
                "\tbackground-color: #666;\n" +
                "\tpadding:10px 16px;\n" +
                "\tfont-weight:bold;\n" +
                "\tmargin-right:10px;\n" +
                "\ttext-align:center;\n" +
                "\tcursor:pointer;\n" +
                "\tdisplay: inline-block;\n" +
                "}\n" +
                "\n" +
                "p.callout {\n" +
                "\tpadding:15px;\n" +
                "\tbackground-color:#ECF8FF;\n" +
                "\tmargin-bottom: 15px;\n" +
                "}\n" +
                ".callout a {\n" +
                "\tfont-weight:bold;\n" +
                "\tcolor: #2BA6CB;\n" +
                "}\n" +
                "\n" +
                "table.social {\n" +
                "/* \tpadding:15px; */\n" +
                "\tbackground-color: #253340;\n" +
                "\tcolor:#fff;\n" +
                "\t\n" +
                "}\n" +
                "\n" +
                ".social h1,.social h2, .social h5{\n" +
                "\tcolor:#fff;\n" +
                "}\n" +
                ".social .soc-btn {\n" +
                "\tpadding: 3px 7px;\n" +
                "\tfont-size:12px;\n" +
                "\tmargin-bottom:10px;\n" +
                "\ttext-decoration:none;\n" +
                "\tcolor: #FFF;font-weight:bold;\n" +
                "\tdisplay:block;\n" +
                "\ttext-align:center;\n" +
                "}\n" +
                "a.fb { background-color: #3B5998!important; }\n" +
                "a.tw { background-color: #1daced!important; }\n" +
                "a.gp { background-color: #0077B5!important; }\n" +
                "a.ms { background-color: #000!important; }\n" +
                "\n" +
                ".sidebar .soc-btn { \n" +
                "\tdisplay:block;\n" +
                "\twidth:100%;\n" +
                "}\n" +
                "\n" +
                "\n" +
                "table.head-wrap { width: 100%;}\n" +
                "\n" +
                ".header.container table td.logo { padding: 15px; }\n" +
                ".header.container table td.label { padding: 15px; padding-left:0px;}\n" +
                "\n" +
                "\n" +
                "table.body-wrap { width: 100%;}\n" +
                "\n" +
                "\n" +
                "table.footer-wrap { width: 100%;\tclear:both!important;\n" +
                "}\n" +
                ".footer-wrap .container td.content  p { border-top: 1px solid rgb(215,215,215); padding-top:15px;}\n" +
                ".footer-wrap .container td.content p {\n" +
                "\tfont-size:10px;\n" +
                "\tfont-weight: bold;\n" +
                "\t\n" +
                "}\n" +
                "\n" +
                "\n" +
                "h1,h2,h3,h4,h5,h6 {\n" +
                "font-family: 'HelveticaNeue-Light', 'Helvetica Neue Light', 'Helvetica Neue', Helvetica, Arial, 'Lucida Grande', sans-serif; line-height: 1.1; margin-bottom:15px; color:#000;\n" +
                "}\n" +
                "h1 small, h2 small, h3 small, h4 small, h5 small, h6 small { font-size: 60%; color: #6f6f6f; line-height: 0; text-transform: none; }\n" +
                "\n" +
                "h1 { font-weight:200; font-size: 44px;}\n" +
                "h2 { font-weight:200; font-size: 37px;}\n" +
                "h3 { font-weight:500; font-size: 27px;}\n" +
                "h4 { font-weight:500; font-size: 23px;}\n" +
                "h5 { font-weight:900; font-size: 17px;}\n" +
                "h6 { font-weight:900; font-size: 14px; text-transform: uppercase; color:#444;}\n" +
                "\n" +
                ".collapse { margin:0!important;}\n" +
                "\n" +
                "p, ul { \n" +
                "\tmargin-bottom: 10px; \n" +
                "\tfont-weight: normal; \n" +
                "\tfont-size:14px; \n" +
                "\tline-height:1.6;\n" +
                "}\n" +
                "p.lead { font-size:17px; }\n" +
                "p.last { margin-bottom:0px;}\n" +
                "\n" +
                "ul li {\n" +
                "\tmargin-left:5px;\n" +
                "\tlist-style-position: inside;\n" +
                "}\n" +
                "\n" +
                "ul.sidebar {\n" +
                "\tbackground:#ebebeb;\n" +
                "\tdisplay:block;\n" +
                "\tlist-style-type: none;\n" +
                "}\n" +
                "ul.sidebar li { display: block; margin:0;}\n" +
                "ul.sidebar li a {\n" +
                "\ttext-decoration:none;\n" +
                "\tcolor: #666;\n" +
                "\tpadding:10px 16px;\n" +
                "/* \tfont-weight:bold; */\n" +
                "\tmargin-right:10px;\n" +
                "/* \ttext-align:center; */\n" +
                "\tcursor:pointer;\n" +
                "\tborder-bottom: 1px solid #777777;\n" +
                "\tborder-top: 1px solid #FFFFFF;\n" +
                "\tdisplay:block;\n" +
                "\tmargin:0;\n" +
                "}\n" +
                "ul.sidebar li a.last { border-bottom-width:0px;}\n" +
                "ul.sidebar li a h1,ul.sidebar li a h2,ul.sidebar li a h3,ul.sidebar li a h4,ul.sidebar li a h5,ul.sidebar li a h6,ul.sidebar li a p { margin-bottom:0!important;}\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                ".container {\n" +
                "\tdisplay:block!important;\n" +
                "\n" +
                "\tmargin:0 auto!important; /* makes it centered */\n" +
                "\tclear:both!important;\n" +
                "}\n" +
                "\n" +
                ".content {\n" +
                "\tpadding:15px;\n" +
                "\n" +
                "\tmargin:0 auto;\n" +
                "\tdisplay:block; \n" +
                "}\n" +
                "\n" +
                ".content table { width: 100%; }\n" +
                "\n" +
                "\n" +
                ".column {\n" +
                "\twidth: 300px;\n" +
                "\tfloat:left;\n" +
                "}\n" +
                ".column tr td { padding: 15px; }\n" +
                ".column-wrap { \n" +
                "\tpadding:0!important; \n" +
                "\tmargin:0 auto; \n" +
                "\n" +
                "}\n" +
                ".column table { width:100%;}\n" +
                ".social .column {\n" +
                "\twidth: 280px;\n" +
                "\tmin-width: 279px;\n" +
                "\tfloat:left;\n" +
                "}\n" +
                ".btn-mail {\n" +
                " font-weight: bold;\n" +
                " color: #fff !important;\n" +
                " background-color: #2BA6CB;\n" +
                " padding: 5px 25px;\n" +
                " text-decoration: none;\n" +
                "}\n" +
                "\n" +
                ".clear { display: block; clear: both; }\n" +
                "\n" +
                "\n" +
                "@media only screen and (max-width: 600px) {\n" +
                "\t\n" +
                "\ta[class='btn'] { display:block!important; margin-bottom:10px!important; background-image:none!important; margin-right:0!important;}\n" +
                "\n" +
                "\tdiv[class='column'] { width: auto!important; float:none!important;}\n" +
                "\t\n" +
                "\ttable.social div[class='column'] {\n" +
                "\t\twidth:auto!important;\n" +
                "\t}\n" +
                "\n" +
                "}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body bgcolor='#FFFFFF'>\n" +
                " \n" +
                "<table class='head-wrap' bgcolor='#393939'>\n" +
                "<tr>\n" +
                "<td></td>\n" +
                "<td class='header container'>\n" +
                "<div class='content'>\n" +
                "<table>\n" +
                "<tr>\n" +
                "<td><img src=\"https://befiler.com/img/email.png\"/></td>\n" +
                "\n" +
                "</tr>\n" +
                "</table>\n" +
                "</div>\n" +
                "</td>\n" +
                "<td></td>\n" +
                "</tr>\n" +
                "</table> \n" +
                " \n" +
                "<table class='body-wrap'>\n" +
                "<tr>\n" +
                "<td></td>\n" +
                "<td class='container' bgcolor='#FFFFFF'>\n" +
                "<div class='content'>\n" +
                "<table>\n" +
                "<tr>\n" +
                "<td style='text-align: justify;'> \n" +
                "<h3>Hello, " + firstName + "</h3>\n" +
                "<p class='lead'>Thank you for signing up with <a href='https://befiler.com/' target='_blank'>BeFiler</a>. Pakistan&#39;s first online tax return preparation software. Whether you are looking to obtain NTN or filing your tax return, you&#39;ll find BeFiler.com to be most convenient and cost effective with following advantages:.</p>\n" +
                /*"the past or looking to file them for the first time, you&#39;ll find that BeFiler offers a\n" +
                "great way to file them in the most simplified manner.</p>\n" +*/
                "<ul>\n" +
                "<li>Easily accessible anywhere anytime</li>\n" +
                "<li>Accurate and up-to-date tax calculation</li>\n" +
                "<li>Online Tax Review of the Tax Return before filing</li>\n" +
                "<li>Claim all the possible tax deductions and credits</li>\n" +
                "</ul>" +
                " \n" +
                "<p >Have any questions? Just shoot us an <a href='#'>email!</a> We’re always here to help.</p> \n" +
                " \n" +
                "<table class='social' width='100%'>\n" +
                "<tr>\n" +
                "<td>\n" +
                " \n" +
                "<table align='left' class='column'>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<h5 class=''>Connect with Us:</h5>\n" +
                "<p class=''><a href='https://www.facebook.com/Befiler.pk/?ref=br_rs'  target='_blank' class='soc-btn fb'>Facebook</a>\n" +
                /*" <a href='https://twitter.com'  target='_blank' class='soc-btn tw'>Twitter</a> \n" +*/
                " <a href='https://www.linkedin.com/company/befiler/'  target='_blank' class='soc-btn gp'>Linkedin</a></p>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table> \n" +
                " \n" +
                "<table align='left' class='column'>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<h5 class=''>Contact Info:</h5>\n" +
                "<p>Phone: <strong>(021) 35303294-6</strong><br/>\n" +
                "Email: <strong><a href='mailto:info@befiler.com'>info@befiler.com</a></strong></p>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table> \n" +
                "<span class='clear'></span>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table> \n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</div> \n" +
                "</td>\n" +
                "<td></td>\n" +
                "</tr>\n" +
                "</table> \n" +
                " \n" +
                "<table class='footer-wrap'>\n" +
                "<tr>\n" +
                "<td></td>\n" +
                "<td class='container'>\n" +
                " \n" +
                "<div class='content'>\n" +
                "<table>\n" +
                "<tr>\n" +
                "<td align='center'>\n" +
                "\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</div> \n" +
                "</td>\n" +
                "<td></td>\n" +
                "</tr>\n" +
                "</table> \n" +
                "</body>\n" +
                "</html>";

       /* File htmlTemplateFile = new File("emailTemplate.html");
        String htmlString = FileUtils.readFileToString(htmlTemplateFile);
        System.out.print("html String " +htmlString);*/

       /* String title = "New Page";
        String body = "This is Body";
        htmlString = htmlString.replace("$title", title);
        htmlString = htmlString.replace("$body", body);
        File newHtmlFile = new File("path/new.html");
        FileUtils.writeStringToFile(newHtmlFile, htmlString);
*/
        return str;
    }

    public static String paymentTemaplateForFinja(String name, String payment, String orderId, String transactionId, String description) throws Exception {
        String str = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<style>\n" +
                "table, th, td {\n" +
                "    border-collapse: collapse;\n" +
                "    \n" +
                "}\n" +
                "th, td {\n" +
                "    padding: 5px;\n" +
                "    text-align: left;\n" +
                "}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "Dear " + name + ",<br><br>\n" +
                "We have successfully received your payment thru Finja of PKR " + payment + ". \n" +
                "<br><br>\n" +
                "\n" +
                "Following is your transaction summary:<br><br>\n" +
                "\n" +
                "<table style=\"width:100%\">\n" +
                "  <tr>\n" +
                "    <th>Order ID: :</th>\n" +
                "    <td>" + orderId + "</td>\n" +
                "  </tr>\n" +
                "  <tr>\n" +
                "    <th>Transaction ID:</th>\n" +
                "    <td>" + transactionId + "</td>\n" +
                "  </tr>\n" +
                "    <tr>\n" +
                "    <th>Description:</th>\n" +
                "    <td>" + description + "</td>\n" +
                "  </tr>\n" +
                "</table>\n" +
                "\n" +
                "<br><br>\n" +
                "Yours Sincerely,<br><br>\n" +
                "Befiler.com\n" +
                "\n" +
                "</body>\n" +
                "</html>\n";
        return str;
    }

    public static String paymentTemaplateForEasypaisaIPNHandler(String name, String payment, String orderId, String transactionId, String description) throws Exception {
        String str = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<style>\n" +
                "table, th, td {\n" +
                "    border-collapse: collapse;\n" +
                "    \n" +
                "}\n" +
                "th, td {\n" +
                "    padding: 5px;\n" +
                "    text-align: left;\n" +
                "}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "Dear " + name + ",<br><br>\n" +
                "We have successfully received your payment thru Easypaisa of PKR " + payment + ". \n" +
                "<br><br>\n" +
                "\n" +
                "Following is your transaction summary:<br><br>\n" +
                "\n" +
                "<table style=\"width:100%\">\n" +
                "  <tr>\n" +
                "    <th>Order ID: :</th>\n" +
                "    <td>" + orderId + "</td>\n" +
                "  </tr>\n" +
                "  <tr>\n" +
                "    <th>Transaction ID:</th>\n" +
                "    <td>" + transactionId + "</td>\n" +
                "  </tr>\n" +
                "    <tr>\n" +
                "    <th>Description:</th>\n" +
                "    <td>" + description + "</td>\n" +
                "  </tr>\n" +
                "</table>\n" +
                "\n" +
                "<br><br>\n" +
                "Yours Sincerely,<br><br>\n" +
                "Befiler.com\n" +
                "\n" +
                "</body>\n" +
                "</html>\n";
        return str;
    }

    public static String paymentTemaplateForPromoCode(String name, String payment, String orderId, String description) throws Exception {
        String str = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<style>\n" +
                "table, th, td {\n" +
                "    border-collapse: collapse;\n" +
                "    \n" +
                "}\n" +
                "th, td {\n" +
                "    padding: 5px;\n" +
                "    text-align: left;\n" +
                "}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "Dear " + name + ",<br><br>\n" +
                "We have successfully received your payment of PKR " + payment + ". \n" +
                "<br><br>\n" +
                "\n" +
                "Following is your transaction summary:<br><br>\n" +
                "\n" +
                "<table style=\"width:100%\">\n" +
                "  <tr>\n" +
                "    <th>Order ID: :</th>\n" +
                "    <td>" + orderId + "</td>\n" +
                "  </tr>\n" +
                "    <tr>\n" +
                "    <th>Description:</th>\n" +
                "    <td>" + description + "</td>\n" +
                "  </tr>\n" +
                "</table>\n" +
                "\n" +
                "<br><br>\n" +
                "Yours Sincerely,<br><br>\n" +
                "Befiler.com\n" +
                "\n" +
                "</body>\n" +
                "</html>\n";
        return str;
    }

    public static String paymentTemaplate(String name, String payment, String orderId, String transactionId, String approval, String description) throws Exception {
        String str = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<style>\n" +
                "table, th, td {\n" +
                "    border-collapse: collapse;\n" +
                "    \n" +
                "}\n" +
                "th, td {\n" +
                "    padding: 5px;\n" +
                "    text-align: left;\n" +
                "}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "Dear " + name + ",<br><br>\n" +
                "We have successfully received your payment of PKR " + payment + ". \n" +
                "<br><br>\n" +
                "\n" +
                "Following is your transaction summary:<br><br>\n" +
                "\n" +
                "<table style=\"width:100%\">\n" +
                "  <tr>\n" +
                "    <th>Order ID: :</th>\n" +
                "    <td>" + orderId + "</td>\n" +
                "  </tr>\n" +
                "  <tr>\n" +
                "    <th>Transaction ID:</th>\n" +
                "    <td>" + transactionId + "</td>\n" +
                "  </tr>\n" +
                "  <tr>\n" +
                "    <th>Approval:</th>\n" +
                "    <td>" + approval + "</td>\n" +
                "  </tr>\n" +
                "    <tr>\n" +
                "    <th>Description:</th>\n" +
                "    <td>" + description + "</td>\n" +
                "  </tr>\n" +
                "</table>\n" +
                "\n" +
                "<br><br>\n" +
                "Yours Sincerely,<br><br>\n" +
                "Befiler.com\n" +
                "\n" +
                "</body>\n" +
                "</html>\n";
        return str;
    }
    public static String paymentTemaplateForKeenu(String name, String payment, String orderId, String transactionId, String description) throws Exception {
        String str = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<style>\n" +
                "table, th, td {\n" +
                "    border-collapse: collapse;\n" +
                "    \n" +
                "}\n" +
                "th, td {\n" +
                "    padding: 5px;\n" +
                "    text-align: left;\n" +
                "}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "Dear " + name + ",<br><br>\n" +
                "We have successfully received your payment of PKR " + payment + ". \n" +
                "<br><br>\n" +
                "\n" +
                "Following is your transaction summary:<br><br>\n" +
                "\n" +
                "<table style=\"width:100%\">\n" +
                "  <tr>\n" +
                "    <th>Order ID: :</th>\n" +
                "    <td>" + orderId + "</td>\n" +
                "  </tr>\n" +
                "  <tr>\n" +
                "    <th>Transaction ID:</th>\n" +
                "    <td>" + transactionId + "</td>\n" +
                "  </tr>\n" +
                "    <tr>\n" +
                "    <th>Description:</th>\n" +
                "    <td>" + description + "</td>\n" +
                "  </tr>\n" +
                "</table>\n" +
                "\n" +
                "<br><br>\n" +
                "Yours Sincerely,<br><br>\n" +
                "Befiler.com\n" +
                "\n" +
                "</body>\n" +
                "</html>\n";
        return str;
    }
    public static String paymentTemaplateForApps(String name, String payment, String orderId, String transactionId, String description) throws Exception {
        String str = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<style>\n" +
                "table, th, td {\n" +
                "    border-collapse: collapse;\n" +
                "    \n" +
                "}\n" +
                "th, td {\n" +
                "    padding: 5px;\n" +
                "    text-align: left;\n" +
                "}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "Dear " + name + ",<br><br>\n" +
                "We have successfully received your payment of PKR " + payment + ". \n" +
                "<br><br>\n" +
                "\n" +
                "Following is your transaction summary:<br><br>\n" +
                "\n" +
                "<table style=\"width:100%\">\n" +
                "  <tr>\n" +
                "    <th>Order ID: :</th>\n" +
                "    <td>" + orderId + "</td>\n" +
                "  </tr>\n" +
                "  <tr>\n" +
                "    <th>Transaction ID:</th>\n" +
                "    <td>" + transactionId + "</td>\n" +
                "  </tr>\n" +
                "    <tr>\n" +
                "    <th>Description:</th>\n" +
                "    <td>" + description + "</td>\n" +
                "  </tr>\n" +
                "</table>\n" +
                "\n" +
                "<br><br>\n" +
                "Yours Sincerely,<br><br>\n" +
                "Befiler.com\n" +
                "\n" +
                "</body>\n" +
                "</html>\n";
        return str;
    }

    public static String paymentTemaplateForEasypaisaRedirect(String name, String payment, String orderId, String description) throws Exception {
        String str = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<style>\n" +
                "table, th, td {\n" +
                "    border-collapse: collapse;\n" +
                "    \n" +
                "}\n" +
                "th, td {\n" +
                "    padding: 5px;\n" +
                "    text-align: left;\n" +
                "}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "Dear " + name + ",<br><br>\n" +
                "We have successfully received your payment of PKR " + payment + ". \n" +
                "<br><br>\n" +
                "\n" +
                "Following is your transaction summary:<br><br>\n" +
                "\n" +
                "<table style=\"width:100%\">\n" +
                "  <tr>\n" +
                "    <th>Order ID: :</th>\n" +
                "    <td>" + orderId + "</td>\n" +
                "  </tr>\n" +
                "    <tr>\n" +
                "    <th>Description:</th>\n" +
                "    <td>" + description + "</td>\n" +
                "  </tr>\n" +
                "</table>\n" +
                "\n" +
                "<br><br>\n" +
                "Yours Sincerely,<br><br>\n" +
                "Befiler.com\n" +
                "\n" +
                "</body>\n" +
                "</html>\n";
        return str;
    }

    public static String pinCodeTemplete(User user, String autoCode) throws Exception {
        String str = "" +
                "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<title>Page Title</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "Dear " + user.getFullName() + "<br><br>\n" +
                "We noticed unauthorized login attempts to your account so your account has been blocked for security purpose.<br>Following is the Pin Code to activate your account:<br><br>\n" +
                "<b><font color=\"blue\">" + autoCode + "<font></b><br><br>\n" +
                "Yours Sincerely,<br><br>\n" +
                "Befiler \n" +
                "\n" +
                "</body>\n" +
                "</html>";

        return str;
    }

    public static String tempPasswordTemplete(User user, String autoCode) throws Exception {
        String str = "" +
                "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<title>Page Title</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "Dear " + user.getFullName() + "<br><br>\n" +
                "You recently requested to have your password reset for your online account at Befiler.com. Please find your new password below:<br><br>\n" +
                "<b><font color=\"blue\">" + autoCode + "<font></b><br><br>\n" +
                "Please make sure that you login and access the 'Change Password' page in settings to change your password to one that you can easily remember. <br><br>\n" +
                "Yours Sincerely,<br><br>\n" +
                "Befiler \n" +
                "\n" +
                "</body>\n" +
                "</html>";

        return str;
    }

    public static String forgotPasswordTemplete(String url, User user) throws Exception {
        String str = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<title>Page Title</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "We received a request to reset the password associated with this e-mail address. If you made this request, please follow the instructions below.<br><br> \n" +
                "Click the link below to reset your password:" +

                "<br><br>\n" +
                "<a href=\"" + url + "?id=" + user.getId() + "&token=" + user.getPassword() + "\">" + url + "?id=" + user.getId() + "&token=" + user.getPassword() + "</a> " +
                "\n" +
                "<br><br>" +

                "If you did not request to have your password reset you can safely ignore this email. Rest assured your customer account is safe.<br><br> \n" +
                "If clicking the link doesn't seem to work, you can copy and paste the link into your browser's address window, or retype it there.<br><br> \n" +
                "Befiler.com will never e-mail you and ask you to disclose or verify your Befiler.com password, or credit card details. " +
                "<br>If you receive a suspicious e-mail with a link to update your account information, do not click on the link." +
                "<br>Instead, report the e-mail to info@befiler.com for investigation. Thanks for visiting Befiler.com! \n" +
                "</body>\n" +
                "</html>\n";
        return str;
    }


    public static String reminderEmailForFbrAfterThreeDays(User user) throws Exception {
        String str = " <!DOCTYPE html>   \n" +
                "<html>   \n" +
                "<head>   \n" +
                "<title>Page Title</title>   \n" +
                "</head>   \n" +
                "<body>   \n" +
                "\n" +
                "Dear "+user.getFullName()+",<br><br>   \n" +
                "We hope that you are doing well. Our system has noticed that you have entered the information required for filing your tax return. " +
                "<br>However, you have not provided us with the credentials required to login to FBR’s tax filing system - IRIS.<br><br>    \n" +
                "We wanted to know if you had any questions, challenges or concerns in connection with your FBR credentials. If so, please let us know, and our representative will get in touch with you to address your queries.<br><br>    \n" +
                "Have a great day!<br><br>   \n" +
              /*  "\n" +
                "<font color=\"#808080\">\n" +
                "Customer Services<br><br>   \n" +
                "\n" +
                "<image src=https://befiler.com/img/logo-befiler.png  height= 80  width= 200/><br>   \n" +
                "\n" +
                "2nd Floor, Plot No. 32-C Lane 4,<br>    \n" +
                "Khayaban-e-Shahbaz, DHA Phase 6<br>   \n" +
                "Karachi, Pakistan<br>   \n" +
                "\n" +
                "</font>\n" +
                "<a href=https://www.investomate.com/>www.investomate.com</a>   \n" +
                "\n" +*/
                "</body>   \n" +
                "</html> ";
        return str;

    }


    public static String reminderEmailForPaymentAfterThreeDays(User user) throws Exception {
        String str = "" +
                "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<title>Page Title</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "Dear "+user.getFullName()+",<br><br>\n" +
                "We hope that you are doing well. Our system has noticed that you have entered the information required for filing your tax return. " +
                "<br>However, you have not made the payment yet.<br><br> \n" +
                "We wanted to know if you had any questions, challenges or concerns in connection with making the payment. " +
                "If so, please let us know, and our representative will get in touch with you to address your queries.<br><br> \n" +
                "Have a great day!<br><br>\n" +
                /*"\n" +
                "<font color=\"#808080\">\n" +
                "Customer Services<br>\n" +
                "\n" +
                "<image src= \"https://befiler.com/img/logo-befiler.png\" height=\"80\" width=\"200\"/><br>\n" +
                " \n" +
                "2nd Floor, Plot No. 32-C Lane 4,<br> \n" +
                "Khayaban-e-Shahbaz, DHA Phase 6<br>\n" +
                "Karachi, Pakistan<br></font>\n" +
                "<a href=\"https://www.investomate.com/\">www.investomate.com</a>\n" +
                "\n" +*/
                "</body>\n" +
                "</html>\n";
        return str;
    }

    public static String emailAfterOneDayOfReceiptPayment(User user) throws Exception {
        String str = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<title>Page Title</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "Dear "+user.getFullName()+",<br><br>\n" +
                "Thank you for allowing us the opportunity to handle your tax return. We have received your payment and your case is currently being reviewed. <br>As a result of this review, we may require some additional information from you. We’ll keep you posted about the progress and will let you know once your return has been successfully filed.<br><br>  \n" +
                "Please let us know if you have any comments or questions regarding our service.\n" +
                "<br><br> \n" +
                "Have a great day!<br><br>\n" +
                /*"\n" +
                "<font color=\"#808080\">\n" +
                "Customer Services<br>\n" +
                "\n" +
                "<image src= \"https://befiler.com/img/logo-befiler.png\" height=\"80\" width=\"200\"/><br>\n" +
                " \n" +
                "2nd Floor, Plot No. 32-C Lane 4,<br> \n" +
                "Khayaban-e-Shahbaz, DHA Phase 6<br>\n" +
                "Karachi, Pakistan<br></font>\n" +
                "<a href=\"https://www.investomate.com/\">www.investomate.com</a>\n" +
                "\n" +*/
                "\n" +
                "</body>\n" +
                "</html>\n";
        return str;
    }

    public static String emailUponSuccessfulSubmissionOfReturn(User user, Taxform taxform) throws Exception {
        String str = "<html>\n" +
                "<head>\n" +
                "\t<title>Befiler</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<strong>Dear " + user.getFullName() + ",</strong>\n" +
                "\t<p>\n" +
                "\t\tYour tax return for Tax Year [" + taxform.getTaxformYear().getYear() + "] has been successfully filed. A copy of your of Income Tax Return and Wealth Statement are attached for your record.\n" +
                "\t\t</p>\n" +
                "\t\t<p>\t\t\n" +
                "\t\t\tYou can also access your returns anytime by logging on to <a href=\"https://www.befiler.com/\">www.befiler.com.</a>\n" +
                "\t\t</p>\n" +
                "\t\t<p>\n" +
                "\t\tWe would love you to stay connected. Like our <a href=\"https://www.facebook.com/pg/Befiler.pk/reviews/?ref=page_internal\">Facebook</a> page, and subscribe to our <a href=\"https://www.youtube.com/channel/UCLaq888iS8O6Zz7I0BwbQvQ\">YouTube Channel</a> to receive latest updates on the matter. If you would like to let others know about your experience at BeFiler, feel free to write a review or recommend us on Facebook. \n" +
                "\t\t</p>\n" +
                "\t\t<p>Warm Regards, </p>\n" +
                "\t\t<strong>Team BeFiler</strong>\n" +
                "</body>\n" +
                "\n" +
                "\n" +
                "</html>";
        return str;
    }

    public static String sendEmailToBoss(String name,String email, String mobileNo, String msg) {
        String str = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<b>Name:</b><br>"+name+"<br><br>\n" +
                "<b>Email:</b><br>"+email+"<br><br>\n" +
                "<b>Mobile No:</b><br>"+mobileNo+"<br><br>\n" +
                "<b>Message:</b><br>"+msg+
                "</body>\n" +
                "</html>\n";

        return str;
    }


    public static String emailUponSuccessfulSubmissionOfReturnNTN(String name) {
        String str = "<html>\n" +
                "<head>\n" +
                "\t<title>Befiler</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<body>\n" +
                "<strong>Dear " + name + ",</strong>\n" +
                "\t\t<p>\t\t\n" +
                "\t\t\tYour NTN has been successfully created. Now you can become a tax filer using  <a href=\"https://www.befiler.com/\">www.befiler.com.</a>\n" +
                "\t\t</p>\n" +
                "\t\t<p>\n" +
                "\t\tWe would love you to stay connected. Like our <a href=\"https://www.facebook.com/pg/Befiler.pk/reviews/?ref=page_internal\">Facebook</a> page, and subscribe to our <a href=\"https://www.youtube.com/channel/UCLaq888iS8O6Zz7I0BwbQvQ\">YouTube Channel</a> to receive latest updates on the matter. If you would like to let others know about your experience at BeFiler, feel free to write a review or recommend us on Facebook. \n" +
                "\t\t</p>\n" +
                "\t\t<p>Warm Regards, </p>\n" +
                "\t\t<strong>Team BeFiler</strong>\n" +
                "</body>\n" +
                "</html>";
        return str;
    }

}
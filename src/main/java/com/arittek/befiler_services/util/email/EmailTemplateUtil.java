package com.arittek.befiler_services.util.email;

public class EmailTemplateUtil {

    public static String emailTemplateForNewUser(String name) {
        return "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>\n" +
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
                "\tmax-width: 100%; \n" +
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
                "a.gp { background-color: #DB4A39!important; }\n" +
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
                "<td><img src='http://www.investomate.com/img/logo.png'/></td>\n" +
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
                "<h3>Hi, </h3>\n" +
                "<p class='lead'>Thank you for signing up with <a href='http://www.investomate.com/' target='_blank'> InvestoMate </a> for managing your investments in mutual funds. You’re on your way to super-productivity and beyond!</p>\n" +
                "<p>Whether you are an existing investor or looking to invest for the first time in mutual funds, you'll find that InvestoMate offers a great way to keep track of the performance of mutual funds in the most simplified manner.Here are a few quick tips to get you started:<br /> <ul><li>Build your portfolio by entering the details of your current holdings in mutual funds. Enter all the transactions including the initial purchase, any subsequent redemptions and dividends received thereon.</li><li>If you have lost track of your transactions, simply enter the current value of your investment based on your latest available statement. The date of investment in that case should be the applicable date of NAV in the statement and select 'No' in case of Front Load. The resulting number of units should correspond to the units mentioned in the statement. If not, please recheck the information entered.</li><li>Once you have entered the details of your holdings, the portal will show the return on investment for each fund both in amount and percentage in relation to the date of investment, and will also show the concentration of your aggregate investment in various asset classes. </li><li>Check out other tabs to experience meaningful analysis and value added tools offered by InvestoMate.</li></ul> <br /> Have any questions? Just shoot us an email! We’re always here to help.</p>\n" +
                " \n" +
                "<p class='callout'>\n" +
                "Go to Investomate now and login <a href='http://www.investomate.com'>Click here! &raquo;</a>\n" +
                "</p> \n" +
                " \n" +
                "<table class='social' width='100%'>\n" +
                "<tr>\n" +
                "<td>\n" +
                " \n" +
                "<table align='left' class='column'>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<h5 class=''>Connect with Us:</h5>\n" +
                "<p class=''><a href='http://www.facebook.com/investomate'  target='_blank' class='soc-btn fb'>Facebook</a>\n" +
                " <a href='https://twitter.com/investomate'  target='_blank' class='soc-btn tw'>Twitter</a> \n" +
                " <a href='https://plus.google.com/+InvestoMateSavingsInvestmentSolutionsKarachi/about'  target='_blank' class='soc-btn gp'>Google+</a></p>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table> \n" +
                " \n" +
                "<table align='left' class='column'>\n" +
                "<tr>\n" +
                "<td>\n" +
                "<h5 class=''>Contact Info:</h5>\n" +
                "<p>Phone: <strong>+92- 21-34169553</strong><br/>\n" +
                "Email: <strong><a href='mailto:contact@investomate.com'>contact@investomate.com</a></strong></p>\n" +
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
    }
}

package com.arittek.befiler_services.util;

import com.arittek.befiler_services.beans.FbrBean;

import static com.arittek.befiler_services.util.FbrInputParmValidationUtil.isValidParameter;

public class FbrUtil {
    public  static FbrBean getFbrNTN(String searchBy,String parm1,String parm2){
        FbrBean fbrBean = new FbrBean();
        try {
            fbrBean.setCode(0);
            fbrBean.setMessage("Invalid Parameters");
        /*....CHECK INPUTS ARE VALID OR NOT... */
            if (!isValidParameter(searchBy, parm1, parm2)) {
                MyPrint.println("GIVEN PARAMATERS ARE ARE INVALID........");
                fbrBean.setCode(0);
                fbrBean.setMessage("Invalid Parameters");
                return fbrBean;
            }

        /*....CONDITION SEARCH FOR  BY  NTN/FTN.....*/
            else if (searchBy.equals("1")) {
                FbrAllService fbrAllService=new FbrAllService();
                fbrBean = fbrAllService.getFbrByNntOrFtn(searchBy, parm1, parm2);
                return fbrBean;
            }
        /*....CONDITION SEARCH FOR  BY  CNIC....*/
            else if (searchBy.equals("3")) {
                MyPrint.println("condition for CNIC from Fbr util class........");
                FbrAllService fbrAllService=new FbrAllService();
                fbrBean = fbrAllService.getFbrByCNIC(searchBy, parm1, parm2);
                return fbrBean;
            }
            MyPrint.println("end of condion class.....fbrUtil.....");
            return fbrBean;
        }catch(Exception e){
            e.printStackTrace();
            Logger4j.getLogger().error("Exception : " + e);
        }
        return fbrBean;
   }

}

package com.arittek.befiler_services.util;

/**
 * Created by Arittek on 7/18/2017.
 */
public class FbrInputParmValidationUtil {
    public  static boolean isValidParameter(String searchBy,String  parm1, String parm2){
        MyPrint.println("search by= ======="+searchBy);
        MyPrint.println(parm1+" parm1 lenght is==========="+parm1.length());
        MyPrint.println(parm2+" parm2 lenght is===========");

        try {
            if (searchBy.equals("1") && (!parm1.isEmpty()) && (!parm2.isEmpty()) && (parm1.length() == 7) && (parm2.length() == 1)) {
                Integer.parseInt(searchBy);
                Integer.parseInt(parm1);
                Integer.parseInt(parm2);
                return true;
            }
            if ((searchBy.equals("3")) && (!parm1.isEmpty())  && (parm1.length() == 13)) {
                MyPrint.println("input class is called and -CNIC- condition  is true");
                Integer.parseInt(searchBy);
                //Integer.parseInt(parm1);
                return true;
            }

            else return false;
        }catch (Exception e){
            MyPrint.println(">>>>>>>>>>>>>>>>>>>>>>>>>>> inValid parameters");
            return false;
        }
    }
}

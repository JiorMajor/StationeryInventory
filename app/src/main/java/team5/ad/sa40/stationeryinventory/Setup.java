package team5.ad.sa40.stationeryinventory;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import team5.ad.sa40.stationeryinventory.Model.Employee;

public class Setup {
    public static String baseurl = "http://www.team5.com/api";
    public static String GTokenForNotification = "";
    public static Employee user;

    public static String parseDateToString(Date d) {
        String output = "";
        SimpleDateFormat out = new SimpleDateFormat("dd/MM/yyyy");
        output = out.format(d);
        return output;
    }

    public static Date parseStringToDate(String d){
        try{
            Date temp;
            SimpleDateFormat out = new SimpleDateFormat("dd/MM/yyyy");
            temp = out.parse(d);
            return temp;
        }catch (ParseException e){

        }
        return null;
    }
}

package com.thangld.managechildren.utils;

/**
 * Created by thangld on 08/02/2017.
 */

public class FileUtils {


    //
    public static String[] sperateNameExtention(String fileName){

        int indexLast = fileName.lastIndexOf(".");
        if (indexLast == -1){
            return new String[]{fileName,""};
        }else{
            return new String[]{fileName.substring(0,indexLast),fileName.substring(indexLast+1,fileName.length()-1)};
        }
    }


}

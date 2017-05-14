package com.thangld.managechildren.utils;

import android.content.Context;

import com.thangld.managechildren.storage.controller.BackupFolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by thangld on 08/02/2017.
 */

public class FileUtils {


    /**
     * Tách tên và đuôi file
     * @param fileName
     * @return
     */
    public static String[] sperateNameExtention(String fileName){

        int indexLast = fileName.lastIndexOf(".");
        if (indexLast == -1){
            return new String[]{fileName,""};
        }else{
            return new String[]{fileName.substring(0,indexLast),fileName.substring(indexLast+1,fileName.length())};
        }
    }

    /**
     * Thực hiện copy file vào thư mục backup
     * @param source   Nguồn copy
     * @param folder   Folder đích
     * @param displayName Tên đích
     * @return Tên đã lưu trên đĩa
     */
    public static String copyFiles(Context context, String source, String folder, String displayName) {

        File fileOut = new File(context.getFilesDir(), File.separator + BackupFolder.BACKUPS + File.separator + folder + File.separator + displayName);

        /**
         * Xử lý nếu trùng tên, phải đổi tên thành xxx-1.mp4, xxx-2.mp4 ....
         */
        if (fileOut.exists()) {
            String[] nameExtention = FileUtils.sperateNameExtention(displayName);
            int i = 1;
            while (fileOut.exists()) {
                if (nameExtention[1].equals("")) {
                    displayName = nameExtention[0] + "-" + i;
                } else {
                    displayName = nameExtention[0] + "-" + i + "." + nameExtention[1];
                }
                fileOut = new File(context.getFilesDir(), File.separator + BackupFolder.BACKUPS + File.separator + folder + File.separator + displayName);
                i++;
            }
        }

        try {
            InputStream in = new FileInputStream(source);
            OutputStream out = new FileOutputStream(fileOut);
            int len;
            byte[] buf = new byte[1024];
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            return displayName;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}

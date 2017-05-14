package com.thangld.managechildren.entry;

/**
 * Tuong xung voi cac truong trong bang csdl
 * @link com.thangld.managechildren.database.model.SmsModel
 * Created by thangld on 18/02/2017.
 */

public class SmsEntry {

    /**
     * Sdt
     */
    private String address;
    /**
     * Tin nhan
     */
    private String body;
    /**
     * Thoi gian
     */
    private String date;

    /**
     * Loai tin nhan
     */
    private String type;
    /**
     * Trang thai backup
     */
    private boolean isBackup;


    public SmsEntry(String address, String body, String date, String type, boolean isBackup) {
        this.address = address;
        this.body = body;
        this.date = date;
        this.type = type;
        this.isBackup = isBackup;
    }
}

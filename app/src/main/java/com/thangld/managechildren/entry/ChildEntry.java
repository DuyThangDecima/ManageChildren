package com.thangld.managechildren.entry;

/**
 * Created by thangld on 24/04/2017.
 */

public class ChildEntry {
    private String fullName;
    private int birth;

    /**
     * isActive = 1 => Đang được active
     * isActive = 0 => Không đc active, đang ở vị trí 3,4,5,6...
     * isActive = -1 => Không đc active, được set là extra account trên màn hình
     */

    private int isActive;
    private String idServer;

    public ChildEntry(String fullName, int birth) {
        this.setFullName(fullName);
        this.setBirth(birth);
    }

    public ChildEntry(String fullName, int birth, int isActive, String idServer) {
        this.setFullName(fullName);
        this.setBirth(birth);
        this.setIsActive(isActive);
        this.setIdServer(idServer);
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getBirth() {
        return birth;
    }

    public void setBirth(int birth) {
        this.birth = birth;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }


    public String getIdServer() {
        return idServer;
    }

    public void setIdServer(String idServer) {
        this.idServer = idServer;
    }
}

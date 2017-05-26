package com.thangld.managechildren.entry;

/**
 * Created by thangld on 22/05/2017.
 */

public class AppEntry {
    private String packageName;
    private String appName;
    private String idServer;
    private String idChild;
    private String type;

    public AppEntry(String packageName, String appName, String idServer, String idChild, String type) {
        this.setPackageName(packageName);
        this.setAppName(appName);
        this.setIdServer(idServer);
        this.setIdChild(idChild);
        this.setType(type);
    }


    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getIdServer() {
        return idServer;
    }

    public void setIdServer(String idServer) {
        this.idServer = idServer;
    }

    public String getIdChild() {
        return idChild;
    }

    public void setIdChild(String idChild) {
        this.idChild = idChild;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}

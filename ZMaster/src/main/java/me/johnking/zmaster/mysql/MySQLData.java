package me.johnking.zmaster.mysql;

/**
 * Created by Marco on 15.08.2014.
 */
public class MySQLData {

    private String hostname = "";
    private String port = "";
    private String database = "";
    private String username = "";
    private String password = "";


    public MySQLData() {

    }

    public MySQLData(String hostname, String port, String database, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public String getHostname() {
        return this.hostname;
    }

    public String getPort() {
        return this.port;
    }

    public String getDatabase() {
        return this.database;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String string){
        this.password = string;
    }
}



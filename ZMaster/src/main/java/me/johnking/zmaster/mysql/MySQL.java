package me.johnking.zmaster.mysql;

import snaq.db.ConnectionPool;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {

    private MySQLData mySQLData;
    private ConnectionPool connectionPool;

    public MySQL(MySQLData mySQLData) {
        this.mySQLData = mySQLData;

        openConnection();
    }

    private void openConnection() {
        try {
            Class<?> c = Class.forName("com.mysql.jdbc.Driver");
            Driver driver = (Driver) c.newInstance();
            DriverManager.registerDriver(driver);
            this.connectionPool = new ConnectionPool("local", 5, 10, 30, 60, "jdbc:mysql://" + mySQLData.getHostname() + ":" + mySQLData.getPort() + "/" + mySQLData.getDatabase(), mySQLData.getUsername(), mySQLData.getPassword());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkConnection() {
        return this.connectionPool != null;
    }

    public Connection getConnection() throws SQLException{
        return this.connectionPool.getConnection(2000);
    }
}



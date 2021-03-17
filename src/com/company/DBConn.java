package com.company;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Properties;


public class DBConn {

    protected Connection conn;

    public DBConn() {
    }

    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("0");
            java.util.Properties p = new Properties();
            p.put("user", "edsongr_eistad");
            p.put("password", "passord");
            System.out.println("1");
            String url = "jdbc:mysql://mysql.stud.ntnu.no:3306/edsongr_eistad_datab?autoReconnect=true&useSSL=false";
            conn = DriverManager.getConnection(url, "edsongr_eistad", "passord");
            System.out.println("2");
            PreparedStatement statement=conn.prepareStatement("INSERT INTO Course VALUES ( (?), (?), (?) ) ");
            System.out.println("3");
            statement.setInt(1, 1);
            statement.setString(2, "Ledelse");
            statement.setString(3, "Spring");
            statement.execute();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}


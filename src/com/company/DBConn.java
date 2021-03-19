package com.company;

import java.sql.*;
import java.util.Properties;


public class DBConn {

    protected Connection conn;

    public DBConn() {
    }

    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            //System.out.println("0");
            java.util.Properties p = new Properties();
            p.put("user", "edsongr_eistad");
            p.put("password", "passord");
            //System.out.println("1");
            String url = "jdbc:mysql://mysql.stud.ntnu.no:3306/edsongr_eistad_datab?autoReconnect=true&useSSL=false";
            conn = DriverManager.getConnection(url, "edsongr_eistad", "passord");
            System.out.println("successfully established connection");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Boolean checkPassword(String username, String password) {
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT name from User where password=(?)");
            statement.setString(1, password);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                if (rs.getString("name").equals(username)) {
                    return true;
                }
            }
            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    public void insertCourse(String name, String term) {
        try {
            PreparedStatement statement = conn.prepareStatement("INSERT INTO Course(CourseName, term) VALUES ( (?), (?) ) ");
            System.out.println("inserting " + name + "into Course");
            statement.setString(1, name);
            statement.setString(2, term);
            statement.execute();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    public void insertFolder(String category, int courseID) {
        try {
            PreparedStatement statement = conn.prepareStatement("INSERT INTO Folder(Category, CourseID) VALUES ( (?), (?) ) ");
            System.out.println("inserting " + category + " into Folder");
            statement.setString(1, category);
            statement.setInt(2, courseID);
            statement.execute();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}


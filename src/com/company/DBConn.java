package com.company;

import java.sql.*;
import java.util.*;
import javax.xml.transform.Result;


public class DBConn {

    protected Connection conn;

    public DBConn() {
    }

    public boolean connect() {
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
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Integer> search(String keyword){
        List<Integer> list = new ArrayList<>();
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT PostID from Post where text like (?) ");
            statement.setString(1, "%" + keyword + "%");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                list.add(rs.getInt("PostID"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
//Use case 5. Only show raw data without columninfo as of now.
    public HashMap<String,List<Integer>> stats(){
        HashMap<String,List<Integer>> list = new HashMap<>();
        try{
            PreparedStatement statement = conn.prepareStatement("Select name, readstats, " +
                "ifnull(count(P.PostID),0) as antallPosts" +
                " From User as U" +
                " Left Outer join Post as P" +
                " on U.UserID = P.UserID" +
                " Group by U.UserID");
            ResultSet rs = statement.executeQuery();
            while (rs.next()){
                list.put(rs.getString("name"),new ArrayList<>(Arrays.asList(rs.getInt("readstats"),rs.getInt("antallPosts"))));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }
    public int checkPassword(String username, String password) {
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT name, email, type from User where password=(?)");
            statement.setString(1, password);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                if (rs.getString("name").equals(username)
                        ||  rs.getString("email").equals(username)) {
                    if (rs.getString("type").toLowerCase().equals("student")){
                        return 1;
                    }
                    else if (rs.getString("type").toLowerCase().equals("instructor")){
                        return 2;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    public int getUserID(String name){
        try {
            PreparedStatement statement = conn.prepareStatement("Select UserId from User where name = (?)");
            statement.setString(1,name);
            ResultSet rs = statement.executeQuery();
            int userID = 0;
            while (rs.next()){
                 return userID = rs.getInt("UserID");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }



    public void insertCourse(String name, String term) {
        try {
            PreparedStatement statement =
                conn.prepareStatement("INSERT INTO Course(CourseName, term) VALUES ( (?), (?) ) ");
            System.out.println("inserting " + name + " into Course");
            statement.setString(1, name);
            statement.setString(2, term);
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void insertUser(String name, String email, String password, int readstats, String type) {
            try {
                PreparedStatement statement = conn.prepareStatement("Insert Into User (Name, Email, Password, readstats, type) VALUES ( (?), (?), (?), (?), (?) ) ");
                System.out.println("inserting user " + name + " into Course");
                statement.setString(1, name);
                statement.setString(2, email);
                statement.setString(3, password);
                statement.setInt(4,readstats);
                statement.setString(5,type);

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
    public void insertThread(String title, String threadText, int views, int userID, int folderID) {
        try {
            PreparedStatement statement = conn.prepareStatement("INSERT INTO Thread(Title,ThreadText, Views,UserID, FolderID) VALUES ( (?), (?), (?), (?), (?) ) ");
            PreparedStatement statement2 = conn.prepareStatement("Select category from Folder where FolderID = (?)");
            statement2.setInt(1,folderID);
            ResultSet rs = statement2.executeQuery();
            String folderCategory = null;
            while (rs.next()) {
                folderCategory = rs.getString("Category");

            }


            System.out.println("inserting " + title + " into " + folderCategory);
            statement.setString(1, title);
            statement.setString(2, threadText);
            statement.setInt(3, views);
            statement.setInt(4, userID);
            statement.setInt(5, folderID);
            statement.execute();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void insertPost(String tag, int likes, String text, int userID, int threadID) {
        try {
            PreparedStatement statement = conn.prepareStatement("INSERT INTO Post(tag, likes, text, UserID, ThreadID) VALUES ( (?), (?), (?), (?), (?) ) ");
            PreparedStatement statement2 = conn.prepareStatement("Select title from Thread where ThreadID = (?)");
            statement2.setInt(1,threadID);
            ResultSet rs = statement2.executeQuery();
            String threadTitle = null;
            while (rs.next()) {
                threadTitle = rs.getString("Title");

            }


            System.out.println("inserting post into " + threadTitle);
            statement.setString(1, tag);
            statement.setInt(2, likes);
            statement.setString(3, text);
            statement.setInt(4, userID);
            statement.setInt(5, threadID);
            statement.execute();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    // Use case 2
    public void postInCorrectFolder(String text, String tag, String folderName,int userID){
        try{
            PreparedStatement statement = conn.prepareStatement("Select FolderID from Folder where Category = (?)");
            statement.setString(1,folderName);
            ResultSet rs = statement.executeQuery();
            int folderIDFromResult= 0;
            while (rs.next()){
                folderIDFromResult = rs.getInt("FolderID");
            }
            PreparedStatement statement2 = conn.prepareStatement("Insert into Thread(UserID, FolderID) Values ( (?), (?))");
            statement2.setInt(1,userID);
            statement2.setInt(2,folderIDFromResult);
            statement2.execute();
            PreparedStatement lastInsert = conn.prepareStatement("Select LAST_INSERT_ID();");
            ResultSet rs2 = lastInsert.executeQuery();
            System.out.println();
            int newThreadID = 0;
            while (rs2.next()){
                newThreadID = rs2.getInt("LAST_INSERT_ID()");
            }

            PreparedStatement statement3 = conn.prepareStatement("Insert into Post(text, tag,UserID,ThreadID) Values ( (?), (?), (?), (?))");
            statement3.setString(1,text);
            statement3.setString(2,tag);
            statement3.setInt(3,userID);
            statement3.setInt(4,newThreadID);
            statement3.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    // Use case 3
    public void replyByPostID(String text, int postID, int userID){
        int correctThreadID = 0;
        try{
            PreparedStatement statement = conn.prepareStatement("Select ThreadID From Post Where PostID = (?) ");
            statement.setInt(1,postID);
            ResultSet rs = statement.executeQuery();
            while (rs.next()){
                correctThreadID = rs.getInt("ThreadID");
            }
            PreparedStatement statement2 = conn.prepareStatement("Insert into Post(text,UserID,ThreadID) values ( (?), (?), (?))");
            statement2.setString(1,text);
            statement2.setInt(2,userID);
            statement2.setInt(3,correctThreadID);
            statement2.execute();
        }
        catch (Exception e ){
            e.printStackTrace();
        }
        
    }

}


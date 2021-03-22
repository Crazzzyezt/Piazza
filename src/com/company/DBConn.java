package com.company;

import java.sql.*;
import java.util.*;



public class DBConn {

    protected Connection conn;

    public DBConn() {
    }
    // Oppretter forbindelse med mysql-servern hos stud.ntnu.no.
    public boolean connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            java.util.Properties p = new Properties();
            p.put("user", "edsongr_eistad");
            p.put("password", "passord");
            String url = "jdbc:mysql://mysql.stud.ntnu.no:3306/edsongr_eistad_datab?autoReconnect=true&useSSL=false";
            conn = DriverManager.getConnection(url, "edsongr_eistad", "passord");
            System.out.println("successfully established connection");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // USE CASE 1.
    // Sjekker om kombinasjonen username og password stemmer.
    public int checkPassword(String username, String password) {
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT name, email, type from User where password=(?)");
            statement.setString(1, password);
            ResultSet rs = statement.executeQuery();
            //Går igjennom hvert result for å sjekke.
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    // USE CASE 2.
    // Legger inn en post basert på folderName, med text og tag.
    //En ny Thread opprettes, der posten vil bli lagt til. Denne inneholder ikke innhold for
    //ThreadText.
    public void postInCorrectFolder(String text, String tag, String folderName,int userID){
        try{
            //Statement 1 henter riktig FolderID.
            PreparedStatement statement = conn.prepareStatement("Select FolderID from Folder where Category = (?)");
            statement.setString(1,folderName);
            ResultSet rs = statement.executeQuery();
            int folderIDFromResult= 0;
            while (rs.next()){
                folderIDFromResult = rs.getInt("FolderID");
            }
            //Statement 2 oppretter en nå Thread basert på folderID fra statement 1.
            PreparedStatement statement2 = conn.prepareStatement("Insert into Thread(UserID, FolderID) Values ( (?), (?))");
            statement2.setInt(1,userID);
            statement2.setInt(2,folderIDFromResult);
            statement2.execute();
            // Henter ThreadIDn på nyopprettede Thread.
            PreparedStatement lastInsert = conn.prepareStatement("Select LAST_INSERT_ID();");
            ResultSet rs2 = lastInsert.executeQuery();
            System.out.println();
            int newThreadID = 0;
            while (rs2.next()){
                newThreadID = rs2.getInt("LAST_INSERT_ID()");
            }
            // Statement 3 inserter en post i denne nyopprettede threaden, med text og tag.
            PreparedStatement statement3 = conn.prepareStatement("Insert into Post(text, tag,UserID,ThreadID) Values ( (?), (?), (?), (?))");
            statement3.setString(1,text);
            statement3.setString(2,tag);
            statement3.setInt(3,userID);
            statement3.setInt(4,newThreadID);
            statement3.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    // USE CASE 3
    //Legger en post i riktig tråd basert på PostID.
    public void replyByPostID(String text, int postID, int userID){
        int correctThreadID = 0;
        try{
            PreparedStatement statement = conn.prepareStatement("Select ThreadID From Post Where PostID = (?) ");
            statement.setInt(1,postID);
            ResultSet rs = statement.executeQuery();
            //Henter ThreadID basert på postID.
            while (rs.next()){
                correctThreadID = rs.getInt("ThreadID");
            }
            PreparedStatement statement2 = conn.prepareStatement("Insert into Post(text,UserID,ThreadID) values ( (?), (?), (?))");
            statement2.setString(1,text);
            statement2.setInt(2,userID);
            statement2.setInt(3,correctThreadID);
            statement2.execute();
        }
        catch (SQLException e ){
            e.printStackTrace();
        }

    }
    // USE CASE 4
    // Søker igjennom hver post med text lik keyword. Returnerer en liste med postIDs.
    public List<Integer> search(String keyword){
        List<Integer> list = new ArrayList<>();
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT PostID from Post where text like (?) ");
            statement.setString(1, "%" + keyword + "%");
            ResultSet rs = statement.executeQuery();
            //Henter hver linje i resultset for å legge til eventuelle treff.
            while (rs.next()) {
                list.add(rs.getInt("PostID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    // USE CASE 5
    //Returnerer en liste over hvor mange lesninger og posts opprettet hver bruker i databasen har.
    //Listen vil også inkludere null-forekomster.
    public HashMap<String,List<Integer>> stats(){
        HashMap<String,List<Integer>> list = new HashMap<>();
        try{
            //Left outer joiner Post med User basert på Userid. Teller oppføringer, og grupperer
            //etter UserID. Sortert deretter synkende etter readstats.
            PreparedStatement statement = conn.prepareStatement("Select name, readstats, " +
                "ifnull(count(P.PostID),0) as antallPosts" +
                " From User as U" +
                " Left Outer join Post as P" +
                " on U.UserID = P.UserID" +
                " Group by U.UserID" +
                " order by readstats DESC");
            ResultSet rs = statement.executeQuery();
            //Henter hver linje av result.
            while (rs.next()){
                list.put(rs.getString("name"),new ArrayList<>(Arrays.asList(rs.getInt("readstats"),rs.getInt("antallPosts"))));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return list;
    }
    //Returnerer UserID lik name.
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


    // Innsetting av Course i DB.
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
    // Innsetting av User i DB. Oppretter også relasjonen mellom User og student/instructor.
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
                PreparedStatement lastInsert = conn.prepareStatement("Select LAST_INSERT_ID();");
                ResultSet rs = lastInsert.executeQuery();
                int userID = 0;
                //Henter siste UserID som ble lagt inn, for å kunne opprette relasjon mot type.
                while (rs.next()){
                    userID = rs.getInt("LAST_INSERT_ID()");
                }
                if(type.toLowerCase().equals("student")){
                    PreparedStatement statement2 = conn.prepareStatement("Insert into Student(UserID) VALUES (?)");
                    statement2.setInt(1,userID);
                    statement2.execute();
                }
                else if(type.toLowerCase().equals("instructor")){
                    PreparedStatement statement3 = conn.prepareStatement("Insert into Instructor(UserID) VALUES (?)");
                    statement3.setInt(1,userID);
                    statement3.execute();
                }
                else{
                    System.out.println("Wrong type of user. Please contact admin.");
                }
            }
            catch(SQLException e) {
                e.printStackTrace();
            }

    }
    // Innsetting av UserInCourse i DB.
    public void insertUserInCourse(int userID, int courseID){
        try{
            PreparedStatement statement = conn.prepareStatement("Insert into UserInCourse(UserID,CourseID VALUES ( (?), (?))");
            statement.setInt(1,userID);
            statement.setInt(2,courseID);
            statement.execute();
            System.out.println("Inserted "+userID+ " in course "+ courseID);

        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    // Innsetting av Folder i DB.
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
    // Insetting av Thread i DB.
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
        catch(SQLException e) {
            e.printStackTrace();
        }
    }
    // Innsetting av Post i DB.
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
        catch(SQLException e) {
            e.printStackTrace();
        }
    }


}


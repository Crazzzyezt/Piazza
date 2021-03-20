package com.company;

import com.mysql.cj.xdevapi.DbDoc;

public class Main {

    public static void main(String[] args) {
        DBConn DBconn = new DBConn();
        DBconn.connect();
        //System.out.println(DBconn.checkPassword("Arne", "arne123"));
        System.out.println(DBconn.search("eksamen"));
        System.out.println(DBconn.checkPassword("Arne", "arne123"));

        /* Insert Into Course (CourseName, term)
        DBconn.insertCourse("TDT4100","Høst");
         */

        /* Insert Into User (Name, Email, Password, readstats, type)*//*
        DBconn.insertUser("David","David@email.no","david123",0,"Instructor");


        /* Insert into Folder (Category, CourseID)
        DBconn.insertFolder("Exam",3);
         */

        /* /*Insert into Post (Tag, Likes, Text, UserID,ThreadID)
        DBconn.insertPost("Question", 2, "Kan jeg ta kont uten godkjent øvinger??"
            , 2, 1);
            */


        /* Insert into Thread (Title, ThreadText, Views, UserID, FolderID)
        DBconn.insertThread("LF","Når kommer LF for eksamen?",0,1,1);
         */
        System.out.println(DBconn.stats());
        DBconn.replyByPostID("Konten er 12.desember", 1,4);

    }
}

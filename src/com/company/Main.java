package com.company;

import com.mysql.cj.xdevapi.DbDoc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws IOException {

        DBConn DBconn = new DBConn();   //jdbc-klasse
        int userType;   //0 = ugyldig, 1 = student, 2 = instruktør
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));   //for user-input

        if(DBconn.connect()){
            System.out.println("velkommen");
        }
        else {
            System.out.println("kunne ikke koble til");
            System.exit(0);
        }



        while (true) {                                      //kan bruke "Arne" og "arne123"
            System.out.println("vennligst logg in med ditt brukernavn eller din e-post");
            String username = reader.readLine();

            System.out.println("vennligst oppgi ditt passord");
            String password = reader.readLine();

            userType = DBconn.checkPassword(username, password);
            if (userType == 1){
                System.out.println("student-innlogging vellykket");
                studentMenu(reader, DBconn);
                System.exit(0);
            }
            else if (userType == 2){
                System.out.println("instruktør-innlogging vellykket");
                instructorMenu(reader, DBconn);
                System.exit(0);

            }
            else {System.out.println("innlogging mislyktes, prøv igjen");}
        }



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
        //System.out.println(DBconn.stats());
        //DBconn.replyByPostID("Konten er 12.desember", 1,4);



    }

    private static void instructorMenu(BufferedReader reader, DBConn DBconn) throws IOException{
        while (true) {
            System.out.println("Velg blant alternativene: \n 1. Svar på en post \n 2. Se statistikk \n 3. avslutt");
            String choice = reader.readLine();

            if (choice.equals("1")) {    //besvar post
                System.out.println("Vennligst oppgi PostID for posten du vil besvare: ");
                int postID = Integer.parseInt(reader.readLine());
                System.out.println("Vennligst oppgi ditt svar på posten: ");
                String text = reader.readLine();
                System.out.println("Vennligst oppgi din UserID: ");
                int userID = Integer.parseInt(reader.readLine());
                System.out.println(userID);
                try{
                    DBconn.replyByPostID(text,postID,userID);
                    System.out.println("Post lagt til. ");
                }catch (Exception e){
                    System.out.println("Kunne ikke legge til post. Prøv igjen.");
                }
            } else if (choice.equals("2")) {   //stats
                System.out.println("Viser statistikk for Brukere, leserstatistikk og antall poster laget.");
                System.out.println(DBconn.stats());
            }
            else {return;}
        }
    }

    private static void studentMenu(BufferedReader reader, DBConn DBconn) throws IOException {
        while (true) {
            System.out.println("Velg blant alternativene: \n 1. Lag en ny post \n 2. Søk etter poster \n 3. avslutt");
            String choice = reader.readLine();

            if (choice.equals("1")) {    //lag post
                System.out.println("Vennligst oppgi hvilken folder du ønsker lage ny tråd/post: ");
                String folderName = reader.readLine();
                System.out.println("Vennligst oppgi tekst: ");
                String text = reader.readLine();
                System.out.print("Vennligst oppgi tag: ");
                String tag = reader.readLine();
                System.out.println("Vennligst oppgi din UserID: ");
                int userID = Integer.parseInt(reader.readLine());
                System.out.print(userID);
                try{
                    DBconn.postInCorrectFolder(text,tag,folderName,userID);
                    System.out.println("Post lagt til.");
                }catch (Exception e){
                    System.out.println("Kunne ikke poste. Prøv igjen.");

                }

            } else if (choice.equals("2")) {   //søk
                System.out.println("Vennligst oppgi et søkeord: ");
                String searchTerm = reader.readLine();
                System.out.println(DBconn.search(searchTerm));
            }
            else {return;}
        }
    }
}

package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


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



        while (true) {      //kan bruke "Arne" og "arne123" for Student
                            //Eller "David" og "david123" for Instructor.
            System.out.println("vennligst logg in med ditt brukernavn eller din e-post");
            String username = reader.readLine();

            System.out.println("vennligst oppgi ditt passord");
            String password = reader.readLine();

            userType = DBconn.checkPassword(username, password);
            if (userType == 1){
                System.out.println("student-innlogging vellykket");
                int userID = DBconn.getUserID(username);
                studentMenu(reader, DBconn,userID);
                System.exit(0);
            }
            else if (userType == 2){
                System.out.println("instruktør-innlogging vellykket");
                int userID = DBconn.getUserID(username);
                instructorMenu(reader, DBconn, userID);
                System.exit(0);

            }
            else {System.out.println("innlogging mislyktes, prøv igjen");}
        }


    }
    //Menyen som Users med type == Instructor får opp. Herunder Use case 3 og 5.
    private static void instructorMenu(BufferedReader reader, DBConn DBconn, int userID) throws IOException{
        while (true) {
            System.out.println("Velg blant alternativene: \n 1. Svar på en post \n 2. Se statistikk \n 3. avslutt");
            String choice = reader.readLine();

            if (choice.equals("1")) {    //besvar post basert på PostID.
                System.out.println("Vennligst oppgi PostID for posten du vil besvare: ");
                int postID = Integer.parseInt(reader.readLine());
                System.out.println("Vennligst oppgi ditt svar på posten: ");
                String text = reader.readLine();
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
    //Menyen som Users med type == Student får opp. Herunder Use case 2 og 4.
    private static void studentMenu(BufferedReader reader, DBConn DBconn, int userID) throws IOException {
        while (true) {
            System.out.println("Velg blant alternativene: \n 1. Lag en ny post \n 2. Søk etter poster \n 3. avslutt");
            String choice = reader.readLine();

            if (choice.equals("1")) {    //lag post med tekst og tag i riktig folder.
                System.out.println("Vennligst oppgi hvilken folder du ønsker lage ny tråd/post: ");
                String folderName = reader.readLine();
                System.out.println("Vennligst oppgi tekst: ");
                String text = reader.readLine();
                System.out.print("Vennligst oppgi tag: ");
                String tag = reader.readLine();
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

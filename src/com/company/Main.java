package com.company;

public class Main {

    public static void main(String[] args) {
        DBConn DBconn = new DBConn();
        DBconn.connect();
        System.out.println(DBconn.checkPassword("Arne", "arne123"));
        DBconn.insertThread("LF","Når kommer LF for eksamen?",0,1,1);
    }
}

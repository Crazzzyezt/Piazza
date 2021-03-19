package com.company;

public class Main {

    public static void main(String[] args) {
        DBConn DBconn = new DBConn();
        DBconn.connect();
        System.out.println(DBconn.checkPassword("Arne", "arne123"));
        DBconn.insertPost("Question",2,"Når er kont på denne eksamen??"
            ,1,1);
    }
}

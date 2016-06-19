package com.maps.unipi.flashcart;

/**
 * Class that represents a User as instance of the Firebase database, hence the attributes must
 * correspond to the DB structure
 * */
public class User {

    private String name;
    private String surname;
    private String card;
    private String password;

    public User(){
        // empty default constructor, necessary for Firebase to be able to deserialize blog posts
    }

    public User(String name, String surname, String card, String password){
        this.name = name;
        this.surname = surname;
        this.card = card;
        this.password = password;
    }

    public String getName(){
        return name;
    }

    public String getSurname(){
        return surname;
    }

    public String getPassword(){
        return password;
    }

    public String getCard(){
        return card;
    }
}


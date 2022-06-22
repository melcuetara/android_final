package com.example.eldroid.model;

public class Account {

    private String firstName;
    private String lastName;
    private int age;
    private String imageUrl;
    private String sex;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Account(String firstName, String lastName, int age, String imageUrl, String sex) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.imageUrl = imageUrl;
        this.sex = sex;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public Account() {}
}

package com.example.shopping.Model;

public class Users {
    private String name,number,password,image,address,invite;

    public Users(){


    }

    public Users(String name, String number, String password, String image, String address
    ,String invite) {
        this.name = name;
        this.number = number;
        this.password = password;
        this.image = image;
        this.address = address;
        this.invite = invite;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getInvite() {
        return invite;
    }

    public void setInvite(String invite) {
        this.invite = invite;
    }
}

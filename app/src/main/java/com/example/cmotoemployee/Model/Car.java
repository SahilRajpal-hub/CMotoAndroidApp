package com.example.cmotoemployee.Model;

public class Car {
    private String Address;

    private String Category;

    private String Color;

    private String Location;

    private String MobileNo;

    private String Model;

    private String Name;

    private String Number;

    private String Photo;

    public Car() {}

    public Car(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, String paramString9) {
        this.Number = paramString1;
        this.Model = paramString2;
        this.Name = paramString3;
        this.Category = paramString4;
        this.Color = paramString5;
        this.Photo = paramString6;
        this.MobileNo = paramString7;
        this.Address = paramString8;
        this.Location = paramString9;
    }

    public String getAddress() {
        return this.Address;
    }

    public String getCategory() {
        return this.Category;
    }

    public String getColor() {
        return this.Color;
    }

    public String getLocation() {
        return this.Location;
    }

    public String getMobileNo() {
        return this.MobileNo;
    }

    public String getModel() {
        return this.Model;
    }

    public String getName() {
        return this.Name;
    }

    public String getNumber() {
        return this.Number;
    }

    public String getPhoto() {
        return this.Photo;
    }

    public void setAddress(String paramString) {
        this.Address = paramString;
    }

    public void setCategory(String paramString) {
        this.Category = paramString;
    }

    public void setColor(String paramString) {
        this.Color = paramString;
    }

    public void setLocation(String paramString) {
        this.Location = paramString;
    }

    public void setMobileNo(String paramString) {
        this.MobileNo = paramString;
    }

    public void setModel(String paramString) {
        this.Model = paramString;
    }

    public void setName(String paramString) {
        this.Name = paramString;
    }

    public void setNumber(String paramString) {
        this.Number = paramString;
    }

    public void setPhoto(String paramString) {
        this.Photo = paramString;
    }
}


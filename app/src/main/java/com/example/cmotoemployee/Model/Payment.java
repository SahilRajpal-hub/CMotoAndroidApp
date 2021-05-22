package com.example.cmotoemployee.Model;

public class Payment {
    private int CarsCleaned;

    private String Date;

    private String PayOn;

    private int Price;

    public Payment() {}

    public Payment(String paramString1, String paramString2, int paramInt1, int paramInt2) {
        this.Date = paramString1;
        this.PayOn = paramString2;
        this.Price = paramInt1;
        this.CarsCleaned = paramInt2;
    }

    public int getCarsCleaned() {
        return this.CarsCleaned;
    }

    public String getDate() {
        return this.Date;
    }

    public String getPayOn() {
        return this.PayOn;
    }

    public int getPrice() {
        return this.Price;
    }

    public void setCarsCleaned(int paramInt) {
        this.CarsCleaned = paramInt;
    }

    public void setDate(String paramString) {
        this.Date = paramString;
    }

    public void setPayOn(String paramString) {
        this.PayOn = paramString;
    }

    public void setPrice(int paramInt) {
        this.Price = paramInt;
    }
}



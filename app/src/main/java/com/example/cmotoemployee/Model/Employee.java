package com.example.cmotoemployee.Model;

public class Employee {
    private String Address;

    private String Area;

    private String Email;

    private String Name;

    private String Number;

    public Employee() {}

    public Employee(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) {
        this.Number = paramString1;
        this.Address = paramString2;
        this.Name = paramString3;
        this.Email = paramString4;
        this.Area = paramString5;
    }

    public String getAddress() {
        return this.Address;
    }

    public String getArea() {
        return this.Number;
    }

    public String getEmail() {
        return this.Email;
    }

    public String getName() {
        return this.Name;
    }

    public String getNumber() {
        return this.Number;
    }

    public void setAddress(String paramString) {
        this.Address = paramString;
    }

    public void setArea(String paramString) {
        this.Area = paramString;
    }

    public void setEmail(String paramString) {
        this.Email = paramString;
    }

    public void setName(String paramString) {
        this.Name = paramString;
    }

    public void setNumber(String paramString) {
        this.Number = paramString;
    }
}


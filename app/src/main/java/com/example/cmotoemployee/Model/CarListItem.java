package com.example.cmotoemployee.Model;

public class CarListItem {
    private Long HouseNumber;

    private String LeaveTime;

    private String Model;

    private String Number;

    private String Photo;

    public CarListItem() {}

    public CarListItem(String paramString1, String paramString2, String paramString3, Long paramLong, String paramString4) {
        this.Number = paramString1;
        this.Model = paramString2;
        this.Photo = paramString3;
        this.HouseNumber = paramLong;
        this.LeaveTime = paramString4;
    }

    public Long getHouseNumber() {
        return this.HouseNumber;
    }

    public String getLeaveTime() {
        return this.LeaveTime;
    }

    public String getModel() {
        return this.Model;
    }

    public String getNumber() {
        return this.Number;
    }

    public String getPhoto() {
        return this.Photo;
    }

    public void setHouseNumber(Long paramLong) {
        this.HouseNumber = paramLong;
    }

    public void setLeaveTime(String paramString) {
        this.LeaveTime = paramString;
    }

    public void setModel(String paramString) {
        this.Model = paramString;
    }

    public void setNumber(String paramString) {
        this.Number = paramString;
    }

    public void setPhoto(String paramString) {
        this.Photo = paramString;
    }
}


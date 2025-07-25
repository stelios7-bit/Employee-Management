package com.model;

import java.io.InputStream;
import java.time.LocalDate;

public class Employee {

    private int id;
    private String name;
    private LocalDate dob;
    private long mobNo;
    private String photoFilename;
    private String photoOriginalFilename;
    private String photoContentType;
    private InputStream photoInputStream;

    // Getters and Setters for all fields

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public long getMobNo() {
        return mobNo;
    }

    public void setMobNo(long mobNo) {
        this.mobNo = mobNo;
    }

    public String getPhotoFilename() {
        return photoFilename;
    }

    public void setPhotoFilename(String photoFilename) {
        this.photoFilename = photoFilename;
    }

    public String getPhotoOriginalFilename() {
        return photoOriginalFilename;
    }

    public void setPhotoOriginalFilename(String photoOriginalFilename) {
        this.photoOriginalFilename = photoOriginalFilename;
    }

    public String getPhotoContentType() {
        return photoContentType;
    }

    public void setPhotoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
    }
    
    public InputStream getPhotoInputStream() {
        return photoInputStream;
    }

    public void setPhotoInputStream(InputStream photoInputStream) {
        this.photoInputStream = photoInputStream;
    }
}

package com.harshit.salarymanager.models;

public class SalaryModel {
    private String id;
    private String staffid;
    private String firstname;
    private String lastname;
    private String salarymonth;
    private String salaryyear;
    private String salarystatus;
    private String salarydate;
    private String salaryfixed;
    private String salarycommisions;
    private String salarydeductable;
    private String note;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStaffid() {
        return staffid;
    }

    public void setStaffid(String staffid) {
        this.staffid = staffid;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getSalarymonth() {
        return salarymonth;
    }

    public void setSalarymonth(String salarymonth) {
        this.salarymonth = salarymonth;
    }

    public String getSalaryyear() {
        return salaryyear;
    }

    public void setSalaryyear(String salaryyear) {
        this.salaryyear = salaryyear;
    }

    public String getSalarystatus() {
        return salarystatus;
    }

    public void setSalarystatus(String salarystatus) {
        this.salarystatus = salarystatus;
    }

    public String getSalarydate() {
        return salarydate;
    }

    public void setSalarydate(String salarydate) {
        this.salarydate = salarydate;
    }

    public String getSalaryfixed() {
        return salaryfixed;
    }

    public void setSalaryfixed(String salaryfixed) {
        this.salaryfixed = salaryfixed;
    }

    public String getSalarycommisions() {
        return salarycommisions;
    }

    public void setSalarycommisions(String salarycommisions) {
        this.salarycommisions = salarycommisions;
    }

    public String getSalarydeductable() {
        return salarydeductable;
    }

    public void setSalarydeductable(String salarydeductable) {
        this.salarydeductable = salarydeductable;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public SalaryModel(String id, String staffid, String firstname, String lastname, String salarymonth, String salaryyear, String salarystatus, String salarydate, String salaryfixed, String salarycommisions, String salarydeductable, String note) {
        this.id = id;
        this.staffid = staffid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.salarymonth = salarymonth;
        this.salaryyear = salaryyear;
        this.salarystatus = salarystatus;
        this.salarydate = salarydate;
        this.salaryfixed = salaryfixed;
        this.salarycommisions = salarycommisions;
        this.salarydeductable = salarydeductable;
        this.note = note;
    }
}


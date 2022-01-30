package com.harshit.salarymanager.models;

public class AttendanceModel {
    String checkins;
    String leaves;

    public String getCheckins() {
        return checkins;
    }

    public void setCheckins(String checkins) {
        this.checkins = checkins;
    }

    public String getLeaves() {
        return leaves;
    }

    public void setLeaves(String leaves) {
        this.leaves = leaves;
    }

    public AttendanceModel(String checkins, String leaves) {
        this.checkins = checkins;
        this.leaves = leaves;
    }
}

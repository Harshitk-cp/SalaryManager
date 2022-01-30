package com.harshit.salarymanager.models;

import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiPlaceHolder {

    @POST("salary-add")
    Call<ResponseBody> addUser(@Body JsonObject jsonObject);

    @POST("salary-getlist")
    Call<ResponseBody> getList(@Body JsonObject jsonObject);

    @POST("salary-getattendance")
    Call<ResponseBody> getAttendance(
            @Body JsonObject jsonObject);

    @POST("salary-update")
    Call<ResponseBody> updateUser(
            @Body JsonObject jsonObject);

    @POST("salary-delete")
    Call<ResponseBody> deleteUser(@Body JsonObject jsonObject);

    @POST("salary-getstaff")
    Call<ResponseBody> getStaff(@Body JsonObject jsonObject);


}

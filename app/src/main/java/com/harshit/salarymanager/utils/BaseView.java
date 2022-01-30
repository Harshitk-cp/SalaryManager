package com.harshit.salarymanager.utils;


import android.content.Context;

public interface BaseView {

    void showLoading();

    //  void showLoading(Context context);

    void showLoading(Context context);

    void hideLoading();

    void showToast(String msg);

    void showSnackBarGreen(String msg);
    void showSnackBarRed(String msg);

    void setTitle(String msg);

}

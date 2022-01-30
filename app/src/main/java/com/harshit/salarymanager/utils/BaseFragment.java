package com.harshit.salarymanager.utils;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.harshit.salarymanager.R;

public abstract class BaseFragment extends Fragment implements BaseView {
    Dialog progressDialog;

    @Override
    public void showLoading() {
    }

    @Override
    public void showLoading(Context context) {
        if (isDetached()) return;
        progressDialog = new Dialog( context);
        progressDialog.show();
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable( Color.TRANSPARENT));
        }
        progressDialog.setContentView( R.layout.progress_dialog);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void showToast(String msg) {
        if (msg != null) {
            Toast.makeText( requireContext(), msg, Toast.LENGTH_SHORT ).show();
        }
    }

    @Override
    public void showSnackBarGreen(String msg) {
        if(getView()!=null){
            Snackbar.make(getView(), msg, Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.accepted_snackbar)).show();

        }

    }

    @Override
    public void showSnackBarRed(String msg) {
        if(getView()!=null) {

            Snackbar.make(getView(), msg, Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.rejected_snackbar)).show();
        }
    }

    @Override
    public void setTitle(String msg) {
    }
}


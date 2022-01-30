package com.harshit.salarymanager.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.harshit.salarymanager.R;
import com.harshit.salarymanager.fragments.SalaryListFragment;

public class SalaryListActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salary_list);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new SalaryListFragment()).commit();

    }

}
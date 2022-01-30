package com.harshit.salarymanager.listeners;

import com.harshit.salarymanager.models.SalaryModel;
import com.harshit.salarymanager.models.StaffModel;

import java.util.List;

public interface IStaffListLoadListener {
    void onListLoadSuccess(List<StaffModel> staffModelList);

    void onListLoadFailed(String message);
}

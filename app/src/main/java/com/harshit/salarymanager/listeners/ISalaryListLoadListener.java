package com.harshit.salarymanager.listeners;

import com.harshit.salarymanager.models.SalaryModel;
import java.util.List;

public interface ISalaryListLoadListener {
    void onListLoadSuccess(List<SalaryModel> salaryModelList);

    void onListLoadFailed(String message);
}

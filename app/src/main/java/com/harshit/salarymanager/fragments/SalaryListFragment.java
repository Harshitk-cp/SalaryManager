package com.harshit.salarymanager.fragments;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.harshit.salarymanager.R;
import com.harshit.salarymanager.adapter.salaryListAdapter;
import com.harshit.salarymanager.adapter.staffAdapter;
import com.harshit.salarymanager.listeners.ISalaryListLoadListener;
import com.harshit.salarymanager.models.ApiPlaceHolder;
import com.harshit.salarymanager.models.AttendanceModel;
import com.harshit.salarymanager.models.SalaryModel;
import com.harshit.salarymanager.models.StaffModel;
import com.harshit.salarymanager.utils.BaseFragment;
import com.harshit.salarymanager.utils.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SalaryListFragment extends BaseFragment implements ISalaryListLoadListener, salaryListAdapter.sendItemData, staffAdapter.sendItemData {


    //initialising views

    //DatePicker

    int mYear;
    int mMonth;
    int mDay;
    String staffId;
    TextView txtTitle;
    EditText etSearch;
    String date_time = "";
    RecyclerView rvSalaryList;
    BottomSheetDialog dialogAdd;
    BottomSheetDialog dialogUpdate;
    BottomSheetDialog dialogMonth;
    BottomSheetDialog dialogStaff;
    BottomSheetDialog dialogStatus;
    SwipeRefreshLayout swipe_refresh;
    salaryListAdapter salaryListAdapter;
    ShimmerFrameLayout shimmerFrameLayout;
    ISalaryListLoadListener salaryListLoadListener;
    ImageView imgSearch, imgCancel, imgEmptyDisplay;
    Button btnAddSalary, btnDate, btnAllStores, btnBack;
    String salaryYear, monthVal, salaryMonth, salaryMonthAdd, salaryYearAdd, salaryYearEdit, salaryMonthEdit;

    List<StaffModel> staffModelList = new ArrayList<StaffModel>();
    List<SalaryModel> salaryModelList = new ArrayList<SalaryModel>();
    List<AttendanceModel> attendanceModelList = new ArrayList<AttendanceModel>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.salary_listview, container, false);

        findViewById(view);

        init();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgSearch.setVisibility(View.INVISIBLE);
                txtTitle.setVisibility(View.INVISIBLE);
                imgCancel.setVisibility(View.VISIBLE);
                etSearch.setVisibility(View.VISIBLE);
                //editText.requestFocus();
            }
        });

        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgSearch.setVisibility(View.VISIBLE);
                txtTitle.setVisibility(View.VISIBLE);
                imgCancel.setVisibility(View.INVISIBLE);
                etSearch.setVisibility(View.INVISIBLE);
                etSearch.setText("");
//               hideSoftKeyboard(ListAcJava.this);
            }
        });

        YearMonth thisMonth1 = YearMonth.now();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM");
        DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy");
        String mon1 = thisMonth1.format(monthFormatter);
        String year1 = thisMonth1.format(yearFormatter);

        salaryMonth = getIntFromMonth(mon1);
        salaryYear = year1;

        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            showSnackBarRed("No internet");
            swipe_refresh.setRefreshing(false);
        } else {
            shimmerFrameLayout.startShimmer();
            getSalaryData(salaryMonth, salaryYear);
        }

        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!NetworkUtils.isNetworkConnected(getContext())) {
                    showSnackBarRed("No internet");
                    swipe_refresh.setRefreshing(false);
                } else {
                    shimmerFrameLayout.startShimmer();
                    getSalaryData(salaryMonth, salaryYear);

                }
            }
        });

        btnAddSalary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddDialog();
            }
        });

        YearMonth thisMonth = YearMonth.now();
        DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMM,  yyyy");
        String month1 = thisMonth.format(monthYearFormatter);
        btnDate.setText(month1);

        btnDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                dialogMonth = new BottomSheetDialog(getActivity());
                dialogMonth.setContentView(R.layout.month_filter_dialog);
                RadioButton check_month1 = dialogMonth.findViewById(R.id.check_month1);
                RadioButton check_month2 = dialogMonth.findViewById(R.id.check_month2);
                RadioButton check_month3 = dialogMonth.findViewById(R.id.check_month3);
                RadioButton check_month4 = dialogMonth.findViewById(R.id.check_month4);
                RadioButton check_month5 = dialogMonth.findViewById(R.id.check_month5);
                RadioGroup rg = (RadioGroup) dialogMonth.findViewById(R.id.radioGroup1);

                YearMonth thisMonth = YearMonth.now();
                YearMonth lastMonth = thisMonth.minusMonths(1);
                YearMonth twoMonthsAgo = thisMonth.minusMonths(2);
                YearMonth threeMonthsAgo = thisMonth.minusMonths(3);
                YearMonth fourMonthsAgo = thisMonth.minusMonths(4);

                DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMM,  yyyy");
                DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM");
                DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy");

                String month1 = thisMonth.format(monthYearFormatter);
                String month2 = lastMonth.format(monthYearFormatter);
                String month3 = twoMonthsAgo.format(monthYearFormatter);
                String month4 = threeMonthsAgo.format(monthYearFormatter);
                String month5 = fourMonthsAgo.format(monthYearFormatter);

                String mon1 = thisMonth.format(monthFormatter);

                String year1 = thisMonth.format(yearFormatter);


                check_month1.setText(month1);
                check_month2.setText(month2);
                check_month3.setText(month3);
                check_month4.setText(month4);
                check_month5.setText(month5);


                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.check_month1:

                                btnDate.setText(month1);
                                salaryMonth = getIntFromMonth(mon1);
                                salaryYear = year1;
                                showLoading(getActivity());
                                getSalaryData(salaryMonth, salaryYear);
                                dialogMonth.dismiss();
                                break;
                            case R.id.check_month2:

                                YearMonth thisMonth2 = YearMonth.now();
                                YearMonth lastMonth = thisMonth2.minusMonths(1);
                                DateTimeFormatter monthFormatter2 = DateTimeFormatter.ofPattern("MMM");
                                DateTimeFormatter yearFormatter2 = DateTimeFormatter.ofPattern("yyyy");
                                String mon2 = lastMonth.format(monthFormatter2);
                                String year2 = lastMonth.format(yearFormatter2);

                                btnDate.setText(month2);
                                salaryMonth = getIntFromMonth(mon2);
                                salaryYear = year2;
                                showLoading(getActivity());
                                getSalaryData(salaryMonth, salaryYear);

                                dialogMonth.dismiss();
                                break;
                            case R.id.check_month3:
                                YearMonth thisMonth3 = YearMonth.now();
                                YearMonth twoMonthsAgo = thisMonth3.minusMonths(2);
                                DateTimeFormatter monthFormatter3 = DateTimeFormatter.ofPattern("MMM");
                                DateTimeFormatter yearFormatter3 = DateTimeFormatter.ofPattern("yyyy");

                                String mon3 = twoMonthsAgo.format(monthFormatter3);
                                String year3 = twoMonthsAgo.format(yearFormatter3);

                                btnDate.setText(month3);
                                salaryMonth = getIntFromMonth(mon3);
                                salaryYear = year3;
                                showLoading(getActivity());
                                getSalaryData(salaryMonth, salaryYear);
                                dialogMonth.dismiss();
                                break;
                            case R.id.check_month4:
                                YearMonth thisMonth4 = YearMonth.now();
                                YearMonth threeMonthsAgo = thisMonth4.minusMonths(3);
                                DateTimeFormatter monthFormatter4 = DateTimeFormatter.ofPattern("MMM");
                                DateTimeFormatter yearFormatter4 = DateTimeFormatter.ofPattern("yyyy");

                                String mon4 = threeMonthsAgo.format(monthFormatter4);
                                String year4 = threeMonthsAgo.format(yearFormatter4);

                                btnDate.setText(month4);
                                salaryMonth = getIntFromMonth(mon4);
                                salaryYear = year4;
                                showLoading(getActivity());
                                getSalaryData(salaryMonth, salaryYear);
                                dialogMonth.dismiss();
                                break;
                            case R.id.check_month5:
                                YearMonth thisMonth5 = YearMonth.now();
                                YearMonth fourMonthsAgo = thisMonth5.minusMonths(3);
                                DateTimeFormatter monthFormatter5 = DateTimeFormatter.ofPattern("MMM");
                                DateTimeFormatter yearFormatter5 = DateTimeFormatter.ofPattern("yyyy");

                                String mon5 = fourMonthsAgo.format(monthFormatter5);
                                String year5 = fourMonthsAgo.format(yearFormatter5);

                                btnDate.setText(month5);
                                salaryMonth = getIntFromMonth(mon5);
                                salaryYear = year5;
                                showLoading(getActivity());
                                getSalaryData(salaryMonth, salaryYear);
                                dialogMonth.dismiss();
                                break;

                        }
                    }
                });
                dialogMonth.show();
            }
        });

        return view;
    }

    private void findViewById(@NonNull View view) {
        btnBack = view.findViewById(R.id.btnBack);
        btnDate = view.findViewById(R.id.btnDate);
        etSearch = view.findViewById(R.id.etSearch);
        txtTitle = view.findViewById(R.id.txtTitle);
        btnAddSalary = view.findViewById(R.id.btnAdd);
        imgSearch = view.findViewById(R.id.imgSearch);
        imgCancel = view.findViewById(R.id.imgCancel);
        btnAllStores = view.findViewById(R.id.btnAllStores);
        swipe_refresh = view.findViewById(R.id.swipe_refresh);
        imgEmptyDisplay = view.findViewById(R.id.imgEmptyDisplay);
        shimmerFrameLayout = view.findViewById(R.id.shimmerFrameLayout);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvSalaryList = view.findViewById(R.id.rvSalaryList);
        rvSalaryList.setLayoutManager(layoutManager);
        rvSalaryList.addItemDecoration(new DividerItemDecoration(getActivity(), 0));
    }

    private void openAddDialog() {
        dialogAdd = new BottomSheetDialog(getActivity());
        dialogAdd.setContentView(R.layout.add_salary_dialog);

        TextView txtChooseStaff = dialogAdd.findViewById(R.id.txtChooseStaff);
        TextView txtChooseMonth = dialogAdd.findViewById(R.id.txtChooseMonth);
        TextView txtStatus = dialogAdd.findViewById(R.id.txtStatus);
        TextView txtSalaryDate = dialogAdd.findViewById(R.id.txtSalaryDate);
        EditText etFixedAmount = dialogAdd.findViewById(R.id.etFixedAmount);
        EditText etCommissions = dialogAdd.findViewById(R.id.etCommissions);
        EditText etDeductable = dialogAdd.findViewById(R.id.etDeductable);
        EditText etNote = dialogAdd.findViewById(R.id.etNote);
        TextView txtFinalSalary = dialogAdd.findViewById(R.id.txtFinalSalary);
        TextView txtCheckIn = dialogAdd.findViewById(R.id.txtCheckIn);
        TextView txtLeaves = dialogAdd.findViewById(R.id.txtLeaves);

        txtSalaryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                datePicker(txtSalaryDate);


            }
        });



        txtStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogStatus = new BottomSheetDialog(getActivity());
                dialogStatus.setContentView(R.layout.status_dialog);

                RadioButton check_paid = dialogStatus.findViewById(R.id.check_paid);
                RadioButton check_unpaid = dialogStatus.findViewById(R.id.check_unpaid);

                RadioGroup radioStatus = (RadioGroup) dialogStatus.findViewById(R.id.radioGroup2);

                radioStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.check_paid:
                                txtStatus.setText("Paid");
                                dialogStatus.dismiss();
                                break;
                            case R.id.check_unpaid:
                                txtStatus.setText("Unpaid");
                                dialogStatus.dismiss();
                                break;

                        }
                    }
                });

                dialogStatus.show();
            }
        });

        txtChooseMonth.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                dialogMonth = new BottomSheetDialog(getActivity());
                dialogMonth.setContentView(R.layout.month_filter_dialog);
                RadioButton check_month1 = dialogMonth.findViewById(R.id.check_month1);
                RadioButton check_month2 = dialogMonth.findViewById(R.id.check_month2);
                RadioButton check_month3 = dialogMonth.findViewById(R.id.check_month3);
                RadioButton check_month4 = dialogMonth.findViewById(R.id.check_month4);
                RadioButton check_month5 = dialogMonth.findViewById(R.id.check_month5);

                RadioGroup rg = (RadioGroup) dialogMonth.findViewById(R.id.radioGroup1);

                YearMonth thisMonth = YearMonth.now();
                YearMonth lastMonth = thisMonth.minusMonths(1);
                YearMonth twoMonthsAgo = thisMonth.minusMonths(2);
                YearMonth threeMonthsAgo = thisMonth.minusMonths(3);
                YearMonth fourMonthsAgo = thisMonth.minusMonths(4);

                DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMM,  yyyy");
                DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM");
                DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy");

                String month1 = thisMonth.format(monthYearFormatter);
                String month2 = lastMonth.format(monthYearFormatter);
                String month3 = twoMonthsAgo.format(monthYearFormatter);
                String month4 = threeMonthsAgo.format(monthYearFormatter);
                String month5 = fourMonthsAgo.format(monthYearFormatter);

                String mon1 = thisMonth.format(monthFormatter);
                String mon2 = lastMonth.format(monthFormatter);
                String mon3 = twoMonthsAgo.format(monthFormatter);
                String mon4 = threeMonthsAgo.format(monthFormatter);
                String mon5 = fourMonthsAgo.format(monthFormatter);

                String year1 = thisMonth.format(yearFormatter);
                String year2 = lastMonth.format(yearFormatter);
                String year3 = twoMonthsAgo.format(yearFormatter);
                String year4 = threeMonthsAgo.format(yearFormatter);
                String year5 = fourMonthsAgo.format(yearFormatter);

                check_month1.setText(month1);
                check_month2.setText(month2);
                check_month3.setText(month3);
                check_month4.setText(month4);
                check_month5.setText(month5);


                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.check_month1:

                                txtChooseMonth.setText(month1);
                                salaryMonthAdd = getIntFromMonth(mon1);
                                salaryYearAdd = year1;
                                dialogMonth.dismiss();
                                break;
                            case R.id.check_month2:
                                txtChooseMonth.setText(month2);
                                salaryMonthAdd = getIntFromMonth(mon2);
                                salaryYearAdd = year2;
                                dialogMonth.dismiss();
                                break;
                            case R.id.check_month3:
                                txtChooseMonth.setText(month3);
                                salaryMonthAdd = getIntFromMonth(mon3);
                                salaryYearAdd = year3;
                                dialogMonth.dismiss();
                                break;
                            case R.id.check_month4:
                                txtChooseMonth.setText(month4);
                                salaryMonthAdd = getIntFromMonth(mon4);
                                salaryYearAdd = year4;
                                dialogMonth.dismiss();
                                break;
                            case R.id.check_month5:
                                txtChooseMonth.setText(month5);
                                salaryMonthAdd = getIntFromMonth(mon5);
                                salaryYearAdd = year5;
                                dialogMonth.dismiss();
                                break;

                        }
                    }
                });
                dialogMonth.show();
            }
        });

        txtChooseStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogStaff = new BottomSheetDialog(getActivity());
                dialogStaff.setContentView(R.layout.choose_staff_dialog);
                RecyclerView rvChooseStaff;
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                rvChooseStaff = dialogStaff.findViewById(R.id.rvChooseStaff);
                rvChooseStaff.setLayoutManager(layoutManager);
                rvChooseStaff.addItemDecoration(new DividerItemDecoration(getActivity(), 0));
                getStaff(rvChooseStaff);
                dialogStaff.show();
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                {
                    if (!etFixedAmount.getText().toString().equals("") && !etCommissions.getText().toString().equals("") && !etDeductable.getText().toString().equals("")) {
                        int temp1 = Integer.parseInt(etFixedAmount.getText().toString());
                        int temp2 = Integer.parseInt(etCommissions.getText().toString());
                        int temp3 = Integer.parseInt(etDeductable.getText().toString());

                        txtFinalSalary.setText(String.valueOf(temp1 + temp2 - temp3));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        etFixedAmount.addTextChangedListener(textWatcher);
        etCommissions.addTextChangedListener(textWatcher);
        etDeductable.addTextChangedListener(textWatcher);

        Button btnAddSalary = dialogAdd.findViewById(R.id.btnAddSalary);

        btnAddSalary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtChooseStaff.getText().toString().isEmpty()) {
                    txtChooseStaff.setError("Choose Staff");
                } else if (txtChooseMonth.getText().toString().isEmpty()) {
                    txtChooseMonth.setError("Choose month");
                } else if (txtStatus.getText().toString().isEmpty()) {
                    txtStatus.setError("select Status");
                } else if (txtSalaryDate.getText().toString().isEmpty()) {
                    txtSalaryDate.setError("select Date");
                } else if (etFixedAmount.getText().toString().isEmpty()) {
                    etFixedAmount.setError("enter fixed amount");
                } else if (etCommissions.getText().toString().isEmpty()) {
                    etCommissions.setError("enter commision amount");
                } else if (etDeductable.getText().toString().isEmpty()) {
                    etDeductable.setError("enter deductable amount");
                } else if (etNote.getText().toString().isEmpty()) {
                    etNote.setError("Write a note");
                } else {
                    dialogAdd.dismiss();
                    if (NetworkUtils.isNetworkConnected(getActivity())) {

                        addSalary(
                                staffId,
                                salaryMonthAdd,
                                salaryYearAdd,
                                txtStatus.getText().toString(),
                                txtSalaryDate.getText().toString(),
                                etFixedAmount.getText().toString(),
                                etCommissions.getText().toString(),
                                etDeductable.getText().toString(),
                                etNote.getText().toString()
                        );

                    } else {
                        showSnackBarRed("No internet");

                    }
                }

            }

        });


        dialogAdd.show();


    }

    private void openUpdateDialog(SalaryModel salaryModel, int pos, String id) {

        dialogUpdate = new BottomSheetDialog(getActivity());
        dialogUpdate.setContentView(R.layout.edit_salary_dialog);

        //initialisingViews
        TextView txtChooseStaffEdit = dialogUpdate.findViewById(R.id.txtChooseStaffEdit);
        TextView txtChooseMonthEdit = dialogUpdate.findViewById(R.id.txtChooseMonthEdit);
        TextView txtStatusEdit = dialogUpdate.findViewById(R.id.txtStatusEdit);
        TextView txtSalaryDateEdit = dialogUpdate.findViewById(R.id.txtSalaryDateEdit);
        EditText etFixedAmountEdit = dialogUpdate.findViewById(R.id.etFixedAmountEdit);
        EditText etCommissionsEdit = dialogUpdate.findViewById(R.id.etCommissionsEdit);
        EditText etDeductableEdit = dialogUpdate.findViewById(R.id.etDeductableEdit);
        EditText etNoteEdit = dialogUpdate.findViewById(R.id.etNoteEdit);
        TextView txtFinalSalaryEdit = dialogUpdate.findViewById(R.id.txtFinalSalaryEdit);
        TextView txtCheckInEdit = dialogUpdate.findViewById(R.id.txtCheckInEdit);
        TextView txtLeavesEdit = dialogUpdate.findViewById(R.id.txtLeavesEdit);
        Button btnUpdateSalary = dialogUpdate.findViewById(R.id.btnUpdateSalaryEdit);
        Button btnDelete = dialogUpdate.findViewById(R.id.btnDeleteEdit);

        //populatingData
        txtChooseMonthEdit.setText(new StringBuilder().append(getMonthFromInt(salaryModel.getSalarymonth()) + ", " + salaryModel.getSalaryyear()));
        txtStatusEdit.setText(salaryModel.getSalarystatus());
        txtSalaryDateEdit.setText(salaryModel.getSalarydate());
        etFixedAmountEdit.setText(salaryModel.getSalaryfixed());
        etCommissionsEdit.setText(salaryModel.getSalarycommisions());
        etDeductableEdit.setText(salaryModel.getSalarydeductable());
        txtFinalSalaryEdit.setText(new StringBuilder().append(Integer.parseInt(salaryModel.getSalaryfixed()) + Integer.parseInt(salaryModel.getSalarycommisions()) - Integer.parseInt(salaryModel.getSalarydeductable())));
        etNoteEdit.setText(salaryModel.getNote());
        salaryMonthEdit = salaryModel.getSalarymonth();
        salaryYearEdit = salaryModel.getSalaryyear();

        getStaffFromId(salaryModel.getStaffid());
        //getAttendance(salaryModel.getStaffid(), salaryModel.getSalarymonth(), salaryModel.getSalaryyear());

//        txtCheckInEdit.setText(new StringBuilder().append(attendanceModelList.get(0).getCheckins() + " Checkin"));
//        txtLeavesEdit.setText(new StringBuilder().append(attendanceModelList.get(0).getLeaves() + " Leaves"));

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                {
                    if (!etFixedAmountEdit.getText().toString().equals("") && !etCommissionsEdit.getText().toString().equals("") && !etDeductableEdit.getText().toString().equals("")) {
                        int temp1 = Integer.parseInt(etFixedAmountEdit.getText().toString());
                        int temp2 = Integer.parseInt(etCommissionsEdit.getText().toString());
                        int temp3 = Integer.parseInt(etDeductableEdit.getText().toString());

                        txtFinalSalaryEdit.setText(String.valueOf(temp1 + temp2 - temp3));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        etFixedAmountEdit.addTextChangedListener(textWatcher);
        etCommissionsEdit.addTextChangedListener(textWatcher);
        etDeductableEdit.addTextChangedListener(textWatcher);

        txtChooseMonthEdit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                dialogMonth = new BottomSheetDialog(getActivity());
                dialogMonth.setContentView(R.layout.month_filter_dialog);
                RadioButton check_month1 = dialogMonth.findViewById(R.id.check_month1);
                RadioButton check_month2 = dialogMonth.findViewById(R.id.check_month2);
                RadioButton check_month3 = dialogMonth.findViewById(R.id.check_month3);
                RadioButton check_month4 = dialogMonth.findViewById(R.id.check_month4);
                RadioButton check_month5 = dialogMonth.findViewById(R.id.check_month5);

                RadioGroup rg = (RadioGroup) dialogMonth.findViewById(R.id.radioGroup1);

                YearMonth thisMonth = YearMonth.now();
                YearMonth lastMonth = thisMonth.minusMonths(1);
                YearMonth twoMonthsAgo = thisMonth.minusMonths(2);
                YearMonth threeMonthsAgo = thisMonth.minusMonths(3);
                YearMonth fourMonthsAgo = thisMonth.minusMonths(4);

                DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMM,  yyyy");
                DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM");
                DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy");

                String month1 = thisMonth.format(monthYearFormatter);
                String month2 = lastMonth.format(monthYearFormatter);
                String month3 = twoMonthsAgo.format(monthYearFormatter);
                String month4 = threeMonthsAgo.format(monthYearFormatter);
                String month5 = fourMonthsAgo.format(monthYearFormatter);

                String mon1 = thisMonth.format(monthFormatter);
                String mon2 = lastMonth.format(monthFormatter);
                String mon3 = twoMonthsAgo.format(monthFormatter);
                String mon4 = threeMonthsAgo.format(monthFormatter);
                String mon5 = fourMonthsAgo.format(monthFormatter);

                String year1 = thisMonth.format(yearFormatter);
                String year2 = lastMonth.format(yearFormatter);
                String year3 = twoMonthsAgo.format(yearFormatter);
                String year4 = threeMonthsAgo.format(yearFormatter);
                String year5 = fourMonthsAgo.format(yearFormatter);

                check_month1.setText(month1);
                check_month2.setText(month2);
                check_month3.setText(month3);
                check_month4.setText(month4);
                check_month5.setText(month5);


                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.check_month1:

                                txtChooseMonthEdit.setText(month1);
                                salaryMonthEdit = getIntFromMonth(mon1);
                                salaryYearEdit = year1;
                                dialogMonth.dismiss();
                                break;
                            case R.id.check_month2:
                                txtChooseMonthEdit.setText(month2);
                                salaryMonthEdit = getIntFromMonth(mon2);
                                salaryYearEdit = year2;
                                dialogMonth.dismiss();
                                break;
                            case R.id.check_month3:
                                txtChooseMonthEdit.setText(month3);
                                salaryMonthEdit = getIntFromMonth(mon3);
                                salaryYearEdit = year3;
                                dialogMonth.dismiss();
                                break;
                            case R.id.check_month4:
                                txtChooseMonthEdit.setText(month4);
                                salaryMonthEdit = getIntFromMonth(mon4);
                                salaryYearEdit = year4;
                                dialogMonth.dismiss();
                                break;
                            case R.id.check_month5:
                                txtChooseMonthEdit.setText(month5);
                                salaryMonthEdit = getIntFromMonth(mon5);
                                salaryYearEdit = year5;
                                dialogMonth.dismiss();
                                break;

                        }
                    }
                });
                dialogMonth.show();
            }
        });

        txtStatusEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogStatus = new BottomSheetDialog(getActivity());
                dialogStatus.setContentView(R.layout.status_dialog);

                RadioButton check_paid = dialogStatus.findViewById(R.id.check_paid);
                RadioButton check_unpaid = dialogStatus.findViewById(R.id.check_unpaid);

                RadioGroup radioStatus = (RadioGroup) dialogStatus.findViewById(R.id.radioGroup2);

                radioStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.check_paid:
                                txtStatusEdit.setText("Paid");
                                dialogStatus.dismiss();
                                break;
                            case R.id.check_unpaid:
                                txtStatusEdit.setText("Unpaid");
                                dialogStatus.dismiss();
                                break;

                        }
                    }
                });

                dialogStatus.show();
            }
        });

        txtSalaryDateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker(txtSalaryDateEdit);
            }
        });

        btnUpdateSalary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtChooseStaffEdit.getText().toString().isEmpty()) {
                    txtChooseStaffEdit.setError("Choose Staff");
                } else if (txtChooseMonthEdit.getText().toString().isEmpty()) {
                    txtChooseMonthEdit.setError("Choose month");
                } else if (txtStatusEdit.getText().toString().isEmpty()) {
                    txtStatusEdit.setError("select Status");
                } else if (txtSalaryDateEdit.getText().toString().isEmpty()) {
                    txtSalaryDateEdit.setError("select Date");
                } else if (etFixedAmountEdit.getText().toString().isEmpty()) {
                    etFixedAmountEdit.setError("enter fixed amount");
                } else if (etCommissionsEdit.getText().toString().isEmpty()) {
                    etCommissionsEdit.setError("enter commision amount");
                } else if (etDeductableEdit.getText().toString().isEmpty()) {
                    etDeductableEdit.setError("enter deductable amount");
                } else if (etNoteEdit.getText().toString().isEmpty()) {
                    etNoteEdit.setError("Write a note");
                } else {
                    if (NetworkUtils.isNetworkConnected(getActivity())) {

                        updateSalary(
                                txtChooseStaffEdit.getText().toString(),
                                salaryMonthEdit,
                                salaryYearEdit,
                                txtStatusEdit.getText().toString(),
                                txtSalaryDateEdit.getText().toString(),
                                etFixedAmountEdit.getText().toString(),
                                etCommissionsEdit.getText().toString(),
                                etDeductableEdit.getText().toString(),
                                etNoteEdit.getText().toString(),
                                salaryModel.getId(),
                                salaryModel.getFirstname(),
                                salaryModel.getLastname(),
                                pos
                        );
                    } else {
                        showSnackBarRed("No internet");
                    }

                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!NetworkUtils.isNetworkConnected(getActivity())) {
                    showSnackBarRed("No internet");
                    swipe_refresh.setRefreshing(false);
                } else {
                    deleteData(id, pos);


                    dialogUpdate.dismiss();

                }
            }
        });

        dialogUpdate.show();

    }

    private void getSalaryData(String month, String year) {


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.pidu.in/mybiz/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiPlaceHolder retrofitAPI = retrofit.create(ApiPlaceHolder.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("apikey", "164310030161efb88d2d888");
        jsonObject.addProperty("userid", "1");
        jsonObject.addProperty("storeid", "158");
        jsonObject.addProperty("month", month);
        jsonObject.addProperty("year", year);


        Call<ResponseBody> call = retrofitAPI.getList(jsonObject);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {

                        try {
                            JSONObject json = new JSONObject(response.body().string());

                            if (json.getString("status").equals("success")) {


                                JSONObject json1 = new JSONObject(json.get("data").toString());

                                Iterator x = json1.keys();
                                //GetStoreModel[] storeModels = new GetStoreModel[json1.length()];

                                salaryModelList.clear();
                                while (x.hasNext()) {
                                    String key = (String) x.next();

                                    JSONObject json2 = new JSONObject(json1.get(key).toString());


                                    salaryModelList.add(new SalaryModel(
                                            json2.get("id") + "",
                                            json2.get("staffid") + "",
                                            json2.get("firstname") + "",
                                            json2.get("lastname") + "",
                                            json2.get("salarymonth") + "",
                                            json2.get("salaryyear") + "",
                                            json2.get("salarystatus") + "",
                                            json2.get("salarydate") + "",
                                            json2.get("salaryfixed") + "",
                                            json2.get("salarycommisions") + "",
                                            json2.get("salarydeductable") + "",
                                            json2.get("note") + ""

                                    ));
                                }

                            } else {
                                showSnackBarRed("No Salary Found");

                                onListLoadSuccess(salaryModelList);


                            }

                            btnDate.setText(new StringBuilder().append(getMonthFromInt(month)+ ",  " + year));

                            shimmerFrameLayout.stopShimmer();
                            shimmerFrameLayout.setVisibility(View.INVISIBLE);
                            swipe_refresh.setVisibility(View.VISIBLE);
                            hideLoading();
                            swipe_refresh.setRefreshing(false);


                            //recycle view
                            onListLoadSuccess(salaryModelList);


                        } catch (IOException | JSONException e) {
                            hideLoading();

                            swipe_refresh.setRefreshing(false);
                        }
                    }
                } else {
                    hideLoading();
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.INVISIBLE);
                    swipe_refresh.setVisibility(View.VISIBLE);

                    showSnackBarRed("Something Error!!");

                    swipe_refresh.setRefreshing(false);
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hideLoading();

                showSnackBarRed(String.valueOf(t));

                swipe_refresh.setRefreshing(false);
            }
        });

    }

    private void addSalary(String staff,
                           String month,
                           String year,
                           String status,
                           String date,
                           String fixed,
                           String commision,
                           String deductable,
                           String note) {

        showLoading(getActivity());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.pidu.in/mybiz/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiPlaceHolder retrofitAPI = retrofit.create(ApiPlaceHolder.class);


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("apikey", "164310030161efb88d2d888");
        jsonObject.addProperty("userid", "1");
        jsonObject.addProperty("storeid", "158");
        jsonObject.addProperty("staffid", staff);
        jsonObject.addProperty("month", month);
        jsonObject.addProperty("year", year);
        jsonObject.addProperty("status", status);
        jsonObject.addProperty("salarydate", date);
        jsonObject.addProperty("fixedamount", fixed);
        jsonObject.addProperty("commission", commision);
        jsonObject.addProperty("deductable", deductable);
        jsonObject.addProperty("note", note);

        Call<ResponseBody> call = retrofitAPI.addUser(jsonObject);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.code() == 200) {
                    if (response.body() != null) {

                        try {
                            JSONObject json = new JSONObject(response.body().string());

                            if (json.getString("status").equals("success")) {
                                hideLoading();
                                dialogAdd.dismiss();
                                getSalaryData(month, year);
                                showSnackBarGreen("Salary Added Successfully");

                            } else {
                                dialogAdd.dismiss();
                                hideLoading();
                                showSnackBarRed("Error Occurred!..");
                            }

                        } catch (IOException | JSONException e) {
                            dialogAdd.dismiss();
                            hideLoading();
                            showSnackBarGreen(e.toString());
                        }
                    } else {
                        dialogAdd.dismiss();
                        showSnackBarGreen("Something Error");
                        hideLoading();
                    }
                } else {
                    dialogAdd.dismiss();
                    showSnackBarGreen("Something Error");
                    hideLoading();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogAdd.dismiss();
                showSnackBarGreen(t.toString());
                hideLoading();
            }
        });


    }

    private void updateSalary(String staff,
                              String month,
                              String year,
                              String status,
                              String date,
                              String fixedAmount,
                              String commisions,
                              String deductable,
                              String note,
                              String id,
                              String firstName,
                              String lastName,
                              int pos) {

        showLoading(getActivity());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.pidu.in/mybiz/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiPlaceHolder retrofitAPI = retrofit.create(ApiPlaceHolder.class);


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("apikey", "164310030161efb88d2d888");
        jsonObject.addProperty("userid", "1");
        jsonObject.addProperty("storeid", "158");
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("staffid", staff);
        jsonObject.addProperty("month", month);
        jsonObject.addProperty("year", year);
        jsonObject.addProperty("status", status);
        jsonObject.addProperty("salarydate", date);
        jsonObject.addProperty("fixedamount", fixedAmount);
        jsonObject.addProperty("commission", commisions);
        jsonObject.addProperty("deductable", deductable);
        jsonObject.addProperty("note", note);

        Call<ResponseBody> call = retrofitAPI.updateUser(jsonObject);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.code() == 200) {
                    if (response.body() != null) {

                        try {
                            JSONObject json = new JSONObject(response.body().string());

                            if (json.getString("status").equals("success")) {

                                dialogUpdate.dismiss();
                                hideLoading();
                                showSnackBarGreen("Salary Updated");
                                SalaryModel salaryModel = new SalaryModel(id, staff, firstName, lastName, month, year, status, date, fixedAmount, commisions, deductable, note);
                                salaryModelList.set(pos, salaryModel);
                                salaryListAdapter.notifyDataSetChanged();
                            } else {
                                hideLoading();
                                dialogUpdate.dismiss();
                            }


                        } catch (IOException | JSONException e) {
                            hideLoading();
                            showSnackBarRed(e.toString());

                        }
                    } else {
                        hideLoading();
                    }
                } else {
                    hideLoading();
                    showSnackBarRed("error");

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hideLoading();
                showSnackBarRed("error");

            }
        });
    }

    private void deleteData(String id, int pos) {

        showLoading(getActivity());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.pidu.in/mybiz/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiPlaceHolder retrofitAPI = retrofit.create(ApiPlaceHolder.class);


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("apikey", "164310030161efb88d2d888");
        jsonObject.addProperty("userid", "1");
        jsonObject.addProperty("storeid", "158");
        jsonObject.addProperty("id", id);

        Call<ResponseBody> call = retrofitAPI.deleteUser(jsonObject);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.code() == 200) {
                    if (response.body() != null) {

                        try {
                            JSONObject json = new JSONObject(response.body().string());

                            if (json.getString("status").equals("success")) {

                                salaryModelList.remove(pos);
                                salaryListAdapter.notifyDataSetChanged();


                                showSnackBarGreen(json.getString("message"));
                            } else {
                                hideLoading();
                            }


                        } catch (IOException | JSONException e) {
                            hideLoading();

                        }
                    } else {
                        hideLoading();
                    }
                } else {
                    hideLoading();

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hideLoading();

            }
        });
    }

    private void getStaff(RecyclerView rvChooseStaff) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.pidu.in/mybiz/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiPlaceHolder retrofitAPI = retrofit.create(ApiPlaceHolder.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("apikey", "164310030161efb88d2d888");
        jsonObject.addProperty("userid", "1");
        jsonObject.addProperty("storeid", "158");

        Call<ResponseBody> call = retrofitAPI.getStaff(jsonObject);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {

                        try {
                            JSONObject json = new JSONObject(response.body().string());
                            JSONObject json1 = new JSONObject(json.get("data").toString());

                            Iterator x = json1.keys();
                            //GetStoreModel[] storeModels = new GetStoreModel[json1.length()];

                            staffModelList.clear();
                            while (x.hasNext()) {
                                String key = (String) x.next();

                                JSONObject json2 = new JSONObject(json1.get(key).toString());
                                staffModelList.add(new StaffModel(json2.get("id") + "",
                                        json2.get("firstname") + "",
                                        json2.get("lastname") + ""
                                ));


                            }

                            //recycle view
                            showSnackBarGreen(staffModelList.get(0).getFirstname());
                            staffAdapter staffAdapter = new staffAdapter(getActivity(), staffModelList, SalaryListFragment.this);
                            rvChooseStaff.setAdapter(staffAdapter);


                        } catch (IOException | JSONException e) {
                            showSnackBarRed(e.toString());
                        }
                    }
                } else {

                    showSnackBarRed("error");
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                showSnackBarRed("error");
            }
        });

    }

    private void getStaffFromId(String id) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.pidu.in/mybiz/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiPlaceHolder retrofitAPI = retrofit.create(ApiPlaceHolder.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("apikey", "164310030161efb88d2d888");
        jsonObject.addProperty("userid", "1");
        jsonObject.addProperty("storeid", "158");

        Call<ResponseBody> call = retrofitAPI.getStaff(jsonObject);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {

                        try {
                            JSONObject json = new JSONObject(response.body().string());
                            JSONObject json1 = new JSONObject(json.get("data").toString());

                            Iterator x = json1.keys();
                            //GetStoreModel[] storeModels = new GetStoreModel[json1.length()];

                            staffModelList.clear();
                            while (x.hasNext()) {
                                String key = (String) x.next();

                                JSONObject json2 = new JSONObject(json1.get(key).toString());
                                staffModelList.add(new StaffModel(
                                        json2.get("id") + "",
                                        json2.get("firstname") + "",
                                        json2.get("lastname") + ""
                                ));


                            }

                            TextView btnChooseStaffEdit = dialogUpdate.findViewById(R.id.txtChooseStaffEdit);

                            for (int i = 0; i < staffModelList.size(); i++) {

                                if (staffModelList.get(i).getId().equals(id)) {
                                    btnChooseStaffEdit.setText(new StringBuilder().append(staffModelList.get(i).getFirstname()+ " " +staffModelList.get(i).getLastname()));
                                }
                            }



                        } catch (IOException | JSONException e) {
                            showSnackBarRed(e.toString());
                        }
                    }
                } else {

                    showSnackBarRed("error");
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                showSnackBarRed("error");
            }
        });

    }

    private void getAttendance(String id, String month, String year) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.pidu.in/mybiz/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiPlaceHolder retrofitAPI = retrofit.create(ApiPlaceHolder.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("apikey", "164310030161efb88d2d888");
        jsonObject.addProperty("userid", "1");
        jsonObject.addProperty("storeid", "158");
        jsonObject.addProperty("staffid", id);
        jsonObject.addProperty("month", month);
        jsonObject.addProperty("year", year);


        Call<ResponseBody> call = retrofitAPI.getAttendance(jsonObject);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {

                        try {
                            JSONObject json = new JSONObject(response.body().string());
                            JSONObject json1 = new JSONObject(json.get("data").toString());


                            //GetStoreModel[] storeModels = new GetStoreModel[json1.length()];

                            attendanceModelList.clear();


                            JSONObject json2 = new JSONObject(json1.toString());
                            attendanceModelList.add(new AttendanceModel(
                                    json2.get("checkins") + "",
                                    json2.get("leaves") + ""

                            ));


                            //recycle view
                            showSnackBarGreen(attendanceModelList.get(0).getCheckins());


                        } catch (IOException | JSONException e) {
                            showSnackBarRed(e.toString());
                        }
                    }
                } else {

                    showSnackBarRed("Something Error!!");
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showSnackBarRed(String.valueOf(t));
            }
        });


    }


    private void init() {
        salaryListLoadListener = SalaryListFragment.this;

    }

    private void datePicker(TextView txtView){

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.DialogTheme,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth) {

                        date_time = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        txtView.setText(date_time);
                    }
                }, mYear, mMonth, mDay);


        datePickerDialog.show();

    }

    public String getIntFromMonth(String month) {
        if (month.equals("Jan")) {
            monthVal = "1";
        } else if (month.equals("Feb")) {
            monthVal = "2";
        } else if (month.equals("Mar")) {
            monthVal = "3";
        } else if (month.equals("Apr")) {
            monthVal = "4";
        } else if (month.equals("May")) {
            monthVal = "5";
        } else if (month.equals("Jun")) {
            monthVal = "6";
        } else if (month.equals("Jul")) {
            monthVal = "7";
        } else if (month.equals("Aug")) {
            monthVal = "8";
        } else if (month.equals("Sep")) {
            monthVal = "9";
        } else if (month.equals("Oct")) {
            monthVal = "10";
        } else if (month.equals("Nov")) {
            monthVal = "11";
        } else {
            monthVal = "12";
        }

        return monthVal;
    }

    public String getMonthFromInt(String month) {
        if (month.equals("1")) {
            monthVal = "Jan";
        } else if (month.equals("2")) {
            monthVal = "Feb";
        } else if (month.equals("3")) {
            monthVal = "Mar";
        } else if (month.equals("4")) {
            monthVal = "Apr";
        } else if (month.equals("5")) {
            monthVal = "May";
        } else if (month.equals("6")) {
            monthVal = "Jun";
        } else if (month.equals("7")) {
            monthVal = "Jul";
        } else if (month.equals("8")) {
            monthVal = "Aug";
        } else if (month.equals("9")) {
            monthVal = "Sep";
        } else if (month.equals("10")) {
            monthVal = "Oct";
        } else if (month.equals("11")) {
            monthVal = "Nov";
        } else {
            monthVal = "Dec";
        }

        return monthVal;
    }

    //SearchFilter
    private void filter(String text) {
        ArrayList<SalaryModel> filteredList = new ArrayList<>();
        for (SalaryModel item : salaryModelList) {
            if (item.getFirstname().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        if (filteredList.isEmpty()) {
        } else {
            salaryListAdapter.filterList(filteredList);
        }
    }

    //Interface
    @Override
    public void onListLoadSuccess(List<SalaryModel> salaryModelList) {
        salaryListAdapter = new salaryListAdapter(this.getContext(), salaryModelList, this);

        if (salaryListAdapter.getItemCount() != 0) {
            imgEmptyDisplay.setVisibility(View.INVISIBLE);
            swipe_refresh.setVisibility(View.VISIBLE);


        } else {
            swipe_refresh.setVisibility(View.INVISIBLE);
            imgEmptyDisplay.setVisibility(View.VISIBLE);


        }

        rvSalaryList.setAdapter(salaryListAdapter);
    }

    //Interface
    @Override
    public void onListLoadFailed(String message) {
        Snackbar.make(getActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    //Interface
    @Override
    public void updateItem(SalaryModel itemViewModel, int pos, String value) {
        openUpdateDialog(itemViewModel, pos, value);
    }


    @Override
    public void getStaff(StaffModel itemViewModel, int pos, String value) {
        TextView btnChooseStaff = dialogAdd.findViewById(R.id.txtChooseStaff);
        btnChooseStaff.setText(new StringBuilder().append(itemViewModel.getFirstname() + " " + itemViewModel.getLastname()));
        staffId = itemViewModel.getId();
        dialogStaff.dismiss();
    }
}



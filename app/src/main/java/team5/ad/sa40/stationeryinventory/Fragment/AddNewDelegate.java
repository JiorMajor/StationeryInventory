package team5.ad.sa40.stationeryinventory.Fragment;


import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import team5.ad.sa40.stationeryinventory.API.DelegateAPI;
import team5.ad.sa40.stationeryinventory.API.EmployeeAPI;
import team5.ad.sa40.stationeryinventory.Model.JSONDelegate;
import team5.ad.sa40.stationeryinventory.Model.JSONEmployee;
import team5.ad.sa40.stationeryinventory.R;
import team5.ad.sa40.stationeryinventory.Utilities.Setup;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddNewDelegate extends android.support.v4.app.Fragment implements MainActivity.OnBackPressedListener {

    @Bind(R.id.spnEmp)Spinner spnEmp;
    @Bind(R.id.txtStartDate)EditText text_start_date;
    @Bind(R.id.txtEndDate)EditText text_end_date;
    @Bind(R.id.btnSubmit)Button btnSubmit;
    @Bind(R.id.spnStat)Spinner spnStat;

    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private List<JSONEmployee> employeeList;
    private String flag = "New";
    String[] stat_ary = {"Select Status","LEAVE/OFF", "MEDICAL LEAVE", "OUTSTATION"};
    String temp1 = "Create"; String temp2 = "create";

    public AddNewDelegate() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_new_delegate, container, false);
        ButterKnife.bind(this, view);

        ((MainActivity)getActivity()).setOnBackPressedListener(this);

        final ArrayAdapter<String> col_collect = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, stat_ary);
        col_collect.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spnStat.setAdapter(col_collect);

        final RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(Setup.baseurl).build();
        final DelegateAPI delegateAPI = restAdapter.create(DelegateAPI.class);
        final EmployeeAPI employeeAPI = restAdapter.create(EmployeeAPI.class);
        final Bundle bundle = this.getArguments();

        employeeAPI.getEmployeeByDeptID(Setup.user.getDeptID(), new Callback<List<JSONEmployee>>() {
            @Override
            public void success(List<JSONEmployee> jsonEmployees, Response response) {
                employeeList = jsonEmployees;
                String[] EmpNames = new String[employeeList.size()];
                String currentEmp = "";

                for(int i = 0; i < employeeList.size(); i++){
                    EmpNames[i] = employeeList.get(i).getEmpName();
                }

                getActivity().setTitle("Add Delegate");

                JSONDelegate delegate;
                if(bundle != null){
                    delegate = (JSONDelegate)bundle.getSerializable("delegate");
                    text_start_date.setText(Setup.parseJSONDateToString(delegate.getStartDate()));
                    text_end_date.setText(Setup.parseJSONDateToString(delegate.getEndDate()));
                    int rindex = col_collect.getPosition(delegate.getStatus());
                    spnStat.setSelection(rindex);
                    btnSubmit.setText("Save");
                    flag = "Current";

                    getActivity().setTitle("Edit Delegate");

                    for(int i = 0; i < employeeList.size(); i++){
                        Log.e("Emp ID", String.valueOf(employeeList.get(i).getEmpID()));
                        Log.e("Delegate empID", String.valueOf(delegate.getEmpID()));
                        if(employeeList.get(i).getEmpID().equals(delegate.getEmpID())){
                            Log.e("Flag", "True");
                            currentEmp = employeeList.get(i).getEmpName();
                            Log.e("Current Emp", currentEmp);
                        }
                    }
                }
                ArrayAdapter<String> col_collect = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,EmpNames);
                col_collect.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                spnEmp.setAdapter(col_collect);
                if(!currentEmp.equals("")){
                    int rindex = col_collect.getPosition(currentEmp);
                    spnEmp.setSelection(rindex);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("getEmployee delegate", error.toString());
            }
        });

        //Date dialog codes
        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this.getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                text_start_date.setText(Setup.parseDateToString(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog = new DatePickerDialog(this.getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                text_end_date.setText(Setup.parseDateToString(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        text_start_date.setInputType(InputType.TYPE_NULL);
        text_start_date.requestFocus();
        text_end_date.setInputType(InputType.TYPE_NULL);

        text_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromDatePickerDialog.show();
            }
        });

        text_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toDatePickerDialog.show();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!text_start_date.getText().toString().matches("") && !text_end_date.getText().toString().matches("")) {
                    Date start = Setup.parseStringToDate(text_start_date.getText().toString());
                    Date end = Setup.parseStringToDate(text_end_date.getText().toString());

                    String sflag = checkWithTodayDate(start);
                    String sflag2 = checkWithTodayDate(end);
                    if(spnStat.getSelectedItemPosition() == 0){
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Fail")
                                .setMessage("Please select the delegation status!")
                                .setCancelable(false)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                    else if (end.before(start)) {
                        text_end_date.setError("Should be after Start Date");
                    }
                    else if(sflag.equals("false")){
                        text_start_date.setError("Should not be before today");
                    }
                    else if(sflag2.equals("false")){
                        text_end_date.setError("Should not be before today");
                    }
                    else{
                        text_start_date.setError(null);
                        text_end_date.setError(null);
                        if(!flag.equals("New")){
                            temp1 = "Edit"; temp2 = "edit";
                        }
                        new AlertDialog.Builder(getActivity())
                                .setTitle(temp1 + " Delegate")
                                .setMessage("Are you sure you want to "+temp2+" this delegation?")
                                .setCancelable(false)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        JsonObject object = new JsonObject();
                                        for(int i = 0; i < employeeList.size(); i++){
                                            if(employeeList.get(i).getEmpName().equals(spnEmp.getSelectedItem().toString())){
                                                object.addProperty("EmpID", employeeList.get(i).getEmpID());
                                            }
                                        }
                                        object.addProperty("DeptID", Setup.user.getDeptID());
                                        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                                        SimpleDateFormat original_format = new SimpleDateFormat("dd/MM/yyyy");
                                        try {
                                            Date uDate1 = original_format.parse(text_start_date.getText().toString());
                                            String str_date1 = format.format(uDate1);
                                            str_date1 = str_date1.replace('/', '-');
                                            Log.e("SQLDate 1", str_date1);

                                            Date uDate2 = original_format.parse(text_end_date.getText().toString());
                                            String str_date2 = format.format(uDate2);
                                            str_date2 = str_date2.replace('/', '-');
                                            Log.e("SQLDate 2", str_date2);

                                            object.addProperty("StartDate", str_date1);
                                            object.addProperty("EndDate", str_date2);
                                            object.addProperty("Status", spnStat.getSelectedItem().toString());
                                        }
                                        catch (ParseException e){
                                            Log.e("Change new del", e.toString());
                                        }
                                        if(flag.equals("New")){
                                            delegateAPI.createDelegate(object, new Callback<Boolean>() {
                                                @Override
                                                public void success(Boolean aBoolean, Response response) {
                                                    Log.e("Checking boolean", aBoolean.toString());
                                                    new AlertDialog.Builder(getActivity())
                                                            .setTitle("Success")
                                                            .setMessage("The delegation has been created!")
                                                            .setCancelable(false)
                                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    flag = "Current";
                                                                    btnSubmit.setText("Save");
                                                                    getActivity().setTitle("Edit Delegate");
                                                                    android.support.v4.app.Fragment frag = new DelegateList();
                                                                    getFragmentManager().beginTransaction().replace(R.id.frame, frag).commit();
                                                                }
                                                            })
                                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                                            .show();
                                                }

                                                @Override
                                                public void failure(RetrofitError error) {
                                                    Log.e("createDelegate", error.toString());
                                                    new AlertDialog.Builder(getActivity())
                                                            .setTitle("Fail")
                                                            .setMessage("Cannot create the delegation!")
                                                            .setCancelable(false)
                                                            .setPositiveButton("TRY AGAIN", new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                }
                                                            })
                                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                                            .show();
                                                }
                                            });
                                        }
                                        if(flag.equals("Current")){
                                            Log.e("json", object.toString());
                                            delegateAPI.editDelegate(object, new Callback<Boolean>() {
                                                @Override
                                                public void success(Boolean aBoolean, Response response) {
                                                    Log.e("Checking boolean", aBoolean.toString());
                                                    new AlertDialog.Builder(getActivity())
                                                            .setTitle("Success")
                                                            .setMessage("The delegation has been updated!")
                                                            .setCancelable(false)
                                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    android.support.v4.app.Fragment frag = new DelegateList();
                                                                    getFragmentManager().beginTransaction().replace(R.id.frame, frag).commit();
                                                                }
                                                            })
                                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                                            .show();
                                                }

                                                @Override
                                                public void failure(RetrofitError error) {
                                                    Log.e("editDelegate", error.toString());
                                                    new AlertDialog.Builder(getActivity())
                                                            .setTitle("Fail")
                                                            .setMessage("Cannot update the delegation!")
                                                            .setCancelable(false)
                                                            .setPositiveButton("TRY AGAIN", new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                }
                                                            })
                                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                                            .show();
                                                }
                                            });
                                        }

                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                }
                else{
                    if(text_start_date.getText().toString().matches("")){
                        text_start_date.setError("Fill Data");
                    }
                    if(text_end_date.getText().toString().matches("")){
                        text_end_date.setError("Fill Data");
                    }
                }
            }
        });

        return view;
    }

    public String checkWithTodayDate(Date temp){
        SimpleDateFormat out = new SimpleDateFormat("dd/MM/yyyy");
        String today_date = out.format(new Date());
        Log.e("today date_str", today_date);
        String flag = "";
        try {
            Date today = out.parse(today_date);
            Log.e("today date", today.toString());
            Log.e("today date2", out.format(today));
            if(temp.before(today)){
                flag = "false";
            }
            else{
                flag = "true";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    public void doBack() {
        android.support.v4.app.Fragment frag = new DelegateList();
        getFragmentManager().beginTransaction().replace(R.id.frame, frag).commit();
    }
}

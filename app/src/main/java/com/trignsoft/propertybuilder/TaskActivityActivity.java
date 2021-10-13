package com.trignsoft.propertybuilder;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.trignsoft.propertybuilder.Adapters.TaskAdapter;
import com.trignsoft.propertybuilder.ConstantApis.Api;
import com.trignsoft.propertybuilder.ConstantApis.MySingleton;
import com.trignsoft.propertybuilder.Models.SpinnerData;
import com.trignsoft.propertybuilder.Models.TaskModel;
import com.trignsoft.propertybuilder.Models.UserModel;
import com.example.propertybuilder.R;
import com.trignsoft.propertybuilder.SharedPreference.SharedPrefManager;
import com.example.propertybuilder.databinding.ActivityTasksBinding;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

public class TaskActivityActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, InterFaceClass {

    private static final String TAG = "wow123";
    ActivityTasksBinding binding;
    String type, userId, userType,devNameSpin,userIdSpin;
    Dialog dialog;
    TaskAdapter taskAdapter;
    ArrayList<SpinnerData> devName= new ArrayList<>();
    private List<TaskModel> taskModelList = new ArrayList<>();

    public void adjustFontScale(Configuration configuration) {
        configuration.fontScale = (float) 1.0;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        getBaseContext().getResources().updateConfiguration(configuration, metrics);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustFontScale(getResources().getConfiguration());
        binding = ActivityTasksBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        UserModel usersModel = SharedPrefManager.getInstance(TaskActivityActivity.this).getUser();
        userId = String.valueOf(usersModel.getId());
        userType = usersModel.getTypeUser();
        if (userType.equals("admin")) {
            getTaskAdmin();
        } else {
            getTask();
        }
        toolBarSetup();
        clicks();
    }

    private void getTaskAdmin() {
        binding.progressBar.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Api.GET_ALL_TASK, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                taskModelList.clear();

                try {
                    binding.progressBar.setVisibility(View.GONE);

                    JSONObject jsonObject = new JSONObject(response);
                    for (int i = 0; i < jsonObject.length() - 2; i++) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject(i + "");
                        Log.d("wow123", "onResponse: " + jsonObject1);
                        TaskModel taskModel = new TaskModel();

                        taskModel.setTask_id(jsonObject1.getString("task_id"));
                        taskModel.setDeveloper_id(jsonObject1.getString("developer_id"));
                        taskModel.setUser_name(jsonObject1.getString("user_name"));
                        taskModel.setType(jsonObject1.getString("type"));
                        taskModel.setDate(jsonObject1.getString("date"));
                        taskModel.setTime(jsonObject1.getString("time"));
                        taskModel.setTask_status(jsonObject1.getString("task_status"));

                        taskModelList.add(taskModel);
                    }
                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(TaskActivityActivity.this));
                    taskAdapter = new TaskAdapter(TaskActivityActivity.this, taskModelList);
                    binding.recyclerView.setAdapter(taskAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MySingleton.getInstance(TaskActivityActivity.this).addToRequestQueue(stringRequest);
    }

    private void getTask() {
        binding.progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.GET_DEVELOPER_TASK, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                taskModelList.clear();

                binding.progressBar.setVisibility(View.GONE);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    for (int i = 0; i < jsonObject.length() - 2; i++) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject(i + "");
                        Log.d("wow123", "onResponse: " + jsonObject1);
                        TaskModel taskModel = new TaskModel();

                        taskModel.setTask_id(jsonObject1.getString("task_id"));
                        taskModel.setDeveloper_id(jsonObject1.getString("developer_id"));
                        taskModel.setUser_name(jsonObject1.getString("user_name"));
                        taskModel.setType(jsonObject1.getString("type"));
                        taskModel.setDate(jsonObject1.getString("date"));
                        taskModel.setTime(jsonObject1.getString("time"));
                        taskModel.setTask_status(jsonObject1.getString("task_status"));

                        taskModelList.add(taskModel);

                    }
                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(TaskActivityActivity.this));
                    taskAdapter = new TaskAdapter(TaskActivityActivity.this, taskModelList);
                    binding.recyclerView.setAdapter(taskAdapter);


                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "onResponse: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onResponse: " + error.getMessage());

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("developer_id", userId);
                return params;
            }
        };
        MySingleton.getInstance(TaskActivityActivity.this).addToRequestQueue(stringRequest);
    }

    private void clicks() {
        binding.addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onClick(View v) {
                dialog = new Dialog(TaskActivityActivity.this);
                dialog.setContentView(R.layout.add_ask_dialog);
                TextView selectDev = dialog.findViewById(R.id.select_dev_task);

                Spinner developerSpinner = dialog.findViewById(R.id.dev_spinner_task);
                developerSpinner.setOnItemSelectedListener(TaskActivityActivity.this);
                EditText personName = dialog.findViewById(R.id.task_person_name_edt);
                CheckBox inspectionCb = dialog.findViewById(R.id.task_inspection_check_box);
                CheckBox enquiryCb = dialog.findViewById(R.id.task_enquiry_check_box);
                EditText bookingCalender = dialog.findViewById(R.id.task_date_picker);
                EditText timeSlotDropEdt = dialog.findViewById(R.id.task_time_slot);
                Button add = dialog.findViewById(R.id.task_add);
                Button cancel = dialog.findViewById(R.id.task_cancel);

                if (userType.equals("developer")){
                    developerSpinner.setVisibility(View.GONE);
                    selectDev.setVisibility(View.GONE);
                }else{

                    StringRequest stringRequest = new StringRequest(Request.Method.GET, Api.GET_ALL_STAFF, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.d(TAG, "onResponse: "+response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                for (int i=0;i< jsonObject.length();i++) {
                                    JSONObject jsonObject1 = jsonObject.getJSONObject(i + "");
                                    Log.d(TAG, "onResponse jason1: " + jsonObject1);
//                                    devName.add((jsonObject1.getString("")));
                                    devName.add(new SpinnerData(jsonObject1.getString("id"), jsonObject1.getString("full_name")));
//                                    devName.add(jsonObject1.getString("full_name"));
                                    Log.d(TAG, "onResponse:devName " + devName);
                                    ArrayAdapter aa = new ArrayAdapter(TaskActivityActivity.this,android.R.layout.simple_spinner_item,devName);
                                    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    developerSpinner.setAdapter(aa);

                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d(TAG, "onResponse: "+e.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "onResponse: "+error.getMessage());
                        }
                    });
                    MySingleton.getInstance(TaskActivityActivity.this).addToRequestQueue(stringRequest);
                }

                inspectionCb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (inspectionCb.isChecked()) {
                            enquiryCb.setChecked(false);
                            type = inspectionCb.getText().toString();
                        } else {
                            type = null;
                        }

                    }
                });
                enquiryCb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (enquiryCb.isChecked()) {
                            inspectionCb.setChecked(false);
                            type = enquiryCb.getText().toString();
                        } else {
                            type = null;
                        }
                    }
                });
                bookingCalender.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePickerDialog picker;
                        final Calendar cldr = Calendar.getInstance();
                        int day = cldr.get(Calendar.DAY_OF_MONTH);
                        int month = cldr.get(Calendar.MONTH);
                        int year = cldr.get(Calendar.YEAR);
                        picker = new DatePickerDialog(TaskActivityActivity.this,
                                new DatePickerDialog.OnDateSetListener() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                        bookingCalender.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
//                                        date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                        Log.d("wow123", "onDateSet: " + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                        bookingCalender.setError(null);
                                    }
                                }, year, month, day);
                        picker.show();
                    }
                });
                timeSlotDropEdt.setOnTouchListener(new View.OnTouchListener() {
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            PopupMenu popup = new PopupMenu(TaskActivityActivity.this, timeSlotDropEdt);
                            //Inflating the Popup using xml file
                            popup.getMenuInflater().inflate(R.menu.time_slots, popup.getMenu());
                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                public boolean onMenuItemClick(MenuItem item) {
                                    String timeSlot = String.valueOf(item.getTitle());
                                    timeSlotDropEdt.setText(timeSlot);
                                    timeSlotDropEdt.setError(null);

                                    return true;
                                }
                            });
                            popup.show();

                            return true;
                        }
                        return false;
                    }
                });

                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (userType.equals("developer")){
                            String name = personName.getText().toString();
                            String bookingDate = String.valueOf(bookingCalender.getText());
                            String bookingTimeSlot = String.valueOf(timeSlotDropEdt.getText());
                            if (name.isEmpty()) {
                                personName.setError("Enter Person Name");
                                personName.requestFocus();
                            } else if (type == null) {
                                Toast.makeText(TaskActivityActivity.this, "Select Type", Toast.LENGTH_SHORT).show();
                            } else if (bookingDate.isEmpty()) {
                                bookingCalender.setError("Select Date");
                                bookingCalender.requestFocus();
                            } else if (bookingTimeSlot.isEmpty()) {
                                timeSlotDropEdt.setError("Select Time Slot");
                                timeSlotDropEdt.requestFocus();
                            } else
                                addTask(userId, name, type, bookingDate, bookingTimeSlot);
                        }else if (userType.equals("admin")){
                            String name = personName.getText().toString();
                            String bookingDate = String.valueOf(bookingCalender.getText());
                            String bookingTimeSlot = String.valueOf(timeSlotDropEdt.getText());
                            if (name.isEmpty()) {
                                personName.setError("Enter Person Name");
                                personName.requestFocus();
                            } else if (type == null) {
                                Toast.makeText(TaskActivityActivity.this, "Select Type", Toast.LENGTH_SHORT).show();
                            } else if (bookingDate.isEmpty()) {
                                bookingCalender.setError("Select Date");
                                bookingCalender.requestFocus();
                            } else if (bookingTimeSlot.isEmpty()) {
                                timeSlotDropEdt.setError("Select Time Slot");
                                timeSlotDropEdt.requestFocus();
                            } else
                                addTask(userIdSpin, name, type, bookingDate, bookingTimeSlot);
                        }

                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                dialog.setCancelable(false);
                Window window = dialog.getWindow();
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        });
    }

    private void addTask(String userId, String name, String type, String bookingDate, String timeSlotDropEdt) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.ADD_TASK, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (userType.equals("admin")){
                    getTaskAdmin();
                }else{
                    getTask();
                }
                dialog.dismiss();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
//                params.put("developer_id", String.valueOf(userId));
                params.put("developer_id", userId);
                params.put("user_name", name);
                params.put("type", type);
                params.put("date", String.valueOf(bookingDate));
                params.put("time", String.valueOf(timeSlotDropEdt));

                return params;
            }
        };
        MySingleton.getInstance(TaskActivityActivity.this).addToRequestQueue(stringRequest);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void toolBarSetup() {
        binding.toolBar.setTitle("Tasks");
        binding.toolBar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_baseline_arrow_back_24));
        binding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        SpinnerData developer = (SpinnerData) parent.getSelectedItem();
        userIdSpin = developer.getId();
         devNameSpin = developer.getName();
//        Toast.makeText(this, "Country ID: "+developer.getId()+",  Country Name : "+developer.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void getAdminTask() {
        getTaskAdmin();
    }

    @Override
    public void getDeveloperTask() {
        getTask();
    }
}
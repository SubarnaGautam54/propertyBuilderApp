package com.trignsoft.propertybuilder.Adapters;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.trignsoft.propertybuilder.ConstantApis.Api;
import com.trignsoft.propertybuilder.ConstantApis.MySingleton;
import com.trignsoft.propertybuilder.InterFaceClass;
import com.trignsoft.propertybuilder.Models.SpinnerData;
import com.trignsoft.propertybuilder.Models.TaskModel;
import com.trignsoft.propertybuilder.Models.UserModel;
import com.example.propertybuilder.R;
import com.trignsoft.propertybuilder.SharedPreference.SharedPrefManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder>{
    private static final String TAG = "wow123";
    private Context context;
    private List<TaskModel> taskModelList;
    ArrayList<SpinnerData> devName= new ArrayList<>();
    String type, userId, userType,devNameSpin,userIdSpin;
    Dialog dialog1,dialog;


    public TaskAdapter(Context context, List<TaskModel> taskModelList) {
        this.context = context;
        this.taskModelList = taskModelList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_items, parent, false);
        return new TaskViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.task.setText(taskModelList.get(position).getUser_name()+" has "+taskModelList.get(position).getType()+" for "+
                taskModelList.get(position).getDate()+" at "+taskModelList.get(position).getTime());
        UserModel usersModel = SharedPrefManager.getInstance(context).getUser();
        String userType = usersModel.getTypeUser();
        if (userType.equals("developer")){
            if (taskModelList.get(position).getTask_status().equals("pending")){
                holder.done.setVisibility(View.VISIBLE);
                holder.checkMark.setVisibility(View.GONE);
            }else {
                holder.done.setVisibility(View.GONE);
                holder.checkMark.setVisibility(View.VISIBLE);
            }
        }else if (userType.equals("admin")){
            if (taskModelList.get(position).getTask_status().equals("pending")){
                holder.done.setVisibility(View.GONE);
                holder.checkMark.setVisibility(View.GONE);
                holder.pending.setVisibility(View.VISIBLE);
            }else {
                holder.done.setVisibility(View.GONE);
                holder.pending.setVisibility(View.GONE);
                holder.checkMark.setVisibility(View.VISIBLE);
            }
        }

        holder.done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.UPDATE_TASK_STATUS, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        holder.checkMark.setVisibility(View.VISIBLE);
                        holder.done.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("wow123", "onErrorResponse: "+error.getMessage());
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<>();
                        params.put("task_id",taskModelList.get(position).getTask_id());
                        params.put("task_status","done");
                        return params;
                    }
                };
                MySingleton.getInstance(context).addToRequestQueue(stringRequest);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.edit_task_dialog);

                LinearLayout deleteTask = dialog.findViewById(R.id.delete_task_ly);
                LinearLayout updateTask = dialog.findViewById(R.id.update_task_ly);

                updateTask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        devName.clear();
                         dialog1 = new Dialog(context);
                        dialog1.setContentView(R.layout.add_ask_dialog);

                        UserModel usersModel = SharedPrefManager.getInstance(context).getUser();
                        String userType = usersModel.getTypeUser();
                        TextView spinName =  dialog1.findViewById(R.id.select_dev_task);
                        Spinner developerSpinner = dialog1.findViewById(R.id.dev_spinner_task);


//                        developerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                            @Override
//                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                                SpinnerData developer = (SpinnerData) parent.getSelectedItem();
//                                userIdSpin = developer.getId();
//                                Log.d(TAG, "onItemSelected: "+userIdSpin);
//                                devNameSpin = developer.getName();
//                            }
//
//                            @Override
//                            public void onNothingSelected(AdapterView<?> parent) {
//
//                            }
//                        });

                        EditText personName = dialog1.findViewById(R.id.task_person_name_edt);
                        personName.setText(taskModelList.get(position).getUser_name());
                        CheckBox inspectionCb = dialog1.findViewById(R.id.task_inspection_check_box);
                        CheckBox enquiryCb = dialog1.findViewById(R.id.task_enquiry_check_box);
                        if (taskModelList.get(position).getType().equals("inspection")){
                            inspectionCb.setChecked(true);
                            type = "inspection";
                        }else {
                            enquiryCb.setChecked(true);
                            type = "enquiry";
                        }
                        EditText bookingCalender = dialog1.findViewById(R.id.task_date_picker);
                        bookingCalender.setText(taskModelList.get(position).getDate());
                        EditText timeSlotDropEdt = dialog1.findViewById(R.id.task_time_slot);
                        timeSlotDropEdt.setText(taskModelList.get(position).getTime());
                        Button add = dialog1.findViewById(R.id.task_add);
                        Button cancel = dialog1.findViewById(R.id.task_cancel);

                        if (userType.equals("developer")){
                            developerSpinner.setVisibility(View.GONE);
                            spinName.setVisibility(View.GONE);
                        }else{
                            developerSpinner.setVisibility(View.GONE);
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
                                picker = new DatePickerDialog(context,
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
                                    PopupMenu popup = new PopupMenu(context, timeSlotDropEdt);
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
                                        Toast.makeText(context, "Select Type", Toast.LENGTH_SHORT).show();
                                    } else if (bookingDate.isEmpty()) {
                                        bookingCalender.setError("Select Date");
                                        bookingCalender.requestFocus();
                                    } else if (bookingTimeSlot.isEmpty()) {
                                        timeSlotDropEdt.setError("Select Time Slot");
                                        timeSlotDropEdt.requestFocus();
                                    } else{
                                        StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.UPDATE_TASK, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {

                                                dialog1.dismiss();
                                                dialog.dismiss();
                                                ((InterFaceClass)context).getDeveloperTask();


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
                                                params.put("task_id", taskModelList.get(position).getTask_id());
                                                params.put("developer_id", taskModelList.get(position).getDeveloper_id());
                                                params.put("user_name", name);
                                                params.put("type", type);
                                                params.put("date", bookingDate);
                                                params.put("time", bookingTimeSlot);

                                                return params;
                                            }
                                        };
                                        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
                                    }
//                                        addTask(userId, name, type, bookingDate, bookingTimeSlot);
                                }else if (userType.equals("admin")){
                                    String name = personName.getText().toString();
                                    String bookingDate = String.valueOf(bookingCalender.getText());
                                    String bookingTimeSlot = String.valueOf(timeSlotDropEdt.getText());
                                    if (name.isEmpty()) {
                                        personName.setError("Enter Person Name");
                                        personName.requestFocus();
                                    } else if (type == null) {
                                        Toast.makeText(context, "Select Type", Toast.LENGTH_SHORT).show();
                                    } else if (bookingDate.isEmpty()) {
                                        bookingCalender.setError("Select Date");
                                        bookingCalender.requestFocus();
                                    } else if (bookingTimeSlot.isEmpty()) {
                                        timeSlotDropEdt.setError("Select Time Slot");
                                        timeSlotDropEdt.requestFocus();
                                    } else{
                                        StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.UPDATE_TASK, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {

                                                dialog1.dismiss();
                                                dialog.dismiss();
                                                ((InterFaceClass)context).getAdminTask();

                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                                Log.d(TAG, "onErrorResponse: "+error.getMessage());
                                            }
                                        }) {
                                            @Override
                                            protected Map<String, String> getParams() throws AuthFailureError {
                                                Map<String, String> params = new HashMap<>();
//                params.put("developer_id", String.valueOf(userId));
                                                params.put("task_id", taskModelList.get(position).getTask_id());
                                                params.put("developer_id", taskModelList.get(position).getDeveloper_id());
                                                params.put("user_name", name);
                                                params.put("type", type);
                                                params.put("date", bookingDate);
                                                params.put("time", bookingTimeSlot);

                                                return params;
                                            }
                                        };
                                        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
                                    }
//                                        addTask(userIdSpin, name, type, bookingDate, bookingTimeSlot);
                                }

                            }
                        });
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog1.dismiss();
                            }
                        });

                        dialog1.show();
                        dialog1.setCancelable(false);
                        Window window = dialog1.getWindow();
                        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    }
                });

                deleteTask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.DELETE_TASK, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d( "wow123", "onResponse: "+response);
                                taskModelList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, taskModelList.size());
                                dialog.dismiss();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                params.put("task_id", taskModelList.get(position).getTask_id());
                                return params;
                            }
                        };
                        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
                    }
                });
                dialog.show();
                dialog.setCancelable(true);
                Window window = dialog.getWindow();
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        });


    }

    @Override
    public int getItemCount() {
        return taskModelList.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView task,pending;
        Button done;
        ImageView checkMark;
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            task = itemView.findViewById(R.id.task_item);
            done = itemView.findViewById(R.id.task_done_btn);
            checkMark = itemView.findViewById(R.id.check_mark);
            pending = itemView.findViewById(R.id.pending_text);
        }
    }


}

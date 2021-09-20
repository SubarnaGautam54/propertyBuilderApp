package com.example.propertybuilder.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.propertybuilder.ConstantApis.Api;
import com.example.propertybuilder.ConstantApis.MySingleton;
import com.example.propertybuilder.Models.UserDataModel;
import com.example.propertybuilder.Models.UserModel;
import com.example.propertybuilder.PostDetailUserActivity;
import com.example.propertybuilder.R;
import com.example.propertybuilder.SharedPreference.DataProccessor;
import com.example.propertybuilder.SharedPreference.SharedPrefManager;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    private static final String TAG = "wow123";
    private Context context;
    private List<UserDataModel> userDataModelList;
    private List<UserDataModel> userDataModelListFull;
    String type;
    public static final String FIREBASE_NOTIFICATION_SERVER_KEY = "key=AAAAjEeogSU:APA91bEa1suwdOU23my3AcKQKobAethPxjdrg-G1HY-woF7jJoIGGeV-D0JGRuUagygOc1MdZKBajB8V9zQvQ4uaie7LEq3ZEZi5CZ1lwx8QjOFlGMhdBEX1DQlzGzY21MNL2TOqSC3y";
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String contentType = "application/json";



    public BookingAdapter(Context context, List<UserDataModel> userDataModelList) {
        this.context = context;
        this.userDataModelList = userDataModelList;
        userDataModelListFull = new ArrayList<>(userDataModelList);
    }

    @NonNull
    @NotNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_items, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BookingAdapter.BookingViewHolder holder, int position) {
        holder.postName.setText(userDataModelList.get(position).getDeveloperPostName());
        holder.developerPostDes.setText(userDataModelList.get(position).getDeveloperPostDetail());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostDetailUserActivity.class);
                intent.putExtra("developer_name",userDataModelList.get(position).getDeveloperPostName());
                intent.putExtra("developer_detail",userDataModelList.get(position).getDeveloperPostDetail());
                intent.putExtra("post_id",userDataModelList.get(position).getPostId());
                intent.putExtra("lat",userDataModelList.get(position).getLat());
                intent.putExtra("lng",userDataModelList.get(position).getLng());
//              Toast.makeText(context, "id"+userDataModelList.get(position).getPostId(), Toast.LENGTH_SHORT).show();

                context.startActivity(intent);
            }
        });
        holder.bookNowBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.booking_dialog);
                // if button is clicked, close the custom dialog
                EditText bookingDateEdt = dialog.findViewById(R.id.bookingDate_dv);
                ImageView bookingCalender = dialog.findViewById(R.id.bookingCalender_dv);
                EditText timeSlotDropEdt = dialog.findViewById(R.id.timeSlot_spin);
                Button confirm = dialog.findViewById(R.id.booking_confirm_btn);
                Button cancel = dialog.findViewById(R.id.booking_cancel_btn);
                CheckBox inspectionCb = dialog.findViewById(R.id.inspectionCheckBox);
                CheckBox enquiryCb = dialog.findViewById(R.id.enquiryCheckBox);

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
                                        bookingDateEdt.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                        bookingDateEdt.setError(null);
                                    }
                                }, year, month, day);
                        picker.show();
                    }
                });
                dialog.show();

                timeSlotDropEdt.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_UP){
                            PopupMenu popup = new PopupMenu(context, timeSlotDropEdt);
                            //Inflating the Popup using xml file
                            popup.getMenuInflater()
                                    .inflate(R.menu.time_slots, popup.getMenu());
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

                inspectionCb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (inspectionCb.isChecked()){
                            enquiryCb.setChecked(false);
                            type =inspectionCb.getText().toString();
                        }else {
                            type = null;
                        }

                    }
                });
                enquiryCb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (enquiryCb.isChecked()){
                            inspectionCb.setChecked(false);
                            type =enquiryCb.getText().toString();
                        }else {
                            type = null;
                        }
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserModel usersModel = SharedPrefManager.getInstance(context).getUser();
                        String userId = String.valueOf(usersModel.getId());
                        String bookingDate = bookingDateEdt.getText().toString();
                        String bookingTimeSlot = timeSlotDropEdt.getText().toString();

                        if(bookingDate.isEmpty()) {
                            bookingDateEdt.requestFocus();
                            bookingDateEdt.setError("Confirm Your Booking Date");
                        }
                        else if (bookingTimeSlot.isEmpty()){
                            timeSlotDropEdt.requestFocus();
                            timeSlotDropEdt.setError("Confirm Your Time Slot");
                        }else if (type == null){
                            Toast.makeText(context, "Select Booking Type", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.ADD_APPOINTMENT_NEW, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    dialog.dismiss();

                                    createnotification("booking","Customer Booking","Customer Has Added Booking For Your Post",userDataModelList.get(position).getDeveloperId());
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }){
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String,String> params = new HashMap<>();
//                params.put("developer_id", String.valueOf(userId));
                                    params.put("date",bookingDate);
                                    params.put("time",bookingTimeSlot);
                                    params.put("type",type);
                                    params.put("developer_id",userDataModelList.get(position).getDeveloperId());
                                    params.put("user_id",userId);
                                    params.put("post_id",userDataModelList.get(position).getPostId());
                                    params.put("status","pending");
                                    return params;
                                }
                            };
                            MySingleton.getInstance(context).addToRequestQueue(stringRequest);

                        }
                    }
                });
                dialog.setCancelable(false);
            }
        });
    }
    public void filterList(ArrayList<UserDataModel> filteredList){
        userDataModelList = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return userDataModelList.size();
    }


    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView postName;
        TextView developerPostDes;
        ExtendedFloatingActionButton bookNowBtn;
        public BookingViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            postName = itemView.findViewById(R.id.postName_booking);
            developerPostDes = itemView.findViewById(R.id.developerPostDetail_booking);
            bookNowBtn = itemView.findViewById(R.id.bookNowBtn);
        }
    }
    public void createnotification(String topic,String title, String message, String id) {
        Log.i(TAG, "createnotification: notification");
        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("topic", topic);
            notifcationBody.put("title", title);
            notifcationBody.put("message", message);
            notification.put("to", "/topics/" + id);
            notification.put("data", notifcationBody);
            sendNotification(notification);
        } catch (JSONException e) {
            Log.i(TAG, "onCreate: " + e.getMessage());
        }
    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", FIREBASE_NOTIFICATION_SERVER_KEY);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
}

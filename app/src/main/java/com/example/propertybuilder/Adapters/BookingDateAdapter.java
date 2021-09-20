package com.example.propertybuilder.Adapters;

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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
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
import com.example.propertybuilder.InspectionRequestActivity;
import com.example.propertybuilder.Models.BookingDateModel;
import com.example.propertybuilder.Models.UserModel;
import com.example.propertybuilder.R;
import com.example.propertybuilder.SharedPreference.SharedPrefManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BookingDateAdapter extends RecyclerView.Adapter<BookingDateAdapter.BookingDateViewHolder> {
    private Context context;
    private List<BookingDateModel> bookingDateModelList;
    String bookingDate, bookingTimeSlot;
    public static final String TAG = "wow123";
    public static final String FIREBASE_NOTIFICATION_SERVER_KEY = "key=AAAAjEeogSU:APA91bEa1suwdOU23my3AcKQKobAethPxjdrg-G1HY-woF7jJoIGGeV-D0JGRuUagygOc1MdZKBajB8V9zQvQ4uaie7LEq3ZEZi5CZ1lwx8QjOFlGMhdBEX1DQlzGzY21MNL2TOqSC3y";
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String contentType = "application/json";

    public BookingDateAdapter(Context context, List<BookingDateModel> bookingDateModelList) {
        this.context = context;
        this.bookingDateModelList = bookingDateModelList;
    }

    @NonNull
    @NotNull
    @Override
    public BookingDateViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_date_items, parent, false);
        return new BookingDateAdapter.BookingDateViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull @NotNull BookingDateAdapter.BookingDateViewHolder holder, int position) {
        if (bookingDateModelList.get(position).getAppointmentStatus().equals("approved")){
            holder.checkMark.setVisibility(View.VISIBLE);
            holder.customerNameBooking.append(bookingDateModelList.get(position).getCustomerName()+" "+"book for an"+
                    " "+bookingDateModelList.get(position).getBookingType());
        }else {
            holder.customerNameBooking.append(bookingDateModelList.get(position).getCustomerName()+" "+"book for an"+
                    " "+bookingDateModelList.get(position).getBookingType());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
                    bottomSheetDialog.setContentView(R.layout.bottom_sheet_booking_details);
                    ImageView closeBD = bottomSheetDialog.findViewById(R.id.close_booking_details);
                    TextView details = bottomSheetDialog.findViewById(R.id.booking_details_date);
                    Button btnReply = bottomSheetDialog.findViewById(R.id.btnReply);
                    Button btnApproved = bottomSheetDialog.findViewById(R.id.btnApproved);
                    assert details != null;
                    bookingDate = bookingDateModelList.get(position).getBookingDate();
                    bookingTimeSlot = bookingDateModelList.get(position).getBookingTime();
                    details.append(bookingDateModelList.get(position).getCustomerName()+" "+
                            "book for an"+" "+bookingDateModelList.get(position).getBookingType()+" "+
                            bookingDate+" "+
                            "at time slot"+" "+bookingTimeSlot);

                    assert closeBD != null;
                    closeBD.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bottomSheetDialog.cancel();
                        }
                    });
                    assert btnApproved != null;
                    btnApproved.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.UPDATE_APPOINTMENT_APPROVED, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    bottomSheetDialog.cancel();
                                    Log.d("wow123", "onResponse: approve: "+response);
                                    holder.checkMark.setVisibility(View.VISIBLE);
                                    createnotification("approve","Booking","Your Booking Has Been Confirmed",bookingDateModelList.get(position).getCustomerID());
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }){
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String,String> params = new HashMap<>();
                                    params.put("appointment_id",bookingDateModelList.get(position).getAppointmentId());
                                    params.put("status","approved");
                                    params.put("message","app");
                                    return params;
                                }
                            };
                            MySingleton.getInstance(context).addToRequestQueue(stringRequest);
                        }
                    });
                    btnReply.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UserModel usersModel = SharedPrefManager.getInstance(context).getUser();
                            String type = usersModel.getTypeUser();
                            if (type.equals("developer")){
                                final Dialog dialog = new Dialog(context);
                                dialog.setContentView(R.layout.booking_edit_dialog);
                                // if button is clicked, close the custom dialog
                                EditText bookingDateEdt = dialog.findViewById(R.id.bookingDate_dv_edit);
                                ImageView bookingCalender = dialog.findViewById(R.id.bookingCalender_dv_edit);
                                EditText timeSlotDropEdt = dialog.findViewById(R.id.timeSlot_spin_edit);
                                Button confirm = dialog.findViewById(R.id.booking_confirm_btn_edit);
                                Button cancel = dialog.findViewById(R.id.booking_cancel_btn_edit);
                                ProgressBar progressBar = dialog.findViewById(R.id.dialogProgress);
                                LinearLayout dialogButtonBooking = dialog.findViewById(R.id.dialogButtonBooking);


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
                                        bookingDate = bookingDateEdt.getText().toString();
                                        bookingTimeSlot = timeSlotDropEdt.getText().toString();

                                        if(bookingDate.isEmpty()) {
                                            bookingDateEdt.requestFocus();
                                            bookingDateEdt.setError("Confirm Your Booking Date");
                                        }
                                        else if (bookingTimeSlot.isEmpty()){
                                            timeSlotDropEdt.requestFocus();
                                            timeSlotDropEdt.setError("Confirm Your Time Slot");
                                        } else {
                                            dialogButtonBooking.setVisibility(View.GONE);
                                            progressBar.setVisibility(View.VISIBLE);
                                            StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.EDIT_APPOINTMENT_NEW, new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    Log.d("wow123", "onResponse: "+response);
                                                    dialogButtonBooking.setVisibility(View.VISIBLE);
                                                    progressBar.setVisibility(View.GONE);
                                                    dialog.dismiss();
                                                    details.setText("");
                                                    details.append(bookingDateModelList.get(position).getCustomerName()+" "+
                                                            "book for an"+" "+bookingDateModelList.get(position).getBookingType()+" "+
                                                            bookingDate+" "+ "at time slot"+" "+bookingTimeSlot);
                                                    createnotification("edited","Booking","Your Booking Date Has Been Changed",bookingDateModelList.get(position).getCustomerID());

                                                }
                                            }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    dialogButtonBooking.setVisibility(View.VISIBLE);
                                                    progressBar.setVisibility(View.GONE);
                                                    Log.d("wow123", "onErrorResponse: "+error.getMessage());
                                                }
                                            }){
                                                @Override
                                                protected Map<String, String> getParams() throws AuthFailureError {
                                                    Map<String,String> params = new HashMap<>();
                                                    params.put("appointment_id",bookingDateModelList.get(position).getAppointmentId());
                                                    params.put("date",bookingDate);
                                                    params.put("time",bookingTimeSlot);
                                                    params.put("developer_id",userId);
                                                    params.put("status","updated");
                                                    params.put("message","app");
                                                    params.put("user_id",bookingDateModelList.get(position).getCustomerID());
                                                    params.put("post_id",bookingDateModelList.get(position).getPostID());

                                                    return params;
                                                }
                                            };
                                            MySingleton.getInstance(context).addToRequestQueue(stringRequest);

                                        }
                                    }
                                });
                                dialog.setCancelable(false);
                            }else {
                                Toast.makeText(context, "wow", Toast.LENGTH_SHORT).show();
                                final Dialog dialog = new Dialog(context);
                                dialog.setContentView(R.layout.admin_reply_dialog);
                                EditText adminReplyEdt = dialog.findViewById(R.id.adminReplyEdt_dialog);
                                Button adminRelySend = dialog.findViewById(R.id.adminReply_send_Btn_dialog);
                                Button adminRelyCancel = dialog.findViewById(R.id.adminReply_cancel_Btn_dialog);

                                adminRelySend.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String adminReply = adminReplyEdt.getText().toString();
                                        if (adminReply.isEmpty()){
                                            adminReplyEdt.requestFocus();
                                            adminReplyEdt.setError("Rely Must Not Be Empty Message");
                                        }else {
                                            StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.ADMIN_SEND_REPLY, new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    dialog.dismiss();

                                                    createnotification("edited","Admin",adminReply,bookingDateModelList.get(position).getCustomerID());

                                                }
                                            }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {

                                                }
                                            }){
                                                @Override
                                                protected Map<String, String> getParams() throws AuthFailureError {
                                                    Map<String,String> params = new HashMap<>();
                                                    params.put("id",bookingDateModelList.get(position).getAppointmentId());
                                                    params.put("message",adminReply);
                                                    params.put("status","updated");

                                                    return params;
                                                }
                                            };
                                            MySingleton.getInstance(context).addToRequestQueue(stringRequest);
                                        }
                                    }
                                });

                                adminRelyCancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                            }

                        }
                    });
                    bottomSheetDialog.setCancelable(false);
                    bottomSheetDialog.show();
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return bookingDateModelList.size();
    }

    public class BookingDateViewHolder extends RecyclerView.ViewHolder {
        TextView customerNameBooking;
        ImageView checkMark;
        public BookingDateViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            checkMark = itemView.findViewById(R.id.checkMark);
            customerNameBooking = itemView.findViewById(R.id.customerBookingDate);
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

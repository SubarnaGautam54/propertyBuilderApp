package com.example.propertybuilder.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.propertybuilder.Models.AppointmentModel;
import com.example.propertybuilder.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {
    private Context context;
    private List<AppointmentModel> appointmentModelList;

    public AppointmentAdapter(Context context, List<AppointmentModel> appointmentModelList) {
        this.context = context;
        this.appointmentModelList = appointmentModelList;
    }

    @NonNull
    @NotNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_items, parent, false);
        return new AppointmentAdapter.AppointmentViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull AppointmentAdapter.AppointmentViewHolder holder, int position) {
        if (appointmentModelList.get(position).getAppointmentMessage().equals("app")){
            holder.admin.setVisibility(View.GONE);
            holder.developer.setVisibility(View.VISIBLE);

            if (appointmentModelList.get(position).getAppointmentStatus().equals("pending")) {
                holder.appointmentStatus.setText("Appointment Pending");
                holder.appointmentDetails.append("Your Appointment For"+" "+ appointmentModelList.get(position).getAppointmentType()+" "+
                        "at Date"+" "+ appointmentModelList.get(position).getAppointmentDate()+" "+" and Time"+" "+
                        appointmentModelList.get(position).getAppointmentTime()+"\n");
            }else if (appointmentModelList.get(position).getAppointmentStatus().equals("approved")) {
                holder.appointmentStatus.setText("Appointment Approved");
                holder.appointmentDetails.append("Your Appointment For"+" "+ appointmentModelList.get(position).getAppointmentType()+" "+
                        "at Date"+" "+ appointmentModelList.get(position).getAppointmentDate()+" "+" and Time"+" "+
                        appointmentModelList.get(position).getAppointmentTime()+"\n");
            }else if (appointmentModelList.get(position).getAppointmentStatus().equals("updated")){
                holder.appointmentStatus.setText("Appointment Updated");
                holder.appointmentDetails.append("Your Appointment For"+" "+ appointmentModelList.get(position).getAppointmentType()+" "+
                        "at Date"+" "+ appointmentModelList.get(position).getAppointmentDate()+" "+" and Time"+" "+
                        appointmentModelList.get(position).getAppointmentTime()+"\n");

            }
        }else {
            holder.admin.setVisibility(View.VISIBLE);
            holder.developer.setVisibility(View.GONE);
            holder.adminRelyTxt.setText(appointmentModelList.get(position).getAppointmentMessage());
        }

    }

    @Override
    public int getItemCount() {
        return appointmentModelList.size();
    }

    public class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView appointmentDetails;
        TextView appointmentStatus;
        TextView adminRelyTxt;
        LinearLayout developer,admin;
        public AppointmentViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            appointmentDetails = itemView.findViewById(R.id.appointmentDetails);
//            textAppointDate = itemView.findViewById(R.id.textAppointDate);
//            textAppointTime = itemView.findViewById(R.id.textAppointTime);
            appointmentStatus = itemView.findViewById(R.id.appointmentStatus);
            adminRelyTxt = itemView.findViewById(R.id.adminRelyTxt);
            developer = itemView.findViewById(R.id.developer);
            admin = itemView.findViewById(R.id.admin);
        }
    }
}

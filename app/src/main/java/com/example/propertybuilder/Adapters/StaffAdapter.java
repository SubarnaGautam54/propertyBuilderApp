package com.example.propertybuilder.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.propertybuilder.ConstantApis.Api;
import com.example.propertybuilder.ConstantApis.MySingleton;
import com.example.propertybuilder.Models.StaffModel;
import com.example.propertybuilder.ProfileActivity;
import com.example.propertybuilder.R;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.StaffViewHolder> {
    private final Context context;
    private final List<StaffModel> staffModelList;

    public StaffAdapter(Context context, List<StaffModel> staffModelList) {
        this.context = context;
        this.staffModelList = staffModelList;
    }

    @NonNull
    @NotNull
    @Override
    public StaffViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.staff_items, parent, false);
        return new StaffViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull @NotNull StaffAdapter.StaffViewHolder holder, int position) {
        Glide.with(context)
                .load(Api.IMAGE_BASE_URL+staffModelList.get(position).getStaffImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.staffImage);
        holder.staffName.setText(staffModelList.get(position).getStaffName());
        holder.deleteStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.DELETE_STAFF, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("wow123", "onResponse: "+response);
                        staffModelList.remove(position);
                        notifyDataSetChanged();
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
                        params.put("id", staffModelList.get(position).getStaffId());
                        return params;
                    }
                };
                MySingleton.getInstance(context).addToRequestQueue(stringRequest);

            }
        });
        holder.editStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,StaffDetailActivity.class);
                intent.putExtra("staffId",staffModelList.get(position).getStaffId());
                intent.putExtra("staffImage",staffModelList.get(position).getStaffImage());
                intent.putExtra("staffName",staffModelList.get(position).getStaffName());
                intent.putExtra("staffAddress",staffModelList.get(position).getStaffAddress());
                intent.putExtra("staffPhone",staffModelList.get(position).getStaffPhone());
                intent.putExtra("staffEmail",staffModelList.get(position).getStaffEmail());
                intent.putExtra("staffPassword",staffModelList.get(position).getStaffPassword());
                intent.putExtra("staffType",staffModelList.get(position).getStaffType());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return staffModelList.size();
    }

    public static class StaffViewHolder extends RecyclerView.ViewHolder {
        CircleImageView staffImage;
        TextView staffName;
        Button deleteStaff, editStaff;
        public StaffViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            staffImage = itemView.findViewById(R.id.staffImageR);
            staffName = itemView.findViewById(R.id.staffNameR);
            deleteStaff = itemView.findViewById(R.id.deleteStaffR);
            editStaff = itemView.findViewById(R.id.editStaffR);
        }
    }
}

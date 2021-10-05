package com.example.propertybuilder.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
import com.example.propertybuilder.Models.EditImagesModel;
import com.example.propertybuilder.Models.UserModel;
import com.example.propertybuilder.R;
import com.example.propertybuilder.SharedPreference.DataProccessor;
import com.example.propertybuilder.SharedPreference.SharedPrefManager;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditImageAdapter extends RecyclerView.Adapter<EditImageAdapter.EIViewHolder> {
    private final Context context;
    private final List<EditImagesModel> editImagesModelList;

    public EditImageAdapter(Context context, List<EditImagesModel> editImagesModelList) {
        this.context = context;
        this.editImagesModelList = editImagesModelList;
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public EIViewHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_image_items, parent, false);
        return new EditImageAdapter.EIViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull EditImageAdapter.EIViewHolder holder, @SuppressLint("RecyclerView") int position) {
        UserModel usersModel = SharedPrefManager.getInstance(context).getUser();
        String type = usersModel.getTypeUser();
        Glide.with(context)
                .load(Api.IMAGE_BASE_URL + editImagesModelList.get(position).getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.edit_image);
        if (type.equals("customer")){
            holder.remove_image.setVisibility(View.GONE);
        }
        holder.remove_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.DELETE_IMAGE, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        editImagesModelList.remove(position);
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
                        params.put("image_id", editImagesModelList.get(position).getId());
                        return params;
                    }
                };
                MySingleton.getInstance(context).addToRequestQueue(stringRequest);
            }
        });
    }

    @Override
    public int getItemCount() {
        return editImagesModelList.size();
    }

    public class EIViewHolder extends RecyclerView.ViewHolder {
        ImageView edit_image;
        ExtendedFloatingActionButton remove_image;

        public EIViewHolder(@NonNull @org.jetbrains.annotations.NotNull View itemView) {
            super(itemView);
            edit_image = itemView.findViewById(R.id.edit_image);
            remove_image = itemView.findViewById(R.id.remove_image);
        }
    }
}

package com.example.propertybuilder.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.propertybuilder.ConstantApis.Api;
import com.example.propertybuilder.ConstantApis.MySingleton;
import com.example.propertybuilder.Models.PhaseModel;
import com.example.propertybuilder.Models.UserModel;
import com.example.propertybuilder.PhaseDetailActivity;
import com.example.propertybuilder.PostDetailUserActivity;
import com.example.propertybuilder.R;
import com.example.propertybuilder.SharedPreference.SharedPrefManager;
import com.google.android.datatransport.runtime.dagger.MapKey;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhaseAdapter extends RecyclerView.Adapter<PhaseAdapter.PhaseViewHolder> {

    private static final String TAG = "wow123";
    private final Context context;
    private final List<PhaseModel> phaseModelList;
    private int dotscount;
    private ImageView[] dots;
    ViewPagerAdapter mViewPagerAdapter;
    ArrayList<String> images = new ArrayList<>();


    public PhaseAdapter(Context context, List<PhaseModel> phaseModelList) {
        this.context = context;
        this.phaseModelList = phaseModelList;
    }

    @NonNull
    @NotNull
    @Override
    public PhaseViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.phase_items, parent, false);
        return new PhaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PhaseAdapter.PhaseViewHolder holder, @SuppressLint("RecyclerView") int position) {
        UserModel usersModel = SharedPrefManager.getInstance(context).getUser();
        String type = usersModel.getTypeUser();
//        holder.phaseCounts.setText(String.valueOf(position+1));
        holder.phaseCounts.setText(phaseModelList.get(position).getPhaseName());
        holder.phaseDecs.setText(phaseModelList.get(position).getPhaseDec());
        holder.phaseDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, PhaseDetailActivity.class);
                intent.putExtra("phaseID",phaseModelList.get(position).getPhaseId());
                intent.putExtra("postID",phaseModelList.get(position).getPostId());
                intent.putExtra("postDescription",phaseModelList.get(position).getPhaseDec());
                intent.putExtra("phaseVideo",phaseModelList.get(position).getPhaseVideo());
                context.startActivity(intent);
            }
        });
        if (type.equals("customer")){
            holder.removePhase.setVisibility(View.GONE);
        }
        holder.removePhase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.REMOVE_PHASE, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        phaseModelList.remove(position);
                        notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
                    @Nullable
                    @org.jetbrains.annotations.Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<>();
                        params.put("id",phaseModelList.get(position).getPhaseId());
                        return params;
                    }
                };
                MySingleton.getInstance(context).addToRequestQueue(stringRequest);
            }
        });





    }

    @Override
    public int getItemCount() {
        return phaseModelList.size();
    }

    public static class PhaseViewHolder extends RecyclerView.ViewHolder {
        TextView phaseDecs;
        TextView phaseCounts;
        Button phaseDetailsBtn;
        ImageView removePhase;
        public PhaseViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            phaseDetailsBtn = itemView.findViewById(R.id.phaseDetailsBtn);
            phaseDecs = itemView.findViewById(R.id.phaseDescription_phase);
            removePhase = itemView.findViewById(R.id.removePhase);
            phaseCounts = itemView.findViewById(R.id.phaseCounts);
        }

    }


}

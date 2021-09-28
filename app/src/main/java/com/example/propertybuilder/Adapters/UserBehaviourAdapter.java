package com.example.propertybuilder.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.propertybuilder.Models.UserBehaviourModel;
import com.example.propertybuilder.R;

import java.util.List;

public class UserBehaviourAdapter extends RecyclerView.Adapter<UserBehaviourAdapter.ViewHolder> {
    private Context context;
    private List<UserBehaviourModel> userBehaviourModelList;

    public UserBehaviourAdapter(Context context, List<UserBehaviourModel> userBehaviourModelList) {
        this.context = context;
        this.userBehaviourModelList = userBehaviourModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_behaviour_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.message.setText(userBehaviourModelList.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return userBehaviourModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView message;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message_user_behaviour);
        }
    }
}

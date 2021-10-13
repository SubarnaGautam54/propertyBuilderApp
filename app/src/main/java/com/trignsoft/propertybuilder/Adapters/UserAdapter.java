package com.trignsoft.propertybuilder.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trignsoft.propertybuilder.Models.UserDataModel;
import com.trignsoft.propertybuilder.PostDetailUserActivity;
import com.example.propertybuilder.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private Context context;
    private List<UserDataModel> userDataModelList;

    public UserAdapter(Context context, List<UserDataModel> userDataModelList) {
        this.context = context;
        this.userDataModelList = userDataModelList;
    }

    @NonNull
    @Override
    public UserAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_items, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
//        holder.developerProfileImage.setImageDrawable(context.getResources().getDrawable(userDataModelList.get(position).getDeveloperProfileImage()));
        holder.postName.setText(userDataModelList.get(position).getDeveloperPostName());
        holder.developerPostDes.setText(userDataModelList.get(position).getDeveloperPostDetail());

      holder.itemView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent = new Intent(context,PostDetailUserActivity.class);
              intent.putExtra("developer_name",userDataModelList.get(position).getDeveloperPostName());
              intent.putExtra("developer_detail",userDataModelList.get(position).getDeveloperPostDetail());
              intent.putExtra("post_id",userDataModelList.get(position).getPostId());
              intent.putExtra("lat",userDataModelList.get(position).getLat());
              intent.putExtra("lng",userDataModelList.get(position).getLng());
//              Toast.makeText(context, "id"+userDataModelList.get(position).getPostId(), Toast.LENGTH_SHORT).show();

              context.startActivity(intent);
          }
      });
    }

    @Override
    public int getItemCount() {
            return userDataModelList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        CircleImageView developerProfileImage;
        TextView developerName;
        TextView postName;
        TextView developerPostDes;
        TextView showDetails;
        Button showDetails_btn;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            developerProfileImage = itemView.findViewById(R.id.developerProfileImage);
            developerName = itemView.findViewById(R.id.developerName);
            postName = itemView.findViewById(R.id.postName);
            developerPostDes = itemView.findViewById(R.id.developerPostDetail);
            showDetails = itemView.findViewById(R.id.showDetails);
            showDetails_btn = itemView.findViewById(R.id.showDetails_btn);
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}

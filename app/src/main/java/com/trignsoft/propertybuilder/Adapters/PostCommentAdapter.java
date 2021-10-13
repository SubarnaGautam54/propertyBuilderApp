package com.trignsoft.propertybuilder.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.trignsoft.propertybuilder.ConstantApis.Api;
import com.trignsoft.propertybuilder.ConstantApis.MySingleton;
import com.trignsoft.propertybuilder.Models.PostCommentModel;
import com.trignsoft.propertybuilder.Models.UserModel;
import com.example.propertybuilder.R;
import com.trignsoft.propertybuilder.SharedPreference.SharedPrefManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostCommentAdapter extends RecyclerView.Adapter<PostCommentAdapter.CommentViewHolder> {
    private Context context;
    private List<PostCommentModel> postCommentModelList;

    public PostCommentAdapter(Context context, List<PostCommentModel> postCommentModelList) {
        this.context = context;
        this.postCommentModelList = postCommentModelList;
    }

    @NonNull
    @Override
    public PostCommentAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_items, parent, false);
        return new PostCommentAdapter.CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostCommentAdapter.CommentViewHolder holder, int position) {
        UserModel usersModel = SharedPrefManager.getInstance(context).getUser();
        String userId = String.valueOf(usersModel.getId());
//        Toast.makeText(this, ""+userId, Toast.LENGTH_SHORT).show();
        holder.userName.setText(postCommentModelList.get(position).getUserName());
        holder.userComment.setText(postCommentModelList.get(position).getUserComment());
        if (postCommentModelList.get(position).getUserId().equals(userId)){
            holder.commentOptions.setVisibility(View.VISIBLE);
        }
        holder.commentOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, holder.commentOptions);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.comment_option_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getTitle().equals("Edit Comment")){
                            final Dialog dialog = new Dialog(context);
                            dialog.setContentView(R.layout.edit_comment_dialog);

                            EditText commentEdt = dialog.findViewById(R.id.editComment_dialog);
                            commentEdt.setText(postCommentModelList.get(position).getUserComment());
                            Button updateComment = dialog.findViewById(R.id.updateComment_dialog);
                            Button cancelComment = dialog.findViewById(R.id.cancelComment_dialog);

                            updateComment.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String comment = commentEdt.getText().toString();
                                    if (comment.isEmpty()){
                                        commentEdt.requestFocus();
                                        commentEdt.setError("Comment Can not Be Empty!");
                                    }else {
                                        StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.UPDATE_COMMENT, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                Log.d("wow123", "onResponse: editComment"+response);
                                                holder.userComment.setText(comment);

                                                dialog.dismiss();
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
                                                params.put("id", postCommentModelList.get(position).getId());
                                                params.put("message", comment);
                                                return params;
                                            }
                                        };
                                        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
                                    }
                                }
                            });
                            cancelComment.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                   dialog.dismiss();
                                }
                            });


                            dialog.show();


                        }else if (item.getTitle().equals("Delete Comment")){
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.DELETE_COMMENT, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    postCommentModelList.remove(position);
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
                                    params.put("id", postCommentModelList.get(position).getId());
                                    return params;
                                }
//                                @Override
//                                protected Map<String, String> getParams() throws AuthFailureError {
//                                    Map<String,String> params = new HashMap<>();
//                                    params.put("id", String.valueOf(postCommentModelList.get(position).getId()));
//                                    return params;
//                                }
                            };
                            MySingleton.getInstance(context).addToRequestQueue(stringRequest);
                        }
                        return true;
                    }
                });

                popup.show(); //showing popup men
            }
        });
    }

    @Override
    public int getItemCount() {
        return postCommentModelList.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView userComment;
        ImageView commentOptions;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userName);
            userComment = itemView.findViewById(R.id.userComment);
            commentOptions = itemView.findViewById(R.id.commentOptions);
        }
    }
}

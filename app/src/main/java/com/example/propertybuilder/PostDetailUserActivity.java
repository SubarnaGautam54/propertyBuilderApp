package com.example.propertybuilder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.propertybuilder.Adapters.EditImageAdapter;
import com.example.propertybuilder.Adapters.PhaseAdapter;
import com.example.propertybuilder.Adapters.PostCommentAdapter;
import com.example.propertybuilder.Adapters.ViewPagerAdapter;
import com.example.propertybuilder.ConstantApis.Api;
import com.example.propertybuilder.ConstantApis.MySingleton;
import com.example.propertybuilder.Models.EditImagesModel;
import com.example.propertybuilder.Models.PhaseModel;
import com.example.propertybuilder.Models.PostCommentModel;
import com.example.propertybuilder.Models.UserModel;
import com.example.propertybuilder.SharedPreference.DataProccessor;
import com.example.propertybuilder.SharedPreference.SharedPrefManager;
import com.example.propertybuilder.databinding.ActivityPostDetailUserBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.gson.Gson;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PostDetailUserActivity extends AppCompatActivity implements OnMapReadyCallback {

    ActivityPostDetailUserBinding binding;
    public static final String TAG = "wow123";

    String selectedPointLat,selectedPointLog ;
    SupportMapFragment supportMapFragment;
    GoogleMap mMap;
    FusedLocationProviderClient client;
    ArrayList<String> images = new ArrayList<>();
    List<PhaseModel> phaseModelList = new ArrayList<>();
    PhaseAdapter phaseAdapter;
    private LinearLayoutManager manager;
    PostCommentAdapter postCommentAdapter;
    private List<PostCommentModel> postCommentModelList;
    private List<EditImagesModel> editImagesModelList = new ArrayList<>();
    EditImageAdapter editImageAdapter;
    ViewPagerAdapter mViewPagerAdapter;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    private int dotscount;
    private ImageView[] dots;
    AlertDialog alertDialog;
    Intent intent;
    String post_id;
    String userId;
    String userName;
    String type;
    double lat,lng;
    int PICK_IMAGE_MULTIPLE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostDetailUserBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        DataProccessor dataProccessor = new DataProccessor(this);
        intent = getIntent();
        binding.reportName.setText(intent.getStringExtra("developer_name"));
        binding.decTxt.setText(intent.getStringExtra("developer_detail"));
        post_id = getIntent().getExtras().getString("post_id");
        Log.d(TAG, "onCreate: postId:1 "+post_id);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            post_id = extras.getString("post_id");
        }
        UserModel usersModel = SharedPrefManager.getInstance(this).getUser();
        userName = usersModel.getFullName();
        userId = String.valueOf(usersModel.getId());
        type = usersModel.getTypeUser();
        if (type.equals("customer")) {
            binding.editPropertyName.setVisibility(View.GONE);
            binding.editPropertyDec.setVisibility(View.GONE);
            binding.addPhase.setVisibility(View.GONE);
        }
//        else if (type.equals("admin")){
//            binding.editPropertyName.setVisibility(View.GONE);
//            binding.editPropertyDec.setVisibility(View.GONE);
//            binding.addPhase.setVisibility(View.GONE);
//            binding.addCommentLy.setVisibility(View.GONE);
//        }
        manager = new LinearLayoutManager(this);
         lat = Double.parseDouble(intent.getStringExtra("lat"));
         lng = Double.parseDouble(intent.getStringExtra("lng"));
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map_post_details);
        supportMapFragment.getMapAsync(this);
        if (ActivityCompat.checkSelfPermission(PostDetailUserActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(PostDetailUserActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        getPhases();
//        getImages();
//        imagesApiCall();
        getAllComments();
        clicks();


    }

    private void getCurrentLocation() {

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
                LatLng latLng = new LatLng(lat
                        ,lng);
                MarkerOptions options = new MarkerOptions().position(latLng).title("I am here")
                        .icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_home_property));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                googleMap.addMarker(options);

                Geocoder geocoder = new Geocoder(PostDetailUserActivity.this, Locale.getDefault());

                List<Address> addresses  = null;
                try {
                    addresses = geocoder.getFromLocation(lat,lng, 1);
                    String address = addresses.get(0).getAddressLine(0);
//        String city = addresses.get(0).getLocality();
//        String state = addresses.get(0).getAdmainArea();
//        String zip = addresses.get(0).getPostalCode();
//        String country = addresses.get(0).getCountryName();
                    Log.d(TAG, "onMapClick: address"+address);
                    if (!address.isEmpty()){
                        binding.completeAddressCard.setVisibility(View.VISIBLE);
                        binding.completeAddressText.setText(address);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(PostDetailUserActivity.this, "Please Make Sure For Internet Connection!", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


    private void getPhases() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.GET_PHASE_NEW, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                phaseModelList.clear();

                Log.d(TAG, "onResponse: phaseApi"+response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    for (int i = 0; i<jsonObject.length();i++){
                        JSONObject jsonObject1 = jsonObject.getJSONObject(i+"");

                        PhaseModel phaseModel = new PhaseModel();
                        phaseModel.setPhaseId(jsonObject1.getString("id"));
                        phaseModel.setPhaseDec(jsonObject1.getString("description"));
                        phaseModel.setPostId(jsonObject1.getString("post_id"));
                        phaseModel.setPhaseVideo(jsonObject1.getString("phase_video"));

                        phaseModelList.add(phaseModel);
                        setupRecyclerView();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d(TAG, "onErrorResponse: "+error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("post_id", post_id);
                return params;
            }
        };
        MySingleton.getInstance(PostDetailUserActivity.this).addToRequestQueue(stringRequest);

    }

    private void setupRecyclerView() {
        binding.recyclerViewPhases.setLayoutManager(new LinearLayoutManager(this));
        phaseAdapter = new PhaseAdapter(this,phaseModelList);
        binding.recyclerViewPhases.setAdapter(phaseAdapter);
    }

    private void getAllComments() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.GET_COMMENTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("wow123", "onResponse message: " + response);

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    postCommentModelList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        PostCommentModel postCommentModel = new PostCommentModel();


                        postCommentModel.setId(jsonObject.getString("id"));
                        postCommentModel.setUserName(jsonObject.getString("name"));
                        postCommentModel.setUserComment(jsonObject.getString("message"));
                        postCommentModel.setUserId(jsonObject.getString("user_id"));


                        postCommentModelList.add(postCommentModel);
                        setUpRecyclerViewComment();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d(TAG, "onErrorResponse: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("id_post", post_id);
                return params;
            }
        };
        MySingleton.getInstance(PostDetailUserActivity.this).addToRequestQueue(stringRequest);
    }

    private void setUpRecyclerViewComment() {
        binding.recyclerView.setLayoutManager(manager);
        postCommentAdapter = new PostCommentAdapter(this, postCommentModelList);
        binding.recyclerView.setAdapter(postCommentAdapter);
        postCommentAdapter.notifyDataSetChanged();
    }

    private void clicks() {
        binding.addPhase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(PostDetailUserActivity.this, AddPhaseActivity.class));
//                        setupPhaseRecyclerView();
                Intent intent = new Intent(PostDetailUserActivity.this,AddPhaseActivity.class);
                intent.putExtra("addId",post_id);
                startActivity(intent);
            }
        });
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.commentSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postCommentApiCall();
            }
        });
        binding.editPropertyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PostDetailUserActivity.this);
                final View dialogView = LayoutInflater.from(PostDetailUserActivity.this).inflate(R.layout.alert_edit_name, null);

                progressBar = dialogView.findViewById(R.id.progressBar_DV);

                recyclerView = dialogView.findViewById(R.id.recyclerView_DV);

                EditText changeName = dialogView.findViewById(R.id.name_edt_Ad);
                changeName.setText(intent.getStringExtra("developer_name"));
                EditText changeDec = dialogView.findViewById(R.id.dec_edt_Ad);
                changeDec.setText(intent.getStringExtra("developer_detail"));

                Button updateName = dialogView.findViewById(R.id.updateNameBtn);
                updateName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String updatedName = changeName.getText().toString();
                        String updatedDec = changeDec.getText().toString();
                        if (updatedName.isEmpty()) {
                            changeName.requestFocus();
                            changeName.setError("Post Name Can't Be Empty");
                        } else if (updatedDec.isEmpty()) {
                            changeDec.requestFocus();
                            changeDec.setError("Post Description Can't Be Empty");
                        } else {
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.EDIT_POST, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    binding.reportName.setText(updatedName);
                                    binding.decTxt.setText(updatedDec);
                                    alertDialog.dismiss();
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<>();

                                    params.put("post_name", updatedName);
                                    params.put("description", updatedDec);
                                    params.put("post_id", post_id);
                                    return params;
                                }
                            };
                            MySingleton.getInstance(PostDetailUserActivity.this).addToRequestQueue(stringRequest);
                        }

                    }
                });

                Button closeDV = dialogView.findViewById(R.id.updateNameBtn_close);
                closeDV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        imagesApiCall();
                        alertDialog.dismiss();
                    }
                });
                Button deletePost = dialogView.findViewById(R.id.deletePostBtn);
                deletePost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.DELETE_POST, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                alertDialog.dismiss();
                                finish();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                params.put("post_id", post_id);
                                return params;
                            }
                        };
                        MySingleton.getInstance(PostDetailUserActivity.this).addToRequestQueue(stringRequest);
                    }
                });

                alertDialogBuilder.setView(dialogView);
                alertDialogBuilder.setCancelable(false);
                alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }




    private void postCommentApiCall() {

        String comment = binding.commentEdt.getText().toString();

        if (!comment.isEmpty()) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.SEND_COMMENTS, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("wow123", "onResponse coomments: " + response);
                    binding.commentEdt.setText("");
//                    postCommentModelList.clear();

                    getAllComments();


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.d(TAG, "onErrorResponse: " + error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();

                    params.put("name", userName);
                    params.put("message", comment);
                    params.put("id_post", post_id);
                    params.put("user_id", userId);
                    return params;
                }
            };
            MySingleton.getInstance(PostDetailUserActivity.this).addToRequestQueue(stringRequest);
        } else {
            binding.commentEdt.requestFocus();
            binding.commentEdt.setError("Write Comment!");
        }

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // When an Image is picked
        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {
            // Get the Image from data
            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                int cout = data.getClipData().getItemCount();
                for (int i = 0; i < cout; i++) {
                    // adding imageuri in array
                    Uri imageurl = data.getClipData().getItemAt(i).getUri();
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageurl);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 10  , byteArrayOutputStream);
                    final String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                    List<String> images = new ArrayList<>();
                    images.add(encodedImage);
                    Log.d(TAG, "onActivityResult: "+images);

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.ADD_IMAGE, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("wow123", "onResponse: addnewimage"+response);

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("wow123", "onErrorResponse: erroe"+error.getMessage());
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {

                            Gson gson = new Gson();
                            String data = gson.toJson(images);
                            Log.d(TAG, "getParams: "+data);

                            Map<String,String> params = new HashMap<>();
                            params.put("post_id", post_id);
                            params.put("images", data);
                            return params;
                        }
                    };
                    MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
                }
            } else {
                // show this if no image is selected
//            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setScrollGesturesEnabled(false);
    }
    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPhases();

    }
}
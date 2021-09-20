package com.example.propertybuilder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.propertybuilder.Adapters.EditImageAdapter;
import com.example.propertybuilder.Adapters.ViewPagerAdapter;
import com.example.propertybuilder.ConstantApis.Api;
import com.example.propertybuilder.ConstantApis.MySingleton;
import com.example.propertybuilder.Models.EditImagesModel;
import com.example.propertybuilder.Models.UserModel;
import com.example.propertybuilder.SharedPreference.SharedPrefManager;
import com.example.propertybuilder.databinding.ActivityPhaseDetailBinding;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhaseDetailActivity extends AppCompatActivity {

    List<EditImagesModel> editImagesModelList = new ArrayList<>();
    EditImageAdapter editImageAdapter;
    ActivityPhaseDetailBinding binding;
    ArrayList<String> arrayList_images = new ArrayList<>();

    int PICK_IMAGE_MULTIPLE = 1;
    MediaController mediaController;


    ArrayList<Uri> mArrayUri;
    int position = 0;

    private int dotscount;
    private ImageView[] dots;
    public static final String TAG = "wow123";
    ArrayList<String> images = new ArrayList<>();
    ViewPagerAdapter mViewPagerAdapter;
    Intent intent;
    String phaseId,postId,postDec,phaseVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhaseDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        UserModel usersModel = SharedPrefManager.getInstance(this).getUser();
        String type = usersModel.getTypeUser();
        intent = getIntent();
        toolBarSetup();
        mArrayUri = new ArrayList<Uri>();


        phaseVideo = intent.getStringExtra("phaseVideo");

        setupVideo();

        phaseId = intent.getStringExtra("phaseID");
        postId = intent.getStringExtra("postID");
        postDec = intent.getStringExtra("postDescription");
        binding.editPhaseDescriptionEdt.setText(postDec);
        binding.saveChangesBtnDec.setVisibility(View.GONE);
        binding.editPhaseDescriptionEdt.setEnabled(false);
        if (type.equals("customer")){
            binding.editPhaseDescriptionLy.setVisibility(View.GONE);
            binding.customerDisplayDisc.setVisibility(View.VISIBLE);
            binding.customerDisplayDisc.setText(postDec);
            binding.addImageEdit.setVisibility(View.GONE);
        }
        Log.d(TAG, "onCreate: "+postId);
        imagesApiCall();
        clicks();
    }

    private void setupVideo() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.videoView.setMediaController(mediaController);
        String v = phaseVideo.substring(1);
        Uri uri = Uri.parse(Api.GET_VIDEOS+v);
        binding.videoView.setVideoURI(uri);
        binding.videoView.requestFocus();
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(binding.videoView);
        binding.videoView.setMediaController(mediaController);
        binding.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                binding.progressBar.setVisibility(View.GONE);
                Log.i("wow123", "Duration = " +
                        binding.videoView.getDuration());
                Log.d(TAG, "onCreate: video"+v);
            }
        });
        binding.playVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.playVideo.setVisibility(View.GONE);
                binding.videoView.start();
            }
        });
//        binding.videoView.start();


    }

    private void clicks() {
        binding.editPhaseDescriptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.saveChangesBtnDec.setVisibility(View.VISIBLE);
                binding.editPhaseDescriptionEdt.setEnabled(true);

            }
        });
        binding.saveChangesBtnDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phaseDec = binding.editPhaseDescriptionEdt.getText().toString();
                if (phaseDec.isEmpty()){
                    binding.editPhaseDescriptionEdt.requestFocus();
                    binding.editPhaseDescriptionEdt.setError("Phase Description Should Not Be Empty");
                }else {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.EDIT_PHASE_DEC, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            binding.saveChangesBtnDec.setVisibility(View.GONE);
                            binding.editPhaseDescriptionEdt.setEnabled(false);

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();

                            params.put("description", phaseDec);
                            params.put("post_id", postId);
                            return params;
                        }
                    };
                    MySingleton.getInstance(PhaseDetailActivity.this).addToRequestQueue(stringRequest);
                }
            }
        });
        binding.addImageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                // setting type to select to be image
                intent.setType("image/*");

                // allowing multiple image to be selected
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
            }
        });
    }

    private void imagesApiCall() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.GET_IMAGES_NEW, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                editImagesModelList.clear();

                Log.d("wow123", "onResponse images: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    for (int i = 0; i < (jsonObject.length()); i++) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject(i + "");
                        Log.d("wow123", "onResponse: " + jsonObject1.getString("image"));
                        EditImagesModel editImagesModel = new EditImagesModel();
//                        images.add(jsonObject1.getString("image"));
                        editImagesModel.setId(jsonObject1.getString("id"));
                        editImagesModel.setImage(jsonObject1.getString("image"));

                        editImagesModelList.add(editImagesModel);

                        binding.recyclerView.setLayoutManager(new LinearLayoutManager(PhaseDetailActivity.this));
                        editImageAdapter = new EditImageAdapter(PhaseDetailActivity.this,editImagesModelList);
                        binding.recyclerView.setAdapter(editImageAdapter);
                    }
//                    setupPager();

                } catch (Exception e) {
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

                params.put("phase_id", phaseId);
                return params;
            }
        };
        MySingleton.getInstance(PhaseDetailActivity.this).addToRequestQueue(stringRequest);

    }

//    private void setupPager() {
//
//
//        mViewPagerAdapter = new ViewPagerAdapter(PhaseDetailActivity.this, images);
//
//        binding.viewPager.setAdapter(mViewPagerAdapter);
//        dotscount = mViewPagerAdapter.getCount();
//
//        dots = new ImageView[dotscount];
//
//        for (int i = 0; i < dotscount; i++) {
//
//            dots[i] = new ImageView(this);
//            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_selected_dots));
//
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//
//            params.setMargins(8, 0, 8, 0);
//
//            binding.SliderDots.addView(dots[i], params);
//
//            binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//                @Override
//                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                    for (int i = 0; i < dotscount; i++) {
//                        dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_selected_dots));
//                    }
//
//                    dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.selected_dots));
//                }
//
//                @Override
//                public void onPageSelected(int position) {
//
//                }
//
//                @Override
//                public void onPageScrollStateChanged(int state) {
//
//                }
//            });
//
//        }
//    }
    @SuppressLint("UseCompatLoadingForDrawables")
    private void toolBarSetup() {
        binding.toolBar.setTitle("Phase Detail");
        binding.toolBar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_baseline_arrow_back_24));
        binding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {
            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                int cout = data.getClipData().getItemCount();
                for (int i = 0; i < cout; i++) {
                    Uri imageurl = data.getClipData().getItemAt(i).getUri();
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageurl);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
                    final String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                    Log.d("wow123", "StringArrayImages: " + encodedImage);
                    arrayList_images.add(encodedImage);
//                    mArrayUri.add(imageurl);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.EDIT_PHASE_IMAGES, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("wow123", "onResponse: addnewimage"+response);
                            imagesApiCall();
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
                            String data = gson.toJson(arrayList_images);

                            Map<String,String> params = new HashMap<>();
                            params.put("phase_id", phaseId);
                            params.put("images", data);
                            return params;
                        }
                    };
                    MySingleton.getInstance(PhaseDetailActivity.this).addToRequestQueue(stringRequest);


                }
                position = 0;
            }
            else {
                Bitmap bitmap = null;

                Uri imageurl = data.getData();
                mArrayUri.add(imageurl);

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageurl);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
                final String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                arrayList_images.add(encodedImage);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.EDIT_PHASE_IMAGES, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("wow123", "onResponse: addnewimage"+response);
                        imagesApiCall();
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
                        String data = gson.toJson(arrayList_images);

                        Map<String,String> params = new HashMap<>();
                        params.put("phase_id", phaseId);
                        params.put("images", data);
                        return params;
                    }
                };
                MySingleton.getInstance(PhaseDetailActivity.this).addToRequestQueue(stringRequest);


//                binding.image.setImageURI(mArrayUri.get(0));
                position = 0;
            }
        }

    }
}
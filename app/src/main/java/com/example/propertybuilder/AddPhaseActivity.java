package com.example.propertybuilder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.propertybuilder.ConstantApis.Api;
import com.example.propertybuilder.ConstantApis.MySingleton;
import com.example.propertybuilder.databinding.ActivityAddPhaseBinding;
import com.example.propertybuilder.utils.Upload;
import com.google.gson.Gson;

import net.alhazmy13.mediapicker.Image.ImagePicker;
import net.alhazmy13.mediapicker.Video.VideoPicker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hb.xvideoplayer.MxVideoPlayer;

public class AddPhaseActivity extends AppCompatActivity {

    ActivityAddPhaseBinding binding;
    public static final String TAG = "wow123";
    ArrayList<String> arrayList_images = new ArrayList<>();
    ArrayList<Uri> mArrayUri;
    private static final int SELECT_VIDEO = 3;
    int PICK_IMAGE_MULTIPLE = 1;
    Uri selectedImageUri;
    Bitmap bitmap;
    MediaController mediaController;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    int position = 0;
    Intent intent;
    String postId;
    private String selectedPath = "0";


    public void adjustFontScale(Configuration configuration) {
        configuration.fontScale = (float) 1.0;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        getBaseContext().getResources().updateConfiguration(configuration, metrics);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustFontScale(getResources().getConfiguration());
        binding = ActivityAddPhaseBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        intent = getIntent();
        mArrayUri = new ArrayList<Uri>();
        toolBarSetup();
        setUpImageSeletor();
        clicks();

        postId = intent.getStringExtra("addId");
        binding.selectFeb.setVisibility(View.VISIBLE);
        binding.next.setVisibility(View.GONE);
        binding.previous.setVisibility(View.GONE);
    }

    private void clicks() {

        binding.confirmPhaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phaseDes = binding.phaseDesc.getText().toString();

                if (arrayList_images.isEmpty()) {
                    Toast.makeText(AddPhaseActivity.this, "Select Images For Phase", Toast.LENGTH_SHORT).show();
                } else if (phaseDes.isEmpty()) {
                    binding.phaseDesc.requestFocus();
                    binding.phaseDesc.setError("Enter Phase Description");
                } else if (selectedPath.equals("0")) {
                    Toast.makeText(AddPhaseActivity.this, "Select Video For Phase", Toast.LENGTH_SHORT).show();

                } else {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.confirmPhaseBtn.setVisibility(View.GONE);
                    uploadVideo(phaseDes);

                }
            }
        });
    }

    private void phaseApiCall(String phaseDes, String post_video) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.ADD_PHASE_NEW, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse:000000    " + response + "     00000");

                finish();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddPhaseActivity.this, "Error" + error.getMessage(), Toast.LENGTH_SHORT).show();
                binding.progressBar.setVisibility(View.GONE);
                binding.confirmPhaseBtn.setVisibility(View.VISIBLE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Gson gson = new Gson();
                String data = gson.toJson(arrayList_images);

                Map<String, String> params = new HashMap<>();
                params.put("description", phaseDes);
                params.put("images", data);
                params.put("post_id", postId);
                params.put("post_video", post_video);

                return params;
            }
        };
        MySingleton.getInstance(AddPhaseActivity.this).addToRequestQueue(stringRequest);
    }

    private void setUpImageSeletor() {
        binding.image.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(getApplicationContext());
                return imageView;
            }
        });
        binding.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position < mArrayUri.size() - 1) {
                    // increase the position by 1
                    position++;
                    binding.image.setImageURI(mArrayUri.get(position));
                }
            }
        });
        binding.previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position > 0) {
                    // decrease the position by 1
                    position--;
                    binding.image.setImageURI(mArrayUri.get(position));
                }
            }
        });
        binding.selectFeb.setOnClickListener(new View.OnClickListener() {
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
        binding.selectVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                selectVideoFromGallery();

            }
        });
        binding.selectVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissionREAD_EXTERNAL_STORAGE(AddPhaseActivity.this)) {
                    // do your stuff..
                    chooseVideo();
                }
            }
        });
    }

    private void uploadVideo(String phaseDes) {
        class UploadVideo extends AsyncTask<Void, Void, String> {

            ProgressDialog uploading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                uploading = ProgressDialog.show(AddPhaseActivity.this, "Uploading File", "Please wait...", false, false);
            }

            @Override
            protected String doInBackground(Void... params) {
                Upload u = new Upload();
                String msg = u.upLoad2Server(selectedPath);
                Log.d("wow123", "doInBackground: " + msg);
                return msg;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                phaseApiCall(phaseDes, s);

                uploading.dismiss();
            }


        }
        UploadVideo uv = new UploadVideo();
        uv.execute();
    }

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{permission},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a Video "), SELECT_VIDEO);
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private void toolBarSetup() {
        binding.toolBar.setTitle("Add Phase");
        binding.toolBar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_baseline_arrow_back_24));
        binding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {
            if (data.getClipData() != null) {

                ClipData mClipData = data.getClipData();
                int cout = data.getClipData().getItemCount();
                Log.d(TAG, "onActivityResult: Count"+cout);
                for (int i = 0; i < cout; i++) {
                    // adding imageuri in array
                    Uri imageurl = data.getClipData().getItemAt(i).getUri();
                    Toast.makeText(this, "getui" + imageurl, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onActivityResult: Uri" + imageurl);
                    mArrayUri.add(imageurl);
                    binding.selectFeb.setVisibility(View.GONE);
                    binding.next.setVisibility(View.VISIBLE);
                    binding.previous.setVisibility(View.VISIBLE);

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageurl);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
                        final String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                        Log.d("wow123", "StringArrayImages: " + encodedImage);

                        arrayList_images.add(encodedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // setting 1st selected image into image switcher
                binding.image.setImageURI(mArrayUri.get(0));
                position = 0;
            } else {
                Uri imageurl = data.getData();
                Log.d(TAG, "onActivityResult: getData" + imageurl);
                Toast.makeText(this, "getdata" + imageurl, Toast.LENGTH_SHORT).show();
                mArrayUri.add(imageurl);
                binding.selectFeb.setVisibility(View.GONE);
                binding.next.setVisibility(View.VISIBLE);
                binding.previous.setVisibility(View.VISIBLE);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageurl);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
                    final String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                    Log.d("wow123", "StringArrayImages: " + encodedImage);

                    arrayList_images.add(encodedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                binding.image.setImageURI(mArrayUri.get(0));
                position = 0;
            }
        } else if (requestCode == SELECT_VIDEO) {
            System.out.println("SELECT_VIDEO");

            if (data!=null){
                binding.noVideoText.setVisibility(View.GONE);
                selectedImageUri = data.getData();
                selectedPath = getPath(selectedImageUri);
                videoPlayer();
            }
        }

    }

    private void videoPlayer() {
        binding.videoView.setMediaController(mediaController);
        binding.videoView.setMediaController(mediaController);
        binding.videoView.setVideoURI(selectedImageUri);
        binding.videoView.requestFocus();
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(binding.videoView);
        binding.videoView.setMediaController(mediaController);
        binding.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.i("wow123", "Duration = " +
                        binding.videoView.getDuration());
            }
        });
        binding.videoView.start();

    }

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        Log.d("wow123", "getPath: " + path);
        cursor.close();

        return path;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chooseVideo();
                } else {
                    Toast.makeText(AddPhaseActivity.this, "GET_ACCOUNTS Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }
}
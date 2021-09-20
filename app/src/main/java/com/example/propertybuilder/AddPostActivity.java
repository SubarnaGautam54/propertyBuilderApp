package com.example.propertybuilder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.propertybuilder.ConstantApis.Api;
import com.example.propertybuilder.ConstantApis.MySingleton;
import com.example.propertybuilder.Models.UserModel;
import com.example.propertybuilder.SharedPreference.SharedPrefManager;
import com.example.propertybuilder.databinding.ActivityAddPostBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class AddPostActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener,GoogleMap.OnMarkerDragListener{
    ActivityAddPostBinding binding;
    public static final String TAG = "wow123";
    double selectedPointLat, selectedPointLog;
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    GoogleMap mMap;
    Marker marker;

    ////////////////////////////////////////////////////////////////
    public static final String FIREBASE_NOTIFICATION_SERVER_KEY = "key=AAAAjEeogSU:APA91bEa1suwdOU23my3AcKQKobAethPxjdrg-G1HY-woF7jJoIGGeV-D0JGRuUagygOc1MdZKBajB8V9zQvQ4uaie7LEq3ZEZi5CZ1lwx8QjOFlGMhdBEX1DQlzGzY21MNL2TOqSC3y";
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String contentType = "application/json";
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPostBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(this);
        client = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(AddPostActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(AddPostActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        UserModel usersModel = SharedPrefManager.getInstance(this).getUser();
        userId = usersModel.getId();
         selectedPointLat = 0;
        selectedPointLog = 0;
        clicks();
    }
    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.setOnMapClickListener(this);
    }
    @SuppressWarnings("MissingPermission")

    private void getCurrentLocation() {
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            LatLng latLng = new LatLng(location.getLatitude()
                                    , location.getLongitude());
                            String latitude = String.valueOf(location.getLatitude());
                            Log.d(TAG, "onMapReady lat: " + latitude);
                            Toast.makeText(AddPostActivity.this, "" + latLng, Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onMapReady: " + latLng);

                            MarkerOptions options = new MarkerOptions().
                                    position(latLng).
                                    title("My Location")
                                    .icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_pin_person));
                            Log.d(TAG, "onMapReady: " + options);


                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            googleMap.addMarker(options);
                        }
                    });
                }

            }
        });
    }
    @Override
    public void onMapClick(@NonNull @NotNull LatLng latLng) {
         selectedPointLat  = latLng.latitude;
         selectedPointLog  = latLng.longitude;
        Log.d(TAG, "onMapClick: "+selectedPointLat+ " "+selectedPointLog);


       if (marker != null){
           marker.remove();
       }

            marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_pin))
                    .draggable(true)
                    .title("Property Location"));
        marker.showInfoWindow();
        Geocoder geocoder = new Geocoder(AddPostActivity.this, Locale.getDefault());

        List<Address> addresses  = null;
        try {
            addresses = geocoder.getFromLocation(selectedPointLat,selectedPointLog, 1);
            String address = addresses.get(0).getAddressLine(0);
//        String city = addresses.get(0).getLocality();
//        String state = addresses.get(0).getAdmainArea();
//        String zip = addresses.get(0).getPostalCode();
//        String country = addresses.get(0).getCountryName();
            Log.d(TAG, "onMapClick: address"+address);
            if (!address.isEmpty()){
                binding.propertyAddressLy.setVisibility(View.VISIBLE);
                binding.propertyAddress.setText(address);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Please Make Sure For Internet Connection!", Toast.LENGTH_SHORT).show();
            selectedPointLog = 0;
            selectedPointLog = 0;

        }


        }


    private void clicks() {
        binding.sendPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkVelidation();
            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.addPhase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddPostActivity.this, AddPhaseActivity.class));
            }
        });
    }




    private void checkVelidation() {

        String postName = binding.postNameEdt.getText().toString();
        String postDec = binding.postDecEdt.getText().toString();
        if (selectedPointLat == 0 || selectedPointLog == 0) {
            Toast.makeText(this, "Please Select Property Location.", Toast.LENGTH_SHORT).show();
        } else if (postName.isEmpty()) {
            binding.postNameEdt.requestFocus();
            binding.postNameEdt.setError("Post Name Must Not Be Given Empty");
        } else if (postDec.isEmpty()) {
            binding.postDecEdt.requestFocus();
            binding.postDecEdt.setError("Post Description Must Not Be Given Empty");
        }
//        else if (arrayList_images.isEmpty()) {
//            Toast.makeText(this, "Select Images Of Property", Toast.LENGTH_SHORT).show();

//    }
    else {
            binding.sendPostBtn.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.ADD_POST_NEW, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "onResponse:000000    " + response + "     00000");

                    createnotification("customer",postName, postDec, "user");
                    finish();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(AddPostActivity.this, "Error"+error.getMessage(), Toast.LENGTH_SHORT).show();
                    binding.sendPostBtn.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.GONE);

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<>();
                    params.put("name", postName);
                    params.put("description", postDec);
                    params.put("developer_id", String.valueOf(userId));
                    params.put("lat", String.valueOf(selectedPointLat));
                    params.put("long", String.valueOf(selectedPointLog));
                    return params;
                }
            };
            MySingleton.getInstance(AddPostActivity.this).addToRequestQueue(stringRequest);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }
        }
    }

    public void createnotification(String topic, String title, String message, String id) {
        Log.i(TAG, "createnotification: notification");
        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("topic", topic);
            notifcationBody.put("title", title);
            notifcationBody.put("message", message);
            notification.put("to", "/topics/" + id);
            notification.put("data", notifcationBody);
            sendNotification(notification);
        } catch (JSONException e) {
            Log.i(TAG, "onCreate: " + e.getMessage());
        }
    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", FIREBASE_NOTIFICATION_SERVER_KEY);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
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
    public void onMarkerDragStart(@NonNull @NotNull Marker marker) {

    }

    @Override
    public void onMarkerDrag(@NonNull @NotNull Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(@NonNull @NotNull Marker marker) {
        LatLng latLng = marker.getPosition();
        double lat = latLng.latitude;
        double lng = latLng.longitude;
        Log.d(TAG, "onMarkerDragEnd: "+lat+" "+lng);
    }
}
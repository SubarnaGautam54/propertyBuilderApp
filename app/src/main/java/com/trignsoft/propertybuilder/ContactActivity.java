package com.trignsoft.propertybuilder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.propertybuilder.R;
import com.example.propertybuilder.databinding.ActivityContactBinding;

public class ContactActivity extends AppCompatActivity {

    ActivityContactBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        toolBarSetup();
    }

    private void toolBarSetup() {
        binding.toolBar.setTitle("Contact Information");
        binding.toolBar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_baseline_arrow_back_24));
        binding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
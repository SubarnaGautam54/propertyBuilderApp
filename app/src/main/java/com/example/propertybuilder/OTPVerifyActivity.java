package com.example.propertybuilder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.propertybuilder.SharedPreference.DataProccessor;
import com.example.propertybuilder.databinding.ActivityOtpverifiyBinding;

public class OTPVerifyActivity extends AppCompatActivity {

    ActivityOtpverifiyBinding binding;
    String code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpverifiyBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        DataProccessor dataProccessor = new DataProccessor(this);
         code = String.valueOf(DataProccessor.getInt("code"));
        clicks();
    }


            private void clicks() {
                binding.confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String otp = binding.editTextNumberOtp.getText().toString();
                        if (otp.isEmpty()) {
                            binding.editTextNumberOtp.requestFocus();
                            binding.editTextNumberOtp.setError("Enter OTP Number");
                        } else if (otp.length() < 4) {
                            binding.editTextNumberOtp.requestFocus();
                            binding.editTextNumberOtp.setError("OTP Number must have 4 digits");
                        } else if (!otp.matches(code)) {
                            binding.editTextNumberOtp.requestFocus();
                            binding.editTextNumberOtp.setError("Incorrect Otp Number");
                        } else {
//                    Toast.makeText(OTPVerifyActivity.this, "success", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(OTPVerifyActivity.this, FNewPasswordActivity.class));
                            finish();
                        }
                    }
                });
                binding.backBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(OTPVerifyActivity.this,ForgotPasswordActivity.class));
                        finish();

                    }
                });

                }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(OTPVerifyActivity.this,SignInActivity.class));
        finish();
            }
            }
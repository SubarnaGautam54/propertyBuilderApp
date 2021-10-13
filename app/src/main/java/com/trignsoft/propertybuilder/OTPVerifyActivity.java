package com.trignsoft.propertybuilder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.trignsoft.propertybuilder.SharedPreference.DataProccessor;
import com.example.propertybuilder.databinding.ActivityOtpverifiyBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OTPVerifyActivity extends AppCompatActivity {

    ActivityOtpverifiyBinding binding;
    String code;
    String phoneNumber, otpId;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpverifiyBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        phoneNumber = getIntent().getStringExtra("mobile").toString();
        Log.d("wow123", "onCreate: " + phoneNumber);
        DataProccessor dataProccessor = new DataProccessor(this);
        code = String.valueOf(DataProccessor.getInt("code"));
//         binding.progressBar2.setVisibility(View.VISIBLE);
//         binding.confirm.setVisibility(View.GONE);
//        clicks();
        mAuth = FirebaseAuth.getInstance();
        initializeotp();

        binding.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.editTextNumberOtp.getText().toString().isEmpty()) {
                    Toast.makeText(OTPVerifyActivity.this, "Otp Must not be set blank", Toast.LENGTH_SHORT).show();
                } else if (binding.editTextNumberOtp.getText().length() != 6) {
                    Toast.makeText(OTPVerifyActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                } else if (otpId != null) {
                    binding.progressBar2.setVisibility(View.VISIBLE);
                    binding.confirm.setVisibility(View.GONE);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otpId, binding.editTextNumberOtp.getText().toString());
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

    }

    private void initializeotp() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        otpId = s;
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                        binding.progressBar2.setVisibility(View.GONE);
                        binding.confirm.setVisibility(View.VISIBLE);
                        String code = phoneAuthCredential.getSmsCode();

                        if (code != null) {
                            binding.editTextNumberOtp.setText(code);
                        }
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(OTPVerifyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("wow123", "onVerificationFailed: " + e.getMessage());
                        binding.progressBar2.setVisibility(View.GONE);
                        binding.confirm.setVisibility(View.VISIBLE);

                    }
                }
        );
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OTPVerifyActivity.this,ForgotPasswordActivity.class));
                finish();

            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(OTPVerifyActivity.this, FNewPasswordActivity.class));
                            finish();
                            binding.progressBar2.setVisibility(View.GONE);
                            binding.confirm.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(OTPVerifyActivity.this, "signing code error", Toast.LENGTH_SHORT).show();
                            binding.progressBar2.setVisibility(View.GONE);
                            binding.confirm.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

//    private void clicks() {
//        binding.confirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String otp = binding.editTextNumberOtp.getText().toString();
//                if (otp.isEmpty() ){
//                    binding.editTextNumberOtp.requestFocus();
//                    binding.editTextNumberOtp.setError("Enter OTP Number");
//                }else if (otp.length() < 4){
//                    binding.editTextNumberOtp.requestFocus();
//                    binding.editTextNumberOtp.setError("OTP Number must have 4 digits");
//                }else if (!otp.matches(code)){
//                    binding.editTextNumberOtp.requestFocus();
//                    binding.editTextNumberOtp.setError("Incorrect Otp Number");
//                }else {
////                    Toast.makeText(OTPVerifyActivity.this, "success", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(OTPVerifyActivity.this,FNewPasswordActivity.class));
//                    finish();
//                }
//            }
//        });
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(OTPVerifyActivity.this,ForgotPasswordActivity.class));
        finish();
    }
}
package com.example.firebasephoneauth;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyPhoneActivity extends AppCompatActivity {
    private Context context;
    private EditText code;
    private Button signInBtn;
    private String sentVerificationCode;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);
        context = VerifyPhoneActivity.this;
        code = (EditText) findViewById(R.id.verificationCode);
        progressBar = findViewById(R.id.progressbar);
        mAuth = FirebaseAuth.getInstance();
        signInBtn = (Button) findViewById(R.id.signIn);

        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        progressBar.setVisibility(View.VISIBLE);
        sendVerificationCode(phoneNumber);  //calling this method for auto detection + auto sign in

        //if auto detection dont work the use the verification code manually
        //then click signIn button to sign in
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codeScannedFromSMS = code.getText().toString().trim();

                if (TextUtils.isEmpty(codeScannedFromSMS))
                {
                    code.setError("Enter code!");
                    code.requestFocus();
                }
                else
                {
                    VerifyCode(codeScannedFromSMS);
                }

            }
        });
    }

    private void sendVerificationCode(String phoneNumber)
    {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                30,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            System.out.println("----------------------------------Storing sent code");
            sentVerificationCode = s;  ///storing the code that sent to the phone number
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String codeScannedFromSMS = phoneAuthCredential.getSmsCode();  ///storing the code that we got form the SMS
            if(codeScannedFromSMS != null)
            {
                code.setText(codeScannedFromSMS);
                VerifyCode(codeScannedFromSMS);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void VerifyCode(String codeScannedFromSMS)
    {
        System.out.println("----------------------entering VerifyCode method");
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(
                sentVerificationCode,
                codeScannedFromSMS
        );

        SignInWithCredential(credential);
    }

    private void SignInWithCredential(final PhoneAuthCredential credential)
    {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Intent profileIntent = new Intent(context, ProfileActivity.class);
                            profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            progressBar.setVisibility(View.INVISIBLE);
                            startActivity(profileIntent);
                            finish();
                        }
                        else
                        {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

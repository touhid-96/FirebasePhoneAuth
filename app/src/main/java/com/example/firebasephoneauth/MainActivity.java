package com.example.firebasephoneauth;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private Context context;

    private Spinner country;
    private EditText phone;
    private Button continueBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;

        country = findViewById(R.id.spinnerCountries);
        country.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, CountryData.countryNames));

        phone = findViewById(R.id.editTextPhone);
        continueBtn = findViewById(R.id.ContinueBtn);

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String countryCode = CountryData.countryCodes[country.getSelectedItemPosition()];
                String phoneNumber = phone.getText().toString().trim();

                if(TextUtils.isEmpty(phoneNumber))
                {
                    phone.setError("Number is required!");
                    phone.requestFocus();
                }
                else if(phoneNumber.length()<10 | phoneNumber.length()>10)
                {
                    Toast.makeText(context, "Invalid phone number!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String phoneNumberWithCountryCode = countryCode+phoneNumber;

                    Intent verifyIntent = new Intent(context, VerifyPhoneActivity.class);
                    verifyIntent.putExtra("phoneNumber", phoneNumberWithCountryCode);
                    startActivity(verifyIntent);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            Intent profileIntent = new Intent(context, ProfileActivity.class);
            profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(profileIntent);
            finish();
        }
    }
}

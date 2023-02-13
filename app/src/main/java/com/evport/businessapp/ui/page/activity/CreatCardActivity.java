package com.evport.businessapp.ui.page.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.evport.businessapp.R;

public class CreatCardActivity extends AppCompatActivity {
    ImageView back;
    EditText input_card_number;
    EditText input_year;
    EditText input_month;
    EditText input_first_name;
    EditText input_last_name;
    TextView confrim;
    EditText input_cvc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat_card);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        BarUtils.setStatusBarColor(this, Color.TRANSPARENT);
//        BarUtils.setStatusBarLightMode(this, true);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.white));
        initView();
    }

    private void initView() {
        back = findViewById(R.id.back);
        input_card_number = findViewById(R.id.input_card_number);
        input_year = findViewById(R.id.input_year);
        input_month = findViewById(R.id.input_month);
        input_first_name = findViewById(R.id.input_first_name);
        input_last_name = findViewById(R.id.input_last_name);
        input_cvc= findViewById(R.id.input_cvc);
        confrim = findViewById(R.id.confrim);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreatCardActivity.this.finish();
            }
        });
        confrim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cardNumber = input_card_number.getText().toString();
                if (TextUtils.isEmpty(cardNumber) || cardNumber.length() != 16) {
                    showToast(getString(R.string.correctcardnumber));
                    return;
                }
                String year = input_year.getText().toString();
                if (TextUtils.isEmpty(year) || year.length() != 4) {
                    showToast(getString(R.string.correctyear));
                    return;
                }
                String month = input_month.getText().toString();
                if (TextUtils.isEmpty(month) || month.length() != 2) {
                    showToast(getString(R.string.correctmonth));
                    return;
                }
                String firstName = input_first_name.getText().toString();
                if (TextUtils.isEmpty(firstName)) {
                    showToast(getString(R.string.correctfirstname));
                    return;
                }
                String lastName = input_last_name.getText().toString();
                if (TextUtils.isEmpty(lastName)) {
                    showToast(getString(R.string.correctlastname));
                    return;
                }
                String cvc = input_cvc.getText().toString();
                if (TextUtils.isEmpty(lastName)) {
                    showToast(getString(R.string.correctcvc));
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("cardNumber", cardNumber);
                intent.putExtra("year", year);
                intent.putExtra("month", month);
                intent.putExtra("firstName", firstName);
                intent.putExtra("lastName", lastName);
                intent.putExtra("cvc", cvc);
                CreatCardActivity.this.setResult(100, intent);
                CreatCardActivity.this.finish();

            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
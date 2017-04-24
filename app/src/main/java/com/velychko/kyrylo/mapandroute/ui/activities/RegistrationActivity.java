package com.velychko.kyrylo.mapandroute.ui.activities;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.velychko.kyrylo.mapandroute.R;
import com.velychko.kyrylo.mapandroute.data.SQLite.DataModel.UserModel;
import com.velychko.kyrylo.mapandroute.data.SQLite.DatabaseMaster;

import static com.velychko.kyrylo.mapandroute.R.id.btn_sign_in;
import static com.velychko.kyrylo.mapandroute.R.id.til_confirm_password;
import static com.velychko.kyrylo.mapandroute.R.id.til_name;
import static com.velychko.kyrylo.mapandroute.R.id.til_password;

public class RegistrationActivity extends AppCompatActivity {

    TextInputLayout til_name;
    TextInputLayout til_password;
    TextInputLayout til_confirm_password;
    Button btn_sign_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regictration);

        til_name = (TextInputLayout) findViewById(R.id.til_name);
        til_name.requestFocus();
        til_password = (TextInputLayout) findViewById(R.id.til_password);
        til_confirm_password = (TextInputLayout) findViewById(R.id.til_confirm_password);
        btn_sign_up = (Button) findViewById(R.id.btn_sign_up);
        btn_sign_up.setOnClickListener(onSignUpClickListener());
    }

    private View.OnClickListener onSignUpClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (til_name.getEditText().getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Please input your login", Toast.LENGTH_LONG).show();
                    return;
                } else if (til_password.getEditText().getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Please input your password", Toast.LENGTH_LONG).show();
                    return;
                } else if (til_confirm_password.getEditText().getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Please confirm your password", Toast.LENGTH_LONG).show();
                    return;
                } else if (!til_password.getEditText().getText().toString()
                        .equals(til_confirm_password.getEditText().getText().toString())){
                    Toast.makeText(getApplicationContext(), "Passwords don't match", Toast.LENGTH_LONG).show();
                    return;
                }

                String name = til_name.getEditText().getText().toString();
                String password = til_password.getEditText().getText().toString();
                DatabaseMaster.getInstance(getApplicationContext()).addUser(
                        new UserModel(name, password));

                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                intent.putExtra("user_name", til_name.getEditText().getText().toString());
                startActivity(intent);
            }
        };
    }

}

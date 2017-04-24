package com.velychko.kyrylo.mapandroute.ui.activities;

import android.content.Intent;
import android.database.Cursor;
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

import static android.R.attr.name;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout til_name;
    TextInputLayout til_password;
    Button btn_sign_in;
    TextView tv_sign_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        til_name = (TextInputLayout) findViewById(R.id.til_name);
        til_password = (TextInputLayout) findViewById(R.id.til_password);
        btn_sign_in = (Button) findViewById(R.id.btn_sign_in);
        btn_sign_in.setOnClickListener(onSignInClickListener());
        tv_sign_up = (TextView) findViewById(R.id.tv_sign_up);
        tv_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            }
        });
    }


    private View.OnClickListener onSignInClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (til_name.getEditText().getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Please input your login", Toast.LENGTH_LONG).show();
                    return;
                } else if (til_password.getEditText().getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Please input your password", Toast.LENGTH_LONG).show();
                    return;
                }

                UserModel userModel = DatabaseMaster.getInstance(getApplicationContext()).getUserByName(
                        til_name.getEditText().getText().toString());
                if (userModel == null){
                    Toast.makeText(getApplicationContext(), "You are not signed up", Toast.LENGTH_LONG).show();
                } else {
                    if (til_password.getEditText().getText().toString().equals(userModel.password)) {
                        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
//                        intent.putExtra("user_id", userModel.id);
                        intent.putExtra("user_name", userModel.name);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Wrong password", Toast.LENGTH_LONG).show();
                        til_password.getEditText().setText("");
                    }
                }
            }
        };
    }

}

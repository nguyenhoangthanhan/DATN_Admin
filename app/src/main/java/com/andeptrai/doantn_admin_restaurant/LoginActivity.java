package com.andeptrai.doantn_admin_restaurant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.andeptrai.doantn_admin_restaurant.URL.urlCheckInfoLoginAdmin;

public class LoginActivity extends AppCompatActivity {

    EditText edtUsername, edtpassword;
    Button btnlogin, btnRegister;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUsername = findViewById(R.id.edtUsername);
        edtpassword = findViewById(R.id.edtpassword);
        btnlogin = findViewById(R.id.btnlogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this,"abc",Toast.LENGTH_SHORT).show();
                edtUsername.setError(null);
                edtpassword.setError(null);

                String username = edtUsername.getText().toString();
                String password = edtpassword.getText().toString();

                if(username.equalsIgnoreCase("")){
                    edtUsername.setError("Không được để trống username");
                }
                else if(password.equalsIgnoreCase("")){
                    edtpassword.setError("Không được để trống Password");
                }
                else{
                    login(username, password);
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
//                startActivity(intent);
            }
        });
    }

    private void login(String phone, String password) {
        ReadJSON(phone, password);
    }

    private void ReadJSON(final String username, final String password){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, urlCheckInfoLoginAdmin
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.trim().equals("Login fail")){

                    try {
                        JSONObject jObject = new JSONObject(response);
                        InfoAdminCurr.currentId = jObject.getInt("Id_customer");
                        InfoAdminCurr.currentUsername = jObject.getString("Username");
                        InfoAdminCurr.currentPhone = jObject.getString("Phone");
                        InfoAdminCurr.currentPwd = jObject.getString("Password");
                        InfoAdminCurr.currentEmail = jObject.getString("Email");
                        InfoAdminCurr.currentName = jObject.getString("Name");
                        InfoAdminCurr.currentAddress = jObject.getString("Address");
                        InfoAdminCurr.list_care_res = jObject.getString("List_care_res");
                        InfoAdminCurr.checkAdmin = jObject.getInt("Check_admin");

                        Toast.makeText(LoginActivity.this, "Login thành công", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                    } catch (JSONException e) {
                        Toast.makeText(LoginActivity.this, "Loi! -- " + e.toString(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(LoginActivity.this, "Login fail!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, "Login fail!---"+error.toString(), Toast.LENGTH_LONG).show();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("usernameAdmin", username);
                params.put("passwordAdmin", password);

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

}
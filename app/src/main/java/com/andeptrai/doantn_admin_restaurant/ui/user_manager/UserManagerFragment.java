package com.andeptrai.doantn_admin_restaurant.ui.user_manager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andeptrai.doantn_admin_restaurant.R;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.andeptrai.doantn_admin_restaurant.URL.urlGetAllUser;
import static com.andeptrai.doantn_admin_restaurant.URL.urlGetInfoUserByUsername;

public class UserManagerFragment extends Fragment {

    TextView txtAddNewUser;
    RecyclerView rvUserManager;
    ArrayList<User> userArrayList = new ArrayList<>();
    UserAdapter userAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user_manager, container, false);

        txtAddNewUser = root.findViewById(R.id.txtAddNewUser);
        rvUserManager = root.findViewById(R.id.rvUserManager);

        userAdapter = new UserAdapter(userArrayList, getContext());
        rvUserManager.setAdapter(userAdapter);
        rvUserManager.setLayoutManager(new LinearLayoutManager(getContext()));
        getAllInfoUser();

        txtAddNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewUser();
            }
        });
        return root;
    }

    private void createNewUser() {
        Intent intent = new Intent(getContext(), RegisterActivity.class);
        startActivityForResult(intent, 1000);
    }

    private void getAllInfoUser() {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, urlGetAllUser, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            for (int i = 0; i < response.length();i++){
                                try {
                                    JSONObject object = response.getJSONObject(i);
                                    userArrayList.add(new User(
                                            object.getInt("Id_customer"),
                                            object.getString("Username"),
                                            object.getString("Password"),
                                            object.getString("Name"),
                                            object.getString("Phone"),
                                            object.getString("Email"),
                                            object.getString("Address"),
                                            object.getInt("Check_admin")
                                    ));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            userAdapter.notifyDataSetChanged();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
            requestQueue.add(arrayRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == 1001){
            String newUsername = data.getStringExtra("usernameNew");
            getInfoNewUser(newUsername);
        }
    }

    private void getInfoNewUser(final String newUsername) {

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, urlGetInfoUserByUsername
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.trim().equals("Get new user fail")){
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        userArrayList.add(0, new User(
                                jsonObject.getInt("Id_customer"),
                                jsonObject.getString("Username"),
                                jsonObject.getString("Password"),
                                jsonObject.getString("Name"),
                                jsonObject.getString("Phone"),
                                jsonObject.getString("Email"),
                                jsonObject.getString("Address")
                        ));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    userAdapter.notifyItemInserted(0);
                }
                else{
                    Toast.makeText(getContext(), "Get new user fail!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Get new user error!---"+error.toString(), Toast.LENGTH_LONG).show();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("newUsername", newUsername);

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}
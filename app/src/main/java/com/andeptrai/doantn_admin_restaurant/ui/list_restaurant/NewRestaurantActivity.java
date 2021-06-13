package com.andeptrai.doantn_admin_restaurant.ui.list_restaurant;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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

import static com.andeptrai.doantn_admin_restaurant.URL.urlCreateNewRestaurant;
import static com.andeptrai.doantn_admin_restaurant.URL.urlGetAllKind;
import static com.andeptrai.doantn_admin_restaurant.URL.urlUpdateResByIdRes;
import static com.andeptrai.doantn_admin_restaurant.lb_code.CheckInfoSignUp.checkPass;
import static com.andeptrai.doantn_admin_restaurant.lb_code.CheckInfoSignUp.checkRePass;
import static com.andeptrai.doantn_admin_restaurant.lb_code.CheckInfoSignUp.inputYet;
import static com.andeptrai.doantn_admin_restaurant.lb_code.CheckInfoSignUp.isEmail;

public class NewRestaurantActivity extends AppCompatActivity implements View.OnClickListener{

    EditText edtIdRes, edtNameRes, edtPhoneRes, edtPassword, edtAddress;
    TextView txtService, txtChangeService, txtKind, txtChangeKind;
    Button btnOkCreateNewRes, btnCancel;

    Restaurant newRestaurant = new Restaurant();

    ArrayList<Kind> kindArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_restaurant);

        newRestaurant.setStatus_restaurant(1);
        newRestaurant.setListKind("");

        getAllKind();
        edtIdRes = findViewById(R.id.edtIdRes);
        edtNameRes = findViewById(R.id.edtNameRes);
        edtPhoneRes = findViewById(R.id.edtPhoneRes);
        edtPassword = findViewById(R.id.edtPassword);
        edtAddress = findViewById(R.id.edtAddress);
        txtService = findViewById(R.id.txtService);
        txtChangeService = findViewById(R.id.txtChangeService);
        txtKind = findViewById(R.id.txtKind);
        txtChangeKind = findViewById(R.id.txtChangeKind);
        btnOkCreateNewRes = findViewById(R.id.btnOkCreateNewRes);
        btnCancel = findViewById(R.id.btnCancel);

        txtChangeService.setOnClickListener(this);
        txtChangeKind.setOnClickListener(this);
        btnOkCreateNewRes.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.txtChangeService){
            openChangeServiceDialog();
        }
        if (view.getId() == R.id.txtChangeKind){
            openChangeKindDialog();
        }
        if (view.getId() == R.id.btnCancel){
            finish();
        }
        if (view.getId() == R.id.btnOkCreateNewRes){
            createNewRestaurant();
        }
    }

    private void createNewRestaurant() {
        newRestaurant.setId_restaurant(edtIdRes.getText().toString());
        newRestaurant.setName_restaurant(edtNameRes.getText().toString());
        newRestaurant.setPhone_restaurant(edtPhoneRes.getText().toString());
        newRestaurant.setPassword(edtPassword.getText().toString());
        newRestaurant.setAddress_restaurant(edtAddress.getText().toString());
        newRestaurant.setReview_point(0);

        if (newRestaurant.getId_restaurant().length() < 8){
            edtIdRes.setError("Tài khoản phải lớn hơn 8 ký tự");
        }
        else if(checkPass(newRestaurant.getPassword()).equals("Length litter 8 characters")){
            edtPassword.setError("Mật khẩu phải lớn hơn 8 ký tự");
        }
        else if(inputYet(newRestaurant.getName_restaurant())){
            edtNameRes.setError("Bạn chưa nhập tên nhà hàng");
        }
        else if(inputYet(newRestaurant.getAddress_restaurant())){
            edtNameRes.setError("Bạn chưa nhập địa chỉ");
        }
        else if(inputYet(newRestaurant.getPhone_restaurant())){
            edtNameRes.setError("Bạn chưa nhập sđt");
        }
        else{
            createRestaurantInDB(newRestaurant);
        }

    }

    private void createRestaurantInDB(final Restaurant newRestaurant) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlCreateNewRestaurant
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.trim().equals("Create new restaurant success")){
                    Toast.makeText(NewRestaurantActivity.this, "Create new restaurant success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("newRestaurant", newRestaurant);
                    setResult(101, intent);
                    finish();
                }
                else if (response.trim().equals("Create new restaurant success, list kind fail")){
                    Toast.makeText(NewRestaurantActivity.this, response, Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(NewRestaurantActivity.this, response, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(NewRestaurantActivity.this, "Create new restaurant error!---"+error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("idRestaurant", newRestaurant.getId_restaurant());
                params.put("nameResNew", newRestaurant.getName_restaurant());
                params.put("phoneNew", newRestaurant.getPhone_restaurant()+"");
                params.put("passwordNew", newRestaurant.getPassword());
                params.put("addressNew", newRestaurant.getAddress_restaurant());
                params.put("reviewPointNew", newRestaurant.getReview_point()+"");
                params.put("statusNew", newRestaurant.getStatus_restaurant()+"");
                params.put("listKindNew", newRestaurant.getListKind());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }


    private void openChangeKindDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_select_kind);

        final RecyclerView rvKind = dialog.findViewById(R.id.rvKind);
        Button btnAccept = dialog.findViewById(R.id.btnAccept);
        Button btnBack = dialog.findViewById(R.id.btnBack);
        final ArrayList<Kind> kindArrayList1;
        if (newRestaurant.getStatus_restaurant() == 1){
            kindArrayList1 = new ArrayList<>();
            for (int i = 0; i < kindArrayList.size();i++){
                if(kindArrayList.get(i).getClassifyKind() == 2){
                    kindArrayList1.add(kindArrayList.get(i));
                }
            }
        }
        else if (newRestaurant.getStatus_restaurant() == 2){
            kindArrayList1 = new ArrayList<>();
            for (int i = 0; i < kindArrayList.size();i++){
                if(kindArrayList.get(i).getClassifyKind() == 1){
                    kindArrayList1.add(kindArrayList.get(i));
                }
            }
        }
        else{
            kindArrayList1 = kindArrayList;
        }

        KindAdapter kindAdapter = new KindAdapter(kindArrayList1, this
                , newRestaurant.getStatus_restaurant(), newRestaurant.getListKind());
        rvKind.setAdapter(kindAdapter);
        rvKind.setLayoutManager(new LinearLayoutManager(this));

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String listKindNew = "";
                for (int i = 0; i < kindArrayList1.size();i++){
                    if (kindArrayList1.get(i).isCheck()){
                        listKindNew += kindArrayList1.get(i).getNameKind() + ",";
                    }
                }
                newRestaurant.setListKind(listKindNew);
                txtKind.setText(listKindNew);
                dialog.dismiss();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void openChangeServiceDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_change_service);

        final RadioGroup groupService = dialog.findViewById(R.id.groupService);
        final RadioButton txtServiceReservation = dialog.findViewById(R.id.txtServiceReservation);
        final RadioButton txtServiceDelivery = dialog.findViewById(R.id.txtServiceDelivery);
        final RadioButton txtServiceAll = dialog.findViewById(R.id.txtServiceAll);
        Button btnAccept = dialog.findViewById(R.id.btnAccept);
        Button btnBack = dialog.findViewById(R.id.btnBack);
        groupService.check(R.id.txtServiceReservation);

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (groupService.getCheckedRadioButtonId() == R.id.txtServiceReservation){
                    newRestaurant.setStatus_restaurant(1);
                    txtService.setText(txtServiceReservation.getText().toString());
                }
                else if (groupService.getCheckedRadioButtonId() == R.id.txtServiceDelivery){
                    newRestaurant.setStatus_restaurant(2);
                    txtService.setText(txtServiceDelivery.getText().toString());
                }
                else if (groupService.getCheckedRadioButtonId() == R.id.txtServiceAll){
                    newRestaurant.setStatus_restaurant(3);
                    txtService.setText(txtServiceAll.getText().toString());
                }
                dialog.dismiss();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    private void getAllKind() {
        RequestQueue requestQueue = Volley.newRequestQueue(NewRestaurantActivity.this);
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, urlGetAllKind, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
//                        Toast.makeText(getContext(), response.toString(), Toast.LENGTH_LONG).show();
                        for (int i = 0; i < response.length();i++){
                            try {
                                JSONObject object = response.getJSONObject(i);
                                kindArrayList.add(new Kind(
                                        object.getString("Id_kind"),
                                        object.getString("Name_kind"),
                                        object.getInt("Classify_kind")
                                ));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(NewRestaurantActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
        requestQueue.add(arrayRequest);
    }
}
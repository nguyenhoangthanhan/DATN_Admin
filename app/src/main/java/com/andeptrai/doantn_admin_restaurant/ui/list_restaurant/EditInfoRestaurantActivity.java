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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.andeptrai.doantn_admin_restaurant.R;
import com.andeptrai.doantn_admin_restaurant.ui.user_manager.RegisterActivity;
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

import static com.andeptrai.doantn_admin_restaurant.URL.urlGetAllKind;
import static com.andeptrai.doantn_admin_restaurant.URL.urlGetInfoRestaurant;
import static com.andeptrai.doantn_admin_restaurant.URL.urlGetReviewPointByIdRes;
import static com.andeptrai.doantn_admin_restaurant.URL.urlSignUpAPI;
import static com.andeptrai.doantn_admin_restaurant.URL.urlUpdateResByIdRes;

public class EditInfoRestaurantActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView icPageBack;
    TextView txtId, txtNameRestaurant, txtPhone, txtPassword, txtAddress
            , txtReviewPoint, txtService, txtKind;
    TextView txtChangeName, txtChangePhone, txtChangePassword, txtChangeAddress, txtUpdateReviewPoint
            , txtChangeService, txtChangeKind;
    Button btnSave, btnBack;

    Restaurant currentRestaurant;

    ArrayList<Kind> kindArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info_restaurant);

        currentRestaurant = (Restaurant) getIntent().getSerializableExtra("restaurantCurrent");
        getAllKind();

        icPageBack = findViewById(R.id.icPageBack);
        txtId = findViewById(R.id.txtId);
        txtNameRestaurant = findViewById(R.id.txtNameRestaurant);
        txtPhone = findViewById(R.id.txtPhone);
        txtPassword = findViewById(R.id.txtPassword);
        txtAddress = findViewById(R.id.txtAddress);
        txtReviewPoint = findViewById(R.id.txtReviewPoint);
        txtService = findViewById(R.id.txtService);
        txtKind = findViewById(R.id.txtKind);
        txtChangeName = findViewById(R.id.txtChangeName);
        txtChangePhone = findViewById(R.id.txtChangePhone);
        txtChangePassword = findViewById(R.id.txtChangePassword);
        txtChangeAddress = findViewById(R.id.txtChangeAddress);
        txtUpdateReviewPoint = findViewById(R.id.txtUpdateReviewPoint);
        txtChangeService = findViewById(R.id.txtChangeService);
        txtChangeKind = findViewById(R.id.txtChangeKind);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        txtId.setText(currentRestaurant.getId_restaurant());
        txtNameRestaurant.setText(currentRestaurant.getName_restaurant());
        txtPhone.setText(currentRestaurant.getPhone_restaurant()+"");
        txtPassword.setText(currentRestaurant.getPassword());
        txtAddress.setText(currentRestaurant.getAddress_restaurant());
        txtReviewPoint.setText(currentRestaurant.getReview_point()+"");
        if (currentRestaurant.getStatus_restaurant() == 1) { txtService.setText(R.string.service_res1); }
        else if (currentRestaurant.getStatus_restaurant() == 2) { txtService.setText(R.string.service_res2); }
        else if (currentRestaurant.getStatus_restaurant() == 3) { txtService.setText(R.string.service_res3); }
        txtKind.setText(currentRestaurant.getListKind());

        icPageBack.setOnClickListener(this);
        txtChangeName.setOnClickListener(this);
        txtChangePhone.setOnClickListener(this);
        txtChangePassword.setOnClickListener(this);
        txtChangeAddress.setOnClickListener(this);
        txtUpdateReviewPoint.setOnClickListener(this);
        txtChangeService.setOnClickListener(this);
        txtChangeKind.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnBack.setOnClickListener(this);

    }

    private void getAllKind() {
        RequestQueue requestQueue = Volley.newRequestQueue(EditInfoRestaurantActivity.this);
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
                        Toast.makeText(EditInfoRestaurantActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
        requestQueue.add(arrayRequest);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.icPageBack){
            finish();
        }
        if (view.getId() == R.id.btnBack){
            finish();
        }
        if (view.getId() == R.id.txtChangeName){
            openChangeNameRestaurantDialog();
        }
        if (view.getId() == R.id.txtChangePassword){
            openChangePasswordDialog();
        }
        if (view.getId() == R.id.txtChangePhone){
            openChangePhoneDialog();
        }
        if (view.getId() == R.id.txtChangeAddress){
            openChangeAddressDialog();
        }
        if (view.getId() == R.id.txtChangeService){
            openChangeServiceDialog();
        }
        if (view.getId() == R.id.txtChangeKind){
            openChangeKindDialog();
        }
        if (view.getId() == R.id.txtUpdateReviewPoint){
            updateReviewPoint();
        }
        if (view.getId() == R.id.btnSave){
            updateInDB();
        }
    }

    private void updateInDB() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlUpdateResByIdRes
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.trim().equals("Update restaurant success")){
                    Toast.makeText(EditInfoRestaurantActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("editRestaurant", currentRestaurant);
                    setResult(1001, intent);
                    finish();
                }
                else if (response.trim().equals("Update restaurant success, list kind fail")){
                    Toast.makeText(EditInfoRestaurantActivity.this, response, Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(EditInfoRestaurantActivity.this, response, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EditInfoRestaurantActivity.this, "Update restaurant fail!---"+error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("idRestaurant", currentRestaurant.getId_restaurant());
                params.put("nameResNew", currentRestaurant.getName_restaurant());
                params.put("phoneNew", currentRestaurant.getPhone_restaurant()+"");
                params.put("passwordNew", currentRestaurant.getPassword());
                params.put("addressNew", currentRestaurant.getAddress_restaurant());
                params.put("reviewPointNew", currentRestaurant.getReview_point()+"");
                params.put("statusNew", currentRestaurant.getStatus_restaurant()+"");
                params.put("listKindNew", currentRestaurant.getListKind());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void updateReviewPoint() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlGetReviewPointByIdRes
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.trim().equals("Update review point fail")){
                    double currentReviewPoint = Double.parseDouble(response);
                    currentReviewPoint = (double) Math.round(currentReviewPoint * 10) / 10;
                    currentRestaurant.setReview_point(currentReviewPoint);
                    txtReviewPoint.setText(currentReviewPoint + "");
                    Toast.makeText(EditInfoRestaurantActivity.this, "Update review point success!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(EditInfoRestaurantActivity.this, "Update review point fail!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EditInfoRestaurantActivity.this, "Update review point error!---"+error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("idRestaurant", currentRestaurant.getId_restaurant());
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
        if (currentRestaurant.getStatus_restaurant() == 1){
            kindArrayList1 = new ArrayList<>();
            for (int i = 0; i < kindArrayList.size();i++){
                if(kindArrayList.get(i).getClassifyKind() == 2){
                    kindArrayList1.add(kindArrayList.get(i));
                }
            }
        }
        else if (currentRestaurant.getStatus_restaurant() == 2){
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
                , currentRestaurant.getStatus_restaurant(), currentRestaurant.getListKind());
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
                currentRestaurant.setListKind(listKindNew);
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

        if (currentRestaurant.getStatus_restaurant() == 1) {
            groupService.check(R.id.txtServiceReservation);
        }
        else if (currentRestaurant.getStatus_restaurant() == 2) {
            groupService.check(R.id.txtServiceDelivery);
        }
        else if (currentRestaurant.getStatus_restaurant() == 3) {
            groupService.check(R.id.txtServiceAll);
        }

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (groupService.getCheckedRadioButtonId() == R.id.txtServiceReservation){
                    currentRestaurant.setStatus_restaurant(1);
                    txtService.setText(txtServiceReservation.getText().toString());
                }
                else if (groupService.getCheckedRadioButtonId() == R.id.txtServiceDelivery){
                    currentRestaurant.setStatus_restaurant(2);
                    txtService.setText(txtServiceDelivery.getText().toString());
                }
                else if (groupService.getCheckedRadioButtonId() == R.id.txtServiceAll){
                    currentRestaurant.setStatus_restaurant(3);
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

    private void openChangeAddressDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_edit_content);

        final EditText edtContent = dialog.findViewById(R.id.edtContent);
        Button btnAccept = dialog.findViewById(R.id.btnAccept);
        Button btnBack = dialog.findViewById(R.id.btnBack);

        edtContent.setText(currentRestaurant.getAddress_restaurant()+"");

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtAddress.setText(edtContent.getText().toString());
                currentRestaurant.setAddress_restaurant(edtContent.getText().toString());
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void openChangePhoneDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_edit_content);

        final EditText edtContent = dialog.findViewById(R.id.edtContent);
        Button btnAccept = dialog.findViewById(R.id.btnAccept);
        Button btnBack = dialog.findViewById(R.id.btnBack);

        edtContent.setText(currentRestaurant.getPhone_restaurant()+"");

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtPhone.setText(edtContent.getText().toString()+"");
                currentRestaurant.setPhone_restaurant(edtContent.getText().toString());
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void openChangePasswordDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_edit_content);

        final EditText edtContent = dialog.findViewById(R.id.edtContent);
        Button btnAccept = dialog.findViewById(R.id.btnAccept);
        Button btnBack = dialog.findViewById(R.id.btnBack);

        edtContent.setText(currentRestaurant.getPassword());

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtPassword.setText(edtContent.getText().toString());
                currentRestaurant.setPassword(edtContent.getText().toString());
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void openChangeNameRestaurantDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_edit_content);

        final EditText edtContent = dialog.findViewById(R.id.edtContent);
        Button btnAccept = dialog.findViewById(R.id.btnAccept);
        Button btnBack = dialog.findViewById(R.id.btnBack);

        edtContent.setText(currentRestaurant.getName_restaurant());

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtNameRestaurant.setText(edtContent.getText().toString());
                currentRestaurant.setName_restaurant(edtContent.getText().toString());
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
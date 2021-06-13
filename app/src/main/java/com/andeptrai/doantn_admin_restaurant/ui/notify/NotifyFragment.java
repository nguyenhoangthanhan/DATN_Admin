package com.andeptrai.doantn_admin_restaurant.ui.notify;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andeptrai.doantn_admin_restaurant.InfoAdminCurr;
import com.andeptrai.doantn_admin_restaurant.R;
import com.andeptrai.doantn_admin_restaurant.RandomString;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.andeptrai.doantn_admin_restaurant.CODE.CREATE_NOTIFY_NEW;
import static com.andeptrai.doantn_admin_restaurant.URL.urlCreateNewNotify;
import static com.andeptrai.doantn_admin_restaurant.URL.urlGetAllNotifyByIdRes;


public class NotifyFragment extends Fragment {

    TextView txtAddNewNotify;
    RecyclerView rvNotify;
    ArrayList<Notify> notifyArrayList = new ArrayList<>();
    NotifyAdapter notifyAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notify, container, false);

        txtAddNewNotify = view.findViewById(R.id.txtAddNewNotify);
        rvNotify = view.findViewById(R.id.rvNotify);

//        for (int i = 0; i < 10; i++){
//            notifyArrayList.add(new Notify("notify1", "content 1"));
//        }
        notifyAdapter = new NotifyAdapter(getContext(), notifyArrayList);
        LinearLayoutManager linearLayoutManagerFood = new LinearLayoutManager(getContext());
        rvNotify.setAdapter(notifyAdapter);
        rvNotify.setLayoutManager(linearLayoutManagerFood);
        getAllNotifyByIdRes();

        txtAddNewNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAddNewNotify();
            }
        });

        return view;
    }

    private void getAllNotifyByIdRes() {

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, urlGetAllNotifyByIdRes
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.trim().equals("Get notify this restaurant fail")){
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            notifyArrayList.add(new Notify(jsonObject.getString("Id_notification"),
                                    jsonObject.getString("Id_restaurant"),
                                    jsonObject.getString("Title_notify"),
                                    jsonObject.getString("Content_notification"),
                                    jsonObject.getString("Time_create_notification")));
                        }
                        notifyAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {

                        Toast.makeText(getContext(), "Get notify error exception! -- " + e.toString(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(getContext(), "Get notify fail!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Get all notify by id res error!---"+error.toString(), Toast.LENGTH_LONG).show();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("idRestaurant",  "admin");

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void dialogAddNewNotify() {
        final Dialog dialogAddNotify = new Dialog(getContext());
        dialogAddNotify.setContentView(R.layout.dialog_edit_notify);

        final EditText edtPromotionTitle = dialogAddNotify.findViewById(R.id.edtPromotionTitle);
        final EditText edtPromotionContent = dialogAddNotify.findViewById(R.id.edtPromotionContent);
        Button btnAcceptEditNotify = dialogAddNotify.findViewById(R.id.btnAcceptEditNotify);
        Button btnBackEditNotify = dialogAddNotify.findViewById(R.id.btnBackEditNotify);

        btnAcceptEditNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Notify newNotify = new Notify(new RandomString(CREATE_NOTIFY_NEW, new Random()).nextString()
                        , "Admin"
                        , edtPromotionTitle.getText().toString()
                        , edtPromotionContent.getText().toString()
                        , (new SimpleDateFormat("yyyy-MM-dd hh-mm-ss")).format(new Date()));
                addNotifyInDB(newNotify, dialogAddNotify);
            }
        });

        btnBackEditNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAddNotify.dismiss();
            }
        });

        dialogAddNotify.show();
    }

    private void addNotifyInDB(final Notify notify, final Dialog dialogAddNotify) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, urlCreateNewNotify
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.trim().equals("Create notify success")){
                    notifyArrayList.add(0, notify);
                    notifyAdapter.notifyItemInserted(0);
                    Toast.makeText(getContext(), "Create notify success!", Toast.LENGTH_SHORT).show();
                    dialogAddNotify.dismiss();
                }
                else{
                    Toast.makeText(getContext(), "Create notify fail!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Create notify error ---"+error.toString(), Toast.LENGTH_LONG).show();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("newIdNotify", notify.getId_notification());
                params.put("idRestaurant", notify.getId_restaurant());
                params.put("newTitleNotify", notify.getTitle_notify());
                params.put("newContentNotify", notify.getContent_notification());
                params.put("timeCreateNotify", notify.getTime_create_notification());

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}
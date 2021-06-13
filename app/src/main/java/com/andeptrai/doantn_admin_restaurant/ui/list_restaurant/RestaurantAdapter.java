package com.andeptrai.doantn_admin_restaurant.ui.list_restaurant;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andeptrai.doantn_admin_restaurant.R;
import com.andeptrai.doantn_admin_restaurant.ui.user_manager.User;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.andeptrai.doantn_admin_restaurant.URL.urlChangePassword;
import static com.andeptrai.doantn_admin_restaurant.URL.urlChangePasswordRestaurant;

public class RestaurantAdapter extends RecyclerView.Adapter {

    ArrayList<Restaurant> restaurantArrayList;
    Context mContext;
    EditRestaurantInterf editRestaurantInterf;

    public RestaurantAdapter(ArrayList<Restaurant> restaurantArrayList, Context mContext, EditRestaurantInterf editRestaurantInterf) {
        this.restaurantArrayList = restaurantArrayList;
        this.mContext = mContext;
        this.editRestaurantInterf = editRestaurantInterf;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view =layoutInflater.inflate(R.layout.item_restaurant, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final Restaurant restaurant = restaurantArrayList.get(position);
        RestaurantViewHolder restaurantViewHolder = (RestaurantViewHolder) holder;

        restaurantViewHolder.txtNameRestaurant.setText(restaurant.getName_restaurant());
        restaurantViewHolder.txtPhoneRestaurant.setText(restaurant.getPhone_restaurant()+"");
        restaurantViewHolder.txtAddressRestaurant.setText(restaurant.getAddress_restaurant());

        restaurantViewHolder.txtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDetailRestaurantDialog(restaurant);
            }
        });

        restaurantViewHolder.txtEditPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditPasswordDialog(restaurant, position);
            }
        });

        restaurantViewHolder.txtEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editRestaurantInterf.editRestaurant(restaurant, position);
            }
        });
    }


    private void openEditPasswordDialog(final Restaurant restaurant, final int position) {


        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_edit_content);

        final EditText edtContent = dialog.findViewById(R.id.edtContent);
        Button btnAccept = dialog.findViewById(R.id.btnAccept);
        Button btnBack = dialog.findViewById(R.id.btnBack);

        edtContent.setText(restaurant.getPassword());

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pwdNew = edtContent.getText().toString();
                changePassword(dialog, restaurant, position, restaurant.getId_restaurant(), pwdNew);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void changePassword(final Dialog dialog, final Restaurant restaurant, final int position, final String id, final String newPassword) {
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, urlChangePasswordRestaurant
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.trim().equals("success_change_password")){
                    restaurant.setPassword(newPassword);
                    notifyItemChanged(position);
                    Toast.makeText(mContext, "success_change_password!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                else{
                    Toast.makeText(mContext, "Error change pwd!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, error.toString(), Toast.LENGTH_LONG).show();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("idToChangePwd", id + "");
                params.put("passwordNew", newPassword);

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void openDetailRestaurantDialog(Restaurant restaurant) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_view_restaurant_info);

        TextView txtId = dialog.findViewById(R.id.txtId);
        TextView txtNameRestaurant = dialog.findViewById(R.id.txtNameRestaurant);
        TextView txtPhone = dialog.findViewById(R.id.txtPhone);
        TextView txtPassword = dialog.findViewById(R.id.txtPassword);
        TextView txtAddress = dialog.findViewById(R.id.txtAddress);
        TextView txtReviewPoint = dialog.findViewById(R.id.txtReviewPoint);
        TextView txtService = dialog.findViewById(R.id.txtService);
        TextView txtKind = dialog.findViewById(R.id.txtKind);
        Button btnBack = dialog.findViewById(R.id.btnBack);

        txtId.setText(restaurant.getId_restaurant() + "");
        txtNameRestaurant.setText(restaurant.getName_restaurant() + "");
        txtPhone.setText(restaurant.getPhone_restaurant() + "");
        txtPassword.setText(restaurant.getPassword() + "");
        txtAddress.setText(restaurant.getAddress_restaurant() + "");
        txtReviewPoint.setText(restaurant.getReview_point() + "");
        if (restaurant.getStatus_restaurant() == 1){ txtService.setText(R.string.service_res1); }
        else if(restaurant.getStatus_restaurant() == 2){ txtService.setText(R.string.service_res2); }
        else{ txtService.setText(R.string.service_res3); }
        txtKind.setText(restaurant.getListKind());

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return restaurantArrayList.size();
    }

    class RestaurantViewHolder extends RecyclerView.ViewHolder{

        TextView txtNameRestaurant, txtPhoneRestaurant, txtAddressRestaurant, txtView, txtEditPass, txtEditInfo;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNameRestaurant = itemView.findViewById(R.id.txtNameRestaurant);
            txtPhoneRestaurant = itemView.findViewById(R.id.txtPhoneRestaurant);
            txtAddressRestaurant = itemView.findViewById(R.id.txtAddressRestaurant);
            txtView = itemView.findViewById(R.id.txtView);
            txtEditPass = itemView.findViewById(R.id.txtEditPass);
            txtEditInfo = itemView.findViewById(R.id.txtEditInfo);
        }
    }
}

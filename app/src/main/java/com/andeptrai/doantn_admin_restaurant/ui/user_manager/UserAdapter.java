package com.andeptrai.doantn_admin_restaurant.ui.user_manager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.andeptrai.doantn_admin_restaurant.InfoAdminCurr;
import com.andeptrai.doantn_admin_restaurant.R;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.andeptrai.doantn_admin_restaurant.URL.urlChangePassword;
import static com.andeptrai.doantn_admin_restaurant.URL.urlUpdateAcceptAdmin;
import static com.andeptrai.doantn_admin_restaurant.URL.urlUpdateInfoUser;

public class UserAdapter extends RecyclerView.Adapter {

    ArrayList<User> userArrayList;
    Context mContext;

    public UserAdapter(ArrayList<User> userArrayList, Context mContext) {
        this.userArrayList = userArrayList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view =layoutInflater.inflate(R.layout.item_user_manager, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final User user = userArrayList.get(position);
        UserViewHolder userViewHolder = (UserViewHolder) holder;
        userViewHolder.txtUsername.setText(user.getUsername());
        userViewHolder.txtName.setText(user.getName());
        userViewHolder.txtPhone.setText(user.getPhone());

        if (InfoAdminCurr.checkAdmin == 2){
            if (user.getCheckAdmin() == 0){
                userViewHolder.txtAcceptAdmin.setVisibility(View.VISIBLE);
                userViewHolder.txtRemoveAdmin.setVisibility(View.GONE);
            }
            else if (user.getCheckAdmin() == 1){
                userViewHolder.txtAcceptAdmin.setVisibility(View.GONE);
                userViewHolder.txtRemoveAdmin.setVisibility(View.VISIBLE);
            }
        }
        else{
            userViewHolder.txtAcceptAdmin.setVisibility(View.GONE);
            userViewHolder.txtRemoveAdmin.setVisibility(View.GONE);
        }
        userViewHolder.txtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openViewPrivateInfoDialog(user);
            }
        });

        userViewHolder.txtEditPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditPasswordDialog(user, position);
            }
        });

        userViewHolder.txtEditPrivateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditPrivateInfoDialog(user, position);
            }
        });

        userViewHolder.txtAcceptAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAlertAcceptAdmin(user, position);
            }
        });

        userViewHolder.txtRemoveAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAlertRejectAdmin(user, position);
            }
        });
    }

    private void createAlertRejectAdmin(final User user, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.content_reject_admin).setTitle(R.string.accept_bill);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                acceptAdminInDB(user, position, "0");
                dialogInterface.cancel();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void createAlertAcceptAdmin(final User user, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.content_accept_admin).setTitle(R.string.accept_bill);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                acceptAdminInDB(user, position, "1");
                dialogInterface.cancel();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void acceptAdminInDB(final User user, final int position, final String s) {

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, urlUpdateAcceptAdmin
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.trim().equals("Update accept admin success")){
                    Toast.makeText(mContext, "Update accept admin success!", Toast.LENGTH_SHORT).show();
                    user.setCheckAdmin(Integer.parseInt(s));
                    notifyItemChanged(position);
                }
                else{
                    Toast.makeText(mContext, "Update accept admin fail!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(mContext, "Update accept admin error!---"+error.toString(), Toast.LENGTH_LONG).show();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("username", user.getUsername());
                params.put("checkAdmin", s);

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void openEditPrivateInfoDialog(final User user, final int position) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_edit_user_info);

        TextView txtId = dialog.findViewById(R.id.txtId);
        TextView txtUsername = dialog.findViewById(R.id.txtUsername);
        final EditText edtName = dialog.findViewById(R.id.edtName);
        final EditText edtPhone = dialog.findViewById(R.id.edtPhone);
        final EditText edtEmail = dialog.findViewById(R.id.edtEmail);
        final EditText edtAddress = dialog.findViewById(R.id.edtAddress);
        Button btnAccept = dialog.findViewById(R.id.btnAccept);
        Button btnBack = dialog.findViewById(R.id.btnBack);

        txtId.setText(user.getIdUser() + "");
        txtUsername.setText(user.getUsername() + "");
        edtName.setText(user.getName() + "");
        edtPhone.setText(user.getPhone() + "");
        edtEmail.setText(user.getEmail() + "");
        edtAddress.setText(user.getAddress() + "");

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User userEdit = new User(user.getIdUser(), user.getUsername(), user.getPassword(), edtName.getText().toString()
                        , edtPhone.getText().toString(), edtEmail.getText().toString(), edtAddress.getText().toString());
                upDateInfoUser(user, position, userEdit, dialog);
            }
        });

        dialog.show();
    }

    private void upDateInfoUser(final User user, final int position, final User userEdit, final Dialog dialog) {
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlUpdateInfoUser,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().equals("Update user success")){
                            Toast.makeText(mContext, "Update success!",
                                    Toast.LENGTH_LONG).show();
                            user.setName(userEdit.getName());
                            user.setEmail(userEdit.getEmail());
                            user.setAddress(userEdit.getAddress());
                            user.setPhone(userEdit.getPhone());
                            notifyItemChanged(position);
                            dialog.dismiss();
                        }
                        else{
                            Toast.makeText(mContext, "Error in code server! - " + response.trim(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, "Error Update User!"+error.toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("new_id_customerClient", user.getIdUser() + "");
                params.put("newNameClient", userEdit.getName() + "");
                params.put("newPhoneClient", userEdit.getPhone() + "");
                params.put("newEmailClient", userEdit.getEmail() + "");
                params.put("newAddressClient", userEdit.getAddress() + "");
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void openEditPasswordDialog(final User user, final int position) {

        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_edit_content);

        final EditText edtContent = dialog.findViewById(R.id.edtContent);
        Button btnAccept = dialog.findViewById(R.id.btnAccept);
        Button btnBack = dialog.findViewById(R.id.btnBack);

        edtContent.setText(user.getPassword());

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
                changePassword(dialog, user, position, user.getIdUser(), pwdNew);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void changePassword(final Dialog dialog, final User user, final int position, final int id, final String newPassword) {
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, urlChangePassword
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.trim().equals("success_change_password")){
                    user.setPassword(newPassword);
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

    private void openViewPrivateInfoDialog(User user) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_view_user_info);

        TextView txtId = dialog.findViewById(R.id.txtId);
        TextView txtUsername = dialog.findViewById(R.id.txtUsername);
        TextView txtPassword = dialog.findViewById(R.id.txtPassword);
        TextView txtName = dialog.findViewById(R.id.txtName);
        TextView txtPhone = dialog.findViewById(R.id.txtPhone);
        TextView txtEmail = dialog.findViewById(R.id.txtEmail);
        TextView txtAddress = dialog.findViewById(R.id.txtAddress);
        Button btnBack = dialog.findViewById(R.id.btnBack);

        txtId.setText(user.getIdUser() + "");
        txtUsername.setText(user.getUsername() + "");
        txtPassword.setText(user.getPassword() + "");
        txtName.setText(user.getName() + "");
        txtPhone.setText(user.getPhone() + "");
        txtEmail.setText(user.getEmail() + "");
        txtAddress.setText(user.getAddress() + "");

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
        return userArrayList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder{

        TextView txtUsername, txtName, txtPhone, txtView, txtEditPass, txtEditPrivateInfo, txtAcceptAdmin, txtRemoveAdmin;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtName = itemView.findViewById(R.id.txtName);
            txtPhone = itemView.findViewById(R.id.txtPhone);
            txtView = itemView.findViewById(R.id.txtView);
            txtEditPass = itemView.findViewById(R.id.txtEditPass);
            txtEditPrivateInfo = itemView.findViewById(R.id.txtEditPrivateInfo);
            txtAcceptAdmin = itemView.findViewById(R.id.txtAcceptAdmin);
            txtRemoveAdmin = itemView.findViewById(R.id.txtRemoveAdmin);


        }
    }
}

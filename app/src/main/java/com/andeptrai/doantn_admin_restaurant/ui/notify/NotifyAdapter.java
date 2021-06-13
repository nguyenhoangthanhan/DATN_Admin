package com.andeptrai.doantn_admin_restaurant.ui.notify;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andeptrai.doantn_admin_restaurant.R;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.andeptrai.doantn_admin_restaurant.URL.urlDeleteNotifyAdmin;
import static com.andeptrai.doantn_admin_restaurant.URL.urlEditNotifyAdmin;


public class NotifyAdapter extends RecyclerView.Adapter {

    Context mContext;
    ArrayList<Notify> notifyArrayList;

    public NotifyAdapter(Context mContext, ArrayList<Notify> notifyArrayList) {
        this.mContext = mContext;
        this.notifyArrayList = notifyArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View notifyView = layoutInflater.inflate(R.layout.item_notification,parent, false);
        return new NotifyViewHolder(notifyView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final Notify notify = notifyArrayList.get(position);
        NotifyViewHolder notifyViewHolder = (NotifyViewHolder) holder;
        notifyViewHolder.txtTittleNotify.setText(notify.getTitle_notify());
        notifyViewHolder.txtContentNotify.setText(notify.getContent_notification());

        notifyViewHolder.imgViewNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openViewNotifyDialog(notify);
            }
        });

        notifyViewHolder.imgEditNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditNotifyDialog(notify, position);
            }
        });

        notifyViewHolder.imgDeleteNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteThisNotify(notify, position);
            }
        });
    }

    private void deleteThisNotify(final Notify notify, final int position) {
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, urlDeleteNotifyAdmin
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.trim().equals("Delete notify success")){
                    notifyArrayList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(mContext, "Delete notify success!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(mContext, "Delete notify fail!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "Delete notify error ---"+error.toString(), Toast.LENGTH_LONG).show();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("idNotifyCurrent", notify.getId_notification());

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void openEditNotifyDialog(final Notify notify, final int position) {

        final Dialog dialogEditNotify = new Dialog(mContext);
        dialogEditNotify.setContentView(R.layout.dialog_edit_notify);

        final EditText edtPromotionTitle = dialogEditNotify.findViewById(R.id.edtPromotionTitle);
        edtPromotionTitle.setText(notify.getTitle_notify());
        final EditText edtPromotionContent = dialogEditNotify.findViewById(R.id.edtPromotionContent);
        edtPromotionContent.setText(notify.getContent_notification()+"");
        Button btnAcceptEditNotify = dialogEditNotify.findViewById(R.id.btnAcceptEditNotify);
        Button btnBackEditNotify = dialogEditNotify.findViewById(R.id.btnBackEditNotify);

        btnAcceptEditNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Notify editNotify = new Notify(notify.getId_notification()
                        , notify.getId_restaurant()
                        , edtPromotionTitle.getText().toString()
                        , edtPromotionContent.getText().toString()
                        , notify.getTime_create_notification());
                editNotifyInDB(editNotify, position, dialogEditNotify);
            }
        });

        btnBackEditNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogEditNotify.dismiss();
            }
        });

        dialogEditNotify.show();
    }

    private void editNotifyInDB(final Notify editNotify, final int position, final Dialog dialogEditNotify) {

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, urlEditNotifyAdmin
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.trim().equals("Edit notify success")){
                    notifyArrayList.remove(position);
                    notifyArrayList.add(position, editNotify);
                    notifyItemChanged(position);
                    Toast.makeText(mContext, "Edit notify success!", Toast.LENGTH_SHORT).show();
                    dialogEditNotify.dismiss();
                }
                else{
                    Toast.makeText(mContext, "Edit notify fail!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "Edit notify error ---"+error.toString(), Toast.LENGTH_LONG).show();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("idNotify", editNotify.getId_notification());
                params.put("titleNotify", editNotify.getTitle_notify());
                params.put("contentNotify", editNotify.getContent_notification());

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void openViewNotifyDialog(Notify notify) {

        final Dialog dialogViewNotify = new Dialog(mContext);
        dialogViewNotify.setContentView(R.layout.dialog_view_notify);

        final TextView txtPromotionTitle = dialogViewNotify.findViewById(R.id.txtPromotionTitle);
        txtPromotionTitle.setText(notify.getTitle_notify());
        final TextView txtPromotionContent = dialogViewNotify.findViewById(R.id.txtPromotionContent);
        txtPromotionContent.setText(notify.getContent_notification()+"");
        Button btnBackNotify = dialogViewNotify.findViewById(R.id.btnBackNotify);

        btnBackNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogViewNotify.dismiss();
            }
        });

        dialogViewNotify.show();
    }

    private void openNotify(Notify notify) {
//        Intent intent = new Intent(mContext, mContext);
//        intent.putExtra("notifyInfo", notify);
//        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return notifyArrayList.size();
    }

    private class NotifyViewHolder extends RecyclerView.ViewHolder{

        LinearLayout item_notify_relative;
        TextView txtTittleNotify, txtContentNotify;
        ImageView imgEditNotify, imgDeleteNotify, imgViewNotify;

        public NotifyViewHolder(@NonNull View itemView) {
            super(itemView);
            item_notify_relative = itemView.findViewById(R.id.item_notify_relative);
            txtTittleNotify = itemView.findViewById(R.id.txtTittleNotify);
            txtContentNotify = itemView.findViewById(R.id.txtContentNotify);
            imgViewNotify = itemView.findViewById(R.id.imgViewNotify);
            imgEditNotify = itemView.findViewById(R.id.imgEditNotify);
            imgDeleteNotify = itemView.findViewById(R.id.imgDeleteNotify);

        }
    }
}

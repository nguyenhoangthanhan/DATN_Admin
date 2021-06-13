package com.andeptrai.doantn_admin_restaurant.ui.list_restaurant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andeptrai.doantn_admin_restaurant.R;

import java.util.ArrayList;

public class KindAdapter extends RecyclerView.Adapter {

    ArrayList<Kind> kindArrayList;
    Context mContext;
    int status;
    String listKind;

    public KindAdapter(ArrayList<Kind> kindArrayList, Context mContext, int status, String listKind) {
        this.kindArrayList = kindArrayList;
        this.mContext = mContext;
        this.status = status;
        this.listKind = listKind;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_kind, parent, false);
        return new KindViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Kind kind = kindArrayList.get(position);
        KindViewHolder kindViewHolder = (KindViewHolder) holder;

        kindViewHolder.cbKind.setText(kind.getNameKind());

        String idKindInList = "";
        int check = 0;
        for (int i = 0; i < listKind.length();i++){
            if (listKind.charAt(i) != ','){
                idKindInList += listKind.charAt(i);
            }
            else{
                if(idKindInList.equals(kind.getNameKind())){
                    kindViewHolder.cbKind.setChecked(true);
                    kind.setCheck(true);
                    check = 1;
                    break;
                }
                idKindInList = "";
            }
        }
        if(check == 0){
            kindViewHolder.cbKind.setChecked(false);
            kind.setCheck(false);
        }

        kindViewHolder.cbKind.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                kind.setCheck(b);
            }
        });
    }

    @Override
    public int getItemCount() {
        return kindArrayList.size();
    }

    class KindViewHolder extends RecyclerView.ViewHolder{

        CheckBox cbKind;

        public KindViewHolder(@NonNull View itemView) {
            super(itemView);

            cbKind = itemView.findViewById(R.id.cbKind);
        }
    }
}

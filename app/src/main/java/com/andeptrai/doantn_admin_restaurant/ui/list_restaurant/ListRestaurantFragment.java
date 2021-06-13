package com.andeptrai.doantn_admin_restaurant.ui.list_restaurant;

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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andeptrai.doantn_admin_restaurant.R;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.andeptrai.doantn_admin_restaurant.URL.urlGetInfoRestaurant;

public class ListRestaurantFragment extends Fragment implements EditRestaurantInterf{

    TextView txtAddNewRestaurant;
    RecyclerView rvRestaurant;
    ArrayList<Restaurant> restaurantArrayList = new ArrayList<>();
    RestaurantAdapter restaurantAdapter;
    int currentPosition = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list_restaurant, container, false);

        txtAddNewRestaurant = root.findViewById(R.id.txtAddNewRestaurant);
        rvRestaurant = root.findViewById(R.id.rvRestaurant);
        restaurantAdapter = new RestaurantAdapter(restaurantArrayList, getContext(), this);
        rvRestaurant.setAdapter(restaurantAdapter);
        rvRestaurant.setLayoutManager(new LinearLayoutManager(getContext()));
        getDataRestaurant();

        txtAddNewRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), NewRestaurantActivity.class);
                startActivityForResult(intent, 100);
            }
        });
        return root;
    }

    @Override
    public void editRestaurant(Restaurant restaurant, int position) {
        currentPosition = position;
        Intent intent = new Intent(getContext(), EditInfoRestaurantActivity.class);
        intent.putExtra("restaurantCurrent", restaurant);
        startActivityForResult(intent, 1000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == 1001){
            Restaurant editRes = (Restaurant) data.getSerializableExtra("editRestaurant");
            restaurantArrayList.remove(currentPosition);
            restaurantArrayList.add(currentPosition, editRes);
            restaurantAdapter.notifyItemChanged(currentPosition);
        }
        if (requestCode == 100 && resultCode == 101){
            Restaurant newRestaurant = (Restaurant) data.getSerializableExtra("newRestaurant");
            restaurantArrayList.add(0, newRestaurant);
            restaurantAdapter.notifyItemInserted(0);
        }
    }


    private void getDataRestaurant(){
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, urlGetInfoRestaurant, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
//                        Toast.makeText(getContext(), response.toString(), Toast.LENGTH_LONG).show();
                        for (int i = 0; i < response.length();i++){
                            try {
                                JSONObject object = response.getJSONObject(i);
                                restaurantArrayList.add(new Restaurant(
                                        object.getString("Id_restaurant"),
                                        object.getString("Name_restaurant"),
                                        object.getString("Phone_restaurant"),
                                        object.getString("Password"),
                                        object.getString("Address_restaurant"),
                                        object.getDouble("Review_point"),
                                        object.getInt("Status_restaurant"),
                                        object.getString("Short_description"),
                                        object.getString("Promotion"),
                                        object.getString("List_kind")
                                ));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        restaurantAdapter.notifyDataSetChanged();
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
}
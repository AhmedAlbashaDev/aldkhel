package com.aldkhel.aldkhel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aldkhel.aldkhel.models.Order;
import com.aldkhel.aldkhel.utils.Consts;
import com.aldkhel.aldkhel.utils.Utils;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OrdersHistoryActivity extends AppCompatActivity {

    private static final String TAG = "OrdersHistoryActivity";

    private RecyclerView recyclerView;
    private Adapter adapter;
    private List<Order> orders;

    private String session;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(Consts.APP_FONT)
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        setContentView(R.layout.activity_orders_history);

        session = Utils.getSessionId(this);

        orders = new ArrayList<>();

        recyclerView = findViewById(R.id.recycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new Adapter();
        recyclerView.setAdapter(adapter);


        getHistoryOrders();
    }

    private void getHistoryOrders() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.show();

        AndroidNetworking.get(Consts.API_URL + "rest/order/orders&limit=&page=")
                .setPriority(Priority.HIGH)
                .addHeaders(Consts.API_KEY, Consts.API_KEY_VALUE)
                .addHeaders(Consts.API_SESSION_KEY, session)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        Log.d(TAG, response.toString());

                        try {

                            if (response.getInt("success") != 1) {
                                Toast.makeText(OrdersHistoryActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            JSONArray data = response.getJSONArray("data");

                            orders.clear();
                            JSONObject json;
                            Order order;
                            for (int i=0;i<data.length();i++) {
                                json = data.getJSONObject(i);
                                order = new Order();
                                order.setId(json.getLong("order_id"));
                                order.setDate(json.getString("date_added"));
                                order.setStatus(json.getString("status"));
                                order.setTotal(json.getDouble("total_raw"));
                                orders.add(order);
                            }

                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        dialog.dismiss();
                        error.printStackTrace();
                        Toast.makeText(OrdersHistoryActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private class Adapter extends RecyclerView.Adapter<Adapter.VH> {

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R.layout.item_order, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final VH holder, int position) {
            final Order order = orders.get(position);
            holder.tvId.setText(order.getId() + " #");
            holder.tvStatus.setText(order.getStatus());
            holder.tvDate.setText(order.getDate());
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(OrdersHistoryActivity.this, OrderDetailsActivity.class);
                    intent.putExtra("order_id", order.getId());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return orders.size();
        }

        class VH extends RecyclerView.ViewHolder {

            private TextView tvId;
            private TextView tvStatus;
            private TextView tvDate;
            private View view;

            VH(View v) {
                super(v);
                view = v;
                tvId = v.findViewById(R.id.tvId);
                tvStatus = v.findViewById(R.id.tvStatus);
                tvDate = v.findViewById(R.id.tvDate);
            }
        }

    }

}

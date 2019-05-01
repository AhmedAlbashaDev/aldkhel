package com.aldkhel.aldkhel;

import android.app.ProgressDialog;
import android.content.Context;
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

import com.aldkhel.aldkhel.models.Product;
import com.aldkhel.aldkhel.utils.Consts;
import com.aldkhel.aldkhel.utils.Utils;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OrderDetailsActivity extends AppCompatActivity {

    private static final String TAG = "OrderDetailsActivity";

    private TextView tvId;
    private TextView tvDate;
    private TextView tvPaymentMethod;
    private TextView tvShippingMethod;
    private TextView tvShippingAddress;
    private TextView tvPaymentAddress;
    private TextView tvStatus;
    private TextView tvNote;
    private RecyclerView recyclerView;

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
        setContentView(R.layout.activity_order_details);



        tvId = findViewById(R.id.tvId);
        tvDate = findViewById(R.id.tvDate);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        tvShippingMethod = findViewById(R.id.tvShippingMethod);
        tvShippingAddress = findViewById(R.id.tvShippingAddress);
        tvPaymentAddress = findViewById(R.id.tvPaymentAddress);
        tvStatus = findViewById(R.id.tvStatus);
        tvNote = findViewById(R.id.tvNote);

        recyclerView = findViewById(R.id.recycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getOrderDetails(getIntent().getLongExtra("order_id", 0));
    }

    private void getOrderDetails(long orderId) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.show();

        AndroidNetworking.get(Consts.API_URL + "rest/order/orders&id=" + orderId)
                .addHeaders(Consts.API_KEY, Consts.API_KEY_VALUE)
                .addHeaders(Consts.API_SESSION_KEY, Utils.getSessionId(this))
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        Log.d(TAG, response.toString());

                        try {

                            if (response.getInt("success") != 1) {
                                Toast.makeText(OrderDetailsActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            JSONObject data = response.getJSONObject("data");

                            tvId.setText(String.format(getString(R.string.id_format), data.getLong("order_id")));
                            tvDate.setText(String.format(getString(R.string.order_date_format), data.getString("date_added")));
                            tvPaymentMethod.setText(String.format(getString(R.string.pay_method_format), "دفع عند الاستلام"));
                            tvShippingMethod.setText(String.format(getString(R.string.shipping_method_format), "Flat Shipping Rate"));
                            if (!data.getString("comment").isEmpty() && !data.getString("comment").equals("string")) {
                                tvNote.setText(String.format(getString(R.string.note_format), data.getString("comment")));
                            } else {
                                tvNote.setVisibility(View.GONE);
                            }
                            tvStatus.setText(String.format(getString(R.string.order_status_format),
                                    data.getJSONArray("histories").getJSONObject(0).getString("status")));

                            tvPaymentAddress.setText(data.getString("payment_address").replace("<br />", ", "));
                            tvShippingAddress.setText(data.getString("shipping_address").replace("<br />", ", "));

                            JSONArray arr = data.getJSONArray("products");
                            Product product;
                            JSONObject json;

                            List<Product> products = new ArrayList<>();
                            for (int i=0;i<arr.length();i++) {
                                json = arr.getJSONObject(i);
                                product = new Product();
                                product.setName(json.getString("name"));
                                product.setPrice(json.getDouble("total_raw"));
                                product.setQuantity(json.getInt("quantity"));
                                products.add(product);
                            }

                            recyclerView.setAdapter(new Adapter(products));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        dialog.dismiss();
                        error.printStackTrace();
                        Toast.makeText(OrderDetailsActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.VH> {

        private List<Product>products;

        Adapter(List<Product>products) {
            this.products = products;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R.layout.item_order_product, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final VH holder, int position) {
            final Product product = products.get(position);

            holder.tvName.setText(product.getName());
            double price = product.getOffer() > 0 ? product.getOffer() : product.getPrice();
            holder.tvPrice.setText(String.format(getString(R.string.price_format),
                    price));
            holder.tvPriceTotal.setText(String.format(getString(R.string.price_format),
                    price*product.getQuantity()));
            holder.tvQuantity.setText(product.getQuantity() + "");

        }

        @Override
        public int getItemCount() {
            return products.size();
        }

        class VH extends RecyclerView.ViewHolder {
            private TextView tvName;
            private TextView tvPrice;
            private TextView tvPriceTotal;
            private TextView tvQuantity;

            VH(View v) {
                super(v);
                tvName = v.findViewById(R.id.tvName);
                tvPrice = v.findViewById(R.id.tvPrice);
                tvPriceTotal = v.findViewById(R.id.tvPriceTotal);
                tvQuantity = v.findViewById(R.id.tvQuantity);
            }
        }

    }

}

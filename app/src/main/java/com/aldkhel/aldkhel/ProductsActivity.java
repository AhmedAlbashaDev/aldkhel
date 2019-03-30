package com.aldkhel.aldkhel;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aldkhel.aldkhel.models.Product;
import com.aldkhel.aldkhel.utils.Consts;
import com.aldkhel.aldkhel.utils.SpacesItemDecoration;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProductsActivity extends AppCompatActivity {

    private static final String TAG = "ProductsActivity";

    private RecyclerView recyclerView;
    private List<Product> products;

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
        setContentView(R.layout.activity_products);

        products = new ArrayList<>();

        TextView tvCategory = findViewById(R.id.tvCategory);

        recyclerView = findViewById(R.id.recycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addItemDecoration(new SpacesItemDecoration(3));

    }

    private void getProducts() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.dismiss();

        AndroidNetworking.get(Consts.API_URL + "show/products.php")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        dialog.dismiss();
                        Log.d(TAG, response.toString());

                        try {

                            products.clear();

                            for (int i=0;i<response.length();i++) {
                                products.add(Product.fromJson(response.getJSONObject(i)));
                            }

                            recyclerView.setAdapter(new Adapter());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        dialog.dismiss();
                        error.printStackTrace();
                        Toast.makeText(ProductsActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                    }
                });

    }


    class Adapter extends RecyclerView.Adapter<Adapter.VH> {

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = getLayoutInflater().inflate(R.layout.item_product, viewGroup, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH vh, int i) {

            final Product product = products.get(i);

            Picasso.with(ProductsActivity.this)
                    .load(product.getImageUrl())
                    .into(vh.imageView);

            vh.tvName.setText(product.getName());
            vh.tvPrice.setText(String.format(getString(R.string.price_format), product.getPrice()));
        }

        @Override
        public int getItemCount() {
            return 0;
        }

        class VH extends RecyclerView.ViewHolder {

            private final ImageView imageView;
            private final TextView tvPrice;
            private final TextView tvOffer;
            private final TextView tvName;

            VH(View v) {
                super(v);
                imageView = v.findViewById(R.id.ivImage);
                tvPrice = v.findViewById(R.id.tvPrice);
                tvOffer = v.findViewById(R.id.tvOffer);
                tvName = v.findViewById(R.id.tvName);
            }

        }

    }


}

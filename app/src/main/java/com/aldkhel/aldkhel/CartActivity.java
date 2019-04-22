package com.aldkhel.aldkhel;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aldkhel.aldkhel.models.Product;
import com.aldkhel.aldkhel.utils.Consts;
import com.aldkhel.aldkhel.utils.DbHelper;
import com.squareup.picasso.Picasso;
import com.travijuu.numberpicker.library.NumberPicker;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CartActivity extends AppCompatActivity {

    private static final String TAG = "CartActivity";

    private RecyclerView recyclerView;
    private TextView tvTotal;
    private Adapter adapter;
    private List<Product> products;
    private DbHelper dbHelper;

    private Button bSubmit;

    private double total = 0;
    private String name;
    private String phone;
    private String city;
    private String clientTime;


    private JSONArray jsonArray;

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",
            new Locale("en", "US"));

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
        setContentView(R.layout.activity_cart);

        bSubmit = findViewById(R.id.bSubmit);
        tvTotal = findViewById(R.id.tvTotal);

        dbHelper = new DbHelper(this);

        products = new ArrayList<>();

        recyclerView = findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        new ProductsLoader(this).startLoading();

        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(CartActivity.this, OrderActivity.class));

            }
        });


    }

    private void computeTotal() {

        total = 0;

        for (Product p : products) {
            if (p.getOffer() > 0) {
                total += p.getOffer() * p.getQuantity();
            } else {
                total += p.getPrice() * p.getQuantity();
            }
        }

        tvTotal.setText(String.format(getString(R.string.price_format), total));

    }

    @SuppressLint("StaticFieldLeak")
    private class ProductsLoader extends AsyncTaskLoader<List<Product>> {

        private final ProgressDialog progressDialog =
                new ProgressDialog(CartActivity.this);

        ProductsLoader(Context context) {
            super(context);
        }

        @Override
        protected void onStartLoading() {
            progressDialog.setMessage(getString(R.string.please_wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
            forceLoad();
        }

        @Override
        public List<Product> loadInBackground() {
            Log.d(TAG, "loadInBackground");
            return dbHelper.getProducts();
        }

        @Override
        public void deliverResult(List<Product> data) {
            Log.d(TAG, "deliverResult");
            progressDialog.dismiss();
            products = data;
            adapter = new Adapter();
            recyclerView.setAdapter(adapter);
            computeTotal();

            if (products.isEmpty()) {
                Toast.makeText(CartActivity.this, "لا يوجد عناصر في السلة", Toast.LENGTH_SHORT).show();
                bSubmit.setEnabled(false);
            }
        }
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.VH> {

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R.layout.item_cart, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final VH holder, int position) {
            final Product product = products.get(position);

            Picasso.with(CartActivity.this)
                    .load(product.getImageUrl().replaceFirst("http://fahdaldobian.com/store_org/image/", ""))
                    .into(holder.ivImage);

            Log.wtf(TAG, product.getImageUrl().replaceFirst("http://fahdaldobian.com/store_org/image/", "") + " IMG");
            holder.tvName.setText(product.getName());
            holder.npQuantity.setValue(product.getQuantity());
            double price = product.getOffer() > 0 ? product.getOffer() : product.getPrice();
            holder.tvPrice.setText(String.format(getString(R.string.cart_price_format),
                    price));

            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                    builder.setMessage("سيتم حذف المنتج من السلة");
                    builder.setCancelable(true);
                    builder.setPositiveButton("تاكيد", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            dbHelper.deleteProduct(product.getId());
                            products.remove(holder.getAdapterPosition());
                            adapter.notifyItemRemoved(holder.getAdapterPosition());
                            computeTotal();
                            Toast.makeText(CartActivity.this, "تم حذف المنتج", Toast.LENGTH_SHORT).show();
                            if (products.size() == 0) {
                                finish();
                            }
                        }
                    });
                    builder.setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return products.size();
        }

        class VH extends RecyclerView.ViewHolder {
            private TextView tvName;
            private TextView tvPrice;
            private NumberPicker npQuantity;
            private ImageView ivDelete;
            private ImageView ivImage;

            VH(View v) {
                super(v);
                tvName = v.findViewById(R.id.tvName);
                tvPrice = v.findViewById(R.id.tvPrice);
                npQuantity = v.findViewById(R.id.npQuantity);
                ivDelete = v.findViewById(R.id.ivDelete);
                ivImage = v.findViewById(R.id.ivImage);
            }
        }

    }


    @Override
    public void onBackPressed() {
        dbHelper.deleteAllProducts();
        super.onBackPressed();
    }

    private void openWhatsApp() {

        String api = "https://api.whatsapp.com/send";


        String text = "";

        String contact = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("phone", "");

        Log.d(TAG, text);

        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putString("order", text)
                .apply();

        String url = String.format("%s?phone=%s&text=%s",
                api,
                contact,
                text);

        PackageManager packageManager = getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);

        try {
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                startActivity(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

package com.aldkhel.aldkhel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aldkhel.aldkhel.adapters.ProductsAdapter;
import com.aldkhel.aldkhel.models.Category;
import com.aldkhel.aldkhel.models.Product;
import com.aldkhel.aldkhel.utils.Consts;
import com.aldkhel.aldkhel.utils.DbHelper;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.squareup.picasso.Picasso;
import com.travijuu.numberpicker.library.Enums.ActionEnum;
import com.travijuu.numberpicker.library.Interface.ValueChangedListener;
import com.travijuu.numberpicker.library.NumberPicker;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProductDetailsActivity extends AppCompatActivity {

    private static final String TAG = "ProductDetailsActivity";

    private RecyclerView recyclerView;
    private DbHelper dbHelper;
    private boolean available = true;

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
        setContentView(R.layout.activity_product_details);

        dbHelper = new DbHelper(this);

        final TextView tvName = findViewById(R.id.tvName);
        final ImageView ivImage = findViewById(R.id.ivImage);
        final TextView tvPrice = findViewById(R.id.tvPrice);
        final TextView tvOffer = findViewById(R.id.tvOffer);
        final TextView tvType = findViewById(R.id.tvType);
        final TextView tvSeenCount = findViewById(R.id.tvSeenCount);
        final TextView tvStatus = findViewById(R.id.tvStatus);
        final TextView tvPriceTotal = findViewById(R.id.tvPriceTotal);
        final TextView tvExtra = findViewById(R.id.tvExtra);
        final NumberPicker npQuantity = findViewById(R.id.npQuantity);
        final Button bAdd = findViewById(R.id.bAdd);
        final TextView tvDetails = findViewById(R.id.tvDetails);
        TextView tvAvailable = findViewById(R.id.tvAvailable);
        RotateAnimation rotate = (RotateAnimation) AnimationUtils.loadAnimation(this, R.anim.rotate_animation);
        tvAvailable.setAnimation(rotate);

        Intent intent = getIntent();

        final Product product = intent.getParcelableExtra("product");

        try {
            available = product.getDateAvailable().before(new Date());
            Log.wtf(TAG, available + " True");
            if (!available) {
                tvAvailable.setVisibility(View.VISIBLE);
            } else {
                tvAvailable.setVisibility(View.GONE);
            }
        } catch (ParseException e) {
            available = false;
            tvAvailable.setVisibility(View.VISIBLE);
        }

//        WebView webView = findViewById(R.id.webView);
//        webView.loadData(product.getDetails(), "text/html", "UTF-8");

        tvName.setText(product.getName());
        tvPrice.setText(String.format(getString(R.string.price_format), product.getPrice()));
        if (product.getOffer() > 0) {
            tvOffer.setVisibility(View.VISIBLE);
            tvPrice.setPaintFlags(tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            tvOffer.setVisibility(View.GONE);
        }

        Picasso.with(this)
                .load(product.getImageUrl())
                .error(R.drawable.logo)
                .fit()
                .into(ivImage);

        tvSeenCount.setText(String.format(getString(R.string.seen_count_format), product.getViewed()));
        tvStatus.setText(String.format(getString(R.string.status_format), "متوفر"));
        tvPriceTotal.setText(String.format(getString(R.string.total_price_format),
                (product.getPrice()*npQuantity.getValue())));

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            tvDetails.setText(Html.fromHtml(product.getDetails(), Html.FROM_HTML_MODE_LEGACY));
        } else {
            tvDetails.setText(Html.fromHtml(product.getDetails()));
        }

//        tvType.setText(String.format(getString(R.string.for)));
        recyclerView = findViewById(R.id.recycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        getOtherProducts();

        npQuantity.setValueChangedListener(new ValueChangedListener() {
            @Override
            public void valueChanged(int value, ActionEnum action) {
                tvPriceTotal.setText(String.format(getString(R.string.total_price_format),
                        (product.getPrice()*value)));
            }
        });

        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!available) {
                    Toast.makeText(ProductDetailsActivity.this, "هذا المنتج غير متاح حاليا", Toast.LENGTH_SHORT).show();
                    return;
                }

                product.setQuantity(npQuantity.getValue());
                dbHelper.addProduct(product);
                Toast.makeText(ProductDetailsActivity.this, "تمت اضافة المنتج الي السلة", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void getOtherProducts() {

        final List<Product> products = new ArrayList<>();

        final Category category = getIntent().getParcelableExtra("category");

        AndroidNetworking.get(Consts.API_URL + "show/products_new.php?category_id=" + category.getId())
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        try {

                            for (int i=0;i<response.length();i++) {
                                products.add(Product.fromJson(response.getJSONObject(i)));
                            }

                            ProductsAdapter adapter = new ProductsAdapter(ProductDetailsActivity.this, products);
                            adapter.setCallback(new ProductsAdapter.ProductCallback() {
                                @Override
                                public void onProductSelected(int position) {
                                    Intent intent = new Intent(ProductDetailsActivity.this, ProductDetailsActivity.class);
                                    intent.putExtra("product", products.get(position));
                                    intent.putExtra("category", category);
                                    startActivity(intent);
                                }
                            });
                            recyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        error.printStackTrace();
                        Toast.makeText(ProductDetailsActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                    }
                });

    }

}
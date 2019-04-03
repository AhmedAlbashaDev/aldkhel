package com.aldkhel.aldkhel;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aldkhel.aldkhel.models.Product;
import com.aldkhel.aldkhel.utils.Consts;
import com.aldkhel.aldkhel.utils.DbHelper;
import com.squareup.picasso.Picasso;
import com.travijuu.numberpicker.library.Enums.ActionEnum;
import com.travijuu.numberpicker.library.Interface.ValueChangedListener;
import com.travijuu.numberpicker.library.NumberPicker;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProductDetailsActivity extends AppCompatActivity {

    private static final String TAG = "ProductDetailsActivity";

    private DbHelper dbHelper;

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

        final Product product = getIntent().getParcelableExtra("product");

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
        RecyclerView recyclerView = findViewById(R.id.recycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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
                product.setQuantity(npQuantity.getValue());
                dbHelper.addProduct(product);
                Toast.makeText(ProductDetailsActivity.this, "تمت اضافة المنتج الي السلة", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

}
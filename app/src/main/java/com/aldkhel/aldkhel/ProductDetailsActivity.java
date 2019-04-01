package com.aldkhel.aldkhel;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aldkhel.aldkhel.utils.Consts;
import com.travijuu.numberpicker.library.NumberPicker;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProductDetailsActivity extends AppCompatActivity {

    private static final String TAG = "ProductDetailsActivity";

    private TextView tvName;
    private ImageView ivImage;
    private TextView tvPrice;
    private TextView tvOffer;
    private TextView tvType;
    private TextView tvSeenCount;
    private TextView tvStatus;
    private TextView tvPriceTotal;
    private TextView tvExtra;
    private NumberPicker npQuantity;
    private Button bAdd;
    private RecyclerView recyclerView;
    private TextView tvDetails;

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

        tvName = findViewById(R.id.tvName);
        ivImage = findViewById(R.id.ivImage);
        tvPrice = findViewById(R.id.tvPrice);
        tvOffer = findViewById(R.id.tvOffer);
        tvType = findViewById(R.id.tvType);
        tvSeenCount = findViewById(R.id.tvSeenCount);
        tvStatus = findViewById(R.id.tvStatus);
        tvPriceTotal = findViewById(R.id.tvPriceTotal);
        tvExtra = findViewById(R.id.tvExtra);
        npQuantity = findViewById(R.id.npQuantity);
        bAdd = findViewById(R.id.bAdd);
        tvDetails = findViewById(R.id.tvDetails);

        recyclerView = findViewById(R.id.recycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

}

package com.aldkhel.aldkhel;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aldkhel.aldkhel.utils.Consts;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    private RecyclerView recyclerView;
    private SliderLayout slider;
    private SliderLayout sliderFooter;

    private List<String> images = new ArrayList<>();

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
        setContentView(R.layout.activity_home);

        findViewById(R.id.ivCart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        recyclerView = findViewById(R.id.recycle);
        recyclerView.setHasFixedSize(true);
        //new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        slider = findViewById(R.id.slider);
        slider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        slider.setCustomAnimation(new DescriptionAnimation());
        slider.setDuration(8000);


        TextSliderView textSliderView = new TextSliderView(HomeActivity.this);
        textSliderView
                .description("")
                .image(R.drawable.logo)
                .setScaleType(BaseSliderView.ScaleType.Fit);
        slider.addSlider(textSliderView);

        sliderFooter = findViewById(R.id.sliderFooter);
        sliderFooter.setPresetTransformer(SliderLayout.Transformer.Accordion);
        sliderFooter.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderFooter.setCustomAnimation(new DescriptionAnimation());
        sliderFooter.setDuration(8000);

    }

    class Adapter extends RecyclerView.Adapter<Adapter.VH> {

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = getLayoutInflater().inflate(R.layout.item_product_home, viewGroup, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH vh, int i) {
            Picasso.with(HomeActivity.this)
                    .load(images.get(i))
                    .into(vh.imageView);
        }

        @Override
        public int getItemCount() {
            return 0;
        }

        class VH extends RecyclerView.ViewHolder {

            private final ImageView imageView;

            VH(View v) {
                super(v);
                imageView = v.findViewById(R.id.ivImage);
            }

        }

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("هل تريد الخروج من التطبيق");
        builder.setPositiveButton("تاكيد", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(true);
        builder.show();
    }

}

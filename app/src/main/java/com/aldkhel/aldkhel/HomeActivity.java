package com.aldkhel.aldkhel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aldkhel.aldkhel.adapters.ProductsAdapter;
import com.aldkhel.aldkhel.models.Category;
import com.aldkhel.aldkhel.models.Product;
import com.aldkhel.aldkhel.utils.Consts;
import com.aldkhel.aldkhel.utils.SpacesItemDecoration;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    private RecyclerView recyclerView;
    private RecyclerView recyclerViewFooter;
    private SliderLayout slider;
    private SliderLayout sliderFooter;
    private TabLayout tabLayout;

    private List<Product> products;
    private List<Category> categories;
    private List<String> categoriesTitle;

    private List<Product> productsExtra;

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
                startActivity(new Intent(HomeActivity.this, ProductDetailsActivity.class));
            }
        });

        findViewById(R.id.ivMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, MenuActivity.class));
            }
        });

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                Log.d(TAG, "Tab " + tab.getPosition() + ": " + tab.getText());
                //category = tab.getText().toString();
//                category = category.equals("الكل") ? "" : category;
//                getProducts();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        recyclerView = findViewById(R.id.recycle);
        recyclerView.setHasFixedSize(true);
        //new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewFooter = findViewById(R.id.recycleFooter);
        recyclerViewFooter.setHasFixedSize(true);
        recyclerViewFooter.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerViewFooter.addItemDecoration(new SpacesItemDecoration(3));

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

        sliderFooter.addSlider(textSliderView);
    }

    private void getCategories() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.dismiss();

        categories = new ArrayList<>();
        categoriesTitle = new ArrayList<>();
        tabLayout.removeAllTabs();

        AndroidNetworking.get(Consts.API_URL + "show/categories.php")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        dialog.dismiss();
                        Log.d(TAG, response.toString());

                        try {

                            for (int i=0;i<response.length();i++) {
                                categories.add(Category.fromJson(response.getJSONObject(i)));
                            }

                            for (int i=categories.size()-1;i >= 0; i--) {
                                tabLayout.addTab(tabLayout.newTab().setText(categories.get(i).getName()));
                            }

                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    int index = categoriesTitle.size()-1;

                                    tabLayout.getTabAt(index).select();
                                    int right = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(index).getRight();
                                    tabLayout.scrollTo(right,0);
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        dialog.dismiss();
                        error.printStackTrace();
                        Toast.makeText(HomeActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void getProductsFooter() {

        productsExtra = new ArrayList<>();

        AndroidNetworking.get(Consts.API_URL + "show/products.php")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        try {

                            for (int i=0;i<response.length();i++) {
                                productsExtra.add(Product.fromJson(response.getJSONObject(i)));
                            }

                            recyclerViewFooter.setAdapter(new ProductsAdapter(HomeActivity.this, productsExtra));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        error.printStackTrace();
                        Toast.makeText(HomeActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                    }
                });

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

                            for (int i=0;i<response.length();i++) {
                                products.add(Product.fromJson(response.getJSONObject(i)));
                            }

                            recyclerView.setAdapter(new ProductsAdapter(HomeActivity.this, products));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        dialog.dismiss();
                        error.printStackTrace();
                        Toast.makeText(HomeActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                    }
                });

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

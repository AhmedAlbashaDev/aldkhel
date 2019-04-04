package com.aldkhel.aldkhel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
    private ImageView imageView;

    private ProductsAdapter newProductsAdapter;
    private ProductsAdapter mostSoldProductsAdapter;

    private List<Product> products;
    private List<Category> categories;

    private List<Product> productsExtra;

    private Category category;

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
                startActivity(new Intent(HomeActivity.this, CartActivity.class));
            }
        });

        findViewById(R.id.ivMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, MenuActivity.class));
            }
        });

        findViewById(R.id.bSoldMore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, ProductsActivity.class);
                i.putExtra("url", Consts.API_URL + "show/products_sold.php?category_id=" + category.getId());
                i.putExtra("category", category);
                startActivity(i);
            }
        });

        findViewById(R.id.bNewsMore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, ProductsActivity.class);
                i.putExtra("url", Consts.API_URL + "show/products_new.php?category_id=" + category.getId());
                i.putExtra("category", category);
                startActivity(i);
            }
        });

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                Log.d(TAG, "Tab " + tab.getPosition() + ": " + tab.getText());
                category = categories.get(tab.getPosition());
                Log.d(TAG, "Tab Id " + category.getId());
                getProducts();
                getProductsFooter();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        imageView = findViewById(R.id.ivImage);

        products = new ArrayList<>();

        recyclerView = findViewById(R.id.recycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        mostSoldProductsAdapter = new ProductsAdapter(this, products);
        mostSoldProductsAdapter.setCallback(new ProductsAdapter.ProductCallback() {
            @Override
            public void onProductSelected(int position) {
                Intent intent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
                intent.putExtra("product", products.get(position));
                startActivity(intent);
            }
        });

        productsExtra = new ArrayList<>();

        recyclerViewFooter = findViewById(R.id.recycleFooter);
        recyclerViewFooter.setHasFixedSize(true);
        recyclerViewFooter.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewFooter.addItemDecoration(new SpacesItemDecoration(3));

        newProductsAdapter = new ProductsAdapter(this, productsExtra);
        newProductsAdapter.setCallback(new ProductsAdapter.ProductCallback() {
            @Override
            public void onProductSelected(int position) {
                Intent intent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
                intent.putExtra("product", productsExtra.get(position));
                startActivity(intent);
            }
        });

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

        int[] images = {R.drawable.s1, R.drawable.s2, R.drawable.s3};

        for (int image : images) {
            TextSliderView tsv = new TextSliderView(HomeActivity.this);
            textSliderView
                    .description("")
                    .image(image)
                    .setScaleType(BaseSliderView.ScaleType.Fit);
            sliderFooter.addSlider(tsv);
        }

        final SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filterProducts(s);
                return true;
            }
        });

        getCategories();
        getBanners();
    }

    private void getCategories() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.dismiss();

        categories = new ArrayList<>();
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

//                            for (int i=categories.size()-1;i >= 0; i--) {
//                                tabLayout.addTab(tabLayout.newTab().setText(categories.get(i).getName()));
//                            }

                            for (int i=0;i< categories.size(); i++) {
                                tabLayout.addTab(tabLayout.newTab().setText(categories.get(i).getName()));
                            }

//                            new Handler().post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    int index = categories.size()-1;
//                                    tabLayout.getTabAt(index).select();
//                                    int right = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(index).getRight();
//                                    tabLayout.scrollTo(right,0);
//                                }
//                            });

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


        AndroidNetworking.get(Consts.API_URL + "show/products_new.php?category_id=" + category.getId())
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        try {

                            productsExtra.clear();

                            for (int i=0;i<response.length();i++) {
                                productsExtra.add(Product.fromJson(response.getJSONObject(i)));
                            }

                            recyclerViewFooter.setAdapter(newProductsAdapter);
                            newProductsAdapter.notifyDataSetChanged();

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

    private void getBanners() {

//        final List<String> images = new ArrayList<>();

        AndroidNetworking.get(Consts.API_URL + "show/banners.php")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        try {

                            TextSliderView textSliderView;

                            for (int i=0;i<response.length();i++) {
//                                images.add(response.getJSONObject(i).getString("image"));
                                textSliderView = new TextSliderView(HomeActivity.this);
                                textSliderView
                                        .description("")
                                        .image(Consts.BASE_IMAGE + response.getJSONObject(i).getString("image"))
                                        .setScaleType(BaseSliderView.ScaleType.Fit);
                                slider.addSlider(textSliderView);
                            }

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

//        final ProgressDialog dialog = new ProgressDialog(this);
//        dialog.setMessage(getString(R.string.please_wait));
//        dialog.setCancelable(false);
//        dialog.dismiss();


        AndroidNetworking.get(Consts.API_URL + "show/products_sold.php?category_id=" + category.getId())
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
//                        dialog.dismiss();
                        Log.d(TAG, response.toString());

                        try {

                            products.clear();

                            for (int i=0;i<response.length();i++) {
                                products.add(Product.fromJson(response.getJSONObject(i)));
                            }

                            recyclerView.setAdapter(mostSoldProductsAdapter);
                            mostSoldProductsAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onError(ANError error) {
//                        dialog.dismiss();
                        error.printStackTrace();
                        Toast.makeText(HomeActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void filterProducts(String search) {

        if (search.length() == 0) {
            recyclerView.setAdapter(mostSoldProductsAdapter);
            recyclerViewFooter.setAdapter(newProductsAdapter);
            return;
        }

        List<Product> products1 = new ArrayList<>();
        List<Product> products2 = new ArrayList<>();

        for (Product product : products) {
            if (product.getName().contains(search)) {
                products1.add(product);
            }
        }

        for (Product product : productsExtra) {
            if (product.getName().contains(search)) {
                products2.add(product);
            }
        }

        recyclerView.setAdapter(new ProductsAdapter(this, products1));
        recyclerViewFooter.setAdapter(new ProductsAdapter(this, products2));

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

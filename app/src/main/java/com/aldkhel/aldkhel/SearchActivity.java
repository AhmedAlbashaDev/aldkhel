package com.aldkhel.aldkhel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
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

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";

    private RecyclerView recyclerView;
    private ProductsAdapter adapter;

    private List<Product> products;
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
        setContentView(R.layout.activity_search);

        products = new ArrayList<>();

        recyclerView = findViewById(R.id.recycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addItemDecoration(new SpacesItemDecoration(3));
//        recyclerView.stopScroll();
        recyclerView.setNestedScrollingEnabled(false);

        category = getIntent().getParcelableExtra("category");

        category.setName("");

        adapter = new ProductsAdapter(this, products);
        adapter.setCallback(new ProductsAdapter.ProductCallback() {
            @Override
            public void onProductSelected(int position) {
                Intent intent = new Intent(SearchActivity.this, ProductDetailsActivity.class);
                intent.putExtra("product", products.get(position));
                intent.putExtra("category", category);
                startActivity(intent);
            }
        });


        final EditText etSearch = findViewById(R.id.etSearch);
        etSearch.setText(getIntent().getStringExtra("search"));

        doSearch(getIntent().getStringExtra("search"));

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                doSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void doSearch(String search) {


        AndroidNetworking.get(Consts.API_URL + "show/search_products.php?search=" + URLEncoder.encode(search))
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



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        error.printStackTrace();
                        Toast.makeText(SearchActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                    }
                });

    }

}

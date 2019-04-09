package com.aldkhel.aldkhel;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aldkhel.aldkhel.models.Category;
import com.aldkhel.aldkhel.utils.Consts;

import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CategoriesActivity extends AppCompatActivity {

    private static final String TAG = "CategoriesActivity";
    List<Category> categoryList;

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
        setContentView(R.layout.activity_categories);

        RecyclerView recyclerView = findViewById(R.id.recycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        categoryList = getIntent().getParcelableArrayListExtra("categories");

        recyclerView.setAdapter(new Adapter());
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.VH> {

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R.layout.item_category, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final VH holder, int position) {
//            Picasso.with(CategoriesActivity.this)
//                    .load(categoryList.get(position).getImage())
//                    .into(holder.ivImage);
        }

        @Override
        public int getItemCount() {
            return categoryList.size();
        }

        class VH extends RecyclerView.ViewHolder {
            private ImageView ivImage;

            VH(View v) {
                super(v);
                ivImage = v.findViewById(R.id.ivImage);
            }
        }

    }

}

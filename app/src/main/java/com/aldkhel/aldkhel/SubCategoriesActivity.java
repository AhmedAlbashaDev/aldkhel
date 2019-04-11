package com.aldkhel.aldkhel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aldkhel.aldkhel.models.Category;
import com.aldkhel.aldkhel.utils.Consts;
import com.aldkhel.aldkhel.utils.SpacesItemDecoration;
import com.squareup.picasso.Picasso;

import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SubCategoriesActivity extends AppCompatActivity {

    private static final String TAG = "CategoriesActivity";
    private List<Category> categoryList;
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
        setContentView(R.layout.activity_sub_categories);

        RecyclerView recyclerView = findViewById(R.id.recycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new SpacesItemDecoration(5));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        categoryList = getIntent().getParcelableArrayListExtra("categories");
        category = getIntent().getParcelableExtra("category");

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
            Picasso.with(SubCategoriesActivity.this)
                    .load(categoryList.get(position).getImage())
                    .into(holder.ivImage);

            holder.tvName.setText(categoryList.get(position).getName());

            holder.v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(SubCategoriesActivity.this, ProductsActivity.class);
                    i.putExtra("url", Consts.API_URL + "show/products_new.php?category_id=" + categoryList.get(holder.getAdapterPosition()).getId());
                    i.putExtra("category", categoryList.get(holder.getAdapterPosition()));
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return categoryList.size();
        }

        class VH extends RecyclerView.ViewHolder {
            private ImageView ivImage;
            private TextView tvName;
            private View v;
            VH(View v) {
                super(v);
                this.v = v;
                ivImage = v.findViewById(R.id.ivImage);
                tvName = v.findViewById(R.id.tvName);
            }
        }

    }

}

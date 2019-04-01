package com.aldkhel.aldkhel.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.aldkhel.aldkhel.R;
import com.aldkhel.aldkhel.models.Product;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.VH> {

    private Context context;
    private List<Product> products;

    public ProductsAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_product, viewGroup, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        final Product product = products.get(position);
        Picasso.with(context)
                .load(product.getImageUrl())
                .into(holder.imageView);

        holder.tvName.setText(product.getName());

        holder.tvPrice.setText(String.format(context.getString(R.string.price_format), product.getPrice()));
        holder.tvOffer.setText(String.format(context.getString(R.string.price_format), product.getOffer()));

        if (product.getOffer() > 0) {
            holder.tvOffer.setVisibility(View.VISIBLE);
            holder.tvPrice.setPaintFlags(holder.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.tvOffer.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class VH extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final TextView tvName;
        private final TextView tvPrice;
        private final TextView tvOffer;
        private final TextView tvAvailable;

        VH(View v) {
            super(v);
            imageView = v.findViewById(R.id.ivImage);
            tvName = v.findViewById(R.id.tvName);
            tvPrice = v.findViewById(R.id.tvPrice);
            tvOffer = v.findViewById(R.id.tvOffer);
            tvAvailable = v.findViewById(R.id.tvAvailable);
            RotateAnimation rotate = (RotateAnimation) AnimationUtils.loadAnimation(context, R.anim.rotate_animation);
            tvAvailable.setAnimation(rotate);
        }

    }

}

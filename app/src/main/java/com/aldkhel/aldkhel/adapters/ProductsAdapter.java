package com.aldkhel.aldkhel.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aldkhel.aldkhel.R;
import com.aldkhel.aldkhel.models.Product;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.VH> {

    private Context context;
    private List<Product> products;
    private ProductCallback callback;

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
    public void onBindViewHolder(@NonNull final VH holder, int position) {
        final Product product = products.get(position);
        Picasso.with(context)
                .load(product.getImageUrl())
                .error(R.drawable.logo)
                .fit()
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

        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onProductSelected(holder.getAdapterPosition());
            }
        });

        if (product.getStockStatus().equals("غير متوفر")) {
            holder.tvAvailable.setVisibility(View.VISIBLE);
        } else {
            holder.tvAvailable.setVisibility(View.INVISIBLE);
        }

    }

    public void setCallback(ProductCallback callback) {
        this.callback = callback;
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
        private final View v;

        VH(View v) {
            super(v);
            this.v = v;
            imageView = v.findViewById(R.id.ivImage);
            tvName = v.findViewById(R.id.tvName);
            tvPrice = v.findViewById(R.id.tvPrice);
            tvOffer = v.findViewById(R.id.tvOffer);
            tvAvailable = v.findViewById(R.id.tvAvailable);
//            RotateAnimation rotate = (RotateAnimation) AnimationUtils.loadAnimation(context, R.anim.rotate_animation);
//            tvAvailable.setAnimation(rotate);
        }

    }

    public interface ProductCallback {
        void onProductSelected(int position);
    }

}

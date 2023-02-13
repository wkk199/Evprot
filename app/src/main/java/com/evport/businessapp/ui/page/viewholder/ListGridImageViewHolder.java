package com.evport.businessapp.ui.page.viewholder;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.evport.businessapp.R;

public class ListGridImageViewHolder extends RecyclerView.ViewHolder {
    public ImageView img_uri;
    public ListGridImageViewHolder(@NonNull View itemView) {
        super(itemView);
        img_uri=itemView.findViewById(R.id.img_uri);
    }
}

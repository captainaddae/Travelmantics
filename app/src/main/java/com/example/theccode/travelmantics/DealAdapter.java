package com.example.theccode.travelmantics;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.DealViewHolder> {

    ArrayList<TravelDeal> deals;
    private FirebaseDatabase m_firebase_database;
    private DatabaseReference m_database_ref;
    private ChildEventListener m_child_listener;
    ImageView image_view;

    public DealAdapter(){
       // FirebaseUtil.open_fb_ref("traveldeals", this);
        m_firebase_database = FirebaseUtil.m_firebase_database;
        m_database_ref = FirebaseUtil.m_database_ref;
        deals = FirebaseUtil.m_deals;
        m_child_listener = new ChildEventListener() {
            @Override
            public void onChildAdded( DataSnapshot dataSnapshot,  String s) {
                TravelDeal td = dataSnapshot.getValue(TravelDeal.class);
                Log.d("Deal:", td.getTitle());
                td.setId(dataSnapshot.getKey());
                deals.add(td);
                notifyItemInserted(deals.size()-1);
            }

            @Override
            public void onChildChanged( DataSnapshot dataSnapshot,  String s) {

            }

            @Override
            public void onChildRemoved( DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot,  String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        m_database_ref.addChildEventListener(m_child_listener);
    }


    @Override
    public DealViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View item_view = LayoutInflater.from(context)
                .inflate(R.layout.rv_row, parent, false);
        return new DealViewHolder(item_view);
    }

    @Override
    public void onBindViewHolder( DealViewHolder holder, int position) {
        TravelDeal deal = deals.get(position);
        holder.bind(deal);
    }

    @Override
    public int getItemCount() {
        return deals.size();
    }

    public class DealViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_title;
        TextView tv_price;
        TextView tv_description;

        public DealViewHolder( View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_price = (TextView) itemView.findViewById(R.id.tv_price);
            tv_description = (TextView) itemView.findViewById(R.id.tv_description);
            image_view = (ImageView) itemView.findViewById(R.id.deal_image);
            itemView.setOnClickListener(this);
        }

        public  void bind(TravelDeal deal){
            tv_title.setText(deal.getTitle());
            tv_price.setText(deal.getPrice());
            tv_description.setText(deal.getDescription());
            show_menu(deal.getImg_url());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Log.d("Clicked:", String.valueOf(position));
            TravelDeal selected_deal = deals.get(position);
            Intent intent = new Intent(v.getContext(), DealActivity.class);
            intent.putExtra("Deal", selected_deal);
            v.getContext().startActivity(intent);
        }
    }

    private void show_menu(String url){
        if (url != null && url.isEmpty() == false){
            Picasso.get()
                    .load(url)
                    .placeholder(R.drawable.common_google_signin_btn_icon_dark)
                    .centerCrop()
                    .resize(160, 160)
                    .into(image_view);
        }
    }
}

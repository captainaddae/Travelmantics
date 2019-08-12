package com.example.theccode.travelmantics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    ArrayList<TravelDeal> deals;
    private FirebaseDatabase m_firebase_database;
    private DatabaseReference m_database_ref;
    private ChildEventListener m_child_listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_activity_menu,menu);

        MenuItem menu_item = menu.findItem(R.id.insert_menu);
        if (FirebaseUtil.is_admin == true){
            menu_item.setVisible(true);
        }
        else {
            menu_item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.insert_menu:
                Intent intent = new Intent(this, DealActivity.class);
                startActivity(intent);
                return true;
            case R.id.sign_out:
                logout();
                FirebaseUtil.detach_auth_listener();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected  void onPause(){
        super.onPause();
        FirebaseUtil.detach_auth_listener();
    }

    @Override
    protected void onResume(){
        super.onResume();
        FirebaseUtil.open_fb_ref("traveldeals", this);

        RecyclerView rv_deals = (RecyclerView) findViewById(R.id.rv_deals);
        final DealAdapter adapter = new DealAdapter();
        rv_deals.setAdapter(adapter);

        LinearLayoutManager deals_layout_manager =
                new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv_deals.setLayoutManager(deals_layout_manager);
        FirebaseUtil.attach_auth_listener();
    }

    private  void logout(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        FirebaseUtil.attach_auth_listener();
                    }
                });
    }

    public void show_menu(){
        invalidateOptionsMenu();
    }
}

package com.example.theccode.travelmantics;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DealActivity extends AppCompatActivity {
    private FirebaseDatabase m_firebase_database;
    private DatabaseReference m_database_reference;
    private static final int PICTURE_RESULT = 42;
    EditText txt_title;
    EditText txt_price;
    EditText txt_description;
    TravelDeal deal;
    Button btn_upload;
    ImageView image_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);

        //getting the firebase database

        m_firebase_database = FirebaseUtil.m_firebase_database;
        m_database_reference = FirebaseUtil.m_database_ref;

        txt_title = (EditText) findViewById(R.id.txt_title);
        txt_price = (EditText) findViewById(R.id.txt_price);
        txt_description = (EditText) findViewById(R.id.txt_description);

        image_view = (ImageView) findViewById(R.id.image);

        Intent intent = getIntent();
        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("Deal");
        if (deal == null){
            deal = new TravelDeal();
        }
        this.deal = deal;
        txt_title.setText(deal.getTitle());
        txt_price.setText(deal.getPrice());
        txt_description.setText(deal.getDescription());
        show_image(deal.getImg_url());
        btn_upload = (Button) findViewById(R.id.upload);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent.createChooser(intent, "Insert Picture"), PICTURE_RESULT);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sav_menu:
                save_deal();
                Toast.makeText(this, "Deal Saved Successfully!", Toast.LENGTH_LONG).show();
                clean();
                back_to_list();
                return true;
            case R.id.delete_menu:
                delete_deal();
                Toast.makeText(this, "Deal removed successfully!", Toast.LENGTH_LONG).show();
                back_to_list();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void save_deal() {
        deal.setTitle(txt_title.getText().toString());
        deal.setPrice(txt_price.getText().toString());
        deal.setDescription(txt_description.getText().toString());

        if(deal.getId() == null){
            m_database_reference.push().setValue(deal);
        }
        else{
            m_database_reference.child(deal.getId()).setValue(deal);
        }
    }

    private  void delete_deal() {
        if (deal == null) {
            Toast.makeText(this, "Please save the deal before you exit!", Toast.LENGTH_LONG).show();
        } else {
            m_database_reference.child(deal.getId()).removeValue();
        }
    }

    private  void back_to_list(){
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }

    private void clean() {
        txt_title.setText("");
        txt_price.setText("");
        txt_description.setText("");
        txt_title.requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);

        if (FirebaseUtil.is_admin){
           menu.findItem(R.id.sav_menu).setVisible(true);
           menu.findItem(R.id.delete_menu).setVisible(true);
            enable_text_edit(true);
        }
        else {
            menu.findItem(R.id.sav_menu).setVisible(false);
            menu.findItem(R.id.delete_menu).setVisible(false);
            enable_text_edit(false);
        }
        return true;
    }

    private  void enable_text_edit(boolean is_enabled){
            txt_title.setEnabled(is_enabled);
            txt_description.setEnabled(is_enabled);
            txt_price.setEnabled(is_enabled);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK){
             Uri img_uri = data.getData();

           final StorageReference ref = FirebaseUtil.storage_reference.child(img_uri.getLastPathSegment());
            ref.putFile(img_uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri>  then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String url = downloadUri.toString();
                        deal.setImg_url(url);
                        //String picture_name;
                        show_image(url);
                    }
                }
            });
            /*
            *
            * ref.putFile(img_uri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String url = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                    deal.setImg_url(url);
                    show_image(url);
                }
            });
            *
            * */
        }
    }

    private void show_image(String url){
        if (url != null && url.isEmpty() == false){
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.get()
                    .load(url)
                    .resize(width, width * 2/3)
                    .placeholder(R.drawable.common_google_signin_btn_icon_dark)
                    .centerCrop()
                    .into(image_view);
        }
    }
}

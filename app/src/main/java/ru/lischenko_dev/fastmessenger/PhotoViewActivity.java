package ru.lischenko_dev.fastmessenger;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;
import com.squareup.picasso.*;

public class PhotoViewActivity extends AppCompatActivity {

    private AppCompatImageView image;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        image = new AppCompatImageView(this);
        setContentView(image, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imagePath = getIntent().getExtras().getString("image");

        try {
            Picasso.with(this).load(imagePath).placeholder(new ColorDrawable(Color.LTGRAY)).error(new ColorDrawable(Color.RED)).centerCrop().into(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void update() {
        try {
            Picasso.with(this).load(imagePath).placeholder(new ColorDrawable(Color.LTGRAY)).error(new ColorDrawable(Color.RED)).centerCrop().into(image);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(PhotoViewActivity.this, "Refreshed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(getString(R.string.refresh));
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        item.setIcon(getResources().getDrawable(R.drawable.ic_refresh));
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                update();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}

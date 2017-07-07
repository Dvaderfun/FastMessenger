package ru.lischenko_dev.fastmessenger;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import ru.lischenko_dev.fastmessenger.adapter.MaterialsAdapter;
import ru.lischenko_dev.fastmessenger.fragment.material.FragmentAudio;
import ru.lischenko_dev.fastmessenger.fragment.material.FragmentDocuments;
import ru.lischenko_dev.fastmessenger.fragment.material.FragmentPhoto;
import ru.lischenko_dev.fastmessenger.fragment.material.FragmentVideo;


public class MaterialsActivity extends AppCompatActivity {

    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    long uid, cid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_materials);


        uid = getIntent().getExtras().getLong("uid");
        cid = getIntent().getExtras().getLong("cid");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        MaterialsAdapter adapter = new MaterialsAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentPhoto(), getString(R.string.materials_photo));
        adapter.addFragment(new FragmentAudio(), getString(R.string.materials_audio));
        adapter.addFragment(new FragmentVideo(), getString(R.string.materials_video));
        adapter.addFragment(new FragmentDocuments(), getString(R.string.materials_doc));
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

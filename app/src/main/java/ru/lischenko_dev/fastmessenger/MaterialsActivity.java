package ru.lischenko_dev.fastmessenger;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import ru.lischenko_dev.fastmessenger.adapter.MaterialsPagerAdapter;
import ru.lischenko_dev.fastmessenger.common.ThemeManager;


public class MaterialsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private long uid, cid;
    private ThemeManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        manager = ThemeManager.get(this);
        setTheme(manager.getCurrentTheme());
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
        MaterialsPagerAdapter adapter = new MaterialsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentMaterialsPhoto(), getString(R.string.materials_photo));
        adapter.addFragment(new FragmentMaterialsAudio(), getString(R.string.materials_audio));
        adapter.addFragment(new FragmentMaterialsVideo(), getString(R.string.materials_video));
        adapter.addFragment(new FragmentMaterialsDocuments(), getString(R.string.materials_doc));
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

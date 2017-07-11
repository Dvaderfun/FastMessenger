package ru.lischenko_dev.fastmessenger;

import android.content.*;
import android.os.*;
import android.preference.*;
import android.support.v4.app.*;
import android.view.*;
import java.lang.reflect.*;
import ru.lischenko_dev.fastmessenger.common.*;
import ru.lischenko_dev.fastmessenger.util.*;

public class SettingsActivity extends AppCompatPreferenceActivity implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

    private Preference template;
    private EditTextPreference template_edit;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ThemeManager.get(this).getCurrentActionBarTheme());
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        template = findPreference("template");
        template_edit = (EditTextPreference) template;
        if (template_edit != null)
            template.setSummary(template_edit.getText());

        template.setOnPreferenceChangeListener(this);
        (findPreference("dark_theme")).setOnPreferenceChangeListener(this);
    }

	
	
    @Override
    public boolean onPreferenceChange(android.preference.Preference p1, Object p2) {
        switch (p1.getKey()) {
            case "dark_theme":
                ThemeManager.get(getApplicationContext()).putTheme((boolean) p2);
                TaskStackBuilder.create(getApplicationContext()).addNextIntent(new Intent(getApplicationContext(), StartActivity.class)).addNextIntent(new Intent(getApplicationContext(), MainActivity.class)).addNextIntent(getIntent()).startActivities();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case "template":
                template.setSummary((String) p2);
                template_edit.setText((String) p2);
                break;
        }
        return false;
    }

    @Override
    public boolean onPreferenceClick(android.preference.Preference p1) {
        switch (p1.getKey()) {
            case "check_updates":
                if (Utils.hasConnection(getApplicationContext()))
                    OTAManager.get(getApplicationContext()).checkOTAUpdates();
                break;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}

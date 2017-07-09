package ru.lischenko_dev.fastmessenger;

import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.support.v4.app.TaskStackBuilder;
import android.view.MenuItem;

import ru.lischenko_dev.fastmessenger.common.OTAManager;
import ru.lischenko_dev.fastmessenger.common.ThemeManager;
import ru.lischenko_dev.fastmessenger.util.AppCompatPreferenceActivity;
import ru.lischenko_dev.fastmessenger.util.Utils;

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
                TaskStackBuilder.create(getApplicationContext()).addNextIntent(new Intent(getApplicationContext(), MainActivity.class)).addNextIntent(getIntent()).startActivities();
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

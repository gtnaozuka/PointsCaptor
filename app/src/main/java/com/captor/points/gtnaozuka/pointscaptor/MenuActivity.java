package com.captor.points.gtnaozuka.pointscaptor;

import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.captor.points.gtnaozuka.dialog.InfoDialog;
import com.captor.points.gtnaozuka.dialog.LanguageDialog;

public class MenuActivity extends FragmentActivity implements LanguageDialog.LanguageListener {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.info_button:
                showInfoDialog();
                return true;
            case R.id.language_button:
                showLanguageDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showInfoDialog() {
        DialogFragment dialog = new InfoDialog();
        dialog.show(getFragmentManager(), "InfoDialog");
    }

    public void showLanguageDialog() {
        DialogFragment dialog = new LanguageDialog();
        dialog.show(getFragmentManager(), "LanguageDialog");
    }

    @Override
    public void setLanguage(DialogFragment dialog, String language) {
        dialog.dismiss();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("Language", language);
        editor.commit();

        Toast toast = Toast.makeText(getApplicationContext(), R.string.reboot, Toast.LENGTH_SHORT);
        toast.show();
    }
}

package com.example.playerpad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    RelativeLayout themeSelector;
    TextView themeused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setThemeofApp();
        setActivityTheme();
        setContentView(R.layout.activity_settings2);

        themeSelector=findViewById(R.id.themeSelector);
        themeused=findViewById(R.id.themeused);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        int theme=preferences.getInt("ThemeNumber",0);
        String [] listOfThemes= getApplicationContext().getResources().getStringArray(R.array.theme_list);
        themeused.setText(listOfThemes[theme]);



        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnSecondary, typedValue, true);
        int colorOnSecondary = typedValue.data;


        TypedValue typedValue2 = new TypedValue();
        getTheme().resolveAttribute(com.google.android.material.R.attr.colorSecondary, typedValue2, true);
        int colorSecondary = typedValue2.data;



        int nightModeFlags =
                getApplicationContext().getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colorSecondary));
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colorOnSecondary));
                break;

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colorOnSecondary));
                break;
        }


        themeSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChoiceThemeDialog choiceThemeDialog=new ChoiceThemeDialog();
                choiceThemeDialog.show(getFragmentManager(),"Alert");
            }
        });


    }



    private void setActivityTheme() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "m back hh", Toast.LENGTH_SHORT).show();
    }

    private void setThemeofApp() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        int theme=preferences.getInt("ThemeNumber",0);

        switch (theme)
        {
            case 0:
                setTheme(R.style.Theme_Playerpad);
                break;

            case 1:
                setTheme(R.style.Theme_Playerpad2);
                break;

            case 2:
                setTheme(R.style.Theme_Playerpad4);
                break;


            case 3:
                setTheme(R.style.Theme_Playerpad3);
                break;

            case 4:
                setTheme(R.style.Theme_Playerpad5);

        }

        //setTheme(R.style.Theme_Playerpad4);
    }
}
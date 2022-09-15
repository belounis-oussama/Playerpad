package com.example.playerpad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setThemeofApp();
        setTheme(R.style.Theme_Playerpad4);
        setContentView(R.layout.activity_settings2);



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


    }

    private void setThemeofApp() {


    }
}
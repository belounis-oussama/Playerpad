package com.example.playerpad;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.android.material.snackbar.Snackbar;

public class ChoiceThemeDialog extends DialogFragment {

    int position=0; //default value




    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {



        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        position=preferences.getInt("ThemeNumber",0);


        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

        String [] listOfThemes= getActivity().getResources().getStringArray(R.array.theme_list);
        builder.setTitle("Select the theme you like");
        builder.setSingleChoiceItems(listOfThemes, position, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                position = i;
                //  Toast.makeText(getActivity(), String.valueOf(position), Toast.LENGTH_SHORT).show();

            }
        });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("ThemeNumber",position);
                editor.apply();

                Snackbar snackbar = Snackbar
                        .make(getActivity().getWindow().getDecorView(), "Restart the app to see changes", Snackbar.LENGTH_LONG);
                // Show
                snackbar.show();
                //   Toast.makeText(getActivity(), String.valueOf(position), Toast.LENGTH_SHORT).show();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        return builder.create();
    }
}

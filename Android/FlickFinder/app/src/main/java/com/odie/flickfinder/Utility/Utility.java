package com.odie.flickfinder.Utility;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Odie on 18/08/2018.
 */
public class Utility {

    public static HashSet<String> getSavedTitles(Context context){
        SharedPreferences preferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        HashSet<String> titles = (HashSet<String>) preferences.getStringSet("titles", new HashSet<String>());
        Log.d("UTILITYXXX", titles.size()+"");
        return titles;
    }

    public static void saveTitle(String title, Context context){
        HashSet<String> titles = new HashSet<>(getSavedTitles(context));
        titles.add(title);

        SharedPreferences preferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet("titles", titles);
        editor.commit();
    }

    public static void removeTitle(String title, Context context){
        HashSet<String> titles = new HashSet<>(getSavedTitles(context));
        titles.remove(title);

        SharedPreferences preferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet("titles", titles);
        editor.commit();
    }

    public static void showAlert(Context context, String title, String message){
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("Cool", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static void setStringsToQueryFormat(String[] titles, boolean shouldBeQueryFormat){
        for(int i = 0; i < titles.length; i++){
            if(shouldBeQueryFormat){
                titles[i] = titles[i].replaceAll(" ", "-");
            }
            else{
                titles[i] = titles[i].replaceAll("-", " ");
            }
        }
    }

}

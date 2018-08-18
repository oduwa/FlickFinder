package com.odie.flickfinder;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.odie.flickfinder.Networking.FanartTv;
import com.odie.flickfinder.Networking.TracktTv;
import com.odie.flickfinder.Utility.Utility;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ShowActivity extends AppCompatActivity {

    public boolean isFromFavourites;
    public String showTvdbId;
    public String showQueryTitle;
    public JSONArray seasonsData;

    public TextView titleTextView;
    public TextView descTextView;
    public ImageView imageView;
    public Button randomButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Context context = this;

        titleTextView = (TextView) findViewById(R.id.title_text_view);
        descTextView = (TextView) findViewById(R.id.description_text_view);
        imageView = (ImageView) findViewById(R.id.imageView);
        randomButton = (Button) findViewById(R.id.button);

        Intent intent = getIntent();
        titleTextView.setText(intent.getStringExtra("title"));
        descTextView.setText(intent.getStringExtra("desc"));
        descTextView.setMovementMethod(new ScrollingMovementMethod());
        showTvdbId = intent.getStringExtra("tvdbId");
        showQueryTitle = intent.getStringExtra("showQueryTitle");
        isFromFavourites = intent.getBooleanExtra("isFromFavourites", false);

        fetchImage();
        fetchSeasonData();

        randomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(seasonsData != null){
                    /* Get season and ep number */

                    // pick a season at random
                    Random rand = new Random();
                    JSONObject randomSeasonObject = null;
                    int seasonCount = seasonsData.length();
                    int randomSeasonNumber = 0;
                    try {
                        randomSeasonObject = (JSONObject) seasonsData.get(rand.nextInt(seasonCount-1)+1);
                        randomSeasonNumber = randomSeasonObject.getInt("number");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // pick an episode from the season at random
                    int episodeCount = 0;
                    int randomEpisodeNumber = 0;
                    try {
                        episodeCount = randomSeasonObject.getInt("aired_episodes");
                        randomEpisodeNumber = rand.nextInt(episodeCount)+1;

                        Log.d("xxx", "S"+randomSeasonObject.getString("number")+"E"+randomEpisodeNumber);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    final int s = randomSeasonNumber;
                    final int e = randomEpisodeNumber;
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("https://api.trakt.tv/")
                            .build();

                    TracktTv trackt = retrofit.create(TracktTv.class);
                    try {
                        Call<ResponseBody> call = trackt.getEpisodeData(showQueryTitle,
                                randomSeasonObject.getInt("number"), randomEpisodeNumber);
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                Log.d("RANDOM_EPISODE", response.body().toString());

                                try {
                                    JSONObject json = new JSONObject(response.body().string());

                                    // Show episode info
                                    final Dialog dialog = new Dialog(context);
                                    dialog.setContentView(R.layout.content_episode_dialog);
                                    TextView episodeTitleTextView = (TextView) dialog.findViewById(R.id.episode_title_text_view);
                                    episodeTitleTextView.setText(String.format("S%02dE%02d", s, e));
                                    TextView episodeDescTextView = (TextView) dialog.findViewById(R.id.episode_desc_text_view);
                                    episodeDescTextView.setText(json.getString("overview"));
                                    episodeDescTextView.setMovementMethod(new ScrollingMovementMethod());
                                    Button okButton = (Button) dialog.findViewById(R.id.ok_button);
                                    okButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });
                                    dialog.show();


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Log.d("RANDOM_EPISODE", "Failed");
                            }
                        });

                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    public void fetchSeasonData(){
        final Context context = this;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.trakt.tv/")
                .build();
        TracktTv trackt = retrofit.create(TracktTv.class);
        Call<ResponseBody> call = trackt.getSeasonData(showQueryTitle);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("SEASON_FETCH", response.body().toString());

                try {
                    JSONArray json = new JSONArray(response.body().string());
                    seasonsData = json;
                    Log.d("SEASON_FETCH", json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("SEASON_FETCH", "Failed");
            }
        });
    }

    public void fetchImage(){
        final Context context = this;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://webservice.fanart.tv/v3/")
                .build();
        FanartTv fanartTv = retrofit.create(FanartTv.class);
        Call<ResponseBody> searchCall = fanartTv.searchImages(showTvdbId);

        searchCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("IMAGE_FETCH", response.body().toString());

                try {
                    JSONObject json = new JSONObject(response.body().string());
                    JSONObject imageJsonObject = (JSONObject)json.getJSONArray("showbackground").get(0);
                    String url = imageJsonObject.getString("url");
                    Picasso.with(context).load(url).placeholder(R.mipmap
                            .ic_launcher).into(imageView);
                    Log.d("IMAGE_FETCH", json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("IMAGE_FETCH", "Failed");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if(isFromFavourites){
            MenuItem item = menu.findItem(R.id.action_settings);
            item.setVisible(false);
            this.invalidateOptionsMenu();
        }
        else{
            MenuItem item = menu.findItem(R.id.action_remove);
            item.setVisible(false);
            this.invalidateOptionsMenu();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Context context = this;

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(this);
            }
            builder.setTitle("HOLD UP MARS!")
                    .setMessage("Sure you want to favourite this one?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Utility.saveTitle(showQueryTitle, context);
                            Log.d("TESTXXX", Utility.getSavedTitles(context).toString());
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return true;
        }
        else if(id == R.id.action_remove){
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(this);
            }
            builder.setTitle("HOLD UP MARS!")
                    .setMessage("Sure you're over this?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Utility.removeTitle(showQueryTitle, context);
                            Log.d("TESTXXX", Utility.getSavedTitles(context).toString());
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}

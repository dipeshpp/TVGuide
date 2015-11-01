package com.example.dipesh.tvguide;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class TrendingShowsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trending_shows);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        String[] showName=new String[20];
        String[] showAir=new String[20];
        String[] showImg=new String[20];
        Bitmap[] bitmap=new Bitmap[20];

        JSONArray mJsonArray=null;
        try {
            URL url = new URL("http://www.felipesilveira.com.br/tvguide/showtrends.php");
            String jsonstr=new FetchURLTask().execute(url).get();
            mJsonArray = new JSONArray(jsonstr);
        }catch (Exception e){
             Log.e("Error", e.toString());
        }

        for(int i=0;i<mJsonArray.length();i++){
            try {
                JSONObject mJsonObject = mJsonArray.getJSONObject(i);
                showName[i]=mJsonObject.getString("name");
                showAir[i]=mJsonObject.getString("air");
                showImg[i]=mJsonObject.getString("img");
            }catch(Exception e){
                Log.e("Error", e.getMessage());
            }
        }

        try {
            bitmap=new DownloadImageTask().execute(showImg).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        CustomListAdapter adapter=new CustomListAdapter(this, showName, showAir, bitmap);
        list=(ListView) findViewById(R.id.list);
        list.setAdapter(adapter);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.trending_shows, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }

    private class FetchURLTask extends AsyncTask<URL, Void, String> {
        protected String doInBackground(URL... urls) {
            String jsonstr=null;
            HttpURLConnection urlConnection=null;
            try {
                urlConnection = (HttpURLConnection) urls[0].openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                jsonstr = readStream(in);
            }catch(Exception e){
                Log.d("Error",e.toString());
            }finally {
                urlConnection.disconnect();
            }
            return jsonstr;
        }

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap[]> {

        protected Bitmap[] doInBackground(String... urls) {
            Bitmap[] bitmap=new Bitmap[20];
            for(int i=0;i<urls.length;i++) {
                try {
                    URL url = new URL(urls[i]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    bitmap[i] = BitmapFactory.decodeStream(input);
                } catch (Exception e) {
                    Log.e("Error", e.toString());
                    e.printStackTrace();
                }
            }
            return bitmap;
        }

    }

}



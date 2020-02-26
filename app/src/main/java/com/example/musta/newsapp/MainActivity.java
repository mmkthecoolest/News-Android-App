package com.example.musta.newsapp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView newslist;
    private ArrayList<String> titles;
    private ArrayList<Long> ids;
    private ArrayList<String> urls;
    private ArrayAdapter<String> adapter;
    private int numArticles;
    private SharedPreferences sharedPreferences;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = this.getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.articleNumSetting){
            Intent intent = new Intent(MainActivity.this, NumSetting.class);
            startActivityForResult(intent, 1);
            return true;
        }
        return false;
    }

    public class DownloadHTML extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            //string... means multiple string params
            // Log.i("URL", strings[0]);
//            String result = "";//html content
            StringBuilder sb = new StringBuilder();
            URL url;
            HttpURLConnection urlConnection = null;//think of it like a browser window

            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();//stream to hold the input of data
                InputStreamReader inputStreamReader = new InputStreamReader(in);
                BufferedReader b = new BufferedReader(inputStreamReader);

//                int data = inputStreamReader.read();//keeps track of location of reader in HTML

//                while (data != -1){//data keeps increasing as it goes on and after finishing has value of -1
//                    char current = (char) data;
//                    result += current;
//
//                    data = inputStreamReader.read();
//                }

                for (String line; (line = b.readLine()) != null; ) {
                    sb.append(line).append("\n");
                }

                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return "Failed";
            }


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("Result string", s);
            JSONObject jsonObject = null;
            try {
                JSONArray jsonArray = new JSONArray(s);
                if(jsonArray.length() < numArticles){
                    numArticles = jsonArray.length();
                    sharedPreferences.edit().putInt("savedNum", numArticles).apply();
                }
//                jsonObject = new JSONObject(s);
                Log.i("JSON Object Info", jsonArray.toString());

                for(int i = 0; i < numArticles; i++){
                    DownloadArticle article = new DownloadArticle();
                    article.execute("https://hacker-news.firebaseio.com/v0/item/" + jsonArray.get(i).toString() + ".json?print=pretty");
                }
                Log.i("Loading status", "Done");


            } catch (JSONException e) {
                e.printStackTrace();
            }


            //Log.i("Website result:", s);
        }
    }

    public class DownloadArticle extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            //string... means multiple string params
            // Log.i("URL", strings[0]);
//            String result = "";//html content

            StringBuilder sb = new StringBuilder();
            URL url;
            HttpURLConnection urlConnection = null;//think of it like a browser window

            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();//stream to hold the input of data
                InputStreamReader inputStreamReader = new InputStreamReader(in);
                BufferedReader b = new BufferedReader(inputStreamReader);

                for (String line; (line = b.readLine()) != null; ) {
                    sb.append(line).append("\n");
                }

                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return "Failed";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            Log.i("Result string article", s);
            JSONObject jsonObject = null;
            String id = null, url = null;
            try {
                jsonObject = new JSONObject(s);
                id = jsonObject.getString("id");
                url = jsonObject.getString("url");
                //last line checks for URL

            } catch (JSONException e) {
                e.printStackTrace();
                Log.i("ID in trouble, skipping", id);
                return;
            }
            try {
                ids.add(jsonObject.getLong("id"));
                urls.add(jsonObject.getString("url"));
                titles.add(jsonObject.getString("title"));
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i("List add error", "!");
            }



            //Log.i("Website result:", s);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        numArticles = sharedPreferences.getInt("savedNum", 10);
        sharedPreferences.edit().putInt("savedNum", numArticles).apply();

        newslist = (ListView) findViewById(R.id.newslist);
        titles = new ArrayList<String>();
        ids = new ArrayList<Long>();
        urls = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, titles);
        newslist.setAdapter(adapter);
        newslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("URL clicked", urls.get(i));
                Intent intent = new Intent(MainActivity.this, Web.class);
                intent.putExtra("URL", urls.get(i));
                startActivity(intent);
            }
        });
        DownloadHTML downloader = new DownloadHTML();

        downloader.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(1 == requestCode) {
            int intExtra = data.getIntExtra("number", numArticles);
            if(numArticles != intExtra){
                numArticles = intExtra;
                sharedPreferences.edit().putInt("savedNum", numArticles).apply();
                //RESTART CODE FROM STACK OVERFLOW
                //https://stackoverflow.com/a/17166729/10219333
//                Intent mStartActivity = new Intent(MainActivity.this, MainActivity.class);
//                int mPendingIntentId = 1;
//                PendingIntent mPendingIntent = PendingIntent.getActivity(MainActivity.this,
//                        mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
//
//                AlarmManager mgr = (AlarmManager) MainActivity.this.
//                        getSystemService(Context.ALARM_SERVICE);
//                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
//                finish();
//                System.exit(0);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}

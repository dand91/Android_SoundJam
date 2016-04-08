package com.example.andersson.musicapp;

import android.os.AsyncTask;

public class MyAsyncTask extends AsyncTask<Void, Void, String> {

    private ThreadHolder holder;

    @Override
    protected String doInBackground(Void... params) {
        String result = FetchClass.getResultFromRss(holder);
        return result;
    }
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

    }

    public void addHolder(ThreadHolder holder) {
        this.holder = holder;
    }
}

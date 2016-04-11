package com.example.andersson.musicapp.AsyncUpdate;

import com.example.andersson.musicapp.SharedResources.SharedInfoHolder;

public class AsyncTask extends android.os.AsyncTask<Void, Void, String> {

    private SharedInfoHolder holder;

    @Override
    protected String doInBackground(Void... params) {
        String result = UpdateTask.saveAndLoad(holder);
        return result;
    }
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

    }
    public void addHolder(SharedInfoHolder holder) {
        this.holder = holder;
    }
}

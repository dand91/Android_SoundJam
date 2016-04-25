package com.example.andersson.musicapp.AsyncUpdate;

import com.example.andersson.musicapp.SharedResources.SharedInfoHolder;
import com.example.andersson.musicapp.SharedResources.UpdateObservable;

public class AsyncTask extends android.os.AsyncTask<Void, Void, String> {

    private SharedInfoHolder holder;
    private UpdateObservable ob;

    @Override
    protected String doInBackground(Void... params) {

        String result = UpdateTask.saveAndLoad(holder, ob);
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

    }

    public void addHolder(SharedInfoHolder holder) {
        this.holder = holder;
    }

    public void addObserver(UpdateObservable ob) {
        this.ob = ob;
    }

}

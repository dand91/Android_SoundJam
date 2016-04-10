package com.example.andersson.musicapp;

public class AsyncTask extends android.os.AsyncTask<Void, Void, String> {

    private ThreadHolder holder;

    @Override
    protected String doInBackground(Void... params) {
        String result = UpdateTask.saveAndLoad(holder);
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

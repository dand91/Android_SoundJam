package com.example.andersson.musicapp.AsyncUpdate;

public class AsyncTask extends android.os.AsyncTask<Void, Void, String> {

    private UpdateObservable ob;

    @Override
    protected String doInBackground(Void... params) {

        String result = UpdateTask.saveAndLoad(ob);
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

    }

    public void addObserver(UpdateObservable ob) {
        this.ob = ob;
    }

}

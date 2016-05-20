package com.example.andersson.musicapp.AsyncUpdate;

public class AsyncTask extends android.os.AsyncTask<Void, Void, String> {

    private UpdateObservable observable;

    @Override
    protected String doInBackground(Void... params) {

        String result = UpdateTask.saveAndLoad(observable);
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

    }

    public void addObserver(UpdateObservable observable) {
        this.observable = observable;
    }

}

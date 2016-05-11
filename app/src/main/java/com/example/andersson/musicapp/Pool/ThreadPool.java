package com.example.andersson.musicapp.Pool;

import android.util.Log;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Andersson on 11/05/16.
 */
public class ThreadPool {

    private static ThreadPool instance = null;
    private ThreadPoolExecutor executor;
    private HashMap<String,Thread> map;

    public static ThreadPool getInstance() {

        if (instance == null) {
            instance = new ThreadPool();
        }
        return instance;
    }

    public ThreadPool(){

        executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        map = new HashMap<String,Thread>();
    }

    public void add(Thread thread,String name){

        try {

            executor.execute(thread);

        }catch(Exception e){

            Log.e("ThreadPool","Error while executing thread. Message: " + e.getMessage());
        }

        map.put(name, thread);
        Log.v("ThreadPool", "Active Threads: " + executor.getActiveCount());
    }
    public void remove(String name){

        executor.remove(map.get(name));
    }
}

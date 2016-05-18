package com.example.andersson.musicapp.Pool;

import android.util.Log;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Andersson on 11/05/16.
 */

public class ThreadPool {

    private static final Integer MAX_THREAD = 50;
    private static ThreadPool instance = null;
    private ThreadPoolExecutor executor;
    private HashMap<String, Thread> threadMap;
    private HashMap<String, Future> futureMap;
    private HashMap<String, Callable> callableMap;

    public ThreadPool() {

        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_THREAD);
        Log.d("ThreadPool", "Pre starting threads: " + executor.prestartAllCoreThreads());

        threadMap = new HashMap<String, Thread>();
        futureMap = new HashMap<String, Future>();
        callableMap = new HashMap<String, Callable>();

    }

    public static ThreadPool getInstance() {

        if (instance == null) {
            instance = new ThreadPool();
        }
        return instance;
    }

    public void add(Thread thread, String name) {

        try {

            executor.execute(thread);

        } catch (Exception e) {

            Log.e("ThreadPool", "Error while executing thread. Message: " + e.getMessage());
        }

        threadMap.put(name, thread);
    }

    public void addWithFuture(Callable thread, String name) {

        Future future = null;

        try {

            future = executor.submit(thread);

        } catch (Exception e) {

            Log.e("ThreadPool", "Error while executing thread. Message: " + e.getMessage());
        }

        callableMap.put(name, thread);
        futureMap.put(name, future);

    }

    public boolean isDone(String name) {

        return futureMap.get(name).isDone();

    }

    public Object getFuture(String name) throws ExecutionException, InterruptedException {

        return futureMap.get(name).get();

    }

    public Object getCallable(String name) throws ExecutionException, InterruptedException {

        return callableMap.get(name);

    }

    public Object getThread(String name) {

        return threadMap.get(name);

    }

    public void removeThread(String name) {

        Thread thread = threadMap.remove(name);

    }
    public void removeCallable(String name) {

        Callable callable = callableMap.remove(name);
    }

    public void removeFuture(String name) {

        try {

            Future future = futureMap.remove(name);
            future.cancel(true);

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    public void getInfo() {

        try {

            Log.v("ThreadPool", "Active Threads: " + executor.getActiveCount());
            Log.v("ThreadPool", "Complete Task Count: " + executor.getCompletedTaskCount());
            Log.v("ThreadPool", "Core Pool Size: " + executor.getCorePoolSize());
            Log.v("ThreadPool", "Largest Pool Size: " + executor.getLargestPoolSize());
            Log.v("ThreadPool", "Maximum Pool Size: " + executor.getMaximumPoolSize());
            Log.v("ThreadPool", "Pool Size: " + executor.getPoolSize());
            Log.v("ThreadPool", "Task Count: " + executor.getTaskCount());
            Log.v("ThreadPool", "Remaining Queue Capacity: " + executor.getQueue().remainingCapacity());

        } catch (Exception e) {

            Log.e("ThreadPool", "Error while printing info");
        }

    }
}

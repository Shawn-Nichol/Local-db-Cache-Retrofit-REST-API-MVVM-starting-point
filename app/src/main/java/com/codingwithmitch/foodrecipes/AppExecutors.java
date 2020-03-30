package com.codingwithmitch.foodrecipes;


import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Executor: An object that executes submitted Runnable tasks. This interface provides a way of decoupling
 * task submissions from the mechanics of how each task will be run, including details of thread use,
 *
 */
public class AppExecutors {

    private static AppExecutors instance;

    public static AppExecutors getInstance(){
        if(instance == null){
            instance = new AppExecutors();
        }
        return instance;
    }


    // Executor: An Object that executes submitted Runnable tasks. This interface provides a way of
    // decoupling task submissions from the mechanics. Does work on a different thread.
    private final Executor mDiskIO  = Executors.newSingleThreadExecutor();

    // Send information from background thread to the MainThread.
    private final Executor mMainThreadExecutor = new MainThreadExecutor();

    public Executor diskIO() {
        return mDiskIO;
    }

    public Executor mainThread(){
        return mMainThreadExecutor;
    }

    private static class MainThreadExecutor implements Executor {

        // Handler: A Handler allows you to send and process Message and Runnable objects associated with
        // a thread's MessageQueue. Each Handler is associated with a single thread and that threads
        // message queue.
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}

package com.example.andersson.musicapp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

/**
 * Created by Andersson on 07/04/16.
 */
public class generateContext extends Activity {

        private  static Context mContext;

        public static Context getContext() {
            return mContext;
        }

        public static void setContext(Context mContext1) {
            mContext = mContext1;
        }

    }


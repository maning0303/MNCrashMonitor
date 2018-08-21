/*
     The MIT License (MIT)
     Copyright (c) 2017 Jenly Yu
     https://github.com/jenly1314
     Permission is hereby granted, free of charge, to any person obtaining
     a copy of this software and associated documentation files
     (the "Software"), to deal in the Software without restriction, including
     without limitation the rights to use, copy, modify, merge, publish,
     distribute, sublicense, and/or sell copies of the Software, and to permit
     persons to whom the Software is furnished to do so, subject to the
     following conditions:
     The above copyright notice and this permission notice shall be included
     in all copies or substantial portions of the Software.
     THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
     IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
     FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
     AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
     LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
     FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
     DEALINGS IN THE SOFTWARE.
 */
package com.maning.librarycrashmonitor.crash;

import android.os.Handler;
import android.os.Looper;

/**
 * 永生，暂时还没有用到
 * 引用github:https://github.com/jenly1314/NeverCrash
 */
public class NeverCrash {

    private CrashHandler mCrashHandler;

    private static NeverCrash mInstance;

    private NeverCrash() {

    }

    private static NeverCrash getInstance() {
        if (mInstance == null) {
            synchronized (NeverCrash.class) {
                if (mInstance == null) {
                    mInstance = new NeverCrash();
                }
            }
        }

        return mInstance;
    }

    public static void init(CrashHandler crashHandler) {
        getInstance().setCrashHandler(crashHandler);
    }

    private void setCrashHandler(CrashHandler crashHandler) {

        mCrashHandler = crashHandler;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                for (; ; ) {
                    try {
                        Looper.loop();
                    } catch (Throwable e) {
                        //捕获异常处理
                        if (mCrashHandler != null) {
                            mCrashHandler.uncaughtException(Looper.getMainLooper().getThread(), e);
                        }
                    }
                }
            }
        });

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                //捕获异常处理
                if (mCrashHandler != null) {
                    mCrashHandler.uncaughtException(t, e);
                }
            }
        });

    }

    public interface CrashHandler {
        void uncaughtException(Thread t, Throwable e);
    }
}

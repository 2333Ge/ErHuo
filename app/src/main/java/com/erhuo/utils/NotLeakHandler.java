package com.erhuo.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**使用 WeakReference 在 Handler 与 Activity 之间搭建一座桥梁。若一个对象仅被 WeakReference 引用，
 * 则不能幸免于 GC(垃圾收集)*/
public class NotLeakHandler<E extends Activity> extends Handler {
    private WeakReference<E> mWeakReference;

    public NotLeakHandler(E reference) {
        mWeakReference = new WeakReference<E>(reference);
    }

    @Override
    public void handleMessage(Message msg) {
        E reference = (E) mWeakReference.get();
        if (reference == null) { // the referenced object has been cleared
            return;
        }
        // do something
    }
}

package xyz.demj.camrecyclerviewadapter;

import android.os.Handler;
import android.os.Message;
import android.test.suitebuilder.annotation.Suppress;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by demj on 2016/10/14.
 */

public class RegisterListenerHelper<T> {
    private static class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            RegisterListenerHelper listenerHelper = (RegisterListenerHelper) msg.obj;
            synchronized (listenerHelper.mTArrayList) {
                List list = Collections.unmodifiableList(listenerHelper.mTArrayList);
                if (listenerHelper.mTCallback.isSingleCallback()) {
                    int count = list.size();
                    int index = -1;
                    for (Object o : list) {
                        listenerHelper.mTCallback.call(o, ++index, count);
                    }
                } else {
                    listenerHelper.mTCallback.call(list);
                }
            }
        }
    }

    private static MyHandler sMyHandler = new MyHandler();

    interface Callback<T> {
        void call(List<T> listeners);

        void call(T t, int index, int count);

        boolean isSingleCallback();
    }

    private final Callback<T> mTCallback;

    private final ArrayList<T> mTArrayList = new ArrayList<>();

    public RegisterListenerHelper(Callback<T> pTCallback) {
        mTCallback = pTCallback;
    }

    public void notifyListener() {
        if (mTCallback != null) {
            Message msg = Message.obtain();
            msg.obj = this;
            sMyHandler.sendMessage(msg);
        }
    }

    public void registerListener(T listener) {
        if (listener == null)
            throw new IllegalStateException("the OnOperateListener can not be null");
        synchronized (mTArrayList) {
            if (mTArrayList.contains(listener))
                throw new IllegalStateException("this OnOperateListener has already registered");
            mTArrayList.add(listener);
        }
    }

    public void unregisterListener(T listener) {
        if (listener == null)
            throw new IllegalStateException("the OnOperateListener can not be null");
        synchronized (mTArrayList) {

            if (!mTArrayList.contains(listener))
                throw new IllegalStateException("this OnOperateListener has not registered yet");
            // int pos = mOnOperateListeners.indexOf(pTOnOperateListener);
            mTArrayList.remove(listener);
            // notifyRegisterStateChanged(pTOnOperateListener, pos);
        }
    }


}

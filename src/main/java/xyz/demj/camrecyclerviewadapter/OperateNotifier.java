package xyz.demj.camrecyclerviewadapter;

import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;

/**
 * Created by demj on 2016/9/15 0015.
 */
public class OperateNotifier<T, OPR> {

    private static int count = 0;
    private final ArrayList<OnOperateListener<T, OPR>> mOnOperateListeners = new ArrayList<>();

    public static <T, OPR> OperateNotifier<T, OPR> get() {
        return new OperateNotifier<>();
    }

    private static final class MyHandler extends Handler {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            if (msg.obj instanceof ObjHolder) {
                ObjHolder lvObjHolder = (ObjHolder) msg.obj;
                switch (lvObjHolder.type) {
                    case ObjHolder.TYPE_ADD:
                        lvObjHolder.mOperateNotifier.notifyListenersOnAdd(lvObjHolder.mOpR, lvObjHolder.mNewT);
                        break;
                    case ObjHolder.TYPE_DEL:
                        lvObjHolder.mOperateNotifier.notifyListenerOnDel(lvObjHolder.mOpR, lvObjHolder.mOldT);
                        break;
                    case ObjHolder.TYPE_UPDATE:
                        lvObjHolder.mOperateNotifier.notifyListenersOnUpdate(lvObjHolder.mOpR, lvObjHolder.mOldT, lvObjHolder.mNewT);
                        break;

                }
                return;
            }
            super.handleMessage(msg);
        }
    }

    private static final class ObjHolder<T, OpR> {
        static final int TYPE_ADD = 0;
        static final int TYPE_UPDATE = 1;
        static final int TYPE_DEL = 2;
        OperateNotifier mOperateNotifier;
        T mNewT;
        T mOldT;
        OpR mOpR;
        int type;
    }

    private static final class HandlerHolder {
        private static MyHandler sMyHandler = new MyHandler();
    }

    public interface OnOperateListener<T, OPR> {
        void onAdd(OPR pOPR, T pAdd);

        void onUpdate(OPR pOPR, T pOldOne, T pNewOne);

        void onDelete(OPR pOPR, T pDeleted);
    }

    public static class SimpleOnOperateListenerImpl<T, OPR> implements OnOperateListener<T, OPR> {
        @Override
        public void onAdd(OPR pOPR, T pAdd) {
        }

        @Override
        public void onUpdate(OPR pOPR, T pOldOne, T pNewOne) {
        }

        @Override
        public void onDelete(OPR pOPR, T pDeleted) {
        }
    }

    public final void registerOnOperateListener(OnOperateListener<T, OPR> pTOnOperateListener) {
        if (pTOnOperateListener == null)
            throw new IllegalStateException("the OnOperateListener can not be null");
        synchronized (mOnOperateListeners) {
            count++;
            if (mOnOperateListeners.contains(pTOnOperateListener))
                throw new IllegalStateException("this OnOperateListener has already registered");
            mOnOperateListeners.add(pTOnOperateListener);
            int position = mOnOperateListeners.indexOf(pTOnOperateListener);
            notifyRegisterStateChanged(pTOnOperateListener, position);
        }
    }

    private void notifyRegisterStateChanged(OnOperateListener<T, OPR> pListener, int pos) {
        RegisterState lvState = new RegisterState();
        lvState.position = pos;
        lvState.onOperateListener = pListener;
        lvState.size = mOnOperateListeners.size();
//        mRegisterNotifer.notifyDel(lvState, this);
    }

    public final void unregisterOnOperateListener(OnOperateListener<T, OPR> pTOnOperateListener) {
        if (pTOnOperateListener == null)
            throw new IllegalStateException("the OnOperateListener can not be null");
        synchronized (mOnOperateListeners) {
            count--;
            if (!mOnOperateListeners.contains(pTOnOperateListener))
                throw new IllegalStateException("this OnOperateListener has not registered yet");
            int pos = mOnOperateListeners.indexOf(pTOnOperateListener);
            mOnOperateListeners.remove(pTOnOperateListener);
            notifyRegisterStateChanged(pTOnOperateListener, pos);
        }
    }

    public final void notifyAdd(OPR pOPR, T pAddedOne) {
        Message lvMessage = Message.obtain();
        ObjHolder<T, OPR> lvTObjHolder = getObjHolder(pOPR, ObjHolder.TYPE_ADD);
        lvTObjHolder.mNewT = pAddedOne;
        lvMessage.obj = lvTObjHolder;
        HandlerHolder.sMyHandler.sendMessage(lvMessage);
    }

    private ObjHolder<T, OPR> getObjHolder(OPR pOPR, int type) {
        ObjHolder<T, OPR> lvTObjHolder = new ObjHolder<>();
        lvTObjHolder.mOperateNotifier = this;
        lvTObjHolder.type = type;
        lvTObjHolder.mOpR = pOPR;
        return lvTObjHolder;
    }

    public final void notifyDel(OPR pOPR, T pDelOne) {
        Message lvMessage = Message.obtain();
        ObjHolder<T, OPR> lvTObjHolder = getObjHolder(pOPR, ObjHolder.TYPE_DEL);
        lvTObjHolder.mOldT = pDelOne;
        lvMessage.obj = lvTObjHolder;
        HandlerHolder.sMyHandler.sendMessage(lvMessage);
    }

//    public final void notifyOperateFailed(int type, T pT) {
//        ObjHolder<T,OPR> lvTObjHolder = getObjHolder(ObjHolder.TYPE_FAILED);
//        Message lvMessage = Message.obtain();
//        lvTObjHolder.mOldT = pT;
//        lvMessage.obj = lvTObjHolder;
//        HandlerHolder.sMyHandler.sendMessage(lvMessage);
//    }

    public final void notifyUpdate(OPR pOPR, T pOld, T pNewOne) {
        Message lvMessage = Message.obtain();
        ObjHolder<T, OPR> lvTObjHolder = getObjHolder(pOPR, ObjHolder.TYPE_UPDATE);
        lvTObjHolder.mOldT = pOld;
        lvTObjHolder.mNewT = pNewOne;
        lvMessage.obj = lvTObjHolder;
        HandlerHolder.sMyHandler.sendMessage(lvMessage);
    }

    private void notifyListenersOnAdd(OPR pOPR, T pNewOne) {
        synchronized (mOnOperateListeners) {
            for (OnOperateListener<T, OPR> lvTOnOperateListener : mOnOperateListeners) {
                lvTOnOperateListener.onAdd(pOPR, pNewOne);
            }
        }
    }

    private void notifyListenerOnDel(OPR pOPR, T pDelOne) {
        synchronized (mOnOperateListeners) {
            for (OnOperateListener<T, OPR> lvTOnOperateListener : mOnOperateListeners) {
                lvTOnOperateListener.onDelete(pOPR, pDelOne);
            }
        }
    }

    private void notifyListenersOnUpdate(OPR pOPR, T pOldOne, T pNewOne) {
        synchronized (mOnOperateListeners) {
            for (OnOperateListener<T, OPR> lvTOnOperateListener : mOnOperateListeners) {
                lvTOnOperateListener.onUpdate(pOPR, pOldOne, pNewOne);
            }
        }
    }


    public static final class RegisterState<T, OPR> {
        public int position = -1;
        public OnOperateListener<T, OPR> onOperateListener;
        public int size = 0;
    }

//    private OperateNotifier<OperateNotifier<T, OPR>, RegisterState> mRegisterNotifer = OperateNotifier.get();
}

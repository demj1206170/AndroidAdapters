package xyz.demj.interfaces;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentHostCallback;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by demj on 2016/5/6 0006.
 * Author demj
 */
public abstract class BackHandleActivity extends AppCompatActivity {

    private static final String TAG = BackHandleActivity.class.getSimpleName();
    private static final String DEFAULT_BACK_PRESSED_LISTENER_MSG = "you see this because you haven't call setOnBackPressedListener yet.";
    private static final String NULL_BACK_PRESSED_LISTENER_MSG = "you see this because you call setOnBackPressedListener but pass null.";
    private static final BackPressedListenerImpl DEFAULT_PRESSED_LISTENER = new BackPressedListenerImpl(DEFAULT_BACK_PRESSED_LISTENER_MSG);
    private static final BackPressedListenerImpl NULL_PRESSED_LISTENER = new BackPressedListenerImpl(NULL_BACK_PRESSED_LISTENER_MSG);

    private LinkedHashSet<Long> mHoldFragmentIds = new LinkedHashSet<>();
    private OnBackPressedListener mOnBackPressedListener = DEFAULT_PRESSED_LISTENER;

    public static final String FRAGMENT = "_fragment";
    private BackHandleFragment mCurrentShowFragment;

    public void setOnBackPressedListener(OnBackPressedListener listener) {
        if (listener == null)
            this.mOnBackPressedListener = NULL_PRESSED_LISTENER;
        else
            this.mOnBackPressedListener = listener;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.mOnBackPressedListener = null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //do something init

        if (savedInstanceState != null) {
            restore(savedInstanceState);

        }
    }

    @Override
    public void onBackPressed() {
        //if listener return false mean's listener will not interested this event or done
        //and activity will pop current fragment back to stack.
        //or if true mean's listener has handle back event and activity should handle again.
        if (mOnBackPressedListener.onBackPressed()) {
            getSupportFragmentManager().popBackStack();
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                super.onBackPressed();
            }
        }
    }

    public void setSelectedFragment(BackHandleFragment backHandleFragment) {
        if (backHandleFragment == null)
            return;
        setOnBackPressedListener(backHandleFragment);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (mCurrentShowFragment != null) {
            fragmentTransaction.hide(mCurrentShowFragment);
        }
        mCurrentShowFragment = backHandleFragment;
        List<Fragment> addedFragments = getSupportFragmentManager().getFragments();
        boolean isAdded = false;
        if (addedFragments != null) {
            for (Fragment fragment : addedFragments) {
                if (fragment == backHandleFragment) {
                    isAdded = true;
                    break;
                }
            }
        }
        if (mCurrentShowFragment.getFragmeentID() == BackHandleFragment.NO_ID) {
            mCurrentShowFragment.setFragmentId(System.currentTimeMillis());
            mHoldFragmentIds.add(backHandleFragment.getFragmeentID());
        }
        if (!isAdded) {
            fragmentTransaction.add(mCurrentShowFragment, "").show(mCurrentShowFragment).commit();
        } else {
            fragmentTransaction.show(mCurrentShowFragment).commit();
        }

    }


    protected abstract int getFragmentContainerViewId();

    private static final String CURRENT_SHOW_FRAGMENT_ID="current_show_fragment_id";
    private static final String HOLD_FRAGMENTS="hold_fragments";
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCurrentShowFragment != null)
            outState.putLong(CURRENT_SHOW_FRAGMENT_ID, mCurrentShowFragment.getFragmeentID());
        long[] mLHoldFragmentIds = new long[mHoldFragmentIds.size()];
        int i = 0;
        for (long id : mHoldFragmentIds) {
            mLHoldFragmentIds[i++] = id;
        }
        outState.putLongArray(HOLD_FRAGMENTS, mLHoldFragmentIds);
    }

    private void restore(Bundle savedInstanceState) {
        long mCurrentShowId = savedInstanceState.getLong(CURRENT_SHOW_FRAGMENT_ID);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        Fragment fragment = getSupportFragmentManager().findFragmentById(id);
//        if (fragment != null && fragment instanceof BackHandleFragment) {
//            mCurrentShowFragment = (BackHandleFragment) fragment;
//        }
        long[] fragments = savedInstanceState.getLongArray(HOLD_FRAGMENTS);
        mHoldFragmentIds.clear();
        if (fragments != null)
            for (long lid : fragments) {
                mHoldFragmentIds.add(lid);
            }
        List<Fragment> fragmentList = fragmentManager.getFragments();
        if (fragmentList != null) {
            for (Fragment fragment : fragmentList) {
                if (fragment != null && fragment instanceof BackHandleFragment) {
                    BackHandleFragment backHandleFragment = (BackHandleFragment) fragment;
                    long fragmentId = backHandleFragment.getFragmeentID();
                    if (mHoldFragmentIds.contains(fragmentId)) {
                        if (fragmentId == mCurrentShowId) {
                            mCurrentShowFragment = backHandleFragment;
                            fragmentTransaction.show(backHandleFragment);
                        } else {
                            fragmentTransaction.hide(backHandleFragment);
                        }
                    }
                }
            }
        }
        fragmentTransaction.commit();
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);


    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //interfaces
    interface OnBackPressedListener {
        boolean onBackPressed();
    }

    private static class BackPressedListenerImpl implements OnBackPressedListener {
        String mString;

        public BackPressedListenerImpl(String msg) {
            mString = msg;
        }

        @Override
        public boolean onBackPressed() {
            Log.e(TAG, mString);
            return false;
        }
    }
}

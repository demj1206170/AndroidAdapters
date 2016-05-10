package xyz.demj.interfaces;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by demj on 2016/5/6 0006.
 * Author demj
 */
public abstract class BackHandleFragment extends Fragment implements BackHandleActivity.OnBackPressedListener {

    static final long NO_ID = -1;

    public BackHandleActivity getBackHandleActivity() {
        return (BackHandleActivity) getActivity();
    }

    long mId = NO_ID;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!(getActivity() instanceof BackHandleActivity)) {
            throw new IllegalStateException("host must BackHandleActivity or subclass.");
        }
        restoreID(savedInstanceState);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (getView() != null) {
            //   getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
        }
    }

    void setFragmentId(long id) {
        this.mId = id;
    }

    public long getFragmeentID() {
        return mId;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("MID", mId);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

    }

    private void restoreID(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mId = savedInstanceState.getLong("MID", NO_ID);
        }
    }
}

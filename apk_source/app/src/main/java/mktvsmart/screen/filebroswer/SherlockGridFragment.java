package mktvsmart.screen.filebroswer;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/* loaded from: classes.dex */
public class SherlockGridFragment extends GridFragment {
    private FragmentActivity mActivity;

    public FragmentActivity getSherlockActivity() {
        return this.mActivity;
    }

    @Override // android.support.v4.app.Fragment
    public void onAttach(Activity activity) {
        if (!(activity instanceof FragmentActivity)) {
            throw new IllegalStateException(String.valueOf(getClass().getSimpleName()) + " must be attached to a SherlockFragmentActivity.");
        }
        this.mActivity = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override // android.support.v4.app.Fragment
    public void onDetach() {
        this.mActivity = null;
        super.onDetach();
    }

    @Override // android.support.v4.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    @Override // android.support.v4.app.Fragment
    public void onPrepareOptionsMenu(Menu menu) {
    }

    @Override // android.support.v4.app.Fragment
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }
}

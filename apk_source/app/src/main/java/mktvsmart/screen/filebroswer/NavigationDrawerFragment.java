package mktvsmart.screen.filebroswer;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import mktvsmart.screen.R;
import mktvsmart.screen.filebroswer.SidebarAdapter;

/* loaded from: classes.dex */
public class NavigationDrawerFragment extends Fragment {
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private SidebarAdapter adapter;
    private SidebarCallbacks mCallbacks;
    private int mCurrentSelectedPosition = -1;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;

    public interface SidebarCallbacks {
        void onSidebarItemSelected(SidebarAdapter.SidebarEntry sidebarEntry, Fragment fragment);
    }

    @Override // android.support.v4.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        this.mDrawerListView = (ListView) view.findViewById(R.id.sidelist);
        this.mDrawerListView.setFooterDividersEnabled(true);
        this.adapter = new SidebarAdapter(getActivity());
        this.mDrawerListView.setAdapter((ListAdapter) this.adapter);
        this.mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: mktvsmart.screen.filebroswer.NavigationDrawerFragment.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> parent, View view2, int position, long id) {
                NavigationDrawerFragment.this.selectItem(position);
            }
        });
        selectItem(0);
        return view;
    }

    public boolean isDrawerOpen() {
        return this.mDrawerLayout != null && this.mDrawerLayout.isDrawerOpen(this.mFragmentContainerView);
    }

    public void closeDrawer() {
        if (this.mDrawerLayout != null) {
            this.mDrawerLayout.closeDrawer(this.mFragmentContainerView);
        }
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        this.mFragmentContainerView = getActivity().findViewById(fragmentId);
        this.mDrawerLayout = drawerLayout;
        this.mDrawerLayout.setScrimColor(0);
        this.mDrawerLayout.openDrawer(this.mFragmentContainerView);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void selectItem(int position) {
        if (this.mCurrentSelectedPosition != position) {
            this.mCurrentSelectedPosition = position;
            if (this.mDrawerListView != null) {
                this.mDrawerListView.setItemChecked(position, true);
            }
            if (this.mDrawerLayout != null) {
                this.mDrawerLayout.closeDrawer(this.mFragmentContainerView);
            }
            if (this.adapter != null) {
                this.adapter.setCurrentFragment(position);
                if (this.mCallbacks != null) {
                    this.mCallbacks.onSidebarItemSelected(this.adapter.getItem(position), this.adapter.fetchFragment(position));
                }
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.support.v4.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof SidebarCallbacks) {
            this.mCallbacks = (SidebarCallbacks) activity;
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onDetach() {
        super.onDetach();
        this.mCallbacks = null;
    }

    @Override // android.support.v4.app.Fragment
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, this.mCurrentSelectedPosition);
    }

    @Override // android.support.v4.app.Fragment, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public boolean onBackPressed() {
        if (!isDrawerOpen()) {
            return false;
        }
        this.mDrawerLayout.closeDrawer(this.mFragmentContainerView);
        return true;
    }
}

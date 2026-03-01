package mktvsmart.screen.filebroswer;

import android.content.ComponentCallbacks;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.alibaba.fastjson.asm.Opcodes;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import mktvsmart.screen.R;
import mktvsmart.screen.filebroswer.NavigationDrawerFragment;
import mktvsmart.screen.filebroswer.SidebarAdapter;

/* loaded from: classes.dex */
public class GsFileBroswerActivity extends FragmentActivity implements NavigationDrawerFragment.SidebarCallbacks {
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private HashMap<String, Fragment> mSecondaryFragments = new HashMap<>();
    private static final List<String> secondaryFragments = Arrays.asList("mediaInfo", "videoGroupList");
    private static final int[] contextMenuItem = {R.string.sortby_name, R.string.sortby_length};

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_file_broswer);
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.filebroswer.GsFileBroswerActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View arg0) throws Throwable {
                GsFileBroswerActivity.this.onBackPressed();
            }
        });
        findViewById(R.id.more_btn).setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.filebroswer.GsFileBroswerActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View arg0) {
                if (GsFileBroswerActivity.this.mNavigationDrawerFragment.isDrawerOpen()) {
                    GsFileBroswerActivity.this.mNavigationDrawerFragment.closeDrawer();
                }
                GsFileBroswerActivity.this.showContextPopupMenu(arg0);
            }
        });
        findViewById(R.id.refresh_btn).setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.filebroswer.GsFileBroswerActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View arg0) {
                if (GsFileBroswerActivity.this.mNavigationDrawerFragment.isDrawerOpen()) {
                    GsFileBroswerActivity.this.mNavigationDrawerFragment.closeDrawer();
                }
                GsFileBroswerActivity.this.refresh();
            }
        });
        this.mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        this.mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override // mktvsmart.screen.filebroswer.NavigationDrawerFragment.SidebarCallbacks
    public void onSidebarItemSelected(SidebarAdapter.SidebarEntry entry, Fragment fragment) {
        if (entry != null) {
            Fragment current = getSupportFragmentManager().findFragmentById(R.id.container);
            if ((current == null || !current.getTag().equals(entry.id)) && fragment != null) {
                if (current != null) {
                    current.setUserVisibleHint(false);
                }
                getSupportFragmentManager().popBackStack((String) null, 1);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, fragment, entry.id).commit();
                fragment.setUserVisibleHint(true);
            }
        }
    }

    private View onPrepareContextPopupMenuView() {
        final ListView view = new ListView(this);
        view.setDivider(new ColorDrawable(-7829368));
        view.setDividerHeight(1);
        view.setBackgroundColor(-1);
        BaseAdapter adapter1 = new BaseAdapter() { // from class: mktvsmart.screen.filebroswer.GsFileBroswerActivity.4
            @Override // android.widget.Adapter
            public View getView(int arg0, View arg1, ViewGroup arg2) {
                if (arg1 == null) {
                    arg1 = GsFileBroswerActivity.this.getLayoutInflater().inflate(R.layout.record_file_menu_option_item, (ViewGroup) null);
                }
                ((TextView) arg1).setText(GsFileBroswerActivity.contextMenuItem[arg0]);
                return arg1;
            }

            @Override // android.widget.Adapter
            public long getItemId(int arg0) {
                return GsFileBroswerActivity.contextMenuItem[arg0];
            }

            @Override // android.widget.Adapter
            public Object getItem(int arg0) {
                return Integer.valueOf(GsFileBroswerActivity.contextMenuItem[arg0]);
            }

            @Override // android.widget.Adapter
            public int getCount() {
                return GsFileBroswerActivity.contextMenuItem.length;
            }
        };
        view.setAdapter((ListAdapter) adapter1);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: mktvsmart.screen.filebroswer.GsFileBroswerActivity.5
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                ComponentCallbacks componentCallbacksFindFragmentById = GsFileBroswerActivity.this.getSupportFragmentManager().findFragmentById(R.id.container);
                switch (GsFileBroswerActivity.contextMenuItem[arg2]) {
                    case R.string.sortby_name /* 2131165539 */:
                    case R.string.sortby_length /* 2131165540 */:
                        if (componentCallbacksFindFragmentById != null) {
                            if (componentCallbacksFindFragmentById instanceof ISortable) {
                                ((ISortable) componentCallbacksFindFragmentById).sortBy(GsFileBroswerActivity.contextMenuItem[arg2] == R.string.sortby_name ? 0 : 1);
                            }
                            if (view.getTag() != null && (view.getTag() instanceof PopupWindow)) {
                                ((PopupWindow) view.getTag()).dismiss();
                                view.setTag(null);
                                break;
                            }
                        }
                        break;
                }
            }
        });
        view.setSelector(new ColorDrawable(0));
        return view;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showContextPopupMenu(final View anchor) {
        View view = onPrepareContextPopupMenuView();
        PopupWindow contextPopupMenu = new PopupWindow(view, convertDpToPx(Opcodes.GETFIELD, this), -2);
        contextPopupMenu.setOnDismissListener(new PopupWindow.OnDismissListener() { // from class: mktvsmart.screen.filebroswer.GsFileBroswerActivity.6
            @Override // android.widget.PopupWindow.OnDismissListener
            public void onDismiss() {
                anchor.setEnabled(true);
            }
        });
        contextPopupMenu.setTouchable(true);
        contextPopupMenu.setFocusable(true);
        contextPopupMenu.setBackgroundDrawable(new BitmapDrawable());
        contextPopupMenu.setOutsideTouchable(true);
        view.setTag(contextPopupMenu);
        contextPopupMenu.showAsDropDown(anchor);
        anchor.setEnabled(false);
    }

    public void updateOptionView() {
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.container);
        if (current != null && (current instanceof VideoGridFragment)) {
            if (findViewById(R.id.option).getVisibility() != 0) {
                findViewById(R.id.option).setVisibility(0);
            }
        } else if (findViewById(R.id.option).getVisibility() == 0) {
            findViewById(R.id.option).setVisibility(8);
        }
    }

    public Fragment fetchSecondaryFragment(String id) {
        Fragment f;
        if (this.mSecondaryFragments.containsKey(id) && this.mSecondaryFragments.get(id) != null) {
            return this.mSecondaryFragments.get(id);
        }
        if (id.equals("mediaInfo")) {
            f = new MediaInfoFragment();
        } else if (id.equals("videoGroupList")) {
            f = new VideoGridFragment();
        } else {
            return null;
        }
        f.setRetainInstance(true);
        this.mSecondaryFragments.put(id, f);
        return f;
    }

    public void switchSecondaryFragment(String tag, Fragment fragment) {
        if (fragment != null) {
            FragmentManager fm = getSupportFragmentManager();
            Fragment current = fm.findFragmentById(R.id.container);
            if ((current == null || !current.getTag().equals(tag)) && !fm.popBackStackImmediate(tag, 0)) {
                FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out);
                ft.replace(R.id.container, fragment, tag);
                ft.addToBackStack(tag);
                ft.commit();
            }
        }
    }

    public void popSecondaryFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStackImmediate((String) null, 1);
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onBackPressed() throws Throwable {
        if (!this.mNavigationDrawerFragment.onBackPressed()) {
            Fragment current = getSupportFragmentManager().findFragmentById(R.id.container);
            if (current != null) {
                if ((current instanceof FolderListFragment) && !((FolderListFragment) current).isRootDirectory()) {
                    ((FolderListFragment) current).showParentDirectory();
                    return;
                } else if (secondaryFragments.contains(current)) {
                    popSecondaryFragment();
                    return;
                }
            }
            super.onBackPressed();
        }
    }

    public static int convertDpToPx(int dp, Context context) {
        return Math.round(TypedValue.applyDimension(1, dp, context.getResources().getDisplayMetrics()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refresh() {
        ComponentCallbacks componentCallbacksFindFragmentById = getSupportFragmentManager().findFragmentById(R.id.container);
        if (componentCallbacksFindFragmentById != null && (componentCallbacksFindFragmentById instanceof IRefreshable)) {
            ((IRefreshable) componentCallbacksFindFragmentById).refresh();
        }
    }
}

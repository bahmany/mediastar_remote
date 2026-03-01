package mktvsmart.screen;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class GsStartGuideActivity extends Activity {
    private ImageView[] mDotViews;
    private ViewGroup mGuideLayout;
    private TextView mJumpGuide;
    private ViewPager mViewPager;
    private ViewGroup mViewPoints;
    private final int TOTAL_GUIDE_PAGE = 3;
    private final int PADDING = 20;
    private ArrayList<View> mPageViews = new ArrayList<>();
    private View.OnClickListener mOnClickListener = new View.OnClickListener() { // from class: mktvsmart.screen.GsStartGuideActivity.1
        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            Intent mIntent = new Intent();
            mIntent.setClass(GsStartGuideActivity.this, GsLoginListActivity.class);
            GsStartGuideActivity.this.startActivity(mIntent);
            GsStartGuideActivity.this.finish();
        }
    };

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) throws Resources.NotFoundException {
        GuidePageAdapter guidePageAdapter = null;
        byte b = 0;
        super.onCreate(bundle);
        LayoutInflater layoutInflater = getLayoutInflater();
        this.mDotViews = new ImageView[3];
        this.mGuideLayout = (ViewGroup) layoutInflater.inflate(R.layout.activity_guide_layout, (ViewGroup) null);
        this.mViewPoints = (ViewGroup) this.mGuideLayout.findViewById(R.id.view_dot);
        this.mViewPager = (ViewPager) this.mGuideLayout.findViewById(R.id.guide_pages);
        this.mJumpGuide = (TextView) this.mGuideLayout.findViewById(R.id.jump_text_view);
        for (int i = 0; i < 3; i++) {
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
            layoutParams.setMargins(20, 20, 20, 20);
            imageView.setLayoutParams(layoutParams);
            this.mDotViews[i] = imageView;
            if (i == 0) {
                this.mDotViews[i].setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                this.mDotViews[i].setBackgroundResource(R.drawable.page_indicator);
            }
            this.mViewPoints.addView(this.mDotViews[i]);
        }
        setContentView(this.mGuideLayout);
        this.mViewPager.setAdapter(new GuidePageAdapter(this, guidePageAdapter));
        this.mViewPager.setOnPageChangeListener(new GuidePageChangeListener(this, b == true ? 1 : 0));
        this.mJumpGuide.setOnClickListener(this.mOnClickListener);
    }

    private class GuidePageAdapter extends PagerAdapter {
        private GuidePageAdapter() {
        }

        /* synthetic */ GuidePageAdapter(GsStartGuideActivity gsStartGuideActivity, GuidePageAdapter guidePageAdapter) {
            this();
        }

        @Override // android.support.v4.view.PagerAdapter
        public void destroyItem(View v, int position, Object arg2) {
            ((ViewPager) v).removeView((View) GsStartGuideActivity.this.mPageViews.get(position));
        }

        @Override // android.support.v4.view.PagerAdapter
        public int getCount() {
            return 3;
        }

        @Override // android.support.v4.view.PagerAdapter
        public Object instantiateItem(View v, int position) {
            GsStartGuideActivity.this.mPageViews.add(GsStartGuideActivity.this.getLayoutInflater().inflate(R.layout.guide_page, (ViewGroup) null));
            ((ViewPager) v).addView((View) GsStartGuideActivity.this.mPageViews.get(position));
            switch (position) {
                case 0:
                    ((View) GsStartGuideActivity.this.mPageViews.get(position)).setBackgroundResource(R.drawable.bg_guide_pager1);
                    break;
                case 1:
                    ((View) GsStartGuideActivity.this.mPageViews.get(position)).setBackgroundResource(R.drawable.bg_guide_pager2);
                    break;
                case 2:
                    TextView closedView = (TextView) ((View) GsStartGuideActivity.this.mPageViews.get(position)).findViewById(R.id.btn_close_guide);
                    closedView.setVisibility(0);
                    closedView.setOnClickListener(GsStartGuideActivity.this.mOnClickListener);
                    break;
            }
            return GsStartGuideActivity.this.mPageViews.get(position);
        }

        @Override // android.support.v4.view.PagerAdapter
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }

    private class GuidePageChangeListener implements ViewPager.OnPageChangeListener {
        private GuidePageChangeListener() {
        }

        /* synthetic */ GuidePageChangeListener(GsStartGuideActivity gsStartGuideActivity, GuidePageChangeListener guidePageChangeListener) {
            this();
        }

        @Override // android.support.v4.view.ViewPager.OnPageChangeListener
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override // android.support.v4.view.ViewPager.OnPageChangeListener
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override // android.support.v4.view.ViewPager.OnPageChangeListener
        public void onPageSelected(int position) {
            for (int i = 0; i < GsStartGuideActivity.this.mDotViews.length; i++) {
                GsStartGuideActivity.this.mDotViews[position].setBackgroundResource(R.drawable.page_indicator_focused);
                if (position != i) {
                    GsStartGuideActivity.this.mDotViews[i].setBackgroundResource(R.drawable.page_indicator);
                }
            }
        }
    }
}

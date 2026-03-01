package mktvsmart.screen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GsUpdateGuideActivity extends Activity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private static final String PREFIX_UPDATE_GUIDE = "update-guide-";
    private ViewGroup mDotsView;
    private TextView mSkipButton;
    private ViewPager mViewPager;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) throws Resources.NotFoundException, IOException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_guide);
        findViews();
        initViews();
    }

    private void findViews() {
        this.mViewPager = (ViewPager) findViewById(R.id.update_guide_pages);
        this.mSkipButton = (TextView) findViewById(R.id.update_guide_skip);
        this.mDotsView = (ViewGroup) findViewById(R.id.pages_dot);
    }

    private void initViews() throws Resources.NotFoundException, IOException {
        byte b = 0;
        ArrayList arrayList = new ArrayList();
        UpdateGuideHelper updateGuideHelper = new UpdateGuideHelper(this, null);
        List<String> fileNamesAccordingDpi = updateGuideHelper.getFileNamesAccordingDpi();
        if (fileNamesAccordingDpi == null || fileNamesAccordingDpi.isEmpty()) {
            enterLogin();
            return;
        }
        int size = fileNamesAccordingDpi.size();
        for (int i = 0; i < size; i++) {
            if (size > 1) {
                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
                layoutParams.setMargins(getResources().getDimensionPixelOffset(R.dimen.margin_little), 0, getResources().getDimensionPixelOffset(R.dimen.margin_little), 0);
                imageView.setLayoutParams(layoutParams);
                if (i == 0) {
                    imageView.setBackgroundResource(R.drawable.page_indicator_focused);
                } else {
                    imageView.setBackgroundResource(R.drawable.page_indicator);
                }
                this.mDotsView.addView(imageView);
            }
            arrayList.add(updateGuideHelper.decodeBitmap(fileNamesAccordingDpi.get(i)));
        }
        this.mViewPager.setAdapter(new GuidePagerAdapter(this, arrayList, b == true ? 1 : 0));
        this.mViewPager.setOnPageChangeListener(this);
        this.mSkipButton.setOnClickListener(this);
    }

    @Override // android.support.v4.view.ViewPager.OnPageChangeListener
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override // android.support.v4.view.ViewPager.OnPageChangeListener
    public void onPageSelected(int position) {
        for (int i = 0; i < this.mDotsView.getChildCount(); i++) {
            if (i == position) {
                this.mDotsView.getChildAt(i).setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                this.mDotsView.getChildAt(i).setBackgroundResource(R.drawable.page_indicator);
            }
        }
    }

    @Override // android.support.v4.view.ViewPager.OnPageChangeListener
    public void onPageScrollStateChanged(int state) {
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        enterLogin();
    }

    private void enterLogin() {
        Intent intent = new Intent(this, (Class<?>) GsLoginListActivity.class);
        startActivity(intent);
        finish();
    }

    private final class GuidePagerAdapter extends PagerAdapter {
        private List<Bitmap> mContents;

        private GuidePagerAdapter(List<Bitmap> contents) {
            this.mContents = contents;
        }

        /* synthetic */ GuidePagerAdapter(GsUpdateGuideActivity gsUpdateGuideActivity, List list, GuidePagerAdapter guidePagerAdapter) {
            this(list);
        }

        @Override // android.support.v4.view.PagerAdapter
        public int getCount() {
            return this.mContents.size();
        }

        @Override // android.support.v4.view.PagerAdapter
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override // android.support.v4.view.PagerAdapter
        public Object instantiateItem(View container, int position) {
            ViewPager pager = (ViewPager) container;
            LayoutInflater inflater = (LayoutInflater) GsUpdateGuideActivity.this.getSystemService("layout_inflater");
            View pageView = inflater.inflate(R.layout.guide_page, (ViewGroup) pager, false);
            TextView close = (TextView) pageView.findViewById(R.id.btn_close_guide);
            pageView.setBackgroundDrawable(new BitmapDrawable(GsUpdateGuideActivity.this.getResources(), this.mContents.get(position)));
            if (position == this.mContents.size() - 1) {
                close.setVisibility(0);
                close.setOnClickListener(GsUpdateGuideActivity.this);
            }
            pager.addView(pageView);
            return pageView;
        }

        @Override // android.support.v4.view.PagerAdapter
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }
    }

    private static final class UpdateGuideHelper {
        private Context mContext;
        private String mPath;

        private UpdateGuideHelper(Context context) {
            this.mContext = context;
        }

        /* synthetic */ UpdateGuideHelper(Context context, UpdateGuideHelper updateGuideHelper) {
            this(context);
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        public List<String> getFileNamesAccordingDpi() throws IOException {
            int i = 0;
            try {
                int versionCode = this.mContext.getPackageManager().getPackageInfo(this.mContext.getPackageName(), 0).versionCode;
                DisplayMetrics displayMetrics = this.mContext.getResources().getDisplayMetrics();
                switch (displayMetrics.densityDpi) {
                    case 240:
                        try {
                            List<String> fileList = new ArrayList<>();
                            this.mPath = GsUpdateGuideActivity.PREFIX_UPDATE_GUIDE + versionCode + "/hdpi";
                            String[] files = this.mContext.getAssets().list(this.mPath);
                            int length = files.length;
                            while (i < length) {
                                String file = files[i];
                                fileList.add(String.valueOf(this.mPath) + '/' + file);
                                i++;
                            }
                            return fileList;
                        } catch (IOException e) {
                            e.printStackTrace();
                            break;
                        }
                    case 320:
                    case 480:
                    case 640:
                        List<String> fileList2 = new ArrayList<>();
                        int widthPixels = displayMetrics.widthPixels;
                        int heightPixels = displayMetrics.heightPixels;
                        try {
                            if (widthPixels == 1824 && heightPixels == 1104) {
                                this.mPath = GsUpdateGuideActivity.PREFIX_UPDATE_GUIDE + versionCode + "/xhdpi-1824-1104";
                            } else {
                                this.mPath = GsUpdateGuideActivity.PREFIX_UPDATE_GUIDE + versionCode + "/xhdpi";
                            }
                            String[] files2 = this.mContext.getAssets().list(this.mPath);
                            int length2 = files2.length;
                            while (i < length2) {
                                String file2 = files2[i];
                                fileList2.add(String.valueOf(this.mPath) + '/' + file2);
                                i++;
                            }
                            return fileList2;
                        } catch (IOException e2) {
                            e2.printStackTrace();
                            break;
                        }
                        break;
                    default:
                        return null;
                }
            } catch (PackageManager.NameNotFoundException e3) {
                throw new RuntimeException("Cannot find package name: " + this.mContext.getPackageName(), e3);
            }
        }

        public Bitmap decodeBitmap(String filePath) throws IOException {
            try {
                InputStream is = this.mContext.getAssets().open(filePath);
                return BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}

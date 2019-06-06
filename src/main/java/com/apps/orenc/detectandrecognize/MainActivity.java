package com.apps.orenc.detectandrecognize;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * The main acrivity that contains the view pager of the camera and persons fragments.
 */
public class MainActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";

    // Max number of pages.
    private static final int MAX_NUM_OF_PAGES = 2;

    // The pager widget: handles animation and allows swiping horizontal to
    // access next and previous pages.
    private ViewPager mViewPager;

    // The pager adapter which provides the pages to the view pager widget.
    private PagerAdapter mPagerAdapter;

    // The persons list and the list view adapter of both fragments.
    private List<Person> mPersons;
    private PeopleListViewAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPersons = new ArrayList<>();
        mAdapter = new PeopleListViewAdapter(getApplicationContext(), mPersons);

        // Initialize the adapter and set the pager with it.
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        // Give zoom out animation to the pager.
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
    }

    @Override
    public void onBackPressed() {
        if(mViewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle
            // the back button. This calls finish() on the activity and pops the back stack.
            super.onBackPressed();
        }
        else {
            // Otherwize, select the previous step.
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
        }
    }

    private class MainPagerAdapter extends FragmentPagerAdapter {

        public MainPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    CameraFragment cf = new CameraFragment();
                    cf.setArguments(mPersons, mAdapter);
                    return cf;
                case 1:
                    PeopleFragment pf = new PeopleFragment();
                    pf.setArguments(mPersons, mAdapter);
                    return pf;
            }

            return null;
        }

        @Override
        public int getCount() {
            return MAX_NUM_OF_PAGES;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0: return CameraFragment.TITLE;
                case 1: return PeopleFragment.TITLE;
            }

            return null;
        }
    }

    private class ZoomOutPageTransformer implements ViewPager.PageTransformer {

        // Minimum scaling when zooming out.
        private static final float MIN_SCALE = 0.85f;
        // Minimum alpha to the page when zooming out.
        private static final float MIN_ALPHA = 0.5f;

        @Override
        public void transformPage(View page, float position) {

            int pageWidth = page.getWidth();
            int pageHeight = page.getHeight();

            if(position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.setAlpha(0);
            }
            else if(position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;

                if(position < 0) {
                    page.setTranslationX(horzMargin - vertMargin / 2);
                }
                else {
                    page.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1).
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                page.setAlpha(
                        MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
            }
            else { // (1,+Infinity]
                // This page is way off-screen to the right.
                page.setAlpha(0);
            }
        }
    }

}

package com.evport.businessapp.ui.base.binding_adapter;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.databinding.BindingAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.kunminx.architecture.ui.adapter.CommonViewPagerAdapter;
import com.kunminx.architecture.utils.Utils;
import com.evport.businessapp.R;
import com.evport.businessapp.utils.CustomViewPager;

/**
 * Create by KunMinX at 2020/3/13
 */
public class TabPageBindingAdapter {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @BindingAdapter(value = {"initTabAndPage", "offscreen", "currentItem"}, requireAll = false)
    public static void initTabAndPage(TabLayout tabLayout, boolean initTabAndPage, int offscreen, int currentItem) {
        int count = tabLayout.getTabCount();
        String[] title = new String[count];
        Drawable[] icon = new Drawable[count];

        for (int i = 0; i < count; i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                title[i] = tab.getText() == null ? "" : tab.getText().toString();
                icon[i] = tab.getIcon();
            }
        }
//        if (offscreen == 7) {
//            ViewPager viewPager = (tabLayout.getRootView()).findViewById(R.id.view_pager);
//            if (viewPager != null) {
//                viewPager.setAdapter(new CommonViewPagerAdapter(count, false, title, null));
//            }
//            tabLayout.setupWithViewPager(viewPager);
//            tabLayout.removeAllTabs();
//            for (int i = 0; i < count; i++) {
//                TabLayout.Tab tab = tabLayout.newTab();
//                View view = LayoutInflater.from(Utils.getApp()).inflate(R.layout.home_tab_content, null);
//                TextView tabText = (TextView) view.findViewById(R.id.tab_content_text);
//                ImageView tabText1 = (ImageView) view.findViewById(R.id.tab_content_text1);
//                tabText1.setVisibility(View.VISIBLE);
//                tabText.setTextSize(14f);
//                tabText.setText(title[i]);
//                tabText.setTextColor(Utils.getApp().getResources().getColor(R.color.black));
//                tabText1.setBackgroundResource(0);
//                if (i == currentItem) {
//                    tabText1.setBackgroundResource(R.drawable.layer_tab_indicator);
//                    tabText.setTextColor(Utils.getApp().getResources().getColor(R.color.colorTheme));
//                }
//                tab.setCustomView(view);//??????????????????View
//                tabLayout.addTab(tab, i);
//            }
//        } else
        if (offscreen == 6) {
            ViewPager viewPager = (tabLayout.getRootView()).findViewById(R.id.view_pager);
            if (viewPager != null) {
                viewPager.setAdapter(new CommonViewPagerAdapter(count, false, title, null));
            }
            tabLayout.setupWithViewPager(viewPager);
            tabLayout.removeAllTabs();
            for (int i = 0; i < count; i++) {
                TabLayout.Tab tab = tabLayout.newTab();
                View view = LayoutInflater.from(Utils.getApp()).inflate(R.layout.home_tab_content, null);
                TextView tabText = (TextView) view.findViewById(R.id.tab_content_text);
                ImageView tabText1 = (ImageView) view.findViewById(R.id.tab_content_text1);
                tabText1.setVisibility(View.VISIBLE);
                tabText.setTextSize(14f);
                tabText.setText(title[i]);
                tabText.setTextColor(Utils.getApp().getResources().getColor(R.color.black));
                tabText1.setBackgroundResource(0);
                if (i == currentItem) {
                    tabText1.setBackgroundResource(R.drawable.layer_tab_indicator);
                    tabText.setTextColor(Utils.getApp().getResources().getColor(R.color.colorTheme));
                }
                tab.setCustomView(view);//??????????????????View
                tabLayout.addTab(tab, i);
            }
        } else

//            ?????????????????????????????????
            if (offscreen < 4) {
                ViewPager viewPager = (tabLayout.getRootView()).findViewById(R.id.view_pager);
                if (viewPager != null) {
                    viewPager.setAdapter(new CommonViewPagerAdapter(count, false, title, null));

                    tabLayout.setupWithViewPager(viewPager);
                    tabLayout.removeAllTabs();
                    if (icon[0] != null) {
                        //?????????????????????????????????????????????,????????????????????????
                        for (int i = 0; i < count; i++) {
                            TabLayout.Tab tab = tabLayout.newTab();
                            View view = LayoutInflater.from(Utils.getApp()).inflate(R.layout.home_tab_content, null);
                            ImageView tabIcon = (ImageView) view.findViewById(R.id.tab_content_image);
                            tabIcon.setImageDrawable(icon[i]);
                            TextView tabText = (TextView) view.findViewById(R.id.tab_content_text);
                            tabText.setText(title[i]);
                            tabText.setTextColor(Utils.getApp().getResources().getColor(R.color.selector_text_color));
                            tab.setCustomView(view);//??????????????????View
                            tabLayout.addTab(tab, i);
                        }
                    } else {

                        for (int i = 0; i < count; i++) {
                            TabLayout.Tab tab = tabLayout.newTab();
                            View view = LayoutInflater.from(Utils.getApp()).inflate(R.layout.home_tab_content, null);
                            TextView tabText = (TextView) view.findViewById(R.id.tab_content_text);
                            ImageView tabText1 = (ImageView) view.findViewById(R.id.tab_content_text1);
                            tabText1.setVisibility(View.VISIBLE);
                            tabText.setTextSize(14f);
                            tabText.setText(title[i]);
                            tabText.setTextColor(Utils.getApp().getResources().getColor(R.color.black_222628));
                            tabText1.setBackgroundResource(0);
                            if (i == currentItem) {
                                tabText1.setBackgroundResource(R.drawable.layer_tab_indicator);
                                tabText.setTextColor(Utils.getApp().getResources().getColor(R.color.colorTheme));
                            }
                            tab.setCustomView(view);//??????????????????View
                            tabLayout.addTab(tab, i);
                        }
                    }
                    if (offscreen <= 0) offscreen = 3;
                    viewPager.setOffscreenPageLimit(offscreen);
                    viewPager.setCurrentItem(currentItem);
                }
            } else {
                CustomViewPager viewPager = (tabLayout.getRootView()).findViewById(R.id.view_pager);
                if (viewPager != null) {
                    viewPager.setAdapter(new CommonViewPagerAdapter(count, false, title, null));

                    tabLayout.setupWithViewPager(viewPager);
                    if (icon[0] != null) {
                        tabLayout.removeAllTabs();
                        //?????????????????????????????????????????????,????????????????????????
                        for (int i = 0; i < count; i++) {
                            TabLayout.Tab tab = tabLayout.newTab();
                            View view = LayoutInflater.from(Utils.getApp()).inflate(R.layout.home_tab_content, null);
                            ImageView tabIcon = (ImageView) view.findViewById(R.id.tab_content_image);
                            tabIcon.setImageDrawable(icon[i]);
                            TextView tabText = (TextView) view.findViewById(R.id.tab_content_text);
                            tabText.setText(title[i]);
//                        tabText.setTextColor(Utils.getApp().getResources().getColor(R.color.selector_text_color));
                            tab.setCustomView(view);//??????????????????View
                            tabLayout.addTab(tab, i);
                        }
                    }
                    viewPager.setOffscreenPageLimit(offscreen);
                    viewPager.setCurrentItem(currentItem);
                }
            }
        tabLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // tabLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                for (int i = 0; i < count; i++) {
                    TabLayout.Tab tab = tabLayout.getTabAt(i);
                    if (tab != null) {
                        tab.view.setLongClickable(false);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            tab.view.setTooltipText(null);
                        }
                    }
                }
            }
        });
        int finalOffscreen = offscreen;
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab != null && tab.getCustomView() != null ) {
                    TextView tab_text = tab.getCustomView().findViewById(R.id.tab_content_text);
                    tab_text.setTextColor(Utils.getApp().getResources().getColor(R.color.colorTheme));
                    ImageView imageView = tab.getCustomView().findViewById(R.id.tab_content_text1);
                    imageView.setBackgroundResource(R.drawable.layer_tab_indicator);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab != null && icon[0] != null) {
                    TextView tab_text = tab.getCustomView().findViewById(R.id.tab_content_text);
                    tab_text.setTextColor(Utils.getApp().getResources().getColor(R.color.color_8F9293));
                    ImageView imageView = tab.getCustomView().findViewById(R.id.tab_content_text1);
                    imageView.setBackgroundResource(0);
                }else {

                    if (tab != null && tab.getCustomView() != null) {
                        TextView tab_text = tab.getCustomView().findViewById(R.id.tab_content_text);
                        tab_text.setTextColor(Utils.getApp().getResources().getColor(R.color.black_222628));
                        ImageView imageView = tab.getCustomView().findViewById(R.id.tab_content_text1);
                        imageView.setBackgroundResource(0);
                    }

                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @BindingAdapter(value = {"tabSelectedListener"}, requireAll = false)
    public static void tabSelectedListener(TabLayout tabLayout, TabLayout.OnTabSelectedListener listener) {
        int count = tabLayout.getTabCount();
        tabLayout.addOnTabSelectedListener(listener);
        tabLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // tabLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                for (int i = 0; i < count; i++) {
                    TabLayout.Tab tab = tabLayout.getTabAt(i);
                    if (tab != null) {
                        tab.view.setLongClickable(false);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            tab.view.setTooltipText(null);
                        }

                    }
                }
            }

        });
    }

    public static void tabSelectedListener(TabLayout tabLayout, String totalComment, String totalDevice, String type) {
        int count = tabLayout.getTabCount();
        tabLayout.removeAllTabs();
        for (int i = 0; i < count; i++) {
            TabLayout.Tab tab = tabLayout.newTab();
            View view = LayoutInflater.from(Utils.getApp()).inflate(R.layout.home_tab_content, null);
            TextView tabText = (TextView) view.findViewById(R.id.tab_content_text);
            tabText.setTextSize(14f);
            ImageView imageView = (ImageView) view.findViewById(R.id.tab_content_text1);
            if (type.equals("??????")) {
                tabText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//??????
                if (i == 0) {
                    tabText.setText("Info");
                    tabText.setTextColor(Utils.getApp().getResources().getColor(R.color.colorTheme));
                    imageView.setBackgroundResource(R.drawable.layer_tab_indicator);
                } else if (i == 1) {
                    tabText.setTextColor(Utils.getApp().getResources().getColor(R.color.black_222628));
                    if (!totalComment.equals("")) tabText.setText(totalComment);
                    imageView.setBackgroundResource(0);
                } else if (i == 2) {
                    tabText.setTextColor(Utils.getApp().getResources().getColor(R.color.black_222628));
                    if (!totalDevice.equals("")) tabText.setText(totalDevice);
                    imageView.setBackgroundResource(0);
                }
            } else {
                tabText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//??????
                if (i == 0) {
                    if (!totalComment.equals("")) tabText.setText(totalComment);
                    imageView.setBackgroundResource(R.drawable.layer_tab_indicator);
                } else {
                    tabText.setTextColor(Utils.getApp().getResources().getColor(R.color.black_222628));
                    if (!totalDevice.equals("")) tabText.setText(totalDevice);
                    imageView.setBackgroundResource(0);
                }
            }


            tab.setCustomView(view);//??????????????????View
            tabLayout.addTab(tab, i);
        }
    }

}

package com.diai.reputation;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int  numberOfTabs;
    public PagerAdapter(FragmentManager fm,int numberOfT) {
        super(fm);
        numberOfTabs=numberOfT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                User tab1=new User();
                return tab1;
            case 1:
                Worker tab2=new Worker();
                return tab2;
            case 2:
                Company tab3=new Company();
                return tab3;
        }
        return null;
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}

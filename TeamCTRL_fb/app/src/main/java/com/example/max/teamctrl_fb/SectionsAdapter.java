package com.example.max.teamctrl_fb;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by max_1_000 on 5-10-2016.
 */

public class SectionsAdapter extends FragmentPagerAdapter {

    //This adapter is used to manage the different sections
    public SectionsAdapter(FragmentManager fm) {
        super(fm);
    }


    //This method decides which Fragment to load based on selected tab
    @Override
    public Fragment getItem(int position) {

        switch (position){

            //The first tab
            case 0:
                return new MatchesFragment();

            case 1:
                return new PlayersFragment();

        }
        return null;
    }

    //The amount of tabs.
    @Override
    public int getCount() {
        return 2;
    }

    //Declares the titles displayed in the various tabs
    public CharSequence getPageTitle(int position) {

        switch (position){

            //The first tab
            case 0:
                return "Matches";

            case 1:
                return "All Players";

        }

        return null;
    }

}
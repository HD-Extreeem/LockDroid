package com.hellomicke89gmail.projektsmartlock;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HadiDeknache on 16-04-25.
 * Author
 * Klassen hanterar dem olika fragmentbyten och items i fragmenten
 * Byter vy när man swipear till den ena fragmentet
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private final ArrayList<Fragment> fragments = new ArrayList<>();

    /**
     * @param manager håller reda på dem olika items i ett fragment
     */
    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    /**
     *
     * @param position positionen på ett item i listan
     * @return items position i listan
     */
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    /**
     * Hämtar antalet element i lista
     * @return antalet element i listan
     */
    @Override
    public int getCount() {
        return fragments.size();
    }

    /**
     * lägger till ett fragmen
     * @param fragment fragmentet som skall läggas till
     */
    public void addFragment(Fragment fragment) {
        fragments.add(fragment);
    }

}



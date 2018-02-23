package fp2.extensioncontrol;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;


public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    private final FragmentManager mFragmentManager;

    public ViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        mFragmentManager = fragmentManager;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFrag(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    public Fragment getFragment(String title) {
        return mFragmentList.get(mFragmentTitleList.indexOf(title));
    }

    public void removeFragment(int position) {
        // Fragment must be removed from the manager to get the correct order of fragments while
        // switching tabs.
        List<Fragment> fragments = mFragmentManager.getFragments();
        fragments.remove(mFragmentList.get(position));

        mFragmentList.remove(position);
        mFragmentTitleList.remove(position);
    }
}

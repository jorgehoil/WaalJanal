package com.carloshoil.waaljanal.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class ViewPager2Adapter extends FragmentStateAdapter {

    private List<Fragment> fragments;

    public ViewPager2Adapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public ViewPager2Adapter(@NonNull Fragment fragment) {
        super(fragment);
    }



    public void setFragments(List<Fragment> fragments)
    {
        this.fragments=fragments;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }
    public Fragment obtenerFragment(int iPosition)
    {
        return fragments.get(iPosition);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }
}

package com.gmijo.mytour.ui.leaderboard;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.gmijo.mytour.R;
import com.gmijo.mytour.database.SQLiteDataHelper;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;



public class LeaderboardFragment extends Fragment {

    //Definisanje elemenata
    TabLayout tabNav;
    ViewPager2 tabLayout;
    FragmentAdapter fragmentAdapter;
    View view;
    SQLiteDataHelper liteDataHelper;
    FirebaseFirestore firebaseFirestore;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_leaderborad, container, false);

        tabNav = (TabLayout) view.findViewById(R.id.tabNav);
        tabLayout = (ViewPager2) view.findViewById(R.id.tabLayout);

        liteDataHelper = new SQLiteDataHelper(getActivity());
        firebaseFirestore = FirebaseFirestore.getInstance();


        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentAdapter = new FragmentAdapter(fragmentManager, getLifecycle());
        tabLayout.setAdapter(fragmentAdapter);

        //Dodavanje elemenata u tab nav
        tabNav.addTab(tabNav.newTab().setIcon(R.drawable.token_icon));
        tabNav.addTab(tabNav.newTab().setIcon(R.drawable.city_icon));
        tabNav.addTab(tabNav.newTab().setIcon(R.drawable.village_icon));
        tabNav.addTab(tabNav.newTab().setIcon(R.drawable.nacionalpark_icon));
        tabNav.addTab(tabNav.newTab().setIcon(R.drawable.naturepoint_icon));


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        getActivity().findViewById(R.id.loaderBox).setVisibility(View.GONE);
        getActivity().findViewById(R.id.fragmentProgBar).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.errImg).setVisibility(View.GONE);
        getActivity().findViewById(R.id.loaderError).setVisibility(View.GONE);

        tabNav.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabLayout.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabNav.selectTab(tabNav.getTabAt(position));
            }
        });


    }






    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
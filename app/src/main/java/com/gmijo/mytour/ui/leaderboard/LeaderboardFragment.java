package com.gmijo.mytour.ui.leaderboard;


import static com.gmijo.mytour.database.SQLiteController.TAB_CITY_STATS;
import static com.gmijo.mytour.database.SQLiteController.TAB_NPARK_STATS;
import static com.gmijo.mytour.database.SQLiteController.TAB_NPOINT_STATS;
import static com.gmijo.mytour.database.SQLiteController.TAB_TOKEN_STATS;
import static com.gmijo.mytour.database.SQLiteController.TAB_VILLAGE_STATS;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.gmijo.mytour.R;
import com.gmijo.mytour.database.SQLiteDataHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class LeaderboardFragment extends Fragment {

    //Definisanje elemenata
    TabLayout tabNav;
    ViewPager2 tabLayout;
    FragmentAdapter fragmentAdapter;
    View view;
    SQLiteDataHelper liteDataHelper;
    FirebaseFirestore firebaseFirestore;
    SwipeRefreshLayout swipeRefreshLayout;
    FragmentManager fragmentManager;
    Fragment ctx;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_leaderborad, container, false);

        //Inicijalizacija elemenata
        tabNav = (TabLayout) view.findViewById(R.id.tabNav);
        tabLayout = (ViewPager2) view.findViewById(R.id.tabLayout);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.leaderboardRefresh);

        liteDataHelper = new SQLiteDataHelper(getActivity());
        firebaseFirestore = FirebaseFirestore.getInstance();

        //Podešavanje i dobavljanje fragment menagera
       fragmentManager = getParentFragmentManager();
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

        //Lisener na promjenu tabova
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

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //Odabir kolekcije u firestore-u (bazi)
                CollectionReference usersRef = firebaseFirestore.collection("users");

                //Query komande
                Query nparkQuery = usersRef.orderBy("achievementData.nationalParkExplored", Query.Direction.DESCENDING).limit(15);
                Query npointQuery = usersRef.orderBy("achievementData.naturepointExplored", Query.Direction.DESCENDING).limit(15);
                Query villageQuery = usersRef.orderBy("achievementData.villageExplored", Query.Direction.DESCENDING).limit(15);
                Query tokenQuery = usersRef.orderBy("achievementData.myTourTokens", Query.Direction.DESCENDING).limit(15);
                Query cityQuery = usersRef.orderBy("achievementData.cityExplored", Query.Direction.DESCENDING).limit(15);

                Thread firebaseThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                         tokenQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                             @Override
                             public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                 if (task.isSuccessful()){
                                     int i = 1;
                                     for (QueryDocumentSnapshot document : task.getResult()) {
                                         try {

                                             //Ubacuje podatke u SQL lite tabelu
                                             liteDataHelper.updateData(TAB_TOKEN_STATS, document.get("personalData.Username").toString(), document.get("achievementData.myTourTokens").toString(), i);

                                                i++;
                                         } catch (Exception e) {

                                             //Došlo je do pogreške, pirkaz errora
                                             showError();
                                             break;

                                         }
                                         //Završen loop kroz rezultate iz taska
                                         if (i == task.getResult().size()+1){
                                             cityQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                 @Override
                                                 public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                     if (task.isSuccessful()){
                                                         int i = 1;
                                                         for (QueryDocumentSnapshot document : task.getResult()) {
                                                             try {

                                                                 //Ubacuje podatke u SQL lite tabelu
                                                                 liteDataHelper.updateData(TAB_CITY_STATS, document.get("personalData.Username").toString(), document.get("achievementData.cityExplored").toString(), i);

                                                                 i++;
                                                             } catch (Exception e) {

                                                                 //Došlo je do pogreške, pirkaz errora
                                                                 showError();
                                                                 break;

                                                             }
                                                             //Završen loop kroz rezultate iz taska
                                                             if (i == task.getResult().size()+1){
                                                                 villageQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                     @Override
                                                                     public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                         if (task.isSuccessful()){
                                                                             int i = 1;
                                                                             for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                 try {

                                                                                     //Ubacuje podatke u SQL lite tabelu
                                                                                     liteDataHelper.updateData(TAB_VILLAGE_STATS, document.get("personalData.Username").toString(), document.get("achievementData.villageExplored").toString(), i);

                                                                                     i++;
                                                                                 } catch (Exception e) {

                                                                                     //Došlo je do pogreške, pirkaz errora
                                                                                     showError();
                                                                                     break;

                                                                                 }
                                                                                 //Završen loop kroz rezultate iz taska
                                                                                 if (i == task.getResult().size()+1){
                                                                                     nparkQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                         @Override
                                                                                         public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                             if (task.isSuccessful()){
                                                                                                 int i = 1;
                                                                                                 for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                                     try {

                                                                                                         //Ubacuje podatke u SQL lite tabelu
                                                                                                         liteDataHelper.updateData(TAB_NPARK_STATS, document.get("personalData.Username").toString(), document.get("achievementData.nationalParkExplored").toString(), i);

                                                                                                         i++;
                                                                                                     } catch (Exception e) {

                                                                                                         //Došlo je do pogreške, pirkaz errora
                                                                                                         showError();
                                                                                                         break;

                                                                                                     }
                                                                                                     //Završen loop kroz rezultate iz taska
                                                                                                     if (i == task.getResult().size()+1){
                                                                                                         npointQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                                             @Override
                                                                                                             public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                                                 if (task.isSuccessful()){
                                                                                                                     int i = 1;
                                                                                                                     for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                                                         try {

                                                                                                                             //Ubacuje podatke u SQL lite tabelu
                                                                                                                             liteDataHelper.updateData(TAB_NPOINT_STATS, document.get("personalData.Username").toString(), document.get("achievementData.naturepointExplored").toString(), i);

                                                                                                                             i++;
                                                                                                                         } catch (Exception e) {

                                                                                                                             //Došlo je do pogreške, pirkaz errora
                                                                                                                             showError();
                                                                                                                             break;

                                                                                                                         }
                                                                                                                         //Završen loop kroz rezultate iz taska
                                                                                                                         if (i == task.getResult().size()+1){

                                                                                                                             new Handler().postDelayed(new Runnable() {
                                                                                                                                 @Override
                                                                                                                                 public void run() {
                                                                                                                                   //TODO, reload fragmenta
                                                                                                                                 }
                                                                                                                             }, 2000);
                                                                                                                         }
                                                                                                                     }
                                                                                                                 }else {
                                                                                                                     swipeRefreshLayout.setRefreshing(false);
                                                                                                                     showError();
                                                                                                                 }
                                                                                                             }
                                                                                                         });
                                                                                                     }
                                                                                                 }
                                                                                             }else {
                                                                                                 swipeRefreshLayout.setRefreshing(false);
                                                                                                 showError();
                                                                                             }
                                                                                         }
                                                                                     });
                                                                                 }
                                                                             }
                                                                         }else {
                                                                             swipeRefreshLayout.setRefreshing(false);
                                                                             showError();
                                                                         }
                                                                     }
                                                                 });
                                                             }
                                                         }
                                                     }else {
                                                         swipeRefreshLayout.setRefreshing(false);
                                                         showError();
                                                     }
                                                 }
                                             });
                                         }
                                     }
                                 }else {
                                     swipeRefreshLayout.setRefreshing(false);
                                     showError();
                                 }
                             }
                         });
                    }
                });
                firebaseThread.start();




            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ctx = this;
    }

    private void showError() {
        Snackbar.make(getView(), R.string.errDataLoading, Snackbar.LENGTH_LONG).setAnchorView(R.id.nav_view).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
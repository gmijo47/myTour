package com.gmijo.mytour.ui.leaderboard;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.gmijo.mytour.ui.leaderboard.nav_fragments.lbCity;
import com.gmijo.mytour.ui.leaderboard.nav_fragments.lbNPark;
import com.gmijo.mytour.ui.leaderboard.nav_fragments.lbNPoint;
import com.gmijo.mytour.ui.leaderboard.nav_fragments.lbTokens;
import com.gmijo.mytour.ui.leaderboard.nav_fragments.lbVillage;

public class FragmentAdapter extends FragmentStateAdapter {
    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override

    //Kreiranje fragmenta na osnovu indexa, odnosno broja njihove pozicije
    public Fragment createFragment(int position) {
        switch (position){
            case 1:{

                //Vraca fragment za top explored gradove
                return new lbCity();

            }
            case 2:{

                //Vraca fragment za top explored sela
                return new lbVillage();

            }
            case 3:{

                //Vraca fragment za top explored nac. parkove
                return new lbNPark();

            }
            case 4:{

                //Vraca fragment za top explored atrakcije
                return new lbNPoint();

            }
        }

        //Vraca fragment za top tokene
        return new lbTokens();

    }

    //Vraca broj tabova
    @Override
    public int getItemCount() {
        return 5;
    }
}

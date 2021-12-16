package com.gmijo.mytour.ui.leaderboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gmijo.mytour.R;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    //Inicijalizacija elemenata
    ArrayList<String> username_list, count_value_list;
    Context context;
    CustomAdapter customAdapter;

    //Prosljeđivanje elemenata klasi iznad
    public CustomAdapter(Context ctx, ArrayList<String> username_list, ArrayList<String> count_value_list){
        this.context = context;
        this.username_list = username_list;
        this.count_value_list = count_value_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_leaderboard_layout, parent, false );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.id.setText(String.valueOf(position));
        holder.username.setText(String.valueOf(username_list.get(position)));
        holder.count.setText(String.valueOf(count_value_list.get(position)));

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //Inicijaliziranje elemenata
        TextView id, username, count;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //Dobavljanje elemenata
            id = itemView.findViewById(R.id.lbNOUser);
            username = itemView.findViewById(R.id.lbUsername);
            count = itemView.findViewById(R.id.lbCount);

        }
    }
}

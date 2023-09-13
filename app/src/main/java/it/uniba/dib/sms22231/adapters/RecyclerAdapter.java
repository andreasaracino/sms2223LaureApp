package it.uniba.dib.sms22231.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.uniba.dib.sms22231.R;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.Viewholder> {
    private final ArrayList<String> titleArrayList;
    private final ArrayList<String> teacherArrayList;
    private final Context context;

    public RecyclerAdapter(ArrayList<String> titleArrayList, ArrayList<String> teacherArrayList, Context context) {
        this.titleArrayList = titleArrayList;
        this.teacherArrayList = teacherArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.Viewholder holder, int position) {
        String title = titleArrayList.get(position);
        String teacher = teacherArrayList.get(position);
        holder.titleText.setText(title);
        holder.teacherText.setText(teacher);
    }

    @Override
    public int getItemCount() {
        return titleArrayList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder{
        private final TextView titleText;
        private final TextView teacherText;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleCardText);
            teacherText = itemView.findViewById(R.id.teacherCardText);
        }
    }
}

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
import it.uniba.dib.sms22231.model.CardData;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.Viewholder> {
    private final ArrayList<CardData> cardData;
    private final Context context;

    public RecyclerAdapter(ArrayList<CardData> cardData, Context context) {
        this.cardData = cardData;
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
        String title = (cardData.get(position)).getTitle();
        String subtitle = (cardData.get(position)).getSubtitle();
        holder.titleText.setText(title);
        holder.subtitleText.setText(subtitle);
    }

    @Override
    public int getItemCount() {
        return cardData.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder{
        private final TextView titleText;
        private final TextView subtitleText;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleCardText);
            subtitleText = itemView.findViewById(R.id.subtitleCardText);
        }
    }
}

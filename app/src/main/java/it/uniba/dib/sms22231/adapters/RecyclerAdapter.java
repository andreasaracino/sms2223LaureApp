package it.uniba.dib.sms22231.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.model.CardData;
import it.uniba.dib.sms22231.utility.RecyclerViewInterface;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.Viewholder> {
    private final ArrayList<CardData> cardData;
    private final Context context;
    private final RecyclerViewInterface recyclerViewInterface;

    public RecyclerAdapter(ArrayList<CardData> cardData, Context context, RecyclerViewInterface recyclerViewInterface) {
        this.cardData = cardData;
        this.context = context;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public RecyclerAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new Viewholder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.Viewholder holder, int position) {
        String title = (cardData.get(position)).getTitle();
        String subtitle = (cardData.get(position)).getSubtitle();
        holder.titleText.setText(title);
        holder.subtitleText.setText(subtitle);
        if (cardData.get(position).getRank()!= null){
            holder.rankText.setText(cardData.get(position).getRank());
            holder.rankText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return cardData.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        private final TextView titleText;
        private final TextView subtitleText;

        private final  TextView rankText;

        public Viewholder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleCardText);
            subtitleText = itemView.findViewById(R.id.subtitleCardText);
            rankText = itemView.findViewById(R.id.rankingText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    view.animate().scaleY(1.1f).setDuration(700).start();
                    view.animate().scaleX(1.1f).setDuration(700).start();
                    return false;
                }
            });
        }
    }
}

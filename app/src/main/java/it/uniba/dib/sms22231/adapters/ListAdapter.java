package it.uniba.dib.sms22231.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.model.ListData;

public class ListAdapter extends ArrayAdapter<ListData> {
    public ListAdapter (@NonNull Context context, ArrayList<ListData> listDataArrayList){
        super(context, R.layout.custom_listview, listDataArrayList);
    }

    @SuppressLint("ViewHolder")
    @androidx.annotation.NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @androidx.annotation.NonNull ViewGroup parent) {
        ListData listData = getItem(position);
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_listview, parent, false);

        ImageView listImage = convertView.findViewById(R.id.listImage);
        TextView listText = convertView.findViewById(R.id.listText);

        listImage.setImageResource(listData.getImageId());
        listText.setText(listData.getText());

        return convertView;

    }
}

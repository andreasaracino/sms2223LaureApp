package it.uniba.dib.sms22231.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.model.Message;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private List<Message> messageList;
    Context context;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final LinearLayout linearLayout;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            textView = (TextView) view.findViewById(R.id.messageText);
            linearLayout = (LinearLayout) view.findViewById(R.id.messageItem);
        }

        public TextView getTextView() {
            return textView;
        }

        public LinearLayout getLinearLayout() {
            return linearLayout;
        }
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param messageList String[] containing the data to populate views to be used
     * by RecyclerView
     */
    public MessagesAdapter(List<Message> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
    }

    public void setMessages(List<Message> messageList) {
        this.messageList = messageList;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.message_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        TextView textView = viewHolder.getTextView();

        Message message = messageList.get(position);

        textView.setText(message.text);

        LinearLayout.LayoutParams textLayoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();

        if (message.sent) {
            textView.setBackgroundColor(ContextCompat.getColor(context, R.color.indigo_500));
            textView.setTextColor(Color.WHITE);
            textLayoutParams.setMargins(64, position > 0 && messageList.get(position - 1).sent ? 8 : 64, 0, 0);
            textLayoutParams.gravity = Gravity.END;
        } else {
            textView.setBackgroundColor(Color.LTGRAY);
            textView.setTextColor(Color.BLACK);
            textLayoutParams.setMargins(0, position > 0 && !messageList.get(position - 1).sent ? 8 : 64, 64, 0);
            textLayoutParams.gravity = Gravity.START;
        }

        textView.setLayoutParams(textLayoutParams);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return messageList.size();
    }
}


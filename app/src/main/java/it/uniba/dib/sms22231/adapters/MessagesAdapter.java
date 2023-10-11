package it.uniba.dib.sms22231.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

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

        textView.setBackground(getBgDrawable(message, position));

        if (message.senderUID == null) {
            String currentLang = Locale.getDefault().getLanguage();
            try {
                JSONObject jsonObject = new JSONObject(message.text);
                if (!jsonObject.has(currentLang)) {
                    currentLang = "en";
                }

                textView.setText(jsonObject.getString(currentLang));
                textView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.indigo_100)));
                textView.setTextColor(Color.BLACK);
                textLayoutParams.setMargins(64, 64, 64, 0);
                textLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            } catch (JSONException e) {}
        } else {
            if (message.sent) {
                textView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.color_primary)));
                textView.setTextColor(Color.WHITE);
                textLayoutParams.setMargins(128, position > 0 && messageList.get(position - 1).sent ? 4 : 32, 0, 0);
                textLayoutParams.gravity = Gravity.END;
            } else {
                textView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.teal_700)));
                textView.setTextColor(Color.WHITE);
                textLayoutParams.setMargins(0, position > 0 && !messageList.get(position - 1).sent ? 4 : 32, 128, 0);
                textLayoutParams.gravity = Gravity.START;
            }
        }

        textView.setLayoutParams(textLayoutParams);
    }

    private Drawable getBgDrawable(Message message, int position) {
        int resId;

        if (messageList.size() == 1 || message.senderUID == null) {
            resId = R.drawable.message_all_round;
        } else if (position == 0) {
            if (message.sent && messageList.get(position + 1).sent) {
                resId = R.drawable.message_top_round_s;
            } else if (!message.sent && !messageList.get(position + 1).sent) {
                resId = R.drawable.message_top_round;
            } else {
                resId = R.drawable.message_all_round;
            }
        } else if (position == messageList.size() - 1) {
            if (message.sent && messageList.get(position - 1).sent) {
                resId = R.drawable.message_bottom_round_s;
            } else if (!message.sent && !messageList.get(position - 1).sent) {
                resId = R.drawable.message_bottom_round;
            } else {
                resId = R.drawable.message_all_round;
            }
        } else {
            if (message.sent && messageList.get(position - 1).sent && messageList.get(position + 1).sent) {
                resId = R.drawable.message_square_s;
            } else if (!message.sent && !messageList.get(position - 1).sent && !messageList.get(position + 1).sent) {
                resId = R.drawable.message_square;
            } else if (message.sent && messageList.get(position + 1).sent && !messageList.get(position - 1).sent) {
                resId = R.drawable.message_top_round_s;
            } else if (!message.sent && !messageList.get(position + 1).sent && messageList.get(position - 1).sent) {
                resId = R.drawable.message_top_round;
            } else if (message.sent && !messageList.get(position + 1).sent && messageList.get(position - 1).sent) {
                resId = R.drawable.message_bottom_round_s;
            } else if (!message.sent && messageList.get(position + 1).sent && !messageList.get(position - 1).sent) {
                resId = R.drawable.message_bottom_round;
            } else {
                resId = R.drawable.message_all_round;
            }
        }

        return context.getDrawable(resId);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return messageList.size();
    }
}


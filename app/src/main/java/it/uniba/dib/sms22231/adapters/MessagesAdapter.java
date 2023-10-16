package it.uniba.dib.sms22231.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.model.Message;
import it.uniba.dib.sms22231.utility.ResUtils;
import it.uniba.dib.sms22231.utility.TimeUtils;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private final ResUtils resUtils = ResUtils.getInstance();
    private List<Message> messageList;
    Context context;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView messageTextView;
        private final TextView dateTextView;
        private final LinearLayout messageContainer;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            messageTextView = (TextView) view.findViewById(R.id.messageText);
            dateTextView = (TextView) view.findViewById(R.id.dateText);
            messageContainer = (LinearLayout) view.findViewById(R.id.messageContainer);
        }

        public TextView getMessageTextView() {
            return messageTextView;
        }

        public TextView getDateTextView() {
            return dateTextView;
        }

        public LinearLayout getMessageContainer() {
            return messageContainer;
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
        TextView messageTextView = viewHolder.getMessageTextView();
        TextView dateTextView = viewHolder.getDateTextView();
        LinearLayout messageContainer = viewHolder.getMessageContainer();

        Message message = messageList.get(position);

        messageTextView.setText(message.text);

        LinearLayout.LayoutParams textLayoutParams = (LinearLayout.LayoutParams) messageContainer.getLayoutParams();

        messageContainer.setBackground(getBgDrawable(message, position));

        messageContainer.setOrientation(LinearLayout.VERTICAL);
        if (message.senderUID == null) {
            String currentLang = Locale.getDefault().getLanguage();
            try {
                JSONObject jsonObject = new JSONObject(message.text);
                if (!jsonObject.has(currentLang)) {
                    currentLang = "en";
                }

                messageTextView.setText(jsonObject.getString(currentLang));
                messageTextView.setTextColor(Color.BLACK);
                messageContainer.setBackgroundTintList(resUtils.getColorStateList(R.color.indigo_100));
                dateTextView.setText(TimeUtils.getTodayTimeFromDate(message.dateSent));
                dateTextView.setTextColor(resUtils.getColor(R.color.black));
                textLayoutParams.setMargins(64, 64, 64, 0);
                textLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            } catch (JSONException e) {}
        } else {
            if (message.sent) {
                messageTextView.setTextColor(Color.WHITE);
                messageContainer.setBackgroundTintList(resUtils.getColorStateList(R.color.color_primary));
                messageContainer.setGravity(Gravity.END);
                dateTextView.setText(TimeUtils.getTodayTimeFromDate(message.dateSent));
                dateTextView.setTextColor(resUtils.getColor(R.color.indigo_100));
                textLayoutParams.setMargins(128, position > 0 && messageList.get(position - 1).sent ? 4 : 32, 0, 0);
                textLayoutParams.gravity = Gravity.END;
            } else {
                messageTextView.setTextColor(Color.WHITE);
                messageContainer.setBackgroundTintList(resUtils.getColorStateList(R.color.teal_700));
                messageContainer.setGravity(Gravity.START);
                dateTextView.setTextColor(resUtils.getColor(R.color.teal_100));
                textLayoutParams.setMargins(0, position > 0 && !messageList.get(position - 1).sent ? 4 : 32, 128, 0);
                textLayoutParams.gravity = Gravity.START;

            }
        }

        messageContainer.setLayoutParams(textLayoutParams);
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


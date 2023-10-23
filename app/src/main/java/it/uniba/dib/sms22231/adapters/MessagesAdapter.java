package it.uniba.dib.sms22231.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import it.uniba.dib.sms22231.model.MessageReference;
import it.uniba.dib.sms22231.utility.CallbackFunction;
import it.uniba.dib.sms22231.utility.ResUtils;
import it.uniba.dib.sms22231.utility.TimeUtils;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private final ResUtils resUtils = ResUtils.getInstance();
    private List<Message> messageList;
    private final CallbackFunction<MessageReference> goToReference;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView messageDateText;
        private final TextView messageTextView;
        private final TextView dateTextView;
        private final TextView chatReferenceMessage;
        private final LinearLayout messageContainer;
        private final LinearLayout dateContainer;
        private final LinearLayout unreadContainer;
        private final LinearLayout messageReferenceContainer;
        private final ImageButton goToReferenceButton;

        public ViewHolder(View view) {
            super(view);

            messageDateText = view.findViewById(R.id.messageDateText);
            messageTextView = (TextView) view.findViewById(R.id.messageText);
            dateTextView = (TextView) view.findViewById(R.id.dateText);
            chatReferenceMessage = view.findViewById(R.id.chatReferenceMessage);
            messageContainer = (LinearLayout) view.findViewById(R.id.messageContainer);
            dateContainer = (LinearLayout) view.findViewById(R.id.dateContainer);
            unreadContainer = (LinearLayout) view.findViewById(R.id.unreadContainer);
            messageReferenceContainer = view.findViewById(R.id.messageReferenceContainer);
            goToReferenceButton = view.findViewById(R.id.goToReference);
        }

        public TextView getMessageDateText() {
            return messageDateText;
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

        public LinearLayout getDateContainer() {
            return dateContainer;
        }

        public LinearLayout getUnreadContainer() {
            return unreadContainer;
        }

        public LinearLayout getMessageReferenceContainer() {
            return messageReferenceContainer;
        }

        public TextView getChatReferenceMessage() {
            return chatReferenceMessage;
        }

        public ImageButton getGoToReferenceButton() {
            return goToReferenceButton;
        }
    }

    public MessagesAdapter(List<Message> messageList, Context context, CallbackFunction<MessageReference> goToReference) {
        this.messageList = messageList;
        this.context = context;
        this.goToReference = goToReference;
    }

    public void setMessages(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.message_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        TextView messageDateText = viewHolder.getMessageDateText();
        TextView messageTextView = viewHolder.getMessageTextView();
        TextView dateTextView = viewHolder.getDateTextView();
        TextView chatReferenceMessage = viewHolder.getChatReferenceMessage();
        LinearLayout messageContainer = viewHolder.getMessageContainer();
        LinearLayout dateContainer = viewHolder.getDateContainer();
        LinearLayout unreadContainer = viewHolder.getUnreadContainer();
        LinearLayout messageReferenceContainer = viewHolder.getMessageReferenceContainer();
        ImageButton goToReferenceButton = viewHolder.getGoToReferenceButton();

        Message message = messageList.get(position);
        Message prevMessage = null;
        Message nextMessage = null;

        if (position > 0)
            prevMessage = messageList.get(position - 1);

        if (position < messageList.size() - 1)
            nextMessage = messageList.get(position + 1);


        messageTextView.setText(message.text);

        LinearLayout.LayoutParams textLayoutParams = (LinearLayout.LayoutParams) messageContainer.getLayoutParams();
        LinearLayout.LayoutParams dateTextParams = (LinearLayout.LayoutParams) dateTextView.getLayoutParams();

        messageContainer.setBackground(getBgDrawable(message, position, nextMessage, prevMessage));

        // Se il messaggio, rispetto a quello precedente, è stato inviato in un giorno diverso, viene visualizzata la data prima dello stesso
        if (prevMessage == null || !TimeUtils.areDatesSameDay(message.dateSent, prevMessage.dateSent)) {
            dateContainer.setVisibility(View.VISIBLE);
            messageDateText.setText(TimeUtils.getTimeFromDate(message.dateSent, false));
        } else {
            dateContainer.setVisibility(View.GONE);
        }

        // Se il messaggio non è stato letto viene mostrata una riga che indica ciò
        if (!message.sent && !message.read && (prevMessage == null || prevMessage.read)) {
            unreadContainer.setVisibility(View.VISIBLE);
        } else {
            unreadContainer.setVisibility(View.GONE);
        }

        // Se non è definito il senderUID allora il messaggio è di servizio
        if (message.senderUID == null) {
            messageContainer.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            messageReferenceContainer.setVisibility(View.GONE);
            messageContainer.setOrientation(LinearLayout.VERTICAL);
            messageContainer.setGravity(Gravity.END);
            textLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            dateTextParams.gravity = Gravity.END | Gravity.BOTTOM;
            messageTextView.setTextColor(Color.BLACK);
            messageContainer.setBackgroundTintList(resUtils.getColorStateList(R.color.indigo_100));
            dateTextView.setText(TimeUtils.getTimeFromDate(message.dateSent, true));
            dateTextView.setTextColor(resUtils.getColor(R.color.black));
            textLayoutParams.setMargins(64, 64, 64, 0);

            // Viene estratto il messaggio corrispondente alla lingua di sistema
            String currentLang = Locale.getDefault().getLanguage();
            try {
                JSONObject jsonObject = new JSONObject(message.text);
                if (!jsonObject.has(currentLang)) {
                    currentLang = "en";
                }

                messageTextView.setText(jsonObject.getString(currentLang));
            } catch (JSONException e) {}
        } else {
            MessageReference messageReference = message.messageReference;

            // Se c'è un riferimento a una tesi o task allora esso viene visualizzato all'interno del messaggio
            if (messageReference != null) {
                messageContainer.setOrientation(LinearLayout.VERTICAL);
                messageReferenceContainer.setVisibility(View.VISIBLE);
                chatReferenceMessage.setText(resUtils.getStringWithParams(messageReference.messageReferenceType.getStringRes(), messageReference.value));
                goToReferenceButton.setOnClickListener((view) -> {
                    goToReference.apply(messageReference);
                });
            } else {
                messageReferenceContainer.setVisibility(View.GONE);

                // Se il messaggio è più corto di 30 caratteri allora il testo e l'orario vengono visualizzati in riga, altrimenti in colonna
                if (message.text.length() <= 30) {
                    messageContainer.setOrientation(LinearLayout.HORIZONTAL);
                    goToReferenceButton.setOnClickListener(null);
                } else {
                    messageContainer.setOrientation(LinearLayout.VERTICAL);
                }
            }

            // Si cambia la posizione e l'aspetto del messaggio sullo schermo in base al mittente dello stesso
            if (message.sent) {
                messageContainer.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                messageTextView.setTextColor(Color.WHITE);
                messageContainer.setBackgroundTintList(resUtils.getColorStateList(R.color.color_primary));
                messageContainer.setGravity(Gravity.END);
                dateTextView.setText(TimeUtils.getTimeFromDate(message.dateSent, true));
                dateTextView.setTextColor(resUtils.getColor(R.color.indigo_100));
                textLayoutParams.setMargins(128, prevMessage != null && prevMessage.sent ? 4 : 32, 0, 0);
                textLayoutParams.gravity = Gravity.END;
            } else {
                messageContainer.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                messageTextView.setTextColor(Color.WHITE);
                messageContainer.setBackgroundTintList(resUtils.getColorStateList(R.color.color_primary_dark));
                messageContainer.setGravity(Gravity.END);
                dateTextView.setText(TimeUtils.getTimeFromDate(message.dateSent, true));
                dateTextView.setTextColor(resUtils.getColor(R.color.indigo_100));
                textLayoutParams.setMargins(0, prevMessage != null && !prevMessage.sent ? 4 : 32, 128, 0);
                textLayoutParams.gravity = Gravity.START;
            }
            dateTextParams.gravity = Gravity.END | Gravity.BOTTOM;
        }

        messageContainer.setLayoutParams(textLayoutParams);
        dateTextView.setLayoutParams(dateTextParams);
    }

    // In base alla posizione del messaggio rispetto a quelli circostanti si mostra uno sfondo di forma diversa
    private Drawable getBgDrawable(Message message, int position, Message nextMessage, Message prevMessage) {
        int resId;

        if (messageList.size() == 1 || message.senderUID == null) {
            resId = R.drawable.message_all_round;
        } else if (position == 0) {
            if (message.sent && nextMessage.sent) {
                resId = R.drawable.message_top_round_s;
            } else if (!message.sent && !nextMessage.sent) {
                resId = R.drawable.message_top_round;
            } else {
                resId = R.drawable.message_all_round;
            }
        } else if (position == messageList.size() - 1) {
            if (message.sent && prevMessage.sent) {
                resId = R.drawable.message_bottom_round_s;
            } else if (!message.sent && !prevMessage.sent) {
                resId = R.drawable.message_bottom_round;
            } else {
                resId = R.drawable.message_all_round;
            }
        } else {
            if (message.sent && prevMessage.sent && nextMessage.sent) {
                resId = R.drawable.message_square_s;
            } else if (!message.sent && !prevMessage.sent && !nextMessage.sent) {
                resId = R.drawable.message_square;
            } else if (message.sent && nextMessage.sent && !prevMessage.sent) {
                resId = R.drawable.message_top_round_s;
            } else if (!message.sent && !nextMessage.sent && prevMessage.sent) {
                resId = R.drawable.message_top_round;
            } else if (message.sent && !nextMessage.sent && prevMessage.sent) {
                resId = R.drawable.message_bottom_round_s;
            } else if (!message.sent && nextMessage.sent && !prevMessage.sent) {
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


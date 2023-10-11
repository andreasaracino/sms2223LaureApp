package it.uniba.dib.sms22231.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.model.Chat;
import it.uniba.dib.sms22231.utility.RecyclerViewInterface;
import it.uniba.dib.sms22231.utility.TimeUtils;

public class ChatElementAdapter extends RecyclerView.Adapter<ChatElementAdapter.ViewHolder> {
    private final Context context;
    private final List<Chat> chatList;
    RecyclerViewInterface recyclerViewInterface;

    public ChatElementAdapter(List<Chat> chatList, Context context, RecyclerViewInterface recyclerViewInterface) {
        this.chatList = chatList;
        this.context = context;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_list_element, parent, false);

        return new ChatElementAdapter.ViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView chatUserIcon = holder.chatUserIcon;
        TextView chatUserName = holder.chatUserName;
        TextView chatLastMessage = holder.chatLastMessage;
        TextView chatUnreadNumber = holder.chatUnreadNumber;
        TextView chatTimeAgo = holder.chatTimeAgo;

        Chat chat = chatList.get(position);
        String userName = chat.userFullName;
        String[] splitName = userName.split(" ");
        String initials = String.valueOf(splitName[0].charAt(0)) + splitName[splitName.length - 1].charAt(0);

        int iconColor = Color.parseColor(context.getResources().getStringArray(R.array.chat_icon_colors)[userName.length() % 10]);

        if (chat.lastMessage != null) {
            String lastMessage = "";

            if (chat.lastMessage.sent) {
                lastMessage += context.getString(R.string.you) + ": ";
            }

            lastMessage += chat.lastMessage.text;
            chatLastMessage.setText(lastMessage);
            chatTimeAgo.setText(TimeUtils.getTimeAgoFromDate(chat.lastMessage.dateSent, context));
        } else {
            chatLastMessage.setVisibility(View.GONE);
        }

        chatUserIcon.setText(initials);
        chatUserIcon.setBackgroundTintList(ColorStateList.valueOf(iconColor));
        chatUserName.setText(userName);
        if (chat.unreadMessages > 0) {
            chatUnreadNumber.setText(String.valueOf(chat.unreadMessages));
            chatUnreadNumber.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView chatUserIcon;
        private final TextView chatUserName;
        private final TextView chatLastMessage;
        private final TextView chatUnreadNumber;
        private final TextView chatTimeAgo;

        public ViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            chatUserIcon = itemView.findViewById(R.id.chatUserIcon);
            chatUserName = itemView.findViewById(R.id.chatUserName);
            chatLastMessage = itemView.findViewById(R.id.chatLastMessage);
            chatUnreadNumber = itemView.findViewById(R.id.chatUnreadNumber);
            chatTimeAgo = itemView.findViewById(R.id.chatTimeAgo);

            itemView.setOnClickListener(view -> {
                if (recyclerViewInterface != null) {
                    int pos = getAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onItemClick(pos);
                    }
                }
            });
        }
    }
}
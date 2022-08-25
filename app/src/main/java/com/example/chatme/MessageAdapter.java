package com.example.chatme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<ChatMe> chatMeList;
    private static final int TYPE_MY_MESSAGE = 0;
    private static final int TYPE_OTHER_MESSAGE = 1;
    public MessageAdapter(List<ChatMe> chatMeList) {
        this.chatMeList = chatMeList;
        notifyDataSetChanged();
    }

    public void setChatMeList(List<ChatMe> chatMeList) {
        this.chatMeList = chatMeList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == TYPE_MY_MESSAGE){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_message, parent, false);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.your_message, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        ChatMe message = chatMeList.get(position);
        if (message.isMine()) {
            return TYPE_MY_MESSAGE;
        } else {
            return TYPE_OTHER_MESSAGE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMe chatMe = chatMeList.get(position);
        boolean isText = chatMe.getImageUrl() == null;
        if(isText){
            holder.imageViewPhoto.setVisibility(View.GONE);
            holder.textViewText.setVisibility(View.VISIBLE);
            holder.textViewText.setText(chatMe.getText());
            holder.textViewName.setText(chatMe.getName());
        }else{
            holder.imageViewPhoto.setVisibility(View.VISIBLE);
            holder.textViewText.setVisibility(View.GONE);
            Glide.with(holder.imageViewPhoto.getContext())
                    .load(chatMe.getImageUrl())
                    .into(holder.imageViewPhoto);
/*            Picasso.get().load(chatMe.getImageUrl()).into(holder.imageViewUrl);*/
            holder.textViewName.setText(chatMe.getName());
        }
    }

    @Override
    public int getItemCount() {
        return chatMeList.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder{

        private TextView textViewText;
        private TextView textViewName;
        private ImageView imageViewPhoto;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewText = itemView.findViewById(R.id.textViewText);
            textViewName = itemView.findViewById(R.id.textViewName);
            imageViewPhoto = itemView.findViewById(R.id.imageViewPhoto);
        }
    }
}

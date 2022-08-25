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

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<Users> usersList;
    private OnClickUserListener onClickUserListener;
    public UserAdapter(List<Users> usersList) {
        this.usersList = usersList;
    }

    public interface OnClickUserListener{
        void onClick(int position);
    }

    public void setOnClickUserListener(OnClickUserListener onClickUserListener) {
        this.onClickUserListener = onClickUserListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_item, parent, false);
        return new UserViewHolder(view, onClickUserListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Users user = usersList.get(position);
        holder.textViewName.setText(user.getName());
        holder.imageView.setImageResource(user.getImgAvatar());
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private TextView textViewName;

        public UserViewHolder(@NonNull View itemView, final OnClickUserListener onClickUserListener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewAvatar);
            textViewName = itemView.findViewById(R.id.textViewName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onClickUserListener != null){
                        onClickUserListener.onClick(getAdapterPosition());
                    }
                }
            });
        }
    }
}

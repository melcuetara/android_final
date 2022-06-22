package com.example.eldroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eldroid.R;
import com.example.eldroid.model.Account;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.ViewHolder> {

    Context context;
    ArrayList<Account> accounts;
    private RecyclerViewClickListener listener;

    public AccountsAdapter(Context context, ArrayList<Account> accounts, RecyclerViewClickListener listener) {
        this.context = context;
        this.accounts = accounts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.account_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String fullName = accounts.get(position).getFirstName() + " " + accounts.get(position).getLastName();
        String age = "" + accounts.get(position).getAge();
        holder.name.setText(fullName);
        holder.email.setText(accounts.get(position).getSex());
        holder.age.setText(age);
        Picasso.get().load(accounts.get(position).getImageUrl()).into(holder.profileImg);
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public ImageView profileImg;
        public TextView name;
        public TextView email;
        public TextView age;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImg = itemView.findViewById(R.id.iv_profile_img);
            name = itemView.findViewById(R.id.tv_name);
            email = itemView.findViewById(R.id.tv_email);
            age = itemView.findViewById(R.id.tv_age);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            listener.onLongClick(v, getAdapterPosition());
            return true;
        }
    }

    public interface RecyclerViewClickListener {
        void onClick(View v, int position);
        void onLongClick(View v, int position);
    }
}

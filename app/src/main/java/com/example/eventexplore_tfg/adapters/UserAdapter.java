package com.example.eventexplore_tfg.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.eventexplore_tfg.Data.User;
import com.example.eventexplore_tfg.R;
import com.example.eventexplore_tfg.activitys.AdminView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private List<User> users;
    private AdminView padre;

    public UserAdapter(List<User> users, AdminView padre) {
        this.users = users;
        this.padre = padre;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gest_user_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);

        holder.deleteButton.setOnClickListener(v -> {
            padre.deleteUser(position, user);
        });
        holder.bindData(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, email;
        FloatingActionButton deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.imagen_Usuario_Admin);
            name = itemView.findViewById(R.id.Nombre_usuario);
            email = itemView.findViewById(R.id.Email_User_gest_user);
            deleteButton = itemView.findViewById(R.id.delete_User_Button);
        }

        public void bindData(final User user) {
            itemView.setOnClickListener(v -> {
                listener.onUserClick(user);
            });
            name.setText(user.getUsername());
            email.setText(user.getEmail());
            // TODO: 06/06/2024 imagen
        }
    }
    private OnUserClickListener listener;

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    public void setOnUserClickListener(OnUserClickListener listener) {
        this.listener = listener;
    }
}

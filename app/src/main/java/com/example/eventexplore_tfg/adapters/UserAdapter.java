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

/**
 * Adapter for displaying a list of users in a RecyclerView.
 *
 * @version 1.0
 * @autor Pablo Esteban Martín
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private List<User> users;
    private AdminView padre;

    /**
     * Constructor for UserAdapter.
     *
     * @param users List of users to display.
     * @param padre Reference to the AdminView activity.
     */
    public UserAdapter(List<User> users, AdminView padre) {
        this.users = users;
        this.padre = padre;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each user item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gest_user_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind data to ViewHolder for each user
        User user = users.get(position);

        // Set click listener for delete button
        holder.deleteButton.setOnClickListener(v -> {
            padre.deleteUser(position, user);
        });
        holder.bindData(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    /**
     * ViewHolder class for individual user items.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, email;
        FloatingActionButton deleteButton;

        /**
         * Constructor for ViewHolder.
         *
         * @param itemView The view of the individual user item.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views from the user item layout
            image = itemView.findViewById(R.id.imagen_Usuario_Admin);
            name = itemView.findViewById(R.id.Nombre_usuario);
            email = itemView.findViewById(R.id.Email_User_gest_user);
            deleteButton = itemView.findViewById(R.id.delete_User_Button);
        }

        /**
         * Bind data to the ViewHolder.
         *
         * @param user The User object containing user data to display.
         */
        public void bindData(final User user) {
            // Set click listener for the entire user item
            itemView.setOnClickListener(v -> {
                listener.onUserClick(user);
            });
            // Set user name and email to respective TextViews
            name.setText(user.getUsername());
            email.setText(user.getEmail());
        }
    }
    private OnUserClickListener listener;

    /**
     * Interface definition for a callback to be invoked when a user is clicked.
     */
    public interface OnUserClickListener {
        /**
         * Called when a user is clicked.
         *
         * @param user The User object representing the clicked user.
         */
        void onUserClick(User user);
    }

    /**
     * Sets the listener for user click events.
     *
     * @param listener The listener to set.
     */
    public void setOnUserClickListener(OnUserClickListener listener) {
        this.listener = listener;
    }
}

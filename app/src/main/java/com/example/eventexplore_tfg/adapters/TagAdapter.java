package com.example.eventexplore_tfg.adapters;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.example.eventexplore_tfg.R;

import java.util.HashMap;
import java.util.List;
/**
 * Adapter for displaying tags in a RecyclerView.
 *
 * @version 1.0
 * @autor Pablo Esteban Martín
 */
public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder> {
    private boolean ischecked = false;
    private List<String> tags;
    private LayoutInflater inflater;
    private RecyclerView padre;
    public HashMap<String, Boolean> selectedStates;

    /**
     * Constructor for TagAdapter.
     *
     * @param tags List of tags to display.
     * @param padre The parent RecyclerView.
     */
    public TagAdapter(List<String> tags, RecyclerView padre) {
        this.tags = tags;
        this.padre = padre;
        this.inflater = LayoutInflater.from(padre.getContext());
        this.selectedStates = new HashMap<>();
        // Initialize selectedStates with all tags initially set to false (not selected)
        for (String i :tags) {
            selectedStates.put(i,false);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each tag item
        View view = inflater.inflate(R.layout.tags_recycler_item, padre, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind data to ViewHolder for each tag
        String string = tags.get(position);
        holder.bindData(string);
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    /**
     * ViewHolder class for individual tag items.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView texto;
        private CardView carta;

        /**
         * Constructor for ViewHolder.
         *
         * @param itemView The view of the individual tag item.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            texto = itemView.findViewById(R.id.textoTag);
            carta = itemView.findViewById(R.id.cartaTag);
        }

        /**
         * Bind data to the ViewHolder.
         *
         * @param item The tag text to display.
         */
        void bindData(final String item) {
            texto.setText(item);
            // Set background color of CardView based on selected state
            if (selectedStates.containsKey(item) && selectedStates.get(item)) {
                carta.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(padre.getContext(), R.color.morado)));
            } else {
                carta.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(padre.getContext(), R.color.md_theme_inversePrimary)));
            }

            // Set click listeners for both TextView and CardView
            texto.setOnClickListener(v -> {
                toggleSelection(item,carta);
                if (listener != null) {
                    listener.onTagClicked(item);
                }
            });

            carta.setOnClickListener(v -> {
                toggleSelection(item,carta);
                if (listener != null) {
                    listener.onTagClicked(item);
                }
            });
        }
    }

    /**
     * Toggle the selection state of a tag and update its appearance.
     *
     * @param item The tag text.
     * @param carta The CardView representing the tag.
     */
    private void toggleSelection(String item, CardView carta) {
        if (selectedStates.containsKey(item)) {
            boolean isSelected = selectedStates.get(item);
            selectedStates.put(item, !isSelected);

            // Update background color of CardView based on new selection state
            if (!isSelected) {
                carta.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(padre.getContext(), R.color.morado)));
            } else {
                carta.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(padre.getContext(), R.color.md_theme_inversePrimary)));
            }
        }
    }

    private OnTagClickListener listener;

    /**
     * Interface definition for a callback to be invoked when a tag is clicked.
     */
    public interface OnTagClickListener {
        /**
         * Called when a tag is clicked.
         *
         * @param item The tag text that was clicked.
         */
        void onTagClicked(String item);
    }

    /**
     * Sets the listener for tag click events.
     *
     * @param listener The listener to set.
     */
    public void setOnTagClickListener(OnTagClickListener listener) {
        this.listener = listener;
    }

}

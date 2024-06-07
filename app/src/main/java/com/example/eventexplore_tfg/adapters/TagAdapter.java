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

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder> {
    private boolean ischecked = false;
    private List<String> tags;
    private LayoutInflater inflater;
    private RecyclerView padre;
    public HashMap<String, Boolean> selectedStates;

    public TagAdapter(List<String> tags, RecyclerView padre) {
        this.tags = tags;
        this.padre = padre;
        this.inflater = LayoutInflater.from(padre.getContext());
        this.selectedStates = new HashMap<>();
        for (String i :tags) {
            selectedStates.put(i,false);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.tags_recycler_item, padre, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String string = tags.get(position);
        holder.bindData(string);
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView texto;
        private CardView carta;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            texto = itemView.findViewById(R.id.textoTag);
            carta = itemView.findViewById(R.id.cartaTag);
        }

        void bindData(final String item) {
            texto.setText(item);
            if (selectedStates.containsKey(item) && selectedStates.get(item)) {
                carta.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(padre.getContext(), R.color.morado)));
            } else {
                carta.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(padre.getContext(), R.color.md_theme_inversePrimary)));
            }

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

    private void toggleSelection(String item, CardView carta) {
        if (selectedStates.containsKey(item)) {
            boolean isSelected = selectedStates.get(item);
            selectedStates.put(item, !isSelected);

            if (!isSelected) {
                carta.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(padre.getContext(), R.color.morado)));
            } else {
                carta.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(padre.getContext(), R.color.md_theme_inversePrimary)));
            }
        }
    }

    private OnTagClickListener listener;

    public interface OnTagClickListener {
        void onTagClicked(String item);
    }

    public void setOnTagClickListener(OnTagClickListener listener) {
        this.listener = listener;
    }

}

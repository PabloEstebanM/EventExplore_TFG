package com.example.eventexplore_tfg.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.eventexplore_tfg.Data.Event;
import com.example.eventexplore_tfg.R;
import com.example.eventexplore_tfg.activitys.CompanyView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class EventsAdapter_Company extends RecyclerView.Adapter<EventsAdapter_Company.ViewHolder> {
    private final List<Event> events;
    private CompanyView padre;

    public EventsAdapter_Company(List<Event> events, CompanyView companyView) {
        this.events = events;
        this.padre = companyView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gest_event_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.deleteButton.setOnClickListener(v -> {
            padre.onClickDelete(position, event);
        });

        holder.bindData(event);

    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, location, ticketsSold;
        ImageView image;
        FloatingActionButton deleteButton;

        ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.Titulo_evento_cliente_Company);
            location = itemView.findViewById(R.id.Ubicacion_evento_cliente);
            ticketsSold = itemView.findViewById(R.id.NVisitasWeb);
            image = itemView.findViewById(R.id.imagen_evento_cliente);
            deleteButton = itemView.findViewById(R.id.btnBorrarEvento_Company);
        }

        public void bindData(final Event event) {
            itemView.setOnClickListener(v -> {
                listener.onEventClick(event);
            });
            title.setText(event.getName());
            location.setText(event.getPlace());
            ticketsSold.setText("Entradas vendidas: " + event.getTicketsSoldNumber());

        }
    }

    private OnEventClickListener listener;

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    public void setOnEventClickListener(OnEventClickListener listener) {
        this.listener = listener;
    }
}

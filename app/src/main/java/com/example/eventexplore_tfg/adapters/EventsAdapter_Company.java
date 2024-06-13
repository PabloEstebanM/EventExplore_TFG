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

/**
 * Adapter for displaying a list of events in the company view.
 *
 * @version 1.0
 * @autor Pablo Esteban Martín
 */
public class EventsAdapter_Company extends RecyclerView.Adapter<EventsAdapter_Company.ViewHolder> {
    private final List<Event> events;
    private CompanyView padre;

    /**
     * Constructor for EventsAdapter_Company.
     *
     * @param events List of events to display.
     * @param companyView The CompanyView instance.
     */
    public EventsAdapter_Company(List<Event> events, CompanyView companyView) {
        this.events = events;
        this.padre = companyView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for each event
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gest_event_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the event for the current position
        Event event = events.get(position);
        // Set click listener for the delete button
        holder.deleteButton.setOnClickListener(v -> {
            padre.onClickDelete(position, event);
        });
// Bind event data to the holder
        holder.bindData(event);

    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    /**
     * ViewHolder class for event items in the company view.
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, location, ticketsSold;
        ImageView image;
        FloatingActionButton deleteButton;

        /**
         * Constructor for ViewHolder.
         *
         * @param itemView The view of the individual item.
         */
        ViewHolder(View itemView) {
            super(itemView);
            // Bind the view elements to their respective fields
            title = itemView.findViewById(R.id.Titulo_evento_cliente_Company);
            location = itemView.findViewById(R.id.Ubicacion_evento_cliente);
            ticketsSold = itemView.findViewById(R.id.NVisitasWeb);
            image = itemView.findViewById(R.id.imagen_evento_cliente);
            deleteButton = itemView.findViewById(R.id.btnBorrarEvento_Company);
        }
        /**
         * Bind event data to the view elements.
         *
         * @param event The event to display.
         */
        public void bindData(final Event event) {
            // Set click listener for the item view
            itemView.setOnClickListener(v -> {
                listener.onEventClick(event);
            });
            // Set the text fields with event data
            title.setText(event.getName());
            location.setText(event.getPlace());
            ticketsSold.setText("Entradas vendidas: " + event.getTicketsSoldNumber());

        }
    }

    private OnEventClickListener listener;

    /**
     * Interface for event click listener.
     */
    public interface OnEventClickListener {
        void onEventClick(Event event);
    }
    /**
     * Set the event click listener.
     *
     * @param listener The listener to set.
     */
    public void setOnEventClickListener(OnEventClickListener listener) {
        this.listener = listener;
    }
}

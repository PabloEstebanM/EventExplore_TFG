package com.example.eventexplore_tfg.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventexplore_tfg.Data.Event;
import com.example.eventexplore_tfg.R;
import com.example.eventexplore_tfg.database.DbManager;

import java.util.List;
/**
 * Adapter for displaying a list of events in the client view.
 *
 * @version 1.0
 * @autor Pablo Esteban Martín
 */
public class EventsAdapter_Client extends RecyclerView.Adapter<EventsAdapter_Client.ViewHolder> {
    private List<Event> events;
    private RecyclerView padre;
    private LayoutInflater inflater;

    /**
     * Constructor for EventsAdapter_Client.
     *
     * @param events List of events to display.
     * @param padre The RecyclerView parent.
     */
    public EventsAdapter_Client(List<Event> events, RecyclerView padre) {
        this.events = events;
        this.padre = padre;
        inflater = LayoutInflater.from(padre.getContext());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for each event
        View view = inflater.inflate(R.layout.client_event_list_item, padre, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the event for the current position
        Event event = events.get(position);
// Set click listener for the buy button
        holder.buyButton.setOnClickListener(v -> {
            DbManager manager = new DbManager(v.getContext());
            SQLiteDatabase db = manager.getWritableDatabase();
            String query = "UPDATE Events SET tickets_sold = tickets_sold + 1 WHERE id = ?";
            SQLiteStatement statement = db.compileStatement(query);
            statement.bindLong(1,Long.parseLong(event.getId()));
            statement.executeUpdateDelete();
            db.close();
            Context context = v.getContext();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.getUrlTicket()));
            context.startActivity(intent);
        });
        // Bind event data to the holder
        holder.bindData(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    /**
     * ViewHolder class for event items in the client view.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView eventTitle, date, place, description;
        private Button buyButton;
        private ImageView image;

        /**
         * Constructor for ViewHolder.
         *
         * @param itemView The view of the individual item.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            eventTitle = itemView.findViewById(R.id.Titulo_evento_cliente);
            date = itemView.findViewById(R.id.FechayHora_evento_cliente);
            place = itemView.findViewById(R.id.Ubicacion_evento_cliente_Client);
            description = itemView.findViewById(R.id.Descripcion_evento_cliente);
            buyButton = itemView.findViewById(R.id.BtnComprar_cliente);
            image = itemView.findViewById(R.id.imagen_evento_cliente);

        }

        /**
         * Bind event data to the view elements.
         *
         * @param event The event to display.
         */
        void bindData(final Event event) {
            // Set click listener for the item view
            itemView.setOnClickListener(v -> {
                listener.onEventClick(event);
            });
            // Set the text fields with event data
            eventTitle.setText(event.getName());
            date.setText(event.getStartDate() +" - " + event.getEndDate());
            place.setText(event.getPlace());
            description.setText(event.getDescription_short());
            buyButton.setText(event.getPrice() + "€");


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

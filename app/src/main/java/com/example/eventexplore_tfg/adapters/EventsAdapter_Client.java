package com.example.eventexplore_tfg.adapters;

import android.content.Context;
import android.content.Intent;
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

import java.util.List;

public class EventsAdapter_Client extends RecyclerView.Adapter<EventsAdapter_Client.ViewHolder> {
    private List<Event> events;
    private RecyclerView padre;
    private LayoutInflater inflater;

    public EventsAdapter_Client(List<Event> events, RecyclerView padre) {
        this.events = events;
        this.padre = padre;
        inflater = LayoutInflater.from(padre.getContext());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.client_event_list_item, padre, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);

        holder.buyButton.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.getUrlTicket()));
            context.startActivity(intent);
        });
        holder.bindData(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView eventTitle, date, place, description;
        private Button buyButton;
        private ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            eventTitle = itemView.findViewById(R.id.Titulo_evento_cliente);
            date = itemView.findViewById(R.id.FechayHora_evento_cliente);
            place = itemView.findViewById(R.id.Ubicacion_evento_cliente_Client);
            description = itemView.findViewById(R.id.Descripcion_evento_cliente);
            buyButton = itemView.findViewById(R.id.BtnComprar_cliente);
            image = itemView.findViewById(R.id.imagen_evento_cliente);

        }

        void bindData(final Event event) {
            itemView.setOnClickListener(v -> {
                listener.onEventClick(event);
            });
            eventTitle.setText(event.getName());
            date.setText(event.getStartDate() +" - " + event.getEndDate());
            place.setText(event.getPlace());
            description.setText(event.getDescription_short());
            buyButton.setText(event.getPrice() + "€");


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

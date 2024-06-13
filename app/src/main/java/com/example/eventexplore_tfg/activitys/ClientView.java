 package com.example.eventexplore_tfg.activitys;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;


import com.example.eventexplore_tfg.Data.Event;
import com.example.eventexplore_tfg.Data.User;
import com.example.eventexplore_tfg.R;
import com.example.eventexplore_tfg.adapters.EventsAdapter_Client;
import com.example.eventexplore_tfg.adapters.TagAdapter;
import com.example.eventexplore_tfg.database.DbManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientView extends AppCompatActivity implements TagAdapter.OnTagClickListener, EventsAdapter_Client.OnEventClickListener {
    private User userLogged;
    private List<String> tags, tagsFiltered;
    private RecyclerView tagsRecycler, eventsRecycler;
    private List<Event> totalEvents, filteredEvents;
    private EventsAdapter_Client eventsAdapter, eventsAdapterFiltered;
    private TagAdapter tagAdapter;
    private SearchView searchView;
    private SearchBar searchBar;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_view);

        Intent intent = getIntent();
        userLogged = (User) intent.getSerializableExtra("usuario");

        // TODO: 03/06/2024 que recoja las categorías de la base de datos
        tags = Arrays.asList(getResources().getStringArray(R.array.event_tags));
        tagsFiltered = new ArrayList<>();
        tagsRecycler = findViewById(R.id.recyclerTags);
        tagAdapter = new TagAdapter(tags, tagsRecycler);
        tagAdapter.setOnTagClickListener(this);
        tagsRecycler.setAdapter(tagAdapter);

        totalEvents = getevents();
        eventsRecycler = findViewById(R.id.recyclerEvents_Client);
        eventsAdapter = new EventsAdapter_Client(totalEvents, eventsRecycler);
        eventsAdapter.setOnEventClickListener(this);
        eventsRecycler.setAdapter(eventsAdapter);

        searchView = findViewById(R.id.searchview_cliente);
        searchBar = findViewById(R.id.search_bar_client);
        searchView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarEventos();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Menu menu = searchBar.getMenu();

        menu.findItem(R.id.cerrar_sesion_meu_item).setOnMenuItemClickListener(item -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Cerrar Sesión")
                    .setMessage("Confirmar cerrar sesión")
                    .setPositiveButton("Cerrar sesión", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.dismiss();
                        }
                    })

                    .show();
            return true;
        });
    }

    @Override
    public void onTagClicked(String tag) {
       tagsFiltered.clear();
        for (String string:tags) {
            System.out.println(tagAdapter.selectedStates.get(string));
            if (tagAdapter.selectedStates.get(string)){
                tagsFiltered.add(string);
            }
        }
        filtrarEventos();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filtrarEventos() {
        if (filteredEvents == null) {
            filteredEvents = new ArrayList<>();
            eventsAdapterFiltered = new EventsAdapter_Client(filteredEvents, eventsRecycler);
            eventsAdapterFiltered.setOnEventClickListener(this);
        } else {
            filteredEvents.clear();
        }

        String searchString = searchView.getEditText().getText().toString().trim();
        for (Event evento : totalEvents) {
            boolean coincide = false;

            for (String categoria : tagsFiltered) {
                if (!evento.getCategories().isEmpty()) {
                    if (evento.getCategories().contains(categoria)) {
                        if (searchString != null) {
                            if (evento.getName().contains(searchString.trim())) {
                                coincide = true;
                            }
                        } else {
                            coincide = true;
                        }
                        break;
                    }
                }
            }

            if (tagsFiltered.size() <= 0 && !searchString.trim().isEmpty()) {
                if (evento.getName().contains(searchString)) coincide = true;
            }

            if (coincide) {
                filteredEvents.add(evento);
            }

        }
        if (tagsFiltered.isEmpty() && searchString.length() <= 0) {
            filteredEvents.addAll(totalEvents);
        }

        eventsRecycler.setAdapter(eventsAdapterFiltered);
        eventsAdapterFiltered.notifyDataSetChanged();
    }

    @Override
    public void onEventClick(Event e) {
        Intent intent = new Intent(this, NewEvent.class);
        intent.putExtra("usuario",userLogged);
        intent.putExtra("evento",e);
        startActivity(intent);
    }

    private List<Event> getevents() {
        List<Event> events = new ArrayList<>();

        DbManager manager = new DbManager(this);
        SQLiteDatabase db = manager.getReadableDatabase();
        String queryEventos = "SELECT " +
                "Events.id, " +
                "Events.name, " +
                "Events.place, " +
                "Events.description_short, " +
                "Events.description_long, " +
                "Events.price, " +
                "Events.startdate, " +
                "Events.enddate, " +
                "Events.tickets_sold, " +
                "Events.urlTicket, " +
                "Users.username, " +
                "Users.email " +
                "FROM Events " +
                "JOIN Users ON Events.id_company = Users.id_user;";
        Cursor cursor = db.rawQuery(queryEventos, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") Event event = new Event(
                        cursor.getString(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getString(cursor.getColumnIndex("place")),
                        cursor.getString(cursor.getColumnIndex("description_long")),
                        cursor.getString(cursor.getColumnIndex("description_short")),
                        cursor.getString(cursor.getColumnIndex("startdate")),
                        cursor.getString(cursor.getColumnIndex("enddate")),
                        cursor.getDouble(cursor.getColumnIndex("price")),
                        cursor.getInt(cursor.getColumnIndex("tickets_sold")),
                        cursor.getString(cursor.getColumnIndex("username")),
                        cursor.getString(cursor.getColumnIndex("email")),
                        cursor.getString(cursor.getColumnIndex("urlTicket"))
                );
                events.add(event);
            } while (cursor.moveToNext());
            cursor.close();
        }
        for (int i = 0; i < events.size(); i++) {
            String queryCategorias = "SELECT Categories.name " +
                    "FROM EventCategories " +
                    "JOIN Categories ON EventCategories.id_category = Categories.id " +
                    "WHERE EventCategories.id_event = ?;";
            Cursor cursor2 = db.rawQuery(queryCategorias, new String[]{events.get(i).getId()});
            List<String> categoriesEvent = new ArrayList<>();
            if (cursor2 != null && cursor2.moveToFirst()) {
                do {
                    categoriesEvent.add(cursor2.getString(0));
                } while (cursor2.moveToNext());
            }
            events.get(i).setCategories(categoriesEvent);
        }
        db.close();
        return events;
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Cerrar Sesión")
                .setMessage("Confirmar cerrar sesión")
                .setPositiveButton("Cerrar sesión", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

}

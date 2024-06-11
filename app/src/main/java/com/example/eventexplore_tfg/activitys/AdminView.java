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
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.eventexplore_tfg.Data.Event;
import com.example.eventexplore_tfg.Data.User;
import com.example.eventexplore_tfg.R;
import com.example.eventexplore_tfg.adapters.EventsAdapter_Admin;
import com.example.eventexplore_tfg.adapters.UserAdapter;
import com.example.eventexplore_tfg.adapters.ViewPagerAdapter_Company;
import com.example.eventexplore_tfg.database.DbManager;
import com.example.eventexplore_tfg.fragments.FragmentListaUsuarios;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class AdminView extends AppCompatActivity {
    private RecyclerView recycler;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private List<User> clients, companies, users;
    private List<Event> events, filtereEvents;
    private User userLogged;
    private DbManager manager;
    private UserAdapter clientsAdapter, companiesAdapter;
    private EventsAdapter_Admin eventsAdapter;
    private FloatingActionButton btnNew;
    private SearchBar searchBar;
    private SearchView searchView;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_view);
        manager = new DbManager(this);

        Intent intent = getIntent();
        userLogged = (User) intent.getSerializableExtra("usuario");
        tabLayout = findViewById(R.id.tab_layout_empresa);
        viewPager = findViewById(R.id.view_pager_empresa);
        btnNew = findViewById(R.id.fab_new_admin);
        recycler = findViewById(R.id.Recycler_Admin);
        searchView = findViewById(R.id.searchview_admin);
        filtereEvents = new ArrayList<>();
        users = getUsers();
        events = getEvents();
        filtereEvents.addAll(events);

        separateUsers();
        manageTabs();
        clientsAdapter = new UserAdapter(clients, this);
        companiesAdapter = new UserAdapter(companies, this);
        eventsAdapter = new EventsAdapter_Admin(filtereEvents, this);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(clientsAdapter);

        eventsAdapter.setOnEventClickListener(event -> {
            Intent i = new Intent(this, NewEvent.class);
            i.putExtra("usuario", userLogged);
            i.putExtra("evento", event);
            startActivity(i);
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getText().toString()) {
                    case "usuarios":
                        btnNew.setVisibility(View.VISIBLE);
                        recycler.setAdapter(clientsAdapter);
                        break;
                    case "empresas":
                        btnNew.setVisibility(View.VISIBLE);
                        recycler.setAdapter(companiesAdapter);
                        break;
                    case "eventos":
                        btnNew.setVisibility(View.GONE);
                        recycler.setAdapter(eventsAdapter);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        btnNew.setOnClickListener(v -> {
            Intent i = new Intent(this, Register.class);
            i.putExtra("usuario", userLogged);
            startActivity(i);
        });

        clientsAdapter.setOnUserClickListener(user -> {
            Intent i = new Intent(this, Register.class);
            i.putExtra("usuario", userLogged);
            i.putExtra("edit", user);
            startActivity(i);
        });

        companiesAdapter.setOnUserClickListener(user -> {
            Intent i = new Intent(this, Register.class);
            i.putExtra("usuario", userLogged);
            i.putExtra("edit", user);
            startActivity(i);
        });

        searchBar = findViewById(R.id.search_bar_admin);

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
        searchView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterData(searchView.getEditText().getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void separateUsers() {
        if (users == null) users = new ArrayList<>();
        if (clients == null) clients = new ArrayList<>();
        if (companies == null) companies = new ArrayList<>();
        for (User user : users) {
            switch (user.getRole().toLowerCase()) {
                case "1":
                    clients.add(user);
                    break;
                case "2":
                    companies.add(user);
                    break;
                case "3":
                    break;
            }
        }
    }

    private void manageTabs() {
        ViewPagerAdapter_Company adapter = new ViewPagerAdapter_Company(this);
        adapter.addFragment(FragmentListaUsuarios.newInstance("a", "b"), "usuarios");
        adapter.addFragment(FragmentListaUsuarios.newInstance("a", "b"), "empresas");
        adapter.addFragment(FragmentListaUsuarios.newInstance("a", "b"), "eventos");
        tabLayout.selectTab(tabLayout.getTabAt(0));
        viewPager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(adapter.getPageTitle(position));
        }).attach();
    }

    @SuppressLint("Range")
    private List<Event> getEvents() {
        List<Event> e = new ArrayList<>();
        String queryEventos = "SELECT e.id, e.name, e.place, e.description_short, e.description_long, e.price, " +
                "e.startdate, e.enddate, e.tickets_sold, e.urlTicket, u.username AS companyName, u.email AS contactEmail " +
                "FROM Events e " +
                "INNER JOIN Users u ON e.id_company = u.id_user;";
        SQLiteDatabase db = manager.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryEventos, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                e.add(new Event(
                        cursor.getString(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getString(cursor.getColumnIndex("place")),
                        cursor.getString(cursor.getColumnIndex("description_long")),
                        cursor.getString(cursor.getColumnIndex("description_short")),
                        cursor.getString(cursor.getColumnIndex("startdate")),
                        cursor.getString(cursor.getColumnIndex("enddate")),
                        cursor.getDouble(cursor.getColumnIndex("price")),
                        cursor.getInt(cursor.getColumnIndex("tickets_sold")),
                        cursor.getString(cursor.getColumnIndex("companyName")),
                        cursor.getString(cursor.getColumnIndex("contactEmail")),
                        cursor.getString(cursor.getColumnIndex("urlTicket"))
                ));
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        db.close();
        return e;
    }

    @SuppressLint("Range")
    private List<User> getUsers() {
        List<User> usuarios = new ArrayList<>();
        String queryUsers = "SELECT * FROM Users";
        SQLiteDatabase db = manager.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryUsers, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                User user = new User(
                        cursor.getString(cursor.getColumnIndex("id_user")),
                        cursor.getString(cursor.getColumnIndex("username")),
                        cursor.getString(cursor.getColumnIndex("rol"))
                );
                user.setEmail(cursor.getString(cursor.getColumnIndex("email")));
                usuarios.add(user);
            } while (cursor.moveToNext());
        }
        return usuarios;
    }

    public void deleteUser(int position, User user) {
        // TODO: 06/06/2024 dialog de confimación
        users.remove(user);
        clients.remove(user);
        companies.remove(user);
        SQLiteDatabase db = manager.getWritableDatabase();
        String deleteUser = "DELETE FROM Users WHERE id_user = ?";
        db.execSQL(deleteUser, new Object[]{user.getId()});
    }

    public void deleteEvent(int position, Event event) {
        // TODO: 09/06/2024 dialog de confirmación
        events.remove(event);
        SQLiteDatabase db = manager.getWritableDatabase();
        String deleteUser = "DELETE FROM Events WHERE id = ?";
        // db.execSQL(deleteUser,new Object[]{event.getId()});
    }

    private void filterData(String query) {
        clients.clear();
        companies.clear();
        filtereEvents.clear();
        if (!query.isEmpty()) {
            if (tabLayout.getSelectedTabPosition() == 0) {
                for (User user : users) {
                    if (user.getRole().equals("1") && user.getUsername().toLowerCase().contains(query.toLowerCase())) {
                        clients.add(user);
                    }
                }

            } else if (tabLayout.getSelectedTabPosition() == 1) {
                for (User user : users) {
                    if (user.getRole().equals("2") && user.getUsername().toLowerCase().contains(query.toLowerCase())) {
                        companies.add(user);
                    }
                }
            } else if (tabLayout.getSelectedTabPosition() == 2) {
                for (Event event : events) {
                    if (event.getName().toLowerCase().contains(query.toLowerCase()) ||
                            event.getDescription_short().toLowerCase().contains(query.toLowerCase()) ||
                            event.getPlace().toLowerCase().contains(query.toLowerCase()) ||
                            event.getStartDate().toLowerCase().contains(query.toLowerCase()) ||
                            event.getEndDate().toLowerCase().contains(query.toLowerCase())) {
                        filtereEvents.add(event);
                    }
                }
            }
        }else {
            separateUsers();
            filtereEvents.addAll(events);
        }

        eventsAdapter.notifyDataSetChanged();
        clientsAdapter.notifyDataSetChanged();
        companiesAdapter.notifyDataSetChanged();
    }
}

package com.example.eventexplore_tfg.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class AdminView extends AppCompatActivity {
    private RecyclerView recycler;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private List<User> clients, companies, users;
    private List<Event> events;
    private User userLogged;
    private DbManager manager;
    private UserAdapter clientsAdapter, companiesAdapter;
    private EventsAdapter_Admin eventsAdapter;
    private FloatingActionButton btnNew;

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
        users = getUsers();
        events = getEvents();

        separateUsers();
        manageTabs();
        clientsAdapter = new UserAdapter(clients, this);
        companiesAdapter = new UserAdapter(companies, this);
        eventsAdapter = new EventsAdapter_Admin(events,this);
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
        String deleteUser = "DELETE FROM User WHERE id = ?";
        db.execSQL(deleteUser,new Object[]{user.getId()});
    }

    public void deleteEvent(int position, Event event) {
        System.out.println("moke deja de gastar dinero");
    }
}

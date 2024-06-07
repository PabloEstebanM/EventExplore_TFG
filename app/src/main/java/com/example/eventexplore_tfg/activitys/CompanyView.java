package com.example.eventexplore_tfg.activitys;

import static java.lang.Long.parseLong;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.eventexplore_tfg.Data.Event;
import com.example.eventexplore_tfg.Data.User;
import com.example.eventexplore_tfg.R;
import com.example.eventexplore_tfg.adapters.EventsAdapter_Company;
import com.example.eventexplore_tfg.adapters.ViewPagerAdapter_Company;
import com.example.eventexplore_tfg.database.DbManager;
import com.example.eventexplore_tfg.fragments.FragmentListaEventos;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CompanyView extends AppCompatActivity {
    private List<Event> totalEvents, endedEvents, nextEvents;
    private static final int NEW_EVENT_REQUEST_CODE = 42;
    private DbManager manager;
    private User userLogged;
    private TabLayout tabLayout;
    private EventsAdapter_Company adapterNext, adapterEnded;
    private RecyclerView recycler;
    private FloatingActionButton newEventBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.company_view);

        Intent intent = getIntent();
        userLogged = (User) intent.getSerializableExtra("usuario");

        manager = new DbManager(this);
        totalEvents = getEvents();

        endedEvents = new ArrayList<>();
        nextEvents = new ArrayList<>();
        tabLayout = findViewById(R.id.tab_layout_empresa);
        recycler = findViewById(R.id.recycler_Company);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        newEventBtn = findViewById(R.id.fab_new_evento);
        separateEvents();
        manageTabs();

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                CharSequence text = tab.getText();
                if (text.equals("Eventos terminados")) {
                    if (adapterEnded == null) {
                        adapterEnded = new EventsAdapter_Company(endedEvents, CompanyView.this);
                    }
                    recycler.setAdapter(adapterEnded);
                } else if (text.equals("Próximos eventos")) {
                    if (adapterNext == null) {
                        adapterNext = new EventsAdapter_Company(nextEvents, CompanyView.this);
                    }
                    recycler.setAdapter(adapterNext);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


        if (adapterEnded == null) {
            adapterEnded = new EventsAdapter_Company(endedEvents, CompanyView.this);
        }


        if (adapterNext == null) {
            adapterNext = new EventsAdapter_Company(nextEvents, CompanyView.this);
        }

        tabLayout.selectTab(tabLayout.getTabAt(0));
        newEventBtn.setOnClickListener(v -> {
            Intent i = new Intent(this, NewEvent.class);
            i.putExtra("usuario", userLogged);
            startActivityForResult(i, NEW_EVENT_REQUEST_CODE);
        });
        adapterNext.setOnEventClickListener(event -> {
            Intent i = new Intent(this, NewEvent.class);
            i.putExtra("usuario", userLogged);
            i.putExtra("evento", event);
            startActivity(i);
        });
        adapterEnded.setOnEventClickListener(event -> {
            Intent i = new Intent(this, NewEvent.class);
            i.putExtra("usuario", userLogged);
            i.putExtra("evento", event);
            startActivity(i);
        });

    }

    public void onClickDelete(int position, Event event) {
        SQLiteDatabase db = manager.getWritableDatabase();
        String deleteQuery = "DELETE FROM Events WHERE id = ?";
        SQLiteStatement statement = db.compileStatement(deleteQuery);
        statement.bindLong(1, parseLong(event.getId()));
        if (statement.executeUpdateDelete() < 1) {
            //fino
        } else {
            //no fino
        }
        db.close();
    }

    private void manageTabs() {
        ViewPager2 viewPager = findViewById(R.id.view_pager_empresa);
        ViewPagerAdapter_Company adapter = new ViewPagerAdapter_Company(this);
        if (nextEvents != null && !nextEvents.isEmpty()) {
            adapter.addFragment(FragmentListaEventos.newInstance(nextEvents), "Próximos eventos");
            if (adapterNext == null) {
                adapterNext = new EventsAdapter_Company(nextEvents, this);
            }
            recycler.setAdapter(adapterNext);
        }
        if (endedEvents != null && !endedEvents.isEmpty()) {
            adapter.addFragment(FragmentListaEventos.newInstance(endedEvents), "Eventos terminados");
            if (adapterEnded == null) {
                adapterEnded = new EventsAdapter_Company(endedEvents, this);
            }
            recycler.setAdapter(adapterEnded);
        }
        if (adapterNext != null) recycler.setAdapter(adapterNext);
        tabLayout.selectTab(tabLayout.getTabAt(0));
        viewPager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(adapter.getPageTitle(position));
        }).attach();

    }

    private void separateEvents() {
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (Event e : totalEvents) {
            try {
                Date endDate = sdf.parse(e.getEndDate());
                if (today.after(endDate)) {
                    endedEvents.add(e);
                } else {
                    nextEvents.add(e);
                }
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        }
    }

    @SuppressLint("Range")
    private List<Event> getEvents() {
        List<Event> e = new ArrayList<>();
        String queryEventos = "SELECT e.id, e.name, e.place, e.description_short, e.description_long, e.price, " +
                "e.startdate, e.enddate, e.tickets_sold, e.urlTicket, u.username AS companyName, u.email AS contactEmail " +
                "FROM Events e " +
                "INNER JOIN Users u ON e.id_company = u.id_user WHERE id_user = ?;";
        SQLiteDatabase db = manager.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryEventos, new String[]{String.valueOf(userLogged.getId())});
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_EVENT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // TODO: 05/06/2024 log en la base de datos y actualizar los adapters
            }
        }
    }
}
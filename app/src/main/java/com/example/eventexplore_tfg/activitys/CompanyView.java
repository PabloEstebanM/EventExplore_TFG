package com.example.eventexplore_tfg.activitys;

import static java.lang.Long.parseLong;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.eventexplore_tfg.Data.Event;
import com.example.eventexplore_tfg.Data.User;
import com.example.eventexplore_tfg.R;
import com.example.eventexplore_tfg.adapters.EventsAdapter_Client;
import com.example.eventexplore_tfg.adapters.EventsAdapter_Company;
import com.example.eventexplore_tfg.adapters.ViewPagerAdapter_Company;
import com.example.eventexplore_tfg.database.DbManager;
import com.example.eventexplore_tfg.fragments.FragmentListaEventos;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * CompanyView activity displays events for a company based on their account.
 * It provides tabs to switch between upcoming and ended events, allows event filtering,
 * and supports event creation and deletion.
 *
 * @version 1.0
 * @autor Pablo Esteban Martín
 */
public class CompanyView extends AppCompatActivity {
    private List<Event> totalEvents, endedEvents, nextEvents;
    private static final int NEW_EVENT_REQUEST_CODE = 42;
    private DbManager manager;
    private User userLogged;
    private TabLayout tabLayout;
    private EventsAdapter_Company adapterNext, adapterEnded;
    private RecyclerView recycler;
    private FloatingActionButton newEventBtn;
    private SearchBar searchBar;
    private SearchView searchView;
    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.company_view);
        // Retrieve user information passed from the previous activity
        Intent intent = getIntent();
        userLogged = (User) intent.getSerializableExtra("usuario");

        // Initialize database manager
        manager = new DbManager(this);

        // Initialize lists to store events
        totalEvents = getEvents();
        endedEvents = new ArrayList<>();
        nextEvents = new ArrayList<>();

        // Initialize UI components.
        tabLayout = findViewById(R.id.tab_layout_empresa);
        recycler = findViewById(R.id.recycler_Company);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        newEventBtn = findViewById(R.id.fab_new_evento);
        searchBar = findViewById(R.id.search_bar_Company);
        searchView = findViewById(R.id.searchview_empresa);

        // Separate events into upcoming and ended categories
        separateEvents();

        // Manage tab selection and setup view pager
        manageTabs();

        // Add listeners to UI elements
        addListeners();
    }

    /**
     * Sets up listeners for UI elements like tabs, search bar, and floating action button.
     */
    private void addListeners() {
        // TabLayout listener to switch between upcoming and ended events
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
        // Ensure adapters are initialized and select the first tab by default
        if (adapterEnded == null) {
            adapterEnded = new EventsAdapter_Company(endedEvents, CompanyView.this);
        }
        if (adapterNext == null) {
            adapterNext = new EventsAdapter_Company(nextEvents, CompanyView.this);
        }
        tabLayout.selectTab(tabLayout.getTabAt(0));
        // Floating action button to create new events
        newEventBtn.setOnClickListener(v -> {
            Intent i = new Intent(this, NewEvent.class);
            i.putExtra("usuario", userLogged);
            startActivityForResult(i, NEW_EVENT_REQUEST_CODE);
        });
        // Click listener for upcoming events adapter
        adapterNext.setOnEventClickListener(event -> {
            Intent i = new Intent(this, NewEvent.class);
            i.putExtra("usuario", userLogged);
            i.putExtra("evento", event);
            startActivity(i);
        });
        // Click listener for ended events adapter
        adapterEnded.setOnEventClickListener(event -> {
            Intent i = new Intent(this, NewEvent.class);
            i.putExtra("usuario", userLogged);
            i.putExtra("evento", event);
            startActivity(i);
        });
        // Menu item listener for logging out
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
        // Search view listener for filtering events
        searchView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterEvents(searchView.getEditText().getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    /**
     * Deletes a Event from the database and updates the list.
     *
     * @param position Position of the user in the list.
     * @param event     The event to be deleted.
     */
    public void onClickDelete(int position, Event event) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Eliminar evento")
                .setMessage("¿Seguro que desea eliminar el evento?")
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
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

    /**
     * Manages tab layout and sets up the view pager.
     */
    private void manageTabs() {
        ViewPager2 viewPager = findViewById(R.id.view_pager_empresa);
        ViewPagerAdapter_Company adapter = new ViewPagerAdapter_Company(this);
        adapter.addFragment(FragmentListaEventos.newInstance(nextEvents), "Próximos eventos");
        if (adapterNext == null) {
            adapterNext = new EventsAdapter_Company(nextEvents, this);
        }
        recycler.setAdapter(adapterNext);
        adapter.addFragment(FragmentListaEventos.newInstance(endedEvents), "Eventos terminados");
        if (adapterEnded == null) {
            adapterEnded = new EventsAdapter_Company(endedEvents, this);
        }
        recycler.setAdapter(adapterEnded);
        if (adapterNext != null) recycler.setAdapter(adapterNext);
        tabLayout.selectTab(tabLayout.getTabAt(0));
        viewPager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(adapter.getPageTitle(position));
        }).attach();

    }

    /**
     * Separates events into upcoming and ended categories based on the current date.
     */
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

    /**
     * Retrieves events for the logged-in user from the database.
     *
     * @return List of events.
     */
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


    /**
     * Handles the result from the NewEvent activity.
     *
     * @param requestCode The request code.
     * @param resultCode The result code.
     * @param data The intent data.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_EVENT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                totalEvents.clear();
                endedEvents.clear();
                nextEvents.clear();

                totalEvents = getEvents();
                separateEvents();
                updateEvents();

            }
        }
    }

    /**
     * Updates the event lists and notifies the adapters.
     */
    @SuppressLint("NotifyDataSetChanged")
    private void updateEvents() {
        adapterEnded.notifyDataSetChanged();
        adapterNext.notifyDataSetChanged();
    }

    /**
     * Filters events based on the search query.
     *
     * @param query The search query.
     */
    private void filterEvents(String query) {
        endedEvents.clear();
        nextEvents.clear();
        String lowerCaseQuery = query.toLowerCase();
        if (!query.isEmpty()) {
            for (Event event : totalEvents) {
                boolean matchesQuery = event.getName().toLowerCase().contains(lowerCaseQuery) ||
                        event.getDescription_short().toLowerCase().contains(lowerCaseQuery) ||
                        event.getPlace().toLowerCase().contains(lowerCaseQuery) ||
                        event.getStartDate().contains(lowerCaseQuery) ||
                        event.getEndDate().contains(lowerCaseQuery);

                if (matchesQuery) {
                    if (tabLayout.getSelectedTabPosition() == 0 && !isEventEnded(event)) {
                        nextEvents.add(event);
                    } else if (tabLayout.getSelectedTabPosition() == 1 && isEventEnded(event)) {
                        endedEvents.add(event);
                    }
                }
            }
        } else {
            separateEvents();
        }

        updateRecyclerView();
    }

    /**
     * Checks if an event has ended based on the current date.
     *
     * @param event The event to check.
     * @return True if the event has ended, false otherwise.
     */
    private boolean isEventEnded(Event event) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date endDate = sdf.parse(event.getEndDate());
            return new Date().after(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates the RecyclerView by notifying the adapters of changes.
     */
    @SuppressLint("NotifyDataSetChanged")
    private void updateRecyclerView() {
        adapterNext.notifyDataSetChanged();
        adapterEnded.notifyDataSetChanged();
    }


    /**
     * Handles the back button press to show a logout confirmation dialog.
     */
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

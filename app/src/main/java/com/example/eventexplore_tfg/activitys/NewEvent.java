package com.example.eventexplore_tfg.activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;


import com.example.eventexplore_tfg.Data.Event;
import com.example.eventexplore_tfg.Data.User;
import com.example.eventexplore_tfg.R;
import com.example.eventexplore_tfg.database.DbManager;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Locale;
/**
 * This activity handles the creation and modification of events.
 * @Author Pablo Esteban Martín
 * @Version 1.0
 */
public class NewEvent extends AppCompatActivity {
    private User userLogged;
    private boolean addMode;
    private Event event;
    private DbManager manager;
    private TextInputLayout eventName, place, descriptionLong, descriptionShort, price, url, startDate, endDate;
    private Button categories, back, saveButton;
    private ExtendedFloatingActionButton buyTicket;
    private ImageView carousel;
    private ToggleButton favToggleButton;
    private String[] totalCategories;
    private boolean[] categoriesSelected;
    private long startDateSelected, endDateSelected;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_event);
        // Initialize the database manager
        manager = new DbManager(this);

        // Initialize the UI components
        eventName = findViewById(R.id.InputEventNameNewEvent);
        place = findViewById(R.id.InputEventUbiNewEvent);
        descriptionLong = findViewById(R.id.InputEventDescriptionLongNewEvent);
        descriptionShort = findViewById(R.id.InputEventDescriptionShortNewEvent);
        price = findViewById(R.id.InputEventPriceNewEvent);
        url = findViewById(R.id.InputEventUrlNewEvent);
        categories = findViewById(R.id.btnSelectCategory);
        back = findViewById(R.id.BtnCancelarNewEvent);
        saveButton = findViewById(R.id.BtnAceptarNewEvent);
        buyTicket = findViewById(R.id.fab_buy_ticket);
        startDate = findViewById(R.id.InputEventStartDate);
        endDate = findViewById(R.id.InputEventEndDate);
        carousel = findViewById(R.id.carousel_NewEvent);
        favToggleButton = findViewById(R.id.favToggleButton);
        totalCategories = getResources().getStringArray(R.array.event_tags);
        categoriesSelected = new boolean[totalCategories.length];

        // Check if we are adding or editing an event
        addMode = true;
        Intent intent = getIntent();
        userLogged = (User) intent.getSerializableExtra("usuario");
        try {
            event = (Event) intent.getSerializableExtra("evento");
        } catch (Exception e) {
            // Handle the exception if any
        }

        // If editing an event, bind event data to the UI
        if (event != null) {
            if (userLogged.getRole().equalsIgnoreCase("client")) disableAll();
            addMode = false;
            bindEventData();
        }
        // Add listeners to UI components
        addListeners();
    }

    /**
     * Adds listeners to UI components for handling user interactions.
     */
    private void addListeners() {
        back.setOnClickListener(v -> finish());
        buyTicket.setOnClickListener(v -> {
            SQLiteDatabase db = manager.getWritableDatabase();
            String query = "UPDATE Events SET tickets_sold = tickets_sold + 1 WHERE id = ?";
            SQLiteStatement statement = db.compileStatement(query);
            statement.bindLong(1, Long.parseLong(event.getId()));
            statement.executeUpdateDelete();
            db.close();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(event.getUrlTicket())));
        });
        favToggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                favToggleButton.setBackgroundResource(R.drawable.corazon_fill);
                addFavorite();
            } else {
                favToggleButton.setBackgroundResource(R.drawable.corazon);
               removeFavorite();
            }
        });


        categories.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose Items");
            builder.setMultiChoiceItems(totalCategories, categoriesSelected, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    categoriesSelected[which] = isChecked;
                }
            });
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        });

        startDate.getEditText().setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDatePicker();
            }
        });

        endDate.getEditText().setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDatePicker();
            }
        });

        saveButton.setOnClickListener(v -> {
            boolean comprobar = checkData();
            if (comprobar) {
                if (addMode) {
                    insertNewEvent();
                } else {
                    modifyEvent();
                }
            }
        });
    }

    /**
     * Modifies an existing event in the database.
     */
    private void modifyEvent() {
        SQLiteDatabase db = manager.getWritableDatabase();
        String updateEvent = "UPDATE Events SET name = ?, place = ?, description_short = ?, description_long = ?, price = ?, startdate = ?, enddate = ?, id_company = ?, urlTicket = ? WHERE id = ?";
        SQLiteStatement statement = db.compileStatement(updateEvent);
        statement.bindString(1, eventName.getEditText().getText().toString());
        statement.bindString(2, place.getEditText().getText().toString());
        statement.bindString(3, descriptionShort.getEditText().getText().toString());
        statement.bindString(4, descriptionLong.getEditText().getText().toString());
        statement.bindDouble(5, Double.valueOf(price.getEditText().getText().toString()));
        statement.bindString(6, startDate.getEditText().getText().toString());
        statement.bindString(7, endDate.getEditText().getText().toString());
        statement.bindLong(8, Long.valueOf(userLogged.getId()));
        statement.bindString(9, url.getEditText().getText().toString());
        statement.bindLong(10, Long.parseLong(event.getId()));
        if (statement.executeInsert() != -1) {
            String deleteCategories = "DELETE FROM EventCategories WHERE id_event = ?";
            db.execSQL(deleteCategories, new Object[]{event.getId()});

            String insertCategoryEvent = "INSERT INTO EventCategories (id_event, id_category) VALUES (?, ?);";
            SQLiteStatement statement2 = db.compileStatement(insertCategoryEvent);
            String idCategoria = "";
            for (int i = 0; i < categoriesSelected.length; i++) {
                if (categoriesSelected[i]) {
                    statement2.clearBindings();
                    String queryIdCategoria = "SELECT id FROM Categories WHERE name = ?;";
                    Cursor cursor2 = db.rawQuery(queryIdCategoria, new String[]{totalCategories[i]});
                    if (cursor2 != null && cursor2.moveToFirst()) {
                        idCategoria = cursor2.getString(0);
                    }
                    statement2.bindLong(1, Long.valueOf(event.getId()));
                    statement2.bindLong(2, Long.valueOf(idCategoria));
                    statement2.executeInsert();
                }
            }

        }
    }

    /**
     * Inserts a new event into the database.
     */
    private void insertNewEvent() {
        SQLiteDatabase db = manager.getWritableDatabase();
        String insertEvent = "INSERT INTO Events (name, place, description_short, description_long, price, startdate, enddate, id_company, urlTicket) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        SQLiteStatement statement = db.compileStatement(insertEvent);
        statement.bindString(1, eventName.getEditText().getText().toString());
        statement.bindString(2, place.getEditText().getText().toString());
        statement.bindString(3, descriptionShort.getEditText().getText().toString());
        statement.bindString(4, descriptionLong.getEditText().getText().toString());
        statement.bindDouble(5, Double.valueOf(price.getEditText().getText().toString()));
        statement.bindString(6, startDate.getEditText().getText().toString());
        statement.bindString(7, endDate.getEditText().getText().toString());
        statement.bindLong(8, Long.valueOf(userLogged.getId()));
        statement.bindString(9, url.getEditText().getText().toString());
        if (statement.executeInsert() != -1) {
            String queryIdEvento = "SELECT MAX(id) FROM Events;";
            Cursor cursor = db.rawQuery(queryIdEvento, null);
            String idEvento = "";
            String idCategoria = "";
            if (cursor != null && cursor.moveToFirst()) {
                idEvento = cursor.getString(0);
            }
            String insertCategoryEvent = "INSERT INTO EventCategories (id_event, id_category) VALUES (?, ?);";
            SQLiteStatement statement2 = db.compileStatement(insertCategoryEvent);
            for (int i = 0; i < categoriesSelected.length; i++) {
                if (categoriesSelected[i]) {
                    statement2.clearBindings();
                    String queryIdCategoria = "SELECT id FROM Categories WHERE name = ?;";
                    Cursor cursor2 = db.rawQuery(queryIdCategoria, new String[]{totalCategories[i]});
                    if (cursor2 != null && cursor2.moveToFirst()) {
                        idCategoria = cursor2.getString(0);
                    }
                    statement2.bindLong(1, Long.valueOf(idEvento));
                    statement2.bindLong(2, Long.valueOf(idCategoria));
                    statement2.executeInsert();
                }
            }
            cursor.close();
            db.close();
            setResult(RESULT_OK);
            finish();
        }
        Snackbar.make(place, "Ha habido un error en la creación de evento", Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Checks if all necessary data is provided.
     * @return true if all required data is present, false otherwise.
     */
    private boolean checkData() {
        if (eventName.getEditText().getText().toString().trim().isEmpty()) return false;
        if (place.getEditText().getText().toString().trim().isEmpty()) return false;
        if (startDate.getEditText().getText().toString().trim().isEmpty()) return false;
        if (endDate.getEditText().getText().toString().trim().isEmpty()) return false;
        if (descriptionLong.getEditText().getText().toString().trim().isEmpty()) return false;
        if (descriptionShort.getEditText().getText().toString().trim().isEmpty()) return false;
        if (price.getEditText().getText().toString().trim().isEmpty()) return false;
        if (url.getEditText().getText().toString().trim().isEmpty()) return false;
        if (categoriesSelected.length == 0) return false;
        boolean hasTrue = false;
        for (boolean bool : categoriesSelected) {
            if (bool) {
                hasTrue = true;
                break;
            }
        }
        if (!hasTrue) return false;
        return true;
    }

    /**
     * Displays a date picker for selecting event dates.
     */
    private void showDatePicker() {
        long today = MaterialDatePicker.todayInUtcMilliseconds();
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setStart(today);
        constraintsBuilder.setOpenAt(today);
        constraintsBuilder.setValidator(DateValidatorPointForward.now());
        MaterialDatePicker<Pair<Long, Long>> dateRangePicker =
                MaterialDatePicker.Builder.dateRangePicker()
                        .setTitleText("Select dates")
                        .setSelection(new Pair<>(today, today))
                        .setCalendarConstraints(constraintsBuilder.build())
                        .build();
        dateRangePicker.addOnPositiveButtonClickListener(selection -> {
            Pair<Long, Long> selectedDates = dateRangePicker.getSelection();
            if (selectedDates != null) {
                startDateSelected = selectedDates.first;
                endDateSelected = selectedDates.second;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                startDate.getEditText().setText(simpleDateFormat.format(startDateSelected));
                endDate.getEditText().setText(simpleDateFormat.format(endDateSelected));
            }
        });
        dateRangePicker.show(getSupportFragmentManager(), "date_range_picker");
    }

    /**
     * Binds event data to the UI components.
     */
    private void bindEventData() {
        eventName.getEditText().setText(event.getName());
        place.getEditText().setText(event.getPlace());
        descriptionLong.getEditText().setText(event.getDescription_Long());
        descriptionShort.getEditText().setText(event.getDescription_short());
        price.getEditText().setText(String.valueOf(event.getPrice()));
        url.getEditText().setText(event.getUrlTicket());
        startDate.getEditText().setText(event.getStartDate());
        endDate.getEditText().setText(event.getEndDate());
        buyTicket.setText("Comprar entradas: " + event.getPrice() + "€");
    }

    /**
     * Disables all UI components if the user is a client.
     */
    private void disableAll() {
        eventName.getEditText().setFocusable(false);
        place.getEditText().setFocusable(false);
        descriptionLong.getEditText().setFocusable(false);
        descriptionShort.setVisibility(View.GONE);
        price.setVisibility(View.GONE);
        url.setVisibility(View.GONE);
        categories.setVisibility(View.GONE);
        saveButton.setVisibility(View.GONE);
        favToggleButton.setVisibility(View.VISIBLE);
        buyTicket.setVisibility(View.VISIBLE);
        startDate.getEditText().setEnabled(false);
        endDate.getEditText().setEnabled(false);

        favToggleButton.setChecked(checkFav());
    }

    /**
     * Checks if the event is marked as a favorite by the user.
     * @return true if the event is a favorite, false otherwise.
     */
    private boolean checkFav() {
        boolean isFav = false;
        SQLiteDatabase db = manager.getReadableDatabase();
        String queryFav = "SELECT COUNT(*) AS count FROM likedEvents WHERE id_user = ? AND id_event = ?;";
        Cursor cursor = db.rawQuery(queryFav, new String[]{userLogged.getId(), event.getId()});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                if (cursor.getInt(0) > 0) isFav = true;
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return isFav;
    }

    /**
     * Adds the event to the user's favorites.
     */
    private void addFavorite() {
        SQLiteDatabase db = manager.getWritableDatabase();
        String insertFavorite = "INSERT INTO LikedEvents (id_user, id_event) VALUES (?, ?)";
        SQLiteStatement statement = db.compileStatement(insertFavorite);
        statement.bindLong(1, Long.valueOf(userLogged.getId()));
        statement.bindLong(2, Long.valueOf(event.getId()));
        statement.executeInsert();
        db.close();
    }

    /**
     * Removes the event from the user's favorites.
     */
    private void removeFavorite() {
        SQLiteDatabase db = manager.getWritableDatabase();
        String deleteFavorite = "DELETE FROM LikedEvents WHERE id_user = ? AND id_event = ?";
        SQLiteStatement statement = db.compileStatement(deleteFavorite);
        statement.bindLong(1, Long.valueOf(userLogged.getId()));
        statement.bindLong(2, Long.valueOf(event.getId()));
        statement.executeUpdateDelete();
        db.close();
    }

}

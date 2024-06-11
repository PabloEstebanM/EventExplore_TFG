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
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Locale;

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

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_event);
        manager = new DbManager(this);

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

        // TODO: 03/06/2024 que recoja las categorías de la base de datos
        totalCategories = getResources().getStringArray(R.array.event_tags);
        categoriesSelected = new boolean[totalCategories.length];

        addMode = true;
        Intent intent = getIntent();
        userLogged = (User) intent.getSerializableExtra("usuario");
        try {
            event = (Event) intent.getSerializableExtra("evento");
        } catch (Exception e) {
        }

        if (event != null) {
            if (userLogged.getRole().equalsIgnoreCase("client")) disableAll();
            addMode = false;
            bindEventData();
        }

        addListeners();
    }

    private void addListeners() {
        back.setOnClickListener(v -> finish());
        buyTicket.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(event.getUrlTicket()))));
        favToggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                favToggleButton.setBackgroundResource(R.drawable.corazon_fill);
                // TODO: 03/06/2024 insert/update en la tabla likedEvents
            } else {
                favToggleButton.setBackgroundResource(R.drawable.corazon);
                // TODO: 03/06/2024 delete en la tabla likedEvents
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
        statement.bindLong(10,Long.parseLong(event.getId()));
        if (statement.executeInsert() != -1) {
            String deleteCategories = "DELETE FROM EventCategories WHERE id_event = ?";
            db.execSQL(deleteCategories,new Object[]{event.getId()});

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
        // TODO: 05/06/2024 snackbar
    }

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
        // TODO: 05/06/2024 comprobar que hay alguna categoría true 
        return true;
    }

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

    private void disableAll() {
        eventName.getEditText().setEnabled(false);
        place.getEditText().setEnabled(false);
        descriptionLong.getEditText().setEnabled(false);
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
}

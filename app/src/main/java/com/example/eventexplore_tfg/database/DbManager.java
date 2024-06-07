package com.example.eventexplore_tfg.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbManager extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NOMBRE = "EventExplore_DB";

    public DbManager(@Nullable Context context) {
        super(context, DATABASE_NOMBRE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createRoleTable(db);
        createCategoriesTable(db);
        createUsersTable(db);
        createEventsTable(db);
        createEventCategoriesTable(db);
        createPhotosTable(db);
        createLikedEvents(db);
        createLogsTable(db);
        insertsPlaceholder(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            createRoleTable(db);
            createCategoriesTable(db);
            createUsersTable(db);
            createEventsTable(db);
            createEventCategoriesTable(db);
            createPhotosTable(db);
            createLikedEvents(db);
            createLogsTable(db);
            insertsPlaceholder(db);
        }
    }


    private static void insertsPlaceholder(SQLiteDatabase db) {

        String insertRole = "INSERT INTO Role (name) VALUES ('client');";
        db.execSQL(insertRole);
        String insertRole2 = "INSERT INTO Role (name) VALUES ('company');";
        db.execSQL(insertRole2);
        String insertRole3 = "INSERT INTO Role (name) VALUES ('admin');";
        db.execSQL(insertRole3);

        String[] categories = {
                "Conciertos",
                "Exposiciones",
                "Anime",
                "Teatro",
                "Festivales",
                "Conferencias",
                "Deportes",
                "Talleres",
                "Cine",
                "Literatura"
        };

        for (String category : categories) {
            String insertCategory = "INSERT INTO Categories (name) VALUES ('" + category + "');";
            db.execSQL(insertCategory);
        }


        String insertUserA = "INSERT INTO Users (username, email, password, rol) VALUES ('a', 'userA@example.com', 'ca978112ca1bbdcafac231b39a23dc4da786eff8147c4e72b9807785afee48bb', 1);";
        db.execSQL(insertUserA);
        String insertUserB = "INSERT INTO Users (username, email, password, rol) VALUES ('b', 'userB@example.com', '3e23e8160039594a33894f6564e1b1348bbd7a0088d42c4acb73eeaed59c009d', 2);";
        db.execSQL(insertUserB);
        String insertUserC = "INSERT INTO Users (username, email, password, rol) VALUES ('c', 'userC@example.com', '2e7d2c03a9507ae265ecf5b5356885a53393a2029d241394997265a1a25aefc6', 3);";
        db.execSQL(insertUserC);

        String insertEvent = "INSERT INTO Events (name, place, description_short, description_long, price, startdate, enddate, id_company, urlTicket, tickets_sold) " +
                "VALUES ('placeholder_event', 'placeholder_place', 'short description', 'long description', 0.0, '1/1/2024', '1/1/2024', 2,'https://stackoverflow.com/questions/2201917/how-can-i-open-a-url-in-androids-web-browser-from-my-application', 0);";
        db.execSQL(insertEvent);

        String insertEventCategory1 = "INSERT INTO EventCategories (id_event, id_category) VALUES (1, 1);";
        db.execSQL(insertEventCategory1);
        String insertEventCategory2 = "INSERT INTO EventCategories (id_event, id_category) VALUES (1, 2);";
        db.execSQL(insertEventCategory2);

        String insertImage = "INSERT INTO Images (id_user, id_event, uri) VALUES (1, 1,'');";
        db.execSQL(insertImage);


        String insertLikedEvent = "INSERT INTO LikedEvents (id_user, id_event) VALUES (1, 1);";
        db.execSQL(insertLikedEvent);


        String insertLog = "INSERT INTO Logs (id_user, id_event, date, hour, description) VALUES (1, 1, '20/06/2024', '12:00:00', 'placeholder_log');";
        db.execSQL(insertLog);
    }

    private void createLogsTable(SQLiteDatabase db) {
        String createLogsTable = "CREATE TABLE IF NOT EXISTS Logs (" +
                "n_log INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_user INTEGER NOT NULL REFERENCES Users(id_user) ON DELETE CASCADE, " +
                "id_event INTEGER REFERENCES Events(id) ON DELETE CASCADE, " +
                "date TEXT NOT NULL, " +
                "hour TEXT NOT NULL, " +
                "description TEXT NOT NULL" +
                ");";
        db.execSQL(createLogsTable);
    }

    private void createLikedEvents(SQLiteDatabase db) {
        String createLikedEventsTable = "CREATE TABLE IF NOT EXISTS LikedEvents (" +
                "id_user INTEGER NOT NULL REFERENCES Users(id_user) ON DELETE CASCADE, " +
                "id_event INTEGER NOT NULL REFERENCES Events(id) ON DELETE CASCADE, " +
                "PRIMARY KEY (id_user, id_event)" +
                ");";
        db.execSQL(createLikedEventsTable);
    }

    private void createRoleTable(SQLiteDatabase db) {
        String createRoleTable = "CREATE TABLE IF NOT EXISTS Role ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL"
                + ");";
        db.execSQL(createRoleTable);
    }

    private void createEventsTable(SQLiteDatabase db) {
        String createEventTable = "CREATE TABLE IF NOT EXISTS Events (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "place TEXT NOT NULL, " +
                "description_short TEXT, " +
                "description_long TEXT, " +
                "price REAL NOT NULL, " +
                "startdate TEXT NOT NULL, " +
                "enddate TEXT NOT NULL, " +
                "id_company INTEGER NOT NULL, " +
                "tickets_sold INTEGER DEFAULT 0, " +
                "urlTicket TEXT, " +
                "FOREIGN KEY(id_company) REFERENCES Users(id_user)" +
                ");";
        db.execSQL(createEventTable);
    }

    private static void createPhotosTable(SQLiteDatabase db) {
        String createImagesTable = "CREATE TABLE IF NOT EXISTS Images (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_user INTEGER NOT NULL REFERENCES Users(id_user) ON DELETE CASCADE, " +
                "id_event INTEGER REFERENCES Events(id), " +
                "uri TEXT NOT NULL" +
                ");";
        db.execSQL(createImagesTable);
    }

    private static void createUsersTable(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS Users (" +
                "id_user INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL, " +
                "email TEXT NOT NULL, " +
                "password TEXT NOT NULL, " +
                "rol INTEGER, " +
                "FOREIGN KEY(rol) REFERENCES Role(id) " +
                ");";
        db.execSQL(createUsersTable);
    }

    private void createCategoriesTable(SQLiteDatabase db) {
        String createCategoriesTable = "CREATE TABLE IF NOT EXISTS Categories (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL" +
                ");";
        db.execSQL(createCategoriesTable);
    }
    private void createEventCategoriesTable(SQLiteDatabase db) {
        String createEventCategoriesTable = "CREATE TABLE IF NOT EXISTS EventCategories (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_event INTEGER NOT NULL, " +
                "id_category INTEGER NOT NULL, " +
                "FOREIGN KEY(id_event) REFERENCES Events(id) ON DELETE CASCADE, " +
                "FOREIGN KEY(id_category) REFERENCES Categories(id) ON DELETE CASCADE" +
                ");";
        db.execSQL(createEventCategoriesTable);
    }

}

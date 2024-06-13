package com.example.eventexplore_tfg.activitys;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.example.eventexplore_tfg.Data.User;
import com.example.eventexplore_tfg.R;
import com.example.eventexplore_tfg.database.DbManager;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.security.MessageDigest;

/**
 * Login activity handles user authentication.
 * It provides input fields for username and password, and buttons for login and registration.
 * @version 1.0
 * @autor Pablo Esteban Martín
 */
public class Login extends AppCompatActivity {
    private TextInputLayout usernameInput, passwordInput;
    private Button registerBtn, loginBtn;
    private DbManager manager;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        // Initialize UI components
        usernameInput = findViewById(R.id.InputUsernameLogin);
        passwordInput = findViewById(R.id.InputPasswordLogin);
        registerBtn = findViewById(R.id.BtnRegisterLogin);
        loginBtn = findViewById(R.id.BtnLoginLogin);
        manager = new DbManager(this);

// Add focus change listener to username input field
        usernameInput.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (!comprobarUsername()) {
                    usernameInput.setError("El nombre de usuario contiene carácteres inválidos");
                }else {
                    usernameInput.setError(null);
                }
            }
        });
        // Set click listener for register button
        registerBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, Register.class));
        });
// Set click listener for login button
        loginBtn.setOnClickListener(v -> {
            if (comprobarLogin()) {
                String id = "";
                String username = "";
                String role = "";
                // Query to get user details
                SQLiteDatabase db = manager.getReadableDatabase();
                String consultaRol = "SELECT Users.id_user,Users.username, Role.name FROM Users " +
                        "JOIN Role ON Users.rol = Role.id " +
                        "WHERE Users.username = ?";
                Cursor cursor = db.rawQuery(consultaRol, new String[]{usernameInput.getEditText().getText().toString().trim()});
                if (cursor != null && cursor.moveToFirst()) {
                    id = cursor.getString(0);
                    username = cursor.getString(1);
                    role = cursor.getString(2);
                }
                // Create User object and log in
                User userLoged = new User(id, username, role);
                logear(userLoged);
            } else {
                Snackbar.make(usernameInput, "Ha habido un error en el inicio de sesión, compruebe que los datos con correctos", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * Logs in the user based on their role.
     *
     * @param userLoged The logged-in user.
     */
    private void logear(User userLoged) {
        Intent intent = null;
        switch (userLoged.getRole().toLowerCase()) {
            case "client":
                intent = new Intent(this, ClientView.class);
                break;
            case "company":
                intent = new Intent(this, CompanyView.class);
                break;
            case "admin":
                intent = new Intent(this, AdminView.class);
                break;
        }
        if (intent != null) {
            intent.putExtra("usuario", userLoged);
            startActivity(intent);
        } else {
            Snackbar.make(usernameInput, "Ha habido un error en el inicio de sesión", Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * Validates login credentials.
     *
     * @return True if the credentials are valid, false otherwise.
     */
    private Boolean comprobarLogin() {
        Boolean loginCorrecto = true;
        // Validate username format
        if (!comprobarUsername()) {
            Snackbar.make(usernameInput, "El nombre de usuario contiene carácteres inválidos", Snackbar.LENGTH_SHORT).show();
        }
        // Check if input fields are not empty
        if (usernameInput.getEditText().getText() == null || usernameInput.getEditText().getText().toString().trim().isEmpty() ||
                passwordInput.getEditText().getText() == null || passwordInput.getEditText().getText().toString().trim().isEmpty()) {
            loginCorrecto = false;
            Snackbar.make(usernameInput, "Todos los campos deben estar rellenos", Snackbar.LENGTH_SHORT).show();
        }
        // Validate credentials with the database
        SQLiteDatabase db = manager.getReadableDatabase();
        String hashedPass = hashPass();
        System.out.println(hashedPass);
        String consultaUser = "SELECT COUNT(id_user) FROM Users WHERE " +
                "username = '" + usernameInput.getEditText().getText().toString().trim() + "' " +
                "AND password = '" + hashedPass + "';";
        Cursor cursor = db.rawQuery(consultaUser, null);
        if (cursor != null && cursor.moveToFirst()) {
            if (cursor.getInt(0) < 1) {
                loginCorrecto = false;
            }
        }
        db.close();

        return loginCorrecto;
    }

    /**
     * Validates the format of the username.
     *
     * @return True if the username format is valid, false otherwise.
     */
    private Boolean comprobarUsername() {
        String username = usernameInput.getEditText().getText().toString();
        for (int i = 0; i < username.length(); i++) {
            char c = username.charAt(i);
            if (!Character.isLetterOrDigit(c) && c != '_') {
                return false; //no vale
            }
        }
        return true;  // vale
    }

    /**
     * Hashes the password using SHA-256.
     *
     * @return The hashed password as a hexadecimal string.
     */
    public String hashPass() {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(passwordInput.getEditText().getText().toString().trim().getBytes());
            return bytesToHex(encodedhash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts a byte array to a hexadecimal string.
     *
     * @param bytes The byte array.
     * @return The hexadecimal string.
     */
    public String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }


}

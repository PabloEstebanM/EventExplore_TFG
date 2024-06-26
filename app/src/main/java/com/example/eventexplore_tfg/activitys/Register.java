package com.example.eventexplore_tfg.activitys;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventexplore_tfg.Data.User;
import com.example.eventexplore_tfg.R;
import com.example.eventexplore_tfg.database.DbManager;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This activity handles the registration and modification of users.
 *
 * @version 1.0
 * @autor Pablo Esteban Martín
 */
public class Register extends AppCompatActivity {
    private ImageView image;
    private TextInputLayout name, email, pass, confirmPass, role;
    private Button back, accept;
    private AutoCompleteTextView autoCompleteRole;
    private DbManager manager;
    private User userLogged, userEdit;
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        // Initialize database manager
        manager = new DbManager(this);

        // Bind UI components to their respective views
        image = findViewById(R.id.profile_picture);
        name = findViewById(R.id.InputUsernameRegister);
        email = findViewById(R.id.InputEmailRegister);
        pass = findViewById(R.id.InputPasswordRegister);
        confirmPass = findViewById(R.id.InputConfirmPasswordRegister);
        role = findViewById(R.id.InputRole_Register);
        back = findViewById(R.id.BtnCancelarRegister);
        accept = findViewById(R.id.BtnAceptarRegister);
        autoCompleteRole = findViewById(R.id.AutoComplete_Role);
        // Get user data from intent
        Intent intent = getIntent();
        userLogged = (User) intent.getSerializableExtra("usuario");
        userEdit = (User) intent.getSerializableExtra("edit");

        // Show role dropdown if the logged-in user is an admin
        if (userLogged != null) {
            if (userLogged.getRole().equalsIgnoreCase("admin")) {
                role.setVisibility(View.VISIBLE);
                autoCompleteRole.setVisibility(View.VISIBLE);
            }
        }

        // If editing a user, bind their data to the form
        if (userEdit!= null){
            bindUserData();
        }
// Add button listeners
        addListeners();
    }

    // Set up button listeners
    private void addListeners() {
        // Back button listener
        back.setOnClickListener(v -> {
            finish();
        });
// Accept button listener
        accept.setOnClickListener(v -> {
            if (checkData()) {
                int id_role = 1;
                if (userLogged != null) {
                    if (userLogged.getRole().equalsIgnoreCase("admin")) {
                        switch (role.getEditText().getText().toString().trim().toLowerCase()) {
                            case "client":
                            case "cliente":
                                id_role = 1;
                                break;
                            case "company":
                            case "empresa":
                                id_role = 2;
                                break;
                            case "admin":
                                id_role = 3;
                                break;
                        }
                    }
                }
                // Get writable database
                SQLiteDatabase db = manager.getWritableDatabase();
                SQLiteStatement statement;
                String sql = "";
                if (userEdit == null){
                    // SQL query for inserting a new user
                     sql = "INSERT INTO Users (username, email, password, rol) VALUES (?, ?, ?, ?)";
                }else {
                    // SQL query for updating an existing user
                    sql = "UPDATE Users SET username = ?, email = ?, password = ?, rol = ? WHERE id_user = ?";
                }
                // Bind data to the statement
                statement = db.compileStatement(sql);

                statement.bindString(1, name.getEditText().getText().toString());
                statement.bindString(2, email.getEditText().getText().toString());
                statement.bindString(3, hashPass());
                statement.bindLong(4, Long.parseLong(String.valueOf(id_role)));
                long i;
                if (userEdit != null){
                    statement.bindLong(5, Long.parseLong(userEdit.getId()));
                    i = statement.executeUpdateDelete();
                    if ( i  >0){
                        setResult(RESULT_OK);
                        finish();
                    }else {
                        Snackbar.make(image, "Ha habido un error en la modificación del usuario", Snackbar.LENGTH_SHORT).show();
                    }
                }else{
                    i = statement.executeInsert();
                    if ( i  != -1){
                        setResult(RESULT_OK);
                        finish();
                    }else {
                        Snackbar.make(image, "Ha habido un error en la creación del usuario", Snackbar.LENGTH_SHORT).show();
                    }
                }

            }else {
                Snackbar.make(image, "Los datos introducidos no son correctos", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    // Bind user data to the form if editing
    private void bindUserData() {
        name.getEditText().setText(userEdit.getUsername());
        email.getEditText().setText(userEdit.getEmail());
        role.getEditText().setText(userEdit.getRole());
    }

    // Check if the form data is valid
    private boolean checkData() {
        if (name.getEditText().getText().toString().trim().isEmpty()) return false;
        if (email.getEditText().getText().toString().trim().isEmpty()) return false;
        if (!isValidEmail(email.getEditText().getText().toString().trim()))return false;
        if (pass.getEditText().getText().toString().trim().isEmpty()) return false;
        if (confirmPass.getEditText().getText().toString().trim().isEmpty()) return false;
        if (userLogged != null) {
            if (userLogged.getRole().equalsIgnoreCase("admin")) {
                if (role.getEditText().getText().toString().trim().isEmpty()) return false;
            }
        }
        if (!pass.getEditText().getText().toString().trim().equals(confirmPass.getEditText().getText().toString().trim())) return false;
        SQLiteDatabase db = manager.getReadableDatabase();
        String queryUser = "SELECT COUNT(*) AS user_count FROM Users WHERE username = ?";
        Cursor cursorName = db.rawQuery(queryUser,new String[]{name.getEditText().getText().toString().trim()});
        if (cursorName != null && cursorName.moveToFirst()){
            if (cursorName.getInt(0)>0){
                Snackbar.make(image, "Ha habido un error en la modificación del usuario", Snackbar.LENGTH_SHORT).show();
                return false;
            }
        }
        cursorName.close();
        String queryUserEmail ="SELECT COUNT(*) AS user_count FROM Users WHERE username = ?";
        Cursor cursorEmail = db.rawQuery(queryUserEmail,new String[]{email.getEditText().getText().toString().trim()});
        if (cursorEmail != null && cursorEmail.moveToFirst()){
            if (cursorEmail.getInt(0)>0){
                Snackbar.make(image, "Ha habido un error en la modificación del usuario", Snackbar.LENGTH_SHORT).show();
                return false;
            }
        }
        cursorEmail.close();
        return true;
    }

    // Validate email format
    public static boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    // Hash the password using SHA-256
    public String hashPass() {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(pass.getEditText().getText().toString().trim().getBytes());
            return bytesToHex(encodedhash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Convert bytes to hexadecimal string
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

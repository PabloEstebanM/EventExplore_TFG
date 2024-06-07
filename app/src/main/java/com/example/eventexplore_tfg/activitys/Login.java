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

public class Login extends AppCompatActivity {
    private TextInputLayout usernameInput, passwordInput;
    private Button registerBtn, loginBtn;
    private DbManager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        usernameInput = findViewById(R.id.InputUsernameLogin);
        passwordInput = findViewById(R.id.InputPasswordLogin);
        registerBtn = findViewById(R.id.BtnRegisterLogin);
        loginBtn = findViewById(R.id.BtnLoginLogin);
        manager = new DbManager(this);


        usernameInput.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (!comprobarUsername()) {
                    usernameInput.setError("El nombre de usuario contiene carácteres inválidos");
                }else {
                    usernameInput.setError(null);
                }
            }
        });
        registerBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, Register.class));
        });

        loginBtn.setOnClickListener(v -> {
            if (comprobarLogin()) {
                String id = "";
                String username = "";
                String role = "";
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
                User userLoged = new User(id, username, role);
                logear(userLoged);
            } else {
                Snackbar.make(usernameInput, "Ha habido un error en el inicio de sesión, compruebe que los datos con correctos", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

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

    private Boolean comprobarLogin() {
        Boolean loginCorrecto = true;
        if (!comprobarUsername()) {
            Snackbar.make(usernameInput, "El nombre de usuario contiene carácteres inválidos", Snackbar.LENGTH_SHORT).show();
        }
        if (usernameInput.getEditText().getText() == null || usernameInput.getEditText().getText().toString().trim().isEmpty() ||
                passwordInput.getEditText().getText() == null || passwordInput.getEditText().getText().toString().trim().isEmpty()) {
            loginCorrecto = false;
            Snackbar.make(usernameInput, "Todos los campos deben estar rellenos", Snackbar.LENGTH_SHORT).show();
        }
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

    public String hashPass() {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(passwordInput.getEditText().getText().toString().trim().getBytes());
            return bytesToHex(encodedhash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
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

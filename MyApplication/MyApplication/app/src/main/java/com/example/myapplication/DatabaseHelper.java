package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "footballApp.db";
    private static final String TABLE_NAME = "users";
    private static final String RECLAMATIONS_TABLE = "reclamations";
    private static final String R_COL_5 = "TYPE"; // Added type column
    private static final String R_COL_6 = "DESCRIPTION"; // Added description column

    private static final String R_COL_1 = "ID";
    private static final String R_COL_2 = "SUJET";
    private static final String R_COL_3 = "DETAILS";
    private static final String R_COL_4 = "USER_ID";

    private static final String COL_1 = "ID";
    private static final String COL_2 = "USERNAME";
    private static final String COL_3 = "EMAIL";
    private static final String COL_4 = "PASSWORD";
    private static final String COL_5 = "ROLE";
    private static final String COL_6 = "BIRTHDATE";
    private static final String COL_7 = "POSITION";
    private static final String COL_8 = "WITH_TEAM";
    private static final String COL_9 = "WITHOUT_TEAM";
    private static final String COL_10 = "PROFILE_IMAGE";
    private static final String COL_11 = "IS_SUPER_ADMIN";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 7);}

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_2 + " TEXT, " +
                COL_3 + " TEXT, " +
                COL_4 + " TEXT, " +
                COL_5 + " TEXT, " +
                COL_6 + " TEXT, " +
                COL_7 + " TEXT, " +
                COL_8 + " INTEGER, " +
                COL_9 + " INTEGER, " +
                COL_10 + " TEXT, " +
                COL_11 + " INTEGER DEFAULT 0)";
        db.execSQL(createTable);
        String createReclamationTable = "CREATE TABLE " + RECLAMATIONS_TABLE + " (" +
                R_COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                R_COL_2 + " TEXT, " +  // Sujet
                R_COL_3 + " TEXT, " +  // Details
                R_COL_4 + " INTEGER, " + // User ID
                R_COL_5 + " TEXT, " +  // Type
                R_COL_6 + " TEXT, " +  // Description
                "FOREIGN KEY(" + R_COL_4 + ") REFERENCES " + TABLE_NAME + "(" + COL_1 + "))";
        db.execSQL(createReclamationTable);
    }
    @Override

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop existing tables
        db.execSQL("DROP TABLE IF EXISTS " + RECLAMATIONS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Recreate tables
        onCreate(db);
    }


    private void createSuperAdmin(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE EMAIL=?", new String[]{"omarabidiadmin@esprit.tn"});
        if (cursor.getCount() == 0) {
            insertUser("Omar Abidi Admin", "omarabidiadmin@esprit.tn", "omaromar", "Super Admin");
            updateIsSuperAdmin("omarabidiadmin@esprit.tn", true);
        }
        cursor.close();
    }

    public boolean insertUser(String username, String email, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, username);
        contentValues.put(COL_3, email);
        contentValues.put(COL_4, password);
        contentValues.put(COL_5, role);
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }
    public boolean addReclamation(String sujet, String details, String type, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(R_COL_2, sujet);
        contentValues.put(R_COL_3, details);
        contentValues.put(R_COL_5, type);
        contentValues.put(R_COL_6, description);

        long result = db.insert(RECLAMATIONS_TABLE, null, contentValues);
        return result != -1;
    }



    // Nouvelle méthode updateUser
    public boolean updateUser(String email, String username, String role, String birthdate, String position, boolean withTeam, boolean withoutTeam, String profileImage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, username);
        contentValues.put(COL_5, role);
        contentValues.put(COL_6, birthdate);
        contentValues.put(COL_7, position);
        contentValues.put(COL_8, withTeam ? 1 : 0);
        contentValues.put(COL_9, withoutTeam ? 1 : 0);
        contentValues.put(COL_10, profileImage);

        return db.update(TABLE_NAME, contentValues, COL_3 + "=?", new String[]{email}) > 0;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE EMAIL=? AND PASSWORD=?", new String[]{email, password});
        return cursor.getCount() > 0;
    }

    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE EMAIL=?", new String[]{email});
    }

    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public boolean updateIsSuperAdmin(String email, boolean isSuperAdmin) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_11, isSuperAdmin ? 1 : 0);
        return db.update(TABLE_NAME, contentValues, COL_3 + "=?", new String[]{email}) > 0;
    }

    public boolean deleteUser(String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COL_1 + "=?", new String[]{userId}) > 0;
    }

    public ArrayList<HashMap<String, Object>> getUsersWithReclamations() {
        ArrayList<HashMap<String, Object>> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM USERS", null);

        // Vérifiez si le curseur contient des données
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, Object> user = new HashMap<>();

                // Obtenez les index des colonnes et vérifiez leur validité
                int idIndex = cursor.getColumnIndex("ID");
                int usernameIndex = cursor.getColumnIndex("USERNAME");
                int emailIndex = cursor.getColumnIndex("EMAIL");
                int roleIndex = cursor.getColumnIndex("ROLE");

                // Vérifiez si les colonnes existent
                if (idIndex != -1 && usernameIndex != -1 && emailIndex != -1 && roleIndex != -1) {
                    String id = cursor.getString(idIndex);
                    String username = cursor.getString(usernameIndex);
                    String email = cursor.getString(emailIndex);
                    String role = cursor.getString(roleIndex);

                    user.put("ID", id);
                    user.put("USERNAME", username);
                    user.put("EMAIL", email);
                    user.put("ROLE", role);

                    // Récupérer les réclamations pour chaque utilisateur
                    ArrayList<Reclamation> reclamations = getReclamationsForUser(id);
                    user.put("RECLAMATIONS", reclamations);

                    users.add(user);
                } else {
                    Log.e("DatabaseHelper", "A required column was not found in the USERS table.");
                }

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return users;
    }

    public ArrayList<Reclamation> getReclamationsForUser(String userId) {
        ArrayList<Reclamation> reclamations = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM RECLAMATIONS WHERE USER_ID = ?", new String[]{userId});

            if (cursor.moveToFirst()) {
                int descriptionIndex = cursor.getColumnIndex("DESCRIPTION");
                int typeIndex = cursor.getColumnIndex("TYPE");

                do {
                    String description = descriptionIndex != -1 ? cursor.getString(descriptionIndex) : null;
                    String type = typeIndex != -1 ? cursor.getString(typeIndex) : null;
                    reclamations.add(new Reclamation(description, type));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return reclamations;
    }
    public ArrayList<String> getReclamationsByUserId(String userId) {
        ArrayList<String> reclamations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query("reclamations",
                null,
                "user_id = ?",
                new String[]{userId},  // Vous devrez peut-être ajouter le champ user_id dans la table
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                String description = cursor.getString(cursor.getColumnIndex("description"));
                String typeReclamation = cursor.getString(cursor.getColumnIndex("type_reclamation"));
                reclamations.add(description + " (" + typeReclamation + ")");
            } while (cursor.moveToNext());
        }
        cursor.close();
        return reclamations;
    }
    public List<HashMap<String, String>> getAllReclamations() {
        List<HashMap<String, String>> reclamations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + RECLAMATIONS_TABLE, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> reclamation = new HashMap<>();
                reclamation.put("id", cursor.getString(cursor.getColumnIndex(R_COL_1)));
                reclamation.put("sujet", cursor.getString(cursor.getColumnIndex(R_COL_2)));
                reclamation.put("details", cursor.getString(cursor.getColumnIndex(R_COL_3)));
                reclamation.put("type", cursor.getString(cursor.getColumnIndex(R_COL_5)));
                reclamation.put("description", cursor.getString(cursor.getColumnIndex(R_COL_6)));
                reclamations.add(reclamation);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return reclamations;
    }

    public boolean deleteReclamation(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(RECLAMATIONS_TABLE, R_COL_1 + "=?", new String[]{id}) > 0;
    }}
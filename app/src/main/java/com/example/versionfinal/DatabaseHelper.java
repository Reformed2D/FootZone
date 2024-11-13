package com.example.versionfinal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.versionfinal.payment.payment;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "footballApp.db";
    private static final int DATABASE_VERSION = 7;

    // Table Utilisateurs
    private static final String TABLE_NAME = "users";
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

    // Table Paiements
    private static final String TABLE_PAYMENTS = "payments";
    private static final String PAYMENT_ID = "ID";
    private static final String PAYMENT_USER_EMAIL = "USER_EMAIL";
    private static final String PAYMENT_AMOUNT = "AMOUNT";
    private static final String PAYMENT_STATUS = "STATUS";
    private static final String PAYMENT_DATE = "PAYMENT_DATE";
    private static final String PAYMENT_METHOD = "PAYMENT_METHOD";
    private static final String PAYMENT_CARD_LAST_FOUR = "CARD_LAST_FOUR";
    private static final String PAYMENT_DESCRIPTION = "DESCRIPTION";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Création de la table utilisateurs
        String createUsersTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_2 + " TEXT, " +
                COL_3 + " TEXT UNIQUE, " +
                COL_4 + " TEXT, " +
                COL_5 + " TEXT, " +
                COL_6 + " TEXT, " +
                COL_7 + " TEXT, " +
                COL_8 + " INTEGER, " +
                COL_9 + " INTEGER, " +
                COL_10 + " TEXT, " +
                COL_11 + " INTEGER DEFAULT 0)";
        db.execSQL(createUsersTable);

        // Création de la table paiements
        String createPaymentsTable = "CREATE TABLE " + TABLE_PAYMENTS + " (" +
                PAYMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PAYMENT_USER_EMAIL + " TEXT, " +
                PAYMENT_AMOUNT + " REAL, " +
                PAYMENT_STATUS + " INTEGER, " +
                PAYMENT_DATE + " INTEGER, " +
                PAYMENT_METHOD + " TEXT, " +
                PAYMENT_CARD_LAST_FOUR + " TEXT, " +
                PAYMENT_DESCRIPTION + " TEXT, " +
                "FOREIGN KEY(" + PAYMENT_USER_EMAIL + ") REFERENCES " + TABLE_NAME + "(" + COL_3 + "))";
        db.execSQL(createPaymentsTable);

        // Insertion de l'administrateur par défaut
        ContentValues adminValues = new ContentValues();
        adminValues.put(COL_2, "Omar Abidi Admin");
        adminValues.put(COL_3, "omarabidiadmin@esprit.tn");
        adminValues.put(COL_4, "omaromar");
        adminValues.put(COL_5, "Super Admin");
        adminValues.put(COL_11, 1);
        db.insert(TABLE_NAME, null, adminValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 5) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_11 + " INTEGER DEFAULT 0");
        }
        if (oldVersion < 6) {
            // Création de la table paiements si elle n'existe pas
            String createPaymentsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_PAYMENTS + " (" +
                    PAYMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PAYMENT_USER_EMAIL + " TEXT, " +
                    PAYMENT_AMOUNT + " REAL, " +
                    PAYMENT_STATUS + " INTEGER, " +
                    PAYMENT_DATE + " INTEGER, " +
                    PAYMENT_METHOD + " TEXT, " +
                    PAYMENT_CARD_LAST_FOUR + " TEXT, " +
                    PAYMENT_DESCRIPTION + " TEXT, " +
                    "FOREIGN KEY(" + PAYMENT_USER_EMAIL + ") REFERENCES " + TABLE_NAME + "(" + COL_3 + "))";
            db.execSQL(createPaymentsTable);
        }
    }

    // Méthodes pour les utilisateurs
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

    public boolean updateUser(String email, String username, String role, String birthdate,
                              String position, boolean withTeam, boolean withoutTeam, String profileImage) {
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
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME +
                " WHERE EMAIL=? AND PASSWORD=?", new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Méthodes pour les paiements
    public long recordPayment(String userEmail, double amount, String method, String lastFour) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(PAYMENT_USER_EMAIL, userEmail);
        values.put(PAYMENT_AMOUNT, amount);
        values.put(PAYMENT_STATUS, 1); // 1 = succès
        values.put(PAYMENT_DATE, System.currentTimeMillis());
        values.put(PAYMENT_METHOD, method);
        values.put(PAYMENT_CARD_LAST_FOUR, lastFour);

        return db.insert(TABLE_PAYMENTS, null, values);
    }

    public Cursor getUserPayments(String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_PAYMENTS,
                null,
                PAYMENT_USER_EMAIL + "=?",
                new String[]{userEmail},
                null,
                null,
                PAYMENT_DATE + " DESC");
    }

    public double getTotalPayments(String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + PAYMENT_AMOUNT + ") FROM " + TABLE_PAYMENTS +
                        " WHERE " + PAYMENT_USER_EMAIL + "=? AND " + PAYMENT_STATUS + "=1",
                new String[]{userEmail}
        );

        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    // Méthodes utilitaires
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
    public List<payment> getPayments(String email) {
        List<payment> payments = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {
                "id",
                "email",
                "amount",
                "payment_method",
                "card_number",
                "timestamp"
        };

        String selection = "email = ?";
        String[] selectionArgs = {email};
        String orderBy = "timestamp DESC";

        Cursor cursor = db.query(
                "payments",
                columns,
                selection,
                selectionArgs,
                null,
                null,
                orderBy
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                payment payment = new payment(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getDouble(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getLong(5)
                );
                payments.add(payment);
            }
            cursor.close();
        }

        return payments;
    }

}
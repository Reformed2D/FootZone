package com.example.versionfinal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.versionfinal.equipe.Team;
import com.example.versionfinal.payment.payment;
import com.example.versionfinal.reclamation.Reclamation;
import com.example.versionfinal.reservation.Reservation;
import com.example.versionfinal.terrain.Terrain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "footballApp.db";
    private static final int DATABASE_VERSION = 10;
/*****************************************************************/
private static final String TABLE_TERRAINS = "terrains";

    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_LOCALISATION = "localisation";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_PHONE = "phone";


    /******************************************maram******************************************/
// Reservation Table Constants
    static final String RECLAMATIONS_TABLE = "reclamations";
    static final String R_COL_5 = "TYPE"; // Added type column
    static final String R_COL_6 = "DESCRIPTION"; // Added description column

    static final String R_COL_1 = "ID";
    static final String R_COL_2 = "SUJET";
    static final String R_COL_3 = "DETAILS";
    private static final String R_COL_4 = "USER_ID";
    /**************************************reclamation*********************************************/
    public static final String TABLE_RESERVATIONS = "reservations";
    public static final String COLUMN_ID = "_id";                   // Primary key
    public static final String COLUMN_CLIENT_NAME = "client_name";  // Client name
    public static final String COLUMN_FIELD_NUMBER = "field_number"; // Field number
    public static final String COLUMN_DATE = "date";                // Reservation date
    public static final String COLUMN_START_TIME = "start_time";    // Start time
    public static final String COLUMN_DURATION = "duration";        // Duration
    public static final String COLUMN_PRICE = "price";              // Price
    public static final String COLUMN_TERRAIN_LIST = "terrain_list"; // List of terrains
    private static final String CREATE_TERRAIN_TABLE = "CREATE TABLE " + TABLE_TERRAINS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NAME + " TEXT,"
            + COLUMN_LOCALISATION + " TEXT,"
            + COLUMN_TYPE + " TEXT,"
            + COLUMN_STATUS + " TEXT,"
            + COLUMN_PHONE + " TEXT"
            + ")";
    private static final String CREATE_RESERVATION_TABLE =
            "CREATE TABLE " + TABLE_RESERVATIONS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CLIENT_NAME + " TEXT NOT NULL, " +
                    COLUMN_FIELD_NUMBER + " INTEGER NOT NULL, " +
                    COLUMN_DATE + " TEXT NOT NULL, " +
                    COLUMN_START_TIME + " TEXT NOT NULL, " +
                    COLUMN_DURATION + " INTEGER NOT NULL, " +
                    COLUMN_PRICE + " REAL NOT NULL, " +
                    COLUMN_TERRAIN_LIST + " TEXT" + ")";
    // Get All Reservations
    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RESERVATIONS,
                null, null, null, null, null,
                COLUMN_DATE + " ASC, " + COLUMN_START_TIME + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                reservations.add(cursorToReservation(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return reservations;
    }

    // Get Single Reservation
    public Reservation getReservation(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RESERVATIONS,
                null, COLUMN_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);

        Reservation reservation = null;
        if (cursor != null && cursor.moveToFirst()) {
            reservation = cursorToReservation(cursor);
            cursor.close();
        }
        return reservation;
    }

    // Update Reservation
    public boolean updateReservation(Reservation reservation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_CLIENT_NAME, reservation.getClientName());
        values.put(COLUMN_FIELD_NUMBER, reservation.getFieldNumber());
        values.put(COLUMN_DATE, reservation.getDate());
        values.put(COLUMN_START_TIME, reservation.getStartTime());
        values.put(COLUMN_DURATION, reservation.getDuration());
        values.put(COLUMN_PRICE, reservation.getPrice());
        values.put(COLUMN_TERRAIN_LIST, reservation.getTerrainListAsString());

        int result = db.update(TABLE_RESERVATIONS, values,
                COLUMN_ID + "=?", new String[]{String.valueOf(reservation.getId())});
        return result > 0;
    }
    public long createReservation(Reservation reservation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_CLIENT_NAME, reservation.getClientName());
        values.put(COLUMN_FIELD_NUMBER, reservation.getFieldNumber());
        values.put(COLUMN_DATE, reservation.getDate());
        values.put(COLUMN_START_TIME, reservation.getStartTime());
        values.put(COLUMN_DURATION, reservation.getDuration());
        values.put(COLUMN_PRICE, reservation.getPrice());
        values.put(COLUMN_TERRAIN_LIST, reservation.getTerrainListAsString());

        return db.insert(TABLE_RESERVATIONS, null, values);
    }
    // Delete Reservation
    public boolean deleteReservation(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_RESERVATIONS, COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}) > 0;
    }

    // Utility method to convert Cursor to Reservation
    private Reservation cursorToReservation(Cursor cursor) {
        Reservation reservation = new Reservation();

        reservation.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
        reservation.setClientName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLIENT_NAME)));
        reservation.setFieldNumber(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FIELD_NUMBER)));
        reservation.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)));
        reservation.setStartTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_TIME)));
        reservation.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DURATION)));
        reservation.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)));

        String terrainList = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TERRAIN_LIST));
        if (terrainList != null && !terrainList.isEmpty()) {
            reservation.setTerrainListFromString(terrainList);
        }

        return reservation;
    }

    // Check if time slot is available
    public boolean isTimeSlotAvailable(String date, String startTime, int fieldNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RESERVATIONS,
                null,
                COLUMN_DATE + "=? AND " + COLUMN_START_TIME + "=? AND " + COLUMN_FIELD_NUMBER + "=?",
                new String[]{date, startTime, String.valueOf(fieldNumber)},
                null, null, null);

        boolean isAvailable = true;
        if (cursor != null) {
            isAvailable = cursor.getCount() == 0;
            cursor.close();
        }
        return isAvailable;
    }
/*****************************************************************************************/
/*****************************************************************/
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
/*******************************************************************/
public static final String TEAM_TABLE_NAME = "team_table";
    public static final String TEAM_COL_1 = "TEAM_ID"; // Primary key for team table
    public static final String TEAM_COL_2 = "TEAM_NAME";
    public static final String TEAM_COL_3 = "GOALKEEPER";
    public static final String TEAM_COL_4 = "DEFENDER_LEFT";
    public static final String TEAM_COL_5 = "DEFENDER_RIGHT";
    public static final String TEAM_COL_6 = "MIDFIELDER_LEFT";
    public static final String TEAM_COL_7 = "MIDFIELDER_RIGHT";
    public static final String TEAM_COL_8 = "FORWARD_LEFT";
    public static final String TEAM_COL_9 = "FORWARD_RIGHT";
    public static final String TEAM_COL_10 = "USER_ID";
    public static final String TEAM_IMAGE_TABLE_NAME = "team_images";
    public static final String TEAM_IMAGE_COL_1 = "ID"; // Primary key for team images table
    public static final String TEAM_IMAGE_COL_2 = "TEAM_ID"; // Foreign key referencing team_table
    public static final String TEAM_IMAGE_COL_3 = "IMAGE";
/****************************************************************************/
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

/**************************************creation user***********************************/

public DatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
}
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DatabaseHelper", "onCreate called");
        db.execSQL("PRAGMA foreign_keys = ON;");

        Log.d("DatabaseHelper", "Creating database and tables...");
        db.execSQL("CREATE TABLE " + TABLE_RESERVATIONS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CLIENT_NAME + " TEXT NOT NULL, " +
                COLUMN_FIELD_NUMBER + " INTEGER NOT NULL, " +
                COLUMN_DATE + " TEXT NOT NULL, " +
                COLUMN_START_TIME + " TEXT NOT NULL, " +
                COLUMN_DURATION + " INTEGER NOT NULL, " +
                COLUMN_PRICE + " REAL NOT NULL, " +
                COLUMN_TERRAIN_LIST + " TEXT" + ")");
        db.execSQL(CREATE_TERRAIN_TABLE);
        // 1. First create the users table as it's referenced by others
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

        // 2. Create the reservations table
      ;
        String createReclamationTable = "CREATE TABLE " + RECLAMATIONS_TABLE + " (" +
                R_COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                R_COL_2 + " TEXT, " +  // Sujet
                R_COL_3 + " TEXT, " +  // Details
                R_COL_4 + " INTEGER, " + // User ID
                R_COL_5 + " TEXT, " +  // Type
                R_COL_6 + " TEXT, " +  // Description
                "FOREIGN KEY(" + R_COL_4 + ") REFERENCES " + TABLE_NAME + "(" + COL_1 + "))";
        db.execSQL(createReclamationTable);

        // 3. Create the payments table
        db.execSQL("CREATE TABLE " + TABLE_PAYMENTS + " (" +
                PAYMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PAYMENT_USER_EMAIL + " TEXT, " +
                PAYMENT_AMOUNT + " REAL, " +
                PAYMENT_STATUS + " INTEGER, " +
                PAYMENT_DATE + " INTEGER, " +
                PAYMENT_METHOD + " TEXT, " +
                PAYMENT_CARD_LAST_FOUR + " TEXT, " +
                PAYMENT_DESCRIPTION + " TEXT, " +
                "FOREIGN KEY(" + PAYMENT_USER_EMAIL + ") REFERENCES " + TABLE_NAME + "(" + COL_3 + "))");

        // 4. Create the team table
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TEAM_TABLE_NAME + " (" +
                TEAM_COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TEAM_COL_2 + " TEXT, " +
                TEAM_COL_3 + " TEXT, " +
                TEAM_COL_4 + " TEXT, " +
                TEAM_COL_5 + " TEXT, " +
                TEAM_COL_6 + " TEXT, " +
                TEAM_COL_7 + " TEXT, " +
                TEAM_COL_8 + " TEXT, " +
                TEAM_COL_9 + " TEXT, " +
                TEAM_COL_10 + " INTEGER, " +
                "FOREIGN KEY(" + TEAM_COL_10 + ") REFERENCES " + TABLE_NAME + "(" + COL_1 + "))");

        // 5. Insert default admin
        ContentValues adminValues = new ContentValues();
        adminValues.put(COL_2, "Omar Abidi Admin");
        adminValues.put(COL_3, "omarabidiadmin@esprit.tn");
        adminValues.put(COL_4, "omaromar");
        adminValues.put(COL_5, "Super Admin");
        adminValues.put(COL_11, 1);
        db.insert(TABLE_NAME, null, adminValues);
        Log.d("DatabaseHelper", "All tables created successfully");
    }
    /******************************************maram******************************************/





    /*****************************************************************************************/

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 9) {  // Adjust as needed.
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESERVATIONS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TERRAINS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAYMENTS);
            db.execSQL("DROP TABLE IF EXISTS " + TEAM_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + RECLAMATIONS_TABLE);
            onCreate(db);
        }
        Log.d("DatabaseHelper", "Upgrading database from version " + oldVersion + " to " + newVersion);
        // Gardez les autres mises à jour
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
    /************Hadji**********************************************/
    public Cursor getUserByID(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE ID = ?";
        String[] selectionArgs = { String.valueOf(userId) };
        return db.rawQuery(query, selectionArgs);
    }
    public int insertTeam(String teamName, String goalkeeper, String defenderLeft, String defenderRight,
                          String midfielderLeft, String midfielderRight, String forwardLeft, String forwardRight, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TEAM_COL_2, teamName);
        contentValues.put(TEAM_COL_3, goalkeeper);
        contentValues.put(TEAM_COL_4, defenderLeft);
        contentValues.put(TEAM_COL_5, defenderRight);
        contentValues.put(TEAM_COL_6, midfielderLeft);
        contentValues.put(TEAM_COL_7, midfielderRight);
        contentValues.put(TEAM_COL_8, forwardLeft);
        contentValues.put(TEAM_COL_9, forwardRight);
        contentValues.put(TEAM_COL_10, userId);  // The foreign key (userId)

        long result = db.insert(TEAM_TABLE_NAME, null, contentValues);

        // If insertion fails, return -1, otherwise return the team_id (result)
        return result != -1 ? (int) result : -1;
    }

    // Insert team image
    public boolean saveTeamImage(int teamId, String encodedImage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TEAM_IMAGE_COL_2, teamId);
        contentValues.put(TEAM_IMAGE_COL_3, encodedImage);
        return db.insert(TEAM_IMAGE_TABLE_NAME, null, contentValues) != -1;
    }

    // Update team image
    public boolean updateTeamImage(int teamId, String encodedImage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TEAM_IMAGE_COL_3, encodedImage);
        return db.update(TEAM_IMAGE_TABLE_NAME, contentValues, TEAM_IMAGE_COL_2 + "=?", new String[]{String.valueOf(teamId)}) > 0;
    }

    // Retrieve all teams
    public Cursor getAllTeams() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TEAM_TABLE_NAME, null);
    }

    // Retrieve team by ID
    public Team getTeamById(int teamId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        Team team = null;

        try {
            String query = "SELECT * FROM " + TEAM_TABLE_NAME + " WHERE " + TEAM_COL_1 + " = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(teamId)});

            Log.d("DatabaseHelper", "Getting team with ID: " + teamId);

            if (cursor != null && cursor.moveToFirst()) {
                team = new Team();

                team.setTeamId(cursor.getInt(cursor.getColumnIndexOrThrow(TEAM_COL_1)));
                team.setTeamName(cursor.getString(cursor.getColumnIndexOrThrow(TEAM_COL_2)));
                team.setGoalkeeper(cursor.getString(cursor.getColumnIndexOrThrow(TEAM_COL_3)));
                team.setDefenderLeft(cursor.getString(cursor.getColumnIndexOrThrow(TEAM_COL_4)));
                team.setDefenderRight(cursor.getString(cursor.getColumnIndexOrThrow(TEAM_COL_5)));
                team.setMidfielderLeft(cursor.getString(cursor.getColumnIndexOrThrow(TEAM_COL_6)));
                team.setMidfielderRight(cursor.getString(cursor.getColumnIndexOrThrow(TEAM_COL_7)));
                team.setForwardLeft(cursor.getString(cursor.getColumnIndexOrThrow(TEAM_COL_8)));
                team.setForwardRight(cursor.getString(cursor.getColumnIndexOrThrow(TEAM_COL_9)));

                Log.d("DatabaseHelper", "Team found: " + team.getTeamName());
            } else {
                Log.d("DatabaseHelper", "No team found with ID: " + teamId);
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting team by ID: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return team;
    }




    // Delete team
    public void deleteTeam(int teamId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TEAM_TABLE_NAME, TEAM_COL_1 + " = ?", new String[]{String.valueOf(teamId)});
        db.close();
    }
    public Cursor getTeamByUserId(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String query = "SELECT * FROM " + TEAM_TABLE_NAME + " WHERE " + TEAM_COL_10 + " = ?";
            Log.d("DatabaseHelper", "Getting team for user ID: " + userId);
            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor != null && cursor.moveToFirst()) {
                Log.d("DatabaseHelper", "Found team for user");
            } else {
                Log.d("DatabaseHelper", "No team found for user");
            }

            return cursor;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting team by user ID: " + e.getMessage());
            return null;
        }
    }


    public boolean updateTeam(int teamId, String teamName, String goalkeeper, String defenderLeft, String defenderRight,
                              String midfielderLeft, String midfielderRight, String forwardLeft, String forwardRight) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(TEAM_COL_2, teamName);
        contentValues.put(TEAM_COL_3, goalkeeper);
        contentValues.put(TEAM_COL_4, defenderLeft);
        contentValues.put(TEAM_COL_5, defenderRight);
        contentValues.put(TEAM_COL_6, midfielderLeft);
        contentValues.put(TEAM_COL_7, midfielderRight);
        contentValues.put(TEAM_COL_8, forwardLeft);
        contentValues.put(TEAM_COL_9, forwardRight);

        // Update the team with the given teamId
        int result = db.update(TEAM_TABLE_NAME, contentValues, TEAM_COL_1 + " = ?", new String[]{String.valueOf(teamId)});
        db.close();

        return result > 0; // Return true if update was successful
    }
    public boolean removePlayerFromTeam(int teamId, String positionColumn) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.putNull(positionColumn);

        int result = db.update(TEAM_TABLE_NAME, contentValues, TEAM_COL_1 + " = ?",
                new String[]{String.valueOf(teamId)});
        db.close();

        return result > 0;
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

        Cursor cursor = db.query(RECLAMATIONS_TABLE,
                null,
                null,
                null,
                null,
                null,
                null);

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




    public boolean deleteReclamation(String reclamationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("Reclamations", "id = ?", new String[]{reclamationId});
        db.close();
        return result > 0; // Retourne true si une réclamation a été supprimée
    }
    public long addTerrain(Terrain terrain) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, terrain.getName());
        values.put(COLUMN_LOCALISATION, terrain.getLocalisation());
        values.put(COLUMN_TYPE, terrain.getType());
        values.put(COLUMN_STATUS, terrain.getStatus());
        values.put(COLUMN_PHONE, terrain.getPhone());

        long id = db.insert(TABLE_TERRAINS, null, values);
        db.close();
        return id;
    }

    public Terrain getTerrain(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TERRAINS,
                new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_LOCALISATION, COLUMN_TYPE, COLUMN_STATUS, COLUMN_PHONE},
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Terrain terrain = new Terrain(
                    cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_LOCALISATION)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_TYPE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_STATUS)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_PHONE))
            );
            terrain.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
            cursor.close();
            return terrain;
        }
        return null;
    }

    public List<Terrain> getAllTerrains() {
        List<Terrain> terrainList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TERRAINS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Terrain terrain = new Terrain(
                        cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_LOCALISATION)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_TYPE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_STATUS)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_PHONE))
                );
                terrain.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                terrainList.add(terrain);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return terrainList;
    }

    public int updateTerrain(Terrain terrain) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, terrain.getName());
        values.put(COLUMN_LOCALISATION, terrain.getLocalisation());
        values.put(COLUMN_TYPE, terrain.getType());
        values.put(COLUMN_STATUS, terrain.getStatus());
        values.put(COLUMN_PHONE, terrain.getPhone());

        return db.update(TABLE_TERRAINS, values,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(terrain.getId())});
    }

    public void deleteTerrain(Terrain terrain) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TERRAINS,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(terrain.getId())});
        db.close();
    }

    public List<Terrain> searchTerrainsByName(String name) {
        List<Terrain> terrainList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_TERRAINS +
                " WHERE " + COLUMN_NAME + " LIKE '%" + name + "%'";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Terrain terrain = new Terrain(
                        cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_LOCALISATION)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_TYPE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_STATUS)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_PHONE))
                );
                terrain.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                terrainList.add(terrain);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return terrainList;
    }
}
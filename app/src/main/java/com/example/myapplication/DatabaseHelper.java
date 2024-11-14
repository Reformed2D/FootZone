package com.example.myapplication;

import static android.content.Context.MODE_PRIVATE;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Arrays;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "footballApp.db";
    public static final String TABLE_NAME = "users";

    public static final String COL_1 = "ID";
    public static final String COL_2 = "USERNAME";
    public static final String COL_3 = "EMAIL";
    public static final String COL_4 = "PASSWORD";
    public static final String COL_5 = "ROLE";
    public static final String COL_6 = "BIRTHDATE";
    public static final String COL_7 = "POSITION";
    public static final String COL_8 = "WITH_TEAM";
    public static final String COL_9 = "WITHOUT_TEAM";
    public static final String COL_10 = "PROFILE_IMAGE";
    public static final String COL_11 = "IS_SUPER_ADMIN";
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
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys = ON;");

        // Create users table
        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
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

        // Create team table
        String createTeamTable = "CREATE TABLE IF NOT EXISTS " + TEAM_TABLE_NAME + " (" +
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
                "FOREIGN KEY(" + TEAM_COL_10 + ") REFERENCES " + TABLE_NAME + "(" + COL_1 + "))"; // User's table should not be touched
        db.execSQL(createTeamTable);

        // Create team images table
        String createTeamImageTable = "CREATE TABLE IF NOT EXISTS " + TEAM_IMAGE_TABLE_NAME + " (" +
                TEAM_IMAGE_COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TEAM_IMAGE_COL_2 + " INTEGER, " +
                TEAM_IMAGE_COL_3 + " TEXT, " +
                "FOREIGN KEY(" + TEAM_IMAGE_COL_2 + ") REFERENCES " + TEAM_TABLE_NAME + "(" + TEAM_COL_1 + "))";
        db.execSQL(createTeamImageTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 7) {
            // Check if 'IS_SUPER_ADMIN' column already exists in the users table
            Cursor cursor = db.rawQuery("PRAGMA table_info(" + TABLE_NAME + ")", null);
            boolean columnExists = false;

            if (cursor != null) {
                int nameIndex = cursor.getColumnIndex("name");
                if (nameIndex >= 0) {
                    while (cursor.moveToNext()) {
                        String columnName = cursor.getString(nameIndex);
                        if (COL_11.equalsIgnoreCase(columnName)) {
                            columnExists = true;
                            break;
                        }
                    }
                }
                cursor.close();
            }

            if (!columnExists) {
                // Add 'IS_SUPER_ADMIN' column if not already present
                db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_11 + " INTEGER DEFAULT 0");
            }
        }

        if (oldVersion < 4) {
            onCreate(db);  // Call to create missing tables if required
        }
    }





    // Create Super Admin if needed
    public void createSuperAdminIfNeeded() {
        SQLiteDatabase db = this.getWritableDatabase();
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
        return db.rawQuery("SELECT ID, USERNAME, EMAIL, PASSWORD, ROLE, BIRTHDATE, POSITION, PROFILE_IMAGE, IS_SUPER_ADMIN FROM " + TABLE_NAME + " WHERE EMAIL=?", new String[]{email});
    }
    public Cursor getUserByID(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE ID = ?";
        String[] selectionArgs = { String.valueOf(userId) };
        return db.rawQuery(query, selectionArgs);
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
    // Insert team data
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
    /***************jdid*************/
    public Cursor getPlayersWithoutTeam() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_9 + " = 0", null);
    }
    private static final int MAX_GOALKEEPERS = 1;
    private static final int MAX_DEFENDERS = 2;
    private static final int MAX_MIDFIELDERS = 2;
    private static final int MAX_FORWARDS = 2;

    public boolean canAssignPosition(String position) {
        SQLiteDatabase db = this.getReadableDatabase();

        int count = 0;
        switch (position) {
            case "GOALKEEPER":
                count = getPositionCount(TEAM_COL_3);
                return count < 1;  // Only 1 goalkeeper allowed
            case "DEFENDER":
                count = getPositionCount(TEAM_COL_4) + getPositionCount(TEAM_COL_5);
                return count < 2;  // Maximum 2 defenders allowed
            case "MIDFIELDER":
                count = getPositionCount(TEAM_COL_6) + getPositionCount(TEAM_COL_7);
                return count < 2;  // Maximum 2 midfielders allowed
            case "ATTACKER":
                count = getPositionCount(TEAM_COL_8) + getPositionCount(TEAM_COL_9);
                return count < 2;  // Maximum 2 attackers allowed
            default:
                return false;
        }
    }

    // Helper method to get the count of players in a specific position column
    private int getPositionCount(String positionColumn) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TEAM_TABLE_NAME + " WHERE " + positionColumn + " IS NOT NULL", null);
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

    // Modified insertTeam method to check positions before assigning
    public int insertTeamWithPositionCheck(String teamName, String goalkeeper, String defenderLeft, String defenderRight,
                                           String midfielderLeft, String midfielderRight, String forwardLeft, String forwardRight, int userId) {
        // Check if each position can be assigned
        if (!canAssignPosition("GOALKEEPER") && goalkeeper != null) {
            Log.d("DatabaseHelper", "Cannot assign goalkeeper, limit reached.");
            return -1;
        }
        if (!canAssignPosition("DEFENDER") && (defenderLeft != null || defenderRight != null)) {
            Log.d("DatabaseHelper", "Cannot assign defender, limit reached.");
            return -1;
        }
        if (!canAssignPosition("MIDFIELDER") && (midfielderLeft != null || midfielderRight != null)) {
            Log.d("DatabaseHelper", "Cannot assign midfielder, limit reached.");
            return -1;
        }
        if (!canAssignPosition("ATTACKER") && (forwardLeft != null || forwardRight != null)) {
            Log.d("DatabaseHelper", "Cannot assign attacker, limit reached.");
            return -1;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(TEAM_COL_2, teamName);
        contentValues.put(TEAM_COL_3, goalkeeper);
        contentValues.put(TEAM_COL_4, defenderLeft);
        contentValues.put(TEAM_COL_5, defenderRight);
        contentValues.put(TEAM_COL_6, midfielderLeft);
        contentValues.put(TEAM_COL_7, midfielderRight);
        contentValues.put(TEAM_COL_8, forwardLeft);
        contentValues.put(TEAM_COL_9, forwardRight);
        contentValues.put(TEAM_COL_10, userId);

        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.insert(TEAM_TABLE_NAME, null, contentValues);
        db.close();

        // Return result or other specific code if needed
        return (int) result;
    }
}
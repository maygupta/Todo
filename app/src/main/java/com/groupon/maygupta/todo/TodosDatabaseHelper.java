package com.groupon.maygupta.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maygupta on 9/21/15.
 */
public class TodosDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "TodosDatabaseHelper";

    // Database Instance
    private static TodosDatabaseHelper sInstance;

    // Database Info
    private static final String DATABASE_NAME = "todosDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_TODOS = "todos";

    // Todo Table Columns
    private static final String KEY_TODO_ID = "id";
    private static final String KEY_TODO_TEXT = "text";
    private static final String KEY_TODO_DUE_DATE = "dueDate";
    private static final String KEY_TODO_PRIORITY = "priority";


    // Singleton Pattern
    public static synchronized TodosDatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new TodosDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public TodosDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TODOS_TABLE = "CREATE TABLE " + TABLE_TODOS +
                "(" +
                    KEY_TODO_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                    KEY_TODO_TEXT + " TEXT," +
                    KEY_TODO_DUE_DATE + " TEXT," +
                    KEY_TODO_PRIORITY + " TEXT" +
                ")";
        db.execSQL(CREATE_TODOS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODOS);
            onCreate(db);
        }
    }

    public long addTodo(Todo todo) {
        SQLiteDatabase db = getWritableDatabase();
        long todoId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_TODO_TEXT, todo.text);
            values.put(KEY_TODO_DUE_DATE, todo.dueDate);
            values.put(KEY_TODO_PRIORITY, todo.priority);

            todoId = db.insertOrThrow(TABLE_TODOS, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add todo due to :" + e.getMessage());
        } finally {
            db.endTransaction();
        }
        return todoId;
    }

    public void updateTodo(Todo todo) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {

            ContentValues values = new ContentValues();
            values.put(KEY_TODO_TEXT, todo.text);
            values.put(KEY_TODO_DUE_DATE, todo.dueDate);
            values.put(KEY_TODO_PRIORITY, todo.priority);

            int rows = db.update(TABLE_TODOS, values, KEY_TODO_ID + "= ?", new String[]{todo.id});
            db.setTransactionSuccessful();

            // Check if there was a matching row
            if (rows != 1) {
                Log.d(TAG, String.format("Unable to find todo with id %s", todo.id));
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to update todo to database due to :" + e.getMessage() );
        } finally {
            db.endTransaction();
        }
    }

    public List<Todo> getAllTodos() {
        List<Todo> todos = new ArrayList<>();

        String TODOS_SELECT_QUERY =
                String.format("SELECT * FROM %s", TABLE_TODOS);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(TODOS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Todo newTodo = new Todo(cursor.getString(cursor.getColumnIndex(KEY_TODO_TEXT)),
                            cursor.getString(cursor.getColumnIndex(KEY_TODO_DUE_DATE)),
                            cursor.getString(cursor.getColumnIndex(KEY_TODO_PRIORITY))
                    );
                    newTodo.id = cursor.getString(cursor.getColumnIndex(KEY_TODO_ID));

                    todos.add(newTodo);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get todos from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return todos;
    }

    public void deleteTodo(Todo todo) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            db.delete(TABLE_TODOS, KEY_TODO_ID + "=" + todo.id, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, String.format("Error while trying to delete todo with id = %s and text = %s", todo.id, todo.text));
        } finally {
            db.endTransaction();
        }
    }

}

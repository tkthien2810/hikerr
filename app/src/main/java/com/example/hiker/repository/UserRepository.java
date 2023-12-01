package com.example.hiker.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.example.hiker.DatabaseHelper;

public class UserRepository {

    public static DatabaseHelper databaseHelper;

    public UserRepository(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public void insertUser(String username, String password, RegisterCallback callback) {
        new InsertUserAsyncTask(callback).execute(username, password);
    }

    public void loginUser(String username, String password, LoginCallback callback) {
        new LoginUserAsyncTask(callback).execute(username, password);
    }

    public interface LoginCallback {
        void onLoginResult(boolean success);
    }

    public interface RegisterCallback {
        void onRegisterResult(boolean success);
    }


    private static class InsertUserAsyncTask extends AsyncTask<String, Void, Boolean> {

        private RegisterCallback callback;

        public InsertUserAsyncTask(RegisterCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String username = params[0];
            String password = params[1];

            // Kiểm tra xem người dùng đã tồn tại chưa
            if (!checkUserExists(username)) {
                // Thêm người dùng vào cơ sở dữ liệu
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_USERNAME, username);
                values.put(DatabaseHelper.COLUMN_PASSWORD, password);
                databaseHelper.getWritableDatabase().insert(DatabaseHelper.TABLE_USERS, null, values);
                return true; // Đăng ký thành công
            } else {
                return false; // Người dùng đã tồn tại
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (callback != null) {
                callback.onRegisterResult(success);
            }
        }
    }

    private static class LoginUserAsyncTask extends AsyncTask<String, Void, Boolean> {

        private LoginCallback callback;

        public LoginUserAsyncTask(LoginCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String username = params[0];
            String password = params[1];

            // Kiểm tra đăng nhập
            return checkLogin(username, password);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (callback != null) {
                callback.onLoginResult(success);
            }
        }
    }
    private static boolean checkUserExists(String username) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, null, DatabaseHelper.COLUMN_USERNAME + " = ?",
                new String[]{username}, null, null, null);

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    private static boolean checkLogin(String username, String password) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, null,
                DatabaseHelper.COLUMN_USERNAME + " = ? AND " + DatabaseHelper.COLUMN_PASSWORD + " = ?",
                new String[]{username, password}, null, null, null);

        boolean success = cursor.getCount() > 0;
        cursor.close();
        return success;
    }
}



package pe.alinet.billetera2;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Created by Aliosh on 15/04/2017.
 */
public class BilleteraContentProvider extends ContentProvider {

    public static final Uri CONTENT_URI =
            Uri.parse("content://pe.alinet.billetera/items");

    public static final String KEY_ID = "_id";
    public static final String KEY_QUANTITY = "quantity";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_CREATION_DATE = "creation_date";

    private MySQLiteOpenHelper myOpenHelper;

    @Override
    public boolean onCreate(){

        myOpenHelper = new MySQLiteOpenHelper(getContext(), MySQLiteOpenHelper.DATABASE_NAME, null,
                MySQLiteOpenHelper.DATABASE_VERSION);
        return true;

    }

    private static final int ALLROWS = 1;
    private static final int SINGLE_ROW = 2;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("pe.alinet.billetera", "items", ALLROWS);
        uriMatcher.addURI("pe.alinet.billetera", "items/#", SINGLE_ROW);
    }

    @Override
    public String  getType(Uri uri){
        switch (uriMatcher.match(uri)) {
            case ALLROWS: return "vnd.android.cursor.dir/pe.alinet.billetera";
            case SINGLE_ROW: return "vnd.android.cursor.item/pe.alinet.billetera";
            default: throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder){
        // Open a read-only database.
        SQLiteDatabase db = myOpenHelper.getWritableDatabase();

        // Replace these with valid SQL statements if necessary.
        String groupBy = null;
        String having = null;
        sortOrder = "_id desc";

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(MySQLiteOpenHelper.DATABASE_TABLE);

        // If this is a row query, limit the result set to the passed in row.
        switch (uriMatcher.match(uri)) {
            case SINGLE_ROW :
                String rowID = uri.getPathSegments().get(1);
                queryBuilder.appendWhere(KEY_ID + "=" + rowID);
            default: break;
        }

        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, groupBy, having, sortOrder);



        return cursor;
    }


    @Override
    public Uri insert(Uri url, ContentValues values){
        // Open a read / write database to support the transaction.
        SQLiteDatabase db = myOpenHelper.getWritableDatabase();

        // To add empty rows to your database by passing in an empty Content Values
        // object, you must use the null column hack parameter to specify the name of
        // the column that can be set to null.
        String nullColumnHack = null;

        // Insert the values into the table
        long id = db.insert(MySQLiteOpenHelper.DATABASE_TABLE,
                nullColumnHack, values);

        if (id > -1) {
            // Construct and return the URI of the newly inserted row.
            Uri insertedId = ContentUris.withAppendedId(CONTENT_URI, id);

            // Notify any observers of the change in the data set.
            getContext().getContentResolver().notifyChange(insertedId, null);

            return insertedId;
        }
        else
            return null;

    }

    @Override
    public int delete(Uri uri,String selection, String[] selectionArgs){
        // Open a read / write database to support the transaction.
        SQLiteDatabase db = myOpenHelper.getWritableDatabase();

        // If this is a row URI, limit the deletion to the specified row.
        switch (uriMatcher.match(uri)) {
            case SINGLE_ROW :
                String rowID = uri.getPathSegments().get(1);
                selection = KEY_ID + "=" + rowID
                        + (!TextUtils.isEmpty(selection) ?
                        " AND (" + selection + ')' : "");
            default: break;
        }

        // To return the number of deleted items, you must specify a where
        // clause. To delete all rows and return a value, pass in "1".
        if (selection == null)
            selection = "1";

        // Execute the deletion.
        int deleteCount = db.delete(MySQLiteOpenHelper.DATABASE_TABLE, selection, selectionArgs);

        // Notify any observers of the change in the data set.
        getContext().getContentResolver().notifyChange(uri, null);

        return deleteCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
        String[] selectionArgs) {

        // Open a read / write database to support the transaction.
        SQLiteDatabase db = myOpenHelper.getWritableDatabase();

        // If this is a row URI, limit the deletion to the specified row.
        switch (uriMatcher.match(uri)) {
            case SINGLE_ROW :
                String rowID = uri.getPathSegments().get(1);
                selection = KEY_ID + "=" + rowID
                        + (!TextUtils.isEmpty(selection) ?
                        " AND (" + selection + ')' : "");
            default: break;
        }

        // Perform the update.
        int updateCount = db.update(MySQLiteOpenHelper.DATABASE_TABLE,
                values, selection, selectionArgs);

        // Notify any observers of the change in the data set.
        getContext().getContentResolver().notifyChange(uri, null);

        return updateCount;

    }

    private static class MySQLiteOpenHelper extends SQLiteOpenHelper{

        private static final String DATABASE_NAME = "billetera.db";
        private static final int DATABASE_VERSION = 5;
        private static final String DATABASE_TABLE = "tb_billetera";

        private static final String DATABASE_CREATE = "create table "+
                DATABASE_TABLE + " ( "+KEY_ID+" integer primary key autoincrement, "+
                KEY_QUANTITY + " real not null, "+
                KEY_DESCRIPTION + " text not null, " +
                KEY_CREATION_DATE + " long not null );";

        public MySQLiteOpenHelper(Context context, String name,
                                  SQLiteDatabase.CursorFactory factory, int version){
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE);
            onCreate(db);
        }
    }
}

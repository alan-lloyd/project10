package androidbasicsnd.lloyd.alan.com.udacity.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

    /**
     * Database helper for Inventory  app. Manages database creation and version management.
     */
    public class InventoryDbHelper  extends SQLiteOpenHelper {

        public static final String LOG_TAG = InventoryDbHelper.class.getSimpleName();

        /** Name of the database file */
        private static final String DATABASE_NAME = "inventories.db";

        /**
         * Database version. If you change the database schema, you must increment the database version.
         */
        private static final int DATABASE_VERSION = 1;


          // Constructs a new instance of InventoryDbHelper
        public InventoryDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

         // This is called when the database is created for the first time
         //  @Override
        public void onCreate(SQLiteDatabase db) {
            // Create a String that contains the SQL statement to create the inventory  table
            String SQL_CREATE_INVENTORY_TABLE =  "CREATE TABLE " + InventoryContract.InventoryEntry.TABLE_NAME + " ("
                    + InventoryContract.InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME + " TEXT NOT NULL, "
                    + InventoryContract.InventoryEntry.COLUMN_INVENTORY_INFO + " TEXT, "
                    + InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE + " REAL NOT NULL DEFAULT 0.00 , "
                    + InventoryContract.InventoryEntry.COLUMN_ITEMS_IN_STOCK + " INTEGER NOT NULL, "
                    + InventoryContract.InventoryEntry.COLUMN_ITEM_IMAGE + " TEXT  NOT NULL ); ";

            // Execute the SQL statement
            db.execSQL(SQL_CREATE_INVENTORY_TABLE);
        }

        /**
         * This is called when the database needs to be upgraded.
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // The database is still at version 1, so there's nothing to do be done here.
        }
    }


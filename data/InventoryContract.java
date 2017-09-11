package androidbasicsnd.lloyd.alan.com.udacity.inventory.data;

import android.net.Uri;
import android.content.ContentResolver;
import android.provider.BaseColumns;


//API Contract for the Inventory app
public class InventoryContract {

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "androidbasicsnd.lloyd.alan.com.udacity.inventory";
    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.inventory/inventory/ is a valid path for
     * looking at inventory data. content://com.example.android.inventory/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_INVENTORY = "inventory";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private InventoryContract() {
    }

    /**
     * Inner class that defines constant values for the inventory  database table
     * Each entry in the table represents a single inventory
     */
    public static final class InventoryEntry implements BaseColumns {

        /**
         * The content URI to access the inventory data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        /**
         * The MIME type of the  CONTENT_URI  for a list of inventory
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        /**
         * The MIME type of the  CONTENT_URI  for a single inventory
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        /**
         * Name of database table for inventory
         */
        public final static String TABLE_NAME = "inventory";

        /**
         * Unique ID number for the inventory (only for use in the database table)
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the inventory
         * Type: TEXT
         */
        public final static String COLUMN_INVENTORY_NAME = "name";




        public final static String COLUMN_INVENTORY_INFO = "iteminfo";

        /**
         * Price of the inventory items

         */
        public final static String COLUMN_INVENTORY_PRICE = "price";

        public final static String COLUMN_ITEMS_IN_STOCK = "stock";

        public final static String COLUMN_ITEM_IMAGE = "image";
    }
}



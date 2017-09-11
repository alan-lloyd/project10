package androidbasicsnd.lloyd.alan.com.udacity.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import androidbasicsnd.lloyd.alan.com.udacity.inventory.data.InventoryContract;
import androidbasicsnd.lloyd.alan.com.udacity.inventory.data.InventoryContract.InventoryEntry;

import static androidbasicsnd.lloyd.alan.com.udacity.inventory.data.InventoryContract.InventoryEntry.TABLE_NAME;

//InventoryCursorAdapter is an adapter for a list or grid view that uses a Cursor of
// inventory data as its data source. This adapter knows how to create list items
// for each row of pet data in the Cursor
public class InventoryCursorAdapter extends CursorAdapter {


    public int stockLevel=0;
    // EditText field to enter inventory item's name
    //   public TextView mStockText;
    public Button decrementStockButton;
    TextView stockTextView;
String productQuantity;

    //Constructs a new InventoryCursorAdapter. context The context.
    //c   The cursor from which to get the data.
    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    //Makes a new blank list item view. No data is set (or bound) to the views yet.
    //context app context. cursor  The cursor from which to get the data. The cursor is already
    // moved to the correct position.parent  The parent to which the new view is attached to
    //return the newly created list item view.
    @Override
    public View newView( Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }


    //This method binds the pet data (in the current row pointed to by cursor) to the given
    //list item layout. For example, the name for the current pet can be set on the name TextView
    //in the list item layout.
    //view    Existing view, returned earlier by newView() method //context app context
    //cursor  The cursor from which to get the data. The cursor is already moved to the
    // correct row
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {


        // String initialStockLevelPerItem = "101";
//THESE THINGS APPEAR IN THE LIST-ITEM SUMMARY PAGE

        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView infoSummaryTextView = (TextView) view.findViewById(R.id.summary);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        stockTextView = (TextView) view.findViewById(R.id.stock);
        decrementStockButton = (Button) view.findViewById(R.id.decrement_stock_button);
        // Button decrementStockButton = (Button) view.findViewById(R.id.decrement_stock_button);

        // Find the columns of item attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME);
        int infoColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_INFO);
        int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE);
        int stockColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEMS_IN_STOCK);


        // Read the item attributes from the Cursor for the current item
        String petName = cursor.getString(nameColumnIndex);
        String itemInfo = cursor.getString(infoColumnIndex);
        String itemPrice = cursor.getString(priceColumnIndex);
        int itemStock = cursor.getInt(stockColumnIndex);


        // Update the TextViews with the attributes for the current item
        nameTextView.setText(petName);
        infoSummaryTextView.setText(itemInfo);
        priceTextView.setText(itemPrice);

        stockTextView.setText(String.valueOf(itemStock));

        //get the position before the button is clicked
        final int position = cursor.getPosition();


        // Read the product attributes from the Cursor for the current product
       // productQuantity = cursor.getString(quantityColumnIndex);

        final int rowId = cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryEntry._ID));


       final int oldStock = itemStock;
        //listener for "Sales" button in ListItem view, decrements in 1's
        decrementStockButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int newStock  = oldStock;
                if (newStock > 0) {
                    newStock  = newStock - 1;
                }
                ContentValues values = new ContentValues();
                Uri updateUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, rowId);
                values.put(InventoryContract.InventoryEntry.COLUMN_ITEMS_IN_STOCK, newStock );
                context.getContentResolver().update(updateUri /*Uri with appended ID*/, values,null, null); //update Content Provider
            }
        });
       // productQuantity = cursor.getString(quantityColumnIndex);//needs update after sales button is clicked




    }
}

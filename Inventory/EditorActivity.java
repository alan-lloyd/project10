package androidbasicsnd.lloyd.alan.com.udacity.inventory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import androidbasicsnd.lloyd.alan.com.udacity.inventory.data.InventoryContract;
import androidbasicsnd.lloyd.alan.com.udacity.inventory.data.InventoryDbHelper;

import static androidbasicsnd.lloyd.alan.com.udacity.inventory.data.InventoryProvider.LOG_TAG;
//DEALS WITH THE EDIT RECORD

/**
 * Allows user to create a new inventory or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String STATE_URI = "STATE_URI";
    // Identifier for the inventory data loader
    private static final int EXISTING_INVENTORY_LOADER = 0;
    private static final int PICK_IMAGE_REQUEST = 0;
    public ImageView mImageView;
    public Uri mUri;
    public ImageView mImageView2;
public int stockLevel;
    public ImageView incrementStockImage;
    public ImageView decrementStockImage;
    TextView stockTextView2;
    // Content URI for the existing inventory (null if it's a new inventory)
    private Uri mCurrentInventoryUri;
    // EditText field to enter inventory item's name
    private EditText mNameEditText;
    // EditText field to enter inventory item's price
    private EditText mPriceEditText;
    // EditText field to enter inventory item background info
    private EditText mItemInfoEditText;
    private TextView mItemStockText;
    private TextView decrementStockImage2;
    private String imagePath;
    // Boolean flag that keeps track of whether the inventory has been edited (true) or not (false)
    private boolean mInventoryHasChanged = false;
    private Button mDecrementButton;
    private TextView mTextView;

    int incrementStockCounter=0;
     int decrementStockCounter=0;





    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mInventoryHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mInventoryHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new inventory or editing an existing one.
        Intent intent = getIntent();
        mCurrentInventoryUri = intent.getData();

        // If the intent DOES NOT contain a inventory content URI, then we know that we are
        // creating a new inventory.
        if (mCurrentInventoryUri == null) {
            // This is a new inventory, so change the app bar to say "Add a Inventory item"
            setTitle(getString(R.string.editor_activity_title_new_inventory));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a inventory that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing inventory, so change app bar to say "Edit Inventory"
            setTitle(getString(R.string.editor_activity_title_edit_inventory));

            // Initialize a loader to read the inventory data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_inventory_name);
        mItemInfoEditText = (EditText) findViewById(R.id.edit_inventory_info);
        mPriceEditText = (EditText) findViewById(R.id.edit_inventory_price);
        mItemStockText = (TextView) findViewById(R.id.stock2);
        mImageView = (ImageView) findViewById(R.id.item_image);
        mImageView2 = (ImageView) findViewById(R.id.item_image2);
        incrementStockImage = (ImageView) findViewById(R.id.item_stock_add_image);
        decrementStockImage = (ImageView) findViewById(R.id.item_stock_delete_image);
       // decrementStockImage2 = (TextView) findViewById(R.id.stock);


        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageSelector();
            }
        });

        //increment stock button in an item record
        incrementStockImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                incrementStockCounter++;
            }
        });

        //increment stock button in an item record
        decrementStockImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                decrementStockCounter++;
            }
        });



        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mItemInfoEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        incrementStockImage.setOnTouchListener(mTouchListener);
        decrementStockImage.setOnTouchListener(mTouchListener);
    }// end of onCreate method


    public void openImageSelector() {
        Intent intent;
        intent = new Intent(Intent.ACTION_OPEN_DOCUMENT); // aob makes all file types accessible.
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");  //this restricts aob selection to imgs only

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST); ////this starts the intent which catches the uri string for the img on phone

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {

            if (resultData != null) {
                try {
                    mUri = resultData.getData();   //catches the string openImageSelector()- this is url to the img

                    //saves URI permission to display imgs, so that img displays will persist after device shut-down
                    int takeFlags = resultData.getFlags();
                    takeFlags &= (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    imagePath = mUri.toString();

                    //check for the latest URI permissions data
                    getContentResolver().takePersistableUriPermission(mUri, takeFlags);

                } catch (Exception e) {
                    e.printStackTrace();
                    //msg user if img not loadable
                    Toast.makeText(EditorActivity.this, "Unable to open image", Toast.LENGTH_LONG).show();
                }

                mImageView2.setImageBitmap(getBitmapFromUri(mUri));  //this displays the img
            }
        }
    }


    // reduce  dynamic heap used by expanding img into a memory array that's already scaled to match the size of the destination view
    public Bitmap getBitmapFromUri(Uri uri) {    //need this method too

        if (uri == null || uri.toString().isEmpty()) {
            return null;
        }

        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        InputStream input = null;
        try {
            input = this.getContentResolver().openInputStream(uri);

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            input = this.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();
            return bitmap;

        } catch (FileNotFoundException fne) {
            Log.e(LOG_TAG, "Failed to load image.", fne);
            return null;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to load image.", e);
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException ioe) {

            }
        }
    }

    // Get user input from editor and save inventory into database
    private void saveInventory() {  //SAVES DATA TO A DB
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String itemInfoString = mItemInfoEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String stockString = String.valueOf(incrementStockCounter-decrementStockCounter); //good code
        String imageString = mUri.toString();

        // Check if this is supposed to be a new inventory
        // and check if all the fields in the editor are blank
        if (mCurrentInventoryUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) && TextUtils.isEmpty(itemInfoString) && TextUtils.isEmpty(stockString) && TextUtils.isEmpty(imageString)) {
            // Since no fields were modified, we can return early without creating a new inventory.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and inventory attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME, nameString);
        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_INFO, itemInfoString);
        values.put(InventoryContract.InventoryEntry.COLUMN_ITEMS_IN_STOCK, stockString);
        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_IMAGE, imageString);
        // If the weight is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        double price = 0.00;
        if (!TextUtils.isEmpty(priceString)) {
            price = Double.parseDouble(priceString);
        }
        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE, price);
        incrementStockCounter =0;
        decrementStockCounter =0;
        // Determine if this is a new or existing inventory by checking if mCurrentInventoryUri is null or not
        if (mCurrentInventoryUri == null) {
            // This is a NEW inventory, so insert a new inventory into the provider,
            // returning the content URI for the new inventory.
            Uri newUri = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_inventory_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_inventory_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING inventory, so update the inventory with content URI: mCurrentInventoryUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentInventoryUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentInventoryUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_inventory_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_inventory_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new inventory, hide the "Delete" menu item.
        if (mCurrentInventoryUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save inventory to database
                saveInventory();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the inventory hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mInventoryHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the inventory hasn't changed, continue with handling back button press
        if (!mInventoryHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all inventory attributes, define a projection that contains
        // all columns from the inventory table
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_INFO,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE,
                InventoryContract.InventoryEntry.COLUMN_ITEM_IMAGE,
                InventoryContract.InventoryEntry.COLUMN_ITEMS_IN_STOCK
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentInventoryUri,         // Query the content URI for the current inventory
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    //method handling reading existing data called up from db, to then display it
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        //READ FROM A DB ROW USING CURSOR, THEN SET IN VIEWS
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of inventory attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME);
            int itemInfoColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_INFO);
            int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE);
            int imageColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_IMAGE);
            int stockColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEMS_IN_STOCK);
            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String itemInfo = cursor.getString(itemInfoColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            String itemImage = cursor.getString(imageColumnIndex);
            int stock = cursor.getInt(stockColumnIndex);


            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mItemInfoEditText.setText(itemInfo);
            mPriceEditText.setText(price);
            //img data from db called up as a string. Converted to a URI format, then img file loaded up via that URI
            // mImageView2.setImageURI(Uri.parse(itemImage));
            mImageView2.setImageBitmap(getBitmapFromUri(Uri.parse(itemImage)));
            mItemStockText.setText(String.valueOf(stock));
           // decrementStockImage2.setText(String.valueOf(stock));
        }
    }//end of onLoadFinished method


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mItemInfoEditText.setText("");
        mPriceEditText.setText("");
        mItemStockText.setText("");
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     * discardButtonClickListener is the click listener for what to do when
     * the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the inventory.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this inventory.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the inventory.
                deleteInventory();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the inventory.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the inventory in the database.
     */
    private void deleteInventory() {
        // Only perform the delete if this is an existing inventory.
        if (mCurrentInventoryUri != null) {
            // Call the ContentResolver to delete the inventory at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentInventoryUri
            // content URI already identifies the inventory that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentInventoryUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_inventory_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_inventory_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}
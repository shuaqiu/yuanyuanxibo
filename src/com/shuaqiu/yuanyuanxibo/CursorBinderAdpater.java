/**
 * 
 */
package com.shuaqiu.yuanyuanxibo;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.ResourceCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author shuaqiu May 6, 2013
 */
public class CursorBinderAdpater extends ResourceCursorAdapter {

    private static final String TAG = "cursoradapter";

    private ViewBinder mBinder;

    private int mStringConversionColumn = -1;
    private CursorToStringConverter mCursorToStringConverter;

    /**
     * Standard constructor.
     * 
     * @param context
     *            The context where the ListView associated with this
     *            SimpleListItemFactory is running
     * @param layout
     *            resource identifier of a layout file that defines the views
     *            for this list item. The layout file should include at least
     *            those named views defined in "to"
     * @param c
     *            The database cursor. Can be null if the cursor is not
     *            available yet.
     * @param flags
     *            Flags used to determine the behavior of the adapter, as per
     *            {@link CursorAdapter#CursorAdapter(Context, Cursor, int)}.
     */
    public CursorBinderAdpater(Context context, int layout, Cursor c, int flags) {
        super(context, layout, c, flags);
    }

    /**
     * Binds all of the field names passed into the "to" parameter of the
     * constructor with their corresponding cursor columns as specified in the
     * "from" parameter. Binding occurs in two phases. First, if a
     * {@link android.widget.SimpleCursorAdapter.ViewBinder} is available,
     * {@link ViewBinder#setViewValue(android.view.View, android.database.Cursor, int)}
     * is invoked. If the returned value is true, binding has occured. If the
     * returned value is false and the view to bind is a TextView,
     * {@link #setViewText(TextView, String)} is invoked. If the returned value
     * is false and the view to bind is an ImageView,
     * {@link #setViewImage(ImageView, String)} is invoked. If no appropriate
     * binding can be found, an {@link IllegalStateException} is thrown.
     * 
     * @throws IllegalStateException
     *             if binding cannot occur
     * @see android.widget.CursorAdapter#bindView(android.view.View,
     *      android.content.Context, android.database.Cursor)
     * @see #getViewBinder()
     * @see #setViewBinder(android.widget.SimpleCursorAdapter.ViewBinder)
     * @see #setViewImage(ImageView, String)
     * @see #setViewText(TextView, String)
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String text = cursor.getString(2);
        JSONObject data;
        try {
            data = new JSONObject(text);
            mBinder.bindView(view, data);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * Returns the {@link ViewBinder} used to bind data to views.
     * 
     * @return a ViewBinder or null if the binder does not exist
     * @see #bindView(android.view.View, android.content.Context,
     *      android.database.Cursor)
     * @see #setViewBinder(android.widget.SimpleCursorAdapter.ViewBinder)
     */
    public ViewBinder getViewBinder() {
        return mBinder;
    }

    /**
     * Sets the binder used to bind data to views.
     * 
     * @param viewBinder
     *            the binder used to bind data to views, can be null to remove
     *            the existing binder
     * @see #bindView(android.view.View, android.content.Context,
     *      android.database.Cursor)
     * @see #getViewBinder()
     */
    public void setViewBinder(ViewBinder viewBinder) {
        mBinder = viewBinder;
    }

    /**
     * Return the index of the column used to get a String representation of the
     * Cursor.
     * 
     * @return a valid index in the current Cursor or -1
     * @see android.widget.CursorAdapter#convertToString(android.database.Cursor)
     * @see #setStringConversionColumn(int)
     * @see #setCursorToStringConverter(android.widget.SimpleCursorAdapter.CursorToStringConverter)
     * @see #getCursorToStringConverter()
     */
    public int getStringConversionColumn() {
        return mStringConversionColumn;
    }

    /**
     * Defines the index of the column in the Cursor used to get a String
     * representation of that Cursor. The column is used to convert the Cursor
     * to a String only when the current CursorToStringConverter is null.
     * 
     * @param stringConversionColumn
     *            a valid index in the current Cursor or -1 to use the default
     *            conversion mechanism
     * @see android.widget.CursorAdapter#convertToString(android.database.Cursor)
     * @see #getStringConversionColumn()
     * @see #setCursorToStringConverter(android.widget.SimpleCursorAdapter.CursorToStringConverter)
     * @see #getCursorToStringConverter()
     */
    public void setStringConversionColumn(int stringConversionColumn) {
        mStringConversionColumn = stringConversionColumn;
    }

    /**
     * Returns the converter used to convert the filtering Cursor into a String.
     * 
     * @return null if the converter does not exist or an instance of
     *         {@link android.widget.SimpleCursorAdapter.CursorToStringConverter}
     * @see #setCursorToStringConverter(android.widget.SimpleCursorAdapter.CursorToStringConverter)
     * @see #getStringConversionColumn()
     * @see #setStringConversionColumn(int)
     * @see android.widget.CursorAdapter#convertToString(android.database.Cursor)
     */
    public CursorToStringConverter getCursorToStringConverter() {
        return mCursorToStringConverter;
    }

    /**
     * Sets the converter used to convert the filtering Cursor into a String.
     * 
     * @param cursorToStringConverter
     *            the Cursor to String converter, or null to remove the
     *            converter
     * @see #setCursorToStringConverter(android.widget.SimpleCursorAdapter.CursorToStringConverter)
     * @see #getStringConversionColumn()
     * @see #setStringConversionColumn(int)
     * @see android.widget.CursorAdapter#convertToString(android.database.Cursor)
     */
    public void setCursorToStringConverter(
            CursorToStringConverter cursorToStringConverter) {
        mCursorToStringConverter = cursorToStringConverter;
    }

    /**
     * Returns a CharSequence representation of the specified Cursor as defined
     * by the current CursorToStringConverter. If no CursorToStringConverter has
     * been set, the String conversion column is used instead. If the conversion
     * column is -1, the returned String is empty if the cursor is null or
     * Cursor.toString().
     * 
     * @param cursor
     *            the Cursor to convert to a CharSequence
     * @return a non-null CharSequence representing the cursor
     */
    @Override
    public CharSequence convertToString(Cursor cursor) {
        if (mCursorToStringConverter != null) {
            return mCursorToStringConverter.convertToString(cursor);
        } else if (mStringConversionColumn > -1) {
            return cursor.getString(mStringConversionColumn);
        }

        return super.convertToString(cursor);
    }

    @Override
    public Cursor swapCursor(Cursor c) {
        Cursor res = super.swapCursor(c);
        return res;
    }

    /**
     * This class can be used by external clients of SimpleCursorAdapter to
     * define how the Cursor should be converted to a String.
     * 
     * @see android.widget.CursorAdapter#convertToString(android.database.Cursor)
     */
    public static interface CursorToStringConverter {
        /**
         * Returns a CharSequence representing the specified Cursor.
         * 
         * @param cursor
         *            the cursor for which a CharSequence representation is
         *            requested
         * @return a non-null CharSequence representing the cursor
         */
        CharSequence convertToString(Cursor cursor);
    }

}

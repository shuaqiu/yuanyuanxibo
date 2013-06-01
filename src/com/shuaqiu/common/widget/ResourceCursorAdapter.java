/**
 * 
 */
package com.shuaqiu.common.widget;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author shuaqiu 2013-6-2
 */
public abstract class ResourceCursorAdapter extends CursorAdapter {
    private int mLayout;

    private int mDropDownLayout;

    private LayoutInflater mInflater;

    /**
     * Standard constructor.
     * 
     * @param context
     *            The context where the ListView associated with this adapter is
     *            running
     * @param layout
     *            Resource identifier of a layout file that defines the views
     *            for this list item. Unless you override them later, this will
     *            define both the item views and the drop down views.
     */
    public ResourceCursorAdapter(Context context, int layout) {
        this(context, layout, null, 0);
    }

    /**
     * Standard constructor.
     * 
     * @param context
     *            The context where the ListView associated with this adapter is
     *            running
     * @param layout
     *            Resource identifier of a layout file that defines the views
     *            for this list item. Unless you override them later, this will
     *            define both the item views and the drop down views.
     * @param flags
     *            Flags used to determine the behavior of the adapter,
     *            as per
     *            {@link CursorAdapter#CursorAdapter(Context, Cursor, int)}.
     */
    public ResourceCursorAdapter(Context context, int layout, int flags) {
        this(context, layout, null, flags);
    }

    /**
     * Standard constructor.
     * 
     * @param context
     *            The context where the ListView associated with this adapter is
     *            running
     * @param layout
     *            Resource identifier of a layout file that defines the views
     *            for this list item. Unless you override them later, this will
     *            define both the item views and the drop down views.
     * @param c
     *            The cursor from which to get the data.
     */
    public ResourceCursorAdapter(Context context, int layout, Cursor c) {
        this(context, layout, c, 0);
    }

    /**
     * Standard constructor.
     * 
     * @param context
     *            The context where the ListView associated with this adapter is
     *            running
     * @param layout
     *            Resource identifier of a layout file that defines the views
     *            for this list item. Unless you override them later, this will
     *            define both the item views and the drop down views.
     * @param c
     *            The cursor from which to get the data.
     * @param flags
     *            Flags used to determine the behavior of the adapter,
     *            as per
     *            {@link CursorAdapter#CursorAdapter(Context, Cursor, int)}.
     */
    public ResourceCursorAdapter(Context context, int layout, Cursor c,
            int flags) {
        super(context, c, flags);
        mLayout = mDropDownLayout = layout;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Inflates view(s) from the specified XML file.
     * 
     * @see android.widget.CursorAdapter#newView(android.content.Context,
     *      android.database.Cursor, ViewGroup)
     */
    @Override
    protected View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(mLayout, parent, false);
    }

    @Override
    protected View newDropDownView(Context context, Cursor cursor,
            ViewGroup parent) {
        return mInflater.inflate(mDropDownLayout, parent, false);
    }

    /**
     * <p>
     * Sets the layout resource of the item views.
     * </p>
     * 
     * @param layout
     *            the layout resources used to create item views
     */
    public void setViewResource(int layout) {
        mLayout = layout;
    }

    /**
     * <p>
     * Sets the layout resource of the drop down views.
     * </p>
     * 
     * @param dropDownLayout
     *            the layout resources used to create drop down views
     */
    public void setDropDownViewResource(int dropDownLayout) {
        mDropDownLayout = dropDownLayout;
    }
}

/**
 * 
 */
package com.shuaqiu.yuanyuanxibo;

import org.json.JSONArray;

import android.view.View;

/**
 * @author shuaqiu 2013-5-1
 *
 */
public interface ViewBinder {

    public abstract void bindView(int position, View view);

    public abstract JSONArray getDataItems();

}
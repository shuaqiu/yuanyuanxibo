/**
 * 
 */
package com.shuaqiu.common.widget;

import android.view.View;

/**
 * @author shuaqiu 2013-5-1
 */
public interface ViewBinder<Data> {

    void bindView(View view, final Data data);

}
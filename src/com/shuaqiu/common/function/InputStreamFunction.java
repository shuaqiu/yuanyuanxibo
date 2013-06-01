/**
 * 
 */
package com.shuaqiu.common.function;

import java.io.InputStream;

/**
 * @author shuaqiu May 2, 2013
 */
public interface InputStreamFunction<Result> extends
        Function<InputStream, Result> {

    /**
     * 對輸入流進行處理
     * 
     * @param in
     *            要處理的輸入流, 操作完成后不需要進行關閉, 關閉操作在外層進行.
     */
    @Override
    Result apply(InputStream in);
}

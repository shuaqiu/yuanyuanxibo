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

    @Override
    Result apply(InputStream in);
}

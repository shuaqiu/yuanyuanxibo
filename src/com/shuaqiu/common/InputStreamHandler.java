/**
 * 
 */
package com.shuaqiu.common;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author shuaqiu May 2, 2013
 */
public interface InputStreamHandler<T> {

    T handle(InputStream in) throws IOException;
}

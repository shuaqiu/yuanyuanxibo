/**
 * 
 */
package com.shuaqiu.common.task;

/**
 * @author shuaqiu May 4, 2013
 */
public interface AsyncTaskListener<Result> {

    void onPostExecute(Result result);
}

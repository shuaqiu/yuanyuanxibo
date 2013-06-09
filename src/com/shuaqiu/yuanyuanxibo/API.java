/**
 * 
 */
package com.shuaqiu.yuanyuanxibo;

/**
 * @author shuaqiu May 2, 2013
 */
public interface API {

    String API = "https://api.weibo.com/";

    String CLIENT_ID = "209297170";

    String CLIENT_SECRET = "6252a5c211047a0007a149d91c369d9d";

    String REDIRECT_URI = "http://shuaqiu.github.io";

    interface OAuth {

        /**
         * 根據APP_KEY 獲取一個code, 根據這個code 和APP_KEY 來獲取access_token
         */
        String AUTHORIZE = "oauth2/authorize";

        /**
         * 用於獲取access_token, 這個access_token 是用來調用其他API 必須的參數
         */
        String ACCESS_TOKEN = "oauth2/access_token";

    }

    interface User {

        /**
         * 根據用戶的ID 或者screen_name 獲取用戶信息
         */
        String USER_INFO = "2/users/show.json";

    }

    interface Status {

        // ----------------------讀取接口----------------------------

        /**
         * 獲取當前登錄用戶及其所關注用戶的最新微博
         */
        String FRIEND_TIMELINE = "2/statuses/friends_timeline.json";

        // ----------------------寫入接口----------------------------

        /**
         * 轉發一條微博
         * <p>
         * HTTP請求：POST
         * </p>
         * 參數：
         * <ul>
         * <li><b>access_token</b>：獲取的用戶授權token, <b>必填</b>, string</li>
         * <li><b>id</b>：需要轉發的微博ID, <b>必填</b>, int64</li>
         * <li><b>status</b>：添加的轉發內容, 必須做URLEncode, 內容不超過140 個漢字, <b>可選</b>,
         * 不填則默認爲"轉發微博", string</li>
         * <li><b>is_comment</b>：是否在轉發的同時發表評論, 0: 否, 1: 評論給當前微博, 2: 評論給原微博, 3:
         * 都評論, 默認爲0, 可選, int</li>
         * </ul>
         */
        String REPOST = "2/statuses/repost.json";
    }

    interface Comment {

        // ----------------------讀取接口----------------------------

        /**
         * 獲取當前登錄用戶的最新評論, 包括接收到的, 和發出的
         * <p>
         * HTTP請求：GET
         * </p>
         * 參數：
         * <ul>
         * <li><b>access_token</b>：獲取的用戶授權token, <b>必填</b>, string</li>
         * <li><b>since_id</b>：若指定此參數, 則返回ID 比since_id 大的評論（即比since_id 晚的評論）,
         * 默認爲0, 可選, int64</li>
         * <li><b>max_id</b>：若指定此參數, 則返回ID 小於或等於max_id 的評論, 默認爲0, 可選, int64</li>
         * <li><b>count</b>：單頁返回的記錄數, 默認爲50, 可選, int</li>
         * <li><b>page</b>：返回結果的頁碼, 默認爲1, 可選, int</li>
         * </ul>
         */
        String TIMELINE = "2/comments/timeline.json";

        /**
         * 根據微博ID 返回微博的評論列表
         * <p>
         * HTTP請求：GET
         * </p>
         * 參數：
         * <ul>
         * <li><b>access_token</b>：獲取的用戶授權token, <b>必填</b>, string</li>
         * <li><b>id</b>：需要查詢的微博ID, <b>必填</b>, int64</li>
         * <li><b>since_id</b>：若指定此參數, 則返回ID 比since_id 大的評論（即比since_id 晚的評論）,
         * 默認爲0, 可選, int64</li>
         * <li><b>max_id</b>：若指定此參數, 則返回ID 小於或等於max_id 的評論, 默認爲0, 可選, int64</li>
         * <li><b>count</b>：單頁返回的記錄數, 默認爲50, 可選, int</li>
         * <li><b>page</b>：返回結果的頁碼, 默認爲1, 可選, int</li>
         * <li><b>filter_by_author</b>：作者的篩選類型, 0: 全部, 1: 我關注的人, 2: 陌生人, 默認爲0,
         * 可選, int</li>
         * </ul>
         */
        String SHOW = "2/comments/show.json";

        // ----------------------寫入接口----------------------------

        /**
         * 對一條微博進行評論
         * <p>
         * HTTP請求：POST
         * </p>
         * 參數：
         * <ul>
         * <li><b>access_token</b>：獲取的用戶授權token, <b>必填</b>, string</li>
         * <li><b>id</b>：需要評論的微博ID, <b>必填</b>, int64</li>
         * <li><b>comment</b>：評論內容, 必須做URLEncode, 內容不超過140 個漢字, <b>必填</b>,
         * string</li>
         * <li><b>comment_ori</b>：當評論轉發微博時, 是否評論給原微博, 0: 否, 1: 是, 默認爲0, 可選, int</li>
         * </ul>
         */
        String CREATE = "2/comments/create.json";

        /**
         * 回復一條評論
         * <p>
         * HTTP請求：POST
         * </p>
         * 參數：
         * <ul>
         * <li><b>access_token</b>：獲取的用戶授權token, <b>必填</b>, string</li>
         * <li><b>id</b>：需要評論的微博ID, <b>必填</b>, int64</li>
         * <li><b>cid</b>：需要評論的評論ID, <b>必填</b>, int64</li>
         * <li><b>comment</b>：評論內容, 必須做URLEncode, 內容不超過140 個漢字, <b>必填</b>,
         * string</li>
         * <li><b>comment_ori</b>：當評論轉發微博時, 是否評論給原微博, 0: 否, 1: 是, 默認爲0, 可選, int</li>
         * <li><b>without_mention</b>：回復中是否自動加入"回復@用戶名", 0: 是, 1: 否, 默認爲0, 可選,
         * int</li>
         * </ul>
         */
        String REPLY = "2/comments/reply.json";

    }

}

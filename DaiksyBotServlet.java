package bot_n_vengio;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.text.DecimalFormat;
import java.util.HashMap;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.Paging;

/**
 * ブランド力測定Twitter Bot
 * 基幹部分の実装はこちらを参考にした。
 * 
 * http://sites.google.com/site/elekmole/gaebottop
 * 
 * @author daiksy
 *
 */
@SuppressWarnings("serial")
public class DaiksyBotServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(DaiksyBotServlet.class.getName());
    private final String BOTNAME = "bot_n_vengio";
    private final String CACHED_TOKEN = "access_token";
    private final String CACHED_SECRET = "access_secret";
    private static final HashMap<String, Integer> DATA_MAP = new HashMap<String, Integer>();
    static{
    	DATA_MAP.put("ア", 25);
    	DATA_MAP.put("イ", 41);
    	DATA_MAP.put("ウ", 6);
        DATA_MAP.put("エ", 21);
        DATA_MAP.put("オ", 6);
        
        DATA_MAP.put("カ", 4);
        DATA_MAP.put("キ", 8);
        DATA_MAP.put("ク", 15);
        DATA_MAP.put("ケ", 3);
        DATA_MAP.put("コ", 5);       

        DATA_MAP.put("サ", 6);
        DATA_MAP.put("シ", 24);
        DATA_MAP.put("ス", 30);
        DATA_MAP.put("セ", 5);
        DATA_MAP.put("ソ", 6);
        
        DATA_MAP.put("タ", 4);
        DATA_MAP.put("チ", 12);
        DATA_MAP.put("ツ", 1);
        DATA_MAP.put("テ", 12);
        DATA_MAP.put("ト", 19);
        
        DATA_MAP.put("ナ", 14);
        DATA_MAP.put("二", 8);
        DATA_MAP.put("ヌ", 3);
        DATA_MAP.put("ネ", 1);
        DATA_MAP.put("ノ", 0);        
        
        DATA_MAP.put("ハ", 3);
        DATA_MAP.put("ヒ", 0);
        DATA_MAP.put("フ", 14);
        DATA_MAP.put("ヘ", 1);
        DATA_MAP.put("ホ", 1);
        
        DATA_MAP.put("マ", 12);
        DATA_MAP.put("ミ", 9);
        DATA_MAP.put("ム", 6);
        DATA_MAP.put("メ", 2);
        DATA_MAP.put("モ", 7);
        
        DATA_MAP.put("ヤ", 1);
        DATA_MAP.put("ユ", 0);
        DATA_MAP.put("ヨ", 0);
        
        DATA_MAP.put("ラ", 39);
        DATA_MAP.put("リ", 29);
        DATA_MAP.put("ル", 45);
        DATA_MAP.put("レ", 10);
        DATA_MAP.put("ロ", 16);
        
        DATA_MAP.put("ワ", 1);
        DATA_MAP.put("ヲ", 0);
        DATA_MAP.put("ン", 48);
        
        DATA_MAP.put("ガ", 13);
        DATA_MAP.put("ギ", 4);
        DATA_MAP.put("グ", 12);
        DATA_MAP.put("ゲ", 1);
        DATA_MAP.put("ゴ", 3);
        
        DATA_MAP.put("ザ", 4);
        DATA_MAP.put("ジ", 21);
        DATA_MAP.put("ズ", 14);
        DATA_MAP.put("ゼ", 3);
        DATA_MAP.put("ゾ", 1);        

        DATA_MAP.put("ダ", 12);
        DATA_MAP.put("ヂ", 0);
        DATA_MAP.put("ヅ", 0);
        DATA_MAP.put("デ", 17);
        DATA_MAP.put("ド", 8);        
        
        DATA_MAP.put("バ", 14);
        DATA_MAP.put("ビ", 6);
        DATA_MAP.put("ブ", 12);
        DATA_MAP.put("ベ", 3);
        DATA_MAP.put("ボ", 3);        
        
        DATA_MAP.put("パ", 3);
        DATA_MAP.put("ピ", 4);
        DATA_MAP.put("プ", 11);
        DATA_MAP.put("ペ", 2);
        DATA_MAP.put("ポ", 3);        
        
        DATA_MAP.put("ァ", 8);
        DATA_MAP.put("ィ", 19);
        DATA_MAP.put("ゥ", 2);
        DATA_MAP.put("ェ", 14);
        DATA_MAP.put("ォ", 3);        

        DATA_MAP.put("ャ", 9);
        DATA_MAP.put("ュ", 20);
        DATA_MAP.put("ョ", 6);
        
        DATA_MAP.put("ッ", 30);
        
        DATA_MAP.put("ヴ", 10);        
        
        DATA_MAP.put("ー", 20);
	}
    
    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                                                        throws ServletException, IOException {

        PersistenceManager pm = PMF.get().getPersistenceManager();
        Cache cache = null;

        try{
            CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
            cache = cacheFactory.createCache(Collections.emptyMap());
        }catch(CacheException e){
            log.info(e.getMessage());
        }

        if(cache.get(CACHED_TOKEN) == null ||
                                    cache.get(CACHED_SECRET) == null ){

            Query query = pm.newQuery(Token.class);
            List<Token> tokens = (List<Token>)query.execute();

            for(Token token:tokens){
                if(token != null && token.getBotName().equals(BOTNAME)){
                    cache.put(CACHED_TOKEN, token.getAccessToken());
                    cache.put(CACHED_SECRET, token.getAccessSecret());
                }
            }
            pm.close();
        }
        doTweet(cache);
    }

    /**
     * Tweetの実行
     * @param cache Twitterアカウントとの接続情報を保持するキャッシュ
     */
    private void doTweet(Cache cache) {
        Twitter twitter = new TwitterFactory().getInstance(
                                    new AccessToken((String)cache.get(CACHED_TOKEN),
                                                    (String)cache.get(CACHED_SECRET)));        
        int pageNumber =1;
        int count = 200;

        Paging paging = new Paging(pageNumber,count);
        
        try {
        	ResponseList<Status> statuses = twitter.getMentions(paging);
            
            for(Status status:statuses){
            	//Bot宛のMentionを3分ごとに拾い、つぶやく内容を編集してTweetする
            	if( !(status.getUser().getScreenName().equals(twitter.getScreenName())) &&
        			 status.getText().trim().substring(0, BOTNAME.length()+1).equals("@"+BOTNAME) &&
        			 (BotUtil.checkTime(status, 1080000))
        			 ){
            		 	String targetString = status.getText().replaceAll("@"+BOTNAME, "").trim();
            		 	
            		 	StringBuilder tweetString = new StringBuilder();
            		 	tweetString.append(".@");
            		 	tweetString.append(status.getUser().getScreenName());
            		 	tweetString.append(" ");
            		 	tweetString.append(editTweet(targetString));
            		 	twitter.updateStatus(tweetString.toString());
                    }
            }
    	} catch (TwitterException e) {
            log.info(e.getMessage());
        }
    }
    
    /**
     * 与えられた文字列を一文字ずつ分解し、
     * DATA_MAPに設定されている値に変換。
     * それらの文字数に対する平均値(ブランド力)を算出し、
     * Tweetとして編集して返却する。
     * 
     * @param targetString ブランド力を測定したい文字列
     * @return Tweetすべき内容
     */
    private String editTweet(String targetString){
    	
    	if (targetString.indexOf("変態") != -1)
    	{
    		return "←　お巡りさん、この人です！";
    	}
    	
    	Double power = 0.0;

    	for(int i=0;i<targetString.length();i++){
    		Integer point = DATA_MAP.get(String.valueOf(targetString.charAt(i)));
    		if (point != null){
    			power += point;
    		} else {
    			power += 0.0;
    		}
    	}

    	Double score = (power / targetString.trim().length());

    	DecimalFormat df = new DecimalFormat(",##0.0");
    	
    	StringBuilder result = new StringBuilder();
    	
    	result.append(targetString);
    	result.append("のブランド力は");
    	result.append(df.format(new BigDecimal(Double.toString(score))));
    	result.append("だね。");
    	
    	return result.toString();    
    }

}

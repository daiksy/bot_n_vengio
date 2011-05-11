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

@SuppressWarnings("serial")
public class DaiksyBotServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(DaiksyBotServlet.class.getName());
    private String botname = "bot_n_vengio";
    private String cachedtoken = "access_token";
    private String cachedsecret = "access_secret";

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

        if((String)cache.get(cachedtoken) == null ||
                                    (String)cache.get(cachedsecret) == null ){

            Query query = pm.newQuery(Token.class);
            List<Token> tokens = (List<Token>)query.execute();

            for(Token token:tokens){
                if(token != null && token.getBotName().equals(botname)){
                    cache.put(cachedtoken, token.getAccessToken());
                    cache.put(cachedsecret, token.getAccessSecret());
                }
            }
            pm.close();
        }
        doTweet(cache);
    }

    private void doTweet(Cache cache) {
        Twitter twitter = new TwitterFactory().getInstance(
                                    new AccessToken((String)cache.get(cachedtoken),
                                                    (String)cache.get(cachedsecret)));        
        ResponseList<Status> statuses = null;
        int pageNumber =1;
        int count = 200;

        Paging paging = new Paging(pageNumber,count);
        
        try {
            statuses = twitter.getMentions(paging);

            for(Status status:statuses){
            	if( !(status.getUser().getScreenName().equals(twitter.getScreenName())) &&
        			 status.getText().trim().substring(0, botname.length()+1).equals("@"+botname) &&
        			 (BotUtil.checkTime(status, 1080000))
        			 ){
            		 	String targetString = status.getText().replaceAll("@"+botname, "").trim();
            		 	twitter.updateStatus(editTweet(targetString));
                    }
            }
    	} catch (TwitterException e) {
            log.info(e.getMessage());
        }
    }
    
    private String editTweet(String s){
 
    	HashMap<String, Integer> dataMap = getDataMap();
        
    	Double power = 0.0;

    	for(int i=0;i<s.length();i++){
    		Integer point = dataMap.get(String.valueOf(s.charAt(i)));
    		if (point != null){
				power += point;
    		} else {
				power += 0;
    		}
    	}

    	Double score = (power / s.length());

    	DecimalFormat df = new DecimalFormat(",##0.0");
    	return s + "のブランド力は" + df.format(new BigDecimal(Double.toString(score))) + "だね。";    
    }
    
    private HashMap<String, Integer> getDataMap() {
        HashMap<String, Integer> m = new HashMap<String, Integer>();
        m.put("ア", 25);
        m.put("イ", 41);
        m.put("ウ", 6);
        m.put("エ", 21);
        m.put("オ", 6);
        
        m.put("カ", 4);
        m.put("キ", 8);
        m.put("ク", 15);
        m.put("ケ", 3);
        m.put("コ", 5);       

        m.put("サ", 6);
        m.put("シ", 24);
        m.put("ス", 30);
        m.put("セ", 5);
        m.put("ソ", 6);
        
        m.put("タ", 4);
        m.put("チ", 12);
        m.put("ツ", 1);
        m.put("テ", 12);
        m.put("ト", 19);
        
        m.put("ナ", 14);
        m.put("二", 8);
        m.put("ヌ", 3);
        m.put("ネ", 1);
        m.put("ノ", 0);        
        
        m.put("ハ", 3);
        m.put("ヒ", 0);
        m.put("フ", 14);
        m.put("ヘ", 1);
        m.put("ホ", 1);
        
        m.put("マ", 12);
        m.put("ミ", 9);
        m.put("ム", 6);
        m.put("メ", 2);
        m.put("モ", 7);
        
        m.put("ヤ", 1);
        m.put("ユ", 0);
        m.put("ヨ", 0);
        
        m.put("ラ", 39);
        m.put("リ", 29);
        m.put("ル", 45);
        m.put("レ", 10);
        m.put("ロ", 16);
        
        m.put("ワ", 1);
        m.put("ヲ", 0);
        m.put("ン", 48);
        
        m.put("ガ", 13);
        m.put("ギ", 4);
        m.put("グ", 12);
        m.put("ゲ", 1);
        m.put("ゴ", 	3);
        
        m.put("ザ", 4);
        m.put("ジ", 21);
        m.put("ズ", 14);
        m.put("ゼ", 3);
        m.put("ゾ", 1);        

        m.put("ダ", 12);
        m.put("ヂ", 0);
        m.put("ヅ", 0);
        m.put("デ", 17);
        m.put("ド", 8);        
        
        m.put("バ", 14);
        m.put("ビ", 6);
        m.put("ブ", 12);
        m.put("ベ", 3);
        m.put("ボ", 3);        
        
        m.put("パ", 3);
        m.put("ピ", 4);
        m.put("プ", 11);
        m.put("ペ", 2);
        m.put("ポ", 3);        
        
        m.put("ァ", 8);
        m.put("ィ", 19);
        m.put("ゥ", 2);
        m.put("ェ", 14);
        m.put("ォ", 3);        

        m.put("ャ", 9);
        m.put("ュ", 20);
        m.put("ョ", 6);
        
        m.put("ッ", 30);
        
        m.put("ヴ", 10);        
        
        m.put("ー", 20);
        
        return m;
    }
}

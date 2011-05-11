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

        //cache内にaccesstoken/secretが無い場合はDataStoreから読み込む
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
        m.put("ア", 1);
        m.put("イ", 2);
        m.put("ウ", 3);
        m.put("エ", 4);
        m.put("オ", 5);
        
        m.put("カ", 1);
        m.put("キ", 2);
        m.put("ク", 3);
        m.put("ケ", 4);
        m.put("コ", 5);       

        m.put("サ", 1);
        m.put("シ", 2);
        m.put("ス", 3);
        m.put("セ", 4);
        m.put("ソ", 5);
        
        m.put("タ", 1);
        m.put("チ", 2);
        m.put("ツ", 3);
        m.put("テ", 4);
        m.put("ト", 5);
        
        m.put("ナ", 1);
        m.put("二", 2);
        m.put("ヌ", 3);
        m.put("ネ", 4);
        m.put("ノ", 5);        
        
        m.put("ハ", 1);
        m.put("ヒ", 2);
        m.put("フ", 3);
        m.put("ヘ", 4);
        m.put("ホ", 5);
        
        m.put("マ", 1);
        m.put("ミ", 2);
        m.put("ム", 3);
        m.put("メ", 4);
        m.put("モ", 5);
        
        m.put("ヤ", 1);
        m.put("ユ", 3);
        m.put("ヨ", 5);
        
        m.put("ラ", 1);
        m.put("リ", 2);
        m.put("ル", 3);
        m.put("レ", 4);
        m.put("ロ", 5);
        
        m.put("ワ", 1);
        m.put("ヲ", 5);
        m.put("ン", 0);
        
        m.put("ガ", 1);
        m.put("ギ", 2);
        m.put("グ", 3);
        m.put("ゲ", 4);
        m.put("ゴ", 5);
        
        m.put("ザ", 1);
        m.put("ジ", 2);
        m.put("ズ", 3);
        m.put("ゼ", 4);
        m.put("ゾ", 5);        

        m.put("ダ", 1);
        m.put("ヂ", 2);
        m.put("ヅ", 3);
        m.put("デ", 4);
        m.put("ド", 5);        
        
        m.put("バ", 1);
        m.put("ビ", 2);
        m.put("ブ", 3);
        m.put("ベ", 4);
        m.put("ボ", 5);        
        
        m.put("パ", 1);
        m.put("ピ", 2);
        m.put("プ", 3);
        m.put("ペ", 4);
        m.put("ポ", 5);        
        
        m.put("ァ", 1);
        m.put("ィ", 2);
        m.put("ゥ", 3);
        m.put("ェ", 4);
        m.put("ォ", 5);        

        m.put("ャ", 1);
        m.put("ュ", 3);
        m.put("ョ", 5);
        
        return m;
    }
}

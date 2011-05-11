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
    	return s + "�̃u�����h�͂�" + df.format(new BigDecimal(Double.toString(score))) + "���ˁB";    
    }
    
    private HashMap<String, Integer> getDataMap() {
        HashMap<String, Integer> m = new HashMap<String, Integer>();
        m.put("�A", 25);
        m.put("�C", 41);
        m.put("�E", 6);
        m.put("�G", 21);
        m.put("�I", 6);
        
        m.put("�J", 4);
        m.put("�L", 8);
        m.put("�N", 15);
        m.put("�P", 3);
        m.put("�R", 5);       

        m.put("�T", 6);
        m.put("�V", 24);
        m.put("�X", 30);
        m.put("�Z", 5);
        m.put("�\", 6);
        
        m.put("�^", 4);
        m.put("�`", 12);
        m.put("�c", 1);
        m.put("�e", 12);
        m.put("�g", 19);
        
        m.put("�i", 14);
        m.put("��", 8);
        m.put("�k", 3);
        m.put("�l", 1);
        m.put("�m", 0);        
        
        m.put("�n", 3);
        m.put("�q", 0);
        m.put("�t", 14);
        m.put("�w", 1);
        m.put("�z", 1);
        
        m.put("�}", 12);
        m.put("�~", 9);
        m.put("��", 6);
        m.put("��", 2);
        m.put("��", 7);
        
        m.put("��", 1);
        m.put("��", 0);
        m.put("��", 0);
        
        m.put("��", 39);
        m.put("��", 29);
        m.put("��", 45);
        m.put("��", 10);
        m.put("��", 16);
        
        m.put("��", 1);
        m.put("��", 0);
        m.put("��", 48);
        
        m.put("�K", 13);
        m.put("�M", 4);
        m.put("�O", 12);
        m.put("�Q", 1);
        m.put("�S", 	3);
        
        m.put("�U", 4);
        m.put("�W", 21);
        m.put("�Y", 14);
        m.put("�[", 3);
        m.put("�]", 1);        

        m.put("�_", 12);
        m.put("�a", 0);
        m.put("�d", 0);
        m.put("�f", 17);
        m.put("�h", 8);        
        
        m.put("�o", 14);
        m.put("�r", 6);
        m.put("�u", 12);
        m.put("�x", 3);
        m.put("�{", 3);        
        
        m.put("�p", 3);
        m.put("�s", 4);
        m.put("�v", 11);
        m.put("�y", 2);
        m.put("�|", 3);        
        
        m.put("�@", 8);
        m.put("�B", 19);
        m.put("�D", 2);
        m.put("�F", 14);
        m.put("�H", 3);        

        m.put("��", 9);
        m.put("��", 20);
        m.put("��", 6);
        
        m.put("�b", 30);
        
        m.put("��", 10);        
        
        m.put("�[", 20);
        
        return m;
    }
}

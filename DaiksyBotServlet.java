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

        //cache����accesstoken/secret�������ꍇ��DataStore����ǂݍ���
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
        m.put("�A", 1);
        m.put("�C", 2);
        m.put("�E", 3);
        m.put("�G", 4);
        m.put("�I", 5);
        
        m.put("�J", 1);
        m.put("�L", 2);
        m.put("�N", 3);
        m.put("�P", 4);
        m.put("�R", 5);       

        m.put("�T", 1);
        m.put("�V", 2);
        m.put("�X", 3);
        m.put("�Z", 4);
        m.put("�\", 5);
        
        m.put("�^", 1);
        m.put("�`", 2);
        m.put("�c", 3);
        m.put("�e", 4);
        m.put("�g", 5);
        
        m.put("�i", 1);
        m.put("��", 2);
        m.put("�k", 3);
        m.put("�l", 4);
        m.put("�m", 5);        
        
        m.put("�n", 1);
        m.put("�q", 2);
        m.put("�t", 3);
        m.put("�w", 4);
        m.put("�z", 5);
        
        m.put("�}", 1);
        m.put("�~", 2);
        m.put("��", 3);
        m.put("��", 4);
        m.put("��", 5);
        
        m.put("��", 1);
        m.put("��", 3);
        m.put("��", 5);
        
        m.put("��", 1);
        m.put("��", 2);
        m.put("��", 3);
        m.put("��", 4);
        m.put("��", 5);
        
        m.put("��", 1);
        m.put("��", 5);
        m.put("��", 0);
        
        m.put("�K", 1);
        m.put("�M", 2);
        m.put("�O", 3);
        m.put("�Q", 4);
        m.put("�S", 5);
        
        m.put("�U", 1);
        m.put("�W", 2);
        m.put("�Y", 3);
        m.put("�[", 4);
        m.put("�]", 5);        

        m.put("�_", 1);
        m.put("�a", 2);
        m.put("�d", 3);
        m.put("�f", 4);
        m.put("�h", 5);        
        
        m.put("�o", 1);
        m.put("�r", 2);
        m.put("�u", 3);
        m.put("�x", 4);
        m.put("�{", 5);        
        
        m.put("�p", 1);
        m.put("�s", 2);
        m.put("�v", 3);
        m.put("�y", 4);
        m.put("�|", 5);        
        
        m.put("�@", 1);
        m.put("�B", 2);
        m.put("�D", 3);
        m.put("�F", 4);
        m.put("�H", 5);        

        m.put("��", 1);
        m.put("��", 3);
        m.put("��", 5);
        
        return m;
    }
}

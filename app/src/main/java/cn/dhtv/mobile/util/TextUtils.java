package cn.dhtv.mobile.util;

import android.telephony.PhoneNumberUtils;
import android.text.format.DateFormat;

import com.android.volley.toolbox.StringRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.dhtv.mobile.Data;
import cn.dhtv.mobile.entity.Category;

/**
 * Created by Jack on 2015/3/20.
 */
public class TextUtils {
    public static final int PAGE_SIZE = 10;

    public static final String URL_LOGIN = "http://api.dhtv.cn/user/?mod=login";
    public static final String URL_REGISTER = "http://api.dhtv.cn/user/?mod=register";
    public static final String URL_SMS_CODE="http://i.dhtv.cn/sms/";

    public static final String URL_CATEGORY = Category.URL;
    public static final String URL_NEWS = "http://api.dhtv.cn/mobile/article/";
    public static final String URL_BLOCK = "http://api.dhtv.cn/mobile/block/";
    public static final String URL_VIDEO = "http://api.dhtv.cn/mobile/video/";
    public static final String URL_TV = "http://api.dhtv.cn/?mod=lookback&ac=tv_archiver";

    public static final String URL_COMMENT = "http://api.dhtv.cn/mobile/comment";
    public static final String URL_FACT = "http://api.dhtv.cn/mobile/fact/";
    public static final String URL_FAVORITE = "http://api.dhtv.cn/mobile/favorite/";

    public static final String URL_RES = "http://data.wztv.cn/";
    public static final String URL_RES_IMG = "http://tv.dhtv.cn/img/";
    public static final String URL_RES_TV = URL_RES+"tv/";

    public static final String URL_UPGRADE = "http://api.dhtv.cn/mobile/update/";

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat();



    public static String makeCategoryUrl(Category category,int page){
        return URL_CATEGORY +"?level=1"+"&catid="+category.getCatid()+"&page="+page/*+"&size="+PAGE_SIZE*/;
    }

    public static String makeNewsOverviewUrl(Category category,int page){
        return URL_NEWS+"?"+"catid="+category.getCatid()+"&page="+page+"&size="+ Data.TOTAL_PAGE_SIZE_NEWS;
    }

    public static String makeBlockQueryUrl(Category category){
        return URL_BLOCK+"?"+"bid="+category.getBid();
    }

    public static String makeVideoUrl(Category category,int beginId){
        return URL_VIDEO+"?"+"catid="+category.getCatid()+"&page="+beginId+"&size="+Data.TOTAL_PAGE_SIZE_NEWS;
    }

    public static String makeTvUrl(Category category,int beginId){
        return URL_TV+"&page="+beginId+"&id="+category.getUpid()+"&catid="+category.getCatid();
    }

    public static String makeCheckUpgradeUrl(int ver,int appid,String uuid){
        return URL_UPGRADE+"?ver="+ver+"&appid="+appid+"&uuid="+uuid+"&devicetype=android";
    }

    public static String makeCommentUrl(long uid,String type){
        return URL_COMMENT+"?uid="+uid+"&type="+type;
    }

    public static String makeFactUrl(long uid,String token,int currentpage,int pagesize){
        return URL_FACT+"?uid="+uid+"&token="+token+"&currentpage="+currentpage+"&pagesize="+Data.FACT_PAGE_SIZE+"&list=1";
    }

    public static String makeFavoriteUrl(long uid,String token,int currentpage,int pagesize){
        return URL_FAVORITE+"?uid="+uid+"&token="+token+"&currentpage="+currentpage+"&pagesize="+Data.FAVORITE_PAGE_SIZE+"&list=1";
    }

    public static String makeFactDetailUrl(long uid,String token,long msgid){
        return URL_FACT+"?uid="+uid+"&token="+token+"&msgid="+msgid;
    }

    public static String makeFavoriteDetailUrl(long uid,String token,long id,String idtype ){
        return URL_FAVORITE+"?uid="+uid+"&token="+token+"&id="+id+"&idtype="+idtype;
    }

    public static boolean validUserName(String username){
        if(StringUtils.isEmpty(username)){
            return false;
        }

        if(username.length() < 6 || username.length() > 15){
            return false;
        }
        return true;
    }

    public static boolean validPassword(String password){
        if(StringUtils.isEmpty(password)){
            return false;
        }

        if(password.length() < 6){
            return false;
        }
        return true;
    }

    public static boolean validEmail(String email){
        return EmailValidator.getInstance().isValid(email);
    }

    public static boolean validPhoneNumber(String phoneNumber){
        if(!StringUtils.isNumeric(phoneNumber)){
            return false;
        }

        if(phoneNumber.length() != 11){
            return false;
        }
        return true;
    }

    public static boolean validSMSCode(String smsCode){
        if(!StringUtils.isNumeric(smsCode)){
            return false;
        }

        return true;
    }












}

package com.mdff.app.utility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.mdff.app.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * Created by Swati.Gupta on 4/2/2018.
 */

public class AppUtil {
    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile("^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$");


        public static final Pattern USER_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9]{6,20}$");
    Context context;
    static Context context1;

    public AppUtil(Context context)
    {
        this.context=context;
        context1=context;

    }
    public static boolean isEmailCorrect(String email)
    {

        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    public static boolean isUserNameCorrect(String uname)
    {

        return USER_NAME_PATTERN.matcher(uname).matches();
    }
    /*
 * setting data in shared preferences
 */
    public void setPrefrence(String key, String value) {
        SharedPreferences prefrence = context.getSharedPreferences(
                context.getString(R.string.app_name), 0);
        SharedPreferences.Editor editor = prefrence.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /*
     * retreiving the data from shared preferences     */
    public String getPrefrence(String key) {
        SharedPreferences prefrence = context.getSharedPreferences(
                context.getString(R.string.app_name), 0);
        String data = prefrence.getString(key, "");
        return data;
    }

    public  void hideSoftKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public  String validateVal(String v)
     {

     if(v.equalsIgnoreCase("null"))
     {
     v="0";
     }
        return v;
    }


    public String getFormattedDateTime(String d)
    {
        if (d == null){
            return "";
        }

        SimpleDateFormat oldFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        oldFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date value = null;
        String dueDateAsNormal ="";
        try {
            value = oldFormatter.parse(d);
            SimpleDateFormat newFormatter = new SimpleDateFormat("MM/dd/yyyy @ hh:mma");
            newFormatter.setTimeZone(TimeZone.getDefault());
            dueDateAsNormal = newFormatter.format(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dueDateAsNormal;
    }

private static int getDeviceWidth()
{
    DisplayMetrics displayMetrics = new DisplayMetrics();
    ((Activity) context1).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//    int height = displayMetrics.heightPixels;
    int width = displayMetrics.widthPixels;
return width;
}

    public String modifyString(String html)
    {
        org.jsoup.nodes.Document doc = Jsoup.parse(html);
//            Element link = doc.select("a").first();
        Elements links = doc.select("a");
        for (Element link : links) {
                /*link.after("<script language=php>$this->DoIt();</script>");
                link.remove();*/
            String linkHref = link.attr("href"); // "http://example.com/"
            if (linkHref.equals("tel")) {
                String linkText = link.text();
                linkHref = linkHref.replace("tel", "tel:" + linkText);
                System.out.print(linkHref);
                link.after("<a href='"+linkHref+"'>"+link.html()+"</a>");
                link.remove();
            }
            else if (linkHref.equals("email")) {
                String linkText = link.text();
                linkHref = linkHref.replace("email", "email:" + linkText);
                System.out.print(linkHref);
                link.after("<a href='"+linkHref+"'>"+link.html()+"</a>");
                link.remove();
            }
            else if (linkHref.equals("url")) {
                String linkText = link.text();
                linkHref = linkHref.replace("url", "url:" + linkText);
                System.out.print(linkHref);
                link.after("<a href='"+linkHref+"'>"+link.html()+"</a>");
                link.remove();
            }
        }
        Elements iframes = doc.select("iframe");
        for (Element iframe : iframes) {
            String src = iframe.attr("src");
//            iframe.after("<iframe width='"+getDeviceWidth()+"' height='200' src='"+src+"' frameborder='0' allow='autoplay; encrypted-media' allowfullscreen></iframe>");
            iframe.after("<iframe margin-left= 'auto' margin-right= 'auto' width='390' height='200' src='"+src+"' frameborder='0' allow='autoplay; encrypted-media' allowfullscreen></iframe>");
            iframe.remove();
        }
        String d=doc.toString();
        return d;
    }



    public void setInboxNotificationCount(TextView ncount)
    {
        int c=0;int nCount=0,mCount=0;
        try {

           c=Integer.parseInt((getPrefrence("unread_count").equals("")?"0":getPrefrence("unread_count")));

            if (c> 0) {
                ncount.setText(String.valueOf(c));
                ncount.setVisibility(View.VISIBLE);
            } else {
                ncount.setVisibility(View.INVISIBLE);
            }
        }
        catch(Exception e)
        {
            System.out.print(e);
        }
    }

}

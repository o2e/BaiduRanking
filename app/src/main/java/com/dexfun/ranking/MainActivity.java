package com.dexfun.ranking;

import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.dexfun.ranking.NetworkConnectReceiver.NetWorkListener;

import java.io.DataOutputStream;
import java.io.File;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String COMMAND_AIRPLANE_OFF = "settings put global airplane_mode_on 0 \n am broadcast -a android.intent.action.AIRPLANE_MODE --ez state false\n ";
    private static final String COMMAND_AIRPLANE_ON = "settings put global airplane_mode_on 1 \n am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true\n ";

    private NetworkConnectReceiver mNetworkConnectReceiver;
    private WebView webView;

    private final String name[] = new String[]{"点赞狂魔官网","点赞狂魔","点赞狂魔下载","点赞狂魔APP"};//关键字集合
    private final String host = "www.dzkm.org";//替换在百度收录的那个域名

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerBroadcast();
        this.webView = findViewById(R.id.web);
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                System.err.println(url);
                if (url.equals("https://m.baidu.com/")) {
                    Toast.makeText(MainActivity.this, "开始搜索", Toast.LENGTH_SHORT).show();
                    String name = MainActivity.this.name[new Random().nextInt(MainActivity.this.name.length)];
                    view.evaluateJavascript("javascript:var input = document.getElementById('index-kw');input.focus();input.value = \"" + name + "\";document.getElementById('index-bn').click();", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {

                        }
                    });
                }
                if (url.contains("https://m.baidu.com/s")) {
                    Toast.makeText(MainActivity.this, "开始点击", Toast.LENGTH_SHORT).show();
                    view.evaluateJavascript("var view=document.getElementsByClassName(\"result c-result c-clk-recommend\");var v=0;for(var i=0;i<view.length;i++){if(i==view.length-1&&v==0){document.getElementsByClassName(\"new-nextpage-only\")[0].click()}if(view[i].innerHTML.indexOf(\"" + host + "\")>0){v=i;view[i].getElementsByClassName(\"c-line-clamp3\")[0].click()}};", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {

                        }
                    });
                }
                if (url.equals("https://" + host + "/")) {
                    System.out.println("完成》》》》");
                    Toast.makeText(MainActivity.this, "完成", Toast.LENGTH_SHORT).show();
                    MainActivity.this.webView.loadUrl("https://devs.onl/");
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            clear();
                        }
                    },3000);
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                System.out.println(url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    private void clear() {
        CookieManager.getInstance().removeAllCookie();
        CookieManager.getInstance().removeSessionCookie();
        CookieSyncManager.getInstance().sync();
        webView.clearCache(true);
        webView.clearFormData();
        webView.clearHistory();
        webView.clearMatches();
        webView.clearSslPreferences();


//        delete(new File("/data/data/com.dexfun.ranking2/"));

        setAirplaneMode(true);

//        writeCmd("am force-stop com.dexfun.ranking2 && am start -n com.dexfun.ranking2/com.dexfun.ranking.MainActivity");

    }

    public void OnClick(View view) {
        if (view.getId() == R.id.button) {
            clear();
        } else if (view.getId() == R.id.button2) {
            this.webView.evaluateJavascript("var view=document.getElementsByClassName(\"result c-result c-clk-recommend\");var v=0;for(var i=0;i<view.length;i++){if(i==view.length-1&&v==0){document.getElementsByClassName(\"new-nextpage-only\")[0].click()}if(view[i].innerHTML.indexOf(\"" + host + "\")>0){v=i;view[i].getElementsByClassName(\"c-line-clamp3\")[0].click()}};", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {

                }
            });
        }
    }

    private void registerBroadcast() {
        this.mNetworkConnectReceiver = new NetworkConnectReceiver(new NetWorkListener() {
            @Override
            public void networkConnect(boolean b) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("networkConnect : ");
                stringBuilder.append(b);
                Log.i("aaa", stringBuilder.toString());
                if (b) {
                    MainActivity.this.webView.loadUrl("https://m.baidu.com/");
                }else {

                    setAirplaneMode(false);
                }
            }
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        registerReceiver(this.mNetworkConnectReceiver, filter);
    }

    public static void setAirplaneMode(boolean enabling) {
        if (enabling) {
            System.out.println("开启飞行模式");
            writeCmd(COMMAND_AIRPLANE_ON);
        } else {
            System.out.println("关闭飞行模式");
            writeCmd(COMMAND_AIRPLANE_OFF);
        }
    }

    public static void writeCmd(String command) {
        try {
            Process su = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());
            outputStream.writeBytes(command);
            outputStream.flush();
            outputStream.writeBytes("exit\n");
            outputStream.flush();
            try {
                su.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
            outputStream.close();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }


    private void delete(File file) {
        for (File file1 : file.listFiles()) {
            if (file1.isDirectory()) {
                delete(file1);
            } else {
                System.out.println(file1.delete());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.mNetworkConnectReceiver);
    }
}

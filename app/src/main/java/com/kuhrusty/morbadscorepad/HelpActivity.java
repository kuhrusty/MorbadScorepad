package com.kuhrusty.morbadscorepad;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Arghh!!  I just want to display some HTML help files bundled with the app.
 * The amount of grief it took to get to this point is insane.
 */
public class HelpActivity extends AppCompatActivity {
    private static final String LOGBIT = "HelpActivity";

    //public static final String INTENT_FILE = "HelpActivity.helpFile";
    public static final String INTENT_URL = "HelpActivity.helpURL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        WebView wv = findViewById(R.id.webView);
        //  enable navigation, because we may have links in our help documents
        wv.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view,
                                                              String url) {
                Log.d(LOGBIT, "shouldOverrideUrlLoading(view, " + url + ")");
                if (url.startsWith("file:///android_asset/")) return false;

                //  from
                //  http://tutorials.jenkov.com/android/android-web-apps-using-android-webview.html
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }
        });

        String url = null;
        //String file = null;
        Intent intent = null;
        if ((intent = getIntent()) != null) {
            //file = intent.getStringExtra(INTENT_FILE);
            url = intent.getStringExtra(INTENT_URL);
        }
        //if (file != null) {
        //    InputStream is = getClassLoader().getResourceAsStream(file);
        //    if (is != null) {
        //        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        //        StringBuilder buf = new StringBuilder();
        //        String line;
        //        try {
        //            while ((line = in.readLine()) != null) {
        //                buf.append(line).append('\n');
        //            }
        //            in.close();
        //        } catch (IOException ioe) {
        //            Log.e(LOGBIT, "bailing on I/O", ioe);
        //        }
        //        wv.loadData(buf.toString(), "text/html", "UTF-8");
        //    }
        //} else
        if (url != null) {
            wv.loadUrl(url);
        }
    }

    /**
     * Calls finish().
     */
    public void done(@Nullable View view) {
        finish();
    }
}

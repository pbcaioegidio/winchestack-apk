package com.winchestack.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * WebView do painel Winchestack.
 *
 * Autenticação: usa o login do Laravel (Fortify). Logue UMA vez marcando
 * "Manter conectado" — o cookie persistente é guardado pelo WebView e
 * reaproveitado nas próximas aberturas (inclusive após reiniciar o aparelho).
 *
 * App de celular normal (não é kiosk): mantém a barra de status, deixa girar a
 * tela e reconecta sozinho quando a internet oscila.
 */
public class MainActivity extends Activity {

    private static final String URL = BuildConfig.PANEL_URL;

    private WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        web = new WebView(this);

        // Cookies persistentes: guarda a sessão / "manter conectado" entre aberturas.
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(web, true);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        // Tudo é servido por HTTPS (Caddy). Não permitir conteúdo cleartext arbitrário.
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);

        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String desc, String failingUrl) {
                // Reconecta sozinho após falha de rede.
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override public void run() { view.loadUrl(URL); }
                }, 5000);
            }
        });

        setContentView(web);
        web.loadUrl(URL);
    }

    @Override
    public void onBackPressed() {
        if (web != null && web.canGoBack()) {
            web.goBack();
        } else {
            moveTaskToBack(true);
        }
    }
}

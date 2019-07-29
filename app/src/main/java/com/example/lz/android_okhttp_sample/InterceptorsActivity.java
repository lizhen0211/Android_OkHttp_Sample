package com.example.lz.android_okhttp_sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.GzipSink;
import okio.Okio;

public class InterceptorsActivity extends Activity {

    private static final String TAG = "Interceptor";

    /**
     * Application interceptors
     * <p>
     * Don’t need to worry about intermediate responses like redirects and retries.
     * Are always invoked once, even if the HTTP response is served from the cache.
     * Observe the application’s original intent. Unconcerned with OkHttp-injected headers like If-None-Match.
     * Permitted to short-circuit and not call Chain.proceed().
     * Permitted to retry and make multiple calls to Chain.proceed().
     *
     * Network Interceptors
     * <p>
     * Able to operate on intermediate responses like redirects and retries.
     * Not invoked for cached responses that short-circuit the network.
     * Observe the data just as it will be transmitted over the network.
     * Access to the Connection that carries the request.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interceptors);
    }

    /**
     * Rewriting Responses
     * <p>
     * Symmetrically, interceptors can rewrite response headers and transform the response body.
     * This is generally more dangerous than
     * rewriting request headers because it may violate the webserver’s expectations!
     * <p>
     * If you’re in a tricky situation and prepared to deal with the consequences,
     * rewriting response headers is a powerful way to work around problems.
     * For example, you can fix a server’s misconfigured Cache-Control response header
     * to enable better response caching:
     */
    public void onRewritingResponsesClick(View view) {
        final OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                .build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RequestBody formBody = new FormBody.Builder()
                            .add("search", "Jurassic Park")
                            .build();
                    Request request = new Request.Builder()
                            .url("https://en.wikipedia.org/w/index.php")
                            .post(formBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    response.body().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Dangerous interceptor that rewrites the server's cache-control header.
     */
    private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            return originalResponse.newBuilder()
                    .header("Cache-Control", "max-age=60")
                    .build();
        }
    };

    /**
     * Rewriting Requests
     * Interceptors can add, remove, or replace request headers.
     * They can also transform the body of those requests that have one.
     * For example, you can use an application interceptor
     * to add request body compression if you’re connecting to a webserver known to support it
     *
     * @param view
     */
    public void onRewritingRequestsClick(View view) {
        final OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new GzipRequestInterceptor())
                .build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RequestBody formBody = new FormBody.Builder()
                            .add("search", "Jurassic Park")
                            .build();
                    Request request = new Request.Builder()
                            .url("https://en.wikipedia.org/w/index.php")
                            .post(formBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    response.body().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * This interceptor compresses the HTTP request body. Many webservers can't handle this!
     */
    final class GzipRequestInterceptor implements Interceptor {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request originalRequest = chain.request();
            if (originalRequest.body() == null || originalRequest.header("Content-Encoding") != null) {
                return chain.proceed(originalRequest);
            }

            Request compressedRequest = originalRequest.newBuilder()
                    .header("Content-Encoding", "gzip")
                    .method(originalRequest.method(), gzip(originalRequest.body()))
                    .build();
            return chain.proceed(compressedRequest);
        }

        private RequestBody gzip(final RequestBody body) {
            return new RequestBody() {
                @Override
                public MediaType contentType() {
                    return body.contentType();
                }

                @Override
                public long contentLength() {
                    return -1; // We don't know the compressed length in advance!
                }

                @Override
                public void writeTo(BufferedSink sink) throws IOException {
                    BufferedSink gzipSink = Okio.buffer(new GzipSink(sink));
                    body.writeTo(gzipSink);
                    gzipSink.close();
                }
            };
        }
    }


    /**
     * Network Interceptors
     * <p>
     * Registering a network interceptor is quite similar.
     * Call addNetworkInterceptor() instead of addInterceptor():
     * <p>
     * When we run this code, the interceptor runs twice.
     * Once for the initial request to http://www.publicobject.com/helloworld.txt,
     * and another for the redirect to https://publicobject.com/helloworld.txt.
     *
     * @param view
     */
    public void onNetworkInterceptorsClick(View view) {
        final OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new LoggingInterceptor())
                .build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder()
                            .url("http://www.publicobject.com/helloworld.txt")
                            .header("User-Agent", "OkHttp Example")
                            .build();

                    Response response = client.newCall(request).execute();
                    response.body().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Application Interceptors
     * <p>
     * Interceptors are registered as either application or network interceptors. We’ll use the LoggingInterceptor defined above to show the difference.
     * <p>
     * Register an application interceptor by calling addInterceptor() on OkHttpClient.Builder
     * <p>
     * We can see that we were redirected because response.request().url() is different from request.url().
     * The two log statements log two different URLs.
     *
     * @param view
     */
    public void onApplicationInterceptorsClick(View view) {
        final OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new LoggingInterceptor())
                .build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .url("http://www.publicobject.com/helloworld.txt")
                        .header("User-Agent", "OkHttp Example")
                        .build();

                Response response = null;
                try {
                    response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                response.body().close();
            }
        }).start();
    }
}


class LoggingInterceptor implements Interceptor {

    /**
     * Interceptors are a powerful mechanism that can monitor, rewrite, and retry calls.
     * Here’s a simple interceptor that logs the outgoing request and the incoming response.
     */
    private static final String TAG = "Interceptor";

    /**
     * A call to chain.proceed(request) is a critical part of each interceptor’s implementation. This simple-looking method is where all the HTTP work happens, producing a response to satisfy the request.
     * <p>
     * Interceptors can be chained.
     * Suppose you have both a compressing interceptor and a checksumming interceptor:
     * you’ll need to decide whether data is compressed and then checksummed,
     * or checksummed and then compressed. OkHttp uses lists to track interceptors,
     * and interceptors are called in order.
     *
     * @param chain
     * @return
     * @throws IOException
     */
    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();
        Log.e(TAG, String.format("Sending request %s on %s%n%s",
                request.url(), chain.connection(), request.headers()));

        Response response = chain.proceed(request);

        long t2 = System.nanoTime();
        Log.e(TAG, String.format("Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6d, response.headers()));

        return response;
    }
}

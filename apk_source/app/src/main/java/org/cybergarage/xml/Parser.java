package org.cybergarage.xml;

import android.os.Build;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.cybergarage.http.HTTPRequest;
import org.cybergarage.http.HTTPResponse;
import org.cybergarage.util.Debug;

/* loaded from: classes.dex */
public abstract class Parser {
    public abstract Node parse(InputStream inputStream) throws ParserException;

    public Node parse(URL locationURL) throws Throwable {
        String host = locationURL.getHost();
        int port = locationURL.getPort();
        if (port == -1) {
            port = 80;
        }
        String uri = locationURL.getPath();
        Node rootElem = getRootNode(locationURL);
        if (rootElem == null) {
            Debug.message("Get root node fail");
            HTTPRequest httpReq = new HTTPRequest();
            httpReq.setMethod("GET");
            httpReq.setURI(uri);
            HTTPResponse httpRes = httpReq.post(host, port);
            if (!httpRes.isSuccessful()) {
                throw new ParserException("HTTP comunication failed: no answer from peer.Unable to retrive resoure -> " + locationURL.toString());
            }
            String content = new String(httpRes.getContent());
            ByteArrayInputStream strBuf = new ByteArrayInputStream(content.getBytes());
            return parse(strBuf);
        }
        return rootElem;
    }

    public Node parse(File descriptionFile) throws ParserException, IOException {
        try {
            InputStream fileIn = new FileInputStream(descriptionFile);
            Node root = parse(fileIn);
            fileIn.close();
            return root;
        } catch (Exception e) {
            throw new ParserException(e);
        }
    }

    public Node parse(String descr) throws ParserException {
        try {
            InputStream decrIn = new ByteArrayInputStream(descr.getBytes());
            Node root = parse(decrIn);
            return root;
        } catch (Exception e) {
            throw new ParserException(e);
        }
    }

    public Node getRootNode(URL locationURL) throws Throwable {
        if (Build.VERSION.SDK_INT < 11) {
            Node rootElem = GetRootElemFromApachHttp(locationURL);
            return rootElem;
        }
        Node rootElem2 = GetRootElemFromCyberHttp(locationURL);
        return rootElem2;
    }

    public Node GetRootElemFromCyberHttp(URL locationURL) throws IOException {
        Node rootElem = null;
        HttpURLConnection urlCon = null;
        InputStream urlIn = null;
        String host = locationURL.getHost();
        int port = locationURL.getPort();
        if (port == -1) {
        }
        locationURL.getPath();
        try {
            try {
                urlCon = (HttpURLConnection) locationURL.openConnection();
                urlCon.setRequestMethod("GET");
                urlCon.setRequestProperty("Content-Length", "0");
                urlCon.setConnectTimeout(2000);
                urlCon.setReadTimeout(2000);
                urlCon.connect();
                if (host != null) {
                    urlCon.setRequestProperty("HOST", host);
                }
                urlIn = urlCon.getInputStream();
                rootElem = parse(urlIn);
            } finally {
                if (urlIn != null) {
                    try {
                        urlIn.close();
                    } catch (IOException e) {
                        Debug.message(e.getMessage());
                    }
                }
                if (urlCon != null) {
                    urlCon.disconnect();
                }
            }
        } catch (Exception e2) {
            Debug.message(e2.getMessage());
            if (urlIn != null) {
                try {
                    urlIn.close();
                } catch (IOException e3) {
                    Debug.message(e3.getMessage());
                }
            }
            if (urlCon != null) {
                urlCon.disconnect();
            }
        }
        return rootElem;
    }

    public Node GetRootElemFromApachHttp(URL locationURL) throws Throwable {
        Node rootElem = null;
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 2000);
        HttpConnectionParams.setSoTimeout(httpParameters, 2000);
        InputStream urlIn = null;
        try {
            try {
                HttpGet httpget = new HttpGet(locationURL.toURI());
                try {
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    try {
                        httpClient.setParams(httpParameters);
                        HttpResponse response = httpClient.execute(httpget);
                        HttpEntity entity = response.getEntity();
                        urlIn = entity.getContent();
                        rootElem = parse(urlIn);
                        if (urlIn != null) {
                            try {
                                urlIn.close();
                            } catch (IOException e) {
                                Debug.message(e.getMessage());
                            }
                        }
                    } catch (IllegalStateException e2) {
                        e = e2;
                        Debug.message(e.getMessage());
                        if (urlIn != null) {
                            try {
                                urlIn.close();
                            } catch (IOException e3) {
                                Debug.message(e3.getMessage());
                            }
                        }
                        return rootElem;
                    } catch (URISyntaxException e4) {
                        e = e4;
                        Debug.message(e.getMessage());
                        if (urlIn != null) {
                            try {
                                urlIn.close();
                            } catch (IOException e5) {
                                Debug.message(e5.getMessage());
                            }
                        }
                        return rootElem;
                    } catch (ClientProtocolException e6) {
                        e = e6;
                        Debug.message(e.getMessage());
                        if (urlIn != null) {
                            try {
                                urlIn.close();
                            } catch (IOException e7) {
                                Debug.message(e7.getMessage());
                            }
                        }
                        return rootElem;
                    } catch (IOException e8) {
                        e = e8;
                        Debug.message(e.getMessage());
                        if (urlIn != null) {
                            try {
                                urlIn.close();
                            } catch (IOException e9) {
                                Debug.message(e9.getMessage());
                            }
                        }
                        return rootElem;
                    } catch (ParserException e10) {
                        e = e10;
                        Debug.message(e.getMessage());
                        if (urlIn != null) {
                            try {
                                urlIn.close();
                            } catch (IOException e11) {
                                Debug.message(e11.getMessage());
                            }
                        }
                        return rootElem;
                    } catch (Throwable th) {
                        th = th;
                        if (urlIn != null) {
                            try {
                                urlIn.close();
                            } catch (IOException e12) {
                                Debug.message(e12.getMessage());
                            }
                        }
                        throw th;
                    }
                } catch (IOException e13) {
                    e = e13;
                } catch (IllegalStateException e14) {
                    e = e14;
                } catch (URISyntaxException e15) {
                    e = e15;
                } catch (ClientProtocolException e16) {
                    e = e16;
                } catch (ParserException e17) {
                    e = e17;
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (IOException e18) {
                e = e18;
            } catch (IllegalStateException e19) {
                e = e19;
            } catch (URISyntaxException e20) {
                e = e20;
            } catch (ClientProtocolException e21) {
                e = e21;
            } catch (ParserException e22) {
                e = e22;
            }
            return rootElem;
        } catch (Throwable th3) {
            th = th3;
        }
    }
}

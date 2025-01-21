package hudson.plugins.emailext.plugins.oauth2;

import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.plugins.emailext.Messages;
import hudson.plugins.emailext.plugins.OAuth2Flow;
import hudson.plugins.emailext.plugins.OAuth2FlowDescriptor;
import hudson.util.Secret;
import java.io.InputStream;
import java.util.List;
import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.cookie.StandardCookieSpec;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.http.message.StatusLine;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

@SuppressWarnings("unused")
public class Office365UsernamePasswordOAuth2Flow extends OAuth2Flow {

    public static final String TOKEN_URL = "https://login.microsoftonline.com/%s/oauth2/v2.0/token";
    public static final String DEFAULT_SCOPE = "https://outlook.office365.com/.default";

    private String scope = DEFAULT_SCOPE;
    private String clientId;
    private String tenantId;
    private transient Secret token;
    private transient long expires = -1;

    @DataBoundConstructor
    public Office365UsernamePasswordOAuth2Flow() {}

    //    public String getAuthority() {
    //        return authority;
    //    }
    //
    //    @DataBoundSetter
    //    public void setAuthority(String authority) {
    //        this.authority = authority;
    //    }

    public String getScope() {
        return scope;
    }

    @DataBoundSetter
    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getClientId() {
        return clientId;
    }

    @DataBoundSetter
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getTenantId() {
        return tenantId;
    }

    @DataBoundSetter
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    @Restricted(NoExternalUse.class)
    public Secret getToken(StandardUsernamePasswordCredentials credentials) throws Exception {
        // returned the cached value if possible
        if ((token != null) && (expires > 0) && (System.currentTimeMillis() < expires)) {
            return token;
        }

        String url = String.format(TOKEN_URL, tenantId);
        token = null;
        try (PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
                        .setSslContext(SSLContexts.createSystemDefault())
                        .setTlsVersions(TLS.V_1_3)
                        .build())
                .setDefaultSocketConfig(
                        SocketConfig.custom().setSoTimeout(Timeout.ofMinutes(1)).build())
                .setPoolConcurrencyPolicy(PoolConcurrencyPolicy.STRICT)
                .setConnPoolPolicy(PoolReusePolicy.LIFO)
                .setDefaultConnectionConfig(ConnectionConfig.custom()
                        .setSocketTimeout(Timeout.ofMinutes(1))
                        .setConnectTimeout(Timeout.ofMinutes(1))
                        .setTimeToLive(TimeValue.ofMinutes(10))
                        .build())
                .build()) {

            try (CloseableHttpClient client = HttpClients.custom()
                    .setConnectionManager(connectionManager)
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setCookieSpec(StandardCookieSpec.STRICT)
                            .build())
                    .build()) {

                CookieStore cookieStore = new BasicCookieStore();

                CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

                HttpClientContext clientContext = HttpClientContext.create();
                clientContext.setCookieStore(cookieStore);
                clientContext.setCredentialsProvider(credentialsProvider);
                clientContext.setRequestConfig(RequestConfig.custom()
                        .setCookieSpec(StandardCookieSpec.STRICT)
                        .build());

                JsonFactory jsonFactory = new JsonFactory();
                ObjectMapper objectMapper = new ObjectMapper(jsonFactory);

                ClassicHttpRequest httpPost = ClassicRequestBuilder.post(url)
                        .setEntity(new UrlEncodedFormEntity(List.of(
                                new BasicNameValuePair("grant_type", "client_credentials"),
                                new BasicNameValuePair("client_id", clientId),
                                new BasicNameValuePair(
                                        "client_secret",
                                        credentials.getPassword().getPlainText()),
                                new BasicNameValuePair("scope", String.join(" ", scope.split(";"))))))
                        .build();

                JsonNode responseData = client.execute(httpPost, response -> {
                    final HttpEntity responseEntity = response.getEntity();
                    if (response.getCode() >= 300) {
                        if(responseEntity != null) {
                            try (InputStream inputStream = responseEntity.getContent()) {
                                JsonNode responseContent = objectMapper.readTree(inputStream);
                                System.out.println(responseContent.toPrettyString());
                            }
                        }
                        throw new ClientProtocolException(new StatusLine(response).toString());
                    }

                    if (responseEntity == null) {
                        return null;
                    }
                    try (InputStream inputStream = responseEntity.getContent()) {
                        return objectMapper.readTree(inputStream);
                    }
                });

                if (responseData.hasNonNull("access_token")) {
                    token = Secret.fromString(responseData.get("access_token").asText());
                    if (responseData.hasNonNull("expires_in")) {
                        long expires_in = responseData.get("expires_in").asLong(-1) - 300;
                        if (expires_in > 0) {
                            expires = System.currentTimeMillis() + (expires_in * 1000);
                        }
                    }
                }
            }
            return token;
        }
    }

    @Extension
    public static final class DescriptorImpl extends OAuth2FlowDescriptor {

        public DescriptorImpl() {}

        @NonNull
        @Override
        public String getDisplayName() {
            return Messages.Office365UsernamePasswordOAuth2Flow_DisplayName();
        }

        public String getDefaultScope() {
            return DEFAULT_SCOPE;
        }
    }
}

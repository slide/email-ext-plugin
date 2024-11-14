package hudson.plugins.emailext.plugins.oauth2;

import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.microsoft.aad.msal4j.IAccount;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.MsalException;
import com.microsoft.aad.msal4j.PublicClientApplication;
import com.microsoft.aad.msal4j.SilentParameters;
import com.microsoft.aad.msal4j.UserNamePasswordParameters;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.plugins.emailext.plugins.OAuth2Flow;
import hudson.plugins.emailext.plugins.OAuth2FlowDescriptor;
import hudson.util.Secret;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public class Office365UsernamePasswordOAuth2Flow extends OAuth2Flow {

    public static final String DEFAULT_AUTHORITY = "https://login.microsoftonline.com/organizations/";
    public static final String DEFAULT_SCOPE = "user.read";

    private String authority = DEFAULT_AUTHORITY;
    private String scope = DEFAULT_SCOPE;
    private String clientId;
    private transient PublicClientApplication pca;

    @DataBoundConstructor
    public Office365UsernamePasswordOAuth2Flow() {}

    public String getAuthority() {
        return authority;
    }

    @DataBoundSetter
    public void setAuthority(String authority) {
        this.authority = authority;
    }

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

    @Override
    @Restricted(NoExternalUse.class)
    public Secret getToken(StandardUsernamePasswordCredentials credentials) throws Exception {
        if (pca == null) {
            pca = PublicClientApplication.builder(clientId).authority(authority).build();
        }

        IAuthenticationResult result;
        // Get list of accounts from the application's token cache, and search them for the configured username
        // getAccounts() will be empty on this first call, as accounts are added to the cache when acquiring a token
        Set<IAccount> accountsInCache = pca.getAccounts().join();
        IAccount account = getAccountByUsername(accountsInCache, credentials.getUsername());
        Set<String> scopes = new HashSet<>(Arrays.stream(scope.split(",")).toList());
        // Attempt to acquire token when user's account is not in the application's token cache
        result = acquireTokenUsernamePassword(
                pca, scopes, account, credentials.getUsername(), credentials.getPassword());
        return result != null ? Secret.fromString(result.accessToken()) : null;
    }

    private IAccount getAccountByUsername(Set<IAccount> accounts, String username) {
        if (!accounts.isEmpty()) {
            for (IAccount account : accounts) {
                if (account.username().equals(username)) {
                    return account;
                }
            }
        }
        return null;
    }

    private IAuthenticationResult acquireTokenUsernamePassword(
            PublicClientApplication pca, Set<String> scope, IAccount account, String username, Secret password)
            throws Exception {
        IAuthenticationResult result;
        try {
            SilentParameters silentParameters =
                    SilentParameters.builder(scope).account(account).build();
            // Try to acquire token silently. This will fail on the first acquireTokenUsernamePassword() call
            // because the token cache does not have any data for the user you are trying to acquire a token for
            result = pca.acquireTokenSilently(silentParameters).join();
        } catch (Exception ex) {
            if (ex.getCause() instanceof MsalException) {
                UserNamePasswordParameters parameters = UserNamePasswordParameters.builder(
                                scope, username, Secret.toString(password).toCharArray())
                        .build();
                // Try to acquire a token via username/password. If successful, you should see
                // the token and account information printed out to console
                result = pca.acquireToken(parameters).join();
            } else {
                // Handle other exceptions accordingly
                throw ex;
            }
        }
        return result;
    }

    @Extension
    public static final class DescriptorImpl extends OAuth2FlowDescriptor {

        public DescriptorImpl() {}

        @NonNull
        @Override
        public String getDisplayName() {
            return "Office365 OAuth 2.0 Username/Password Flow";
        }

        public String getDefaultAuthority() {
            return DEFAULT_AUTHORITY;
        }

        public String getDefaultScope() {
            return DEFAULT_SCOPE;
        }
    }
}

package hudson.plugins.emailext.plugins.oauth2;

import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.plugins.emailext.Messages;
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
    private transient ConfidentialClientApplication clientApplication;

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
        if (clientApplication == null) {
            clientApplication = ConfidentialClientApplication.builder(clientId, ClientCredentialFactory.createFromSecret(credentials.getPassword().getPlainText())).authority(authority).build();
        }

        Set<String> scopes = new HashSet<>(Arrays.stream(scope.split(",")).toList());
        scopes.add("https://graph.microsoft.com/.default");
        ClientCredentialParameters parameters = ClientCredentialParameters.builder(scopes).build();
        IAuthenticationResult result = clientApplication.acquireToken(parameters).get();
        return result != null ? Secret.fromString(result.accessToken()) : null;
    }

    @Extension
    public static final class DescriptorImpl extends OAuth2FlowDescriptor {

        public DescriptorImpl() {}

        @NonNull
        @Override
        public String getDisplayName() {
            return Messages.Office365UsernamePasswordOAuth2Flow_DisplayName();
        }

        public String getDefaultAuthority() {
            return DEFAULT_AUTHORITY;
        }

        public String getDefaultScope() {
            return DEFAULT_SCOPE;
        }
    }
}

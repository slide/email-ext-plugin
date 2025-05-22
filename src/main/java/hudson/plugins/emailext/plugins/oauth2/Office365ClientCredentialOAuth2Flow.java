package hudson.plugins.emailext.plugins.oauth2;

import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.plugins.emailext.Messages;
import hudson.plugins.emailext.plugins.OAuth2Flow;
import hudson.plugins.emailext.plugins.OAuth2FlowDescriptor;
import hudson.util.Secret;

import java.util.Set;

import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

@SuppressWarnings("unused")
public class Office365ClientCredentialOAuth2Flow extends OAuth2Flow {

    private String tenantId;
    private transient Secret token;
    private transient long expires = -1;

    @DataBoundConstructor
    public Office365ClientCredentialOAuth2Flow() {}

    public String getTenantId() {
        return tenantId;
    }

    @DataBoundSetter
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public boolean useUsername() {
        return false;
    }

    @Override
    @Restricted(NoExternalUse.class)
    public Secret getToken(StandardUsernamePasswordCredentials credentials) throws Exception {
        // returned the cached value if possible
        if ((token != null) && (expires > 0) && (System.currentTimeMillis() < expires)) {
            return token;
        }

        ConfidentialClientApplication app = ConfidentialClientApplication.builder(
                        credentials.getUsername(),
                        ClientCredentialFactory.createFromSecret(credentials.getPassword().getPlainText()))
                .authority("https://login.microsoftonline.com/" + tenantId)
                .build();
        ClientCredentialParameters parameters = ClientCredentialParameters.builder(Set.of("https://graph.microsoft.com/.default")).build();

        String accessToken = app.acquireToken(parameters).get().accessToken();
        token = Secret.fromString(accessToken);
        return token;
    }

    @Extension
    public static final class DescriptorImpl extends OAuth2FlowDescriptor {

        public DescriptorImpl() {}

        @NonNull
        @Override
        public String getDisplayName() {
            return Messages.Office365ClientCredentialOAuth2Flow_DisplayName();
        }
    }
}

package hudson.plugins.emailext.plugins.oauth2;

import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.plugins.emailext.plugins.OAuth2Flow;
import hudson.plugins.emailext.plugins.OAuth2FlowDescriptor;
import hudson.util.Secret;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

public class DirectTokenOAuth2Flow extends OAuth2Flow {
    @Override
    @Restricted(NoExternalUse.class)
    public Secret getToken(StandardUsernamePasswordCredentials credentials) throws Exception {
        // get credentials and return the password
        return credentials.getPassword();
    }

    @Extension
    public static final class DescriptorImpl extends OAuth2FlowDescriptor {

        public DescriptorImpl() {}

        @NonNull
        @Override
        public String getDisplayName() {
            return "Password from credentials as OAuth 2.0 token";
        }
    }
}

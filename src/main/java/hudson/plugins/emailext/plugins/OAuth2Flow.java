package hudson.plugins.emailext.plugins;

import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import hudson.DescriptorExtensionList;
import hudson.ExtensionPoint;
import hudson.model.Describable;
import hudson.util.Secret;
import jenkins.model.Jenkins;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

public abstract class OAuth2Flow implements Describable<OAuth2Flow>, ExtensionPoint {

    /**
     * Retrieves a token using the flow implementation
     * @param credentials The credential specified for the mail account
     * @return A secret token used as an OAuth 2.0 token
     */
    @Restricted(NoExternalUse.class)
    public abstract Secret getToken(StandardUsernamePasswordCredentials credentials) throws Exception;

    public static DescriptorExtensionList<OAuth2Flow, OAuth2FlowDescriptor> all() {
        return Jenkins.get().getDescriptorList(OAuth2Flow.class);
    }

    @Override
    public OAuth2FlowDescriptor getDescriptor() {
        return (OAuth2FlowDescriptor) Jenkins.get().getDescriptor(getClass());
    }
}

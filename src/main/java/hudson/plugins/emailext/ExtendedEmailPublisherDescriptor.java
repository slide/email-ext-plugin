package hudson.plugins.emailext;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.security.Permission;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import hudson.util.Secret;
import jakarta.mail.Authenticator;
import jakarta.mail.Session;
import java.io.PrintStream;
import java.util.List;
import java.util.function.BiFunction;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest2;

/**
 * Descriptor for {@link ExtendedEmailPublisher}. Global configuration has moved to
 * {@link ExtendedEmailGlobalConfiguration}, accessible via {@link ExtendedEmailManagementLink}.
 * All configuration getters and setters on this class delegate to
 * {@link ExtendedEmailGlobalConfiguration#get()}.
 */
@Extension
public final class ExtendedEmailPublisherDescriptor extends BuildStepDescriptor<Publisher> {

    public ExtendedEmailPublisherDescriptor() {
        super(ExtendedEmailPublisher.class);
    }

    @NonNull
    @Override
    public String getDisplayName() {
        return Messages.ExtendedEmailPublisherDescriptor_DisplayName();
    }

    @Override
    public boolean isApplicable(Class<? extends AbstractProject> jobType) {
        return true;
    }

    @Override
    public String getHelpFile() {
        return "/plugin/email-ext/help/main.html";
    }

    /**
     * No-op: all configuration is now managed by {@link ExtendedEmailGlobalConfiguration}.
     * The global.jelly for this descriptor renders only a redirect link.
     */
    @Override
    public boolean configure(StaplerRequest2 req, JSONObject formData) throws FormException {
        return true;
    }

    @NonNull
    @Override
    public Permission getRequiredGlobalConfigPagePermission() {
        return Jenkins.MANAGE;
    }

    // -------------------------------------------------------------------------
    // All methods below delegate to ExtendedEmailGlobalConfiguration.get()
    // -------------------------------------------------------------------------

    public String getAdminAddress() {
        return ExtendedEmailGlobalConfiguration.get().getAdminAddress();
    }

    public String getDefaultSuffix() {
        return ExtendedEmailGlobalConfiguration.get().getDefaultSuffix();
    }

    public void setDefaultSuffix(String defaultSuffix) {
        ExtendedEmailGlobalConfiguration.get().setDefaultSuffix(defaultSuffix);
    }

    public boolean isThrottlingEnabled() {
        return ExtendedEmailGlobalConfiguration.get().isThrottlingEnabled();
    }

    public void setThrottlingEnabled(boolean throttlingEnabled) {
        ExtendedEmailGlobalConfiguration.get().setThrottlingEnabled(throttlingEnabled);
    }

    @org.kohsuke.accmod.Restricted(org.kohsuke.accmod.restrictions.NoExternalUse.class)
    Session createSession(MailAccount acc, ExtendedEmailPublisherContext context) {
        return ExtendedEmailGlobalConfiguration.get().createSession(acc, context);
    }

    public String getHudsonUrl() {
        return ExtendedEmailGlobalConfiguration.get().getHudsonUrl();
    }

    public List<MailAccount> getAddAccounts() {
        return ExtendedEmailGlobalConfiguration.get().getAddAccounts();
    }

    public void setAddAccounts(List<MailAccount> addAccounts) {
        ExtendedEmailGlobalConfiguration.get().setAddAccounts(addAccounts);
    }

    @Deprecated
    public String getSmtpServer() {
        return ExtendedEmailGlobalConfiguration.get().getSmtpServer();
    }

    @Deprecated
    public void setSmtpServer(String smtpServer) {
        ExtendedEmailGlobalConfiguration.get().setSmtpServer(smtpServer);
    }

    @Deprecated
    public String getSmtpUsername() {
        return ExtendedEmailGlobalConfiguration.get().getSmtpUsername();
    }

    @Deprecated
    public void setSmtpUsername(String username) {
        ExtendedEmailGlobalConfiguration.get().setSmtpUsername(username);
    }

    @Deprecated
    public Secret getSmtpPassword() {
        return ExtendedEmailGlobalConfiguration.get().getSmtpPassword();
    }

    @Deprecated
    public void setSmtpPassword(String password) {
        ExtendedEmailGlobalConfiguration.get().setSmtpPassword(password);
    }

    public void setSmtpAuth(String userName, String password) {
        ExtendedEmailGlobalConfiguration.get().setSmtpAuth(userName, password);
    }

    @Deprecated
    public boolean getUseSsl() {
        return ExtendedEmailGlobalConfiguration.get().getUseSsl();
    }

    @Deprecated
    public void setUseSsl(boolean useSsl) {
        ExtendedEmailGlobalConfiguration.get().setUseSsl(useSsl);
    }

    @Deprecated
    public String getSmtpPort() {
        return ExtendedEmailGlobalConfiguration.get().getSmtpPort();
    }

    @Deprecated
    public void setSmtpPort(String port) {
        ExtendedEmailGlobalConfiguration.get().setSmtpPort(port);
    }

    @Deprecated
    public String getAdvProperties() {
        return ExtendedEmailGlobalConfiguration.get().getAdvProperties();
    }

    @Deprecated
    public void setAdvProperties(String advProperties) {
        ExtendedEmailGlobalConfiguration.get().setAdvProperties(advProperties);
    }

    public String getCharset() {
        return ExtendedEmailGlobalConfiguration.get().getCharset();
    }

    public void setCharset(String charset) {
        ExtendedEmailGlobalConfiguration.get().setCharset(charset);
    }

    public String getDefaultContentType() {
        return ExtendedEmailGlobalConfiguration.get().getDefaultContentType();
    }

    public void setDefaultContentType(String contentType) {
        ExtendedEmailGlobalConfiguration.get().setDefaultContentType(contentType);
    }

    @SuppressWarnings({"lgtm[jenkins/csrf]", "lgtm[jenkins/no-permission-check]"})
    public FormValidation doCheckDefaultSuffix(@org.kohsuke.stapler.QueryParameter String value) {
        return ExtendedEmailGlobalConfiguration.get().doCheckDefaultSuffix(value);
    }

    public String getDefaultSubject() {
        return ExtendedEmailGlobalConfiguration.get().getDefaultSubject();
    }

    public void setDefaultSubject(String subject) {
        ExtendedEmailGlobalConfiguration.get().setDefaultSubject(subject);
    }

    public String getDefaultBody() {
        return ExtendedEmailGlobalConfiguration.get().getDefaultBody();
    }

    public void setDefaultBody(String body) {
        ExtendedEmailGlobalConfiguration.get().setDefaultBody(body);
    }

    public String getEmergencyReroute() {
        return ExtendedEmailGlobalConfiguration.get().getEmergencyReroute();
    }

    public void setEmergencyReroute(String emergencyReroute) {
        ExtendedEmailGlobalConfiguration.get().setEmergencyReroute(emergencyReroute);
    }

    public long getMaxAttachmentSize() {
        return ExtendedEmailGlobalConfiguration.get().getMaxAttachmentSize();
    }

    public void setMaxAttachmentSize(long bytes) {
        ExtendedEmailGlobalConfiguration.get().setMaxAttachmentSize(bytes);
    }

    public MailAccount getMailAccount() {
        return ExtendedEmailGlobalConfiguration.get().getMailAccount();
    }

    public void setMailAccount(MailAccount mailAccount) {
        ExtendedEmailGlobalConfiguration.get().setMailAccount(mailAccount);
    }

    public long getMaxAttachmentSizeMb() {
        return ExtendedEmailGlobalConfiguration.get().getMaxAttachmentSizeMb();
    }

    public void setMaxAttachmentSizeMb(long mb) {
        ExtendedEmailGlobalConfiguration.get().setMaxAttachmentSizeMb(mb);
    }

    public String getDefaultRecipients() {
        return ExtendedEmailGlobalConfiguration.get().getDefaultRecipients();
    }

    public void setDefaultRecipients(String recipients) {
        ExtendedEmailGlobalConfiguration.get().setDefaultRecipients(recipients);
    }

    public String getAllowedDomains() {
        return ExtendedEmailGlobalConfiguration.get().getAllowedDomains();
    }

    public void setAllowedDomains(String allowed) {
        ExtendedEmailGlobalConfiguration.get().setAllowedDomains(allowed);
    }

    public String getExcludedCommitters() {
        return ExtendedEmailGlobalConfiguration.get().getExcludedCommitters();
    }

    public void setExcludedCommitters(String excluded) {
        ExtendedEmailGlobalConfiguration.get().setExcludedCommitters(excluded);
    }

    @Deprecated
    public boolean getOverrideGlobalSettings() {
        return ExtendedEmailGlobalConfiguration.get().getOverrideGlobalSettings();
    }

    public String getListId() {
        return ExtendedEmailGlobalConfiguration.get().getListId();
    }

    public void setListId(String id) {
        ExtendedEmailGlobalConfiguration.get().setListId(id);
    }

    public boolean getPrecedenceBulk() {
        return ExtendedEmailGlobalConfiguration.get().getPrecedenceBulk();
    }

    public void setPrecedenceBulk(boolean bulk) {
        ExtendedEmailGlobalConfiguration.get().setPrecedenceBulk(bulk);
    }

    public int getDefaultAttachBuildLog() {
        return ExtendedEmailGlobalConfiguration.get().getDefaultAttachBuildLog();
    }

    public void setDefaultAttachBuildLog(int defaultAttachBuildLog) {
        ExtendedEmailGlobalConfiguration.get().setDefaultAttachBuildLog(defaultAttachBuildLog);
    }

    @SuppressWarnings({"lgtm[jenkins/csrf]", "lgtm[jenkins/no-permission-check]", "unused"})
    public ListBoxModel doFillDefaultAttachBuildLogItems() {
        return ExtendedEmailGlobalConfiguration.get().doFillDefaultAttachBuildLogItems();
    }

    public String getDefaultReplyTo() {
        return ExtendedEmailGlobalConfiguration.get().getDefaultReplyTo();
    }

    public void setDefaultReplyTo(String to) {
        ExtendedEmailGlobalConfiguration.get().setDefaultReplyTo(to);
    }

    public boolean isSecurityEnabled() {
        return false;
    }

    public boolean isAdminRequiredForTemplateTesting() {
        return ExtendedEmailGlobalConfiguration.get().isAdminRequiredForTemplateTesting();
    }

    public void setAdminRequiredForTemplateTesting(boolean requireAdmin) {
        ExtendedEmailGlobalConfiguration.get().setAdminRequiredForTemplateTesting(requireAdmin);
    }

    public boolean isWatchingEnabled() {
        return ExtendedEmailGlobalConfiguration.get().isWatchingEnabled();
    }

    public boolean isAllowUnregisteredEnabled() {
        return ExtendedEmailGlobalConfiguration.get().isAllowUnregisteredEnabled();
    }

    public void setWatchingEnabled(boolean enabled) {
        ExtendedEmailGlobalConfiguration.get().setWatchingEnabled(enabled);
    }

    public void setAllowUnregisteredEnabled(boolean enabled) {
        ExtendedEmailGlobalConfiguration.get().setAllowUnregisteredEnabled(enabled);
    }

    public @CheckForNull String getDefaultPresendScript() {
        return ExtendedEmailGlobalConfiguration.get().getDefaultPresendScript();
    }

    public void setDefaultPresendScript(@CheckForNull String script) {
        ExtendedEmailGlobalConfiguration.get().setDefaultPresendScript(script);
    }

    public @CheckForNull String getDefaultPostsendScript() {
        return ExtendedEmailGlobalConfiguration.get().getDefaultPostsendScript();
    }

    public void setDefaultPostsendScript(@CheckForNull String script) {
        ExtendedEmailGlobalConfiguration.get().setDefaultPostsendScript(script);
    }

    public List<GroovyScriptPath> getDefaultClasspath() {
        return ExtendedEmailGlobalConfiguration.get().getDefaultClasspath();
    }

    public void setDefaultClasspath(List<GroovyScriptPath> defaultClasspath) throws FormException {
        ExtendedEmailGlobalConfiguration.get().setDefaultClasspath(defaultClasspath);
    }

    public List<String> getDefaultTriggerIds() {
        return ExtendedEmailGlobalConfiguration.get().getDefaultTriggerIds();
    }

    public void setDefaultTriggerIds(List<String> triggerIds) {
        ExtendedEmailGlobalConfiguration.get().setDefaultTriggerIds(triggerIds);
    }

    @SuppressWarnings({"lgtm[jenkins/csrf]", "lgtm[jenkins/no-permission-check]", "unused"})
    public ListBoxModel doFillDefaultContentTypeItems() {
        return ExtendedEmailGlobalConfiguration.get().doFillDefaultContentTypeItems();
    }

    @SuppressWarnings({"lgtm[jenkins/csrf]", "lgtm[jenkins/no-permission-check]"})
    public FormValidation doAddressCheck(@org.kohsuke.stapler.QueryParameter final String value) {
        return ExtendedEmailGlobalConfiguration.get().doAddressCheck(value);
    }

    @SuppressWarnings({"lgtm[jenkins/csrf]", "lgtm[jenkins/no-permission-check]"})
    public FormValidation doRecipientListRecipientsCheck(
            @org.kohsuke.stapler.QueryParameter final String value) {
        return ExtendedEmailGlobalConfiguration.get().doRecipientListRecipientsCheck(value);
    }

    @SuppressWarnings({"lgtm[jenkins/csrf]", "lgtm[jenkins/no-permission-check]"})
    public FormValidation doMaxAttachmentSizeCheck(@org.kohsuke.stapler.QueryParameter final String value) {
        return ExtendedEmailGlobalConfiguration.get().doMaxAttachmentSizeCheck(value);
    }

    public boolean isMatrixProject(Object project) {
        return project.getClass().getName().equals("hudson.matrix.MatrixProject");
    }

    public boolean isDebugMode() {
        return ExtendedEmailGlobalConfiguration.get().isDebugMode();
    }

    public void setDebugMode(boolean debugMode) {
        ExtendedEmailGlobalConfiguration.get().setDebugMode(debugMode);
    }

    public void debug(PrintStream logger, String format, Object... args) {
        ExtendedEmailGlobalConfiguration.get().debug(logger, format, args);
    }

    BiFunction<MailAccount, Run<?, ?>, Authenticator> getAuthenticatorProvider() {
        return ExtendedEmailGlobalConfiguration.get().getAuthenticatorProvider();
    }

    void setAuthenticatorProvider(BiFunction<MailAccount, Run<?, ?>, Authenticator> authenticatorProvider) {
        ExtendedEmailGlobalConfiguration.get().setAuthenticatorProvider(authenticatorProvider);
    }

    @SuppressWarnings({"lgtm/jenkins/csrf", "lgtm/jenkins/no-permission-check"})
    public FormValidation doCheckAttachmentsPattern(@QueryParameter String value) {
        return ExtendedEmailGlobalConfiguration.get().validateAttachmentPattern(value);
    }

    @SuppressWarnings({"lgtm/jenkins/csrf", "lgtm/jenkins/no-permission-check"})
    public FormValidation doCheckInlineAttachmentsPattern(@QueryParameter String value) {
        return ExtendedEmailGlobalConfiguration.get().validateAttachmentPattern(value);
    }
    
}

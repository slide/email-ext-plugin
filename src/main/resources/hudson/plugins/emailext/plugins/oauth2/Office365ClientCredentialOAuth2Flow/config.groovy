package hudson.plugins.emailext.plugins.oauth2.Office365ClientCredentialOAuth2Flow

f = namespace("/lib/form")

f.entry(title: _("Tenant ID"), field: "tenantId") {
    f.textbox()
}
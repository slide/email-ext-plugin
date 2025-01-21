package hudson.plugins.emailext.plugins.oauth2.Office365UsernamePasswordOAuth2Flow

f = namespace("/lib/form")

f.entry(title: _("Tenant ID"), field: "tenantId") {
    f.textbox()
}

f.entry(title: _("Scope"), field: "scope") {
    f.textbox(default: descriptor.defaultScope)
}

f.entry(title: _("Client ID"), field: "clientId") {
    f.textbox()
}
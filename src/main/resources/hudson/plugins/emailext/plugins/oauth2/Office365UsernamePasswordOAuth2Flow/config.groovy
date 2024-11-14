package hudson.plugins.emailext.plugins.oauth2.Office365UsernamePasswordOAuth2Flow

f = namespace("/lib/form")

f.entry(title: _("Authority"), field: "authority") {
    f.textbox(default: descriptor.defaultAuthority)
}

f.entry(title: _("Scope"), field: "scope") {
    f.textbox(default: descriptor.defaultScope)
}

f.entry(title: _("Client ID"), field: "clientId") {
    f.textbox()
}
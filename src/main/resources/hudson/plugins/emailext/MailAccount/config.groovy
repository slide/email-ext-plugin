import hudson.plugins.emailext.plugins.OAuth2Flow

// Namespaces
f = namespace("/lib/form")
c = namespace("/lib/credentials")

if(instance?.defaultAccount) {
    f.invisibleEntry {
        f.textbox(field: "address")
    }
} else {
    f.entry(field: "address", title: _("Admin Account Address")) {
        f.textbox()
    }
}

f.entry(field: "smtpHost", title: _("SMTP server")) {
    f.textbox()
}
f.entry(field: "smtpPort", title: _("SMTP Port")) {
    f.number(default: "25")
}

f.advanced {
    f.entry(field: "credentialsId", title: _("Credentials")) {
        c.select()
    }
    f.entry(field: "useSsl", title: _("Use SSL")) {
        f.checkbox()
    }
    f.entry(field: "useTls", title: _("Use TLS")) {
        f.checkbox()
    }
    f.entry(field: "OAuth2Flow", title: _("OAuth 2.0 Configuration")) {
        f.dropdownDescriptorSelector(descriptors: OAuth2Flow.all(),
                default: descriptor.defaultOAuth2Flow
        )
    }
    f.entry(field: "advProperties", title: _("Advanced Email Properties")) {
        f.textarea()
    }
}

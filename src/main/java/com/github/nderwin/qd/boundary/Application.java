package com.github.nderwin.qd.boundary;

import com.github.nderwin.qd.control.UserPreferencesStore;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.annotations.RegisterResourceBundle;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import static com.github.nderwin.qd.boundary.LafSupport.DEFAULT_LAF;
import static com.github.nderwin.qd.boundary.LafSupport.apply;
import static com.github.nderwin.qd.boundary.LafSupport.installFlatLookAndFeels;

@RegisterResourceBundle(bundleName = "messages")
public class Application {

    @Inject
    UserPreferencesStore prefs;

    void startup(@Observes StartupEvent evt) {
        installFlatLookAndFeels();

        var laf = prefs.loadLookAndFeel();
        apply(null == laf ? DEFAULT_LAF : laf);
    }

}

package com.nickuc.login.auth.settings;

import com.nickuc.login.config.PasswordHashLoader;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.representer.Representer;
import com.nickuc.login.security.hashing.PendingPasswordHashProvider;

public class SettingsBarrier extends Representer {
   public SettingsBarrier(PasswordHashLoader target, DumperOptions input) {
      super(input);
      this.passwordHashLoader = target;
      this.representers.put(PendingPasswordHashProvider.class, targetValue -> this.represent(((PendingPasswordHashProvider)targetValue).sessions));
   }
}

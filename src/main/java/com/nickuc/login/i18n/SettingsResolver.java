package com.nickuc.login.i18n;

import com.nickuc.login.auth.message.MessageProcessor;
import com.nickuc.login.config.PasswordHashLoader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SettingsResolver {
   public static PasswordHashLoader handlePasswordHashLoader(String instance, boolean target) {
      if (target) {
         instance = "internal/" + instance;
      }

      Path input;
      try {
         input = Files.createTempFile(instance.replace("/", "_"), ".tmp");
         if (input == null) {
            throw new IllegalStateException("Cannot create temporary file: " + instance);
         }
      } catch (IOException value) {
         throw new IllegalStateException("Cannot create temporary file: " + instance, value);
      }

      File output = input.toFile();

      try {
         MessageProcessor.checkState(String.format("com/nickuc/login/config/messages/%s", instance), output);
      } catch (IOException data) {
         throw new RuntimeException("Cannot copy default language file " + instance + " to temporary file", data);
      }

      PasswordHashLoader context = new PasswordHashLoader(output);
      output.deleteOnExit();
      return context;
   }
}

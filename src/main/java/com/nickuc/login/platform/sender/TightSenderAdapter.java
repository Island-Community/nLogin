package com.nickuc.login.platform.sender;

import com.nickuc.login.discord.PasswordHashBridge;
import com.nickuc.login.spawn.LoginLocator;
import java.io.File;
import java.util.List;
import javax.annotation.Nullable;

public interface TightSenderAdapter {
   String getVersion();

   default String handleMessage(boolean target) {
      String input = this.fetchMessage() + "-" + this.getVersion() + ".jar";
      if (target) {
         input = input + ".tmp";
      }

      return input;
   }

   boolean getState();

   @Nullable
   String getMessage();

   boolean fetchState();

   List<LoginLocator> loadCollection();

   default File handleFile(PasswordHashBridge target, boolean input) {
      return new File(target.dataFile, this.handleMessage(input));
   }

   default String getMessageForMessage() {
      return this.fetchMessage() + " v" + this.getVersion();
   }

   String fetchMessage();

   String retrieveMessage();
}

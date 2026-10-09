package com.nickuc.login.auth.password;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.platform.proxy.SilentProxyState;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.settings.SettingsGateway;
import java.io.File;
import java.util.Base64;
import java.util.Collections;
import java.util.UUID;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;

public class PasswordProcessor extends SettingsGateway {
   @Override
   public void saveFile(File target) {
      String input = null;
      String output = null;
      OpenLoginBarrier context = StoredLoginFlow.loadOpenLoginBarrier(target);

      try {
         String data;
         while ((data = context.findMessage()) != null) {
            if (data.startsWith("PLAYER: ") && data.length() > 8) {
               input = data.substring(8);
            } else if (data.startsWith("PASSWORD: ") && data.length() > 10) {
               output = data.substring(10);
            } else {
               String value = data.trim();
               if (value.startsWith("'player' -> \"") && value.length() > 14) {
                  input = value.substring(13, value.length() - 1);
               } else if (value.startsWith("'password' -> \"") && value.length() > 16) {
                  output = value.substring(15, value.length() - 1);
               }
            }
         }

         if (output == null) {
            PasswordHashContainer.performMessage("[" + this.messageOption.getName() + "] Unable to find the player password in file " + target.getName() + ".");
            return;
         }

         UUID element = null;
         if (input == null) {
            String result = target.getName();
            if (result.length() > 5) {
               result = result.substring(0, result.length() - 5);
            }

            element = DeadLoginFlow.buildUniqueId(result);
            OfflinePlayer request = ((Server)this.passwordStore.b().c()).getOfflinePlayer(element);
            input = request.getName();
         }

         if (input != null) {
            String content;
            try {
               content = new String(Base64.getDecoder().decode(output.getBytes()));
            } catch (Exception record) {
               content = output;
            }

            this.updateMessage(input, content + "$LOCKLOGIN", null, element);
         } else {
            PasswordHashContainer.performMessage("[" + this.messageOption.getName() + "] Unable to find the player that has the " + target.getName() + " uuid.");
         }
      } finally {
         if (Collections.singletonList(context).get(0) != null) {
            context.close();
         }
      }
   }

   public PasswordProcessor(PasswordStore target) {
      super(
         target,
         MessageOption.MAIN_MESSAGEOPTION,
         "data" + File.separator + "accounts",
         target.findIncomingLoginGate().retrieveSilentProxyState() == SilentProxyState.SILENT_PROXY_STATE
      );
   }
}

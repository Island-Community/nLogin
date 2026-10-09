package com.nickuc.login.storage.password;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.auth.password.IndirectPasswordCheckpoint;
import com.nickuc.login.auth.message.MessageProcessor;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.platform.connection.LenientConnectionContract;
import java.io.File;
import java.io.IOException;

public class PasswordDao implements LenientConnectionContract {
   public static final PasswordDao passwordDao = new PasswordDao();

   public void processPasswordStore(PasswordStore target) {
      MessageOption input = MessageOption.PRIMARY_MESSAGEOPTION;
      PasswordHashRepository output = input.retrievePasswordHashRepository();
      File context = new File(((IndirectPasswordCheckpoint)output).retrieveFile(), "converted.state");
      if (!context.exists()) {
         PasswordHashContainer.processMessage(
            CachedSettingsGateway.loadState() ? "§eIniciando a conversão " + input.getName() + "..." : "§eStarting the " + input.getName() + " conversion..."
         );
         output.saveOutgoingSenderAdapter(null, true);

         try {
            MessageProcessor.isState(context);
         } catch (IOException value) {
            throw new RuntimeException(value);
         }
      }
   }

   @Override
   public String retrieveMessage() {
      return "OpenLoginConverter";
   }

   @Override
   public boolean validateState(PasswordStore target) {
      MessageOption input = MessageOption.PRIMARY_MESSAGEOPTION;
      PasswordHashRepository output = input.retrievePasswordHashRepository();
      return output != null && output.isAvailable();
   }
}

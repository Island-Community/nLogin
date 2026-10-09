package com.nickuc.login.platform.connection;

import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.premium.FastLoginLinker;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.floodgate.FloodgateGateway;
import com.nickuc.login.storage.password.PasswordHashCollection;
import com.nickuc.login.storage.password.PasswordHashGateway;
import com.nickuc.login.storage.password.PasswordStore;

public interface LenientConnectionContract {
   boolean validateState(PasswordStore target);

   static void handleTask() {
      try {
         Thread.sleep(60000L);
      } catch (InterruptedException target) {
         PasswordHashContainer.sendThrowable(target);
      }

      System.exit(1);
   }

   static void processPasswordStore(PasswordStore instance) {
      if (!FastLoginLinker.fastLoginLinker.b(instance)) {
         handleTask();
      }

      if (!PasswordHashGateway.passwordHashGateway.b(instance)) {
         handleTask();
      }

      if (!PasswordHashCollection.passwordHashCollection.b(instance)) {
         handleTask();
      }

      if (!FloodgateGateway.floodgateGateway.b(instance)) {
         handleTask();
      }
   }

   default boolean hasState(PasswordStore target) {
      try {
         if (this.validateState(target)) {
            this.processPasswordStoreForValue(target);
         }

         return true;
      } catch (Throwable output) {
         PasswordHashContainer.sendThrowable(output);
         if (CachedSettingsGateway.loadState()) {
            PasswordHashContainer.updateMessage("");
            PasswordHashContainer.updateMessage(" Não foi possível converter o banco de dados: " + this.retrieveMessage() + ".");
            PasswordHashContainer.updateMessage(" Por favor, contate nossa equipe:");
            PasswordHashContainer.updateMessage("");
            PasswordHashContainer.updateMessage(" Discord: §bwww.nickuc.com/discord");
            PasswordHashContainer.updateMessage(" E-mail: §fsupport@nickuc.com");
            if (CachedSettingsGateway.resolveState()) {
               PasswordHashContainer.updateMessage(" VK: §fwww.nickuc.com/vk");
            }

            PasswordHashContainer.updateMessage("");
            PasswordHashContainer.updateMessage(" Servidor desligará em 60 segundos");
            PasswordHashContainer.updateMessage("");
         } else {
            PasswordHashContainer.updateMessage("");
            PasswordHashContainer.updateMessage(" The database could not be converted: " + this.retrieveMessage() + ".");
            PasswordHashContainer.updateMessage(" Please contact our team:");
            PasswordHashContainer.updateMessage("");
            PasswordHashContainer.updateMessage(" Discord: §bwww.nickuc.com/discord");
            PasswordHashContainer.updateMessage(" Email: §fsupport@nickuc.com");
            if (CachedSettingsGateway.resolveState()) {
               PasswordHashContainer.updateMessage(" VK: §fwww.nickuc.com/vk");
            }

            PasswordHashContainer.updateMessage("");
            PasswordHashContainer.updateMessage(" Server will shut down in 60 seconds");
            PasswordHashContainer.updateMessage("");
         }

         return false;
      }
   }

   String retrieveMessage();

   void processPasswordStoreForValue(PasswordStore target);

   default void saveSharedListenerContract(SharedListenerContract target, String input, String output) {
      switch (target.resolveDirectNoticeCatalog()) {
         case DIRECT_NOTICE_CATALOG:
         case ACTIVE_DIRECTNOTICECATALOG:
            target.saveMessage("RENAME TABLE " + input + " TO " + output);
            break;
         case CURRENT_DIRECTNOTICECATALOG:
            target.saveMessage("ALTER TABLE " + input + " RENAME TO " + output);
            break;
         default:
            throw new IllegalArgumentException("Invalid database type! " + target.resolveDirectNoticeCatalog());
      }
   }
}

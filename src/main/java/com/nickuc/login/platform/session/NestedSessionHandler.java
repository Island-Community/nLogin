package com.nickuc.login.platform.session;

import com.nickuc.login.storage.settings.LocalSettingsRepository;
import com.nickuc.login.storage.password.PasswordStore;

public interface NestedSessionHandler extends LenientConnectionContract {
   default boolean retrieveState() {
      return true;
   }

   default void processPasswordStore(PasswordStore target) {
      LocalSettingsRepository input = target.fetchLocalSettingsRepository();
      SharedListenerContract output = input.loadSharedListenerContract();
      if (this.retrieveState()) {
         SharedListenerContract context = LocalSettingsRepository.processSharedListenerContract(target, output.resolveDirectNoticeCatalog(), true);

         try {
            this.savePasswordStore(target, input, context);
         } finally {
            context.executeTask();
         }
      } else {
         this.savePasswordStore(target, input, output);
      }
   }

   @Override
   default boolean validateState(PasswordStore target) {
      LocalSettingsRepository input = target.fetchLocalSettingsRepository();
      return this.checkState(target, input, input.loadSharedListenerContract());
   }

   void savePasswordStore(PasswordStore target, LocalSettingsRepository input, SharedListenerContract output);

   boolean checkState(PasswordStore target, LocalSettingsRepository input, SharedListenerContract output);
}

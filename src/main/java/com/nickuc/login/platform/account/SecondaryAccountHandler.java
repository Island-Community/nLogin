package com.nickuc.login.platform.account;

import com.nickuc.login.auth.login.BusyLoginBarrier;
import com.nickuc.login.command.NoticeCommand;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.storage.password.PasswordStore;
import javax.annotation.Nullable;

public interface SecondaryAccountHandler {
   default void updateMessage(String target, String input) {
      this.executeMessage(target, null, null, input, null);
   }

   default void saveMessage(String target, String input) {
      this.executeMessage(target, null, input, null, null);
   }

   default void performMessage(String target, String input) {
      this.executeMessage(target, input, null, null, null);
   }

   VerifiedServerAdapter findVerifiedServerAdapter();

   default void sendMessage(String target, String input, String output) {
      this.executeMessage(target, input, output, null, null);
   }

   void dispatchCount(int target, BusyLoginBarrier[] input);

   default void executeMessage(String target, String input, String output) {
      if (!output.isEmpty() && output.charAt(0) != '/') {
         output = '/' + output;
      }

      this.executeMessage(target, input, output, null, null);
   }

   static SecondaryAccountHandler processSecondaryAccountHandler(PasswordStore instance, VerifiedServerAdapter target, LimboCoordinator input) {
      return new NoticeCommand(target, input, instance.resolveRootMessageHandler());
   }

   void executeMessage(String target, @Nullable String input, @Nullable String output, @Nullable String context, @Nullable String data);

   default void handleMessage(String target, String input, String output) {
      this.executeMessage(target, input, null, output, null);
   }

   default void executeMessage(String target) {
      this.executeMessage(target, null, null, null, null);
   }

   default void processMessage(String target, String input, String output) {
      this.executeMessage(target, input, null, null, output);
   }

   void executeMessage(String target, String input);

   default void dispatchMessage(String target, String input) {
      this.executeMessage(target, null, input);
   }
}

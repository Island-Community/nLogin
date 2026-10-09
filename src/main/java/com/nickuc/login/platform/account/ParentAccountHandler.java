package com.nickuc.login.platform.account;

import com.nickuc.login.auth.login.QuickLoginBarrier;
import com.nickuc.login.auth.sha.Sha256Barrier;
import com.nickuc.login.command.PasswordHashCommand;
import java.util.Collection;
import java.util.Locale;
import java.util.UUID;
import javax.annotation.Nullable;

public interface ParentAccountHandler extends ChildListenerContract {
   AuthenticatedServerAdapter findAuthenticatedServerAdapter();

   Collection<VerifiedServerAdapter> fetchCollection();

   VerifiedServerAdapter resolveVerifiedServerAdapter(String target);

   boolean canState(String target);

   VerifiedServerAdapter processVerifiedServerAdapter(Object target);

   VerifiedServerAdapter createVerifiedServerAdapter(UUID target);

   void executeTask();

   @Nullable
   QuickLoginBarrier loadQuickLoginBarrier(String target);

   Sha256Barrier[] retrieveValues();

   default VerifiedServerAdapter createVerifiedServerAdapter(String target) {
      VerifiedServerAdapter input = this.resolveVerifiedServerAdapter(target);
      if (input == null) {
         int output = Integer.MAX_VALUE;
         String context = target.toLowerCase(Locale.ENGLISH);

         for (VerifiedServerAdapter value : this.fetchCollection()) {
            String result = value.getName();
            if (result.toLowerCase(Locale.ENGLISH).startsWith(context)) {
               int request = Math.abs(result.length() - context.length());
               if (request < output) {
                  input = value;
                  output = request;
               }

               if (request == 0) {
                  break;
               }
            }
         }
      }

      return input;
   }

   NestedServerAdapter processNestedServerAdapter(PasswordHashCommand<?> target);
}

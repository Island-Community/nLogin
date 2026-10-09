package com.nickuc.login.config;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.SharedLoginOption;
import com.nickuc.login.auth.message.MessageProcessor;
import com.nickuc.login.auth.login.SecureLoginGate;
import com.nickuc.login.model.LenientPremiumOption;
import com.nickuc.login.i18n.SettingsResolver;
import com.nickuc.login.notification.NoticeSender;
import com.nickuc.login.notification.SettingsPublisher;
import com.nickuc.login.security.hashing.VerifiedPasswordHashHasher;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.updater.MessageKind;
import com.nickuc.login.updater.NoticeCatalog;
import java.io.File;
import java.util.List;
import javax.annotation.Nullable;

public class SettingsWriter {
   private final SecureLoginGate secureLoginGate;
   private final SecureLoginGate activeSecureLoginGate;
   private final SecureLoginGate pendingSecureLoginGate;
   private final SecureLoginGate currentSecureLoginGate;
   private final PasswordHashLoader passwordHashLoader;
   private final LenientPremiumOption lenientPremiumOption;

   public List<String> loadCollection(LoudProxyState target, Object... input) {
      if (!target.enabled) {
         throw new IllegalStateException(target + " is not a string list!");
      } else {
         return target.b(this.pendingSecureLoginGate, input);
      }
   }

   private void processPasswordHashLoader(PasswordHashLoader target, PasswordHashLoader input, SharedLoginOption output) {
      String context = output.busyLoginProcessor.fetchNames()[0];
      SettingsPublisher data = SettingsPublisher.buildSettingsPublisher(context, output.enabled ? input : this.passwordHashLoader);
      if (!output.enabled && !data.resolveState()) {
         data = SettingsPublisher.buildSettingsPublisher(context, target);
      }

      if (data == null) {
         data = (SettingsPublisher)output.getObject();
      }

      VerifiedPasswordHashHasher.updateInternalListenerContract(output, this.currentSecureLoginGate, data, false);
   }

   public List<String> processCollection(NoticeCatalog target, Object... input) {
      if (!target.enabled) {
         throw new IllegalStateException(target + " is not a string list!");
      } else {
         return target.b(this.activeSecureLoginGate, input);
      }
   }

   public SettingsWriter(PasswordStore target, LenientPremiumOption input, String output) {
      this.lenientPremiumOption = input;
      this.passwordHashLoader = new PasswordHashLoader(new File(target.resolveFile() + File.separator + "lang", output));
      this.pendingSecureLoginGate = new SecureLoginGate(String.format("i18n-%s-text", input.activeName), LoudProxyState.values().length);
      this.activeSecureLoginGate = new SecureLoginGate(String.format("i18n-%s-internaltext", input.activeName), NoticeCatalog.values().length);
      this.currentSecureLoginGate = new SecureLoginGate(String.format("i18n-%s-title", input.activeName), SharedLoginOption.values().length);
      this.secureLoginGate = new SecureLoginGate(String.format("i18n-%s-button", input.activeName), MessageKind.values().length);
      PasswordHashLoader context = SettingsResolver.handlePasswordHashLoader(input.pendingName, false);
      PasswordHashLoader data = SettingsResolver.handlePasswordHashLoader(input.pendingName, true);
      if (!this.passwordHashLoader.resolveStateForState()) {
         if (!this.isState(context, data)) {
            PasswordHashContainer.updateMessage("Unexpected error while loading %s configuration (%s) (probably this is a bug!)", input.pendingName, input);
         }
      } else if (!this.canState(context, data)) {
         File value = MessageProcessor.buildFile(
            new File(this.passwordHashLoader.retrieveFile().getParentFile(), output + ".old"),
            MessageProcessor.resolveMessage(this.passwordHashLoader.retrieveFile()) + "-%d.yml"
         );
         if (!this.passwordHashLoader.retrieveFile().renameTo(value)) {
            return;
         }

         if (!this.isState(context, data)) {
            PasswordHashContainer.updateMessage("Unexpected error while loading %s configuration (%s) (probably this is a bug!)", input.pendingName, input);
         }
      }

      context.findState();
      data.findState();
   }

   private boolean isState(PasswordHashLoader target, PasswordHashLoader input) {
      this.passwordHashLoader.hasStateForState("com/nickuc/login/config/messages/" + this.lenientPremiumOption.pendingName);
      return this.canState(target, input);
   }

   public String processMessage(NoticeCatalog target, Object... input) {
      if (target.enabled) {
         throw new IllegalStateException(target + " is not a string!");
      } else {
         return target.a(this.activeSecureLoginGate, input);
      }
   }

   public String computeMessage(LoudProxyState target, Object... input) {
      if (target.enabled) {
         throw new IllegalStateException(target + " is not a string!");
      } else {
         return target.a(this.pendingSecureLoginGate, input);
      }
   }

   public SettingsPublisher loadSettingsPublisher(SharedLoginOption target) {
      return (SettingsPublisher)target.b(this.currentSecureLoginGate);
   }

   public List<String> resolveCollection(LoudProxyState target, Object... input) {
      if (!target.enabled) {
         throw new IllegalStateException(target + " is not a string list!");
      } else {
         return target.a(this.pendingSecureLoginGate, input);
      }
   }

   public NoticeSender handleNoticeSender(MessageKind target) {
      return (NoticeSender)target.b(this.secureLoginGate);
   }

   private void updatePasswordHashLoader(PasswordHashLoader target, PasswordHashLoader input, MessageKind output) {
      String context = output.busyLoginProcessor.fetchNames()[0];
      NoticeSender data = MessageKind.createNoticeSender(context, output.enabled ? input : this.passwordHashLoader);
      if (!output.enabled && data == null) {
         data = MessageKind.createNoticeSender(context, target);
      }

      if (data == null) {
         data = (NoticeSender)output.getObject();
      }

      VerifiedPasswordHashHasher.updateInternalListenerContract(output, this.secureLoginGate, data, false);
   }

   public List<String> processCollectionForCollection(NoticeCatalog target, Object... input) {
      if (!target.enabled) {
         throw new IllegalStateException(target + " is not a string list!");
      } else {
         return target.a(this.activeSecureLoginGate, input);
      }
   }

   private boolean canState(PasswordHashLoader target, PasswordHashLoader input) {
      File output = this.passwordHashLoader.retrieveFile();
      PasswordHashContainer.dispatchMessage("Loading the " + output.getName() + " message file...");
      byte context = 1;

      for (LoudProxyState request : LoudProxyState.values()) {
         try {
            this.handlePasswordHashLoader(target, request);
         } catch (Exception item) {
            context = 0;
            VerifiedPasswordHashHasher.updateInternalListenerContract(request, this.pendingSecureLoginGate, request.object, true);
            PasswordHashContainer.handleMessage("Failed to load message " + request.busyLoginProcessor.fetchNames()[0] + ", lang: " + output.getName(), item);
         }
      }

      for (NoticeCatalog attribute : NoticeCatalog.values()) {
         try {
            this.savePasswordHashLoader(input, attribute);
         } catch (Exception record) {
            context = 0;
            VerifiedPasswordHashHasher.updateInternalListenerContract(attribute, this.activeSecureLoginGate, attribute.getObject(), true);
            PasswordHashContainer.handleMessage("Failed to load message " + attribute.busyLoginProcessor.fetchNames()[0] + ", lang: " + output.getName(), record);
         }
      }

      for (SharedLoginOption parameter : SharedLoginOption.values()) {
         try {
            this.processPasswordHashLoader(target, input, parameter);
         } catch (Exception entry) {
            context = 0;
            PasswordHashContainer.handleMessage("Failed to load title " + parameter.busyLoginProcessor.fetchNames()[0] + ", lang: " + output.getName(), entry);
         }
      }

      for (MessageKind argument : MessageKind.values()) {
         try {
            this.updatePasswordHashLoader(target, input, argument);
         } catch (Exception source) {
            context = 0;
            PasswordHashContainer.handleMessage("Failed to load button " + argument.busyLoginProcessor.fetchNames()[0] + ", lang: " + output.getName(), source);
         }
      }

      return (boolean)context;
   }

   private void handlePasswordHashLoader(PasswordHashLoader target, LoudProxyState input) {
      if (input.enabled) {
         List output = null;

         for (String result : input.busyLoginProcessor.fetchNames()) {
            if ((output = this.passwordHashLoader.computeCollection(result, null)) != null) {
               break;
            }
         }

         if (output == null) {
            for (String reference : input.busyLoginProcessor.fetchNames()) {
               if ((output = target.computeCollection(reference, null)) != null) {
                  break;
               }
            }
         }

         if (output == null || output.isEmpty()) {
            output = (List)input.object;
         }

         VerifiedPasswordHashHasher.updateInternalListenerContract(input, this.pendingSecureLoginGate, output, true);
      } else {
         String request = null;

         for (String subject : input.busyLoginProcessor.fetchNames()) {
            if ((request = this.passwordHashLoader.b(subject)) != null) {
               break;
            }
         }

         if (request == null && "HANDLED".equals(request = this.createMessage(input))) {
            return;
         }

         if (request == null) {
            for (String option : input.busyLoginProcessor.fetchNames()) {
               if ((request = target.b(option)) != null) {
                  break;
               }
            }
         }

         if (request == null) {
            request = (String)input.object;
         }

         VerifiedPasswordHashHasher.updateInternalListenerContract(input, this.pendingSecureLoginGate, request, true);
      }
   }

   private void savePasswordHashLoader(PasswordHashLoader target, NoticeCatalog input) {
      if (input.enabled) {
         List output = null;

         for (String result : input.busyLoginProcessor.fetchNames()) {
            if ((output = target.computeCollection(result, null)) != null) {
               break;
            }
         }

         if (output == null || output.isEmpty()) {
            output = (List)input.getObject();
         }

         VerifiedPasswordHashHasher.updateInternalListenerContract(input, this.activeSecureLoginGate, output, true);
      } else {
         String request = null;

         for (String record : input.busyLoginProcessor.fetchNames()) {
            if ((request = target.b(record)) != null) {
               break;
            }
         }

         if (request == null) {
            request = (String)input.getObject();
         }

         VerifiedPasswordHashHasher.updateInternalListenerContract(input, this.activeSecureLoginGate, request, true);
      }
   }

   @Nullable
   private String createMessage(LoudProxyState target) {
      switch (target) {
         case PRIMARY_LOUDPROXYSTATE:
         case MAIN_LOUDPROXYSTATE:
            LoudProxyState input = target == LoudProxyState.PRIMARY_LOUDPROXYSTATE ? LoudProxyState.PENDING_LOUDPROXYSTATE : LoudProxyState.CURRENT_LOUDPROXYSTATE;
            if (this.passwordHashLoader.canState(input.busyLoginProcessor.fetchNames()[0])) {
               String output = this.computeMessage(input, "x");
               String[] context = output.split("///");
               if (context.length == 1) {
                  context = output.split("/");
                  if (context.length == 2) {
                     VerifiedPasswordHashHasher.updateInternalListenerContract(
                        input, this.pendingSecureLoginGate, input.a(this.pendingSecureLoginGate, new Object[0]).replace("/", "///"), false
                     );
                  }
               }

               if (context.length == 2 && context[0].length() > 1 && context[1].length() > 1) {
                  int data = context[0].lastIndexOf("{");
                  int value = context[1].indexOf("}");
                  if (data >= 0 && value > 0) {
                     return context[0].substring(data + 1) + "///" + context[1].substring(0, value);
                  }
               }

               return "";
            }

            if (this.lenientPremiumOption != LenientPremiumOption.LENIENT_PREMIUM_OPTION
               && this.lenientPremiumOption != LenientPremiumOption.INCOMING_LENIENTPREMIUMOPTION) {
               VerifiedPasswordHashHasher.updateInternalListenerContract(
                  input,
                  this.pendingSecureLoginGate,
                  "§cYou have {0} second" + (target == LoudProxyState.PRIMARY_LOUDPROXYSTATE ? "" : "s") + " to {login///register}",
                  false
               );
               VerifiedPasswordHashHasher.updateInternalListenerContract(target, this.pendingSecureLoginGate, "login///register", false);
            } else {
               VerifiedPasswordHashHasher.updateInternalListenerContract(
                  input,
                  this.pendingSecureLoginGate,
                  "§cVocê possui {0} segundo" + (target == LoudProxyState.PRIMARY_LOUDPROXYSTATE ? "" : "s") + " para se {logar///registrar}",
                  false
               );
               VerifiedPasswordHashHasher.updateInternalListenerContract(target, this.pendingSecureLoginGate, "logar///registrar", false);
            }

            return "HANDLED";
         default:
            return null;
      }
   }
}

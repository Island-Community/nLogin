package com.nickuc.login.mail;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.StrictMessageKind;
import com.nickuc.login.auth.login.SecureLoginHandler;
import com.nickuc.login.auth.login.StoredLoginGate;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.config.PasswordHashLoader;
import com.nickuc.login.model.DirectProxyState;
import com.nickuc.login.model.SecondaryMessageKind;
import com.nickuc.login.model.SecondaryPlatformCatalog;
import com.nickuc.login.platform.listener.RootListenerContract;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.DiscordVerifier;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.password.PasswordStore;
import java.util.function.Consumer;
import javax.annotation.Nullable;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

public class MailNotifier implements RootListenerContract {
   private boolean enabled;
   private final PasswordStore passwordStore;
   private final DiscordVerifier discordVerifier;
   private StoredLoginGate storedLoginGate;

   public void updateMessage(String target, String input, String output, @Nullable Consumer<Boolean> context) {
      this.passwordStore.processLinkedSessionHandler(true).buildStrictCommandHandler(() -> {
         boolean data = this.verifyState(target, input, output);
         if (context != null) {
            context.accept(data);
         }
      });
   }

   @Override
   public void saveSpawnLookup(SpawnLookup target, VerifiedServerAdapter input, String output) {
      String context = target.fetchQuickDiscordHandler().loadMessage();
      String data = String.join("", CachedSettingsGateway.computeCollection(LoudProxyState.PRIMARY_SECONDARY_LOUDPROXYSTATE, input, output));
      String value = CachedSettingsGateway.computeMessage(LoudProxyState.PRIMARY_OUTGOING_LOUDPROXYSTATE, input, output);
      this.updateMessage(context, value, data, null);
   }

   @Override
   public boolean findState() {
      return this.enabled;
   }

   public StoredLoginGate findStoredLoginGate() {
      return this.storedLoginGate;
   }

   @Override
   public void dispatchSpawnLookup(SpawnLookup target, VerifiedServerAdapter input) {
      String output = input.getName();
      String context = target.fetchQuickDiscordHandler().loadMessage();
      if (!StrictMessageKind.ACTIVE_STRICTMESSAGEKIND.canState(target, SecondaryPlatformCatalog.ACTIVE_SECONDARYPLATFORMCATALOG)) {
         String data = SecureLoginHandler.loadMessage(SecondaryMessageKind.CURRENT_SECONDARYMESSAGEKIND, 6);
         StrictMessageKind.ACTIVE_STRICTMESSAGEKIND.updateSpawnLookup(target, data, SecondaryPlatformCatalog.ACTIVE_SECONDARYPLATFORMCATALOG);
         String value = String.join("", CachedSettingsGateway.handleCollection(LoudProxyState.PRIMARY_INTERNAL_LOUDPROXYSTATE))
            .replace("@player", output)
            .replace("@code", data);
         String result = CachedSettingsGateway.computeMessage(LoudProxyState.PRIMARY_PRIVATE_LOUDPROXYSTATE).replace("@player", output).replace("@code", data);
         this.updateMessage(context, result, value, targetValue -> {
            if (!targetValue) {
               StrictMessageKind.ACTIVE_STRICTMESSAGEKIND.updateSpawnLookup(target, null, null);
            }
         });
      }
   }

   public DiscordVerifier resolveDiscordVerifier() {
      return this.discordVerifier;
   }

   public PasswordStore retrievePasswordStore() {
      return this.passwordStore;
   }

   @Override
   public void processSpawnLookup(SpawnLookup target, VerifiedServerAdapter input, String output) {
      String context = input.getName();
      String data = SecureLoginHandler.loadMessage(SecondaryMessageKind.CURRENT_SECONDARYMESSAGEKIND, 6);
      StrictMessageKind.ACTIVE_STRICTMESSAGEKIND.updateSpawnLookup(target, data, SecondaryPlatformCatalog.SECONDARY_PLATFORM_CATALOG);
      String value = String.join("", CachedSettingsGateway.handleCollection(LoudProxyState.PRIMARY_VERIFIED_LOUDPROXYSTATE))
         .replace("@player", context)
         .replace("@code", data);
      String result = CachedSettingsGateway.computeMessage(LoudProxyState.PRIMARY_STORED_LOUDPROXYSTATE).replace("@player", context).replace("@code", data);
      this.updateMessage(output, result, value, targetValue -> {
         if (!targetValue) {
            StrictMessageKind.ACTIVE_STRICTMESSAGEKIND.updateSpawnLookup(target, null, null);
         }
      });
   }

   @Override
   public void performSpawnLookup(SpawnLookup target, VerifiedServerAdapter input) {
      String output = input.getName();
      String context = target.fetchQuickDiscordHandler().loadMessage();
      if (!StrictMessageKind.ACTIVE_STRICTMESSAGEKIND.canState(target, SecondaryPlatformCatalog.PENDING_SECONDARYPLATFORMCATALOG)) {
         String data = SecureLoginHandler.loadMessage(SecondaryMessageKind.CURRENT_SECONDARYMESSAGEKIND, 6);
         StrictMessageKind.ACTIVE_STRICTMESSAGEKIND.updateSpawnLookup(target, data, SecondaryPlatformCatalog.PENDING_SECONDARYPLATFORMCATALOG);
         String value = String.join("", CachedSettingsGateway.handleCollection(LoudProxyState.PRIMARY_SHARED_LOUDPROXYSTATE))
            .replace("@player", output)
            .replace("@code", data);
         String result = CachedSettingsGateway.computeMessage(LoudProxyState.PRIMARY_AUTHENTICATED_LOUDPROXYSTATE)
            .replace("@player", output)
            .replace("@code", data);
         this.updateMessage(context, result, value, targetValue -> {
            if (!targetValue) {
               StrictMessageKind.ACTIVE_STRICTMESSAGEKIND.updateSpawnLookup(target, null, null);
            }
         });
      }
   }

   private boolean verifyState(String target, String input, String output) {
      if (!this.enabled) {
         return false;
      }

      if (this.storedLoginGate == null) {
         return false;
      }

      if (target.split("@").length != 2) {
         throw new IllegalArgumentException("Invalid email! " + target);
      }

      if (input == null || input.isEmpty()) {
         throw new IllegalArgumentException("Email subject cannot be null or empty!");
      }

      if (output != null && !output.isEmpty()) {
         HtmlEmail context = new HtmlEmail();
         context.setHostName(this.storedLoginGate.localName);
         context.setAuthenticator(new DefaultAuthenticator(this.storedLoginGate.primaryName, this.storedLoginGate.name));
         context.setSmtpPort(this.storedLoginGate.count);
         context.setCharset(this.storedLoginGate.activeName);
         context.setDebug(this.storedLoginGate.enabled);
         switch (this.storedLoginGate.directProxyState) {
            case ACTIVE_DIRECTPROXYSTATE:
               context.setStartTLSEnabled(true);
               break;
            case PENDING_DIRECTPROXYSTATE:
               context.setSSLOnConnect(true);
               context.setSslSmtpPort(Integer.toString(this.storedLoginGate.count));
         }

         try {
            context.setFrom(this.storedLoginGate.pendingName, this.storedLoginGate.currentName);
            context.addTo(target);
            context.setSubject(input);
            context.setContent(output, this.storedLoginGate.mainName);
            context.send();
            return true;
         } catch (EmailException value) {
            PasswordHashContainer.handleMessage("[Email] Failed to send email. Check the current credentials provided and your firewall.", value);
            return false;
         }
      } else {
         throw new IllegalArgumentException("Email html content cannot be null or empty!");
      }
   }

   @Override
   public void updateSpawnLookup(SpawnLookup target, VerifiedServerAdapter input, String output) {
      String context = target.fetchQuickDiscordHandler().loadMessage();
      String data = String.join("", CachedSettingsGateway.computeCollection(LoudProxyState.PRIMARY_INCOMING_LOUDPROXYSTATE, input, output));
      String value = CachedSettingsGateway.computeMessage(LoudProxyState.PRIMARY_UPSTREAM_LOUDPROXYSTATE, input, output);
      this.updateMessage(context, value, data, null);
   }

   public MailNotifier(PasswordStore target, DiscordVerifier input) {
      this.passwordStore = target;
      this.discordVerifier = input;
   }

   @Override
   public void updateTask() {
      PasswordHashLoader target = this.discordVerifier.retrievePasswordHashLoader();
      if (target == null) {
         throw new IllegalStateException(this + " config not loaded!");
      }

      String input = target.b("authentication.email");
      if (input != null) {
         String output = target.a("authentication.user", "");
         if (output.isEmpty()) {
            output = input;
         }

         int context = target.a("authentication.port", 587);
         DirectProxyState data = DirectProxyState.handleDirectProxyState(target.a("authentication.encryption", "auto"), context);
         this.storedLoginGate = new StoredLoginGate(
            input,
            target.b("authentication.from"),
            output,
            target.b("authentication.password"),
            target.a("authentication.charset", "UTF-8"),
            target.a("authentication.mimetype", "text/html"),
            target.a("authentication.host", "smtp.gmail.com"),
            context,
            data,
            target.a("authentication.debug", false)
         );
         this.enabled = true;
      }
   }
}

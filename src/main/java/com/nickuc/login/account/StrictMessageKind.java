package com.nickuc.login.account;

import com.nickuc.login.auth.login.PrimaryLoginHandler;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.config.PasswordHashLoader;
import com.nickuc.login.discord.QuickDiscordHandler;
import com.nickuc.login.model.OutgoingSpawnState;
import com.nickuc.login.model.SecondaryPlatformCatalog;
import com.nickuc.login.platform.listener.RootListenerContract;
import com.nickuc.login.platform.listener.SharedListenerContract;
import com.nickuc.login.premium.DiscordVerifier;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.security.hashing.CachedPasswordHashHasher;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.notice.DirectNoticeCatalog;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.spawn.SpawnState;
import java.sql.ResultSet;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;


public enum StrictMessageKind {
   STRICT_MESSAGE_KIND("discord"),
   ACTIVE_STRICTMESSAGEKIND("email");

   public static final int count = 15;
   public static final int activeCount = 3;
   private final String name;

   public SecondaryPlatformCatalog createSecondaryPlatformCatalog(SpawnLookup target) {
      String input = target.resolveCachedPasswordHashHasher().buildObject("2fa-intent");
      return input != null ? SecondaryPlatformCatalog.valueOf(input) : null;
   }

   public boolean findState() {
      switch (this) {
         case STRICT_MESSAGE_KIND:
            return StrictPremiumOption.LOCAL_STRICTPREMIUMOPTION.retrieveState();
         case ACTIVE_STRICTMESSAGEKIND:
            return StrictPremiumOption.UPSTREAM_STRICTPREMIUMOPTION.retrieveState();
         default:
            throw new IllegalArgumentException("Unknown type " + this + ".");
      }
   }

   public boolean validateState(SpawnLookup target) {
      QuickDiscordHandler input = target.fetchQuickDiscordHandler();
      switch (this) {
         case STRICT_MESSAGE_KIND:
            return input.fetchState();
         case ACTIVE_STRICTMESSAGEKIND:
            return input.retrieveState();
         default:
            throw new IllegalArgumentException("Unknown type " + this + ".");
      }
   }

   public boolean hasState(SpawnLookup target, @Nullable SecondaryPlatformCatalog input, String output) {
      return output != null && output.equalsIgnoreCase(this.buildMessage(target, input));
   }

   public String buildMessage(SpawnLookup target, @Nullable SecondaryPlatformCatalog input) {
      return this.verifyState(target, input) ? target.resolveCachedPasswordHashHasher().buildObject("2fa-code") : null;
   }

   public void handleSpawnLookup(SpawnLookup target) {
      CachedPasswordHashHasher input = target.resolveCachedPasswordHashHasher();
      Integer output = input.buildObject("2fa-" + this.name + "-rate-limit-trigger");
      if (output == null) {
         output = 0;
      }

      output = output + 1;
      input.updateMessage("2fa-" + this.name + "-rate-limit-trigger", output, 15L, TimeUnit.MINUTES);
   }

   public String getName() {
      return this.name;
   }

   public String findMessage() {
      return this == ACTIVE_STRICTMESSAGEKIND && CachedSettingsGateway.loadState() ? "e-mail" : this.name;
   }

   public void updateSpawnLookup(SpawnLookup target, String input, SecondaryPlatformCatalog output) {
      if (this == STRICT_MESSAGE_KIND && output == SecondaryPlatformCatalog.SECONDARY_PLATFORM_CATALOG) {
         throw new IllegalStateException("Temporary code cannot be set for " + this + " 2FA when using " + output + " intent!");
      }

      CachedPasswordHashHasher context = target.resolveCachedPasswordHashHasher();
      if (input != null && output != null) {
         context.updateMessage("2fa-code", input, 15L, TimeUnit.MINUTES);
         context.updateMessage("2fa-intent", output.name(), 15L, TimeUnit.MINUTES);
      } else {
         context.updateMessage("2fa-code");
         context.updateMessage("2fa-intent");
      }
   }

   @Nullable
   public PasswordHashLoader processPasswordHashLoader(PasswordStore target) {
      DiscordVerifier input = target.resolveDiscordVerifier();
      switch (this) {
         case STRICT_MESSAGE_KIND:
            return input.fetchPasswordHashLoader();
         case ACTIVE_STRICTMESSAGEKIND:
            return input.retrievePasswordHashLoader();
         default:
            throw new IllegalArgumentException("Unknown type " + this + ".");
      }
   }

   public boolean isState(SpawnLookup target) {
      Integer input = target.resolveCachedPasswordHashHasher().buildObject("2fa-" + this.name + "-rate-limit-trigger");
      return input != null && input >= 3;
   }

   public int processCount(PasswordStore target, String input) {
      OutgoingSpawnState output;
      switch (this) {
         case STRICT_MESSAGE_KIND:
            output = OutgoingSpawnState.VERIFIED_OUTGOINGSPAWNSTATE;
            break;
         case ACTIVE_STRICTMESSAGEKIND:
            output = OutgoingSpawnState.STORED_OUTGOINGSPAWNSTATE;
            break;
         default:
            throw new IllegalArgumentException("Unknown type " + this + ".");
      }

      SharedListenerContract context = target.fetchLocalSettingsRepository().loadSharedListenerContract();
      String data = "SELECT COUNT(*) FROM `%s` WHERE %s = ?"
         + (context.resolveDirectNoticeCatalog() == DirectNoticeCatalog.CURRENT_DIRECTNOTICECATALOG ? " COLLATE NOCASE" : "");

      try {
         PrimaryLoginHandler value = context.buildPrimaryLoginHandler(String.format(data, SpawnState.CURRENT_SPAWNSTATE.a(new Object[0]), output.getName()), input);

         int request;
         label65: {
            try {
               ResultSet result = value.resolveObject();
               if (result.next()) {
                  request = result.getInt("COUNT(*)");
                  break label65;
               }
            } catch (Throwable source) {
               if (value != null) {
                  try {
                     value.close();
                  } catch (Throwable response) {
                     source.addSuppressed(response);
                  }
               }

               throw source;
            }

            if (value != null) {
               value.close();
            }

            return 0;
         }

         if (value != null) {
            value.close();
         }

         return request;
      } catch (Exception entry) {
         PasswordHashContainer.updateMessage("Unable to get duplicates for account " + input + ", type: " + this);
         return 0;
      }
   }

   public boolean retrieveState() {
      switch (this) {
         case STRICT_MESSAGE_KIND:
            return StrictPremiumOption.STORED_STRICTPREMIUMOPTION.retrieveState();
         case ACTIVE_STRICTMESSAGEKIND:
            return StrictPremiumOption.OUTGOING_STRICTPREMIUMOPTION.retrieveState();
         default:
            throw new IllegalArgumentException("Unknown type " + this + ".");
      }
   }

   public boolean verifyState(SpawnLookup target, @Nullable SecondaryPlatformCatalog input) {
      if (input == null) {
         return true;
      }

      SecondaryPlatformCatalog output = this.createSecondaryPlatformCatalog(target);
      return output != null && output == input;
   }

   public boolean loadState() {
      switch (this) {
         case STRICT_MESSAGE_KIND:
            return StrictPremiumOption.MAIN_STRICTPREMIUMOPTION.retrieveState();
         case ACTIVE_STRICTMESSAGEKIND:
            return StrictPremiumOption.INTERNAL_STRICTPREMIUMOPTION.retrieveState();
         default:
            throw new IllegalArgumentException("Unknown type " + this + ".");
      }
   }

   public boolean hasState(PasswordStore target) {
      switch (this) {
         case STRICT_MESSAGE_KIND:
            return StrictPremiumOption.STRICT_PREMIUM_OPTION.retrieveState() && target.resolveDiscordVerifier().retrieveFastDiscordNotifier() != null;
         case ACTIVE_STRICTMESSAGEKIND:
            return StrictPremiumOption.VERIFIED_STRICTPREMIUMOPTION.retrieveState() && target.resolveDiscordVerifier().getMailNotifier() != null;
         default:
            throw new IllegalArgumentException("Unknown type " + this + ".");
      }
   }

   public boolean getState() {
      switch (this) {
         case STRICT_MESSAGE_KIND:
            return StrictPremiumOption.PRIMARY_STRICTPREMIUMOPTION.retrieveState();
         case ACTIVE_STRICTMESSAGEKIND:
            return StrictPremiumOption.PRIVATE_STRICTPREMIUMOPTION.retrieveState();
         default:
            throw new IllegalArgumentException("Unknown type " + this + ".");
      }
   }

   public RootListenerContract processRootListenerContract(PasswordStore target) {
      DiscordVerifier input = target.resolveDiscordVerifier();
      RootListenerContract output;
      switch (this) {
         case STRICT_MESSAGE_KIND:
            output = input.retrieveFastDiscordNotifier();
            break;
         case ACTIVE_STRICTMESSAGEKIND:
            output = input.getMailNotifier();
            break;
         default:
            throw new IllegalArgumentException("Unknown type " + this + ".");
      }

      if (output == null) {
         throw new IllegalStateException(this + " manager is not available!");
      } else {
         return output;
      }
   }

   public String createMessage(SpawnLookup target) {
      return target.resolveCachedPasswordHashHasher().buildObject("2fa-" + this.name + "-account");
   }

   public boolean fetchState() {
      switch (this) {
         case STRICT_MESSAGE_KIND:
            return StrictPremiumOption.CURRENT_STRICTPREMIUMOPTION.retrieveState();
         case ACTIVE_STRICTMESSAGEKIND:
            return StrictPremiumOption.SHARED_STRICTPREMIUMOPTION.retrieveState();
         default:
            throw new IllegalArgumentException("Unknown type " + this + ".");
      }
   }

   @Nullable
   public String resolveMessage() {
      InternalLoginOption target;
      switch (this) {
         case STRICT_MESSAGE_KIND:
            target = InternalLoginOption.PENDING_INTERNALLOGINOPTION;
            break;
         case ACTIVE_STRICTMESSAGEKIND:
            target = InternalLoginOption.CURRENT_INTERNALLOGINOPTION;
            break;
         default:
            throw new IllegalArgumentException("Unknown type " + this + ".");
      }

      return target.fetchPasswordHashCommand().getCollection().stream().findFirst().orElse(null);
   }

   public boolean resolveState() {
      switch (this) {
         case STRICT_MESSAGE_KIND:
            return StrictPremiumOption.PENDING_STRICTPREMIUMOPTION.retrieveState();
         case ACTIVE_STRICTMESSAGEKIND:
            return StrictPremiumOption.AUTHENTICATED_STRICTPREMIUMOPTION.retrieveState();
         default:
            throw new IllegalArgumentException("Unknown type " + this + ".");
      }
   }

   StrictMessageKind(String output) {
      this.name = output;
   }

   public boolean canState(SpawnLookup target, @Nullable SecondaryPlatformCatalog input) {
      return this.buildMessage(target, input) != null;
   }

   @Nullable
   public String computeMessage(SpawnLookup target) {
      QuickDiscordHandler input = target.fetchQuickDiscordHandler();
      switch (this) {
         case STRICT_MESSAGE_KIND:
            return input.getMessage();
         case ACTIVE_STRICTMESSAGEKIND:
            return input.loadMessage();
         default:
            throw new IllegalArgumentException("Unknown type " + this + ".");
      }
   }

   public void updateSpawnLookup(SpawnLookup target, String input) {
      CachedPasswordHashHasher output = target.resolveCachedPasswordHashHasher();
      if (input != null) {
         output.updateMessage("2fa-" + this.name + "-account", input, 15L, TimeUnit.MINUTES);
      } else {
         output.updateMessage("2fa-" + this.name + "-account");
      }
   }
}

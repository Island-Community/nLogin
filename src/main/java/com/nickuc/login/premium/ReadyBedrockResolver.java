package com.nickuc.login.premium;

import com.nickuc.login.auth.login.ChildLoginCheckpoint;
import com.nickuc.login.auth.login.LoginCheckpoint;
import com.nickuc.login.auth.login.PrimaryLoginGate;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.platform.connection.SecondaryConnectionContract;
import com.nickuc.login.platform.proxy.SilentProxyState;
import com.nickuc.login.session.MojangCoordinator;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.floodgate.FloodgateTable;
import com.nickuc.login.storage.password.PasswordStore;
import io.netty.util.AttributeKey;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import javax.annotation.Nullable;

import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

public class ReadyBedrockResolver implements SecondaryConnectionContract {
   private final AttributeKey<FloodgatePlayer> attributeKey = AttributeKey.valueOf("floodgate-player");
   private static final Field field;
   @Nullable
   private final FloodgateTable floodgateTable;
   private static final Class<?> value = ChildLoginCheckpoint.loadClass("org.geysermc.floodgate.player.FloodgatePlayerImpl");

   public AttributeKey<FloodgatePlayer> fetchAttributeKey() {
      return this.attributeKey;
   }

   @Override
   public boolean isState(UUID target) {
      try {
         return target != null && FloodgateApi.getInstance().isFloodgatePlayer(target);
      } catch (Throwable output) {
         if (output.getCause() instanceof ClassNotFoundException) {
            PasswordHashContainer.performMessage("Unable to retrieve the Floodgate's player. This is a Floodgate error: %s", output.getMessage());
         } else {
            PasswordHashContainer.processMessage("Unable to retrieve the Floodgate's player. Most likely this is a Floodgate error.", output);
         }

         return false;
      }
   }

   @Nullable
   public FloodgateTable findFloodgateTable() {
      return this.floodgateTable;
   }

   @Override
   public boolean fetchState() {
      return this.findCount() != 0;
   }

   public String fetchMessage() {
      try {
         return FloodgateApi.getInstance().getPlayerPrefix();
      } catch (Throwable input) {
         if (input.getCause() instanceof ClassNotFoundException) {
            PasswordHashContainer.performMessage("Unable to retrieve the Floodgate's player. This is a Floodgate error: %s", input.getMessage());
         } else {
            PasswordHashContainer.processMessage("Unable to retrieve the Floodgate's player. Most likely this is a Floodgate error.", input);
         }

         return ".";
      }
   }

   private boolean hasState(InetAddress target) {
      return target.isLoopbackAddress()
         || target.isAnyLocalAddress()
         || PrimaryLoginGate.activePrimaryLoginGate.validateState(target)
         || PrimaryLoginGate.primaryLoginGate.validateState(target)
         || PrimaryLoginGate.pendingPrimaryLoginGate.validateState(target);
   }

   @Nullable
   public FloodgatePlayer computeFloodgatePlayer(String target, String input) {
      try {
         for (FloodgatePlayer context : FloodgateApi.getInstance().getPlayers()) {
            String data = context.getUsername().replace(' ', '_');
            String value = context.getCorrectUsername();
            if (data.equals(target) || value.equals(target)) {
               String result;
               try {
                  result = (String)field.get(context);
               } catch (IllegalAccessException entry) {
                  throw new RuntimeException(entry);
               }

               if (!result.equals(input)) {
                  InetAddress request;
                  try {
                     request = InetAddress.getByName(input);
                  } catch (UnknownHostException record) {
                     String source = String.format(
                        "Bedrock account \"%s\" (known as %s) threw a validation error: Java IP \"%s\" is not equal to Bedrock IP \"%s\" and it is invalid! \""
                           + record.getMessage()
                           + "\"",
                        context.getUsername(),
                        context.getCorrectUsername(),
                        input,
                        result
                     );
                     PasswordHashContainer.performMessage(source);
                     break;
                  }

                  if (!this.hasState(request)) {
                     String response = String.format(
                        "Bedrock account \"%s\" (known as %s) threw a validation error: Bedrock IP \"%s\" is not equal to Java IP \"%s\"",
                        context.getUsername(),
                        context.getCorrectUsername(),
                        result,
                        input
                     );
                     PasswordHashContainer.performMessage(response);
                     break;
                  }
               }

               return context;
            }
         }
      } catch (Throwable item) {
         PasswordHashContainer.handleMessage("Unable to retrieve the Floodgate's player.", item);
      }

      return null;
   }

   public ReadyBedrockResolver(PasswordStore target) {
      if (target.findIncomingLoginGate().retrieveSilentProxyState() == SilentProxyState.ACTIVE_SILENTPROXYSTATE && !MojangCoordinator.enabled) {
         PasswordHashContainer.performMessage(
            "Disabling Cumulus forms, since it is broken for BungeeCord in Floodgate v2: https://github.com/GeyserMC/Floodgate/issues/178."
         );
         PasswordHashContainer.performMessage("Run the server with flag \"-Dnlogin.force-geyser-forms=1\" to force enable Cumulus forms.");
         this.floodgateTable = null;
      } else {
         this.floodgateTable = new FloodgateTable(target, this);
      }

      if (!this.fetchState()) {
         if (CachedSettingsGateway.loadState()) {
            PasswordHashContainer.performMessage("Nenhum prefixo foi definido para os jogadores Geyser (usando Floodgate).");
            PasswordHashContainer.performMessage("Isso pode evitar que contas offline/premium possam acessar usando nicknames de jogadores Bedrock.");
         } else {
            PasswordHashContainer.performMessage("No prefix has been defined for Geyser players (using Floodgate).");
            PasswordHashContainer.performMessage("This can prevent offline/premium accounts from logging in using Bedrock player nicknames");
         }
      }
   }

   @Nullable
   public FloodgatePlayer loadFloodgatePlayer(UUID target) {
      try {
         return target != null ? FloodgateApi.getInstance().getPlayer(target) : null;
      } catch (Throwable output) {
         if (output.getCause() instanceof ClassNotFoundException) {
            PasswordHashContainer.performMessage("Unable to retrieve the Floodgate's player. This is a Floodgate error: %s", output.getMessage());
         } else {
            PasswordHashContainer.processMessage("Unable to retrieve the Floodgate's player. Most likely this is a Floodgate error.", output);
         }

         return null;
      }
   }

   static {
      if (value == null) {
         throw new IllegalArgumentException("Cannot find the class FloodgatePlayerImpl");
      }

      field = LoginCheckpoint.loadField(value, "ip");
      if (field == null) {
         throw new IllegalArgumentException("Cannot find the field FloodgatePlayerImpl#ip");
      }
   }

   public int findCount() {
      return this.fetchMessage().length();
   }
}

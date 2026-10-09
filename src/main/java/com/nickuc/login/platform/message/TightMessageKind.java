package com.nickuc.login.platform.message;

import com.nickuc.login.auth.login.BusyLoginProcessor;
import com.nickuc.login.auth.login.LiveLoginCheckpoint;
import com.nickuc.login.auth.login.RemoteLoginBarrier;
import com.nickuc.login.auth.login.SecureLoginGate;
import com.nickuc.login.bukkit.BukkitPlatform;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.config.PasswordHashLoader;
import com.nickuc.login.model.StrictPlatformCatalog;
import com.nickuc.login.i18n.LocaleBundle;
import com.nickuc.login.premium.Pbkdf2Linker;
import com.nickuc.login.security.hashing.VerifiedPasswordHashHasher;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.spawn.SpawnState;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;

public enum TightMessageKind implements InternalListenerContract {
   TIGHT_MESSAGE_KIND("check-ack-message", true);

   public static final int count = 4;
   private static final SecureLoginGate secureLoginGate = new SecureLoginGate("proxy-config", values().length);
   private final BusyLoginProcessor busyLoginProcessor;
   private final Object object;

   @Override
   public int fetchCount() {
      return this.ordinal();
   }

   TightMessageKind(String output, Object context) {
      this.busyLoginProcessor = BusyLoginProcessor.handleBusyLoginProcessor(output);
      this.object = context;
   }

   public static boolean loadState() {
      return "messages_br.yml".equals(SpawnState.SPAWN_STATE.a(new Object[0]));
   }

   @Override
   public Object getObject() {
      return this.object;
   }

   public static boolean canState(BukkitPlatform instance, boolean target) {
      LiveLoginCheckpoint input = new LiveLoginCheckpoint();
      PasswordHashLoader output = instance.a();
      if (!output.resolveStateForState()) {
         output.hasStateForState("com/nickuc/login/config/proxy/proxymode.yml");
      } else if (!target && !output.loadState()) {
         return false;
      }

      if (!output.resolveState()) {
         return false;
      }

      VerifiedPasswordHashHasher.performValues(values(), secureLoginGate, output);
      savePasswordStore(instance.fetchPasswordStore());
      PasswordHashContainer.dispatchMessage("All settings were loaded in " + input.loadTime() + " ms.");
      return true;
   }

   private static void savePasswordStore(PasswordStore instance) {
      File target = new File(instance.resolveFile(), "settings.data");
      if (!target.exists()) {
         StrictPlatformCatalog element = LocaleBundle.loadStrictPlatformCatalog();
         if (element != null) {
            String content = element.getMessage();
            if (element == StrictPlatformCatalog.CURRENT_STRICTPLATFORMCATALOG) {
               content = "br";
            }

            Pbkdf2Linker.handleSpawnState(SpawnState.SPAWN_STATE, "messages_" + content + ".yml");
         }
      } else {
         try {
            label84: {
               InputStream input = Files.newInputStream(target.toPath());

               label77: {
                  try {
                     DataInputStream output = new DataInputStream(input);
                     if (output.readUnsignedShort() != 4) {
                        break label77;
                     }

                     int context = output.readUnsignedShort();

                     for (int data = 0; data < context; data++) {
                        String value = output.readUTF();
                        SpawnState result = Arrays.stream(SpawnState.values())
                           .filter(targetValue -> value.equals(targetValue.busyLoginProcessor.fetchNames()[0]))
                           .findFirst()
                           .orElse(null);
                        if (result != null) {
                           switch (output.readByte()) {
                              case 0:
                                 VerifiedPasswordHashHasher.executeInternalListenerContract(result, SpawnState.secureLoginGate, output.readUTF());
                                 break;
                              case 1:
                                 VerifiedPasswordHashHasher.executeInternalListenerContract(result, SpawnState.secureLoginGate, output.readBoolean());
                                 break;
                              case 2:
                                 VerifiedPasswordHashHasher.executeInternalListenerContract(result, SpawnState.secureLoginGate, output.readInt());
                                 break;
                              case 3:
                                 int request = output.readInt();
                                 String[] response = new String[request];

                                 for (int source = 0; source < request; source++) {
                                    response[source] = output.readUTF();
                                 }

                                 RemoteLoginBarrier payload = RemoteLoginBarrier.loadRemoteLoginBarrier(response);
                                 VerifiedPasswordHashHasher.executeInternalListenerContract(result, SpawnState.secureLoginGate, payload);
                           }
                        }
                     }
                  } catch (Throwable record) {
                     if (input != null) {
                        try {
                           input.close();
                        } catch (Throwable entry) {
                           record.addSuppressed(entry);
                        }
                     }

                     throw record;
                  }

                  if (input != null) {
                     input.close();
                  }
                  break label84;
               }

               if (input != null) {
                  input.close();
               }

               return;
            }
         } catch (IOException item) {
            PasswordHashContainer.handleMessage("Unable to read settings clone from proxy server! version = %s", item, 4);
         }
      }

      Pbkdf2Linker.savePasswordStore(instance, true);
      Pbkdf2Linker.performPasswordStore(instance, true);
   }

   @Override
   public SecureLoginGate loadSecureLoginGate() {
      return secureLoginGate;
   }

   @Override
   public BusyLoginProcessor retrieveBusyLoginProcessor() {
      return this.busyLoginProcessor;
   }
}

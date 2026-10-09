package com.nickuc.login.storage.backup;

import com.nickuc.login.auth.login.ChainedLoginFlow;
import com.nickuc.login.auth.locale.LocaleFlow;
import com.nickuc.login.auth.message.MessageProcessor;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.proxy.QuickProxyState;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class BackupDao {
   private static final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
   private static float factor = Float.intBitsToFloat(1097859072);
   private static float activeFactor = Float.intBitsToFloat(1077936128);

   public static File createFile(PasswordStore instance, @Nullable OutgoingSenderAdapter target, boolean input) {
      if (atomicBoolean.getAndSet(true)) {
         if (target != null) {
            CachedSettingsGateway.handleOutgoingSenderAdapter(
               target,
               input
                  ? "§cJá existe um backup em andamento. Aguarde um pouco para realizar outro."
                  : "§cA backup is already in progress. Wait a while to perform another one."
            );
         }

         return null;
      } else {
         if (target != null) {
            CachedSettingsGateway.handleOutgoingSenderAdapter(target, input ? "§aIniciando backup dos arquivos..." : "§aCreating root folder backup...");
         }

         try {
            LocaleFlow output = new LocaleFlow();
            File context = new File(instance.resolveFile(), "backup");
            if (!context.exists() && !context.mkdirs()) {
               throw new IOException("Unable to create " + context + " folder");
            }

            String data = String.format("%s.%s.%s", output.findMessage(), output.resolveMessage(), output.retrieveMessage())
               + "-"
               + output.fetchMessage()
               + "h"
               + output.getMessageForMessage();
            File value = MessageProcessor.buildFile(new File(context, data + "-0.zip"), data + "-%d.zip");
            List result = Arrays.stream(Objects.requireNonNull(instance.resolveFile().listFiles())).filter(targetValue -> !targetValue.equals(context)).collect(Collectors.toList());
            if (result.isEmpty()) {
               return null;
            }

            ChainedLoginFlow.dispatchCollection(result, value);
            if (target != null) {
               CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.LIVE_QUICKPROXYSTATE, factor, activeFactor);
               CachedSettingsGateway.handleOutgoingSenderAdapter(target, input ? "§aBackup realizado com sucesso." : "§aBackup performed successfully.");
            }

            return value;
         } catch (Exception record) {
            PasswordHashContainer.handleMessage("Failed to backup the plugin folder", record);
            if (target != null) {
               CachedSettingsGateway.handleOutgoingSenderAdapter(target, input ? "§cBackup não pôde ser realizado." : "§cBackup could not be performed.");
            }

            return null;
         } finally {
            atomicBoolean.set(false);
         }
      }
   }
}

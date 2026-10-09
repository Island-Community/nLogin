package com.nickuc.login.storage.floodgate;

import com.nickuc.login.account.InternalLoginOption;
import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.model.TightPlatformCatalog;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.ReadyBedrockResolver;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.session.LimboCoordinator;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.CustomForm.Builder;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

public class FloodgateTable {
   private final ReadyBedrockResolver readyBedrockResolver;
   private final PasswordStore passwordStore;

   @Nullable
   private String resolveMessage(@Nullable String target) {
      if (target == null) {
         return null;
      }

      target = target.trim();
      return !target.isEmpty() ? target.replace(' ', '_') : null;
   }

   public FloodgateTable(PasswordStore target, ReadyBedrockResolver input) {
      this.passwordStore = target;
      this.readyBedrockResolver = input;
   }

   public boolean validateState(VerifiedServerAdapter target, LimboCoordinator input, SpawnLookup output) {
      FloodgatePlayer context = this.readyBedrockResolver.loadFloodgatePlayer(target.getUniqueId());
      if (context == null) {
         return false;
      }

      Builder data = (Builder)CustomForm.builder().title(CachedSettingsGateway.computeMessage(LoudProxyState.PRIMARY_DIRECT_LOUDPROXYSTATE, target));
      int value = -1;
      int result = -1;

      for (String source : CachedSettingsGateway.computeCollection(LoudProxyState.PRIMARY_LINKED_LOUDPROXYSTATE, target)) {
         value++;
         if (source.length() > 5 && source.charAt(0) == '[' && source.charAt(source.length() - 1) == ']') {
            String entry = source.substring(1, source.length() - 1);
            if (entry.contains("//")) {
               String[] record = entry.split("//");
               if (record.length == 2) {
                  data.input(record[0], record[1]);
                  result = value;
                  continue;
               }
            }
         }

         data.label(source);
      }

      if (result == -1) {
         return false;
      }

      data.closedOrInvalidResultHandler(() -> this.processVerifiedServerAdapter(target, input, output));
      data.validResultHandler(
         contextValue -> {
            String dataValue = this.resolveMessage((String)contextValue.next());
            if (dataValue == null) {
               this.processVerifiedServerAdapter(target, input, output);
            } else {
               InternalLoginOption.PRIMARY_INTERNALLOGINOPTION.performVerifiedServerAdapter(target, input, dataValue);
               target.getLinkedSessionHandler()
                  .loadStrictCommandHandler(
                     () -> {
                        if (target.loadState()
                           && this.passwordStore.loadLimboRegistry().loadLimboCoordinator(target).loadTightPlatformCatalog()
                              == TightPlatformCatalog.CURRENT_TIGHTPLATFORMCATALOG) {
                           this.processVerifiedServerAdapter(target, input, output);
                        }
                     },
                     5L,
                     TimeUnit.SECONDS
                  );
            }
         }
      );
      return context.sendForm(data.build());
   }

   public boolean verifyState(VerifiedServerAdapter target, LimboCoordinator input, SpawnLookup output) {
      FloodgatePlayer context = this.readyBedrockResolver.loadFloodgatePlayer(target.getUniqueId());
      if (context == null) {
         return false;
      }

      Builder data = (Builder)CustomForm.builder().title(CachedSettingsGateway.computeMessage(LoudProxyState.PRIMARY_ROOT_LOUDPROXYSTATE, target));
      int value = -1;
      int[] result = new int[]{-1, -1};

      for (String source : CachedSettingsGateway.computeCollection(LoudProxyState.PRIMARY_TOP_LOUDPROXYSTATE, target)) {
         value++;
         if (source.length() > 5 && source.charAt(0) == '[' && source.charAt(source.length() - 1) == ']') {
            String entry = source.substring(1, source.length() - 1);
            if (entry.contains("//")) {
               String[] record = entry.split("//");
               if (record.length == 2) {
                  data.input(record[0], record[1]);
                  if (result[0] == -1) {
                     result[0] = value;
                  } else {
                     result[1] = value;
                  }
                  continue;
               }
            }
         }

         data.label(source);
      }

      int item = result[0] != -1 ? 1 : 0;
      if (item == 0) {
         return false;
      }

      int element = result[1] != -1 ? 1 : 0;
      data.closedOrInvalidResultHandler(() -> this.processVerifiedServerAdapter(target, input, output));
      data.validResultHandler(
         dataValue -> {
            String valueValue = this.resolveMessage((String)dataValue.next());
            if (valueValue == null) {
               this.processVerifiedServerAdapter(target, input, output);
            } else {
               String resultValue;
               if (element) {
                  resultValue = this.resolveMessage((String)dataValue.next());
                  if (resultValue == null) {
                     this.processVerifiedServerAdapter(target, input, output);
                     return;
                  }
               } else {
                  resultValue = valueValue;
               }

               InternalLoginOption.CACHED_INTERNALLOGINOPTION.performVerifiedServerAdapter(target, input, valueValue, resultValue);
               target.getLinkedSessionHandler()
                  .loadStrictCommandHandler(
                     () -> {
                        if (target.loadState()
                           && this.passwordStore.loadLimboRegistry().loadLimboCoordinator(target).loadTightPlatformCatalog()
                              == TightPlatformCatalog.CURRENT_TIGHTPLATFORMCATALOG) {
                           this.processVerifiedServerAdapter(target, input, output);
                        }
                     },
                     5L,
                     TimeUnit.SECONDS
                  );
            }
         }
      );
      return context.sendForm(data.build());
   }

   private void processVerifiedServerAdapter(VerifiedServerAdapter target, LimboCoordinator input, SpawnLookup output) {
      if (target.loadState()) {
         target.getLinkedSessionHandler()
            .loadStrictCommandHandler(
               () -> {
                  if (this.passwordStore.loadLimboRegistry().loadLimboCoordinator(target).loadTightPlatformCatalog()
                     == TightPlatformCatalog.CURRENT_TIGHTPLATFORMCATALOG) {
                     if (output.findStateForState()) {
                        this.verifyState(target, input, output);
                     } else {
                        this.validateState(target, input, output);
                     }
                  }
               },
               3L,
               TimeUnit.SECONDS
            );
      }
   }
}

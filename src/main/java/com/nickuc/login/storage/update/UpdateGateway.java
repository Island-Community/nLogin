package com.nickuc.login.storage.update;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.auth.login.PrimaryLoginHandler;
import com.nickuc.login.auth.login.PrivateLoginHandler;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.premium.IndirectPasswordResolver;
import com.nickuc.login.premium.SpawnLookup;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collections;

public class UpdateGateway extends PasswordHashRepository {
   private final DirectNoticeCatalog directNoticeCatalog;
   private final DirectNoticeCatalog activeDirectNoticeCatalog;

   private boolean validateState(DirectNoticeCatalog target) {
      return (target == DirectNoticeCatalog.ACTIVE_DIRECTNOTICECATALOG || target == DirectNoticeCatalog.DIRECT_NOTICE_CATALOG)
         && (
            this.activeDirectNoticeCatalog == DirectNoticeCatalog.ACTIVE_DIRECTNOTICECATALOG
               || this.activeDirectNoticeCatalog == DirectNoticeCatalog.DIRECT_NOTICE_CATALOG
         );
   }

   @Override
   public void handleOutgoingSenderAdapter(OutgoingSenderAdapter target) {
      DirectNoticeCatalog input = this.passwordStore.fetchLocalSettingsRepository().loadSharedListenerContract().resolveDirectNoticeCatalog();
      if (input != this.activeDirectNoticeCatalog && !this.validateState(input)) {
         throw new IllegalStateException("Current backend type does not match with target backend!");
      }

      this.sharedListenerContract = LocalSettingsRepository.processSharedListenerContract(this.passwordStore, this.directNoticeCatalog, false);
      String output = SpawnState.CURRENT_SPAWNSTATE.a(new Object[0]);
      this.processMessage(output);

      try {
         IndirectPasswordResolver context = this.passwordStore.findIndirectPasswordResolver();
         PrimaryLoginHandler data = this.sharedListenerContract
            .buildPrimaryLoginHandler("SELECT * FROM `" + SpawnState.CURRENT_SPAWNSTATE.a(new Object[0]) + "`");

         try {
            ResultSet value = data.resolveObject();

            while (value.next() && this.passwordStore.resolveState()) {
               String result = null;

               try {
                  SpawnLookup request = context.processSpawnLookup(value);
                  if (request == null) {
                     throw new Exception("Failed to build account. (" + this.directNoticeCatalog + ")");
                  }

                  result = request.retrieveMessage();
                  SpawnLookup response = context.computeSpawnLookup(request.retrieveMessage(), request.getMojangId(), request.getBedrockId(), false);
                  if (response == null) {
                     throw new Exception("Failed to build " + result + "'s account. (" + this.activeDirectNoticeCatalog + ")");
                  }

                  if (!response.fetchState()) {
                     if (!context.checkState(this.loginBarrier, request)) {
                        throw new Exception("Failed to update " + result + "'s account. (" + this.activeDirectNoticeCatalog + ")");
                     }

                     this.activeTimestamp++;
                  }
               } catch (Exception secondaryData) {
                  PasswordHashContainer.processMessage(
                     "[" + this.messageOption.getName() + "] " + (result == null ? "Unknown" : result + "'s") + " data could not be converted.", secondaryData
                  );
               } finally {
                  this.pendingTimestamp++;
               }
            }
         } catch (Throwable secondaryResult) {
            if (data != null) {
               try {
                  data.close();
               } catch (Throwable primaryData) {
                  secondaryResult.addSuppressed(primaryData);
               }
            }

            throw secondaryResult;
         }

         if (data != null) {
            data.close();
         }

         PrivateLoginHandler localInput = this.sharedListenerContract.getPrivateLoginHandler();

         try {
            Connection localOutput = localInput.retrieveConnection();
            PreparedStatement localContext = localOutput.prepareStatement("SELECT * FROM `" + SpawnState.INCOMING_SPAWNSTATE.a(new Object[0]) + "`");

            try {
               ResultSet localData = localContext.executeQuery();

               try {
                  while (localData.next() && this.passwordStore.resolveState()) {
                     String localValue = localData.getString(SpawnState.SECONDARY_SPAWNSTATE.a(new Object[0]));
                     byte[] source = localData.getBytes(SpawnState.DIRECT_SPAWNSTATE.a(new Object[0]));

                     try {
                        PreparedStatement entry = localOutput.prepareStatement(
                           String.format(
                              "UPDATE `%s` SET `%s` = ? WHERE `%s` = ?",
                              SpawnState.INCOMING_SPAWNSTATE.a(new Object[0]),
                              SpawnState.DIRECT_SPAWNSTATE.a(new Object[0]),
                              SpawnState.SECONDARY_SPAWNSTATE.a(new Object[0])
                           )
                        );

                        try {
                           entry.setBytes(1, source);
                           entry.setString(2, localValue);
                           int record = entry.executeUpdate();
                           if (record == 0) {
                              PreparedStatement item = localOutput.prepareStatement(
                                 String.format(
                                    "INSERT INTO `%s` (`%s`, `%s`) VALUES (?, ?)",
                                    SpawnState.INCOMING_SPAWNSTATE.a(new Object[0]),
                                    SpawnState.SECONDARY_SPAWNSTATE.a(new Object[0]),
                                    SpawnState.DIRECT_SPAWNSTATE.a(new Object[0])
                                 )
                              );

                              try {
                                 item.setString(1, localValue);
                                 item.setBytes(2, source);
                                 item.execute();
                              } catch (Throwable primaryValue) {
                                 if (item != null) {
                                    try {
                                       item.close();
                                    } catch (Throwable primaryContext) {
                                       primaryValue.addSuppressed(primaryContext);
                                    }
                                 }

                                 throw primaryValue;
                              }

                              if (item != null) {
                                 item.close();
                              }
                           }
                        } catch (Throwable primaryResult) {
                           if (entry != null) {
                              try {
                                 entry.close();
                              } catch (Throwable primaryOutput) {
                                 primaryResult.addSuppressed(primaryOutput);
                              }
                           }

                           throw primaryResult;
                        }

                        if (entry != null) {
                           entry.close();
                        }
                     } catch (Exception secondaryTarget) {
                        throw new RuntimeException("Unable to update or insert data with key \"" + localValue + "\"!", secondaryTarget);
                     }
                  }
               } catch (Throwable secondaryInput) {
                  if (localData != null) {
                     try {
                        localData.close();
                     } catch (Throwable primaryInput) {
                        secondaryInput.addSuppressed(primaryInput);
                     }
                  }

                  throw secondaryInput;
               }

               if (localData != null) {
                  localData.close();
               }
            } catch (Throwable secondaryOutput) {
               if (localContext != null) {
                  try {
                     localContext.close();
                  } catch (Throwable primaryTarget) {
                     secondaryOutput.addSuppressed(primaryTarget);
                  }
               }

               throw secondaryOutput;
            }

            if (localContext != null) {
               localContext.close();
            }
         } finally {
            if (Collections.singletonList(localInput).get(0) != null) {
               localInput.close();
            }
         }
      } finally {
         this.sharedListenerContract.executeTask();
      }

      this.sendOutgoingSenderAdapter(target);
   }

   public UpdateGateway(PasswordStore target, MessageOption input, DirectNoticeCatalog output, DirectNoticeCatalog context) {
      super(target, input);
      this.directNoticeCatalog = output;
      this.activeDirectNoticeCatalog = context;
   }
}

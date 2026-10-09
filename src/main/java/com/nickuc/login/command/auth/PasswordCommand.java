package com.nickuc.login.command.auth;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.QuickPremiumOption;
import com.nickuc.login.api.enums.event.EventEnum;
import com.nickuc.login.api.enums.event.UpdatePasswordSource;
import com.nickuc.login.auth.login.LiveLoginCheckpoint;
import com.nickuc.login.auth.locale.OpenLocaleBarrier;
import com.nickuc.login.auth.login.PrimaryLoginHandler;
import com.nickuc.login.auth.login.PrivateLoginCheckpoint;
import com.nickuc.login.auth.login.StrictLoginHandler;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.OutgoingSpawnState;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.proxy.QuickProxyState;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.IndirectPasswordResolver;
import com.nickuc.login.premium.Pbkdf2Linker;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.login.LoginSource;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.spawn.SpawnState;
import java.sql.ResultSet;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class PasswordCommand extends LoginSource {
   private static double ratio = Double.longBitsToDouble(4636737291354636288L);
   private final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
   private long timestamp;
   private static double activeRatio = Double.longBitsToDouble(4636737291354636288L);
   private static double pendingRatio = Double.longBitsToDouble(4636737291354636288L);

   public PasswordCommand(PasswordStore target) {
      super(target, "purge", "nlogin.admin", true, false);
   }

   @Override
   public void processOutgoingSenderAdapter(OutgoingSenderAdapter target, String[] input) {
      if (target instanceof VerifiedServerAdapter) {
         CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.FAST_LOUDPROXYSTATE);
      } else if (input.length != 2 && input.length != 3) {
         CachedSettingsGateway.executeOutgoingSenderAdapter(
            target, LoudProxyState.LINKED_LOUDPROXYSTATE, "/nlogin " + this.findMessage().toLowerCase(Locale.ENGLISH) + " <days>"
         );
      } else if (input.length == 2 && input[1].equalsIgnoreCase("stop")) {
         if (!this.atomicBoolean.get()) {
            CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§cNo purge process is running.");
         } else {
            CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§cPurge process interrupted.");
            this.atomicBoolean.set(false);
         }
      } else if (this.atomicBoolean.get()) {
         CachedSettingsGateway.handleOutgoingSenderAdapter(
            target, "§cA purge process is already running. Run §f\"nlogin " + this.findMessage().toLowerCase(Locale.ENGLISH) + " stop\" §cto interrupt."
         );
      } else {
         Integer output = PrivateLoginCheckpoint.createInteger(input[1]);
         if (output != null && output > 0) {
            if (output <= 7) {
               CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§cThe number of days must be greater than 7 days.");
            } else {
               long context = System.currentTimeMillis();
               if (context - this.timestamp >= 60000L) {
                  CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§4CAUTION! §cActions performed by this operation are irreversible.");
                  CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§cThis operation will only remove the users password §b(UUIDs will not be lost!)§c.");
                  CachedSettingsGateway.handleOutgoingSenderAdapter(
                     target, "§cIf you want to permanently delete the accounts, add the §f--delete §coption to the command:"
                  );
                  CachedSettingsGateway.handleOutgoingSenderAdapter(
                     target, "§f\"nlogin " + this.findMessage().toLowerCase(Locale.ENGLISH) + " " + input[1] + " --delete\""
                  );
                  CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
                  CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§eTo proceed with the execution, please repeat this command again.");
                  this.timestamp = context;
               } else {
                  int value = input.length == 3 && "--delete".equals(input[2]) ? 1 : 0;
                  long result = System.currentTimeMillis() - output * 86400 * 1000;

                  try {
                     if (this.atomicBoolean.getAndSet(true)) {
                        CachedSettingsGateway.handleOutgoingSenderAdapter(
                           target,
                           "§cA purge process is already running. Run §f\"nlogin " + this.findMessage().toLowerCase(Locale.ENGLISH) + " stop\" §cto interrupt."
                        );
                        return;
                     }

                     PrimaryLoginHandler job;
                     label471: {
                        LiveLoginCheckpoint response = new LiveLoginCheckpoint();
                        long source = 0L;
                        if (value != 0) {
                           StrictLoginHandler record = this.passwordStore
                              .fetchLocalSettingsRepository()
                              .loadSharedListenerContract()
                              .computeStrictLoginHandler(
                                 String.format(
                                    "DELETE FROM `%s` WHERE `%s` > 0 AND `%s` < ?",
                                    SpawnState.CURRENT_SPAWNSTATE.a(new Object[0]),
                                    OutgoingSpawnState.REMOTE_OUTGOINGSPAWNSTATE.getName(),
                                    OutgoingSpawnState.REMOTE_OUTGOINGSPAWNSTATE.getName()
                                 ),
                                 result
                              );
                           source = ((Integer)record.resolveObject()).intValue();
                        } else {
                           IndirectPasswordResolver element = this.passwordStore.findIndirectPasswordResolver();
                           job = this.passwordStore
                              .fetchLocalSettingsRepository()
                              .loadSharedListenerContract()
                              .buildPrimaryLoginHandler(
                                 String.format(
                                    "SELECT COUNT(*) FROM `%s` WHERE `%s` IS NOT NULL AND `%s` > 0 AND `%s` < ?",
                                    SpawnState.CURRENT_SPAWNSTATE.a(new Object[0]),
                                    OutgoingSpawnState.MAIN_OUTGOINGSPAWNSTATE.getName(),
                                    OutgoingSpawnState.REMOTE_OUTGOINGSPAWNSTATE.getName(),
                                    OutgoingSpawnState.REMOTE_OUTGOINGSPAWNSTATE.getName()
                                 ),
                                 result
                              );

                           long task;
                           try {
                              ResultSet payload = job.resolveObject();
                              task = payload.next() ? payload.getLong(1) : 0L;
                           } catch (Throwable position) {
                              if (job != null) {
                                 try {
                                    job.close();
                                 } catch (Throwable backend) {
                                    position.addSuppressed(backend);
                                 }
                              }

                              throw position;
                           }

                           if (job != null) {
                              job.close();
                           }

                           if (task > 0L) {
                              CachedSettingsGateway.handleOutgoingSenderAdapter(
                                 target,
                                 "§aPurge progress: §f%s §7of §f%s §7purged §a(%s)",
                                 OpenLocaleBarrier.resolveMessage(source),
                                 OpenLocaleBarrier.resolveMessage(task),
                                 OpenLocaleBarrier.resolveMessage((double)source / task * ratio, 2) + "%"
                              );
                              job = this.passwordStore
                                 .fetchLocalSettingsRepository()
                                 .loadSharedListenerContract()
                                 .buildPrimaryLoginHandler(
                                    String.format(
                                       "SELECT * FROM `%s` WHERE `%s` IS NOT NULL AND `%s` > 0 AND `%s` < ?",
                                       SpawnState.CURRENT_SPAWNSTATE.a(new Object[0]),
                                       OutgoingSpawnState.MAIN_OUTGOINGSPAWNSTATE.getName(),
                                       OutgoingSpawnState.REMOTE_OUTGOINGSPAWNSTATE.getName(),
                                       OutgoingSpawnState.REMOTE_OUTGOINGSPAWNSTATE.getName()
                                    ),
                                    result
                                 );

                              try {
                                 ResultSet future = job.resolveObject();

                                 while (this.atomicBoolean.get() && future.next()) {
                                    SpawnLookup holder = element.processSpawnLookup(future);
                                    if (holder == null || !element.canState(holder)) {
                                       CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.DIRECT_LOUDPROXYSTATE);
                                       CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
                                       break label471;
                                    }

                                    String reference = holder.retrieveMessage();
                                    String subject = QuickPremiumOption.CURRENT_QUICKPREMIUMOPTION.retrieveState()
                                       ? Pbkdf2Linker.handleMessage(reference, holder.fetchStateAndState())
                                       : reference;
                                    PasswordHashContainer.dispatchMessage("The account of " + subject + " player was unregistered via purge command");
                                    VerifiedServerAdapter option = this.passwordStore.b().resolveVerifiedServerAdapter(subject);
                                    UUID setting = option != null ? option.getUniqueId() : holder.getUniqueId();
                                    boolean client = false /* VF: Semaphore variable */;

                                    try {
                                       client = true;
                                       this.passwordStore
                                          .verifyState(EventEnum.PASSWORD_UPDATE_EVENT, option, setting, subject, null, UpdatePasswordSource.BY_ADMIN);
                                       client = false;
                                    } finally {
                                       if (client) {
                                          if (option != null) {
                                             option.buildCompletableFuture(CachedSettingsGateway.computeMessage(LoudProxyState.PRIVATE_LOUDPROXYSTATE, option));
                                          }

                                          if (++source % 100L == 0L) {
                                             CachedSettingsGateway.handleOutgoingSenderAdapter(
                                                target,
                                                "§aPurge progress: §f%s §7of §f%s §7purged §a(%s)",
                                                OpenLocaleBarrier.resolveMessage(source),
                                                OpenLocaleBarrier.resolveMessage(task),
                                                OpenLocaleBarrier.resolveMessage((double)source / task * pendingRatio, 2) + "%"
                                             );
                                          }
                                       }
                                    }

                                    if (option != null) {
                                       option.buildCompletableFuture(CachedSettingsGateway.computeMessage(LoudProxyState.PRIVATE_LOUDPROXYSTATE, option));
                                    }

                                    if (++source % 100L == 0L) {
                                       CachedSettingsGateway.handleOutgoingSenderAdapter(
                                          target,
                                          "§aPurge progress: §f%s §7of §f%s §7purged §a(%s)",
                                          OpenLocaleBarrier.resolveMessage(source),
                                          OpenLocaleBarrier.resolveMessage(task),
                                          OpenLocaleBarrier.resolveMessage((double)source / task * activeRatio, 2) + "%"
                                       );
                                    }
                                 }
                              } catch (Throwable spawn) {
                                 if (job != null) {
                                    try {
                                       job.close();
                                    } catch (Throwable proxy) {
                                       spawn.addSuppressed(proxy);
                                    }
                                 }

                                 throw spawn;
                              }

                              if (job != null) {
                                 job.close();
                              }
                           }
                        }

                        CachedSettingsGateway.handleOutgoingSenderAdapter(
                           target, "§a✔ " + source + " account" + (source > 1L ? "s" : "") + " were successfully purged."
                        );
                        CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
                        CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§e⚑ This operation took §f" + response.loadMessage(TimeUnit.SECONDS, 2) + "s§e.");
                        return;
                     }

                     if (job != null) {
                        job.close();
                     }
                  } catch (Exception state) {
                     PasswordHashContainer.handleMessage("Unable to delete accounts older than %s days%s", state, output, value != 0 ? " (deleting accounts)" : "");
                     CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.DIRECT_LOUDPROXYSTATE);
                     CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
                     return;
                  } finally {
                     this.atomicBoolean.set(false);
                  }
               }
            }
         } else {
            CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§cInvalid or not positive number of days! " + input[1]);
         }
      }
   }
}

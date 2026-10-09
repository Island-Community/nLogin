package com.nickuc.login.storage.settings;

import com.nickuc.login.auth.message.FastMessageHandler;
import com.nickuc.login.auth.login.InternalLoginHandler;
import com.nickuc.login.auth.login.LowLoginService;
import com.nickuc.login.auth.login.PrimaryLoginHandler;
import com.nickuc.login.auth.update.UpdateBarrier;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.config.PasswordHashLoader;
import com.nickuc.login.model.OutgoingSpawnState;
import com.nickuc.login.platform.listener.SharedListenerContract;
import com.nickuc.login.premium.Pbkdf2Linker;
import com.nickuc.login.protocol.PasswordHashAdapter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


public class LocalSettingsRepository {
   private final PasswordStore passwordStore;
   public static final int count = 1;
   private final SharedListenerContract sharedListenerContract;

   public void executeTask() {
      try {
         PasswordHashRepository.performTask();
      } catch (Exception output) {
         PasswordHashContainer.handleMessage("Unable to stop data conversion", output);
      }

      try {
         this.sharedListenerContract.executeTask();
      } catch (SQLException input) {
         PasswordHashContainer.handleMessage("Unable to shutdown nLogin database", input);
      }
   }

   public boolean loadState() {
      return this.passwordStore.a().loadPrimaryPasswordHashVerifier().hasState("first-run", true);
   }

   public boolean findState() {
      return this.passwordStore.a().loadPrimaryPasswordHashVerifier().hasState("config-reset", true);
   }

   public void sendTask() {
      this.handleState(true);
   }

   public PasswordStore retrievePasswordStore() {
      return this.passwordStore;
   }

   public void sendCount(int target) {
      Integer input = this.loadInteger();
      if (input == null) {
         LowLoginService output = this.sharedListenerContract
            .createLowLoginService(
               String.format(
                  "INSERT INTO `%s` (`%s`, `%s`) VALUES (?, ?)",
                  SpawnState.INCOMING_SPAWNSTATE.a(new Object[0]),
                  SpawnState.SECONDARY_SPAWNSTATE.a(new Object[0]),
                  SpawnState.DIRECT_SPAWNSTATE.a(new Object[0])
               )
            );

         try {
            byte[] context = FastMessageHandler.buildPayload(targetValue -> targetValue.dispatchCount(target));
            PreparedStatement data = output.resolveObject();
            data.setString(1, "database_version");
            data.setBytes(2, context);
            data.execute();
         } catch (Throwable response) {
            if (output != null) {
               try {
                  output.close();
               } catch (Throwable result) {
                  response.addSuppressed(result);
               }
            }

            throw response;
         }

         if (output != null) {
            output.close();
         }
      } else if (input != target) {
         LowLoginService source = this.sharedListenerContract
            .createLowLoginService(
               String.format(
                  "UPDATE `%s` SET `%s` = ? WHERE `%s` = ?",
                  SpawnState.INCOMING_SPAWNSTATE.a(new Object[0]),
                  SpawnState.SECONDARY_SPAWNSTATE.a(new Object[0]),
                  SpawnState.DIRECT_SPAWNSTATE.a(new Object[0])
               )
            );

         try {
            byte[] entry = FastMessageHandler.buildPayload(targetValue -> targetValue.dispatchCount(target));
            PreparedStatement record = source.resolveObject();
            record.setBytes(1, entry);
            record.setString(2, "database_version");
            record.execute();
         } catch (Throwable request) {
            if (source != null) {
               try {
                  source.close();
               } catch (Throwable value) {
                  request.addSuppressed(value);
               }
            }

            throw request;
         }

         if (source != null) {
            source.close();
         }
      }
   }

   public static SharedListenerContract processSharedListenerContract(PasswordStore instance, DirectNoticeCatalog target, boolean input) {
      switch (target) {
         case ACTIVE_DIRECTNOTICECATALOG:
         case DIRECT_NOTICE_CATALOG:
            PasswordHashLoader data = instance.a();
            PasswordRepository context = LocalPasswordTable.buildPasswordRepository(data, target);
            if (input) {
               context.retrieveProperties().put("socketTimeout", "300000");
               context.retrieveProperties().put("connectTimeout", "300000");
               context.retrieveProperties().put("readTimeout", "300000");
            }

            return LocalPasswordTable.createBusySettingsArchive(instance, target, context, targetValue -> {
               targetValue.setMaximumPoolSize(data.a("database.pool-settings.maximum-pool-size", 10));
               targetValue.setMinimumIdle(data.a("database.pool-settings..minimum-idle", 10));
               targetValue.setMaxLifetime(data.a("database.pool-settings..maximum-lifetime", (int)TimeUnit.MINUTES.toMillis(30L)));
               targetValue.setConnectionTimeout(data.a("database.pool-settings..connection-timeout", (int)TimeUnit.SECONDS.toMillis(5L)));
            });
         case CURRENT_DIRECTNOTICECATALOG:
            File output = new File(instance.resolveFile(), "nlogin.db");
            return LoginGateway.handleLoginGateway(instance, output, new Properties());
         default:
            throw new IllegalArgumentException("Invalid database type! " + target);
      }
   }

   public SharedListenerContract loadSharedListenerContract() {
      return this.sharedListenerContract;
   }

   public long getTime() {
      String target = SpawnState.CURRENT_SPAWNSTATE.a(new Object[0]);

      try {
         PrimaryLoginHandler input = this.sharedListenerContract.buildPrimaryLoginHandler("SELECT COUNT(*) FROM `" + target + "`");

         long context;
         label53: {
            try {
               ResultSet output = input.resolveObject();
               if (output.next()) {
                  context = output.getLong(1);
                  break label53;
               }
            } catch (Throwable result) {
               if (input != null) {
                  try {
                     input.close();
                  } catch (Throwable value) {
                     result.addSuppressed(value);
                  }
               }

               throw result;
            }

            if (input != null) {
               input.close();
            }

            return 0L;
         }

         if (input != null) {
            input.close();
         }

         return context;
      } catch (Exception request) {
         PasswordHashContainer.handleMessage("Failed to retrieve registered count, table: " + target, request);
         return 0L;
      }
   }

   public void handleState(boolean target) {
      DirectNoticeCatalog input = this.sharedListenerContract.resolveDirectNoticeCatalog();

      try {
         Connection output = this.sharedListenerContract.resolveConnection();

         try {
            Statement context = output.createStatement();

            try {
               boolean data = LocalPasswordTable.canState(this.sharedListenerContract, SpawnState.CURRENT_SPAWNSTATE.a(new Object[0]));
               boolean value = LocalPasswordTable.canState(this.sharedListenerContract, SpawnState.INCOMING_SPAWNSTATE.a(new Object[0]));
               switch (input) {
                  case ACTIVE_DIRECTNOTICECATALOG:
                  case DIRECT_NOTICE_CATALOG:
                     if (!data) {
                        context.execute(
                           "CREATE TABLE `"
                              + SpawnState.CURRENT_SPAWNSTATE.a(new Object[0])
                              + "` (`"
                              + OutgoingSpawnState.OUTGOING_SPAWN_STATE.getName()
                              + "` INT NOT NULL AUTO_INCREMENT, `"
                              + OutgoingSpawnState.ACTIVE_OUTGOINGSPAWNSTATE.getName()
                              + "` VARCHAR(191) NOT NULL, `"
                              + OutgoingSpawnState.PENDING_OUTGOINGSPAWNSTATE.getName()
                              + "` VARCHAR(32)"
                              + (target ? " UNIQUE" : "")
                              + ", `"
                              + OutgoingSpawnState.CURRENT_OUTGOINGSPAWNSTATE.getName()
                              + "` VARCHAR(32) UNIQUE, `"
                              + OutgoingSpawnState.PRIMARY_OUTGOINGSPAWNSTATE.getName()
                              + "` VARCHAR(32) UNIQUE, `"
                              + OutgoingSpawnState.MAIN_OUTGOINGSPAWNSTATE.getName()
                              + "` VARCHAR(191), `"
                              + OutgoingSpawnState.LOCAL_OUTGOINGSPAWNSTATE.getName()
                              + "` VARCHAR(60) DEFAULT '127.0.0.1', `"
                              + OutgoingSpawnState.REMOTE_OUTGOINGSPAWNSTATE.getName()
                              + "` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, `"
                              + OutgoingSpawnState.CACHED_OUTGOINGSPAWNSTATE.getName()
                              + "` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, `"
                              + OutgoingSpawnState.STORED_OUTGOINGSPAWNSTATE.getName()
                              + "` VARCHAR(191), `"
                              + OutgoingSpawnState.VERIFIED_OUTGOINGSPAWNSTATE.getName()
                              + "` VARCHAR(191), `"
                              + OutgoingSpawnState.AUTHENTICATED_OUTGOINGSPAWNSTATE.getName()
                              + "` MEDIUMTEXT, PRIMARY KEY (`"
                              + OutgoingSpawnState.OUTGOING_SPAWN_STATE.getName()
                              + "`)) CHARACTER SET = utf8mb4;"
                        );
                        context.execute(
                           "CREATE INDEX "
                              + OutgoingSpawnState.ACTIVE_OUTGOINGSPAWNSTATE.getName()
                              + "_idx ON "
                              + SpawnState.CURRENT_SPAWNSTATE.a(new Object[0])
                              + " ("
                              + OutgoingSpawnState.ACTIVE_OUTGOINGSPAWNSTATE.getName()
                              + ")"
                        );
                        context.execute(
                           "CREATE INDEX "
                              + OutgoingSpawnState.LOCAL_OUTGOINGSPAWNSTATE.getName()
                              + "_idx ON "
                              + SpawnState.CURRENT_SPAWNSTATE.a(new Object[0])
                              + " ("
                              + OutgoingSpawnState.LOCAL_OUTGOINGSPAWNSTATE.getName()
                              + ")"
                        );
                     }

                     if (!value) {
                        context.execute(
                           "CREATE TABLE `"
                              + SpawnState.INCOMING_SPAWNSTATE.a(new Object[0])
                              + "` (`"
                              + SpawnState.OUTGOING_SPAWNSTATE.a(new Object[0])
                              + "` INT NOT NULL AUTO_INCREMENT, `"
                              + SpawnState.SECONDARY_SPAWNSTATE.a(new Object[0])
                              + "` VARCHAR(191) NOT NULL UNIQUE, `"
                              + SpawnState.DIRECT_SPAWNSTATE.a(new Object[0])
                              + "` MEDIUMBLOB NOT NULL, PRIMARY KEY (`"
                              + SpawnState.OUTGOING_SPAWNSTATE.a(new Object[0])
                              + "`)) CHARACTER SET = utf8mb4;"
                        );
                        this.sendCount(1);
                     }
                     break;
                  case CURRENT_DIRECTNOTICECATALOG:
                     if (!data) {
                        context.execute(
                           "CREATE TABLE "
                              + SpawnState.CURRENT_SPAWNSTATE.a(new Object[0])
                              + " ("
                              + OutgoingSpawnState.OUTGOING_SPAWN_STATE.getName()
                              + " INTEGER PRIMARY KEY, `"
                              + OutgoingSpawnState.ACTIVE_OUTGOINGSPAWNSTATE.getName()
                              + "` VARCHAR(255) NOT NULL, `"
                              + OutgoingSpawnState.PENDING_OUTGOINGSPAWNSTATE.getName()
                              + "` VARCHAR(32)"
                              + (target ? " UNIQUE" : "")
                              + ", `"
                              + OutgoingSpawnState.CURRENT_OUTGOINGSPAWNSTATE.getName()
                              + "` VARCHAR(32) UNIQUE, `"
                              + OutgoingSpawnState.PRIMARY_OUTGOINGSPAWNSTATE.getName()
                              + "` VARCHAR(32) UNIQUE, `"
                              + OutgoingSpawnState.MAIN_OUTGOINGSPAWNSTATE.getName()
                              + "` VARCHAR(255), `"
                              + OutgoingSpawnState.LOCAL_OUTGOINGSPAWNSTATE.getName()
                              + "` VARCHAR(60) DEFAULT '127.0.0.1', `"
                              + OutgoingSpawnState.REMOTE_OUTGOINGSPAWNSTATE.getName()
                              + "` TIMESTAMP, `"
                              + OutgoingSpawnState.CACHED_OUTGOINGSPAWNSTATE.getName()
                              + "` TIMESTAMP, `"
                              + OutgoingSpawnState.STORED_OUTGOINGSPAWNSTATE.getName()
                              + "` VARCHAR(255), `"
                              + OutgoingSpawnState.VERIFIED_OUTGOINGSPAWNSTATE.getName()
                              + "` VARCHAR(255), `"
                              + OutgoingSpawnState.AUTHENTICATED_OUTGOINGSPAWNSTATE.getName()
                              + "` TEXT)"
                        );
                        context.execute(
                           "CREATE INDEX "
                              + OutgoingSpawnState.ACTIVE_OUTGOINGSPAWNSTATE.getName()
                              + "_idx ON "
                              + SpawnState.CURRENT_SPAWNSTATE.a(new Object[0])
                              + " ("
                              + OutgoingSpawnState.ACTIVE_OUTGOINGSPAWNSTATE.getName()
                              + ")"
                        );
                        context.execute(
                           "CREATE INDEX "
                              + OutgoingSpawnState.LOCAL_OUTGOINGSPAWNSTATE.getName()
                              + "_idx ON "
                              + SpawnState.CURRENT_SPAWNSTATE.a(new Object[0])
                              + " ("
                              + OutgoingSpawnState.LOCAL_OUTGOINGSPAWNSTATE.getName()
                              + ")"
                        );
                     }

                     if (!value) {
                        context.execute(
                           "CREATE TABLE "
                              + SpawnState.INCOMING_SPAWNSTATE.a(new Object[0])
                              + " ("
                              + OutgoingSpawnState.OUTGOING_SPAWN_STATE.getName()
                              + " INTEGER PRIMARY KEY, `"
                              + SpawnState.SECONDARY_SPAWNSTATE.a(new Object[0])
                              + "` VARCHAR(255) NOT NULL UNIQUE, `"
                              + SpawnState.DIRECT_SPAWNSTATE.a(new Object[0])
                              + "` BLOB NOT NULL)"
                        );
                        this.sendCount(1);
                     }
                     break;
                  default:
                     throw new IllegalArgumentException("Invalid database type! " + input);
               }
            } catch (Throwable response) {
               if (context != null) {
                  try {
                     context.close();
                  } catch (Throwable request) {
                     response.addSuppressed(request);
                  }
               }

               throw response;
            }

            if (context != null) {
               context.close();
            }
         } catch (Throwable source) {
            if (output != null) {
               try {
                  output.close();
               } catch (Throwable result) {
                  source.addSuppressed(result);
               }
            }

            throw source;
         }

         if (output != null) {
            output.close();
         }
      } catch (Exception entry) {
         PasswordHashContainer.handleMessage("Unable to create database tables (using " + input.name() + ")", entry);
      }
   }

   public void handleTask() {
      this.passwordStore.a().loadPrimaryPasswordHashVerifier().createPrimaryPasswordHashVerifier("config-reset", false).sendTask();
   }

   public void updateTask() {
      try {
         InternalLoginHandler target = UpdateBarrier.computeInternalLoginHandler(this.sharedListenerContract);
         if (target != null) {
            Pbkdf2Linker.updateKeyPair(target.findKeyPair());
         }
      } catch (Exception input) {
         PasswordHashContainer.handleMessage("Unable to setup server uuid and server key pair!", input);
      }
   }

   public void executePasswordHashAdapter(PasswordHashAdapter target) {
      byte[] input;
      try {
         PrimaryLoginHandler output = this.sharedListenerContract
            .buildPrimaryLoginHandler(
               String.format(
                  "SELECT `%s` FROM `%s` WHERE `%s` = ? LIMIT 1",
                  SpawnState.DIRECT_SPAWNSTATE.a(new Object[0]),
                  SpawnState.INCOMING_SPAWNSTATE.a(new Object[0]),
                  SpawnState.SECONDARY_SPAWNSTATE.a(new Object[0])
               ),
               "pm_secret_key"
            );

         try {
            ResultSet context = output.resolveObject();
            if (!context.next()) {
               input = new byte[64];
               new SecureRandom().nextBytes(input);
               Connection data = this.sharedListenerContract.resolveConnection();

               try {
                  PreparedStatement value = data.prepareStatement(
                     String.format(
                        "INSERT INTO `%s` (`%s`, `%s`) VALUES (?, ?)",
                        SpawnState.INCOMING_SPAWNSTATE.a(new Object[0]),
                        SpawnState.SECONDARY_SPAWNSTATE.a(new Object[0]),
                        SpawnState.DIRECT_SPAWNSTATE.a(new Object[0])
                     )
                  );

                  try {
                     value.setString(1, "pm_secret_key");
                     value.setBytes(2, input);
                     value.execute();
                  } catch (Throwable subject) {
                     if (value != null) {
                        try {
                           value.close();
                        } catch (Throwable reference) {
                           subject.addSuppressed(reference);
                        }
                     }

                     throw subject;
                  }

                  if (value != null) {
                     value.close();
                  }
               } catch (Exception option) {
                  throw new IllegalArgumentException("Unable to generate pm secret key", option);
               } finally {
                  this.sharedListenerContract.sendConnection(data);
               }
            } else {
               input = context.getBytes(1);
               if (input == null || input.length == 0) {
                  throw new IllegalArgumentException("Invalid pm secret key stored!");
               }
            }
         } catch (Throwable property) {
            if (output != null) {
               try {
                  output.close();
               } catch (Throwable holder) {
                  property.addSuppressed(holder);
               }
            }

            throw property;
         }

         if (output != null) {
            output.close();
         }
      } catch (Exception attribute) {
         throw new IllegalArgumentException("Unable to retrieve pm secret key", attribute);
      }

      target.updatePayload(input);
   }

   public void processTask() {
      this.passwordStore.a().loadPrimaryPasswordHashVerifier().createPrimaryPasswordHashVerifier("first-run", false).sendTask();
   }

   public Integer loadInteger() {
      PrimaryLoginHandler target = this.sharedListenerContract
         .buildPrimaryLoginHandler(
            String.format(
               "SELECT `%s` FROM `%s` WHERE `%s` = ? LIMIT 1",
               SpawnState.DIRECT_SPAWNSTATE.a(new Object[0]),
               SpawnState.INCOMING_SPAWNSTATE.a(new Object[0]),
               SpawnState.SECONDARY_SPAWNSTATE.a(new Object[0])
            ),
            "database_version"
         );

      Object request;
      label43: {
         Integer data;
         try {
            ResultSet input = target.resolveObject();
            if (!input.next()) {
               request = null;
               break label43;
            }

            byte[] output = input.getBytes(1);
            DataInputStream context = new DataInputStream(new ByteArrayInputStream(output));
            data = context.readUnsignedShort();
         } catch (Throwable result) {
            if (target != null) {
               try {
                  target.close();
               } catch (Throwable value) {
                  result.addSuppressed(value);
               }
            }

            throw result;
         }

         if (target != null) {
            target.close();
         }

         return data;
      }

      if (target != null) {
         target.close();
      }

      return (Integer)request;
   }

   public LocalSettingsRepository(PasswordStore target, DirectNoticeCatalog input) {
      this.passwordStore = target;
      PasswordHashContainer.dispatchMessage("Loading database...");
      this.sharedListenerContract = processSharedListenerContract(target, input, false);
   }
}

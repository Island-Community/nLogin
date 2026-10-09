package com.nickuc.login.storage.password;

import com.nickuc.login.auth.login.LiveLoginCheckpoint;
import com.nickuc.login.auth.locale.OpenLocaleBarrier;
import com.nickuc.login.auth.login.PrimaryLoginHandler;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.OutgoingSpawnState;
import com.nickuc.login.platform.session.NestedSessionHandler;
import com.nickuc.login.platform.listener.SharedListenerContract;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class PasswordHashCollection implements NestedSessionHandler {
   public static final PasswordHashCollection passwordHashCollection = new PasswordHashCollection();

   @Override
   public String retrieveMessage() {
      return "nLogin104Converter";
   }

   @Override
   public void savePasswordStore(PasswordStore target, LocalSettingsRepository input, SharedListenerContract output) {
      LiveLoginCheckpoint context = new LiveLoginCheckpoint();
      if (CachedSettingsGateway.loadState()) {
         PasswordHashContainer.performMessage("");
         PasswordHashContainer.performMessage("O servidor exige uma conversão do banco de dados. (" + this.retrieveMessage() + ")");
         PasswordHashContainer.performMessage("NÃO DESLIGUE O SEU SERVIDOR (mesmo que o processo pareça travado)");
         PasswordHashContainer.performMessage("");
      } else {
         PasswordHashContainer.performMessage("");
         PasswordHashContainer.performMessage("Server requires conversion of database. (" + this.retrieveMessage() + ")");
         PasswordHashContainer.performMessage("DO NOT TURN OFF YOUR SERVER (even if the process seems stuck)");
         PasswordHashContainer.performMessage("");
      }

      String data = SpawnState.CURRENT_SPAWNSTATE.a(new Object[0]);
      String value = LocalPasswordTable.validateState(output, data, "last_login") ? "last_login" : OutgoingSpawnState.REMOTE_OUTGOINGSPAWNSTATE.getName();
      PasswordHashContainer.performMessage("Fetching column names...");
      String[] result = new String[]{
         OutgoingSpawnState.OUTGOING_SPAWN_STATE.getName(),
         OutgoingSpawnState.ACTIVE_OUTGOINGSPAWNSTATE.getName(),
         OutgoingSpawnState.PENDING_OUTGOINGSPAWNSTATE.getName(),
         OutgoingSpawnState.CURRENT_OUTGOINGSPAWNSTATE.getName(),
         OutgoingSpawnState.PRIMARY_OUTGOINGSPAWNSTATE.getName(),
         OutgoingSpawnState.MAIN_OUTGOINGSPAWNSTATE.getName(),
         LocalPasswordTable.validateState(output, data, "last_address") ? "last_address" : OutgoingSpawnState.LOCAL_OUTGOINGSPAWNSTATE.getName(),
         value,
         OutgoingSpawnState.CACHED_OUTGOINGSPAWNSTATE.getName(),
         OutgoingSpawnState.STORED_OUTGOINGSPAWNSTATE.getName(),
         OutgoingSpawnState.VERIFIED_OUTGOINGSPAWNSTATE.getName(),
         OutgoingSpawnState.AUTHENTICATED_OUTGOINGSPAWNSTATE.getName()
      };
      String[] request = new String[]{
         OutgoingSpawnState.OUTGOING_SPAWN_STATE.getName(),
         OutgoingSpawnState.ACTIVE_OUTGOINGSPAWNSTATE.getName(),
         OutgoingSpawnState.PENDING_OUTGOINGSPAWNSTATE.getName(),
         OutgoingSpawnState.CURRENT_OUTGOINGSPAWNSTATE.getName(),
         OutgoingSpawnState.PRIMARY_OUTGOINGSPAWNSTATE.getName(),
         OutgoingSpawnState.MAIN_OUTGOINGSPAWNSTATE.getName(),
         OutgoingSpawnState.LOCAL_OUTGOINGSPAWNSTATE.getName(),
         OutgoingSpawnState.REMOTE_OUTGOINGSPAWNSTATE.getName(),
         OutgoingSpawnState.CACHED_OUTGOINGSPAWNSTATE.getName(),
         OutgoingSpawnState.STORED_OUTGOINGSPAWNSTATE.getName(),
         OutgoingSpawnState.VERIFIED_OUTGOINGSPAWNSTATE.getName(),
         OutgoingSpawnState.AUTHENTICATED_OUTGOINGSPAWNSTATE.getName()
      };
      if (output.resolveDirectNoticeCatalog() == DirectNoticeCatalog.CURRENT_DIRECTNOTICECATALOG) {
         output.saveMessage("DROP INDEX IF EXISTS " + OutgoingSpawnState.ACTIVE_OUTGOINGSPAWNSTATE.getName() + "_idx");
         output.saveMessage("DROP INDEX IF EXISTS " + OutgoingSpawnState.LOCAL_OUTGOINGSPAWNSTATE.getName() + "_idx");
      }

      PasswordHashContainer.performMessage("Renaming table...");
      String response = data + "_103";
      this.a(output, data, response);
      input.sendTask();
      PasswordHashContainer.performMessage("Cloning table...");
      Connection source = output.resolveConnection();

      try {
         Statement entry = source.createStatement();

         try {
            if (output.resolveDirectNoticeCatalog() == DirectNoticeCatalog.ACTIVE_DIRECTNOTICECATALOG
               || output.resolveDirectNoticeCatalog() == DirectNoticeCatalog.DIRECT_NOTICE_CATALOG) {
               entry.addBatch("SET SESSION sql_mode = (SELECT REPLACE(@@sql_mode,'ONLY_FULL_GROUP_BY',''))");
            }

            entry.addBatch(
               String.format(
                  "INSERT INTO `%s` (`%s`) SELECT `%s` FROM `%s` WHERE `%s` IS NOT NULL GROUP BY `%s` HAVING COUNT(`%s`) <= 1;",
                  data,
                  String.join("`, `", request),
                  String.join("`, `", result),
                  response,
                  OutgoingSpawnState.PENDING_OUTGOINGSPAWNSTATE.getName(),
                  OutgoingSpawnState.PENDING_OUTGOINGSPAWNSTATE.getName(),
                  OutgoingSpawnState.PENDING_OUTGOINGSPAWNSTATE.getName()
               )
            );
            entry.executeBatch();
         } catch (Throwable option) {
            if (entry != null) {
               try {
                  entry.close();
               } catch (Throwable reference) {
                  option.addSuppressed(reference);
               }
            }

            throw option;
         }

         if (entry != null) {
            entry.close();
         }
      } catch (Throwable setting) {
         if (source != null) {
            try {
               source.close();
            } catch (Throwable holder) {
               setting.addSuppressed(holder);
            }
         }

         throw setting;
      }

      if (source != null) {
         source.close();
      }

      PasswordHashContainer.performMessage("Fetching duplicated UUIDs...");
      HashSet property = new HashSet();
      long attribute = 0L;
      PrimaryLoginHandler item = output.buildPrimaryLoginHandler(
         String.format(
            "SELECT `%s`, COUNT(`%s`) FROM " + response + " GROUP BY `%s` HAVING COUNT(`%s`) > 1",
            OutgoingSpawnState.PENDING_OUTGOINGSPAWNSTATE.getName(),
            OutgoingSpawnState.PENDING_OUTGOINGSPAWNSTATE.getName(),
            OutgoingSpawnState.PENDING_OUTGOINGSPAWNSTATE.getName(),
            OutgoingSpawnState.PENDING_OUTGOINGSPAWNSTATE.getName()
         )
      );

      try {
         for (ResultSet element = item.resolveObject(); element.next(); attribute += element.getInt(2)) {
            property.add(element.getString(1));
         }
      } catch (Throwable subject) {
         if (item != null) {
            try {
               item.close();
            } catch (Throwable payload) {
               subject.addSuppressed(payload);
            }
         }

         throw subject;
      }

      if (item != null) {
         item.close();
      }

      PasswordHashContainer.performMessage(
         "Inserting remaining accounts with duplicated UUIDs... (insert = "
            + OpenLocaleBarrier.resolveMessage(property.size())
            + ", skip = "
            + OpenLocaleBarrier.resolveMessage(attribute - property.size())
            + ")"
      );

      for (String argument : property) {
         output.saveMessage(
            String.format(
               "INSERT INTO `%s` (`%s`) SELECT `%s` FROM `%s` WHERE `%s` = ? ORDER BY `%s` DESC, `%s` DESC LIMIT 1",
               data,
               String.join("`, `", request),
               String.join("`, `", result),
               response,
               OutgoingSpawnState.PENDING_OUTGOINGSPAWNSTATE.getName(),
               OutgoingSpawnState.CURRENT_OUTGOINGSPAWNSTATE.getName(),
               value
            ),
            argument
         );
      }

      PasswordHashContainer.performMessage("Conversion finished! (" + context.loadMessage(TimeUnit.SECONDS, 3) + "s)");
   }

   @Override
   public boolean checkState(PasswordStore target, LocalSettingsRepository input, SharedListenerContract output) {
      String context = SpawnState.CURRENT_SPAWNSTATE.a(new Object[0]);
      if (!LocalPasswordTable.canState(output, context)) {
         return false;
      }

      String data = OutgoingSpawnState.PENDING_OUTGOINGSPAWNSTATE.getName();
      if (!LocalPasswordTable.validateState(output, context, data)) {
         return false;
      }

      switch (output.resolveDirectNoticeCatalog()) {
         case DIRECT_NOTICE_CATALOG:
         case ACTIVE_DIRECTNOTICECATALOG:
            PrimaryLoginHandler subject = output.buildPrimaryLoginHandler("SHOW COLUMNS FROM `" + context + "`");

            byte attribute;
            label136: {
               try {
                  ResultSet option = subject.resolveObject();

                  while (option.next()) {
                     String setting = option.getString("Field");
                     String property = option.getString("Key");
                     if (setting.equals(data) && !"UNI".equals(property)) {
                        attribute = 1;
                        break label136;
                     }
                  }
               } catch (Throwable reference) {
                  if (subject != null) {
                     try {
                        subject.close();
                     } catch (Throwable content) {
                        reference.addSuppressed(content);
                     }
                  }

                  throw reference;
               }

               if (subject != null) {
                  subject.close();
               }

               return false;
            }

            if (subject != null) {
               subject.close();
            }

            return (boolean)attribute;
         case CURRENT_DIRECTNOTICECATALOG:
            PrimaryLoginHandler value = output.buildPrimaryLoginHandler("PRAGMA index_list(" + context + ")");

            byte record;
            label131: {
               try {
                  ResultSet result = value.resolveObject();

                  while (result.next()) {
                     String request = result.getString("name");
                     PrimaryLoginHandler response = output.buildPrimaryLoginHandler("PRAGMA index_info(" + request + ")");

                     label133: {
                        try {
                           ResultSet source = response.resolveObject();
                           if (!source.next()) {
                              break label133;
                           }

                           String entry = source.getString("name");
                           if (!OutgoingSpawnState.PENDING_OUTGOINGSPAWNSTATE.getName().equals(entry)) {
                              break label133;
                           }

                           record = 0;
                        } catch (Throwable payload) {
                           if (response != null) {
                              try {
                                 response.close();
                              } catch (Throwable element) {
                                 payload.addSuppressed(element);
                              }
                           }

                           throw payload;
                        }

                        if (response != null) {
                           response.close();
                        }
                        break label131;
                     }

                     if (response != null) {
                        response.close();
                     }
                  }
               } catch (Throwable holder) {
                  if (value != null) {
                     try {
                        value.close();
                     } catch (Throwable item) {
                        holder.addSuppressed(item);
                     }
                  }

                  throw holder;
               }

               if (value != null) {
                  value.close();
               }

               return true;
            }

            if (value != null) {
               value.close();
            }

            return (boolean)record;
         default:
            throw new IllegalArgumentException("Invalid database type! " + output.resolveDirectNoticeCatalog());
      }
   }
}

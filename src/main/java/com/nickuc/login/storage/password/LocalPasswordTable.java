package com.nickuc.login.storage.password;

import com.nickuc.login.config.PasswordHashLoader;
import com.zaxxer.hikari.HikariConfig;
import com.nickuc.login.platform.listener.SharedListenerContract;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Properties;
import java.util.function.Consumer;
import javax.annotation.Nullable;

public class LocalPasswordTable {
   public static boolean canState(SharedListenerContract instance, String target) {
      Connection input = instance.resolveConnection();

      try {
         ResultSet output = input.getMetaData().getTables(input.getCatalog(), null, "%", null);

         byte context;
         label108: {
            try {
               while (output.next()) {
                  if (output.getString(3).equalsIgnoreCase(target)) {
                     context = 1;
                     break label108;
                  }
               }

               context = 0;
            } catch (Throwable entry) {
               if (output != null) {
                  try {
                     output.close();
                  } catch (Throwable source) {
                     entry.addSuppressed(source);
                  }
               }

               throw entry;
            }

            if (output != null) {
               output.close();
            }

            return (boolean)context;
         }

         if (output != null) {
            output.close();
         }

         return (boolean)context;
      } finally {
         instance.sendConnection(input);
      }
   }

   public static PasswordRepository buildPasswordRepository(PasswordHashLoader instance, DirectNoticeCatalog target) {
      Properties input = new Properties();
      String output = instance.b("database.remote.hostname");
      String context = instance.b("database.remote.database");
      String data = instance.b("database.remote.username");
      String value = instance.b("database.remote.password");

      for (String request : instance.loadSet("database.remote.properties")) {
         String response = instance.b("database.remote.properties." + request);
         input.setProperty(request, response);
      }

      return PasswordRepository.processPasswordRepository(output, context, data, value, input, target.loadCount());
   }

   public static boolean validateState(SharedListenerContract instance, String target, String input) {
      Connection output = instance.resolveConnection();

      boolean data;
      try {
         ResultSet context = output.getMetaData().getColumns(output.getCatalog(), null, target, input);

         try {
            data = context.next();
         } catch (Throwable record) {
            if (context != null) {
               try {
                  context.close();
               } catch (Throwable entry) {
                  record.addSuppressed(entry);
               }
            }

            throw record;
         }

         if (context != null) {
            context.close();
         }
      } finally {
         instance.sendConnection(output);
      }

      return data;
   }

   public static BusySettingsArchive createBusySettingsArchive(
      PasswordStore instance, DirectNoticeCatalog target, PasswordRepository input, @Nullable Consumer<HikariConfig> output
   ) {
      if (!target.fetchState()) {
         throw new IllegalArgumentException("Database " + target + " is not remote!");
      }

      switch (target) {
         case DIRECT_NOTICE_CATALOG:
            return PasswordTable.resolvePasswordTable(instance, input, output);
         case ACTIVE_DIRECTNOTICECATALOG:
            return PrimaryPasswordDao.resolvePrimaryPasswordDao(instance, input, output);
         default:
            throw new IllegalArgumentException("Unsupported database type! " + target);
      }
   }

   public static boolean isState(ResultSet instance, String target) {
      ResultSetMetaData input = instance.getMetaData();
      int output = input.getColumnCount();

      for (int context = 1; context <= output; context++) {
         if (target.equals(input.getColumnName(context))) {
            return true;
         }
      }

      return false;
   }
}

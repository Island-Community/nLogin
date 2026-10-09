package com.nickuc.login.storage.login;

import com.nickuc.login.auth.login.LoginProcessor;
import com.nickuc.login.auth.login.NestedLoginHandler;
import com.nickuc.login.model.LinkedSpawnOption;
import com.nickuc.login.platform.session.IndirectSessionHandler;
import java.io.File;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import javax.annotation.Nullable;

public class PrimaryLoginSource extends NestedLoginHandler {
   @Nullable
   private final Object object;
   private final LinkedSpawnOption linkedSpawnOption;
   private final Constructor<?> constructor;
   private final String name;
   private final Properties properties;
   @Nullable
   private final String activeName;

   private PrimaryLoginSource(IndirectSessionHandler<?> target, LinkedSpawnOption input, File output, Properties context, @Nullable String data, @Nullable Object value) {
      LoginProcessor result = target.fetchPasswordHashBridge().computeLoginProcessor(LinkedSpawnOption.handleTightSenderAdapter(input));

      try {
         Class request = result.loadClass("org.h2.jdbc.JdbcConnection");
         this.constructor = input == LinkedSpawnOption.LINKED_SPAWN_OPTION
            ? request.getConstructor(String.class, Properties.class)
            : request.getConstructor(String.class, Properties.class, String.class, Object.class, boolean.class);
      } catch (ReflectiveOperationException source) {
         throw new RuntimeException(source);
      }

      File entry = output.getParentFile();
      if (!entry.exists() && !entry.mkdirs()) {
         throw new RuntimeException("Unable to create folder " + entry + "!");
      }

      String response = output.getAbsolutePath();
      if (response.endsWith(".mv.db")) {
         response = response.substring(0, response.length() - ".mv.db".length());
      }

      this.linkedSpawnOption = input;
      this.name = "jdbc:h2:" + response;
      this.properties = context;
      this.activeName = data;
      this.object = value;
   }

   public static PrimaryLoginSource createPrimaryLoginSource(IndirectSessionHandler<?> instance, LinkedSpawnOption target, File input, Properties output) {
      return createPrimaryLoginSource(instance, target, input, output, null, null);
   }

   @Override
   public DirectNoticeCatalog resolveDirectNoticeCatalog() {
      return DirectNoticeCatalog.PRIMARY_DIRECTNOTICECATALOG;
   }

   @Override
   public Connection fetchConnection() {
      try {
         Object target = this.linkedSpawnOption.compareTo(LinkedSpawnOption.ACTIVE_LINKEDSPAWNOPTION) >= 0
            ? this.constructor.newInstance(this.name, this.properties, this.activeName, this.object, false)
            : this.constructor.newInstance(this.name, this.properties);
         return (Connection)target;
      } catch (ReflectiveOperationException input) {
         if (input.getCause() instanceof SQLException) {
            throw (SQLException)input.getCause();
         } else {
            throw new RuntimeException(input);
         }
      }
   }

   public static PrimaryLoginSource createPrimaryLoginSource(
      IndirectSessionHandler<?> instance, LinkedSpawnOption target, File input, Properties output, @Nullable String context, @Nullable Object data
   ) {
      return new PrimaryLoginSource(instance, target, input, output, context, data);
   }
}

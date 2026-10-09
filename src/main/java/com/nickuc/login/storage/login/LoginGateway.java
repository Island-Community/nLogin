package com.nickuc.login.storage.login;

import com.nickuc.login.auth.login.LoginProcessor;
import com.nickuc.login.auth.login.NestedLoginHandler;
import com.nickuc.login.model.InternalSpawnState;
import com.nickuc.login.platform.session.IndirectSessionHandler;
import com.nickuc.login.platform.proxy.SilentProxyState;
import java.io.File;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class LoginGateway extends NestedLoginHandler {
   private final String name;
   private final Properties properties;
   private final Constructor<?> constructor;

   private LoginGateway(IndirectSessionHandler<?> target, File input, Properties output) {
      if (target.findIncomingLoginGate().retrieveSilentProxyState() == SilentProxyState.SILENT_PROXY_STATE && target.findIncomingLoginGate().loadState()) {
         try {
            Class.forName("org.sqlite.JDBC");
         } catch (ClassNotFoundException result) {
            throw new RuntimeException("SQLite driver not found!", result);
         }

         this.constructor = null;
      } else {
         LoginProcessor context = target.fetchPasswordHashBridge()
            .computeLoginProcessor(
               InternalSpawnState.PRIVATE_INTERNALSPAWNSTATE, InternalSpawnState.INTERNAL_INTERNALSPAWNSTATE, InternalSpawnState.SECURE_INTERNALSPAWNSTATE
            );

         try {
            Class data = context.loadClass("org.sqlite.jdbc4.JDBC4Connection");
            this.constructor = data.getConstructor(String.class, String.class, Properties.class);
         } catch (ReflectiveOperationException value) {
            throw new RuntimeException(value);
         }
      }

      File request = input.getParentFile();
      if (!request.exists() && !request.mkdirs()) {
         throw new RuntimeException("Unable to create folder " + request + "!");
      }

      this.name = input.getAbsolutePath();
      this.properties = output;
   }

   public static LoginGateway handleLoginGateway(IndirectSessionHandler<?> instance, File target, Properties input) {
      return new LoginGateway(instance, target, input);
   }

   @Override
   public Connection fetchConnection() {
      if (this.constructor != null) {
         try {
            return (Connection)this.constructor.newInstance("jdbc:sqlite:" + this.name, this.name, this.properties);
         } catch (ReflectiveOperationException input) {
            if (input.getCause() instanceof SQLException) {
               throw (SQLException)input.getCause();
            } else {
               throw new RuntimeException(input);
            }
         }
      } else {
         return DriverManager.getConnection("jdbc:sqlite:" + this.name, this.properties);
      }
   }

   @Override
   public DirectNoticeCatalog resolveDirectNoticeCatalog() {
      return DirectNoticeCatalog.CURRENT_DIRECTNOTICECATALOG;
   }
}

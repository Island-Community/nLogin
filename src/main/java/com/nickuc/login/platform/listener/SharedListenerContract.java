package com.nickuc.login.platform.listener;

import com.nickuc.login.auth.login.LowLoginService;
import com.nickuc.login.auth.login.PrimaryLoginHandler;
import com.nickuc.login.auth.login.PrivateLoginHandler;
import com.nickuc.login.auth.login.StrictLoginHandler;
import com.nickuc.login.storage.notice.DirectNoticeCatalog;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import javax.annotation.CheckReturnValue;

public interface SharedListenerContract {
   default void saveMessage(String target, Object... input) {
      Connection output = this.resolveConnection();

      try {
         PreparedStatement context = output.prepareStatement(target);

         try {
            this.executePreparedStatement(context, input);
            context.execute();
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
         this.sendConnection(output);
      }
   }

   Connection resolveConnection();

   void executeTask();

   DirectNoticeCatalog resolveDirectNoticeCatalog();

   default StrictLoginHandler<Integer> computeStrictLoginHandler(String target, Object... input) {
      Connection output = this.resolveConnection();

      StrictLoginHandler data;
      try {
         PreparedStatement context = output.prepareStatement(target);

         try {
            this.executePreparedStatement(context, input);
            data = new StrictLoginHandler(context.executeUpdate(), null);
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
         this.sendConnection(output);
      }

      return data;
   }

   @CheckReturnValue
   default PrimaryLoginHandler buildPrimaryLoginHandler(String target, Object... input) {
      Connection output = this.resolveConnection();
      PreparedStatement context = null;

      try {
         context = output.prepareStatement(target);
         this.executePreparedStatement(context, input);
         ResultSet data = context.executeQuery();
         return new PrimaryLoginHandler(this, output, context, data, null);
      } catch (SQLException value) {
         if (context != null) {
            context.close();
         }

         this.sendConnection(output);
         throw value;
      }
   }

   @CheckReturnValue
   default PrivateLoginHandler getPrivateLoginHandler() {
      return new PrivateLoginHandler(this, this.resolveConnection(), null);
   }

   void sendConnection(Connection target);

   default StrictLoginHandler<Boolean> resolveStrictLoginHandler(String target, Object... input) {
      Connection output = this.resolveConnection();

      StrictLoginHandler data;
      try {
         PreparedStatement context = output.prepareStatement(target);

         try {
            this.executePreparedStatement(context, input);
            data = new StrictLoginHandler(context.execute(), null);
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
         this.sendConnection(output);
      }

      return data;
   }

   default void executePreparedStatement(PreparedStatement target, Object[] input) {
      for (int output = 0; output < input.length; output++) {
         Object context = input[output];
         if (context instanceof String) {
            target.setString(output + 1, (String)context);
         } else if (context instanceof Long) {
            target.setLong(output + 1, (Long)context);
         } else if (context instanceof Integer) {
            target.setInt(output + 1, (Integer)context);
         } else if (context instanceof Timestamp) {
            target.setTimestamp(output + 1, (Timestamp)context);
         } else if (context instanceof byte[]) {
            target.setBytes(output + 1, (byte[])context);
         } else {
            target.setObject(output + 1, context);
         }
      }
   }

   @CheckReturnValue
   default LowLoginService createLowLoginService(String target, Object... input) {
      Connection output = this.resolveConnection();
      PreparedStatement context = null;

      try {
         context = output.prepareStatement(target);
         this.executePreparedStatement(context, input);
         return new LowLoginService(this, output, context, null);
      } catch (SQLException value) {
         if (context != null) {
            context.close();
         }

         this.sendConnection(output);
         throw value;
      }
   }
}

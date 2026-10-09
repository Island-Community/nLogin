package com.nickuc.login.auth.update;

import com.nickuc.login.platform.listener.SharedListenerContract;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.annotation.CheckReturnValue;


public class SecondaryUpdateCheckpoint {
   private final Object[] values;
   private final String name;

   public SecondaryUpdateCheckpoint(String target, Object[] input) {
      this.name = target;
      this.values = input;
   }

   public int loadCount(SharedListenerContract target) {
      return target.computeStrictLoginHandler(this.name, this.values).resolveObject();
   }

   @CheckReturnValue
   public StrictLoginHandler<ResultSet> handleStrictLoginHandler(SharedListenerContract target) {
      return target.buildPrimaryLoginHandler(this.name, this.values);
   }

   public boolean validateState(SharedListenerContract target) {
      return target.resolveStrictLoginHandler(this.name, this.values).resolveObject();
   }

   public long computeTime(SharedListenerContract target) {
      PrivateLoginHandler input = target.getPrivateLoginHandler();

      long value;
      try {
         PreparedStatement output = input.retrieveConnection().prepareStatement(this.name, 1);

         try {
            target.executePreparedStatement(output, this.values);
            int context = output.executeUpdate();
            if (context == 0) {
               throw new RuntimeException("Unable to update account! affected rows=" + context);
            }

            ResultSet data = output.getGeneratedKeys();
            if (!data.next()) {
               throw new SQLException("Unable to retrieve generated keys!");
            }

            value = data.getLong(1);
         } catch (Throwable source) {
            if (output != null) {
               try {
                  output.close();
               } catch (Throwable response) {
                  source.addSuppressed(response);
               }
            }

            throw source;
         }

         if (output != null) {
            output.close();
         }
      } catch (Throwable entry) {
         if (input != null) {
            try {
               input.close();
            } catch (Throwable request) {
               entry.addSuppressed(request);
            }
         }

         throw entry;
      }

      if (input != null) {
         input.close();
      }

      return value;
   }
}

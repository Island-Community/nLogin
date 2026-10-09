package com.nickuc.login.storage.account;

import com.nickuc.login.api.types.AccountData;
import com.nickuc.login.api.types.AccountDataImpl;
import com.nickuc.login.auth.login.LowLoginService;
import com.nickuc.login.auth.mojang.MojangProcessor;
import com.nickuc.login.auth.login.PrimaryLoginHandler;
import com.nickuc.login.model.OutgoingSpawnState;
import com.nickuc.login.platform.listener.SharedListenerContract;
import com.nickuc.login.premium.IndirectPasswordResolver;
import com.nickuc.login.premium.SpawnLookup;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.function.Consumer;


public class AccountGateway implements Iterator<AccountData> {
   private final SharedListenerContract sharedListenerContract;
   private final long timestamp;
   private long activeTimestamp;
   private final IndirectPasswordResolver indirectPasswordResolver;

   @Override
   public boolean hasNext() {
      return this.activeTimestamp <= this.timestamp;
   }

   public AccountData next() {
      this.activeTimestamp++;

      try {
         PrimaryLoginHandler target = this.sharedListenerContract
            .buildPrimaryLoginHandler(
               String.format(
                  "SELECT * FROM `%s` WHERE `%s` = ? LIMIT 1",
                  SpawnState.CURRENT_SPAWNSTATE.a(new Object[0]),
                  OutgoingSpawnState.OUTGOING_SPAWN_STATE.getName()
               ),
               this.activeTimestamp
            );

         AccountDataImpl context;
         label56: {
            try {
               ResultSet input = target.resolveObject();
               if (input.next()) {
                  SpawnLookup output = this.indirectPasswordResolver.processSpawnLookup(input);
                  if (output != null) {
                     context = MojangProcessor.from(output);
                     break label56;
                  }

                  throw new RuntimeException("Cannot fetch account");
               }
            } catch (Throwable value) {
               if (target != null) {
                  try {
                     target.close();
                  } catch (Throwable data) {
                     value.addSuppressed(data);
                  }
               }

               throw value;
            }

            if (target != null) {
               target.close();
            }

            return null;
         }

         if (target != null) {
            target.close();
         }

         return context;
      } catch (SQLException result) {
         throw new RuntimeException(result);
      } catch (Exception request) {
         throw new RuntimeException("Unexpected exception on nLogin", request);
      }
   }

   @Override
   public void forEachRemaining(Consumer<? super AccountData> target) {
      try {
         LowLoginService input = this.sharedListenerContract
            .createLowLoginService(
               String.format(
                  "SELECT * FROM `%s` WHERE `%s` >= ?", SpawnState.CURRENT_SPAWNSTATE.a(new Object[0]), OutgoingSpawnState.OUTGOING_SPAWN_STATE.getName()
               ),
               this.activeTimestamp
            );

         try {
            PreparedStatement output = input.resolveObject();
            output.setFetchSize(256);
            ResultSet context = output.executeQuery();

            try {
               while (context.next()) {
                  SpawnLookup data = this.indirectPasswordResolver.processSpawnLookup(context);
                  if (data == null) {
                     throw new RuntimeException("Cannot fetch account");
                  }

                  target.accept(MojangProcessor.from(data));
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
            if (input != null) {
               try {
                  input.close();
               } catch (Throwable result) {
                  source.addSuppressed(result);
               }
            }

            throw source;
         }

         if (input != null) {
            input.close();
         }
      } catch (SQLException entry) {
         throw new RuntimeException(entry);
      } catch (Exception record) {
         throw new RuntimeException("Unexpected exception on nLogin", record);
      }
   }

   public AccountGateway(IndirectPasswordResolver target, SharedListenerContract input, long output) {
      this.indirectPasswordResolver = target;
      this.sharedListenerContract = input;
      this.timestamp = output;
   }

   @Override
   public void remove() {
      this.activeTimestamp++;
   }
}

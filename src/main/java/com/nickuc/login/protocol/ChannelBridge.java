package com.nickuc.login.protocol;

import com.nickuc.login.model.RemotePremiumState;
import com.nickuc.login.platform.session.IndirectSessionHandler;
import com.nickuc.login.platform.connection.TightConnectionContract;
import com.nickuc.login.platform.server.VerifiedServerAdapter;


public abstract class ChannelBridge<V extends IndirectSessionHandler<?>> implements TightConnectionContract {
   public final V indirectSessionHandler;

   public ChannelBridge(V target) {
      this.indirectSessionHandler = (V)target;
   }

   @Override
   public boolean checkState(VerifiedServerAdapter target, RemotePremiumState input, Object output, byte[] context) {
      if (output == null) {
         throw new IllegalArgumentException("Channel cannot be null!");
      }

      if (output instanceof String && ((String)output).isEmpty()) {
         throw new IllegalArgumentException("Channel cannot be empty!");
      }

      if (context == null) {
         throw new IllegalArgumentException("Output data cannot be null!");
      }

      if (target != null) {
         target.performIndirectSessionHandler(this.indirectSessionHandler, input, output, context);
         return true;
      }

      for (VerifiedServerAdapter value : this.indirectSessionHandler.retrieveParentAccountHandler().fetchCollection()) {
         if (value.loadState()) {
            value.performIndirectSessionHandler(this.indirectSessionHandler, input, output, context);
            return true;
         }
      }

      return false;
   }
}

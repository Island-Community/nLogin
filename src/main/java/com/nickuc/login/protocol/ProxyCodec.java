package com.nickuc.login.protocol;

import com.nickuc.login.config.PasswordHashDefinition;
import com.nickuc.login.listener.RootServerAdapter;
import com.nickuc.login.platform.account.OutgoingAccountHandler;

public class ProxyCodec extends ChannelBridge<PasswordHashDefinition> {
   @Override
   public void processObject(Object target) {
      this.dispatchObject(target, null);
   }

   @Override
   public void dispatchObject(Object target, Object input) {
      if (input != null && !(input instanceof RootServerAdapter)) {
         throw new IllegalArgumentException("Invalid listener! " + input.getClass().getCanonicalName());
      }

      if (!(target instanceof String)) {
         throw new IllegalArgumentException("Channel must be a string!");
      }

      this.indirectSessionHandler.retrieveBungeeLoader().getProxy().registerChannel((String)target);
      if (input != null) {
         this.indirectSessionHandler.a((OutgoingAccountHandler)input, new OutgoingAccountHandler[0]);
      }
   }

   public ProxyCodec(PasswordHashDefinition target) {
      super(target);
   }

   @Override
   public void updateObject(Object target) {
      if (!(target instanceof String)) {
         throw new IllegalArgumentException("Channel must be a string!");
      }

      this.indirectSessionHandler.retrieveBungeeLoader().getProxy().unregisterChannel((String)target);
   }
}

package com.nickuc.login.storage.floodgate;

import com.nickuc.login.auth.bedrock.BedrockCheckpoint;
import com.nickuc.login.listener.proxy.StrictBungeeGuard;
import com.nickuc.login.platform.account.InternalAccountHandler;
import com.nickuc.login.platform.connection.SecondaryConnectionContract;
import com.nickuc.login.premium.ReadyBedrockResolver;
import com.nickuc.login.protocol.ProxyCodec;
import com.nickuc.login.bungee.BungeePlatform;


public class FloodgateArchive implements InternalAccountHandler {
   private SecondaryConnectionContract secondaryConnectionContract;
   private final StrictBungeeGuard strictBungeeGuard;
   private LoginCollection loginCollection;
   private final BungeePlatform BungeePlatform;

   @Override
   public void processPasswordStore(PasswordStore target, boolean input) {
      this.executeTask();
   }

   public FloodgateArchive(BungeePlatform target, StrictBungeeGuard input) {
      this.BungeePlatform = target;
      this.strictBungeeGuard = input;
   }

   @Override
   public SecondaryConnectionContract retrieveSecondaryConnectionContract() {
      return this.secondaryConnectionContract;
   }

   @Override
   public LoginCollection getLoginCollection() {
      return this.loginCollection;
   }

   @Override
   public void executeTask() {
      PasswordStore target = this.BungeePlatform.fetchPasswordStore();
      if (this.BungeePlatform.b().canState("floodgate")) {
         this.secondaryConnectionContract = new ReadyBedrockResolver(target);
      } else if (this.BungeePlatform.b().canState("Geyser-BungeeCord")) {
         this.secondaryConnectionContract = new BedrockCheckpoint();
      } else {
         this.secondaryConnectionContract = null;
      }

      if (this.loginCollection == null) {
         ProxyCodec input = this.BungeePlatform.fetchProxyCodec();
         input.dispatchObject("nlogin:addon", this.strictBungeeGuard);
         this.loginCollection = new LoginCollection(target);
      }
   }

   @Override
   public void processTask() {
      if (this.loginCollection != null) {
         ProxyCodec target = this.BungeePlatform.fetchProxyCodec();
         target.updateObject("nlogin:addon");
      }

      this.secondaryConnectionContract = null;
   }
}

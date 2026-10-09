package com.nickuc.login.storage.floodgate;

import com.nickuc.login.auth.bedrock.BedrockCheckpoint;
import com.nickuc.login.listener.proxy.VelocityGuard;
import com.nickuc.login.listener.proxy.VelocityListener;
import com.nickuc.login.platform.account.InternalAccountHandler;
import com.nickuc.login.platform.connection.SecondaryConnectionContract;
import com.nickuc.login.premium.ReadyBedrockResolver;
import com.nickuc.login.velocity.VelocityPlatform;


public class FloodgateStore implements InternalAccountHandler {
   private final VelocityPlatform VelocityPlatform;
   private final VelocityGuard velocityGuard;
   private SecondaryConnectionContract secondaryConnectionContract;
   private LoginCollection loginCollection;

   @Override
   public LoginCollection getLoginCollection() {
      return this.loginCollection;
   }

   public FloodgateStore(VelocityPlatform target, VelocityGuard input) {
      this.VelocityPlatform = target;
      this.velocityGuard = input;
   }

   @Override
   public void processPasswordStore(PasswordStore target, boolean input) {
      this.executeTask();
   }

   @Override
   public SecondaryConnectionContract retrieveSecondaryConnectionContract() {
      return this.secondaryConnectionContract;
   }

   @Override
   public void processTask() {
      if (this.loginCollection != null) {
         VelocityListener target = this.VelocityPlatform.retrieveVelocityListener();
         target.updateObject(VelocityGuard.activeChannelIdentifier);
         target.updateObject(VelocityGuard.channelIdentifier);
      }

      this.secondaryConnectionContract = null;
   }

   @Override
   public void executeTask() {
      PasswordStore target = this.VelocityPlatform.fetchPasswordStore();
      if (this.VelocityPlatform.b().canState("floodgate")) {
         this.secondaryConnectionContract = new ReadyBedrockResolver(target);
      } else if (this.VelocityPlatform.b().canState("geyser")) {
         this.secondaryConnectionContract = new BedrockCheckpoint();
      } else {
         this.secondaryConnectionContract = null;
      }

      if (this.loginCollection == null) {
         VelocityListener input = this.VelocityPlatform.retrieveVelocityListener();
         input.dispatchObject(VelocityGuard.activeChannelIdentifier, this.velocityGuard);
         input.dispatchObject(VelocityGuard.channelIdentifier, this.velocityGuard);
         this.loginCollection = new LoginCollection(target);
      }
   }
}

package com.nickuc.login.auth.login;

import com.nickuc.login.platform.sender.SenderAdapter;

import org.slf4j.Logger;

public class MainLoginHandler implements SenderAdapter {
   private final Logger logger;

   @Override
   public void processMessage(String target, Throwable input) {
      this.logger.warn(target, input);
   }

   @Override
   public void performMessage(String target) {
      this.logger.warn(target);
   }

   @Override
   public <T> T findObject() {
      return (T)this.logger;
   }

   public MainLoginHandler(Logger target) {
      this.logger = target;
   }

   @Override
   public void handleMessage(String target, Throwable input) {
      this.logger.error(target, input);
   }

   @Override
   public void updateMessage(String target) {
      this.logger.info(target);
   }

   @Override
   public void dispatchMessage(String target) {
      this.logger.error(target);
   }
}

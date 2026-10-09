package com.nickuc.login.auth.login;

import com.nickuc.login.platform.sender.SenderAdapter;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ReadyLoginHandler implements SenderAdapter {
   private final Logger logger;

   @Override
   public void processMessage(String target, Throwable input) {
      this.logger.log(Level.WARNING, target, input);
   }

   @Override
   public void dispatchMessage(String target) {
      this.logger.severe(target);
   }

   public ReadyLoginHandler(Logger target) {
      this.logger = target;
   }

   @Override
   public void handleMessage(String target, Throwable input) {
      this.logger.log(Level.SEVERE, target, input);
   }

   @Override
   public void updateMessage(String target) {
      this.logger.info(target);
   }

   @Override
   public void performMessage(String target) {
      this.logger.warning(target);
   }

   @Override
   public <T> T findObject() {
      return (T)this.logger;
   }
}

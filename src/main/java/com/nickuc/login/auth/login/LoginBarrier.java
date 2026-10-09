package com.nickuc.login.auth.login;

import com.nickuc.login.platform.listener.SharedListenerContract;
import com.nickuc.login.storage.notice.DirectNoticeCatalog;
import java.sql.Connection;
import java.util.concurrent.atomic.AtomicInteger;


public class LoginBarrier implements SharedListenerContract {
   private final int count;
   private final SharedListenerContract sharedListenerContract;
   private final AtomicInteger atomicInteger = new AtomicInteger();
   private final Connection connection;

   @Override
   public void sendConnection(Connection target) {
      if (this.atomicInteger.get() > 0) {
         target.commit();
      }
   }

   @Override
   public Connection resolveConnection() {
      if (this.atomicInteger.getAndIncrement() >= this.count) {
         this.atomicInteger.set(1);
         this.connection.commit();
      }

      return this.connection;
   }

   @Override
   public DirectNoticeCatalog resolveDirectNoticeCatalog() {
      return this.sharedListenerContract.resolveDirectNoticeCatalog();
   }

   public LoginBarrier(SharedListenerContract target, int input) {
      this.sharedListenerContract = target;
      this.count = input;
      this.connection = target.resolveConnection();
      this.connection.setAutoCommit(false);
   }

   @Override
   public void executeTask() {
      if (this.atomicInteger.get() > 0) {
         this.connection.commit();
      }

      this.connection.close();
   }

   public int loadCount() {
      return this.count;
   }
}

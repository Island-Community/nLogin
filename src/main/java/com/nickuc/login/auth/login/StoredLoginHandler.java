package com.nickuc.login.auth.login;

import com.nickuc.login.platform.command.StrictCommandHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class StoredLoginHandler {
   private final List<StrictCommandHandler> entries = new ArrayList<>();
   private volatile CountDownLatch countDownLatch;

   public void performStrictCommandHandler(StrictCommandHandler target) {
      synchronized (this.entries) {
         this.entries.remove(target);
         if (this.countDownLatch != null) {
            this.countDownLatch.countDown();
         }
      }
   }

   public List<StrictCommandHandler> fetchCollection() {
      synchronized (this.entries) {
         return RemoteLoginBarrier.createRemoteLoginBarrier(this.entries);
      }
   }

   public boolean isState(long target, TimeUnit output) {
      synchronized (this.entries) {
         if (this.countDownLatch == null) {
            this.countDownLatch = new CountDownLatch(this.entries.size());
         }
      }

      boolean result = this.countDownLatch.await(target, output);
      this.countDownLatch = null;
      return result;
   }

   public void dispatchStrictCommandHandler(StrictCommandHandler target) {
      synchronized (this.entries) {
         this.entries.add(target);
      }
   }
}

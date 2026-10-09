package com.nickuc.login.auth.login;

import com.nickuc.login.platform.session.IndirectSessionHandler;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import javax.annotation.Nullable;


public class CachedLoginBarrier {
   private final AtomicBoolean atomicBoolean = new AtomicBoolean();
   private final IndirectSessionHandler<?> indirectSessionHandler;
   private boolean enabled = true;

   public void dispatchTask() {
   }

   public void dispatchState(boolean target) {
      this.enabled = target;
   }

   public CachedLoginBarrier(IndirectSessionHandler<?> target) {
      this.indirectSessionHandler = target;
   }

   public void saveConsumer(@Nullable Consumer<Boolean> target) {
   }

   public void saveTask() {
   }

   public boolean getState() {
      return this.enabled;
   }
}

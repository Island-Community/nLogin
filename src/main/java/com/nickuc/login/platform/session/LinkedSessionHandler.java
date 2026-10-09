package com.nickuc.login.platform.session;

import com.nickuc.login.auth.login.StoredLoginHandler;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public interface LinkedSessionHandler {
   default StrictCommandHandler loadStrictCommandHandler(Consumer<StrictCommandHandler> target, long input, long context) {
      return this.loadStrictCommandHandler(target, input, context, TimeUnit.MILLISECONDS);
   }

   StrictCommandHandler loadStrictCommandHandler(Runnable target, long input, TimeUnit context);

   StrictCommandHandler processStrictCommandHandler(Runnable target, long input, long context, TimeUnit value);

   default boolean checkState(int target, TimeUnit input) {
      return this.loadStoredLoginHandler().isState(target, input);
   }

   StrictCommandHandler buildStrictCommandHandler(Runnable target);

   StrictCommandHandler loadStrictCommandHandler(Consumer<StrictCommandHandler> target, long input, TimeUnit context);

   default Collection<StrictCommandHandler> getCollection() {
      return this.loadStoredLoginHandler().fetchCollection();
   }

   StoredLoginHandler loadStoredLoginHandler();

   void dispatchTask();

   StrictCommandHandler loadStrictCommandHandler(Consumer<StrictCommandHandler> target);

   default StrictCommandHandler createStrictCommandHandler(Runnable target, long input) {
      return this.loadStrictCommandHandler(target, input, TimeUnit.MILLISECONDS);
   }

   StrictCommandHandler loadStrictCommandHandler(Consumer<StrictCommandHandler> target, long input, long context, TimeUnit value);

   default StrictCommandHandler processStrictCommandHandler(Consumer<StrictCommandHandler> target, long input) {
      return this.loadStrictCommandHandler(target, input, TimeUnit.MILLISECONDS);
   }

   default StrictCommandHandler computeStrictCommandHandler(Runnable target, long input, long context) {
      return this.processStrictCommandHandler(target, input, context, TimeUnit.MILLISECONDS);
   }
}

package com.nickuc.login.auth.login;

import java.util.concurrent.Callable;


public class ActiveLoginFlow {
   private final LiveLoginBarrier[] values;
   private int count;
   private Long value;
   private long timestamp;
   private final CachedLoginHandler cachedLoginHandler;

   public ActiveLoginFlow(int target) {
      this(target, System.nanoTime());
   }

   public void dispatchTask() {
      if (this.value != null) {
         throw new IllegalArgumentException("Performance position has already been marked!");
      }

      this.value = System.nanoTime();
   }

   @Override
   public String toString() {
      if (this.values.length == 0) {
         return "No data!";
      }

      StringBuilder target = new StringBuilder();
      LiveLoginCheckpoint input = LiveLoginCheckpoint.buildLiveLoginCheckpoint(this.timestamp);

      for (int output = 0; output < this.values.length; output++) {
         LiveLoginBarrier context = this.values[output];
         if (context == null) {
            break;
         }

         if (target.length() > 0) {
            target.append("\n");
         }

         String data = CachedLoginHandler.processMessage(this.cachedLoginHandler)
            .replace("{pos}", Integer.toString(output))
            .replace("{id}", LiveLoginBarrier.buildMessage(context))
            .replace(
               "{start}",
               input.resolveMessage(
                  CachedLoginHandler.computeTimeUnit(this.cachedLoginHandler),
                  LiveLoginBarrier.processTime(context),
                  CachedLoginHandler.handleCount(this.cachedLoginHandler)
               )
            )
            .replace(
               "{end}",
               input.resolveMessage(
                  CachedLoginHandler.computeTimeUnit(this.cachedLoginHandler),
                  LiveLoginBarrier.processTimeForTime(context),
                  CachedLoginHandler.handleCount(this.cachedLoginHandler)
               )
            )
            .replace(
               "{took}",
               LiveLoginCheckpoint.buildLiveLoginCheckpoint(LiveLoginBarrier.processTime(context))
                  .resolveMessage(
                     CachedLoginHandler.computeTimeUnit(this.cachedLoginHandler),
                     LiveLoginBarrier.processTimeForTime(context),
                     CachedLoginHandler.handleCount(this.cachedLoginHandler)
                  )
            );
         target.append(data);
      }

      if (CachedLoginHandler.buildMessage(this.cachedLoginHandler) != null) {
         target.append("\n\n");
         target.append(
            String.format(
               CachedLoginHandler.buildMessage(this.cachedLoginHandler),
               input.loadMessage(CachedLoginHandler.computeTimeUnit(this.cachedLoginHandler), CachedLoginHandler.handleCount(this.cachedLoginHandler))
            )
         );
      }

      return target.toString();
   }

   public void updateMessage(String target) {
      this.performMessage(target);
      this.value = System.nanoTime();
   }

   public void saveTask() {
      this.timestamp = System.nanoTime();
      this.count = 0;
      this.value = null;
   }

   public void performMessage(String target) {
      if (this.value == null) {
         throw new IllegalArgumentException("Performance position was not marked!");
      }

      this.values[this.count++] = new LiveLoginBarrier(target, this.value, System.nanoTime(), null);
      this.value = null;
   }

   public void dispatchMessage(String target, Runnable input) {
      try {
         this.dispatchTask();
         input.run();
      } finally {
         this.performMessage(target);
      }
   }

   public ActiveLoginFlow(int target, long input, CachedLoginHandler context) {
      this.timestamp = input;
      this.values = new LiveLoginBarrier[target];
      this.cachedLoginHandler = context;
   }

   public <T> T processObject(String target, Callable<T> input) {
      try {
         this.dispatchTask();

         try {
            return (T)input.call();
         } catch (Exception result) {
            throw new RuntimeException(result);
         }
      } finally {
         this.performMessage(target);
      }
   }

   public LiveLoginBarrier[] fetchValues() {
      return this.values;
   }

   public ActiveLoginFlow(int target, long input) {
      this(target, input, CachedLoginHandler.cachedLoginHandler);
   }
}

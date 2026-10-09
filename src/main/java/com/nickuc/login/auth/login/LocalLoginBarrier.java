package com.nickuc.login.auth.login;



public class LocalLoginBarrier {
   private final boolean enabled;
   private final long timestamp;
   private final int count;
   private final Throwable throwable;

   public long fetchTime() {
      return this.timestamp;
   }

   @Override
   public String toString() {
      return "DownloadResponse(done="
         + this.retrieveState()
         + ", responseCode="
         + this.findCount()
         + ", contentLength="
         + this.fetchTime()
         + ", throwable="
         + this.resolveThrowable()
         + ")";
   }

   public Throwable resolveThrowable() {
      return this.throwable;
   }

   public LocalLoginBarrier(boolean target, int input, long output, Throwable data) {
      this.enabled = target;
      this.count = input;
      this.timestamp = output;
      this.throwable = data;
   }

   public int findCount() {
      return this.count;
   }

   public LocalLoginBarrier(boolean target, int input, long output) {
      this(target, input, output, null);
   }

   public boolean retrieveState() {
      return this.enabled;
   }
}

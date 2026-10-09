package com.nickuc.login.auth.login;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;


public class VerifiedLoginGate {
   private final int count;
   private final byte[] values;
   private final Throwable throwable;

   public VerifiedLoginGate(byte[] target, int input, Throwable output) {
      this.values = target;
      this.count = input;
      this.throwable = output;
   }

   public byte[] loadPayload() {
      return this.values;
   }

   public boolean fetchState() {
      return this.count != 0;
   }

   public int findCount() {
      return this.count;
   }

   @Override
   public String toString() {
      return "HttpResponse(content="
         + Arrays.toString(this.loadPayload())
         + ", responseCode="
         + this.findCount()
         + ", throwable="
         + this.resolveThrowable()
         + ")";
   }

   public Throwable resolveThrowable() {
      return this.throwable;
   }

   public String loadMessage() {
      return this.values == null ? null : new String(this.values, StandardCharsets.UTF_8);
   }

   public VerifiedLoginGate(byte[] target, int input) {
      this(target, input, null);
   }
}

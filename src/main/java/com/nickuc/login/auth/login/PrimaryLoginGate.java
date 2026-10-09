package com.nickuc.login.auth.login;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class PrimaryLoginGate {
   private final int count;
   public static final PrimaryLoginGate primaryLoginGate;
   private final int activeCount;
   public static final PrimaryLoginGate activePrimaryLoginGate;
   private final boolean enabled;
   private final InetAddress inetAddress;
   public static final PrimaryLoginGate pendingPrimaryLoginGate;

   static {
      try {
         activePrimaryLoginGate = new PrimaryLoginGate("10.0.0.0/8");
         primaryLoginGate = new PrimaryLoginGate("172.16.0.0/12");
         pendingPrimaryLoginGate = new PrimaryLoginGate("192.168.0.0/16");
      } catch (UnknownHostException target) {
         throw new RuntimeException(target);
      }
   }

   public PrimaryLoginGate(String target) {
      String[] input = target.split("/");
      String output;
      if (input.length == 2) {
         output = input[0];
         this.activeCount = Integer.parseInt(input[1]);
      } else {
         output = target;
         this.activeCount = -1;
      }

      this.inetAddress = InetAddress.getByName(output);
      if (this.inetAddress instanceof Inet4Address) {
         this.enabled = (boolean)(input.length == 2 && this.activeCount != 32 ? 0 : 1);
      } else {
         if (!(this.inetAddress instanceof Inet6Address)) {
            throw new IllegalArgumentException("Unsupported inet address! " + this.inetAddress.getClass().getCanonicalName());
         }

         this.enabled = (boolean)(input.length == 2 && this.activeCount != 128 ? 0 : 1);
      }

      this.count = this.enabled ? -1 : this.activeCount / 8;
   }

   public boolean validateState(InetAddress target) {
      if (!this.inetAddress.getClass().equals(target.getClass())) {
         return false;
      }

      if (this.enabled) {
         return target.equals(this.inetAddress);
      }

      byte[] input = target.getAddress();
      byte[] output = this.inetAddress.getAddress();
      byte context = (byte)(65280 >> (this.activeCount & 7));

      for (int data = 0; data < this.count; data++) {
         if (input.length < data || output.length < data || input[data] != output[data]) {
            return false;
         }
      }

      return context != 0 ? (input[this.count] & context) == (output[this.count] & context) : true;
   }
}

package com.nickuc.login.auth.login;

import com.nickuc.login.platform.proxy.SilentProxyState;


public class IncomingLoginGate {
   private final SilentProxyState silentProxyState;
   private final String name;
   private final String activeName;
   private final boolean enabled;
   private final String pendingName;

   public IncomingLoginGate(String target, String input, String output, SilentProxyState context, boolean data) {
      this.name = target;
      this.activeName = input;
      this.pendingName = output;
      this.silentProxyState = context;
      this.enabled = data;
   }

   public SilentProxyState retrieveSilentProxyState() {
      return this.silentProxyState;
   }

   public String retrieveMessage() {
      return this.pendingName;
   }

   public boolean loadState() {
      return this.enabled;
   }

   public String getMessage() {
      return this.activeName;
   }

   @Override
   public String toString() {
      return "PlatformVersion(name="
         + this.retrieveMessageForMessage()
         + ", version="
         + this.getMessage()
         + ", fullVersion="
         + this.retrieveMessage()
         + ", type="
         + this.retrieveSilentProxyState()
         + ", hybrid="
         + this.loadState()
         + ")";
   }

   public String retrieveMessageForMessage() {
      return this.name;
   }
}

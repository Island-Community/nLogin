package com.nickuc.login.model;



public enum DirectProxyState {
   DIRECT_PROXY_STATE(0),
   ACTIVE_DIRECTPROXYSTATE(587),
   PENDING_DIRECTPROXYSTATE(465);

   private final int count;

   DirectProxyState(int output) {
      this.count = output;
   }

   private static DirectProxyState loadDirectProxyState(String instance, int target) {
      boolean input = "AUTO".equalsIgnoreCase(instance);

      for (DirectProxyState value : values()) {
         int result = input ? (value.count == target ? 1 : 0) : value.name().equalsIgnoreCase(instance);
         if (result != 0) {
            return value;
         }
      }

      return DIRECT_PROXY_STATE;
   }
}

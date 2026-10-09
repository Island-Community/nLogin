package com.nickuc.login.model;



public enum ProxyCatalog {
   PROXY_CATALOG,
   ACTIVE_PROXYCATALOG,
   PENDING_PROXYCATALOG,
   CURRENT_PROXYCATALOG,
   PRIMARY_PROXYCATALOG;

   public String createMessage(String target) {
      switch (this) {
         case PROXY_CATALOG:
            return "$MD5$" + target;
         case ACTIVE_PROXYCATALOG:
            return "$SHA256$" + target;
         case PENDING_PROXYCATALOG:
            return "$SHA512$" + target;
         default:
            return target;
      }
   }
}

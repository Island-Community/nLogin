package com.nickuc.login.model;

import com.nickuc.login.config.PasswordHashContainer;


public enum IncomingLoginOption {
   INCOMING_LOGIN_OPTION("", ""),
   ACTIVE_INCOMINGLOGINOPTION("[DEBUG] ", "§3"),
   PENDING_INCOMINGLOGINOPTION("[WARN] ", "§e"),
   CURRENT_INCOMINGLOGINOPTION("[ERROR] ", "§4");

   public final String name;
   public final String activeName;

   public String resolveMessage(String target, boolean input) {
      return input
         ? this.activeName + PasswordHashContainer.fetchMessage() + this.name + (this == CURRENT_INCOMINGLOGINOPTION ? "§c" : "") + target
         : PasswordHashContainer.fetchMessage() + this.name + target;
   }

   IncomingLoginOption(String output, String context) {
      this.name = output;
      this.activeName = context;
   }
}

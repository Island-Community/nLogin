package com.nickuc.login.notification;

import javax.annotation.Nullable;


public class NoticeSender {
   @Nullable
   private final String name;
   private final String activeName;

   public String findMessage() {
      return this.activeName;
   }

   @Nullable
   public String as() {
      return this.name;
   }

   @Override
   public String toString() {
      return "Notification.ButtonContent(text=" + this.findMessage() + ", hoverText=" + this.as() + ")";
   }

   public NoticeSender(String target, @Nullable String input) {
      this.activeName = target;
      this.name = input;
   }
}

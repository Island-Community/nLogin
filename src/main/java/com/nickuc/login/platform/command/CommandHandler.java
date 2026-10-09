package com.nickuc.login.platform.command;

import com.nickuc.login.discord.DiscordNotifier;
import com.nickuc.login.discord.ParentDiscordNotifier;

public interface CommandHandler {
   default void handleCount(int target) {
      this.getParentDiscordNotifier().createParentDiscordNotifier(target);
   }

   ParentDiscordNotifier getParentDiscordNotifier();

   default void processDiscordNotifier(DiscordNotifier target) {
      this.getParentDiscordNotifier().processDiscordNotifier(target);
   }
}

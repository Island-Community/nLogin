package com.nickuc.login.discord;

import com.nickuc.login.premium.SpawnLookup;


public class QuickDiscordHandler {
   public String name;
   public String activeName;

   public QuickDiscordHandler(SpawnLookup target) {
      this.spawnLookup = target;
   }

   public boolean fetchState() {
      return this.spawnLookup.cachedPasswordHashHasher.resolveObject("discord-2fa", false);
   }

   public String getMessage() {
      return this.name;
   }

   public boolean retrieveState() {
      return this.spawnLookup.cachedPasswordHashHasher.resolveObject("email-2fa", false);
   }

   public void saveMessage(String target) {
      this.activeName = target;
   }

   public void saveTask() {
      this.name = null;
      this.activeName = null;
      this.spawnLookup.cachedPasswordHashHasher.performMessage("email-2fa");
      this.spawnLookup.cachedPasswordHashHasher.performMessage("discord-2fa");
   }

   public void processState(boolean target) {
      this.spawnLookup.cachedPasswordHashHasher.updateMessage("email-2fa", target);
   }

   public String loadMessage() {
      return this.activeName;
   }

   public boolean resolveState() {
      return this.activeName != null || this.name != null;
   }

   public void dispatchMessage(String target) {
      this.name = target;
   }

   public void executeState(boolean target) {
      this.spawnLookup.cachedPasswordHashHasher.updateMessage("discord-2fa", target);
   }
}

package com.nickuc.login.spawn;

import com.nickuc.login.api.enums.SpawnType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;


public enum IndirectLoginKind {
   INDIRECT_LOGIN_KIND("join", "Join", SpawnType.JOIN),
   ACTIVE_INDIRECTLOGINKIND("firstjoin", "First Join", SpawnType.FIRST_JOIN),
   PENDING_INDIRECTLOGINKIND("auth", "Auth", SpawnType.LOGIN),
   CURRENT_INDIRECTLOGINKIND("register", "Register", SpawnType.REGISTER),
   PRIMARY_INDIRECTLOGINKIND("respawn", "Respawn", SpawnType.RESPAWN),
   MAIN_INDIRECTLOGINKIND("last_location", "Last Location", null);

   public static final List<String> entries = Arrays.stream(values()).map(instance -> instance.name).collect(Collectors.toList());
   public final String name;
   public final String activeName;
   public final SpawnType spawnType;

   @Nullable
   public static IndirectLoginKind handleIndirectLoginKind(String instance) {
      switch (instance) {
         case "join":
            return INDIRECT_LOGIN_KIND;
         case "firstjoin":
            return ACTIVE_INDIRECTLOGINKIND;
         case "auth":
            return PENDING_INDIRECTLOGINKIND;
         case "register":
            return CURRENT_INDIRECTLOGINKIND;
         case "respawn":
            return PRIMARY_INDIRECTLOGINKIND;
         case "last_location":
            return MAIN_INDIRECTLOGINKIND;
         default:
            return null;
      }
   }

   public static IndirectLoginKind computeIndirectLoginKind(SpawnType instance) {
      switch (instance) {
         case JOIN:
            return INDIRECT_LOGIN_KIND;
         case FIRST_JOIN:
            return ACTIVE_INDIRECTLOGINKIND;
         case LOGIN:
            return PENDING_INDIRECTLOGINKIND;
         case REGISTER:
            return CURRENT_INDIRECTLOGINKIND;
         case RESPAWN:
            return PRIMARY_INDIRECTLOGINKIND;
         default:
            throw new IllegalArgumentException("Unsupported spawn type! " + instance);
      }
   }

   public String fetchMessage() {
      return "spawn." + this.name;
   }

   IndirectLoginKind(String output, String context, SpawnType data) {
      this.name = output;
      this.activeName = context;
      this.spawnType = data;
   }

   public String processMessage(boolean target) {
      switch (this) {
         case INDIRECT_LOGIN_KIND:
            return target ? "Localização após entrar." : "Location after joining.";
         case ACTIVE_INDIRECTLOGINKIND:
            return target ? "Localização após entrar pela 1ª vez." : "Location after joining for the first time.";
         case PENDING_INDIRECTLOGINKIND:
            return target ? "Localização após autenticar." : "Location after authentication.";
         case CURRENT_INDIRECTLOGINKIND:
            return target ? "Localização após registrar." : "Location after registration.";
         case PRIMARY_INDIRECTLOGINKIND:
            return target ? "Localização após renascer." : "Location after respawning.";
         case MAIN_INDIRECTLOGINKIND:
            return target ? "Restaura a última localização após autenticar." : "Restores the last location after authenticating.";
         default:
            throw new IllegalArgumentException("Unsupported spawn type! " + this);
      }
   }
}

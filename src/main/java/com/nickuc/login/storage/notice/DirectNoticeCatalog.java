package com.nickuc.login.storage.notice;

import com.nickuc.login.model.InternalSpawnState;


public enum DirectNoticeCatalog {
   DIRECT_NOTICE_CATALOG(InternalSpawnState.DIRECT_INTERNALSPAWNSTATE, "MariaDB", true, 3306),
   ACTIVE_DIRECTNOTICECATALOG(InternalSpawnState.LINKED_INTERNALSPAWNSTATE, "MySQL", true, 3306),
   PENDING_DIRECTNOTICECATALOG(InternalSpawnState.ROOT_INTERNALSPAWNSTATE, "PostgreSQL", true, 5432),
   CURRENT_DIRECTNOTICECATALOG(InternalSpawnState.SECURE_INTERNALSPAWNSTATE, "SQLite", false, -1),
   PRIMARY_DIRECTNOTICECATALOG(InternalSpawnState.FAST_INTERNALSPAWNSTATE, "H2", false, -1);

   private final InternalSpawnState internalSpawnState;
   private final String name;
   private final boolean enabled;
   private final int count;

   public boolean fetchState() {
      return this.enabled;
   }

   public int loadCount() {
      return this.count;
   }

   public InternalSpawnState findInternalSpawnState() {
      return this.internalSpawnState;
   }

   DirectNoticeCatalog(InternalSpawnState output, String context, boolean data, int value) {
      this.internalSpawnState = output;
      this.name = context;
      this.enabled = data;
      this.count = value;
   }

   public String resolveMessage() {
      return this.name;
   }
}

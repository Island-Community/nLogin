package com.nickuc.login.storage.login;

import com.nickuc.login.platform.listener.SharedListenerContract;
import java.sql.Connection;


public class LoginTable implements SharedListenerContract {
   private final ReadySettingsTable readySettingsTable;
   private final DirectNoticeCatalog directNoticeCatalog;

   @Override
   public DirectNoticeCatalog resolveDirectNoticeCatalog() {
      return this.directNoticeCatalog;
   }

   @Override
   public void sendConnection(Connection target) {
      target.close();
   }

   @Override
   public Connection resolveConnection() {
      return this.readySettingsTable.getConnection();
   }

   @Override
   public void executeTask() {
      this.readySettingsTable.executeTask();
   }

   private LoginTable(DirectNoticeCatalog target, ReadySettingsTable input) {
      this.directNoticeCatalog = target;
      this.readySettingsTable = input;
   }
}

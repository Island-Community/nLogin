package com.nickuc.login.storage.backup;

import com.nickuc.login.platform.sender.OutgoingSenderAdapter;

public class BackupRepository extends LoginSource {
   @Override
   public void processOutgoingSenderAdapter(OutgoingSenderAdapter target, String[] input) {
      BackupDao.createFile(this.passwordStore, target, this.loadState());
   }

   public BackupRepository(PasswordStore target) {
      super(target, "backup", "nlogin.admin", false, false);
   }
}

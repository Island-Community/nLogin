package com.nickuc.login.storage.login;

import com.nickuc.login.account.MessageOption;

public class PendingLoginTable extends UpdateGateway {
   public PendingLoginTable(PasswordStore target) {
      super(target, MessageOption.ACTIVE_UPSTREAM_MESSAGEOPTION, DirectNoticeCatalog.ACTIVE_DIRECTNOTICECATALOG, DirectNoticeCatalog.CURRENT_DIRECTNOTICECATALOG);
   }
}

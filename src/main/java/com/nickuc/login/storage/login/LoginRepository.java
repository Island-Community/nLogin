package com.nickuc.login.storage.login;

import com.nickuc.login.account.MessageOption;

public class LoginRepository extends UpdateGateway {
   public LoginRepository(PasswordStore target) {
      super(target, MessageOption.ACTIVE_INTERNAL_MESSAGEOPTION, DirectNoticeCatalog.CURRENT_DIRECTNOTICECATALOG, DirectNoticeCatalog.ACTIVE_DIRECTNOTICECATALOG);
   }
}

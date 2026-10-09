package com.nickuc.login.storage.login;

import com.nickuc.login.protocol.LocaleAdapter;
import com.nickuc.login.protocol.LocaleInterceptor;
import com.nickuc.login.protocol.MessageAdapter;
import com.nickuc.login.protocol.MessageHandler;


public class LoginDao {
   public final LocaleInterceptor localeInterceptor;
   private final PasswordStore passwordStore;
   public final MessageHandler messageHandler = new MessageHandler(this, null);
   public final MessageAdapter messageAdapter;
   public final LocaleAdapter localeAdapter = new LocaleAdapter(this, null);

   public LoginDao(PasswordStore target) {
      this.localeInterceptor = new LocaleInterceptor(this, null);
      this.messageAdapter = new MessageAdapter(this, null);
      this.passwordStore = target;
   }
}

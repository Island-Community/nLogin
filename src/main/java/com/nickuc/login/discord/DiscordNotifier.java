package com.nickuc.login.discord;

import com.nickuc.login.auth.login.IncomingLoginGate;
import com.nickuc.login.command.PasswordHashCommand;
import com.nickuc.login.platform.session.CachedSessionHandler;
import com.nickuc.login.platform.listener.ChildListenerContract;
import com.nickuc.login.platform.session.IndirectSessionHandler;
import com.nickuc.login.platform.session.LinkedSessionHandler;
import com.nickuc.login.platform.account.OutgoingAccountHandler;
import com.nickuc.login.platform.sender.SenderAdapter;
import com.nickuc.login.platform.connection.TightConnectionContract;
import com.nickuc.login.platform.sender.TightSenderAdapter;
import java.io.File;


public abstract class DiscordNotifier implements IndirectSessionHandler<Object>, ChildListenerContract {
   public IndirectSessionHandler<?> indirectSessionHandler;

   public void performTask() {
   }

   public void dispatchTask() {
   }

   public void processTask() {
   }

   @Override
   public ParentDiscordNotifier getParentDiscordNotifier() {
      return this.indirectSessionHandler.getParentDiscordNotifier();
   }

   public void updateTask() {
   }

   @Override
   public TightConnectionContract resolveTightConnectionContract() {
      return this.indirectSessionHandler.resolveTightConnectionContract();
   }

   @Override
   public <T> T findObject() {
      return (T)this.indirectSessionHandler;
   }

   public TightSenderAdapter[] findValues() {
      return this.resolveCachedSessionHandler().findValues();
   }

   @Override
   public void savePasswordHashCommand(PasswordHashCommand<?> target, PasswordHashCommand<?>... input) {
      this.indirectSessionHandler.savePasswordHashCommand(target, input);
   }

   @Override
   public void saveOutgoingAccountHandler(OutgoingAccountHandler target, OutgoingAccountHandler... input) {
      this.indirectSessionHandler.saveOutgoingAccountHandler(target, input);
   }

   @Override
   public <T extends DiscordNotifier> T findDiscordNotifier() {
      return (T)this;
   }

   @Override
   public CachedSessionHandler resolveCachedSessionHandler() {
      return this.indirectSessionHandler.resolveCachedSessionHandler();
   }

   @Override
   public SenderAdapter fetchSenderAdapter() {
      return this.indirectSessionHandler.fetchSenderAdapter();
   }

   @Override
   public String retrieveMessage() {
      return this.indirectSessionHandler.retrieveMessage();
   }

   @Override
   public void executeTask() {
      this.indirectSessionHandler.executeTask();
   }

   public void handleIndirectSessionHandler(IndirectSessionHandler<?> target) {
      this.indirectSessionHandler = target;
   }

   @Override
   public boolean resolveState() {
      return this.indirectSessionHandler.resolveState();
   }

   @Override
   public LinkedSessionHandler processLinkedSessionHandler(boolean target) {
      return this.indirectSessionHandler.processLinkedSessionHandler(target);
   }

   @Override
   public Object loadObject() {
      return this.indirectSessionHandler.loadObject();
   }

   @Override
   public IncomingLoginGate findIncomingLoginGate() {
      return this.indirectSessionHandler.findIncomingLoginGate();
   }

   @Override
   public Object buildObject(int target) {
      return this.indirectSessionHandler.buildObject(target);
   }

   @Override
   public File resolveFile() {
      return this.indirectSessionHandler.resolveFile();
   }

   @Override
   public String toString() {
      return this.indirectSessionHandler.retrieveMessage()
         + " v"
         + this.indirectSessionHandler.getMessage()
         + (
            this.indirectSessionHandler.retrieveUpdateLookup() == null
               ? ""
               : " (build " + this.indirectSessionHandler.retrieveUpdateLookup().getMessage() + ")"
         );
   }

   @Override
   public String getMessage() {
      return this.indirectSessionHandler.getMessage();
   }
}

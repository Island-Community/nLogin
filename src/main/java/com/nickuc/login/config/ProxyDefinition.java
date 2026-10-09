package com.nickuc.login.config;

import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.session.LimboSupervisor;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import java.util.TimerTask;

public class ProxyDefinition extends TimerTask {
   public void updateTask() {
      int target = LimboSupervisor.computeCache(this.limboSupervisor).asMap().remove(this.verifiedServerAdapter) != null ? 1 : 0;
      if (target == 0) {
         VerifiedServerAdapter input = LimboSupervisor.loadNLoginBukkit(this.limboSupervisor)
            .b()
            .createVerifiedServerAdapter(this.verifiedServerAdapter.getUniqueId());
         if (this.verifiedServerAdapter.equals(input)) {
            this.verifiedServerAdapter
               .buildCompletableFuture(
                  CachedSettingsGateway.loadState()
                     ? "§4[nLogin] Erro de configuração detectado no servidor proxy:\n§r\n§r§cMensagem ACK não recebida no servidor backend.\n§r\n§r§ePor favor, verifique se você instalou o nLogin no servidor proxy."
                     : "§4[nLogin] Error detected on proxy server setup:\n§r\n§r§cACK message not received on backend server.\n§r\n§r§ePlease make sure you have installed nLogin on the proxy server."
               );
         }
      }
   }

   public ProxyDefinition(LimboSupervisor target, VerifiedServerAdapter input) {
      this.limboSupervisor = target;
      this.verifiedServerAdapter = input;
   }
}

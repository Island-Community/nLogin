package com.nickuc.login.tasks.limbo;

import com.nickuc.login.tasks.CommonTask;

public class DelayedPlayerLimboClearTask extends CommonTask {
   public DelayedPlayerLimboClearTask(Runnable target) {
      super(target);
   }
}

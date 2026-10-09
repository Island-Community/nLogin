package com.nickuc.login.spawn;

import com.nickuc.login.api.types.Location;


public class LoginResolver implements Location {
   private final String name;
   private final float factor;
   private final double ratio;
   private final float activeFactor;
   private final double activeRatio;
   private final double pendingRatio;

   public float getPitch() {
      return this.activeFactor;
   }

   public double getY() {
      return this.activeRatio;
   }

   public String getWorldName() {
      return this.name;
   }

   public LoginResolver(String target, double input, double context, double value, float request, float response) {
      this.name = target;
      this.pendingRatio = input;
      this.activeRatio = context;
      this.ratio = value;
      this.factor = request;
      this.activeFactor = response;
   }

   @Override
   public String toString() {
      return "LocationImpl(worldName="
         + this.getWorldName()
         + ", x="
         + this.getX()
         + ", y="
         + this.getY()
         + ", z="
         + this.getZ()
         + ", yaw="
         + this.getYaw()
         + ", pitch="
         + this.getPitch()
         + ")";
   }

   public float getYaw() {
      return this.factor;
   }

   public double getZ() {
      return this.ratio;
   }

   public double getX() {
      return this.pendingRatio;
   }
}

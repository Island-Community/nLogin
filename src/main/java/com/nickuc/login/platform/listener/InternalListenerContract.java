package com.nickuc.login.platform.listener;

import com.nickuc.login.auth.login.BusyLoginProcessor;
import com.nickuc.login.auth.locale.OpenLocaleBarrier;
import com.nickuc.login.auth.login.RemoteLoginBarrier;
import com.nickuc.login.auth.login.SecureLoginGate;
import com.nickuc.login.security.hashing.VerifiedPasswordHashHasher;
import java.util.ArrayList;
import java.util.List;

public interface InternalListenerContract {
   default <T> T retrieveObject() {
      return this.processObject(this.loadSecureLoginGate());
   }

   default boolean retrieveState() {
      return this.checkState(this.loadSecureLoginGate());
   }

   default List<Integer> findCollection() {
      return this.handleCollection(this.loadSecureLoginGate());
   }

   default List<String> handleCollection(SecureLoginGate target, Object... input) {
      return new ArrayList<>(this.processCollection(target, input));
   }

   default String resolveMessage(Object... target) {
      return this.computeMessage(this.loadSecureLoginGate(), target);
   }

   Object getObject();

   default boolean checkState(SecureLoginGate target) {
      return this.<Boolean>processObject(target);
   }

   default List<Integer> handleCollection(SecureLoginGate target) {
      return new ArrayList<>(this.buildCollection(target));
   }

   default List<?> findCollectionForCollection() {
      return this.computeCollection(this.loadSecureLoginGate());
   }

   default List<String> loadCollection(Object... target) {
      return this.processCollection(this.loadSecureLoginGate(), target);
   }

   default List<?> loadCollection() {
      return this.createCollection(this.loadSecureLoginGate());
   }

   default <T> T processObject(SecureLoginGate target) {
      Object input = VerifiedPasswordHashHasher.computeObject(this, target);
      if (input == null) {
         input = this.getObject();
         if (input == null) {
            throw new IllegalArgumentException("Default value cannot be null!");
         }
      }

      try {
         return (T)input;
      } catch (Throwable context) {
         throw new RuntimeException("Unable to cast " + this + " to requested type!", context);
      }
   }

   default double findRatio() {
      return this.computeRatio(this.loadSecureLoginGate());
   }

   BusyLoginProcessor retrieveBusyLoginProcessor();

   SecureLoginGate loadSecureLoginGate();

   default List<String> processCollection(SecureLoginGate target, Object... input) {
      List output = this.processObject(target);
      if (input != null && input.length > 0) {
         String[] context = output.toArray(new String[0]);
         boolean data = false;

         for (int value = 0; value < context.length; value++) {
            String result = context[value];
            String request = OpenLocaleBarrier.handleMessage(result, input);
            if (!data && !result.equals(request)) {
               data = true;
            }

            context[value] = request;
         }

         if (data) {
            return RemoteLoginBarrier.loadRemoteLoginBarrier(context);
         }
      }

      return output;
   }

   default Object computeObject(SecureLoginGate target) {
      return this.processObject(target);
   }

   default List<?> computeCollection(SecureLoginGate target) {
      return new ArrayList(this.createCollection(target));
   }

   int fetchCount();

   default long handleTime(SecureLoginGate target) {
      return this.<Long>processObject(target);
   }

   default List<Integer> resolveCollection() {
      return this.buildCollection(this.loadSecureLoginGate());
   }

   default Object processObject(InternalListenerContract target, Object input) {
      return input;
   }

   default int processCount(SecureLoginGate target) {
      return this.<Integer>processObject(target);
   }

   default double computeRatio(SecureLoginGate target) {
      return this.<Double>processObject(target);
   }

   default short loadCode(SecureLoginGate target) {
      return this.<Short>processObject(target);
   }

   default List<String> buildCollection(Object... target) {
      return this.handleCollection(this.loadSecureLoginGate(), target);
   }

   default Object fetchObject() {
      return this.computeObject(this.loadSecureLoginGate());
   }

   default long getTime() {
      return this.handleTime(this.loadSecureLoginGate());
   }

   default List<?> createCollection(SecureLoginGate target) {
      return this.processObject(target);
   }

   default int retrieveCount() {
      return this.processCount(this.loadSecureLoginGate());
   }

   default List<Integer> buildCollection(SecureLoginGate target) {
      return this.processObject(target);
   }

   default String computeMessage(SecureLoginGate target, Object... input) {
      return OpenLocaleBarrier.handleMessage(this.processObject(target), input);
   }

   default short resolveCode() {
      return this.loadCode(this.loadSecureLoginGate());
   }
}

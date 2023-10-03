package it.uniba.dib.sms22231.utility;

public interface SubscribeCallback<T> {
    void apply(T data, CallbackFunctionVoid unsubscribe);
}

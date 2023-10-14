package it.uniba.dib.sms22231.utility;

public interface RunnableFunction<T> {
    void apply(CallbackFunction<T> data, CallbackFunction<CallbackFunctionVoid> setOnUnsubscribe);
}

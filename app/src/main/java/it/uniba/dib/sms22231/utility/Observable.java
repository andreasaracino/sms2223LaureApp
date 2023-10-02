package it.uniba.dib.sms22231.utility;

import android.telecom.Call;

import java.util.ArrayList;

public class Observable<T> {
    private T data;
    private final ArrayList<Subscription> subscribers = new ArrayList<>();
    private CallbackFunction<CallbackFunction<T>> runnable;

    public Observable() {}

    public Observable(T data) {
        this.data = data;
    }

    public Observable(CallbackFunction<CallbackFunction<T>> runnable) {
        this.runnable = runnable;
    }

    public T getValue() {
        return data;
    }

    public Subscription subscribe(CallbackFunction<T> updateFunction) {
        Subscription subscription = new Subscription(updateFunction);
        subscribers.add(subscription);

        if (runnable != null) {
            runnable.apply(this::next);
        }
        return subscription;
    }

    public void next(T data) {
        this.data = data;
        for (Subscription updateFunction : subscribers) {
            updateFunction.update(data);
        }
    }

    public void reset() {
        data = null;
    }

    public class Subscription {
        private final CallbackFunction<T> updateFunction;

        public Subscription(CallbackFunction<T> updateFunction) {
            this.updateFunction = updateFunction;

            if (data != null) {
                updateFunction.apply(data);
            }
        }

        public void update(T data) {
            updateFunction.apply(data);
        }

        public void unsubscribe() {
            subscribers.remove(this);
        }
    }
}


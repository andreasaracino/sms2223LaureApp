package it.uniba.dib.sms22231.utility;

import android.telecom.Call;

import java.util.ArrayList;

public class Observable<T> {
    private T data;
    private final ArrayList<Subscription> subscribers = new ArrayList<>();
    private CallbackFunction<CallbackFunction<T>> runnable;

    public Observable() {}

    public Observable(CallbackFunction<CallbackFunction<T>> runnable) {
        this.runnable = runnable;
    }

    public T getValue() {
        return data;
    }

    public Subscription subscribe(SubscribeCallback<T> updateFunction) {
        Subscription subscription = new Subscription(updateFunction);
        subscribers.add(subscription);

        if (runnable != null) {
            runnable.apply(this::next);
        }
        return subscription;
    }

    public Subscription subscribe(CallbackFunction<T> updateFunction) {
        return subscribe((data1, unsubscribe) -> updateFunction.apply(data1));
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
        private final SubscribeCallback<T> updateFunction;

        public Subscription(SubscribeCallback<T> updateFunction) {
            this.updateFunction = updateFunction;

            if (data != null) {
                updateFunction.apply(data, this::unsubscribe);
            }
        }

        public void update(T data) {
            updateFunction.apply(data, this::unsubscribe);
        }

        public void unsubscribe() {
            subscribers.remove(this);
        }
    }
}


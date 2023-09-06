package it.uniba.dib.sms22231.utility;

import java.util.ArrayList;

public class Observable<T> {
    private T data;
    private final ArrayList<Subscription> subscribers = new ArrayList<>();

    public Observable(T data) {
        this.data = data;
    }

    public T getValue() {
        return data;
    }

    public Subscription subscribe(CallbackFunction<T> updateFunction) {
        Subscription subscription = new Subscription(updateFunction);
        subscribers.add(subscription);
        return subscription;
    }

    public void next(T data) {
        this.data = data;
        for (Subscription updateFunction : subscribers) {
            updateFunction.update(data);
        }
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


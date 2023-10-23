package it.uniba.dib.sms22231.utility;

import java.util.ArrayList;
import java.util.function.Function;

public class Observable<T> {
    private T data;
    private final ArrayList<Subscription> subscribers = new ArrayList<>();
    private RunnableFunction<T> runnable;

    public Observable() {}

    // costruttore con funzione eseguibile
    public Observable(RunnableFunction<T> runnable) {
        this.runnable = runnable;
    }

    // restituisce il dato corrente
    public T getValue() {
        return data;
    }

    /* viene creata una nuova Subscription da inserire nella lista di Subscription
     * nella subscription viene passata la funzione lambda argomento del metodo subscribe
     * se è definita una funzione runnable, essa viene eseguita passando il metodo d'istanza
     * next() e il metodo d'istanza setOnUnsubscribe dell'oggetto Subscription
     * alla fine viene restituita la nuova Subscription
     */
    public Subscription subscribe(SubscribeCallback<T> updateFunction) {
        Subscription subscription = new Subscription(updateFunction);
        subscribers.add(subscription);

        if (runnable != null) {
            runnable.apply(this::next, subscription::setOnUnsubscribe);
        }
        return subscription;
    }

    // metodo subscribe al quale non viene specificato il passaggio del metodo "unsubscribe"
    public Subscription subscribe(CallbackFunction<T> updateFunction) {
        return subscribe((data1, unsubscribe) -> updateFunction.apply(data1));
    }

    /* metodo che permette l'aggiornamento dell'observable
     * al momento della chiamata di questo metodo viene effettuato un ciclo che aggiorna ogni subscriber
     */
    public void next(T data) {
        this.data = data;
        for (Subscription updateFunction : subscribers) {
            updateFunction.update(data);
        }
    }

    // rende null il dato senza aggiornare i subscriber
    public void reset() {
        data = null;
    }

    // classe interna per la gestione dei subscriber
    public class Subscription {
        private final SubscribeCallback<T> updateFunction;
        private CallbackFunctionVoid onUnsubscribe;

        // costruttore che prende in input la lambda usata per aggiornare il subscriber
        // se il dato corrente è definito viene subito chiamata la lambda di cui sopra per mandare il dato al subscriber
        public Subscription(SubscribeCallback<T> updateFunction) {
            this.updateFunction = updateFunction;

            if (data != null) {
                updateFunction.apply(data, this::unsubscribe);
            }
        }

        // chiamo la funzione di aggiornamento del subscriber
        public void update(T data) {
            updateFunction.apply(data, this::unsubscribe);
        }

        // definisco la funzione da chiamare nel momento in cui viene effettuato l'unsubscribe
        public void setOnUnsubscribe(CallbackFunctionVoid onUnsubscribe) {
            this.onUnsubscribe = onUnsubscribe;
        }

        // rimuovo la Subscription corrente dalla lista
        // se definita, chiamo la funzione onUnsubscribe
        public void unsubscribe() {
            subscribers.remove(this);

            if (onUnsubscribe != null) {
                onUnsubscribe.apply();
            }
        }
    }
}


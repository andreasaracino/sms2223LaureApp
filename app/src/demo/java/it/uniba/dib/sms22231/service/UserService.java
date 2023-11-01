package it.uniba.dib.sms22231.service;

import android.telecom.Call;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import it.uniba.dib.sms22231.config.UserTypes;
import it.uniba.dib.sms22231.model.User;
import it.uniba.dib.sms22231.utility.Observable;
import it.uniba.dib.sms22231.utility.CallbackFunction;

// SERVICE SINGLETON
public class UserService {
    private final List<User> database;
    static UserService instance;    // Istanza singleton
    public final Observable<User> userObservable = new Observable<>();  // Observable contenente i dati dell'utente

    /*
     * i dati aggiornati dell'utente si salvano direttamente nel documento ottenuto in initData
     * a task completato si segnala, attraverso il callback, la buona riuscita dell'operazione al metodo chiamante
     */
    public void saveUserData(User user, CallbackFunction<Boolean> callback) {
        callback.apply(true);
    }

    /*
     * si effettua il login attraverso l'uso di email e password inseriti dall'utente
     * una volta terminata l'operazione viene chiamato initData e successivamente la callback per segnalare l'esito dell'operazione
     */
    public void signIn(String email, String password, CallbackFunction<Boolean> callback) {
        userObservable.next(database.stream().filter(user1 -> user1.email.equals(email)).findFirst().orElse(null));
        callback.apply(userObservable.getValue() != null);
    }

    // accedi come ospite
    public void signInAsGuest() {
        User guest = new User();
        guest.userType = UserTypes.GUEST;
        userObservable.next(guest);
    }

    public User getUserById(String uid) {
        return database.stream().filter(user -> user.uid.equals(uid)).findFirst().orElse(null);
    }

    public boolean isLoggedIn() {
        return getUserData() != null;
    }

    public boolean isEmailVerified() {
        return true;
    }

    // effettua la registrazione con email e password
    public void signUp(String email, String password, CallbackFunction<Boolean> callback) {
        callback.apply(true);
    }

    // elimina l'utente loggato
    public void deleteUser(CallbackFunction<Boolean> callback) {
        callback.apply(true);
    }

    // effettua il logout
    public void signOut() {
        userObservable.reset();
    }

    // richiedi l'email per il reset della password
    public void resetPassword(String email, CallbackFunction<Boolean> callback) {
        callback.apply(true);
    }

    /*
     * metodo per ottenere il valore corrente dell'observable
     */
    public User getUserData() {
        return userObservable.getValue();
    }

    private UserService() {
        database = Arrays.asList(
                new User(
                        "6hEhKQEz5rgUTdcWfUP46w2lasn1",
                        "marcpit85@gmail.com",
                        "Antonio Bianchi",
                        "",
                        UserTypes.STUDENT,
                        true
                ),
                new User(
                        "V1hHe9O2Oge3VpVP1cvmyGIlFqR2",
                        "a.saracino62@studenti.uniba.it",
                        "Andrea Saracino",
                        "369166",
                        UserTypes.STUDENT,
                        true
                ),

                new User(
                        "rf1Y5RWeiyg6NItgdtrptH01pCy2",
                        "m.piteo@studenti.uniba.it",
                        "Marcello Piteo",
                        "",
                        UserTypes.TEACHER,
                        true
                )
        );
    }

    /*
     * metodo statico per la creazione di un'istanza secondo il pattern singleton
     */
    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }

        return instance;
    }
}

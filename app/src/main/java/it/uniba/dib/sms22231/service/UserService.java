package it.uniba.dib.sms22231.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import it.uniba.dib.sms22231.model.User;
import it.uniba.dib.sms22231.utility.Observable;
import it.uniba.dib.sms22231.utility.CallbackFunction;

// SERVICE SINGLETON
public class UserService {
    static UserService instance;
    private FirebaseFirestore db;   // Istanza database Firestore
    private FirebaseAuth mAuth;     // Istanza gestore autenticazione
    private FirebaseUser user;      // Istanza utente corrente
    private DocumentReference userDocument; // Riferimento al documento sul database
    public final Observable<User> userObservable = new Observable<>(null);  // Observable contenente i dati dell'utente

    private UserService() {
        initData();
    }

    /*
    * creazione dell'istanza di FirebaseFirestore
    * creazione dell'istanza di FirebaseAuth
    * se l'utente è loggato, allora ottieni i suoi dati
    */
    private void initData() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user != null) {
            updateDocuments();
        }
    }

    /*
     * accesso alla collection "users" e ottenimento del documento con id uguale a quello dell'utente loggato
     * una volta ottenuti i dati, si controlla se l'operazione è andata a buon fine
     * in tal caso si crea un nuovo oggetto di tipo User in cui viene passato il documento ottenuto
     * successivamente si passano anche UID ed email dell'utente loggato, oltre ad aggiornare l'observable
     */
    private void updateDocuments() {
        userDocument = db.collection("users").document(user.getUid());
        userDocument.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                User userData;

                if (doc.exists()) {
                    userData = new User(Objects.requireNonNull(doc.getData()));
                } else {
                    userData = new User();
                }

                userData.uid = user.getUid();
                userData.email = user.getEmail();
                userObservable.next(userData);
            }
        });
    }

    /*
     * i dati aggiornati dell'utente si salvano direttamente nel documento ottenuto in initData
     * a task completato si segnala, attraverso il callback, la buona riuscita dell'operazione al metodo chiamante
     */
    public void saveUserData(User user, CallbackFunction<Boolean> callback) {
        userDocument.set(user).addOnCompleteListener(task -> callback.apply(task.isSuccessful()));
    }

    /*
     * si effettua il login attraverso l'uso di email e password inseriti dall'utente
     * una volta terminata l'operazione viene chiamato initData e successivamente la callback per segnalare l'esito dell'operazione
     */
    public void signIn(String email, String password, CallbackFunction<Boolean> callback) {
        if (!email.isEmpty() && !password.isEmpty()) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                initData();
                callback.apply(task.isSuccessful());
            });
        } else {
            callback.apply(false);
        }
    }

    /*
     * metodo per ottenere il valore corrente dell'observable
     */
    public User getUserData() {
        return userObservable.getValue();
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

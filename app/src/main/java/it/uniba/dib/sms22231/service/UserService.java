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

public class UserService {
    static UserService instance;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DocumentReference userDocument;
    public final Observable<User> userObservable = new Observable<>(null);

    private UserService() {
        initData();
    }

    private void initData() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user != null) {
            updateDocuments();
        }
    }

    private void updateDocuments() {
        userDocument = db.collection("users").document(user.getUid());
        userDocument.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();

                if (doc.exists()) {
                    User userData = new User(Objects.requireNonNull(doc.getData()));
                    userData.uid = user.getUid();
                    userData.email = user.getEmail();
                    userObservable.next(userData);
                }
            }
        });
    }

    public void saveUserData(User user, CallbackFunction<Boolean> callback) {
        userDocument.set(user).addOnCompleteListener(task -> callback.apply(task.isSuccessful()));
    }

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

    public User getUserData() {
        return userObservable.getValue();
    }

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }

        return instance;
    }
}

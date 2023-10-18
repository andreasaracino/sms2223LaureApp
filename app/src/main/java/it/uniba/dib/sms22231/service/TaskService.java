package it.uniba.dib.sms22231.service;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.stream.Collectors;

import it.uniba.dib.sms22231.model.Task;
import it.uniba.dib.sms22231.utility.CallbackFunction;
import it.uniba.dib.sms22231.utility.Observable;

public class TaskService {
    private static TaskService instance;
    private static final String TASK_COLLECTION = "tasks";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference tasksCollection = db.collection(TASK_COLLECTION);

    public Observable<Task> getTaskById(String taskId) {
        return new Observable<>((next, setOnUnsubscribe) -> {
            tasksCollection.document(taskId).get().addOnCompleteListener(task -> {
                next.apply(mapTask(task.getResult()));
            });
        });
    }

    public Observable<List<Task>> getTasksByApplicationId(String applicationId) {
        return new Observable<>((next, setOnUnsubscribe) -> {
            tasksCollection.whereEqualTo("applicationId", applicationId).get().addOnCompleteListener(task -> {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                List<Task> Tasks = documents.stream().map(TaskService::mapTask).collect(Collectors.toList());

                next.apply(Tasks);
            });
        });
    }

    @NonNull
    private static Task mapTask(DocumentSnapshot rawTask) {
        Task Task = new Task(rawTask.getData());
        Task.id = rawTask.getId();
        return Task;
    }

    public void saveNewTask(Task task, CallbackFunction<Boolean> callback) {
        tasksCollection.add(task.toMap()).addOnCompleteListener(task1 -> callback.apply(task1.isSuccessful()));
    }

    public void updateTask(Task task, CallbackFunction<Boolean> callback) {
        tasksCollection.document(task.id).set(task.toMap()).addOnCompleteListener(task1 -> callback.apply(task1.isSuccessful()));
    }

    public void deleteTask(String taskId, CallbackFunction<Boolean> callback) {
        tasksCollection.document(taskId).delete().addOnCompleteListener(task -> callback.apply(task.isSuccessful()));
    }

    private TaskService() {}

    public static TaskService getInstance() {
        if (instance == null) {
            instance = new TaskService();
        }

        return instance;
    }
}

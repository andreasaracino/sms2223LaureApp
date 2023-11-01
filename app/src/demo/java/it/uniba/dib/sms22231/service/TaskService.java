package it.uniba.dib.sms22231.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import it.uniba.dib.sms22231.config.TaskStatus;
import it.uniba.dib.sms22231.model.Task;
import it.uniba.dib.sms22231.utility.CallbackFunction;
import it.uniba.dib.sms22231.utility.Observable;

public class TaskService {
    private final List<Task> database;
    private static TaskService instance;

    // Ottengo uno specifico task per id
    public Observable<Task> getTaskById(String taskId) {
        return new Observable<>((next, setOnUnsubscribe) -> {
            Optional<Task> taskOptional = database.stream().filter(task -> task.id.equals(taskId)).findAny();
            next.apply(taskOptional.orElse(null));
        });
    }

    // Ottengo i task per id dell'Application
    public Observable<List<Task>> getTasksByApplicationId(String applicationId) {
        return new Observable<>((next, setOnUnsubscribe) -> {
            List<Task> tasks = database.stream().filter(task -> task.applicationId.equals(applicationId)).collect(Collectors.toList());
            next.apply(tasks);
        });
    }

    // Salvataggio nuovo task
    public void saveNewTask(Task task, CallbackFunction<Boolean> callback) {
        callback.apply(true);
    }

    // Aggiornamento task esistente
    public void updateTask(Task task, CallbackFunction<Boolean> callback) {
        callback.apply(true);
    }

    // eliminazione task
    public void deleteTask(String taskId, CallbackFunction<Boolean> callback) {
        callback.apply(true);
    }

    private TaskService() {
        database = Arrays.asList(new Task("7rX41QstWVixX6iwPuBW", TaskStatus.open, "Task 1 - Saracino", "Introduzione alla tesi", "X7vND9g2r5o3GSHW5iDx", new Date(1699458032000L)));
    }

    public static TaskService getInstance() {
        if (instance == null) {
            instance = new TaskService();
        }

        return instance;
    }
}

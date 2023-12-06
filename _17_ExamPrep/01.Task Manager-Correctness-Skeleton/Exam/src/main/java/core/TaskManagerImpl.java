package core;

import models.Task;

import java.util.*;
import java.util.stream.Collectors;

public class TaskManagerImpl implements TaskManager {

    LinkedHashMap<String, Task> tasksById = new LinkedHashMap<>();
    LinkedHashSet<Task> pendingTasks = new LinkedHashSet<>();
    Map<String, Task> executedTasks = new HashMap<>();
    @Override
    public void addTask(Task task) {
        tasksById.put(task.getId(), task);
        pendingTasks.add(task);
    }

    @Override
    public boolean contains(Task task) {
        return getTaskById(task.getId()) != null;
    }

    @Override
    public int size() {
        return tasksById.size();
    }

    @Override
    public Task getTask(String taskId) {
        Task searched = getTaskById(taskId);
        if (searched == null) {
            throw new IllegalArgumentException();
        }
        return searched;
    }

    @Override
    public void deleteTask(String taskId) {
        Task removed = tasksById.remove(taskId);
        if (removed == null) {
            throw new IllegalArgumentException();
        }

        //set
        pendingTasks.remove(removed);
        //map
        executedTasks.remove(taskId);
    }

    @Override
    public Task executeTask() {
        if (pendingTasks.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Iterator<Task> iterator = pendingTasks.iterator();
        Task firstTask = iterator.next();
        iterator.remove();

        executedTasks.put(firstTask.getId(), firstTask);

        return firstTask;
    }

    @Override
    public void rescheduleTask(String taskId) {
        Task executed = executedTasks.remove(taskId);
        if (executed == null) {
            throw new IllegalArgumentException();
        }

        pendingTasks.add(executed);
    }

    @Override
    public Iterable<Task> getDomainTasks(String domain) {
        List<Task> pendingTasksByDomain = new ArrayList<>();
        for (Task pendingTask : pendingTasks) {
            if (pendingTask.getDomain().equals(domain)) {
                pendingTasksByDomain.add(pendingTask);
            }
        }

        if (pendingTasksByDomain.isEmpty()) {
            throw new IllegalArgumentException();
        }

        return pendingTasksByDomain;
    }

    @Override
    public Iterable<Task> getTasksInEETRange(int lowerBound, int upperBound) {
        return pendingTasks.stream()
                .filter(p -> p.getEstimatedExecutionTime() >= lowerBound && p.getEstimatedExecutionTime() <= upperBound)
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Task> getAllTasksOrderedByEETThenByName() {

        return tasksById.values().stream()
                .sorted(Comparator.comparing(Task::getEstimatedExecutionTime, Comparator.reverseOrder())
                        .thenComparing(t -> t.getName().length()))
                .collect(Collectors.toList());
    }

    private Task getTaskById(String taskId) {
        return tasksById.get(taskId);
    }
}

package fi.hel.allu.model.domain;

import fi.hel.allu.model.domain.user.User;

import java.util.List;

public class UpdateTaskOwners {

    private List<Integer> taskIds;
    private User newUser;

    public List<Integer> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<Integer> taskIds) {
        this.taskIds = taskIds;
    }

    public User getNewUser() {
        return newUser;
    }

    public void setNewUser(User newUser) {
        this.newUser = newUser;
    }
}
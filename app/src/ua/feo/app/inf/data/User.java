package ua.feo.app.inf.data;

import ua.feo.app.inf.task.TargetInf;

import java.util.HashMap;
import java.util.Map;

public class User {

    public final static User NULL_USER = new User("", "", new HashMap<>());

    public final String name;
    public final String password;
    private final Map<TargetInf, TargetStatus> targets;

    public User(String name, String password, Map<TargetInf, TargetStatus> targets) {
        this.name = name;
        this.password = password;
        this.targets = targets;
    }

    public void setStatusOfTarget(TargetInf target, TargetStatus targetStatus) {
        targets.put(target, targetStatus);
    }

    public TargetStatus statusOfTarget(TargetInf target) {
        TargetStatus targetStatus = targets.get(target);
        return targetStatus == null ? TargetStatus.Disabled : targetStatus;
    }

    public boolean targetIsFailed(TargetInf target) {
        return statusOfTarget(target) == TargetStatus.Failed;
    }

    public boolean targetIsCompleted(TargetInf target) {
        return statusOfTarget(target) == TargetStatus.Completed;
    }

    public boolean targetIsDisabled(TargetInf target) {
        return statusOfTarget(target) == TargetStatus.Disabled;
    }

    public boolean targetIsEnabled(TargetInf target) {
        return statusOfTarget(target) == TargetStatus.Enabled;
    }

}

package ua.feo.app.data;

import javafx.stage.Stage;
import javafx.util.Pair;
import ua.feo.app.inf.StageRouter;
import ua.feo.app.inf.TaskRouter;
import ua.feo.app.task.*;
import ua.feo.md5.MD5;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class ProgramData implements Externalizable  {

    public static final ProgramData NULL_PROGRAM_DATA = new ProgramData();

    private static ProgramData programData;

    public static ProgramData get() {
        if (programData == null) {
            ProgramData data = Serializator.restore();
            programData = data == NULL_PROGRAM_DATA ? new ProgramData() : data;
        }
        return programData;
    }

    private Stage stage;
    private StageRouter stageRouter;
    private TaskRouter taskRouter;

    private int currentTarget;
    private List<TargetInf> targetList = new ArrayList<TargetInf>(){{
        add(new Target1());
        add(new Target2());
        add(new Target3());
        add(new Target4());
        add(new Target5());
        add(new Target6());
        add(new Target7());
        add(new Target8());
        add(new Target9());
        add(new Target10());
        add(new Target11());
        add(new Target12());
        add(new Target13());
        add(new Target14());
        add(new Target15());
        add(new Target16());
    }};

    private User currentUser = User.NULL_USER;
    private List<User> userList;

    private Map<String, Boolean> testData = new HashMap<>();

    public ProgramData() {
        userList = new ArrayList<>();
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stageRouter = new StageRouter(stage);
        this.taskRouter = new TaskRouter(new Stage());
        this.stage = stage;
    }

    public StageRouter getStageRouter() {
        return stageRouter;
    }

    public TaskRouter getTaskRouter() {
        return taskRouter;
    }

    public boolean registerUser(String name, String password) {
        if (loginUser(name, password)) return false;
        User user = new User(name, MD5.getMD5(password), new HashMap<>());
        for (int i = 0; i < targetList.size(); i++) {
            user.setStatusOfTarget(targetList.get(i), i == 0 ? TargetStatus.Enabled : TargetStatus.Disabled);
        }
        userList.add(user);
        return loginUser(name, password);
    }

    public boolean loginUser(String name, String password) {
        for (User user : userList) {
            if (name.equals(user.name) && MD5.getMD5(password).equals(user.password)) {
                currentUser = user;
                return true;
            }
        }
        return false;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public List<TargetInf> getTargetList(){
        return targetList;
    }

    public List<String> getTargetNameList(){
        return targetList.stream().map(t -> t.getName()).collect(Collectors.toList());
    }

    public int getIndexCurrentTarget() {
        return currentTarget;
    }

    public void setCurrentTarget(int target) {
        this.currentTarget = target;
    }

    public TargetInf getCurrentTarget() {
        return targetList.get(currentTarget);
    }

    public boolean hasNextTarget() {
        return currentTarget + 1 < targetList.size();
    }

    public void setNextCurrentTarget() {
        if (currentTarget + 1 < targetList.size()) {
            this.currentTarget++;
        }
    }

    public void setTestData(Map<String, Boolean> testData) {
        this.testData = testData;
    }

    public Pair<Map<String, Boolean>, Map<String, Boolean>> getTestData() {
        return new Pair<>(getCurrentTarget().getEtalonTestCaseList(), testData);
    }

    public int getResult() {
        Collection<Boolean> etalon = getCurrentTarget().getEtalonTestCaseList().values();
        Collection<Boolean> data = testData.values();
        int result = Etalon.compareWithEtalon(etalon.toArray(new Boolean[etalon.size()]), data.toArray(new Boolean[data.size()]));
        if (result == 100) {
            currentUser.setStatusOfTarget(targetList.get(currentTarget), TargetStatus.Completed);
            if (hasNextTarget() && currentUser.targetIsDisabled(targetList.get(currentTarget + 1))) {
                currentUser.setStatusOfTarget(targetList.get(currentTarget + 1), TargetStatus.Enabled);
            }
        } else {
            currentUser.setStatusOfTarget(targetList.get(currentTarget), TargetStatus.Failed);
        }
        return result;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.write(userList.size());
        for (User u : userList) {
            out.writeObject(u.name);
            out.writeObject(u.password);
            for (TargetInf target : targetList) {
                out.write(u.statusOfTarget(target).i);
            }
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int userCount = in.read();
        userList = new ArrayList<>(userCount);
        for (int i = 0; i < userCount; i++) {
            String name = (String) in.readObject();
            String password = (String) in.readObject();
            User user = new User(name, password, new HashMap<>());
            for (TargetInf target : targetList) {
                int ii = in.read();
                TargetStatus ts = TargetStatus.get(ii);
                user.setStatusOfTarget(target, ts);
            }
            userList.add(user);
        }
    }
}

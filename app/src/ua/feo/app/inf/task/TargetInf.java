package ua.feo.app.inf.task;

import javafx.util.Pair;

import java.util.List;
import java.util.Map;

public interface TargetInf {

    String getName();

    List<String> getTestCaseList();

    Map<String, Boolean> getEtalonTestCaseList();

}

package ua.feo.app.task;

import java.util.List;
import java.util.Map;

public interface TargetInf {

    String getName();

    List<String> getTestCaseList();

    Map<String, Boolean> getEtalonTestCaseList();

}

package ua.feo.app.task;

import ua.feo.app.inf.Window;

import java.util.List;
import java.util.Map;

public interface TargetInf extends Window {

    String getName();

    List<String> getTestCaseList();

    Map<String, Boolean> getEtalonTestCaseList();

}

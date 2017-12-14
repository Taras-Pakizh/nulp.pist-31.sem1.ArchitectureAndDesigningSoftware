package ua.feo.app.data;

import java.util.Arrays;

public enum TargetStatus {

    Disabled(0), Enabled(1), Completed(2), Failed(3);

    final int i;

    TargetStatus(int i) {
        this.i = i;
    }

    static TargetStatus get(int i) {
        return Arrays.stream(values()).filter(ts -> ts.i == i).findFirst().orElse(Disabled);
    }

}

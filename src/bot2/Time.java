package bot2;

import java.util.LinkedList;
import java.util.List;

public class Time {

    private long startTime;
    private long lastTime;
    private List<TimeAction> actions = new LinkedList<TimeAction>();
    public static final Time time = new Time();

    class TimeAction {
        String type;
        String act;
        long duration;

        TimeAction(String type, String act, long duration) {
            this.type = type;
            this.act = act;
            this.duration = duration;
        }
    }

    public void startTurn() {
        this.startTime = System.currentTimeMillis();
        lastTime = startTime;
        actions.clear();
    }

    public void completed(String action) {
        completed(action, action);
    }
    public void completed(String type, String action) {
        long newTime = System.currentTimeMillis();
        long duration = newTime - lastTime;
        lastTime = newTime;
        actions.add(new TimeAction(type, action, duration));
    }

    public void logTime() {
        Logger.logTime(actions, System.currentTimeMillis() - startTime);
    }

}

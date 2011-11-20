package bot.brain;

import bot.Ants;

import java.util.concurrent.TimeoutException;

public class TimeGuard {

    private long maxActionTime = 0;
    private long currentTime;
    private Ants info;

    public TimeGuard(Ants info) {
        this.info = info;
    }

    public void newAction() {
        maxActionTime = 0;
        currentTime = System.currentTimeMillis();
    }

    public void actionStep() throws TimeoutException {
        long time = System.currentTimeMillis() - currentTime;
        if (time > maxActionTime) {
            maxActionTime = time;
        }
        if (info.getTimeRemaining() < 2*maxActionTime) {
            throw new TimeoutException();
        }
    }

}

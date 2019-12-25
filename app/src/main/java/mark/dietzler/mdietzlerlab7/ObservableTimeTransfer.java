package mark.dietzler.mdietzlerlab7;

import java.util.Observable;

public class ObservableTimeTransfer extends Observable {

    public TimeContainer container = new TimeContainer();

    public String getTime() {
        return "";
    }


    public void setTime(int hour, int minute, int seconds, int milliseconds) {
        //container = new TimeContainer();
        container.setHour(hour);
        container.setMinute(minute);
        container.setSeconds(seconds);
        container.setMilliseconds(milliseconds);
        setChanged();
        notifyObservers();
    }

}
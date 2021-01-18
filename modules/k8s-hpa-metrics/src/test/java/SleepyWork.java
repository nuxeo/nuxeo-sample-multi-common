import org.nuxeo.ecm.core.work.SleepWork;

public class SleepyWork extends SleepWork {

	public SleepyWork(long durationMillis) {
		super(durationMillis);
	}

	@Override
	protected void doWork() throws InterruptedException {
		System.out.println("Working!!!");
		super.doWork();
	}

}


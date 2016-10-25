import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.time.StopWatch;

public class Application {

	static class Test {
		public int x = 0;
		public int y = 0;
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		int[] values = new int[100_000_000];

		Random random = new Random(10000000);
		for (int i = 0; i < values.length; i++) {
			values[i] = random.nextInt();

		}

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		// Quicksort.sort(values, new CurrentThreadExecutor());
		Quicksort.sort(values, Executors.newWorkStealingPool(), 100);
		stopWatch.stop();

		System.out.println(stopWatch.getTime());
	}

	public static void temp() throws InterruptedException {
		AtomicReference<Test> test = new AtomicReference<>(new Test());
		CountDownLatch barrer = new CountDownLatch(2);

		Runnable task1 = () -> {
			int counter = 0;
			while (counter < 10000) {
				Test expected = test.get();

				Test updated = new Test();
				updated.x = expected.x + 1;
				updated.y = expected.y + 1;

				if (!test.compareAndSet(expected, updated))
					continue;

				counter++;
			}

			barrer.countDown();
		};

		Thread thread1 = new Thread(task1);
		Thread thread2 = new Thread(task1);

		thread1.start();
		thread2.start();

		barrer.await();

		System.out.println(test.get().x);
		System.out.println(test.get().y);
	}
}

class CurrentThreadExecutor implements Executor {
	public void execute(Runnable r) {
		r.run();
	}
}

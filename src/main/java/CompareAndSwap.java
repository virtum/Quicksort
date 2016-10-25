import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class CompareAndSwap {

	static class Test {
		public int x = 0;
		public int y = 0;
	}

	public static void main(String[] args) throws InterruptedException {
		AtomicReference<Test> test = new AtomicReference<>(new Test());
		CountDownLatch barrer = new CountDownLatch(2);

		Runnable task1 = () -> {
			int counter = 0;
			while (counter < 10000) {
				Test expected = test.get();

				Test updated = new Test();
				updated.x = expected.x + 1;
				updated.y = expected.y + 1;

				if (!test.compareAndSet(expected, updated)) {
					continue;
				}

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

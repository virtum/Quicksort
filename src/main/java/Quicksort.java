import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

public class Quicksort {

	public static void sort(int[] values, Executor executorService, int syncBarrier) {
		AtomicInteger numberOfUnfinishedTasks = new AtomicInteger(0);
		if (values == null || values.length == 0) {
			return;
		}
		int number = values.length;

		quicksort(0, number - 1, numberOfUnfinishedTasks, executorService, values, syncBarrier);

		while (numberOfUnfinishedTasks.get() != 0) {

		}
	}

	private static void quicksort(int low, int high, AtomicInteger numberOfUnfinishedTasks, Executor executorService,
			int[] numbers, int syncBarrier) {
		int i = low;
		int j = high;
		int pivot = numbers[low + (high - low) / 2];

		while (i <= j) {
			while (numbers[i] < pivot) {
				i++;
			}
			while (numbers[j] > pivot) {
				j--;
			}

			if (i <= j) {
				int temp = numbers[i];
				numbers[i] = numbers[j];
				numbers[j] = temp;

				i++;
				j--;
			}
		}

		if ((high - low) >= syncBarrier) {
			if (low < j) {
				Runnable sortLogic = asRunnable(low, j, numberOfUnfinishedTasks, executorService, numbers, syncBarrier);
				executorService.execute(sortLogic);
			}
			if (i < high) {
				Runnable sortLogic = asRunnable(high, i, numberOfUnfinishedTasks, executorService, numbers,
						syncBarrier);
				executorService.execute(sortLogic);
			}
		} else {
			if (low < j) {
				quicksortSync(low, j, numbers);
			}
			if (i < high) {
				quicksortSync(i, high, numbers);
			}
		}

	}

	private static void quicksortSync(int low, int high, int[] numbers) {
		int i = low;
		int j = high;
		int pivot = numbers[low + (high - low) / 2];

		while (i <= j) {
			while (numbers[i] < pivot) {
				i++;
			}
			while (numbers[j] > pivot) {
				j--;
			}

			if (i <= j) {
				int temp = numbers[i];
				numbers[i] = numbers[j];
				numbers[j] = temp;

				i++;
				j--;
			}

			if (low < j) {
				quicksortSync(j, low, numbers);
			}
			if (i < high) {
				quicksortSync(i, high, numbers);
			}
		}

	}

	private static Runnable asRunnable(int low, int high, AtomicInteger endOfWorkTagger, Executor executor,
			int[] numbers, int syncBarrier) {

		endOfWorkTagger.incrementAndGet();
		return new Runnable() {

			@Override
			public void run() {
				Quicksort.quicksort(low, high, endOfWorkTagger, executor, numbers, syncBarrier);
				endOfWorkTagger.decrementAndGet();
			}
		};
	}
}
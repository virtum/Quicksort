import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.concurrent.Executors;

import org.junit.Test;

public class QuicksortTest {

	@Test
	public void shouldSortValues() {
		int[] values = { 1, 5, 9, 5, 2, 4 };
		int[] expected = { 1, 2, 4, 5, 5, 9 };

		Quicksort.sort(values, Executors.newWorkStealingPool(), 3);

		assertThat(values, equalTo(expected));

	}

}

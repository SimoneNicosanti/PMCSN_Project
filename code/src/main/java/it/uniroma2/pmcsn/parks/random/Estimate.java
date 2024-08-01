package it.uniroma2.pmcsn.parks.random;

import java.util.List;

public class Estimate {

	static final double LOC = 0.95; /* level of confidence, */
	/* use 0.95 for 95% confidence */

	// public static void main(String[] args) {
	// long n = 0; /* counts data points */
	// double sum = 0.0;
	// double mean = 0.0;
	// double data;
	// double stdev;
	// double u, t, w;
	// double diff;

	// String line = "";

	// Rvms rvms = new Rvms();

	// BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	// try {
	// line = br.readLine();

	// while (line != null) { /* use Welford's one-pass method */
	// StringTokenizer tokenizer = new StringTokenizer(line);
	// if (tokenizer.hasMoreTokens()) {
	// data = Double.parseDouble(tokenizer.nextToken());

	// n++; /* and standard deviation */
	// diff = data - mean;
	// sum += diff * diff * (n - 1.0) / n;
	// mean += diff / n;
	// }

	// line = br.readLine();

	// }
	// } catch (IOException e) {
	// System.err.println(e);
	// System.exit(1);
	// }

	// stdev = Math.sqrt(sum / n);

	// // DecimalFormat df = new DecimalFormat("###0.00");

	// if (n > 1) {
	// u = 1.0 - 0.5 * (1.0 - LOC); /* interval parameter */
	// t = rvms.idfStudent(n - 1, u); /* critical value of t */
	// w = t * stdev / Math.sqrt(n - 1); /* interval half width */

	// // System.out.print("\nbased upon " + n + " data points");
	// // System.out.print(" and with " + (int) (100.0 * LOC + 0.5) +
	// // "% confidence\n");
	// // System.out.print("the expected value is in the interval ");
	// // System.out.print(df.format(mean) + " +/- " + df.format(w) + "\n");
	// } else {
	// System.out.print("ERROR - insufficient data\n");
	// }
	// }

	// Increment levelOfConfidence for bigger intervals
	public static Double computeConfidenceInterval(List<Double> valueList, Double levelOfConfidence) {
		long n = 0; /* counts data points */
		double sum = 0.0;
		double mean = 0.0;
		double stdev;
		double u, t, w = 0.0;
		double diff;

		Rvms rvms = new Rvms();

		for (Double data : valueList) {
			n++; /* and standard deviation */
			diff = data - mean;
			sum += diff * diff * (n - 1.0) / n;
			mean += diff / n;
		}

		stdev = Math.sqrt(sum / n);

		if (n > 1) {
			u = 1.0 - 0.5 * (1.0 - levelOfConfidence); /* interval parameter */
			t = rvms.idfStudent(n - 1, u); /* critical value of t */
			w = t * stdev / Math.sqrt(n - 1); /* interval half width */
		} else {
			System.out.print("ERROR - insufficient data\n");
		}

		return Double.valueOf(w);
	}
}

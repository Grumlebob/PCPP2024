package lecture08;
class MeanVar {
	public static void main(String[] args) {
    //System.out.println(Math.pow(0.6, 15));
		double[] a= { 30.7, 30.3, 30.1, 30.7, 50.2, 30.4, 30.9, 30.3, 30.5, 30.8 };
		//double[] a= { 30.7, 30.3, 30.1, 30.7, 50.2, 30.4, 30.9, 30.3, 30.5, 30.8, 25 };
		//double[] a= { 30.7, 100.2, 30.1, 30.7, 20.2, 30.4, 2, 30.3, 30.5, 5.4 };
		//double[] a= { 30.7, 100.2, 30.1, 30.7, 20.2, 30.4, 2, 30.3, 30.5, 5.4, 25 };
		double st= 0;
		double sst= 0;
		int n= a.length; //System.out.println(n);
		for (int j= 0; j<n; j++) {
			st += a[j]; //System.out.println(st);
			sst += a[j] * a[j];
		}
		double mean = st/n, sdev = Math.sqrt((sst - mean*mean*n)/(n-1));
		System.out.printf("On-line alg %6.1f ns +/- %6.3f%n", mean, sdev);

    //Using formula in Benchmark note
    double sd= 0;
    for (int j= 0; j<n; j++) {
      double temp= (a[j]- mean);
      sd+= temp*temp;
    }
    sdev= Math.sqrt(sd/(n-1));
    System.out.printf("Two pass alg %6.1f ns +/- %6.3f%n", mean, sdev);
	}
}
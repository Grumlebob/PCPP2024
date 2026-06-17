package lecture09;

import java.util.*;

class ProblemHeap {
    private List<Problem> heap= new LinkedList<Problem>();

    private int nrWaitingThreads = 0;
    private final int nrOfWorkerThreads;

    public ProblemHeap(int nrOfWorkerThreads) {
	this.nrOfWorkerThreads = nrOfWorkerThreads;
    }

    public synchronized void add(Problem newProblem) {	
	heap.add(newProblem);
	if (nrWaitingThreads > 0)
	    this.notify();
    }

    public synchronized Problem getProblem() throws InterruptedException {
	while (heap.size() == 0 && nrWaitingThreads < nrOfWorkerThreads) {
	    nrWaitingThreads++;
	    if(nrWaitingThreads==nrOfWorkerThreads) {
		this.notifyAll();
	    } else {
		this.wait();
		nrWaitingThreads--;
	    }
	}

	if (nrWaitingThreads==nrOfWorkerThreads)
	    return null;
	else
	    return heap.remove(0);
    }
}

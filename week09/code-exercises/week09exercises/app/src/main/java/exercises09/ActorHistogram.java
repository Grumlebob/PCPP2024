package exercises09;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;

public class ActorHistogram implements Histogram {

    private final BinActor[] binActors;
    private final int span;
    private final int contentionLevel;

    //Actor server.
    public ActorHistogram(int span, int contentionLevel) {
        this.span = span;
        this.contentionLevel = contentionLevel;
        this.binActors = new BinActor[span];
        for (int i = 0; i < span; i++) {
            binActors[i] = new BinActor(contentionLevel);
        }
    }

    @Override
    public void increment(int bin) {
        SimulateCPUWork(contentionLevel);
        binActors[bin].increment();
    }

    @Override
    public int getCount(int bin) {
        return binActors[bin].getCount();
    }

    @Override
    public int getSpan() {
        return span;
    }

    public void shutdown() {
        for (BinActor actor : binActors) {
            actor.shutdown();
        }
    }

    // Each bin is managed by one "actor": a thread + mailbox.
    private static class BinActor {
        // Queue of messages to be processed
        private final BlockingQueue<Message> mailbox;
        // The actor thread
        private final Thread thread;
        private volatile int count; // local count for this bin
        private int contentionLevel;

        BinActor(int contentionLevel) {
            this.contentionLevel = contentionLevel;
            mailbox = new LinkedBlockingQueue<>();
            thread = new Thread(this::runActor, "BinActorThread-" + hashCode());
            thread.start();
        }

        void runActor() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Message msg = mailbox.take();
                    // SimulateContention(contentionLevel);
                    if (msg instanceof IncrementMessage) {
                        count++;
                    } else if (msg instanceof GetCountMessage) {
                        GetCountMessage gMsg = (GetCountMessage) msg;
                        gMsg.future.complete(count);
                    } else if (msg instanceof StopMessage) {
                        return;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        void increment() {
            mailbox.offer(new IncrementMessage());
        }

        int getCount() {
            //Future holds the count valued returned to the server.
            CompletableFuture<Integer> future = new CompletableFuture<>();
            mailbox.offer(new GetCountMessage(future));
            try {
                return future.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return 0;
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        void shutdown() {
            mailbox.offer(new StopMessage());
            thread.interrupt();
        }
    }

    // Message types
    private interface Message { }

    private static class IncrementMessage implements Message { }

    private static class StopMessage implements Message { }

    private static class GetCountMessage implements Message {
        final CompletableFuture<Integer> future;
        GetCountMessage(CompletableFuture<Integer> future) {
            this.future = future;
        }
    }
}

package exercises03;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class Person {

    //Variables are private to prevent ESCAPING

    //Shared global state
    private static long idCounter = 0;

    //Shared state
    private final long id;
    private String name;
    private int zip;
    private String address;

    public Person() {
        //Lock globally the idCounter, so we can safely increment it
        synchronized (Person.class) {
            id = idCounter++;
        }
    }

    public Person(long initialId) {
        //from assignment "In case the constructor is used to create the first instance of Person, the initial parameter
        //must be used."
        synchronized (Person.class) {
            if (idCounter == 0) {
                idCounter = initialId;
            }
            id = idCounter++;
        }
    }

    //• It must be possible to change zip and address together.
    public synchronized void setZipAndAddress(int zip, String address) {
        this.zip = zip;
        this.address = address;
    }

    //behøver ikke synchronized, da id er final
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    public int getZip() {
        return zip;
    }

    public String getAddress() {
        return address;
    }


    //main
    public static void main(String[] args) throws InterruptedException {

        AtomicReference<Person> sharedPerson = new AtomicReference<>();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                Person p1 = new Person(1005);
                p1.setName("Jacob");
                p1.setZipAndAddress(2630, "Høje Tåstrup");
                System.out.println("Person 1: " + p1.getId() + " " + p1.getName() + " " + p1.getZip() + " " + p1.getAddress());

                //If last iteration, try to touch the shared person
                if (i == 9) {
                    sharedPerson.set(new Person(1005));
                    sharedPerson.get().setName("Last Jacob");
                    sharedPerson.get().setZipAndAddress(2630, "Høje Tåstrup");
                    System.out.println("Shared Person: " + sharedPerson.get().getId() + " " + sharedPerson.get().getName() + " " + sharedPerson.get().getZip() + " " + sharedPerson.get().getAddress());
                }
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                Person p2;
                //rng either 0 or 1
                Random rng = new Random();
                int random = rng.nextInt(2);
                if (random % 2 == 0) {
                    p2 = new Person(2005);
                }
                else {
                    p2 = new Person();
                }
                p2.setName("Thor");
                p2.setZipAndAddress(2625, "Albertslund");

                System.out.println("Person 2: " + p2.getId() + " " + p2.getName() + " " + p2.getZip() + " " + p2.getAddress());

                //If last iteration, try to touch the shared person
                if (i == 9) {
                    sharedPerson.set(new Person(1005));
                    sharedPerson.get().setName("Last Thor");
                    sharedPerson.get().setZipAndAddress(2625, "Slunden");
                    System.out.println("Shared Person: " + sharedPerson.get().getId() + " " + sharedPerson.get().getName() + " " + sharedPerson.get().getZip() + " " + sharedPerson.get().getAddress());
                }
            }
        });

        t2.start();
        t1.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}

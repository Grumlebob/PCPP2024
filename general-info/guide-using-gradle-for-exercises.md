# Mini-guide on using Gradle for exercises

We use the build tool [Gradle](https://gradle.org/) for compiling and running the code in this course.
This is because Gradle offers many advantages: easy and uniform way to import libraries, uniform compilation and execution environment in different OSs, and compatibility with popular IDEs. We require Gradle version 7.2 or higher.

Furthermore, we will use Java version 8 or higher up to version 17.

Below we explain how to install Gradle and the Java Development Kit (JDK), run Gradle projects and create your own Gradle project to write code from scratch. We describe the process for 3 OSs: Ubuntu 22.04, Windows 10 and MacOS. Most likely this guide applies to other versions of these OSs, and to other Linux distributions. The guide focuses on Gradle 7.2 and JDK 17, but the steps below should work for other versions. If you have problems following these steps do not hesitate to contact us.

## Installing Java JDK 17 and Gradle 7.2

1. Download and install java JDK 17 (see below, if you already have Java installed). We recommend Openjdk (e.g., version 17 GA (build 17+35) in  [https://jdk.java.net/archive/](https://jdk.java.net/archive/)).
   - <u>Ubuntu 22.04</u>: 
	 1. `$ sudo apt install openjdk-XXX-jdk` with `XXX` ≥ 8 (e.g., `openjdk-17-jdk`).
   - <u>Windows 10</u>:
     1. Download zip from [https://jdk.java.net/archive/](https://jdk.java.net/archive/).
     2. Unzip in an appropriate directory. Let's denote it as `/path/to/jdk/`.
     3. Add to your `Path` variable the path to the `bin` folder, i.e., `/path/to/jdk/bin`.
		1. Open "Edit the system environment variables".
		2. Select "Environment Variables...".
        3. Double-click in the `Path` variable.
        4. Click "New".
		5. Add the path to unzipped `bin` folder, i.e., `/path/to/jdk/bin` (replace `/path/to/jdk/bin` with the actual path to the folder you unzipped).
   - <u>MacOS</u>:
     1. Download zip from [https://jdk.java.net/archive/](https://jdk.java.net/archive/).
     2. Unzip in an appropriate directory. Let's denote it as `/path/to/jdk`
     3. Append `/Contents/Home` to the path above (i.e., `/path/to/jdk/Contents/Home`) and set this as your `JAVA_HOME` path.
		1. We explain how to set up this variable using `zsh`. 
		   Edit `$HOME/.zshrc` to include `export JAVA_HOME=/path/to/jdk/Contents/Home`. You may need to create this file if it does not exist.
		   Remember that, for changes to take effect, you should start a new terminal or run `source ~/.zshrc`.

2. Download and install Gradle 7.2 (see [https://gradle.org/install/](https://gradle.org/install/) | Installing Manually)
   
   - <u>Ubuntu 22.04</u>
	 1. Download Gradle 7.2 from [https://gradle.org/releases/](https://gradle.org/releases/).
	 2. Unzip in a directory of your choice. Let's denote it as `/path/to/gradle/`.
	 3. Set your path variable to include the `bin` directory.
		 1. Edit `$HOME/.bashrc` with `export PATH=/path/to/gradle/bin:$PATH` (replace `/path/to/gradle/bin` with the actual path to the folder on your machine).
		    Remember that, for changes to take effect, you should start a new terminal or run `source ~/.bashrc`.
   - <u>Windows 10</u>
	 1. Download Gradle 7.2 from [https://gradle.org/releases/](https://gradle.org/releases/).
	 2. Unzip in a directory of your choice.
	 3. Set your path variable to include the `bin` directory
		 1. Open "Edit the system environment variables".
		 2. Select "Environment Variables...".
		 3. Double-click in the `Path` variable.
		 4. Click "New", 
		 5. Add the path to unzipped `bin` folder.
   - <u>MacOS</u>: (Alternatively, if you use brew, you can install gradle with `brew install gradle` and skip the steps below)
	 1. Download Gradle 7.2 from [https://gradle.org/releases/](https://gradle.org/releases/).
	 2. Unzip in a directory of your choice. Let's denote it as `/path/to/gradle/`.
	 3. Append `/bin` to the path above (i.e., `/path/to/gradle/bin`) and add this to your PATH.
		 1. Edit `$HOME/.zshrc` with `export PATH=/path/to/gradle/bin:$PATH` (replace `/path/to/gradle/bin` with the actual path to the folder on your machine). Remember that, for changes to take effect, you should start a new terminal or run `source ~/.bashrc`.


### Adjusting path variables (Windows) if you already have Java installed

If you already have Java installed:
1. Check that it uses JDK 8 or higher up to version 17.
2. Check that the environment variable `JAVA_HOME` is set correctly as explained in the guide: https://explainjava.com/set-java-path-and-java-home-windows/


## Running the exercises in Gradle

1. Clone the course repository `$ git clone https://github.itu.dk/jst/PCPP2023-public.git`
2. Navigate to the exercises folder for first week, e.g., `$ cd week01/code-exercises/week01exercises/`.
3. Execute the desired program by running `$ gradle -PmainClass=<package>.<java_class> run`, e.g., `$ gradle -PmainClass=exercises01.TestLongCounterExperiments run`.
   - Note that `<java_class>` should include a `main()` method.
   - This step implicitly compiles the code. You can compile only (without executing) by running `$ gradle compileJava`. No need to specify a class; this command compiles all files in `app/src/main/java`.

### Windows PowerShell

If you use Windows PowerShell to run Grade, make sure that it interprets the `Pmainclass` flag correctly by using quotation marks (`""`) around it.
For instance, to run the code for the first week above, you should modify the command above as follows:

`$ gradle "-PmainClass=exercises01.TestLongCounterExperiments" run`.

In general, for an arbitrary package and class you should run `$ gradle "-PmainClass=<package>.<java_class>" run` in Windows PowerShell.


## Create a Gradle project of your own

Sometimes you may want to start a Gradle project from scratch. To this end, we recommend following this official Gradle documentation [https://docs.gradle.org/current/samples/sample_building_java_applications.html](https://docs.gradle.org/current/samples/sample_building_java_applications.html).

In summary, when running `$ gradle init`, you should select:

- Type of project: `2. application`
- Implementation language: `3. Java`
- Functionality across multiple subprojects: `1. no - only...`
- Build script DSL: `1. Groovy`
- Test framework: `1. JUnit 4`

Finally, select project name and source package of your choice.

### Minor differences in running the project

Note that in the official Gradle guide they suggest to run the project using `$ ./gradlew run`. This is equivalent to our command above. So you should be able to run the new project using `gradle run`.
Note also that in this newly created project it is not necessary to specify the flag `PmainClass`. This is because the project has only one entry point in the automatically generated class.


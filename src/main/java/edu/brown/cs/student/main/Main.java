package edu.brown.cs.student.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.ImmutableMap;

import freemarker.template.Configuration;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

/**
 * The Main class of our project. This is where execution begins.
 */
public final class Main {

  // use port 4567 by default when running server
  private static final int DEFAULT_PORT = 4567;

  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private String[] args;

  private Main(String[] args) {
    this.args = args;
  }

  private void run() {
    // set up parsing of command line flags
    OptionParser parser = new OptionParser();

    // "./run --gui" will start a web server
    parser.accepts("gui");

    // use "--port <n>" to specify what port on which the server runs
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
        .defaultsTo(DEFAULT_PORT);

    OptionSet options = parser.parse(args);
    if (options.has("gui")) {
      runSparkServer((int) options.valueOf("port"));
    }

    try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
      String input;
      while ((input = br.readLine()) != null) {
        try {
          input = input.trim();
          String[] arguments = input.split(" ");

          //Initialize starsList, an ArrayList where all the stars in a given file will be stored.
          ArrayList<String[]> starsList = new ArrayList<>();

          //Initialize currFilePath, a String that has the file path of the loaded stars file
          String currFilePath = "";

          //If there are three arguments in the input, and the 1st argument is the add command,
          if (arguments.length == 3 && arguments[0].equals("add")){

            //Create a new MathBot, and use it to add and print the 1st and 2nd arguments
            MathBot math = new MathBot();
            System.out.println(math.add(Double.parseDouble(arguments[1]),
                Double.parseDouble(arguments[2])));
          }

          //If there are 3 arguments in the input, and the 1st argument is the subtract command,
          if (arguments.length == 3 && arguments[0].equals("subtract")){

            //Create a new MathBot, and use it to subtract and print the 1st and 2nd arguments
            MathBot math = new MathBot();
            System.out.println(math.subtract(Double.parseDouble(arguments[1]),
                Double.parseDouble(arguments[2])));
          }

          //If there are 2 arguments in the input, and the 1st argument is the stars command,
          if (arguments.length == 2 && arguments[0].equals("stars")){

            /*Create a new BufferedReader based off the 2nd argument (a file path), and
              set starsList to a new ArrayList. */
            BufferedReader starsReader = new BufferedReader(new FileReader(arguments[1]));
            starsList = new ArrayList<>();

            //Check if arguments[1] is the path to an invalid input file
            String[] header = starsReader.readLine().split(",");
            if (!header[0].equals("StarID") || !header[1].equals("ProperName")
                || !header[2].equals("X") || !header[3].equals("Y") || !header[4].equals("Z")){
              throw new Exception("ERROR: Invalid input file.");
            }

            /*If the path is valid, set it to currFilePath, and  loop through all the lines in
              the file with the stars, reading and adding them to starsList. */
            else{
              currFilePath = arguments[1];
              String currentStar;
              while ((currentStar = starsReader.readLine()) != null){
                starsList.add(currentStar.split(","));
              }
            }
          }

          //If the naive_neighbors command is run before a file was loaded, throw an error.
          if (arguments[0].equals("naive_neighbors") && currFilePath.isEmpty()){
            throw new Exception("ERROR: No star data has been loaded yet");
          }

          //If the first argument is the naive_neighbors command, and if starsList is not empty,
          if (arguments[0].equals("naive_neighbors") && !currFilePath.isEmpty()) {

            //Initialize the coordinates and the number of neighbors.
            double[] coords = new double[3];
            int k = 0;
            int sourceStarID = -1;


            //First, if there are 3 arguments,
            if (arguments.length == 3) {

              //Update k, the number of requested neighbors
              k = Integer.parseInt(arguments[1]);

              //Find the entry corresponding to the given star name in starsList, if there is one.
              String[] sourceStar = null;
              for (String[] star : starsList) {
                if (arguments[2].equals(star[1])) {
                  sourceStar = star;
                }
              }

              /*If the given star name does exist in starsList, and the amount of requested
                neighbors is smaller to the total amount of stars, update coords and k. */
              if (sourceStar != null && k < starsList.size()) {
                coords[0] = Double.parseDouble(sourceStar[2]);
                coords[1] = Double.parseDouble(sourceStar[2]);
                coords[2] = Double.parseDouble(sourceStar[2]);
                k = Integer.parseInt(arguments[1]) + 1;
                sourceStarID = Integer.parseInt(sourceStar[0]);
              }

              //If there are more requested neighbors than possible, throw an error.
              else if (k >= starsList.size()){
                throw new Exception("ERROR: Number of requested neighbors exceeds the number of loaded stars.");
              }
            }

            /*Now, if there are 5 arguments, and the amount of requested neighbors is no bigger
              than the amount of stars in starsList, update coords, and k.*/
            else if (arguments.length == 5) {
              if (Integer.parseInt(arguments[1]) <= starsList.size()) {
                coords[0] = Double.parseDouble(arguments[2]);
                coords[1] = Double.parseDouble(arguments[3]);
                coords[2] = Double.parseDouble(arguments[4]);
                k = Integer.parseInt(arguments[1]);
              }

              //If there are more requested neighbors than possible, throw an error.
              else if (k > starsList.size()){
                throw new Exception("ERROR: Requested number of neighbors is greater than " + (starsList.size()-1) + ".");
              }
            }


            /*Find the k closest stars to the coords. If the naive_neighbors command used a
              specific star (and not a coordinate position, then remove that specific star
              from the neighbors list. */
            System.out.println("Read " + k + " stars from " + currFilePath);
            ArrayList<Integer> neighbors = this.findBestNeighbors(k, coords, starsList);
            if (sourceStarID != -1){
              neighbors.remove(sourceStarID);
            }

            //Print out all the neighbors.
            for (int starID: neighbors){
              System.out.println(starID);
            }
          }

        } catch (Exception e) {
          // e.printStackTrace();
          System.out.println("ERROR: We couldn't process your input");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("ERROR: Invalid input for REPL");
    }

  }

  /**
   * This method finds a certain amount of stars that are closest to a given set of coordinates.
   * @param k the number of stars to find
   * @param coords the coordinates to a location
   * @return a list of the starID of the closest stars
   */
  public ArrayList<Integer> findBestNeighbors(int k, double[] coords, ArrayList<String[]> starsList){

    //Create a new list to store the closest stars to the position.
    ArrayList<Integer> bestNeighbors = new ArrayList<>();

    //Loop through the stars k times
    for (int i = 0; i<k; i++){

      //Initialize the minimum distance and the best neighbor of this run to temporary values.
      double currentMin = Double.MAX_VALUE; //the biggest possible value for a double
      int currentBestN = -1;

      //For every star in the list, calculate its distance from the coords.
      for (String[] star: starsList){
        double currentDistance = Math.sqrt(Math.pow((coords[0]-Double.parseDouble(star[2])), 2)
            + Math.pow((coords[1]-Double.parseDouble(star[3])), 2)
            + Math.pow((coords[2]-Double.parseDouble(star[4])), 2));

        /*If the current star's distance is smaller or equal to the minimum, and it is not already
          in bestNeighbors, update the current distance. */
        if (currentDistance <= currentMin && !bestNeighbors.contains(Integer.parseInt(star[0]))){
          currentMin = currentDistance;

          //If the current distance is equal to currentMin, randomly pick between the two.
          if (currentDistance == currentMin){
            int rnd = new Random().nextInt(2);

            //Update the current best neighbor only if the random int is 1
            if (rnd == 1){
              currentBestN = Integer.parseInt(star[0]);
            }
          }

          //If the current distance is smaller than currentMin, update the current best neighbor
          else if (currentDistance < currentMin){
            currentBestN = Integer.parseInt(star[0]);
          }
        }
      }

      //Only add currentBestNeighbor if the value changed from its initial temporary value.
      if (currentBestN != -1){
        bestNeighbors.add(currentBestN);
      }

      //If the value did not change, return all the neighbors.
      else {
        return bestNeighbors;
      }
    }

    return bestNeighbors;
  }

  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration(Configuration.VERSION_2_3_0);

    // this is the directory where FreeMarker templates are placed
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.%n",
          templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  private void runSparkServer(int port) {
    // set port to run the server on
    Spark.port(port);

    // specify location of static resources (HTML, CSS, JS, images, etc.)
    Spark.externalStaticFileLocation("src/main/resources/static");

    // when there's a server error, use ExceptionPrinter to display error on GUI
    Spark.exception(Exception.class, new ExceptionPrinter());

    // initialize FreeMarker template engine (converts .ftl templates to HTML)
    FreeMarkerEngine freeMarker = createEngine();

    // setup Spark Routes
    Spark.get("/", new MainHandler(), freeMarker);
  }

  /**
   * Display an error page when an exception occurs in the server.
   */
  private static class ExceptionPrinter implements ExceptionHandler<Exception> {
    @Override
    public void handle(Exception e, Request req, Response res) {
      // status 500 generally means there was an internal server error
      res.status(500);

      // write stack trace to GUI
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }

  /**
   * A handler to serve the site's main page.
   *
   * @return ModelAndView to render.
   * (main.ftl).
   */
  private static class MainHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      // this is a map of variables that are used in the FreeMarker template
      Map<String, Object> variables = ImmutableMap.of("title",
          "Go go GUI");

      return new ModelAndView(variables, "main.ftl");
    }
  }
}

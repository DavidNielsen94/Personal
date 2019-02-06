import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.io.IOException;
import java.lang.Process;
import java.lang.ProcessBuilder;
import java.util.concurrent.TimeUnit;
import java.util.*;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main
{
    private static Properties prop = System.getProperties();
    private static Scanner scanObj = new Scanner(System.in);
    private static ArrayList<String> history = new ArrayList<>();
    private static String currentDir = prop.getProperty("user.dir");
    private static File directory = new File(currentDir);
    private static String[] ARG;
    private static int iteration = 0;
    private static String command = "";


    public static void main(String[] args)
    {

        System.out.print(currentDir + "]:");
     while(scanObj.hasNextLine())
     {

             command = getCommand();
             switch (command)
             {
                 case "history":
                     hist();
                     break;

                 case "list":
                 case "dir":
                 case "ls":
                    list();
                     break;

                 case "cd":
                 case "CD":
                 case "..":
                     if(ARG.length > 1)
                     {
                         cd(ARG[1]);
                     }
                     else if(command.equals(".."))
                     {
                         cd(ARG[0]);
                     }
                     else
                     {
                         cd(prop.getProperty("user.home"));
                     }
                     break;

                 case "exit":
                     System.exit(1);
                     break;

                 default:
                     System.out.println("Invalid command: " + command);
                     break;
             }
         System.out.print(currentDir + "]:");
     }
    }

    private static String getCommand()
    {
        String string = scanObj.nextLine();
        ARG = splitCommand(string);
        if(ARG.length > 0)
        {
            history.add(string);
            command = ARG[iteration];
            if (command.equals("^") && ARG.length > 1)
            {
                int value = Integer.parseInt(ARG[1]);
                if (value <= history.size() && value != 0)
                {
                    ARG = splitCommand(history.get(value - 1));
                    command = ARG[0];
                    if (command.equals("^"))
                    {
                        value = Integer.parseInt(ARG[1]);
                        command = history.get(value - 1);
                    }
                }
            }
        }
        else
        {
            command = "\"\"";
        }

        return command;
    }

    private static void list()
    {
        File contents = new File(currentDir);
            File[] files = contents.listFiles();
            if(contents.isDirectory())
            {
                if(contents.list().length>0)
                {
                    for (int i = 0; i < files.length; i++) {
                        System.out.println(files[i].getName());
                    }
                }
                else
                {
                    System.out.println("Empty");
                }
            }
            else
            {
                System.out.println(contents.getName() +" is not a valid directory");
            }

    }

    private static void cd(String newDir)
    {
      switch (newDir)
      {
          case "..":
          if (directory.getParent() != null)
          {
              currentDir = directory.getParent();
              directory = new File(currentDir);
          }
          break;
          default:
              isValidFile(newDir);
              break;
      }
    }

    private static boolean isValidFile(String newDir)
    {
        if(newDir.equals(prop.getProperty("user.home")))
        {
            currentDir = prop.getProperty("user.home");
            directory = new File(prop.getProperty("user.home"));
        }
        else
        {
            File contents = new File(currentDir);
            File[] files = contents.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (newDir.equals(files[i].getName())) {
                    currentDir = files[i].getPath();
                    directory = new File(files[i].getPath());
                    return true;
                }
            }
        }

        return false;
    }


    private static void hist()
    {
        int size = history.size();
        System.out.println("-- Command History --");
        for(int i = 0; i < size; i++)
        {
            System.out.println(i+1 + " : " + history.get(i));
        }
    }

    /**
     * Split the user command by spaces, but preserving them when inside double-quotes.
     * Code Adapted from: https://stackoverflow.com/questions/366202/regex-for-splitting-a-string-using-space-when-not-surrounded-by-single-or-double
     */
    public static String[] splitCommand(String command) {
        java.util.List<String> matchList = new java.util.ArrayList<>();

        Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
        Matcher regexMatcher = regex.matcher(command);
        while (regexMatcher.find()) {
            if (regexMatcher.group(1) != null) {
                // Add double-quoted string without the quotes
                matchList.add(regexMatcher.group(1));
            } else if (regexMatcher.group(2) != null) {
                // Add single-quoted string without the quotes
                matchList.add(regexMatcher.group(2));
            } else {
                // Add unquoted word
                matchList.add(regexMatcher.group());
            }
        }

        return matchList.toArray(new String[matchList.size()]);
    }
}

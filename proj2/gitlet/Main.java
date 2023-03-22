package gitlet;

import java.io.IOException;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author payfish
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }
        String firstArg = args[0];
        Repository repository = new Repository();
        switch(firstArg) {
            case "init":
                repository.validateNumArgs("init", args, 1);
                repository.init();
                break;
            case "add":
                repository.validateNumArgs("add", args, 2);
                repository.add(args[1]);
                break;
            case "commit":
                repository.validateNumArgs("commit", args, 2);
                repository.commit(args[1]);
                break;
            case "rm":
                break;
            case "log":
                System.out.println(1);
                break;
            case "global-log":
                System.out.println();
                break;
            case "find":
                break;
            case "status":
                break;
            case "checkout":
                break;
            case "branch":
                break;
            case "rm-branch":
                break;
            case "reset":
                break;
            case "merge":
                break;
            case "0":
                System.exit(0);
            default:
                System.out.println("No command with that name exists.");
                break;
        }
        return;
    }
}

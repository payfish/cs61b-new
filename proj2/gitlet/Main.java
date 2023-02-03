package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author payfish
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }
        String firstArg = args[0];
        Repository repository = new Repository();
        switch(firstArg) {
            case "init":
                repository.validateNumArgs("init", args, 1);
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                break;
            case "commit":
                break;
            default:
                System.out.println("No command with that name exists.");
                break;
        }
        return;
    }
}

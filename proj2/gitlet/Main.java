package gitlet;

import java.io.IOException;

import static gitlet.Utils.*;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author payfish
 */
public class Main {

    public static void main(String[] args)  {
        int n = args.length;
        if (n == 0) {
            message("Please enter a command.");
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
                if (n == 1) {
                    repository.commit(null);
                } else {
                    repository.commit(args[1]);
                }
                break;
            case "rm":
                repository.validateNumArgs("rm", args, 2);
                repository.rm(args[1]);
                break;
            case "log":
                repository.validateNumArgs("log", args, 1);
                repository.log();
                break;
            case "global-log":
                repository.validateNumArgs("global-log", args, 1);
                repository.global_log();
                break;
            case "find":
                repository.validateNumArgs("find", args, 2);
                repository.find(args[1]);
                break;
            case "status":
                repository.validateNumArgs("status", args, 1);
                repository.status();
                break;
            case "checkout":
                if (n == 2) {
                    repository.checkout(n, args[1], null, null);
                } else {
                    if (!args[n - 2].equals("--")) {
                        message("Incorrect operands.");
                        break;
                    }
                    if (n == 3) {
                        repository.checkout(n, null, null, args[2]);
                    } else if (n == 4) {
                        repository.checkout(n, null, args[1], args[3]);
                    }
                }
                break;
            case "branch":
                repository.validateNumArgs("branch", args, 2);
                repository.branch(args[1]);
                break;
            case "rm-branch":
                repository.validateNumArgs("rm-branch", args, 2);
                repository.rm_branch(args[1]);
                break;
            case "reset":
                repository.validateNumArgs("reset", args, 2);
                repository.reset(args[1]);
                break;
            case "merge":
                repository.validateNumArgs("merge", args, 2);
                repository.merge(args[1]);
                break;
            case "0":
                System.exit(0);
            default:
                message("No command with that name exists.");
                break;
        }
    }
}

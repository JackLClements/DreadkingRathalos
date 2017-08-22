/*
 */
package commands;
/**
 *
 * @author Jack L. Clements
 */
public class TestCommand extends Command{
    //flexible, inherits all from command parent class in order to determine at runtime
    public TestCommand(String commandKey) {
        super(commandKey);
    }
    
}

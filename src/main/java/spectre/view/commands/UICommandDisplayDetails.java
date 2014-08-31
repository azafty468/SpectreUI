package spectre.view.commands;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew
 * Date: 6/22/14
 * Time: 5:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class UICommandDisplayDetails extends UICommand {
    public final long uniqueId;

    public UICommandDisplayDetails(long uniqueId) {
        this.uniqueId = uniqueId;
    }
}

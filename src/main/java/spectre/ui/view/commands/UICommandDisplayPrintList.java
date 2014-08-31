package spectre.ui.view.commands;

import spectre.shared.GameObject;
import spectre.shared.PrintList;

/**
 * Created by andrewzafft on 6/17/14.
 */
public class UICommandDisplayPrintList extends UICommand {
    public final PrintList printList;
    public GameObject extendedDetails;

    public UICommandDisplayPrintList(PrintList printList) {
        this.printList = printList;
    }

    public UICommandDisplayPrintList withExtendedDetails(GameObject extendedDetails) {
        this.extendedDetails = extendedDetails;
        return this;
    }
}

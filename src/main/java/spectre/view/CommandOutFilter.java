package spectre.view;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * Created by andrewzafft on 6/6/14.
 */
public class CommandOutFilter extends DocumentFilter {
    private JTextArea area;
    private final int max;

    public CommandOutFilter(JTextArea area, int max) {
        this.area = area;
        this.max = max;
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length,
                        String text, AttributeSet attrs) throws BadLocationException {
        super.replace(fb, offset, length, text, attrs);
        int lines = area.getLineCount();
        if (lines > max) {
            int linesToRemove = lines - max -1;
            int lengthToRemove = area.getLineStartOffset(linesToRemove);
            remove(fb, 0, lengthToRemove);
        }
    }
}

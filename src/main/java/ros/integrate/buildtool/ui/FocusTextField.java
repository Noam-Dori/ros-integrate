package ros.integrate.buildtool.ui;

import com.intellij.ui.components.JBTextField;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * a specialized text field that selects entire text when focus is gained.
 * @author dacwe <a href="https://stackoverflow.com/a/7361369">...</a>
 */
public class FocusTextField extends JBTextField {
    public FocusTextField() {
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                FocusTextField.this.select(0, getText().length());
            }

            @Override
            public void focusLost(FocusEvent e) {
                FocusTextField.this.select(0, 0);
            }
        });
    }
}

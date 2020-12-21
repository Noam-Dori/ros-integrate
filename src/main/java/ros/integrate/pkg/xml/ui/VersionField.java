package ros.integrate.pkg.xml.ui;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.fields.IntegerField;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.ROSPackageXml;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;

public class VersionField extends JPanel {
    private final JBLabel compatibilityLabel = new JBLabel();
    private final IntegerField[] data = {new IntegerField(), new IntegerField(), new IntegerField(),
            new IntegerField(), new IntegerField(), new IntegerField()};

    public VersionField() {
        super(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = NONE;
        c.anchor = WEST;
        c.insets = JBUI.insets(0, 0, 0, 0);
        add(data[0], c);

        c.gridx++;
        add(data[1], c);

        c.gridx++;
        c.weightx = 1;
        add(data[2], c);

        c.gridx++;
        c.weightx = 0;
        c.insets = JBUI.insets(0, 10, 0, 5);
        add(compatibilityLabel, c);

        c.gridx++;
        c.insets = JBUI.insets(0, 0, 0, 0);
        data[3].setCanBeEmpty(true);
        add(data[3], c);

        c.gridx++;
        data[4].setCanBeEmpty(true);
        add(data[4], c);

        c.gridx++;
        data[5].setCanBeEmpty(true);
        add(data[5], c);
    }

    @NotNull
    public ROSPackageXml.Version getVersion() {
            if (data[0].getValue() < 0) {
                data[0].setValue(1);
            }
            if (data[1].getValue() < 0) {
                data[1].setValue(0);
            }
            if (data[2].getValue() < 0) {
                data[2].setValue(0);
            }
            String compatibility = null;
            if (data[3].getValue() >= 0 && data[4].getValue() >= 0 && data[4].getValue() >= 0) {
                compatibility = String.format("%d.%d.%d", data[3].getValue(), data[4].getValue(),
                        data[5].getValue());
            }
            String vString = String.format("%d.%d.%d", Arrays.stream(data).map(IntegerField::getValue).toArray());
            if (vString.equals(compatibility)) {
                compatibility = null;
            }
            return new ROSPackageXml.Version(vString, compatibility);
        }

    public void setCompatibilityLabel(String label) {
        compatibilityLabel.setText(label);
    }

    public void setVersion(@NotNull ROSPackageXml.Version version) {
        String[] vNum = version.getValue().split("\\.");
        for (int i = 0; i < 3; i++) {
            data[i].setText(vNum[i]);
        }
        if (version.getRawCompatibility() != null) {
            vNum = version.getRawCompatibility().split("\\.");
            for (int i = 3; i < 6; i++) {
                data[i].setText(vNum[i - 3]);
            }
        }
    }

    public void installKeyEvents() {
        for (int i = 0; i < data.length - 1; i++) {
            IntegerField versionField = data[i], nextField = data[i + 1];
            versionField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    if (e.getKeyChar() == '.') {
                        e.consume();
                        nextField.requestFocus();
                    }
                }
            });
        }
    }
}

package ros.integrate.ui;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.fields.ExpandableTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * an extension of the form builder that supports sections.
 * Sections have similar vertical spacing and are divided by horizontal bars
 * @author Noam Dori
 */
public class SectionedFormBuilder extends FormBuilder {
    /**
     * an extension of the form builder that builds one section
     */
    public static class SectionBuilder extends FormBuilder {
        @Nullable
        private final String name;
        @NotNull
        private final SectionedFormBuilder parent;

        private JPanel newSectionHeader(@NotNull String sectionName) {
            JBLabel name = new JBLabel(sectionName);
            Font oldFont = name.getFont();
            name.setFont(new Font(oldFont.getName(), oldFont.getStyle(), oldFont.getSize() - 2));

            return FormBuilder.createFormBuilder()
                    .addLabeledComponent(name, new JSeparator())
                    .getPanel();
        }

        /**
         * construct a new section builder
         * @param name the name of the section
         * @param parent the form builder this belongs to
         */
        SectionBuilder(@Nullable String name, @NotNull SectionedFormBuilder parent) {
            this.name = name;
            this.parent = parent;
        }

        /**
         * builds the section and appends it to the parent form builder
         * @return the parent form builder
         */
        public SectionedFormBuilder closeSection() {
            return (SectionedFormBuilder) parent
                    .addComponent(name == null ? new JSeparator() : newSectionHeader(name), UIUtil.LARGE_VGAP)
                    .setFormLeftIndent(name == null ? 0 : UIUtil.DEFAULT_HGAP * 2)
                    .addComponent(super.getPanel())
                    .setFormLeftIndent(0);
        }

        @Override
        public SectionBuilder addComponent(@NotNull JComponent component) {
            return (SectionBuilder) super.addComponent(component);
        }

        @Override
        public SectionBuilder addLabeledComponent(@Nullable JComponent label, @NotNull JComponent component) {
            return (SectionBuilder) super.addLabeledComponent(label, component);
        }

        @Override
        protected int getFill(JComponent component) {
            return parent.getFill(component);
        }

        @Override
        public JPanel getPanel() {
            return closeSection().getPanel();
        }
    }

    private final List<Class<? extends JComponent>> unfilledClasses, horizontalClasses;

    private SectionedFormBuilder() {
        unfilledClasses = Collections.singletonList(JButton.class);
        horizontalClasses = Stream.of(PathTextFieldWithHistory.class, ExpandableTextField.class)
                .collect(Collectors.toList());
        setHorizontalGap(20);
    }

    @Override
    protected int getFill(JComponent component) {
        if(unfilledClasses.stream().anyMatch(clazz -> clazz.isInstance(component))) {
            return GridBagConstraints.NONE;
        }
        if (horizontalClasses.stream().anyMatch(clazz -> clazz.isInstance(component))) {
            return GridBagConstraints.HORIZONTAL;
        }
        return super.getFill(component);
    }

    /**
     * construct the form builder
     * @return the new form builder
     */
    @NotNull
    @Contract(" -> new")
    public static SectionedFormBuilder createFormBuilder() {
        return new SectionedFormBuilder();
    }

    /**
     * opens a new section in the form
     * @param name the name of the new section
     * @return a new section builder
     */
    public SectionBuilder addSection(@Nullable String name) {
        return new SectionBuilder(name, this);
    }

    @Override
    public SectionedFormBuilder addComponent(@NotNull JComponent component) {
        return (SectionedFormBuilder) super.addComponent(component);
    }

    @Override
    public SectionedFormBuilder addLabeledComponent(@Nullable JComponent label, @NotNull JComponent component) {
        return (SectionedFormBuilder) super.addLabeledComponent(label, component);
    }

    @Override
    public JPanel getPanel() {
        JPanel ret = new JPanel(new BorderLayout());
        ret.add(super.getPanel(), BorderLayout.NORTH);
        return ret;
    }
}

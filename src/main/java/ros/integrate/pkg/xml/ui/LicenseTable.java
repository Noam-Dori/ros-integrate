package ros.integrate.pkg.xml.ui;

import com.intellij.execution.util.ListTableWithButtons;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.util.ui.ListTableModel;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.xml.ROSLicenses;
import ros.integrate.pkg.xml.ROSPackageXml.License;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.util.List;
import java.util.stream.Collectors;

public class LicenseTable extends ListTableWithButtons<LicenseTable.Entry> {
    private static final String[] LICENSE_OPTIONS = getLicenseOptions();

    @NotNull
    @Contract(value = " -> new", pure = true)
    private static String[] getLicenseOptions() {
        return ROSLicenses.AVAILABLE_LICENSES.keySet().stream().sorted().toArray(String[]::new);
    }

    /**
     * the physical path. This is the entry row of the table
     */
    static class Entry {
        private License entry;

        /**
         * @return the actual string path
         */
        License get() {
            return entry;
        }

        /**
         * sets the value this entry stores
         * @param entry the actual string path
         */
        void set(@NotNull License entry) {
            this.entry = entry;
        }

        /**
         * construct a new path entry
         * @param entry the actual string path
         */
        @Contract(pure = true)
        Entry(@NotNull License entry) {
            this.entry = entry;
        }
    }

    @Override
    protected ListTableModel<?> createListModel() {
        return new ListTableModel<>(new ElementsColumnInfoBase<Entry>("Type") {
            @NotNull
            @Override
            public String valueOf(@NotNull Entry entry) {
                return entry.get().getValue();
            }

            @NotNull
            @Override
            protected String getDescription(@NotNull Entry entry) {
                return "The type of the license";
            }

            @Override
            public void setValue(@NotNull Entry entry, @NotNull String value) {
                if (entry.get().getValue().equals(value) || value.isEmpty()) {
                    return;
                }
                entry.set(new License(value, entry.get().getFile()));
                setModified();
            }

            @Override
            public boolean isCellEditable(Entry path) {
                return true;
            }

            @NotNull
            @Override
            public TableCellEditor getEditor(Entry entry) {
                ComboBox<String> choices = new ComboBox<>(LICENSE_OPTIONS);
                choices.setEditable(true);
                return new DefaultCellEditor(choices);
            }
        }, new ElementsColumnInfoBase<Entry>("File") {
            @Nullable
            @Override
            public String valueOf(@NotNull Entry entry) {
                return entry.get().getFile();
            }

            @NotNull
            @Override
            protected String getDescription(@NotNull Entry entry) {
                return "The URL of the file containing the raw text of this license";
            }

            @Override
            public void setValue(@NotNull Entry entry, @NotNull String value) {
                if (value.equals(entry.get().getFile())) {
                    return;
                }
                entry.set(new License(entry.get().getValue(), value));
                setModified();
            }

            @Override
            public boolean isCellEditable(Entry path) {
                return true;
            }
        });
    }

    @Override
    protected Entry createElement() {
        return new Entry(new License("TODO", null));
    }

    @Override
    protected boolean isEmpty(Entry element) {
        return false;
    }

    @Override
    protected Entry cloneElement(@NotNull Entry variable) {
        return new Entry(variable.get());
    }

    @Override
    protected boolean canDeleteElement(Entry selection) {
        return getElements().size() > 1;
    }

    public List<License> getLicenses() {
        return getElements().stream().map(Entry::get).collect(Collectors.toList());
    }

    public void setLicenses(@NotNull List<License> licenses) {
        setValues(licenses.stream().map(Entry::new).collect(Collectors.toList()));
        if (licenses.isEmpty()) {
            addNewElement(createElement());
        }
    }
}

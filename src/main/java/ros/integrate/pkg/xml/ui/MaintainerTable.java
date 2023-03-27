package ros.integrate.pkg.xml.ui;

import com.intellij.execution.util.ListTableWithButtons;
import com.intellij.util.ui.ListTableModel;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.ROSPackageXml.Contributor;

import java.util.List;
import java.util.stream.Collectors;

public class MaintainerTable extends ListTableWithButtons<MaintainerTable.Entry> {

    /**
     * the physical path. This is the entry row of the table
     */
    static class Entry {
        private Contributor entry;

        /**
         * @return the actual string path
         */
        Contributor get() {
            return entry;
        }

        /**
         * sets the value this entry stores
         * @param entry the actual string path
         */
        void set(@NotNull Contributor entry) {
            this.entry = entry;
        }

        /**
         * construct a new path entry
         * @param entry the actual string path
         */
        @Contract(pure = true)
        Entry(@NotNull Contributor entry) {
            this.entry = entry;
        }
    }

    @Override
    protected ListTableModel<?> createListModel() {
        return new ListTableModel<>(new ElementsColumnInfoBase<Entry>("Name") {
            @NotNull
            @Override
            public String valueOf(@NotNull Entry entry) {
                return entry.get().getName();
            }

            @NotNull
            @Override
            protected String getDescription(@NotNull Entry entry) {
                return "The name of the maintainer";
            }

            @Override
            public void setValue(@NotNull Entry entry, @NotNull String value) {
                if (valueOf(entry).equals(value) || value.isEmpty()) {
                    return;
                }
                entry.set(new Contributor(value, entry.get().getEmail()));
                setModified();
            }

            @Override
            public boolean isCellEditable(Entry path) {
                return true;
            }
        }, new ElementsColumnInfoBase<Entry>("Email") {
            @NotNull
            @Override
            public String valueOf(@NotNull Entry entry) {
                return entry.get().getEmail();
            }

            @NotNull
            @Override
            protected String getDescription(@NotNull Entry entry) {
                return "The URL of the file containing the raw text of this license";
            }

            @Override
            public void setValue(@NotNull Entry entry, @NotNull String value) {
                if (value.equals(valueOf(entry))) {
                    return;
                }
                entry.set(new Contributor(entry.get().getName(), value));
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
        return new Entry(new Contributor("user", "user@todo.todo"));
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

    public List<Contributor> getMaintainers() {
        return getElements().stream().map(Entry::get).collect(Collectors.toList());
    }

    public void setMaintainers(@NotNull List<Contributor> contributors) {
        setValues(contributors.stream().map(Entry::new).collect(Collectors.toList()));
        if (contributors.isEmpty()) {
            addNewElement(createElement());
        }
    }
}

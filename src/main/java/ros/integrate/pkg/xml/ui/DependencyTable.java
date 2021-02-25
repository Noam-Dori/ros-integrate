package ros.integrate.pkg.xml.ui;

import com.intellij.execution.util.ListTableWithButtons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.TextFieldWithAutoCompletion;
import com.intellij.ui.TextFieldWithAutoCompletionListProvider;
import com.intellij.ui.components.fields.IntegerField;
import com.intellij.util.ui.AbstractTableCellEditor;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.ROSDepKeyCache;
import ros.integrate.pkg.ROSPackageManager;
import ros.integrate.pkg.psi.ROSPackage;
import ros.integrate.pkg.psi.impl.ROSDepKey;
import ros.integrate.pkg.xml.DependencyType;
import ros.integrate.pkg.xml.ROSPackageXml.Dependency;
import ros.integrate.pkg.xml.VersionRange;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class DependencyTable extends ListTableWithButtons<DependencyTable.Entry> {
    private final Project project;
    private int format = 0;

    Collection<ROSPackage> packages = new HashSet<>();

    public DependencyTable(@NotNull Project project, @NotNull IntegerField formatField) {
        this.project = project;

        packages.addAll(project.getService(ROSPackageManager.class).getAllPackages());
        packages.addAll(project.getService(ROSDepKeyCache.class).getAllKeys());

        formatField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                Integer value = formatField.getValue();
                if (!value.equals(formatField.getDefaultValue()) && format != value) {
                    format = formatField.getValue();
                }
            }
        });
    }

    /**
     * the physical path. This is the entry row of the table
     */
    static class Entry {
        private Dependency entry;

        /**
         * @return the actual string path
         */
        Dependency get() {
            return entry;
        }

        /**
         * sets the value this entry stores
         * @param entry the actual string path
         */
        void set(@NotNull Dependency entry) {
            this.entry = entry;
        }

        /**
         * construct a new path entry
         * @param entry the actual string path
         */
        @Contract(pure = true)
        Entry(@NotNull Dependency entry) {
            this.entry = entry;
        }
    }

    @Override
    protected ListTableModel<?> createListModel() {
        return new ListTableModel<>(new ColumnInfo<Entry, DependencyType>("Type") {

            @NotNull
            @Override
            public DependencyType valueOf(@NotNull Entry entry) {
                return entry.get().getType();
            }

            @NotNull
            @Override
            public TableCellRenderer getRenderer(Entry entry) {
                return new DefaultTableCellRenderer() {
                    @Override
                    protected void setValue(Object value) {
                        super.setValue(valueOf(entry).toString().toLowerCase());
                    }
                };
            }

            @Override
            public void setValue(@NotNull Entry entry, @NotNull DependencyType value) {
                if (valueOf(entry).equals(value)) {
                    return;
                }
                entry.set(new Dependency(value, entry.get().getPackage(), entry.get().getVersionRange(),
                        entry.get().getCondition()));
                setModified();
            }

            @Override
            public boolean isCellEditable(Entry path) {
                return true;
            }

            @NotNull
            @Override
            public TableCellEditor getEditor(Entry entry) {
                ComboBox<DependencyType> choices = new ComboBox<>();
                choices.setRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                                  boolean isSelected, boolean cellHasFocus) {
                        return super.getListCellRendererComponent(list, value.toString().toLowerCase(),
                                index, isSelected, cellHasFocus);
                    }
                });
                Arrays.stream(DependencyType.values()).filter(dep -> dep.relevant(format)).forEach(choices::addItem);
                return new DefaultCellEditor(choices);
            }
        }, new ColumnInfo<Entry, ROSPackage>("Package") {
            @NotNull
            @Override
            public ROSPackage valueOf(@NotNull Entry entry) {
                return entry.get().getPackage();
            }

            @Override
            public void setValue(@NotNull Entry entry, @NotNull ROSPackage value) {
                if (value.equals(valueOf(entry))) {
                    return;
                }
                entry.set(new Dependency(entry.get().getType(), value, entry.get().getVersionRange(),
                        entry.get().getCondition()));
                setModified();
            }

            @Override
            public boolean isCellEditable(Entry path) {
                return true;
            }

            @Override
            public TableCellEditor getEditor(Entry entry) {
                TextFieldWithAutoCompletionListProvider<ROSPackage> provider =
                        new TextFieldWithAutoCompletionListProvider<ROSPackage>(packages){
                            @NotNull
                            @Override
                            protected String getLookupString(@NotNull ROSPackage item) {
                                return item.getName();
                            }
                        };
                TextFieldWithAutoCompletion<ROSPackage> field = new TextFieldWithAutoCompletion<>(project,
                        provider, true, null);
                field.setText(entry.get().getPackage().getName());
                return new AbstractTableCellEditor() {
                    @Override
                    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                        return field.getComponent();
                    }

                    @Override
                    public ROSPackage getCellEditorValue() {
                        String text = field.getText();
                        return provider.getItems(text, false, null).stream()
                                .filter(item -> item.getName().equals(text))
                                .findFirst().orElse(new ROSDepKey(project, text));
                    }
                };
            }

            @NotNull
            @Override
            public TableCellRenderer getRenderer(Entry entry) {
                return new DefaultTableCellRenderer() {
                    @Override
                    protected void setValue(Object value) {
                        super.setValue(valueOf(entry).getName());
                    }
                };
            }
        });
    }

    @Override
    protected Entry createElement() {
        DependencyType defaultType = format >= 2 ? DependencyType.DEFAULT : DependencyType.BUILD;
        return new Entry(new Dependency(defaultType, ROSPackage.ORPHAN, VersionRange.any(), null));
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

    public List<Dependency> getDependencies() {
        return getElements().stream().map(Entry::get).collect(Collectors.toList());
    }

    public void setDependencies(@NotNull List<Dependency> dependencies) {
        setValues(dependencies.stream().map(Entry::new).collect(Collectors.toList()));
        if (dependencies.isEmpty()) {
            addNewElement(new Entry(new Dependency(DependencyType.BUILDTOOL, new ROSDepKey(project,"catkin"),
                    VersionRange.any(), null)));
        }
    }
}

package ros.integrate.settings;

import com.intellij.execution.util.ListTableWithButtons;
import com.intellij.util.ui.ListTableModel;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PackagePathTable extends ListTableWithButtons<PackagePathTable.Path> {
    static class Path {
        private String path;

        public String get() {
            return path;
        }

        public void set(@NotNull String path) {
            this.path = path;
        }

        @Contract(pure = true)
        Path(@NotNull String path) {
            this.path = path;
        }
    }

    PackagePathTable() {
        getTableView().getEmptyText().setText("No additional directories");
    }

    @Override
    protected ListTableModel createListModel() {
        return new ListTableModel(new ElementsColumnInfoBase<Path>("") {
            @Contract("null -> !null")
            @Nullable
            @Override
            public String valueOf(Path path) {
                return path == null ? "" : path.get();
            }

            @Contract("null -> !null")
            @Nullable
            @Override
            protected String getDescription(Path path) {
                return valueOf(path);
            }

            @Override
            public void setValue(Path oldPath, @NotNull String value) {
                if (value.equals(valueOf(oldPath))) {
                    return;
                }
                oldPath.set(value);
                setModified();
            }

            @Override
            public boolean isCellEditable(Path path) {
                return canDeleteElement(path);
            }
        });
    }

    @Override
    protected Path createElement() {
        return new Path("");
    }

    @Override
    protected boolean isEmpty(@NotNull Path element) {
        return element.get().isEmpty();
    }

    @Override
    protected Path cloneElement(@NotNull Path pathToClone) {
        return new Path(pathToClone.get());
    }

    @Override
    protected boolean canDeleteElement(Path selection) {
        return true;
    }

    List<String> getPaths() {
        return getElements().stream().map(Path::get).collect(Collectors.toList());
    }

    void setValues(@NotNull Stream<String> paths) {
        setValues(paths.map(Path::new).collect(Collectors.toList()));
    }
}

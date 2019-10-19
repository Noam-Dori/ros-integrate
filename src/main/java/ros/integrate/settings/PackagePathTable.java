package ros.integrate.settings;

import com.intellij.execution.util.ListTableWithButtons;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PackagePathTable extends ListTableWithButtons<String> {
    PackagePathTable() {
        getTableView().getEmptyText().setText("No additional directories");
    }

    @Override
    protected ListTableModel createListModel() {
        ColumnInfo entries = new ElementsColumnInfoBase<String>("") {
            @Nullable
            @Override
            public String valueOf(String s) {
                return s;
            }

            @Nullable
            @Override
            protected String getDescription(String element) {
                return element;
            }
        };
        return new ListTableModel(entries);
    }

    @Override
    protected String createElement() {
        return "";
    }

    @Override
    protected boolean isEmpty(@NotNull String element) {
        return element.isEmpty();
    }

    @Override
    protected String cloneElement(String variable) {
        return variable;
    }

    @Override
    protected boolean canDeleteElement(String selection) {
        return true;
    }

    List<String> getPaths() {
        return new ArrayList<>();
    }
}

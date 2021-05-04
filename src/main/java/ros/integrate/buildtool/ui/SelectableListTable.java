package ros.integrate.buildtool.ui;

import com.intellij.execution.util.ListTableWithButtons;
import com.intellij.openapi.actionSystem.ActionToolbarPosition;
import com.intellij.ui.JBColor;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.ListTableModel;
import com.intellij.util.ui.UIUtil;
import com.intellij.util.ui.table.IconTableCellRenderer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A GUI object representing a list of objects that cannot be edited manually, but rather selected.
 * The list allows:
 * - adding & removing entities like a normal list table
 * - displaying a text and/or icon pulled from some database
 * - refreshing the render via {@link SelectableListTable#refresh()}
 * - selecting objects in the list to show (or edit) details about the selected object via
 *   {@link com.intellij.ui.table.TableView#getSelectionModel()}
 * - bulk load identifiers from external source via {@link ListTableWithButtons#setValues(java.util.List)}
 * @author Noam Dori
 */
public class SelectableListTable extends ListTableWithButtons<Integer> {
    private final Supplier<Integer> requestId;
    private final Function<Integer, String> requestName;
    private final Function<Integer, Icon> requestIcon;

    @Override
    protected ListTableModel<Integer> createListModel() {
        return new ListTableModel<>(new ColumnInfo<Integer, String>("") {
            @NotNull
            @Override
            public String valueOf(Integer id) {
                return requestName.apply(id);
            }

            @Override
            public boolean isCellEditable(Integer id) {
                return false;
            }

            @NotNull
            public Icon iconOf(Integer id) {
                return requestIcon.apply(id);
            }

            @Override
            public TableCellRenderer getRenderer(Integer id) {
                IconTableCellRenderer<?> renderer = (IconTableCellRenderer<?>)
                        IconTableCellRenderer.create(iconOf(id));
                renderer.setText(valueOf(id));
                renderer.setToolTipText("Select item to customize");
                return renderer;
            }
        });
    }

    /**
     * create a selectable item list entity
     * @param requestId a function used to request a new ID for a new item in the list.
     *                  This function should create a new item in the database and return its identifier.
     * @param requestName a function that retrieves the "name" of the item with the corresponding input ID.
     *                    This function should return the string you want to display in the cell.
     * @param requestIcon a function that retrieves the icon of the item with the corresponding input ID.
     *                    This function should return the icon you want to display in the cell.
     */
    public SelectableListTable(Supplier<Integer> requestId,
                               Function<Integer, String> requestName,
                               Function<Integer, Icon> requestIcon) {
        this.requestId = requestId;
        this.requestName = requestName;
        this.requestIcon = requestIcon;
    }

    @Override
    protected Integer createElement() {
        return requestId.get();
    }

    @Override
    protected boolean isEmpty(Integer id) {
        return false;
    }

    @Override
    protected Integer cloneElement(Integer id) {
        return id;
    }

    @Override
    protected boolean canDeleteElement(Integer id) {
        return true;
    }

    @Override
    protected ToolbarDecorator createToolbarDecorator() {
        return super.createToolbarDecorator()
                .setToolbarPosition(ActionToolbarPosition.TOP)
                .setPanelBorder(JBUI.Borders.empty())
                .setToolbarBorder(JBUI.Borders.customLine(
                        JBColor.namedColor("DefaultTabs.borderColor", UIUtil.CONTRAST_BORDER_COLOR)
                        , 0, 0, 0, 1));
    }

    /**
     * update the icons/strings displayed in each cell.
     * This will pull all the information from the functions provided.
     */
    public void refresh() {
        getTableView().setVisible(false);
        getTableView().setVisible(true);
    }
}

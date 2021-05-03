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
}

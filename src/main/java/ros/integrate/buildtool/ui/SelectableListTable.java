package ros.integrate.buildtool.ui;

import com.intellij.execution.util.ListTableWithButtons;
import com.intellij.openapi.actionSystem.ActionToolbarPosition;
import com.intellij.ui.JBColor;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.ListTableModel;
import com.intellij.util.ui.UIUtil;

public class SelectableListTable extends ListTableWithButtons<SelectableListTable.Profile> {
    static class Profile {

    }


    @Override
    protected ListTableModel<Profile> createListModel() {
        return new ListTableModel<>();
    }

    @Override
    protected Profile createElement() {
        return null;
    }

    @Override
    protected boolean isEmpty(Profile element) {
        return false;
    }

    @Override
    protected Profile cloneElement(Profile variable) {
        return null;
    }

    @Override
    protected boolean canDeleteElement(Profile selection) {
        return false;
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

package com.perfetto.ros.integrate.psi;

import com.intellij.psi.tree.IElementType;
import com.perfetto.ros.integrate.ROSMsgLanguage;
import org.jetbrains.annotations.NotNull;

public class ROSMsgElementType extends IElementType {
    public ROSMsgElementType(@NotNull String debugName) {
        super(debugName, ROSMsgLanguage.INSTANCE);
    }
}

package com.perfetto.ros.integrate.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.perfetto.ros.integrate.ROSIcons;
import com.perfetto.ros.integrate.ROSMsgFileType;
import com.perfetto.ros.integrate.ROSMsgLanguage;
import com.perfetto.ros.integrate.ROSMsgUtil;
import com.perfetto.ros.integrate.psi.impl.ROSMsgPsiImplUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ROSMsgFile extends PsiFileBase {
    public ROSMsgFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, ROSMsgLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return ROSMsgFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "ROS Message File";
    }

    @Override
    public Icon getIcon(int flags) {
        return super.getIcon(flags);
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @NotNull
            @Override
            public String getPresentableText() {
                return ROSMsgUtil.trimMsgFileName(getContainingFile().getName());
            }

            @Override
            public String getLocationString() {
                return getProject().getBaseDir().getName() + "/" + getPresentableText();
            }

            @Override
            public Icon getIcon(boolean unused) {
                return ROSIcons.MSG_FILE;
            }
        };
    }
}

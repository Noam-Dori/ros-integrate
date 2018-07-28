package com.perfetto.ros.integrate.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.perfetto.ros.integrate.ROSIcons;
import com.perfetto.ros.integrate.psi.ROSMsgProperty;
import com.perfetto.ros.integrate.psi.ROSMsgTypes;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ROSMsgPsiImplUtil {
    public static String getType(ROSMsgProperty element) {
        ASTNode keyNode = element.getNode().findChildByType(ROSMsgTypes.TYPE);
        if (keyNode != null) {
            return keyNode.getText();
        } else {
            return null;
        }
    }

//    public static PsiElement setType(ROSMsgProperty element, String newName) {
//        ASTNode keyNode = element.getNode().findChildByType(ROSMsgTypes.TYPE);
//        if (keyNode != null) {
//
//            ROSMsgProperty property = ROSMsgElementFactory.createProperty(element.getProject(), newName);
//            ASTNode newKeyNode = property.getFirstChild().getNode();
//            element.getNode().replaceChild(keyNode, newKeyNode);
//        }
//        return element;
//    }

    public static ItemPresentation getPresentation(final ROSMsgProperty element) { return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return element.getType();
            }

            @Override
            public String getLocationString() {
                return element.getContainingFile().getName();
            }

            @Override
            public Icon getIcon(boolean unused) {
                return ROSIcons.MSG_FILE;
            }
        };
    }
}

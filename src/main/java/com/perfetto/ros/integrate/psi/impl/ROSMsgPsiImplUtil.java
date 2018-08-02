package com.perfetto.ros.integrate.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.perfetto.ros.integrate.ROSIcons;
import com.perfetto.ros.integrate.psi.ROSMsgElementFactory;
import com.perfetto.ros.integrate.psi.ROSMsgProperty;
import com.perfetto.ros.integrate.psi.ROSMsgTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ROSMsgPsiImplUtil {
    @Nullable
    public static String getType(@NotNull ROSMsgProperty element) {
        ASTNode keyNode = element.getNode().findChildByType(ROSMsgTypes.TYPE);
        if (keyNode != null) {
            return keyNode.getText();
        } else {
            return null;
        }
    }

    @Nullable
    public static String getGeneralType(@NotNull ROSMsgProperty element) {
        ASTNode keyNode = element.getNode().findChildByType(ROSMsgTypes.KEYTYPE);
        if (keyNode != null) {
            return keyNode.getText();
        } else {
            return getType(element);
        }
    }

    /**
     * gets the array size of the property, if its even an array
     * @param element the element to test
     * @return -1 if the element is not an array,
     *         0 if the element has variable size (since size 0 should not be used)
     *         otherwise, the size of the array
     */
    public static int getArraySize(@NotNull ROSMsgProperty element) {
        if (element.getNode().findChildByType(ROSMsgTypes.LBRACKET) != null) {
            ASTNode arrSize = element.getNode().findChildByType(ROSMsgTypes.NUMBER);
            if (arrSize != null) {
                return Integer.parseInt(arrSize.getText());
            }
            return 0;
        } else {
            return -1;
        }
    }

    @Nullable
    public static String getCConst(@NotNull ROSMsgProperty element) {
        ASTNode keyNode = element.getNode().findChildByType(ROSMsgTypes.CONST);
        if (keyNode != null) {
            return keyNode.getText();
        } else {
            return null;
        }
    }

    public static PsiElement setType(ROSMsgProperty element, String newName) {
        ASTNode typeNode = element.getNode().findChildByType(ROSMsgTypes.TYPE);
        if (typeNode != null) {

            ROSMsgProperty property = ROSMsgElementFactory.createProperty(element.getProject(), newName);
            ASTNode newTypeNode = property.getFirstChild().getNode();
            element.getNode().replaceChild(typeNode, newTypeNode);
        }
        return element;
    }

    public static PsiElement removeArray(ROSMsgProperty element) {
        ASTNode lbr = element.getNode().findChildByType(ROSMsgTypes.LBRACKET);
        ASTNode rbr = element.getNode().findChildByType(ROSMsgTypes.RBRACKET);
        if (rbr != null && lbr != null) {
            element.deleteChildRange(lbr.getPsi(),rbr.getPsi()); // this also deletes whats inside the array.
        }
        return element;
    }

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

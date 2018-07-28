package com.perfetto.ros.integrate;

import com.intellij.lang.Language;

public class ROSMsgLanguage extends Language {
    public static final ROSMsgLanguage INSTANCE = new ROSMsgLanguage();

    private ROSMsgLanguage() {
        super("RosMSG");
    }
}

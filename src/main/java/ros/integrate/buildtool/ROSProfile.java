package ros.integrate.buildtool;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * stores information about one ROS build profile, independent of the buildtool.
 * @author Noam Dori
 */
public class ROSProfile {
    @NotNull
    private String name = "new profile";
    @NotNull
    private ROSBuildTool buildtool = ROSBuildTool.CATKIN_MAKE;

    @NotNull
    private String guiName = name;
    @NotNull
    private ROSBuildTool guiBuildtool = buildtool;
    private boolean doInstall = false;
    private boolean doIsolation = true;
    @NotNull
    private String makeArgs = "", cmakeArgs = "", buildtoolArgs = "";
    @NotNull
    private String sourceDir = "", buildDir = "", develDir = "", installDir = "";
    private String allowList = "", denyList = "";

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public ROSBuildTool getBuildtool() {
        return buildtool;
    }

    @NotNull
    String getGuiName() {
        return guiName;
    }

    public void setGuiName(@NotNull String newName) {
        guiName = newName;
    }

    public void save() {
        name = guiName;
        buildtool = guiBuildtool;
    }

    @NotNull
    public ROSBuildTool getGuiBuildtool() {
        return guiBuildtool;
    }

    public void setGuiBuildtool(@NotNull ROSBuildTool newBuildtool) {
        guiBuildtool = newBuildtool;
    }

    public boolean isInstall() {
        return doInstall;
    }

    public void setInstall(boolean doInstall) {
        this.doInstall = doInstall;
    }

    public boolean getIsolation() {
        return doIsolation;
    }

    public void setIsolation(boolean doIsolation) {
        this.doIsolation = doIsolation;
    }


    @NotNull
    public String getMakeArgs() {
        return makeArgs;
    }

    public void setMakeArgs(@NotNull String makeArgs) {
        this.makeArgs = makeArgs;
    }

    @NotNull
    public String getCmakeArgs() {
        return cmakeArgs;
    }

    public void setCmakeArgs(@NotNull String cmakeArgs) {
        this.cmakeArgs = cmakeArgs;
    }

    @NotNull
    public String getBuildtoolArgs() {
        return buildtoolArgs;
    }

    public void setBuildtoolArgs(@NotNull String buildtoolArgs) {
        this.buildtoolArgs = buildtoolArgs;
    }

    public String getSourceDirectory() {
        return sourceDir;
    }

    public String getBuildDirectory() {
        return buildDir;
    }

    public String getDevelDirectory() {
        return develDir;
    }

    public String getInstallDirectory() {
        return installDir;
    }

    public void setSourceDirectory(@NotNull String sourceDir) {
        this.sourceDir = sourceDir;
    }


    public void setBuildDirectory(@NotNull String buildDir) {
        this.buildDir = buildDir;
    }

    public void setDevelDirectory(@NotNull String develDir) {
        this.develDir = develDir;
    }

    public void setInstallDirectory(@NotNull String installDir) {
        this.installDir = installDir;
    }

    public String getAllowList() {
        return allowList;
    }

    public String getDenyList() {
        return denyList;
    }

    public void setDenyList(String denyList) {
        this.denyList = denyList;
    }

    public void setAllowList(String allowList) {
        this.allowList = allowList;
    }

    public Map<String, String> getRawData() {
        Map<String, String> ret = new HashMap<>();
        ret.put("guiName", guiName);
        ret.put("name", name);
        ret.put("buildtool", buildtool.toString());
        ret.put("guiBuildtool", guiBuildtool.toString());
        ret.put("doInstall", doInstall ? "true" : "false");
        ret.put("doIsolation", doIsolation ? "true" : "false");
        ret.put("buildtoolArgs", buildtoolArgs);
        ret.put("cmakeArgs", cmakeArgs);
        ret.put("makeArgs", makeArgs);
        ret.put("buildDir", buildDir);
        ret.put("develDir", develDir);
        ret.put("installDir", installDir);
        ret.put("sourceDir", sourceDir);
        ret.put("allowList", allowList);
        ret.put("denyList", denyList);
        return ret;
    }

    @NotNull
    public static ROSProfile fromRawData(@NotNull Map<String, String> raw) {
        ROSProfile dest = new ROSProfile();
        if (raw.containsKey("guiName")) {
            dest.guiName = raw.get("guiName");
        }
        if (raw.containsKey("name")) {
            dest.name = raw.get("name");
        }
        if (raw.containsKey("buildtool")) {
            dest.buildtool = ROSBuildTool.valueOf(raw.get("buildtool"));
        }
        if (raw.containsKey("guiBuildtool")) {
            dest.guiBuildtool = ROSBuildTool.valueOf(raw.get("guiBuildtool"));
        }
        if (raw.containsKey("doInstall")) {
            dest.doInstall = raw.get("doInstall").equals("true");
        }
        if (raw.containsKey("doIsolation")) {
            dest.doIsolation = raw.get("doIsolation").equals("true");
        }
        if (raw.containsKey("buildtoolArgs")) {
            dest.buildtoolArgs = raw.get("buildtoolArgs");
        }
        if (raw.containsKey("cmakeArgs")) {
            dest.cmakeArgs = raw.get("cmakeArgs");
        }
        if (raw.containsKey("makeArgs")) {
            dest.makeArgs = raw.get("makeArgs");
        }
        if (raw.containsKey("buildDir")) {
            dest.buildDir = raw.get("buildDir");
        }
        if (raw.containsKey("develDir")) {
            dest.develDir = raw.get("develDir");
        }
        if (raw.containsKey("installDir")) {
            dest.installDir = raw.get("installDir");
        }
        if (raw.containsKey("sourceDir")) {
            dest.sourceDir = raw.get("sourceDir");
        }
        if (raw.containsKey("allowList")) {
            dest.allowList = raw.get("allowList");
        }
        if (raw.containsKey("denyList")) {
            dest.denyList = raw.get("denyList");
        }
        return dest;
    }
}

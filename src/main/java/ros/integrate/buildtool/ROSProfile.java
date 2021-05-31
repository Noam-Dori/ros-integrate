package ros.integrate.buildtool;

import org.jetbrains.annotations.NotNull;

/**
 * stores information about one ROS build profile, independent of the buildtool.
 * @author Noam Dori
 */
// TODO: 5/4/2021 implement away the mock stuff
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

    public void setSourceDir(@NotNull String sourceDir) {
        this.sourceDir = sourceDir;
    }


    public void setBuildDir(@NotNull String buildDir) {
        this.buildDir = buildDir;
    }

    public void setDevelDir(@NotNull String develDir) {
        this.develDir = develDir;
    }

    public void setInstallDir(@NotNull String installDir) {
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
}

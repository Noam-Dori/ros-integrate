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
}

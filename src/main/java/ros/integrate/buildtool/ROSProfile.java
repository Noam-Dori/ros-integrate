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

    /**
     * @return the name of this profile
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * @return the buildtool this profile uses to run commands
     */
    @NotNull
    public ROSBuildTool getBuildtool() {
        return buildtool;
    }

    /**
     * @return the name of this profile as shown by the GUI.
     * This is not necessarily the real name of the profile, for that use {@link ROSProfile#getName()}
     */
    @NotNull
    String getGuiName() {
        return guiName;
    }

    /**
     * modifies the name of the profile, from a GUI perspective. To truly change it, use {@link ROSProfile#save()}
     * @param newName the new name of this profile
     */
    public void setGuiName(@NotNull String newName) {
        guiName = newName;
    }

    /**
     * saves changes made on the GUI level to the core level. These changes are permanent.
     */
    public void save() {
        name = guiName;
        buildtool = guiBuildtool;
    }

    /**
     * @return the buildtool that is used to build this profile as shown by the GUI
     * This is not necessarily the real buildtool of the profile, for that use {@link ROSProfile#getBuildtool()}
     */
    @NotNull
    public ROSBuildTool getGuiBuildtool() {
        return guiBuildtool;
    }

    /**
     * modifies the buildtool used by this profile, from a GUI perspective. To truly change it, use {@link ROSProfile#save()}
     * @param newBuildtool the new buildtool used by this profile
     */
    public void setGuiBuildtool(@NotNull ROSBuildTool newBuildtool) {
        guiBuildtool = newBuildtool;
    }

    /**
     * @return true if when building, an "install" step should be run as well, false otherwise.
     */
    public boolean isInstall() {
        return doInstall;
    }

    /**
     * set whether this profile will trigger an "install" step in the build process
     * @param doInstall true if there should be an "install" step, false otherwise.
     */
    public void setInstall(boolean doInstall) {
        this.doInstall = doInstall;
    }

    /**
     * @return the build mode of the profile: true if being built in isolation (each folder is built individually)
     * false if "merged" (everything is thrown together)
     */
    public boolean getIsolation() {
        return doIsolation;
    }

    /**
     * change the build mode
     * @param doIsolation true if the build should be done in isolation, false otherwise.
     */
    public void setIsolation(boolean doIsolation) {
        this.doIsolation = doIsolation;
    }

    /**
     * @return a string-ed list of arguments passed to the make program (ninja or GNU make) when building
     */
    @NotNull
    public String getMakeArgs() {
        return makeArgs;
    }

    /**
     * change the arguments passed to the make program
     * @param makeArgs an arg-string meant for the make build step
     */
    public void setMakeArgs(@NotNull String makeArgs) {
        this.makeArgs = makeArgs;
    }

    /**
     * @return a string-ed list of arguments passed to CMake when building
     */
    @NotNull
    public String getCmakeArgs() {
        return cmakeArgs;
    }

    /**
     * change the arguments passed to CMake
     * @param cmakeArgs an arg-string meant for the CMake build step
     */
    public void setCmakeArgs(@NotNull String cmakeArgs) {
        this.cmakeArgs = cmakeArgs;
    }

    /**
     * @return an arg-string containing additional arguments passed specifically to the buildtool
     */
    @NotNull
    public String getBuildtoolArgs() {
        return buildtoolArgs;
    }

    /**
     * change the arguments passed to the buildtool
     * @param buildtoolArgs an arg-string meant for the first step, the buildtool step.
     */
    public void setBuildtoolArgs(@NotNull String buildtoolArgs) {
        this.buildtoolArgs = buildtoolArgs;
    }

    /**
     * @return the absolute path to the root source directory. Here, the profile will search for all source files.
     */
    public String getSourceDirectory() {
        return sourceDir;
    }

    /**
     * @return the absolute path to the root build directory.
     * Here, the profile will place all intermediate build-related files
     */
    public String getBuildDirectory() {
        return buildDir;
    }

    /**
     * @return the absolute path to the root development directory.
     * Here, the profile will place all the result build-related files, though in a less efficient way
     */
    public String getDevelDirectory() {
        return develDir;
    }

    /**
     * @return the absolute path to the root installation directory.
     * Here, the profile will place all the files necessary to operate, but not develop,
     * the packages built by the profile.
     */
    public String getInstallDirectory() {
        return installDir;
    }

    /**
     * set the absolute path to the source directory.
     * @param sourceDir the new absolute path to the source directory
     */
    public void setSourceDirectory(@NotNull String sourceDir) {
        this.sourceDir = sourceDir;
    }

    /**
     * set the absolute path to the build directory.
     * @param buildDir the new absolute path to the build directory
     */
    public void setBuildDirectory(@NotNull String buildDir) {
        this.buildDir = buildDir;
    }

    /**
     * set the absolute path to the development directory.
     * @param develDir the new absolute path to the development directory
     */
    public void setDevelDirectory(@NotNull String develDir) {
        this.develDir = develDir;
    }

    /**
     * set the absolute path to the installation directory.
     * @param installDir the new absolute path to the installation directory
     */
    public void setInstallDirectory(@NotNull String installDir) {
        this.installDir = installDir;
    }

    /**
     * @return a list of packages that should be built by this profile.
     * packages outside this list will NOT be built.
     */
    public String getAllowList() {
        return allowList;
    }

    /**
     * @return a list of packages that should not be built by this profile.
     * packages in this list will NOT be built.
     */
    public String getDenyList() {
        return denyList;
    }

    /**
     * change the "deny" list
     * @param denyList a list of comma separated package names that should not be built
     */
    public void setDenyList(String denyList) {
        this.denyList = denyList;
    }

    /**
     * change the "allow" list
     * @param allowList a list of comma separated package names that should be built
     */
    public void setAllowList(String allowList) {
        this.allowList = allowList;
    }

    /**
     * convert this object into a raw map that can be written or indexed by a persistence layer.
     * @return a dictionary form of this object
     */
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

    /**
     * convert a map into a true profile object
     * @param raw the raw form of the profile created by a persistent layer
     * @return a new profile containing the data from {@param raw}
     */
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

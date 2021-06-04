![Downloads](https://img.shields.io/jetbrains/plugin/d/ros-integrate)
![Version](https://img.shields.io/jetbrains/plugin/v/ros-integrate)

# What is this plugin about?
[ROS](https://www.ros.org/) is a framework that makes writing robot software significantly easier.
However, implementing a feature for your robot in ROS requires writing a significant amount of code, 
which can cause errors that can be prevented using an IDE.

This plugin provides support for implementing ROS features in your code, 
and improves the experience of using and writing ROS.
The plugin has several end-goals:
1. Allow programming simple robots without boilerplate ("in one click").
2. Make common ROS features more accessible and user-friendly.
3. Raise errors in the robot software and suggest fixes, and provide suggestions for more efficient use of ROS.
4. provide the developer with the means to learn and use the more advanced and less known features ROS provides.

# Target platforms
This plugin targets all IntelliJ based IDEs.

# Community

Every input helps the plugin improve.

Have a question? need help? [ask away!](https://github.com/Noam-Dori/ros-integrate/issues/new?assignees=Noam-Dori&labels=question&template=ask-a-question.md&title=)

Want something new? [request a feature!](https://github.com/Noam-Dori/ros-integrate/issues/new?assignees=Noam-Dori&labels=feature&template=feature_request.md&title=)

Ran into an issue? [report it!](https://github.com/Noam-Dori/ros-integrate/issues/new?assignees=Noam-Dori&labels=bug&template=bug_report.md&title=)

Want to try the latest version or edit the code? [here is a guide!](https://github.com/Noam-Dori/ros-integrate/wiki/Working-From-Source)

# Planned Features

**version 0.1.7:** CMake in ROS
* Msg,Srv,Action files linked from CMake clauses
* CMake code generation when adding new messages/services
* additional CMake features...

**version 1.0:**
* *.launch support
* run & debug launch files
* automatic ``roscore``
* new workspace option in project wizard
* new ROS meta/package option
* ROS related inspections in C/C++/python code
* dynamic_reconfigure support (?)
* RQT and RViz windows
* CMake code generation
* catkin support
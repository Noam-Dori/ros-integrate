# ROS integration
A plugin for IntelliJ based IDEs with the purpose of allowing easy use of the ROS framework.

**plans for version 0.1.5:**
* package.xml:
    * package linking from \<depends\>
    * clause completion (such as depends, etc.)
    * package dependency graph (getDependencies)
    * package indexing reads package.xml for context
    * more stuff
* fix more NPEs

**plans for version 0.1.6:**
* CMake in ROS:
    * Msg,Srv,Action files linked from CMake clauses
    * CMake code generation when adding new messages/services
    * more stuff
* fix even more NPEs

**plans for version 1.0:**
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
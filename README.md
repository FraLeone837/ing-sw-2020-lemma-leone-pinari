GROUP COMPONENTS

Lemma Simone, 
Leone Francesco, 
Pinari Etion



IMPLEMENTED FUNCTIONALITIES

Complete rules + CLI + GUI + Socket + 1 FA (5 Advanced Gods)



JAR COMPILATION

In order to compile the JAR files, you have to modify the attribute in 'mainclass'
in the maven-shade-plugin with the following values:       
Server: Controller.ViewManager,   
Gui: View.GUIMode.GuiMain,   
Cli: View.CliMode.CliMain  

Afterwards you need to launch the packaging with maven.


 
JAR EXECUTION:

To open the Gui you need to double-click on the jar file icon, while for the cli you will need the cmd.
The server can be launched both with icon and cmd, but notice that if you use the icon you can close it only from task manager.

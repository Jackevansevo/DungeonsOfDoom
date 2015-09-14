#README
Dungeons of Doom v.3.0
This version of Dungeons of Doom has a graphical user interface and allows user to play each other
over a network

##COMPILING
To compile the source, please use the following command in the directory containing src and bin.

			$ javac -d out -cp src src/dod/*.java

##RUNNING

###Creating a Server Instance
The game cannot be ran without first connecting to a server instance
Create an instance of the server with

			$ java -cp out dod/DodServer [-p] [-m]

where -p specifies the port number for the server to be hosted on and -m specifies
the map to be played. For example

			$ java -cp out dod/DodServer 4444 defaultMap

###Connecting a Client
A client can then be connected with the following

			$ java -cp out dod/Client [-p] [-h]

where -p specifies the port number and -h specifies the hostname to connect to. So
in order to connect to the above example you'd execute the following

			$ java -cp out dod/Client 4444 127.0.0.1

###Game Demo
Alternatively a Game Demo wrapper can be run to quickly spin up a server and create
multiple clients, allowing people to quickly create a multiplayer game on a single computer

			$ java -cp out dod/GameDemo
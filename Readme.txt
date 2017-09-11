RoadGenerator
-------------
1)Source code can be found in RoadGenerator/src/ folder
2)As soon as this project is run, it will ask user to enter number of junctions
3)Reading that from the user, calculates number of other segments and generates a RoadNetwork in the format of a Dot file.


Graph Importer
--------------
1)Source code can be found in GraphImporter/src/ folder
2)The output dot file from Road Generator should be placed in this project folder.
3)As soon as this code is run, it reads the dot file and asks the user to enter source and destination.
4)Assuming the user is just before the source, it dynamicaaly generates best possible alternative path calculating Estimated time to reach at everypoint till he
  reaches the destination.
5)The output is printed in the console for all the iterations.
6)A visualized dot file will be exported which tells us the current status of the user route after that particular iteration.
7)Orange coloured segment is the one on which user is currently moving and Green coloured segments are the current best path's segments.


External Library Used:
----------------------
This folder contains gephi toolkit jar file which should be added to the RoadGenerator and GraphImporter projects. This library helps to read the dot file
and find out nodes and edges without much difficulties.


Seperate Bellmanford and DFS:
-----------------------------
It contains the seperate project code for the algorithms BellmanFord and DFS. There is no need of these to run the 'Road Generator' and 'Graph Importer' projects. 
These are just given for understanding actual algorithms. 
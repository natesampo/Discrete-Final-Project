# Discrete-Final-Project
In order to run this project, run Graph.java after compiling all classes. The series of team formation algorithms which occur in a reasonable amount of time will run.

## Team Creation Priorities and Formula


## Used Algorithms

### Greedy Clique Creation V1
Starting with the highest edge left in the graph, cliques were created by adding the best fit member to the current, incomplete clique. Thie pattern continued until all full teams were created. If there were overflow persons, they were added to the teams they fit best with. 

### Greedy Clique Creation V2
For the second greedy clique creation, we realized that if we got unlucky there could be no final team that could be created. What this therefore does is take in additional int about the number of top edges that should be skipped when starting with the first two members of a team. If at the conclusion of one round no solution has been found, it then recursively tries until a depth set within the code (defaulting to 10).

### Brute Force Clique Assignments V1 (recursiveSoln)
The brute force clique assignments first took all valid cliques that could be created from the given person profiles and then recursively started picking valid combinations of the teams. It was too long to feasibly run.

### Brute Force Clique Assignments V2 (recursiveSolnV2)
[[how was this different again?]]

### Greedy Clique Selection
The greedy clique selection algorithm determined what the mean clique score should be by determining the mean skill total of the profiles and then multiplying it by the clique size. After this, it uses the list of all possible cliques from the students to draw teams with the closest score to the mean possible. Any additional members are then added to the team of four where they fit best. This was a compromise between checking all possible cliques and creating more balanced cliques.

### Clique Coloring

## Outcomes

## Conclusion

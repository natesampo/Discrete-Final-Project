# Discrete-Final-Project
In order to run this project, run Graph.java after compiling all classes. This will call the main method, which will run Greedy Clique Creation V1, Greedy Clique Creation V2, Greedy Clique Selection, and Clique Coloring, the solutions that are feasibly runnable.

## Team Creation Priorities and Formula


## Used Algorithms

### Greedy Clique Creation V1
The first greedy clique creation starts each new clique by finding the edge with the highest weight in the adjacency matrix. From there, it searches all other nodes to see which is most strongly connected to those already in the clique. This pattern continues until the maximum number of full cliques is formed. If there were overflow persons, they were added to the teams they fit best with by total edge weightings between the current members and the prospective overflow person.

### Greedy Clique Creation V2
For the second greedy clique creation, we innovated on the first greedy clique creation algorithm by realizing if we got unlucky there could be no final team that could be created with the leftover members. What this therefore does is take in additional value indicating the number of top edges that should be skipped when starting with the first two members of a team. If at the conclusion of one round no solution has been found, it then recursively increases the number of top edges skipped by one until a set depth  (the default of which is 10).

### Brute Force Clique Assignments V1 (done recursively)
The brute force clique assignments first took all valid cliques that could be created from the given persons who needed to be placed in teams and then recursively started picking valid combinations of the teams. The goal was to generate all teams, compare them to see which have the strongest edges, and pick groups in descending orders. However, it was determined far too long to run. Looking at this combinatorically and assuming a worst-case scenario of everyone liking each other and there being 0 silver bullets (which wouldn't happen, but for computer science runtime analysis we have to do worst-case scenarios), we can calculate exactly how many combinations of teams there are. Assuming there are the 95 POE students wee were working with and that the goal was to have teams of 4, we could generate (95 Choose 4) or 3183545 different cliques. From these cliques, we have to choose 23 teams, and (3183545 Choose 23) is
14269997543090240956357300053952460562995691697881045897728015592300706590585628050714184284327695008067970348080814055177143300. Put in a readable format, that's ~1.437 x 10^123. As we didn't have a supercomputer on hand, it was determined to take too long to run.

### Brute Force Clique Assignments V2 (done recursively)
Before fully writing out how many computations were going to be necessary for the brute force clique assignments, we decided to try to make the code for brute force clique formation a lot faster. In order to do this, as cliques were selected, possible cliques that contained members of the selected clique were eliminated and not passed forward to future iterations. This allowed for significantly fewer calculations, as ~1/23 of the cliques had each member, so the algorithm had to process significantly fewer inward calculations. It was still way too large, which is why the Greedy Clique Selection algorithm was created.

### Greedy Clique Selection
The greedy clique selection algorithm determined what the mean clique score should be by determining the mean skill total of the profiles and then multiplying it by the clique size. After this, it uses the list of all possible cliques from the students to draw teams with the closest score to the mean possible. Any additional members are then added to the team of four where they fit best. This was a compromise between checking all possible cliques and creating more balanced cliques.

### Clique Coloring

## Outcomes

## Conclusion

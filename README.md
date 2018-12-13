# Discrete-Final-Project
In order to run this project, run Graph.java after compiling all classes. This will call the main method, which will run Random Teaming, Greedy Clique Creation V1, Greedy Clique Creation V2, Greedy Clique Selection, and Colored Cliques, the solutions that are feasibly runnable.

## Team Creation Priorities and Formula
Before making any of the cliques, the group had multiple extended discussions over what is necessary to account for when creating teams as well as how heavily they should be weighted. We decided the following were all important considerations when making teams:
* Silver bullets
* What people were skilled in (Mechanical Design, Fabrication, ECE, Software, Design)
* People's project preferences
* People's partner preferences

Silver bullets we tried to guarantee, and in almost all our algorithms we were able to guarantee it, but preferences were treated as just preferences overall with an emphasis in an even skill distribution. There's a lot of discussion about whether this was a fair treatment, as sometimes instead of distributing people who could be determined "poor" in terms of skill total and/or number of silver bullets accumulated they were evenly distributed amongst teams. We justify this by saying that we want everyone to have a fairly similar experience when using our algorithms for team creation. With this in mind, the algorithm we used in raw Java format to determine the strength of edge weights was:

```Java
(((p1.silverBullets.contains(p2.id) || p2.silverBullets.contains(p1.id))) ? 0 : skillsWeight * (1-(helper.dotProduct(p1.skills, p2.skills)/p1.skills.length)) + preferenceWeight * (p1.preferredPartners.contains(p2.id) ? 1 : 0) + projectWeight * sameProjects);
```

Interpreting this from left to right:
```Java
(((p1.silverBullets.contains(p2.id) || p2.silverBullets.contains(p1.id))) ? 0 : ...)
```
This component of the line checks to see if either people being considered silver bulleted each other. If one had, then the edge weight was set to 0 to represent that there was no connection between the two.
```Java
skillsWeight * (1-(helper.dotProduct(p1.skills, p2.skills)/p1.skills.length)) + ...;
```
We then consider the skill differences between the students. We do this by taking the dot product of the two, normalizing it, averaging the score by the total number of skills, then subtracting this total from 1. This meant people with complementing skills (for example, mechanical design and coding) to have a stronger edge weight, indicating that they were a better fit for a balanced team.
```Java
... + preferenceWeight * (p1.preferredPartners.contains(p2.id) ? 1 : 0) + projectWeight * sameProjects);
```
Finally, we considered students' preferences when it came to preferred partners and the similarity in projects. sameProjects was the number of projects that both students were interested in; the preferredPartners was a one-way check to see if the current person had wanted the second person. This allowed for 1-way preferredPartners, which was purposeful. Put together, we got a reasonably high level edge weighting algorithm for our project.

## Used Algorithms

### Random
One of the first algorithms we implemented, random teaming, was not intended to generate useful teamings, but rather to generate teamings for our future algorithms to test against. Random teaming, like our other algorithms, is deterministic, meaning that with the same data it will have the same output, allowing for better comparisons with other algorithms. The key difference between random teaming and our other team formation strategies is that random teaming does not take edge weights into consideration at all, and only pairs the first available people who haven't silver bulleted each other together.

### Greedy Clique Creation V1
The first greedy clique creation starts each new clique by finding the edge with the highest weight in the adjacency matrix. From there, it searches all other nodes to see which is most strongly connected to those already in the clique. This pattern continues until the maximum number of full cliques is formed. If there were overflow persons, they were added to the teams they fit best with by total edge weightings between the current members and the prospective overflow person.

### Greedy Clique Creation V2
For the second greedy clique creation, we innovated on the first greedy clique creation algorithm by realizing if we got unlucky there could be no final team that could be created with the leftover members. What this therefore does is take in additional value indicating the number of top edges that should be skipped when starting with the first two members of a team. If at the conclusion of one round no solution has been found, it then recursively increases the number of top edges skipped by one until a set depth  (the default of which is 10).

### Brute Force Clique Assignments V1 (done recursively)
The brute force clique assignments first took all valid cliques that could be created from the given persons who needed to be placed in teams and then recursively started picking valid combinations of the teams. The goal was to generate all teams, compare them to see which have the strongest edges, and pick groups in descending orders. However, it was determined far too long to run. Looking at this combinatorically and assuming a worst-case scenario of everyone liking each other and there being 0 silver bullets (which wouldn't happen, but for computer science runtime analysis we have to do worst-case scenarios), we can calculate exactly how many combinations of teams there are. Assuming there are the 95 POE students we were working with and that the goal was to have teams of 4, we could generate (95 Choose 4) or 3183545 different cliques. From these cliques, we have to choose 23 teams, and (3183545 Choose 23) is
14269997543090240956357300053952460562995691697881045897728015592300706590585628050714184284327695008067970348080814055177143300. Put in a readable format, that's ~1.437 x 10^123. As we didn't have a supercomputer on hand, it was determined to take too long to run.

### Brute Force Clique Assignments V2 (done recursively)
Before fully writing out how many computations were going to be necessary for the brute force clique assignments, we decided to try to make the code for brute force clique formation a lot faster. In order to do this, as cliques were selected, possible cliques that contained members of the selected clique were eliminated and not passed forward to future iterations. This allowed for significantly fewer calculations, as ~1/23 of the cliques had each member, so the algorithm had to process significantly fewer inward calculations. It was still way too large, which is why the Greedy Clique Selection algorithm was created.

### Greedy Clique Selection
The greedy clique selection algorithm determined what the mean clique score should be by deter7mining the mean skill total of the profiles and then multiplying it by the clique size. After this, it uses the list of all possible cliques from the students to draw teams with the closest score to the mean possible. Any additional members are then added to the team of four where they fit best. This was a compromise between checking all possible cliques and creating more balanced cliques.

### Colored Cliques
One of our original ideas for creating teams was to generate them based on each team member's major, and try to have a good representation of majors within each team. What we are attempting to create here is a complete set of 'rainbow cliques', teamings of 4-5 nodes with as many different colors (majors) represented as possible. This algorithm first traverses the list of profiles created to represent the students and assigns each student a major according to their reported skills. Currently, we are operating with three majors: Electrical and Computer Engineering, Software Engineering, and Mechanical Engineering. After assigning each student to one of these majors, we begin team creation. This is done in a greedy fashion, where we identify the highest edge weight in the graph between two students of different majors and assign those two to a team. All other students with the same majors as the two already chosen students have reduced edge weights to encourage other majors and discourage the same majors. We continue this pattern for every member of every team until every team has 4 people on it, a typical PoE team size. After every team has 4 members, any leftover members are assigned onto teams based on where they would fit best, keeping in mind how many of each major each team already has and the major of each leftover person.

## Outcomes
![alt text](https://raw.githubusercontent.com/natesampo/Discrete-Final-Project/master/charts/chart.png "Average Team Project Preference Overlap")
![alt text](https://raw.githubusercontent.com/natesampo/Discrete-Final-Project/master/charts/chart1.png "Percent of Partner Preferences Met")
![alt text](https://raw.githubusercontent.com/natesampo/Discrete-Final-Project/master/charts/chart2.png "Highest and Lowest Skill Teams")
![alt text](https://raw.githubusercontent.com/natesampo/Discrete-Final-Project/master/charts/chart3.png "Range of Teams' Total Skill")

### Time Complexity of Each Algorithm
* **Random Teams:** O(n)
* **Greedy Clique Creation:** O(n^3)
* **Greedy Clique Selection:** O(number of cliques ^ number of teams)
* **Colored Cliques:** O(n^2)

## Conclusion
### Pros and Cons of Each Algorithm
#### Random Teams
Pros: VERY Fast

Cons: Doesn't care about you

#### Greedy Clique Creation
Pros: Relatively fast, creates good teams, and meets many partner and project preferences

Cons: Will sometimes create lopsided teams, additional time complexity when stuck with people who have silver bulleted one another at the end

#### Greedy Clique Selection
Pros: Creates teams that are very good on average, slim distribution of team skills

Cons: Slow, bias towards similarly numbered individuals, often ignores partner and project preferences

#### Colored Cliques
Pros: Fast, inexpensive algorithm, meets most partner and project preferences, standard deviation between teams' skills is very low

Cons: Average team score is often lower than some other algorithms, sometimes ignores partner preferences of the same major



### Use Cases

We've identified several potential use cases for our algorithms based upon teaming in classes at Olin. We hope these suggestions will help provide some insight into which algorithms work best under what conditions.

#### Design Nature (DesNat)
* Team members don't know each other well
* Team members don't know much about the projects

*Greedy Clique Selection*

Because this algorithm typically only takes skill orthogonality into consideration and often disregards partner and project preferences, it makes sense to use it in a situation where partner and projects preferences mean little, as this is the students' first semester at Olin.

#### Products and Markets (PnM)
* Team member skills don't matter as much
* Partner and project preferences matter

*Colored Cliques* or *Greedy Clique Creation*

As Products and Markets is not a typical Engineering course, Engineering skills matter little. Partner and projects preferences weigh heavily into successful teaming here and the two algorithms that accomplish those best are Greedy Clique Creation and Colored Cliques.

#### Principles of Engineering (PoE)
* Team member skills are very important
* Silver bullets are especially important
* Partner and project preferences are less important

*Greedy Clique Selection*

For a successful teaming experience in Principles of Engineering, it is integral that teams' skills are well balanced and as high as possible. Because Greedy Clique Selection accomplishes creating the highest average team skills while maintaining a low standard deviation, we recommend its use here.

#### Senior Capstone Program (SCOPE)
* Team member skills are somewhat important
* Silver bullets are especially important
* Partner and project preferences are important

*Colored Cliques*

For teaming in SCOPE, we are looking for an algorithm that accomplishes most partner and project preferences, while maintaining a relatively even distribution of teams. The algorithm that does this best is Colored Cliques, thus our recommendation for its use in SCOPE.


# Annotated Bibliography
#### Anderson, R. (n.d.). Windows. Retrieved December 11, 2018, from http://everythingcomputerscience.com/algorithms/Greedy_Algorithm.html
We were looking for a good overview of what a greedy algorithm was, and found this website. It has an overall good overview of it as well as some source code that we could reference outside of our personal experiences. As all team members were familiar at a high level with how to write different greedy algorithms, the heart of the discussion we typically faced was what should we be focusing on while creating cliques.  <br/><br/>

#### Angles d’Auriac, J., El Maftouhi, H., Legay, S., Cohen, N., Harutyunyan, A., & Manoussakis, Y. (2016, July 26). Connected Tropical Subgraphs in Vertex-Colored Graphs(Rep.). Retrieved December 11, 2018, from Discrete Mathematics and Theoretical Computer Science website: https://www.dmtcs.org/dmtcs-ojs/index.php/dmtcs/article/download/2765/4764.pdf
We used this source in order to read more about tropical graphs and subgraphs as well as provide insight into how we could best approach this program algorithmically. Sarah had suggested that we review this document, and it was very helpful.  <br/><br/>

#### Graph Coloring Exploration (Tech.). (n.d.). Retrieved December 11, 2018, from Sarah Spence Adams website: https://drive.google.com/open?id=16IaoO2pte9BbkEVT_uXAsTyPGf3PUMgq   
We referenced Annie Kroo’s implementation of POE teaming in an attempt to see another way that the same teaming that we were attempting was accomplished. Although it was useful to look at, we ended up mostly defining our own ways to solve things based on what we researched and were familiar with.  <br/><br/>

#### Millner, A. (2018, December 11). [POE Teaming Survey Data]. Unpublished raw data.  
We needed some physical metric of how our algorithms were doing, and we decided the best way to do this was to use some data from an Olin teaming class. As Kyle was a NINJA for POE this semester, we were granted access to the anonymized teaming data for this year’s POE teams. We used this data for testing purposes throughout our project.  <br/><br/>

#### StackOverflow. (n.d.). Retrieved December 11, 2018, from https://stackoverflow.com/  
As is the norm for programmers, as we were implementing our solutions we encountered a variety of issues. The easiest way to deal with these issues was typically to visit StackOverflow. It also occasionally provided hints at ways to streamline our code.

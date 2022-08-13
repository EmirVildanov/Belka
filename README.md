# BlaBlaElka
## Application for finding elka* (and not only) interlocutors

<img src="readme_data/BlaBlaElkaPhoto.png" width="1152" height="648" alt="Elka">

### About
This application supposed to an application for students of SPbU** who live in the PUNK*** dormitory and usually travel to town by suburban train.
It would give them an opportunity to find an interlocutor for a 40-50 minutes ride in two directions:
* From PUNK to town
* From town to PUNK

After some code was written the idea of application of more wide use came up. Why to limit yourself with a suburban train when you can find an interlocutor in any
kind of public transport or even pedestrian walk. Everything you have to do is to choose a route, transport, time and create an application for other
people to join you (much like you do in BlaBlaCar, for example).

### Rules
* User have to fill an account info with following information:
  * Name
  * Surname (optionally)
  * Age
  * Small about section
  * Photo (optionally)
* There works a rating system in the app: any user have to leave a rate and write a meaningful feedback about 
the interlocutor he/she was matched with.
* Every route has two points: FROM and TO. Both of these points have their identification ids. There are 2 types of routes in the app:
  * Hardcoded. They are the one you can choose from the list.
  * User created. The ids for them are generated randomly and saved with custom mark addition.

### Note
*Elka -- Russian slang name of suburban train.  
**SPbU -- St Petersburg University.  
***PUNK -- Petrodvorets Educational and Scientific Complex of SPbU.

### TODO:
- [ ] Update .sh so it creates accounts database on machine  
- [ ] data/db dir must be created in the root folder: `sudo chmod -R go+w /data/db`
- [ ] [look at course on kubernetes](https://www.edx.org/course/introduction-to-kubernetes)
- [ ] configure kubernetes
- [ ] From [Docker security tips](https://blog.aquasec.com/docker-security-best-practices): [run the docker container as a non-root user](https://docs.docker.com/engine/security/rootless/)
- [ ] [pass configs to image through secrets](https://kubernetes.io/docs/concepts/configuration/secret/)

### Dev links:
I would like to see you as a contributor to this project :)
* [Link to chatbot states](https://lucid.app/lucidchart/be301ab7-e7b3-4da6-8945-35b652179a83/edit?invitationId=inv_b88953e5-c8e9-458f-963f-41b3ad14658e&page=0_0#)
* [Link to database diagram](https://dbdiagram.io/d/62ed062bc2d9cf52fa52969a)
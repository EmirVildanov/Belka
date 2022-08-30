# BeLkA

## Telegram bot for finding an interlocutor in public transport rides and pedestrian walks

<img src="readme_data/Elka.jpg" width="1280" height="361" alt="Elka">

### About

Like BlaBlaCar, but BlaBlaElka<sup>[1]</sup>, but **BLA** stands for intention to talk and not for the fact of talk.

This application supposed to an application for students of SPbU<sup>[2]</sup> who live in the PUNK<sup>[3]</sup>
dormitory and usually travel to town by suburban train. It would give them an opportunity to find an interlocutor for a
40-50 minutes ride in two directions:

* From PUNK to town
* From town to PUNK

After some code was written the idea of application of more wide use came up. Why to limit yourself with a suburban
train when you can find an interlocutor in any kind of public transport or even pedestrian walk. Everything you have to
do is to choose a route, transport, time and create an application for other people to join you (much like you do in
BlaBlaCar, for example).

#### Note

[1] Elka -- Russian slang name of suburban train.  
[2] SPbU -- St Petersburg University.  
[3] PUNK -- Petrodvorets Educational and Scientific Complex of SPbU.

### Rules

* User have to fill an account info with following information:
    * 👤 Name
    * 👥 Surname (optionally)
    * 🔢 Age (optionally)
    * 🗒 Small about section
    * 📸 Photo (optionally)
* There works a feedback system: users leave a rate and write a meaningful feedback about the interlocutor he/she was
  matched with. User may leave feedback in two cases:
    * When the application succeeded and there was a match between two users. User must add feedback after every
      succeeded match. **If user doesn't leave a feedback, it gets unreliability mark**;
    * While analyzing other user's profile in two cases:
        * Analyzing existed application;
        * Analyzing request for accepting his/her application.

      This feedback will override the old one in case it exist.
* Every route has two points: FROM and TO. Both of these points have their identification ids. There are 2 types of
  routes in the app:
    * Fetched by Yandex Rasp API. They are the one you can choose from the list;
    * User created.

  Pedestrian application is the only one where FROM and TO points may coincide.
* It **would** be great to have an opportunity to subscribe on new applications updates, so you don't miss an
  opportunity.

### Architecture

* **KotlinTelegramBot** for telegram bot frontend
* **[VkJavaSDK](https://github.com/VKCOM/vk-java-sdk)** for Vk bot frontend **TBD**
* **MongoDb** for storing data
  * Creates collections automatically when first requested
* **Redis** for caching:
    * Network calls (such as requests to Yandex Rasp API)
    * MongoDb calls
* **Ktor** for http requests
* **Yandex Rasp API** for fetching rides info. On first request <FROM-TO> in a day it stores information in db (cache?).

### Development

Tested on Ubuntu 20.04.4 LTS.

* Main functionality:
    1. Run `mongod`.
        * Using IntelliJIDEA you may connect to local MongoDb in order to work with data more comfortably.
    2. Run `redis-server`.
        * Redis plugin for IntelliJIDEA is "Paid", OMG.
    3. Run `./gradlew run`.
    4. Communicate with **@BeLkAH_bot**.
* Tests:
    * Run `./gradlew test`.
* Code style. I **decided** not to use ktlint as I had problems with its configuration. Using detekt, guys:
    * Run `./gradlew detekt` before pushing.
        * File > Settings > Project Settings > Code Style > Kotlin > Imports > General > Use single name import.
        * Remove everything from "Package to use Imports with *".

### Dev links:

Lucid app gives limited number of elements on diagram for non-premium users. That's why there are two diagrams: one for
main and one for side logic.

* [Link to chat bot **
  side** logic states](https://lucid.app/lucidchart/be301ab7-e7b3-4da6-8945-35b652179a83/edit?invitationId=inv_b88953e5-c8e9-458f-963f-41b3ad14658e&page=0_0#)
* [Link to chat bot **
  main** logic states](https://lucid.app/lucidchart/2fadb7ff-78ad-4e3d-a2ea-88541bf43511/edit?viewport_loc=-1259%2C82%2C3328%2C1684%2C0_0&invitationId=inv_95e2bc04-b5fd-4e88-abc6-63ce8b9980d3#)
* [Link to database diagram](https://dbdiagram.io/d/62ed062bc2d9cf52fa52969a)

I would like to see you as a contributor to this project :)
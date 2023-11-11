# SYSC 4806 Project - Opinion Owl

Build Status: ![Build Status](https://github.com/bardia-p/OpinionOwl/actions/workflows/maven.yml/badge.svg)

Deployment Status: ![Deployment Status](https://github.com/bardia-p/OpinionOwl/actions/workflows/main_opinionowl.yml/badge.svg)

### Group Members:
- [Bardia Parmoun](https://github.com/bardia-p)
- [Max Curkovic](https://github.com/maxcurkovic)
- [Dorothy Tran](https://github.com/dorothytran)
- [Anthony Massaad](https://github.com/Anthony-Massaad)
- Cassidy Pacada

## Description:
A mini version of SurveyMonkey in which surveyors can create a survey with a three categories of questions. These include open-ended questions with a text-based answer,
questions where a user is asked to select a number within a range, and multiple choice questions.  The surveyor can close the survey at any point which would prevent new users from responding.
Closing the survey generates a response form that contains a compilation of all the answers given in the survey. Text answers are displayed as written, answers to ranged questions are
displayed as a histogram, and answers to multiple choice questions are displayed as a pie chart.

## Milestone 1
For milestone 1 of the project, the survey creation functionality was implemented. This includes the creation of a survey with any combination of questions, saving the survey, and
retrieving it so that users can access it and fill out information. This milestone contains the app's initial structure and also included setting up the processes and
workflows to be used throughout the project.

## Plan for Next Milestone
All open issues and planned work can be found on our [kanban board](https://github.com/users/bardia-p/projects/2). The current issues in backlog are listed below.
- [Issue 17 - Add CSS Styles to the web pages](https://github.com/users/bardia-p/projects/2?pane=issue&itemId=44211716)
- [Issue 18 - Edit Surveys](https://github.com/users/bardia-p/projects/2?pane=issue&itemId=44211831)
- [Issue 20 - Fill Surveys](https://github.com/users/bardia-p/projects/2?pane=issue&itemId=44212227)
- [Issue 21 - Close survey and display the results in the charts](https://github.com/users/bardia-p/projects/2?pane=issue&itemId=44212259)
- [Issue 22 - Register users](https://github.com/users/bardia-p/projects/2?pane=issue&itemId=44212259)
- [Issue 23 - Login users](https://github.com/users/bardia-p/projects/2?pane=issue&itemId=44212340)

## Dependencies:
* Java JDK 17
* Spring Boot
* JUnit
* Project Lombok
* Maven
* Ajax

## Installation
1. Clone the project from the repo and open the project in IntelliJ.
2. Ensure that the dependencies listed above are all installed and configured properly.
3. Go to the class *OpinionOwlApplication* in src > main > com.opinionowl.opinionowl and run it. (either with green arrow or by right clicking)
4. Open your browser of choice and type in 'localhost:8080'

## Usage
1. Opinion Owl can be accessed through https://opinionowl.azurewebsites.net/
2. From here you can act as a surveyor to add text questions, multiple choice questions, or numeric range questions by clicking the respective 'Add Question' buttons.
3. Click on 'Form Title' to edit the survey name or click on the text associated with each question to edit the content.
4. For multiple choice questions, click the '+' sign to add choices as needed. These can also be removed.
5. To save the survey, click 'create form'.

## Diagrams
- [UML diagram](diagrams/Milestone1_UML_class_diagram.png)
- [Schema](diagrams/Milestone1_ER_Diagram.png)

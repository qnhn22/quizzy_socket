# Quizzy: Real-Time Multiplayer Quiz Game

## Introduction
Quizzy is a real-time multiplayer quiz game implemented in Java. It allows multiple players to compete against each other in answering trivia questions. The game utilizes Java Datagram and Socket classes to establish a TCP connection between the server and client programs, ensuring smooth communication between players.

## Features
- Real-time multiplayer gameplay.
- Multiple-choice trivia questions.
- Server-client architecture for seamless communication.
- Multithreading for efficient management of game timelines.
- Timer functionality to keep track of time per question.
- Synchronized functions to ensure data consistency in a multi-threaded environment.

## Installation
To run Quizzy on your local machine, follow these steps:

1. Clone the Quizzy repository.

2. Compile the Java source files using your preferred Java compiler.

3. Run the server program on one machine.

4. Run the client program on multiple machines to join the game.

## How to Play
1. Launch the Quizzy client program on your machine.
2. Connect to the Quizzy server by providing the server's IP address and port number.
3. Wait for other players to join.
4. Once the game starts, answer the trivia questions within the allotted time.
5. Earn points for each correct answer.
6. The player with the highest score at the end of the game wins.

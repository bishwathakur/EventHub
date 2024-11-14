# EventHub

EventHub is an Android application built with Kotlin that enables seamless event management and social interaction. The app allows users to create, discover, and interact with events while connecting with other users. It leverages Firebase for backend services and provides a rich, engaging experience through real-time features and a user-friendly design.

---

## Table of Contents

- [Core Architecture](#core-architecture)
- [Features](#features)
  - [Authentication](#authentication)
  - [Event Management](#event-management)
  - [Social Features](#social-features)
  - [UI Components](#ui-components)
- [Firebase Integration](#firebase-integration)
- [Getting Started](#getting-started)
- [Contributing](#contributing)
- [License](#license)

---

## Core Architecture

EventHub is built with a modular architecture that organizes functionality into feature-specific components. The main activity uses a bottom navigation bar to manage fragments and navigate between sections. Key architectural components include:

- **Main Activity** with bottom navigation (Home, Profile, Chats)
- **Fragment-based navigation system** to streamline in-app transitions and user experience

---

## Features

### Authentication

EventHub uses Firebase Authentication to enable a secure, flexible authentication system, providing users with seamless sign-in and sign-up options.

- **Sign In/Sign Up functionality**
- **Profile management** with the ability to upload profile images

### Event Management

Users can manage events effortlessly, including creating, updating, and deleting events. EventHub also supports image uploads and date selection to enhance the event creation experience.

- **Create, read, update, delete (CRUD) events**
- **Image upload** capability for event banners and profiles
- **Date selection** with an interactive calendar dialog

### Social Features

Enhancing the social aspect, EventHub includes real-time interactions and communication options to keep users connected and engaged with ongoing events.

- **Real-time chat system** for one-on-one or group communication
- **Comment functionality** on events for user feedback and interaction
- **Like and registration system** to boost engagement on events
- **Event sharing** with deep links to allow easy sharing and access

### UI Components

EventHub is designed with Material Design principles, ensuring a consistent and modern user interface across different devices.

- **Material Design** implementation for intuitive UI
- **Custom themes** supporting both light and dark modes
- **RecyclerView** for efficient list rendering
- **SwipeRefreshLayout** for refreshing event data
- **Progress indicators** to improve user feedback during loading states

---

## Firebase Integration

EventHub integrates Firebase services to provide a reliable backend for data storage, real-time communication, and authentication.

- **Firebase Realtime Database** to store event and user data
- **Firebase Storage** for storing user and event images
- **Firebase Authentication** to manage secure user access

---

## Getting Started

To get a local copy up and running, follow these steps.

### Prerequisites

- Android Studio
- Firebase Project for API keys and configuration

### Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/bishwathakur/EventHub.git
    ```

2. Open the project in Android Studio.

3. Set up Firebase:
    - Create a Firebase project.
    - Enable Authentication, Realtime Database, and Storage.
    - Download the `google-services.json` file and add it to the `app` directory.

4. Build and run the project.

---

## Contributing

Contributions are welcome! If you'd like to improve EventHub, please fork the repository and create a pull request. For major changes, open an issue first to discuss what you’d like to change.

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

EventHub brings people together through events and social interaction. Enjoy exploring and managing events with this versatile app!

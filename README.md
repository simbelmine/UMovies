# UMovies
An application for browsing recent movies and allows the user to save favorites, play trailers, and read user reviews. 


# Introduction
Steps for trying out this application:
* Compile and install the mobile app onto your mobile device or emulator.
* Application: start the app from menu or some of the screens.

This application aims to demonstrate the usage of RecyclerView, Picasso, ContentResolver, ContentProvider, AsyncTask Loader, SettingsFragment.
The app also focuses on providing a simple, yet clean design and UI for both phones and tablets.

Some of the UI widgets and design patterns used in the mobile app include:
* Use of Material theme including definition of primary and accent colors
* AppCompat usage for Material theme backward compatibility
* Metrics and keylines based on the Material guidelines
* Window content and activity transitions based on the Material guidelines
* Use of the Design Support Library


# Pre-requisites
* Android SDK 25
* Android Build Tools v25.2.0
* Android Support Repository


# Screenshots
<img src="screenshots/composite_screenshot.png?raw=true" height="500" alt="Screenshot"/> 

# Getting Started
Steps: 
* To fetch popular movies, you will use the API from themoviedb.org.
* If you don’t already have an account, you will need to create one in order to request an API Key.
https://www.themoviedb.org/account/signup
* In your request for a key, state that your usage will be for educational/non-commercial use.
* You will also need to provide some personal information to complete the request. 
* Once you submit your request, you should receive your key via email shortly after.
* When you have the API key, add it in res/values/strings/   < string name="API_KEY" >

# License
Copyright 2017 The Android Open Source Project, Inc.

Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

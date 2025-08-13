# BrzoDoLokacije

## 📖 Description

Android application that functions as a social network, with the main idea of sharing realistic photos of tourist locations.

## 💡 Ideas and Prototypes

You can view the Figma prototype at the following [link](https://www.figma.com/file/X1pf7FB1KpEHQrVT2ziyJA/Untitled?node-id=0%3A1).

## 📥 Download

The application can be downloaded from the following [link](http://softeng.pmf.kg.ac.rs:10051/).  
Access password: `123passwd123`

## 👤 Example Accounts

- **filip_bojovic** : Sifra123  
- **filip_andric** : Sifra123  

Emails are the same as in Microsoft Teams.

---

## ⚙️ App Functionality

### **Mandatory Features**

#### 👤 User

- Login / Registration / Password recovery via email
- Changing name, surname, profile description, username, password, and profile picture  
  *(known bug: the old profile picture remains until the app is restarted or automatically changes if the username is changed at the same time; also, image recognition will use the new profile picture. The image is correctly uploaded to the server and retrieved, but the app gets confused — worked fine before adding post display on profile)*
- Viewing the user's own posts
- Viewing pinned locations visited by the user
- Viewing the follower list
- Displaying the number of posts, followers, and total likes on all posts
- Deleting the profile

#### 📝 Post

- Location, description, date, and time
- Liking posts
- Commenting on posts
- Viewing the location on a map when clicked
- Visiting the profile of the user who created the post
- Visiting profiles of users who liked or commented on a post

#### 🔍 Location Search

- Search by city or by country only *(may take time to load images from the server)*
- Displaying posts on the map
- Filtering by popularity (likes and comments) or by recency
- Viewing a specific post by clicking on its map image
- Visiting the profile of the user who created the post

#### 🏠 Home

- Feed of posts from followed users and the logged-in user, sorted by recency
- Pagination with 3 posts per page and endless scrolling
- Commenting
- Liking
- Visiting the profile of the post’s author
- Viewing the location on a map when clicked

#### 🔒 Security and Data Integrity

- Input validation
- Only one user can have a given username and email
- A user’s profile can only be accessed via post search, follower list, likes list, or post comments

---

### **Recommended Features**

#### 💬 Chat

- **Inbox** – Real-time updates of new senders and storage of previous messages  
- **Direct Messages** – Real-time updates of new and sent messages, with history saved

#### 📱 App Icon

- :)

---

### **Optional Features**

#### 🖼️ Image Recognition

- Profile picture must be in an ID document-like format and contain only one person
- A post image must not contain the posting user

#### 📊 User Statistics

- Number of posts, followers, and total likes across all posts


#  BONJOUR
<div align="center" style="bold">
![alt text](https://github.com/seif1125/BONJOUR/blob/master/app/src/main/res/drawable/logo.png?raw=true)
Realtime Android chat App
</div>




# Special Thanks
- i would like to thank Amit Academy and Instructor Omar Ahmed for their endless
efforts thank you sincerly


# About This Project
-Bonjour is a chat messenger App wher you can connect to your friends 
Anywhere,AnyTime


# Features Of App:-
  -Realtime messaging

-profile Managment(update name ,status,profile)                                                   

-presence system (showing your friends online or not)

-last Seen feature

-Account local storage

# Android/Java  Topics Covered in this Project

-Room (for saving accounts which is unsaved after asking user)

-SharedPreferences (for displaying the introscreen(MainActivity.java)

-fragmentation (IntroActivity and DashboardActivity contains fragments )

-Navigation (As user navigates to app using fragment navigation)

-Activity and fragments lifeCycle (for detecting online and offline and for stopping and starting listining service )

-threading (for starting service in another thread to less the loadon main thread)

-Adapter (for listing friends,chats,users,and each chat messages)

-Services (background service to listen for new received messages and notifying user even when not opening the app)

-FireBase (for backend)




# what is done in each activity?

   -Main Activity

    .in this Activity we check that user is firsttime to open this App 
    if yes then we will show the  MainActivity else we will move to the IntroActivity
    to login user
   
   -IntroCarousel Activity

    .in this Activity first we check if the user is already logged in using firebaseAuth to check if there is a logged user 
    and if logged we move to our dashboard
    .if user not logged in we check weather the user have saved an account or not if saved then fragment saveduser will be displayed
    were this fragment have a dropdown of saved account once user choose the account and press login without need to retype credential again
    (As Facebook login)
    else if user didnt save an account orfirst time to use the app the login fragment will be shown as start
    .all the three fragment (savedlogin,login,signup) can be navigated from any fragment
    once user logged in through email and password or signed up after entering(email,username,password and choosing image as profile)
    we move to the Dashboard
    
    -Dashboard Activity
    
     .this Activity consist of (chats,friends,request,explore,profile) where user can navigate through a bottom navigation
     .we started a service in dashboard to listen for notifications and received messages
     .on activity paused or stopped we change the user status to offline and on resume to online
     .we created a toolbar which contain a logout option to logout to introActivity
     .in profile settings we retreive user name status and profie where the user can change and update these info
     .in the explore we list all the users in database except the user where when user click on view it will move to this user profile
     .in the request we show all received and pending request were user can accept request then this request is removed and user added to friend list or user can
     ignorethe request or can view the profile
     .in friends we list all friends of user wheree on click user have a option dialogue to choose weather to chat or view this friend profile
    .in chats we list all chats with the last chat and unread messages where if we click on the chat item the app moves to chat Activity
    
    
    -UserProfile Activity
    
     .in this activity we show the profile where the user choosen 
     depending on the relation between these two users  options are shown to the user where if they are not friends
     the user have the option to send request,if user sends already request he can cancel this request,if the other user sends a friend request the user
     can ignore or accept request and if they are friends user can unfriend htis user 
     
     
     
    -Chats Activity
     
     .in this activity we set a custom toolbar which holder the other user profile name,image and the last seen 
     and an option menu where user can view profile of other user or logout
     in this Activity we show the chats in a recycler view where user can send and receive messages
     if the users message is read an eye icon is appeared within the time.
     
     
     
     
     
    

package com.doublea.talktify.backgroundTools;

import java.util.LinkedList;

public class UserData {
    private String firstName;
    private String lastName;
    private String displayName;
    private LinkedList<String> contacts;

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    private String UID;
    public UserData(String firstName, String lastName, String displayName, String UID){
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayName = displayName;
        this.UID = UID;
        contacts = new LinkedList<>();
    }
    public UserData(){
        firstName = "";
        lastName = "";
        displayName = "";
    }
    @Override
    public String toString(){
        return "firstName: " + this.displayName + "\n" +"lastName:"+ lastName + "\n" +"displayName:"+ displayName + "\n" + "uid:" + UID + "\n" + "contacts:" + contacts ;
    }
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public LinkedList<String> getContacts() {
        return contacts;
    }

    public void setContacts(LinkedList<String> contacts) {
        this.contacts = contacts;
    }

    public boolean matches(String userEntry) {
        for (int i = 0; i < firstName.length();i++){
            if (firstName.substring(0,i+1).equalsIgnoreCase(userEntry))
                return true;
        }
        for (int i = 0; i < lastName.length();i++){
            if (lastName.substring(0,i+1).equalsIgnoreCase(userEntry))
                return true;
        }
        for (int i = 0; i < displayName.length();i++){
            if (displayName.substring(0,i+1).equalsIgnoreCase(userEntry))
                return true;
        }
        return false;
    }
}

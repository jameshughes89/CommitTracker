package com.jameshughes89.committracker;

import android.graphics.Picture;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by JamesHughes89 on 6/11/2017.
 *
 * This class will keep track of a single repository's details.
 * These details are saved and will be presented in the list,
 * and/or the project details view (fragment?).
 */

public class Tracker {

    // REGEX: The tags for the different details from the HTML text
    // THIS IS WHAT WILL NEED TO BE UPDATED IF THE HTML TAGS EVER CHANGE ON GITHUB
    private final String TIME_TAG_REGEX = "<relative-time datetime=\"(.+?)\">";

    // These should stay the same throughout the lifetime of the Tracker
    private String projectUser;
    private String projectName;
    private URL projectURL;

    // These will update over the life of the Tracker
    private String commitTime;
    private String commitTimeDelta;
    private String commitDescription;
    private String commitUser;
    private Picture commitUserPicture;

    /**
     * Default constructor. Basically useless. Should never get called.
     *
     * @throws MalformedURLException Will be thrown if the URL creation doesn't work out.
     */
    public Tracker() throws MalformedURLException{
        this("jameshughes89", "committracker");
    }

    /**
     * Constructor that should basically always be used. Will create the Tracker and assign the
     * project user, name, and URL
     *
     * @param projectUser The user/orginisation that created the repository.
     * @param projectName The name of the repository.
     * @throws MalformedURLException Will be thrown if the URL creation doesn't work out.
     */
    public Tracker(String projectUser, String projectName) throws MalformedURLException{
        this.projectUser = projectUser;
        this.projectName = projectName;
        projectURL = new URL("https://github.com/" + projectUser + "/" + projectName);
    }

    /**
     * Updates the details for the tracker. This includes:
     * - commitTime
     * - commitDescription
     * - commitUser
     * - commitUserPicture
     *
     * Will read the URL details and put it into one long string.
     */
    public void update(){
        try{
            String HTMLtext = getHTMLtext();
            updateTimeAndTimeDelta(HTMLtext);
        }
        catch (IOException | ParseException | NoPatternFoundException e){
            // Currently do nothing? This may make sense... Just leave things alone. Maybe a popup?
        }

    }

    /**
     * Will return a String version of the HTML text from the project's URL. Uses a StringBuilder
     * because that's the right thing to do here.
     *
     * @return The String version of the HTML text from the project URL.
     * @throws IOException Will be thrown if the InputStream creation doesn't work out.
     */
    private String getHTMLtext() throws IOException{
        BufferedReader in = new BufferedReader(new InputStreamReader(projectURL.openStream()));
        StringBuilder sb = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            sb.append(inputLine);
        in.close();
        return sb.toString();
    }


    /**
     * Updates both the commitTime and TimeDelta (time since commit).
     *
     * @param HTMLtext HTML text from the project's website.
     * @throws ParseException Will be thrown if the DateFormat's parse() doesn't work out.
     */
    private void updateTimeAndTimeDelta(String HTMLtext) throws ParseException, NoPatternFoundException{

        // Finds the string for the date
        final Pattern pattern = Pattern.compile(TIME_TAG_REGEX);
        final Matcher matcher = pattern.matcher(HTMLtext);

        if (matcher.find()){
            System.out.println(matcher.group(1)); // Prints String I want to extract
            String commitDateTimeString = matcher.group(1);

            // Get a nice, more *normal* version of the commit time. This will also be local time zone too.
            DateFormat m_ISO8601Local = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            commitTime = m_ISO8601Local.parse(commitDateTimeString).toString();

            // Gets the delta time.
            DateTime currentDateTime = DateTime.now();
            DateTime commitDateTime = DateTime.parse(commitDateTimeString,
                    DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
            commitTimeDelta = getDeltaTime(currentDateTime, commitDateTime);

        }
        else{
            throw new NoPatternFoundException();
        }
    }

    /**
     * Returns the String of the difference between the commit date time and the current date time.
     *
     * Currently this is set up to only return the most significant unit (days > hours > min > sec).
     *
     * @param currentDateTime The current DateTime of day
     * @param commitDateTime The DateTime of the commit
     * @return A string of the the time difference
     */
    private String getDeltaTime(DateTime currentDateTime, DateTime commitDateTime){

        // Days
        Duration deltaTime = new Duration(commitDateTime, currentDateTime);
        int days = (int)deltaTime.getStandardDays();
        if (days == 1){
            return days + " day ago";
        }
        else if (days >1){
            return days + " days ago";
        }

        // Hours
        int hours = (int)deltaTime.getStandardHours();
        if (hours == 1){
            return hours + " hour ago";
        }
        else if (hours >1){
            return hours + " hours ago";
        }

        // Minutes
        int minutes = (int)deltaTime.getStandardMinutes();
        if (minutes == 1){
            return minutes + " minute ago";
        }
        else if (minutes >1){
            return minutes + " minutes ago";
        }

        // Seconds
        int seconds = (int)deltaTime.getStandardSeconds();
        if (seconds == 1){
            return seconds + "second ago";
        }
        else {
            return seconds + " seconds ago";
        }

        // Ignore this. This is if I want a long string saying everything between the two days.
        /*
        StringBuilder sb = new StringBuilder();

        // Days
        Duration deltaTime = new Duration(commitDateTime, currentDateTime);
        int days = (int)deltaTime.getStandardDays();
        if (days == 1){
            sb.append(days + "day ");
        }
        else if (days >1){
            sb.append(days + "days ");
        }

        // Hours
        commitDateTime = commitDateTime.plusDays(days);
        deltaTime = new Duration(commitDateTime, currentDateTime);
        int hours = (int)deltaTime.getStandardHours();
        if (hours == 1){
            sb.append(hours + "hour ");
        }
        else if (hours >1){
            sb.append(hours + "hours ");
        }

        // Minutes
        commitDateTime = commitDateTime.plusDays(hours);
        deltaTime = new Duration(commitDateTime, currentDateTime);
        int minutes = (int)deltaTime.getStandardMinutes();
        if (minutes == 1){
            sb.append(minutes + "minute ");
        }
        else if (minutes >1){
            sb.append(minutes + "minutes ");
        }

        // Seconds
        commitDateTime = commitDateTime.plusDays(hours);
        deltaTime = new Duration(commitDateTime, currentDateTime);
        int seconds = (int)deltaTime.getStandardSeconds();
        if (seconds == 1){
            sb.append(seconds + "second ago");
        }
        */
    }


    private void updateDescription(String HTMLtext){

    }

    private void updateCommitUser(String HTMLtext){

    }

    private void updateCommitUserPicture(String HTMLtext){

    }


    // A bunch of getters follow. Some may not be needed

    public String getProjectUser() {
        return projectUser;
    }

    public String getProjectName() {
        return projectName;
    }

    public URL getProjectURL() {
        return projectURL;
    }

    public String getCommitTime() {
        return commitTime;
    }

    public String getCommitDescription() {
        return commitDescription;
    }

    public String getCommitUser() {
        return commitUser;
    }

    public Picture getCommitUserPicture() {
        return commitUserPicture;
    }

}

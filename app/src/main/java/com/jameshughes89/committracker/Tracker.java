package com.jameshughes89.committracker;

import android.graphics.Picture;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        catch (IOException | ParseException e){
            // Currently do nothing? This may make sense... Just leave things alone. Maybe a popup?
        }

    }

    /**
     * Will return a String version of the HTML text from the project's URL. Uses a StringBuilder
     * because that's the right thing to do here.
     *
     * @return The String version of the HTML text from the project URL.
     * @throws IOException
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
     * @throws ParseException
     */
    private void updateTimeAndTimeDelta(String HTMLtext) throws ParseException{

        // Finds the string for the date
        final Pattern pattern = Pattern.compile(TIME_TAG_REGEX);
        final Matcher matcher = pattern.matcher(HTMLtext);

        // TODO
        // Consider throwing an exception here? This statement returns a boolena. If false, it wasn't found, therefore maybe do nothing (throw exception)?
        matcher.find();

        System.out.println(matcher.group(1)); // Prints String I want to extract
        String theDateTime = matcher.group(1);

        // Get a nice, more *normal* version of the commit time. This will also be local time zone too.
        DateFormat m_ISO8601Local = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        commitTime = m_ISO8601Local.parse(theDateTime).toString();

        // TODO
        // Gets the delta time.

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

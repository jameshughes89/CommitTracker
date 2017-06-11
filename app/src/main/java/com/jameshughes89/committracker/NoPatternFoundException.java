package com.jameshughes89.committracker;

/**
 * Created by James on 6/11/2017.
 *
 * The exception that will be thrown if the Matcher's .find() returns false.
 */

class NoPatternFoundException extends Exception {


    NoPatternFoundException() {}


    NoPatternFoundException(String message)
    {
        super(message);
    }

}

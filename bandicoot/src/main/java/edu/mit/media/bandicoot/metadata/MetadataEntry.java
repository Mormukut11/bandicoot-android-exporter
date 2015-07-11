package edu.mit.media.bandicoot.metadata;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * Base class for interaction metadata. Only intended to be used when writing out to a CSV file.
 *
 * @author Brian Sweatt
 */
public class MetadataEntry implements Comparable<MetadataEntry> {
    protected long dateTime;
    protected String interaction;
    protected String direction;
    protected String correspondentId;
    protected long callDuration;
    protected String antennaId;

    @Override
    public String toString() {
        return String.format(
            "%s,%s,%s,%s,%s,",
            interaction,
            direction,
            correspondentId,
            getDateString(),
            (callDuration > 0)? callDuration : "");
    }

    protected void setCorrespondentId(String phoneNumber) {
        PhoneNumberUtil util = PhoneNumberUtil.getInstance();

        Phonenumber.PhoneNumber number = null;
        try {
            number = util.parse(phoneNumber, "US");
        } catch (NumberParseException e) {
            e.printStackTrace();
        }


        if (number != null) {
            correspondentId = util.format(number, PhoneNumberUtil.PhoneNumberFormat.E164);
        } else {
            correspondentId = phoneNumber;
        }

        try {
            // Hex encoded SHA-1 of the phone number, rather than the actual number
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            sha1.update(correspondentId.getBytes());
            BigInteger digestInt = new BigInteger(1,sha1.digest());
            correspondentId = digestInt.toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    protected String getDateString() {
        Date date = new Date(dateTime);
        return date.toString();
    }

    @Override
    public int compareTo(MetadataEntry another) {
        return Long.signum(dateTime - another.dateTime);
    }
}

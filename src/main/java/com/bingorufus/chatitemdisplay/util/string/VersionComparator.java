package com.bingorufus.chatitemdisplay.util.string;

public class VersionComparator {

    private VersionComparator() {
    }

    private static int[] toIntArray(String version) {
        int[] out = new int[20];
        String[] splitVer = version.split("[.]");
        for (int i = 0; i < splitVer.length; i++) {
            out[i] = Integer.parseInt(splitVer[i]);
        }

        return out;
    }

    public static Status isRecent(String current, String updated) {
        return compare(toIntArray(current), toIntArray(updated), 0);
    }

    private static Status compare(int[] array1, int[] array2, int section) {

        if (array1.length == section || array2.length == section) {
            if (array1.length == array2.length)
                return Status.SAME;
            if (array1.length < array2.length)
                return Status.BEHIND;
            return Status.AHEAD;
        }
        if (array1[section] != array2[section]) {
            if (array1[section] > array2[section])
                return Status.AHEAD;
            return Status.BEHIND;
        }
        return compare(array1, array2, section + 1);
    }


    public enum Status {
        SAME,
        AHEAD,
        BEHIND
    }
}

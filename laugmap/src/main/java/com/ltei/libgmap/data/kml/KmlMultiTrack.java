package com.ltei.laugmap.data.kml;

import com.ltei.laugmap.data.Geometry;

import java.util.ArrayList;

/**
 * Created by thorin on 22/02/2017.
 */

public class KmlMultiTrack extends KmlMultiGeometry {
    /**
     * Creates a new MultiGeometry object
     *
     * @param tracks array of KmlTrack objects contained in the MultiGeometry
     */
    public KmlMultiTrack(ArrayList<KmlTrack> tracks) {
        super(createGeometries(tracks));
    }

    private static ArrayList<Geometry> createGeometries(ArrayList<KmlTrack> tracks) {
        if (tracks == null) {
            throw new IllegalArgumentException("Tracks cannot be null");
        }

        ArrayList<Geometry> geometries = new ArrayList<Geometry>(tracks);

        return geometries;
    }
}

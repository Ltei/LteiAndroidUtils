package com.ltei.laugmap.data.geojson;

import com.ltei.laugmap.data.Geometry;
import com.ltei.laugmap.data.MultiGeometry;

import java.util.ArrayList;
import java.util.List;

/**
 * A GeoJsonMultiPoint geometry contains a number of {@link GeoJsonPoint}s.
 */
public class GeoJsonMultiPoint extends MultiGeometry {
    /**
     * Creates a GeoJsonMultiPoint object
     *
     * @param geoJsonPoints list of GeoJsonPoints to store
     */
    public GeoJsonMultiPoint(List<GeoJsonPoint> geoJsonPoints) {
        super(geoJsonPoints);
        setGeometryType("MultiPoint");
    }

    /**
     * Gets the type of geometry. The type of geometry conforms to the GeoJSON 'type'
     * specification.
     *
     * @return type of geometry
     */
    public String getType() {
        return getGeometryType();
    }

    /**
     * Gets a list of GeoJsonPoints
     *
     * @return list of GeoJsonPoints
     */
    public List<GeoJsonPoint> getPoints() {
        //convert list of Geometry types to list of GeoJsonPoint types
        List<Geometry> geometryList = getGeometryObject();
        ArrayList<GeoJsonPoint> geoJsonPoints = new ArrayList<>();
        for (Geometry geometry : geometryList) {
            GeoJsonPoint point = (GeoJsonPoint) geometry;
            geoJsonPoints.add(point);
        }
        return geoJsonPoints;
    }
}

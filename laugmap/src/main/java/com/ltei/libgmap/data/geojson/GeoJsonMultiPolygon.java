package com.ltei.laugmap.data.geojson;

import com.ltei.laugmap.data.Geometry;
import com.ltei.laugmap.data.MultiGeometry;

import java.util.ArrayList;
import java.util.List;

/**
 * A GeoJsonMultiPolygon geometry contains a number of {@link GeoJsonPolygon}s.
 */
public class GeoJsonMultiPolygon extends MultiGeometry {

    /**
     * Creates a new GeoJsonMultiPolygon
     *
     * @param geoJsonPolygons list of GeoJsonPolygons to store
     */
    public GeoJsonMultiPolygon(List<GeoJsonPolygon> geoJsonPolygons) {
        super(geoJsonPolygons);
        setGeometryType("MultiPolygon");
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
     * Gets a list of GeoJsonPolygons
     *
     * @return list of GeoJsonPolygons
     */
    public List<GeoJsonPolygon> getPolygons() {
        //convert list of Geometry types to list of GeoJsonPolygon types
        List<Geometry> geometryList = getGeometryObject();
        ArrayList<GeoJsonPolygon> geoJsonPolygon = new ArrayList<>();
        for (Geometry geometry : geometryList) {
            GeoJsonPolygon polygon = (GeoJsonPolygon) geometry;
            geoJsonPolygon.add(polygon);
        }
        return geoJsonPolygon;
    }
}
